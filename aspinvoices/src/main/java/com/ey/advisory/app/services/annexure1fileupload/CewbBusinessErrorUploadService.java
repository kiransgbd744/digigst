package com.ey.advisory.app.services.annexure1fileupload;

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

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.CewbEntity;
import com.ey.advisory.app.data.entities.client.CewbExcelEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.CewbRepository;
import com.ey.advisory.app.services.docs.SRFileToCewbExcelConvertion;
import com.ey.advisory.app.services.validation.cewb.CewbValidationChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.ewb.common.EyEwbCommonUtil;
import com.ey.advisory.ewb.dto.ConsolidateEWBReqDto;
import com.ey.advisory.ewb.dto.TripSheetEwbBills;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Component("CewbBusinessErrorUploadService")
@Slf4j
public class CewbBusinessErrorUploadService {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("SRFileToCewbExcelConvertion")
	private SRFileToCewbExcelConvertion sRFileToCewbExcelConvertion;

	@Autowired
	@Qualifier("CewbValidationChain")
	private CewbValidationChain cewbValidationChain;

	@Autowired
	@Qualifier("CewbRepository")
	private CewbRepository cewbRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final String NEWFARMATTER = "dd-MM-yyyy";

	public void processBusinessData(List<Object[]> cewbList,
			List<CewbExcelEntity> strProcessRecords,
			List<CewbExcelEntity> strErrRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";

		List<CewbExcelEntity> businessErrorRecords = new ArrayList<>();
		List<CewbExcelEntity> infoProcessed = new ArrayList<>();
		List<CewbExcelEntity> busProcessRecords = new ArrayList<>();

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

		for (CewbExcelEntity cewb : strProcessRecords) {
			List<ProcessingResult> results = cewbValidationChain.validate(cewb,
					null);
			Long id = cewb.getId();
			if (!results.isEmpty()) {
				String key = cewb.getCewbInvKey();
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
					if (!errorType.isEmpty()) {
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
					if (!errorType.isEmpty()) {
						if (errorType.contains(GSTConstants.ERROR)
								&& errorType.contains(GSTConstants.INFO)) {
							errorInfo.add(key);
							errorKeys.add(key);
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

		for (CewbExcelEntity process : strProcessRecords) {
			String key = process.getCewbInvKey();
			if (!errorKeys.contains(key)) {
				if (infoKeys != null && infoKeys.contains(key)) {
					process.setInfo(true);
					infoProcessed.add(process);
				}
				busProcessRecords.add(process);
			} else {
				if (errorInfo != null && errorInfo.contains(key)) {
					process.setInfo(true);
				}
				process.setError(true);
				businessErrorRecords.add(process);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("businessErrorRecords ", businessErrorRecords.size());
			LOGGER.debug("businessProcessedRecords ", busProcessRecords.size());
		}
		if (!businessValErrors.isEmpty()) {
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(businessValErrors, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedCewbRecords(businessErrorRecords, errorMap);
		}

		if (!infoWithProcessed.isEmpty()) {
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(infoWithProcessed, BUSINESS_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService.storedCewbRecords(infoProcessed,
					errorMap);
		}

		if (!busProcessRecords.isEmpty()) {
			List<CewbEntity> processedOfBusi = sRFileToCewbExcelConvertion
					.convertSRFileToCewb(busProcessRecords, updateFileStatus);

			for (CewbEntity interestUpdate : processedOfBusi) {
				String cewbKey = interestUpdate.getCewbInvKey();
				cewbRepository.UpdateSameInvKey(cewbKey);
			}
			cewbRepository.saveAll(processedOfBusi);

			Map<Long, List<CewbEntity>> allDocsMap = processedOfBusi.stream()
					.collect(Collectors.groupingBy(CewbEntity::getSNo));
			Map<Long, Integer> sNoMap = new HashMap<Long, Integer>();

			ConsolidateEWBReqDto cewbDto = new ConsolidateEWBReqDto();
			for (CewbEntity processData : processedOfBusi) {

				if (sNoMap.containsKey(processData.getSNo()))
					continue;
				sNoMap.put(processData.getSNo(), 1);
				cewbDto.setSerialNo(processData.getSNo());
				cewbDto.setFileId(processData.getFileId());
				cewbDto.setGstin(processData.getGstin());
				cewbDto.setFromPlace(processData.getFromPlace());
				cewbDto.setFromState(processData.getFromState());
				cewbDto.setTransDocDate(processData.getTransDocDate());
				cewbDto.setTransDocNo(processData.getTransDocNo());
				cewbDto.setTransMode(EyEwbCommonUtil.getTransMode(processData.getTransMode()));
				cewbDto.setVehicleNo(processData.getVehicleNo());
				cewbDto.setTripSheetEwbBills(
						getEwbNums(allDocsMap.get(processData.getSNo())));

				createCewbjob(cewbDto);
			}
		}

		totalRecords = (!cewbList.isEmpty()) ? cewbList.size() : 0;
		errorRecords = (!businessErrorRecords.isEmpty()
				|| !strErrRecords.isEmpty())
						? businessErrorRecords.size() + strErrRecords.size()
						: 0;
		processedRecords = totalRecords - errorRecords;
		information = (!infoProcessed.isEmpty()) ? infoProcessed.size() : 0;

		updateFileStatus.setTotal(totalRecords);
		updateFileStatus.setProcessed(processedRecords);
		updateFileStatus.setError(errorRecords);
		updateFileStatus.setInformation(information);
		gstr1FileStatusRepository.save(updateFileStatus);

	}

	private void createCewbjob(ConsolidateEWBReqDto cewbReqDto) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to create CEWB AsyncJob ");
		}
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonElement respBody = gson.toJsonTree(cewbReqDto);
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		asyncJobsService.createJob(TenantContext.getTenantId(),
				JobConstants.EWB_CONSOLIDTAED, respBody.toString(), userName,
				1L, null, 1L);
	}

	private static List<TripSheetEwbBills> getEwbNums(
			List<CewbEntity> processedOfBusi) {

		List<TripSheetEwbBills> ewnNums = new ArrayList<>();
		for (CewbEntity processData : processedOfBusi) {
			TripSheetEwbBills cewbDto = new TripSheetEwbBills();
			cewbDto.setEwbNo(processData.getEwbNo());
			ewnNums.add(cewbDto);
		}
		return ewnNums;

	}
}
