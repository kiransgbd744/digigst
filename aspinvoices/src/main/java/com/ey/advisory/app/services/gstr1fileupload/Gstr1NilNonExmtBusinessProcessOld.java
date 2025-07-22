package com.ey.advisory.app.services.gstr1fileupload;

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
import com.ey.advisory.app.data.entities.client.Ann1VerticalWebError;
import com.ey.advisory.app.data.entities.client.Gstr1NilDetailsEntity;
import com.ey.advisory.app.data.entities.client.Gstr1NilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.Gstr1NilNonExmptSummaryEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtAsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1TxpdGstnRepository;
import com.ey.advisory.app.data.repositories.client.NilAndHsnProcedureCallRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService;
import com.ey.advisory.app.services.docs.SRFileToNilDetailsConvertion;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.validation.NilNonExpt.NilNonExptValidationChain;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Gstr1NilNonExmtBusinessProcessOld")
@Slf4j
public class Gstr1NilNonExmtBusinessProcessOld {

	@Autowired
	@Qualifier("Gstr1NilRepository")
	private Gstr1NilRepository gstr1NilRepository;

	@Autowired
	@Qualifier("Gstr1NilNonExtSummaryRepository")
	private Gstr1NilNonExtSummaryRepository gstr1NilNonExtSummaryRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1NilNonExtAsRepository")
	private Gstr1NilNonExtAsRepository gstr1NilNonExtAsRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("NilNonExptValidationChain")
	private NilNonExptValidationChain nilNonExptValidationChain;

	@Autowired
	@Qualifier("SRFileToNilDetailsConvertion")
	private SRFileToNilDetailsConvertion sRFileToNilDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1TxpdGstnRepository")
	private Gstr1TxpdGstnRepository gstr1TxpdGstnRepository;

	@Autowired
	@Qualifier("NilAndHsnProcedureCallRepositoryImpl")
	private NilAndHsnProcedureCallRepository nilAndHsnProcedureCallRepository;
	
	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	public void processTxpdBusinessData(List<Object[]> listOfAtas,
			List<Gstr1NilNonExemptedAsEnteredEntity> strProcessRecords,
			List<Gstr1NilNonExemptedAsEnteredEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {
		List<Gstr1NilNonExemptedAsEnteredEntity> busErrorRecords = new ArrayList<>();
		List<Gstr1NilNonExemptedAsEnteredEntity> infoProcessed = new ArrayList<>();
		List<Gstr1NilNonExemptedAsEnteredEntity> busProcessRecords = new ArrayList<>();

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
		List<Gstr1NilNonExemptedAsEnteredEntity> strProcess = sRFileToNilDetailsConvertion
				.convertSRFileToNilNonExmpt(strProcessRecords,
						updateFileStatus);
		 deriveEntityId(strProcess);
		for (Gstr1NilNonExemptedAsEnteredEntity advanceAdjust : strProcess) {
			List<ProcessingResult> results = nilNonExptValidationChain
					.validateOld(advanceAdjust, null);

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

		for (Gstr1NilNonExemptedAsEnteredEntity process : strProcessRecords) {
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
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1NilRecords(busErrorRecords, errorMap);
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed,
							GSTConstants.BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1NilRecords(infoProcessed, errorMap);
		}

		if (busProcessRecords.size() > 0 && !busProcessRecords.isEmpty()) {

			List<Gstr1NilDetailsEntity> b2csDoc = sRFileToNilDetailsConvertion
					.convertSRFileToNNE(busProcessRecords, updateFileStatus);

			List<String> existProcessData = new ArrayList<>();
			for (Gstr1NilDetailsEntity b2cProcessed : b2csDoc) {
				String txpdInvKey = b2cProcessed.getNKey();
				existProcessData.add(txpdInvKey);
			}
			if (existProcessData.size() > 0 && !existProcessData.isEmpty()) {
				gstr1NilRepository.UpdateSameInvKey(existProcessData);
			}

			List<Gstr1NilDetailsEntity> saveAll = gstr1NilRepository
					.saveAll(b2csDoc);

			List<Gstr1NilDetailsEntity> list = convertSRFileToNNEToSummary(
					saveAll, updateFileStatus);

			List<Gstr1NilNonExmptSummaryEntity> summaryDate = sRFileToNilDetailsConvertion
					.convertSRFileToNNEToSummary(list, updateFileStatus);
			List<String> listOfKeys = new ArrayList<String>();
			for (Gstr1NilNonExmptSummaryEntity summary : summaryDate) {
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

	private List<Gstr1NilDetailsEntity> convertSRFileToNNEToSummary(
			List<Gstr1NilDetailsEntity> saveAll,
			Gstr1FileStatusEntity updateFileStatus) {
		Map<String, Gstr1NilDetailsEntity> map = new HashMap<>();
		for (Gstr1NilDetailsEntity summEntity : saveAll) {
			String key = summEntity.getNKey();
			if (!map.containsKey(key)) {
				map.put(key, summEntity);
				continue;
			}
			Gstr1NilDetailsEntity b2cGstnKey = map.get(key);
			b2cGstnKey.add(summEntity);
		}

		return new ArrayList<>(map.values());
	}
private void deriveEntityId(List<Gstr1NilNonExemptedAsEnteredEntity> strProcessstrProcess){
		
		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();
		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		strProcessstrProcess.stream().forEach(data->{
			Long entityId = gstinAndEntityMap.get(data.getSgstin());
			data.setEntityId(entityId);
			data.setEntityConfigParamMap(map);
		});
	}
}