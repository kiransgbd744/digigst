package com.ey.advisory.app.services.gstr1fileupload;

import static com.ey.advisory.common.GSTConstants.BUSINESS_VALIDATIONS;

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
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredHsnEntity;
import com.ey.advisory.app.data.entities.client.Gstr1HsnFileUploadEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1HsnAsEnteredRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1HsnRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService;
import com.ey.advisory.app.services.docs.SRFileToHsnDetailsConvertion;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.services.validation.HsnSacSummery.HsnValidationChain;
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

@Component("Gstr1HsnBusinessProcess")
@Slf4j
public class Gstr1HsnBusinessProcess {

	@Autowired
	@Qualifier("HsnValidationChain")
	private HsnValidationChain hsnValidationChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1HsnAsEnteredRepository")
	private Gstr1HsnAsEnteredRepository gstr1HsnAsEnteredRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToHsnDetailsConvertion")
	private SRFileToHsnDetailsConvertion sRFileToHsnDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1HsnRepository")
	private Gstr1HsnRepository gstr1HsnRepository;
	
	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	public void processBusinessData(List<Object[]> listOfB2cs,
			List<Gstr1AsEnteredHsnEntity> strucProcessedRecords,
			List<Gstr1AsEnteredHsnEntity> structuralErrorsRecords,
			Gstr1FileStatusEntity updateFileStatus, List<Long> ids) {

		List<Gstr1AsEnteredHsnEntity> busErrorRecords = new ArrayList<>();
		List<Gstr1AsEnteredHsnEntity> infoProcessed = new ArrayList<>();
		List<Gstr1AsEnteredHsnEntity> busProcessRecords = new ArrayList<>();
	//	List<Gstr1B2csGstnDetailsEntity> addFile = new ArrayList<>();

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
		/*List<Gstr1AsEnteredHsnEntity> strucProcessed = sRFileToB2CSDetailsConvertion
				.convertSRFileToOutward(strucProcessedRecords,
						updateFileStatus);*/
		 deriveEntityId(strucProcessedRecords);
		for (Gstr1AsEnteredHsnEntity b2cs : strucProcessedRecords) {
			List<ProcessingResult> results = hsnValidationChain.validate(b2cs,
					null);
			if (results != null && results.size() > 0) {
				String key = b2cs.getInvHsnKey(); 
				Long id = b2cs.getId();
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
							/*
							 * gstr1B2CSAsEnteredRepository
							 * .b2csUpdateErrorInfo(key, id);
							 */
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
							errorInfo.add(key);
							errorKeys.add(key);
							/*
							 * gstr1B2CSAsEnteredRepository
							 * .b2csUpdateErrorInfo(key, id);
							 */
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

		for (Gstr1AsEnteredHsnEntity process : strucProcessedRecords) {
			String key = process.getInvHsnKey();
			if (!errorKeys.contains(key)) {
				busiProcessedKeys.add(key);
			}
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			gstr1HsnAsEnteredRepository.b2cUpdateStructuralError(errorKeys,
					ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			gstr1HsnAsEnteredRepository.b2csUpdateInfo(infoKeys, ids);
		}
		if (errorInfo.size() > 0 && !errorInfo.isEmpty()) {
			gstr1HsnAsEnteredRepository.b2csUpdateErrorInfo(errorInfo, ids);
		}
		if (errorKeys.size() > 0 && !errorKeys.isEmpty()) {
			busErrorRecords = gstr1HsnAsEnteredRepository
					.getAllExcelData(errorKeys, ids);
		}
		if (infoKeys.size() > 0 && !infoKeys.isEmpty()) {
			infoProcessed = gstr1HsnAsEnteredRepository
					.getAllProcessedWithInfoData(infoKeys, ids);
		}
		if (busiProcessedKeys.size() > 0 && !busiProcessedKeys.isEmpty()) {
			busProcessRecords = gstr1HsnAsEnteredRepository
					.getAllExcelData(busiProcessedKeys, ids);
		}

		LOGGER.error("businessErrorRecords ", busErrorRecords.size());
		LOGGER.error("businessProcessedRecords ", busProcessRecords.size());
		if (busErrorRecords.size() > 0 && !busErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1HsnRecords(busErrorRecords, errorMap); 
		}
		if (infoWithProcessed.size() > 0 && !infoWithProcessed.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorGstr1HsnRecords(infoProcessed, errorMap);
		}

		if (busProcessRecords.size() > 0 && !busProcessRecords.isEmpty()) {

			List<Gstr1HsnFileUploadEntity> b2csDoc = sRFileToHsnDetailsConvertion
					.convertSRFileToHsnTransDoc(busProcessRecords,
							updateFileStatus);

			List<String> existProcessData = new ArrayList<>();
			for (Gstr1HsnFileUploadEntity b2cProcessed : b2csDoc) {
				String b2csInvKey = b2cProcessed.getInvHsnKey();
				existProcessData.add(b2csInvKey);
			}
			if (existProcessData.size() > 0 && !existProcessData.isEmpty()) {
				/*duplicateVerticalDocCheckService.
				softDeleteDuplicateDocuments(b2csDoc);*/
				gstr1HsnRepository.UpdateSameInvKey(existProcessData);
		}

			/* List<Gstr1B2csDetailsEntity> saveAll = */gstr1HsnRepository
					.saveAll(b2csDoc);

			/*
			 * List<Gstr1B2csGstnDetailsEntity> sameRecords =
			 * sameRecords(saveAll, updateFileStatus);
			 * 
			 * addFile = convertAddWithinSameFile(sameRecords);
			 * 
			 * List<String> existGstnProcessData = new ArrayList<>(); for
			 * (Gstr1B2csGstnDetailsEntity b2cUpdate : addFile) { String b2cKey
			 * = b2cUpdate.getGstnB2csKey(); existGstnProcessData.add(b2cKey); }
			 * if (existGstnProcessData.size() > 0 &&
			 * !existGstnProcessData.isEmpty()) {
			 * gstr1B2CSGstnRepository.UpdateSameGstnKey(existGstnProcessData);
			 * } gstr1B2CSGstnRepository.saveAll(addFile);
			 */

		}

		totalRecords = (listOfB2cs.size() != 0) ? listOfB2cs.size() : 0;
		errorRecords = (busErrorRecords.size() != 0
				|| structuralErrorsRecords.size() != 0)
						? busErrorRecords.size()
								+ structuralErrorsRecords.size()
						: 0;
		processedRecords = totalRecords - errorRecords;
		information = (infoProcessed.size() != 0) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	private void deriveEntityId(
			List<Gstr1AsEnteredHsnEntity> strucProcessedRecords) {

		
		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();
		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		strucProcessedRecords.stream().forEach(data->{
			Long entityId = gstinAndEntityMap.get(data.getSgstin());
			data.setEntityId(entityId);
			data.setEntityConfigParamMap(map);
		});
	
		
	}
}