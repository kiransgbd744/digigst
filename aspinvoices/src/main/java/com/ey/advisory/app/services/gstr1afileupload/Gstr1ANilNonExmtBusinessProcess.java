package com.ey.advisory.app.services.gstr1afileupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilDetailsEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExmptSummaryEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilNonExtAsRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilNonExtSummaryRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilRepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.doc.gstr1a.SRFileToGstr1ANilDetailsConvertion;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.validation.gstr1a.NilNonExpt.Gstr1ANilNonExptValidationChain;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Component("Gstr1ANilNonExmtBusinessProcess")
@Slf4j
public class Gstr1ANilNonExmtBusinessProcess {

	@Autowired
	@Qualifier("Gstr1ANilRepository")
	private Gstr1ANilRepository gstr1NilRepository;

	@Autowired
	@Qualifier("Gstr1ANilNonExtSummaryRepository")
	private Gstr1ANilNonExtSummaryRepository gstr1NilNonExtSummaryRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1ANilNonExtAsRepository")
	private Gstr1ANilNonExtAsRepository gstr1NilNonExtAsRepository;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1ANilNonExptValidationChain")
	private Gstr1ANilNonExptValidationChain nilNonExptValidationChain;

	@Autowired
	@Qualifier("SRFileToGstr1ANilDetailsConvertion")
	private SRFileToGstr1ANilDetailsConvertion sRFileToNilDetailsConvertion;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	public void processTxpdBusinessData(List<Object[]> listOfAtas,
			List<Gstr1ANilNonExemptedAsEnteredEntity> strProcessRecords,
			List<Gstr1ANilNonExemptedAsEnteredEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {
		List<Gstr1ANilNonExemptedAsEnteredEntity> busErrorRecords = new ArrayList<>();
		List<Gstr1ANilNonExemptedAsEnteredEntity> infoProcessed = new ArrayList<>();
		List<Gstr1ANilNonExemptedAsEnteredEntity> busProcessRecords = new ArrayList<>();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		Map<String, List<ProcessingResult>> businessValErrors = new HashMap<>();
		Map<String, List<ProcessingResult>> infoWithProcessed = new HashMap<>();

		List<String> errorKeys = new ArrayList<>();
		List<String> infoKeys = new ArrayList<>();
		List<String> errorInfo = new ArrayList<>();

		List<ProcessingResult> current = null;
		List<Gstr1ANilNonExemptedAsEnteredEntity> strProcess = sRFileToNilDetailsConvertion
				.convertSRFileToNilNonExmpt(strProcessRecords,
						updateFileStatus);
		deriveEntityId(strProcess);
		for (Gstr1ANilNonExemptedAsEnteredEntity advanceAdjust : strProcess) {
			List<ProcessingResult> results = nilNonExptValidationChain
					.validate(advanceAdjust, null);

			if (results != null && results.size() > 0) {
				String key = advanceAdjust.getNKey();
				Long id = advanceAdjust.getId();
				String keys = key.concat(GSTConstants.SLASH)
						.concat(id.toString());

				List<ProcessingResultType> listTypes = new ArrayList<>();
				for (ProcessingResult types : results) {
					ProcessingResultType type = types.getType();
					listTypes.add(type);
				}
				List<String> errorType = listTypes.stream()
						.map(object -> Objects.toString(object, null))
						.collect(Collectors.toList());

				current = businessValErrors.get(keys);
				if (current == null) {
					current = new ArrayList<>();
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(key);
							errorKeys.add(key);
							businessValErrors.put(keys, results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors.put(keys, results);
						} else {
							infoWithProcessed.put(keys, results);
							infoKeys.add(key);
						}
					}
				} else {
					if (errorType.size() > 0) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorKeys.add(key);
							errorInfo.add(key);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else if (errorType.contains(GSTConstants.ERROR)) {
							errorKeys.add(key);
							businessValErrors
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
						} else {
							infoWithProcessed
									.computeIfAbsent(keys,
											k -> new ArrayList<ProcessingResult>())
									.addAll(results);
							infoKeys.add(key);
						}
					}
				}
			}
		}

		List<String> busiProcessedKeys = new ArrayList<>();

		for (String infoKey : infoWithProcessed.keySet()) {
			infoKeys.add(infoKey);
		}

		for (Gstr1ANilNonExemptedAsEnteredEntity process : strProcessRecords) {
			String key = process.getNKey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			gstr1NilNonExtAsRepository.invUpdateStructuralError(errorKeys, ids);
		}
		if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			gstr1NilNonExtAsRepository.invUpdateErrorInfo(errorInfo, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			gstr1NilNonExtAsRepository.invUpdateInfo(infoKeys, ids);
		}

		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			busErrorRecords = gstr1NilNonExtAsRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = gstr1NilNonExtAsRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			busProcessRecords = gstr1NilNonExtAsRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords ", busErrorRecords.size());
		LOGGER.error("businessProcessedRecords ", busProcessRecords.size());
		if (busErrorRecords.size() > 0 && !busErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1NilRecords(busErrorRecords, errorMap);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1NilRecords(infoProcessed, errorMap);
		}

		if (busProcessRecords.size() > 0 && !busProcessRecords.isEmpty()) {

			List<Gstr1ANilDetailsEntity> b2csDoc = sRFileToNilDetailsConvertion
					.convertSRFileToNNE(busProcessRecords, updateFileStatus);

			List<String> existProcessData = new ArrayList<>();
			for (Gstr1ANilDetailsEntity b2cProcessed : b2csDoc) {
				String txpdInvKey = b2cProcessed.getNKey();
				existProcessData.add(txpdInvKey);
			}
			if (existProcessData.size() > 0 && !existProcessData.isEmpty()) {
				gstr1NilRepository.UpdateSameInvKey(existProcessData);
			}

			List<Gstr1ANilDetailsEntity> saveAll = gstr1NilRepository
					.saveAll(b2csDoc);

			List<Gstr1ANilDetailsEntity> list = convertSRFileToNNEToSummary(
					saveAll, updateFileStatus);

			List<Gstr1ANilNonExmptSummaryEntity> summaryDate = sRFileToNilDetailsConvertion
					.convertSRFileToNNEToSummary(list, updateFileStatus);
			List<String> listOfKeys = new ArrayList<String>();
			for (Gstr1ANilNonExmptSummaryEntity summary : summaryDate) {
				String key = summary.getNKey();
				listOfKeys.add(key);
			}
			if (listOfKeys.size() > 0 && !listOfKeys.isEmpty()) {
				gstr1NilNonExtSummaryRepository.UpdateSameInvKey(listOfKeys);
			}
			gstr1NilNonExtSummaryRepository.saveAll(summaryDate);
		}

		totalRecords = (listOfAtas.size() != 0) ? listOfAtas.size() : 0;
		errorRecords = (busErrorRecords.size() != 0
				|| strErrRecords.size() != 0)
						? busErrorRecords.size() + strErrRecords.size() : 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	private List<Gstr1ANilDetailsEntity> convertSRFileToNNEToSummary(
			List<Gstr1ANilDetailsEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		Map<String, Gstr1ANilDetailsEntity> map = new HashMap<>();
		for (Gstr1ANilDetailsEntity summEntity : saveAll) {
			String key = summEntity.getNKey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}
			Gstr1ANilDetailsEntity b2cGstnKey = map.get(key);
			b2cGstnKey.add(summEntity);
		}

		return new ArrayList<>(map.values());
	}

	private void deriveEntityId(
			List<Gstr1ANilNonExemptedAsEnteredEntity> strProcessstrProcess) {

		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();
		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		strProcessstrProcess.stream().forEach(data -> {
			Long entityId = gstinAndEntityMap.get(data.getSgstin());
			data.setEntityId(entityId);
			data.setEntityConfigParamMap(map);
		});
	}
}