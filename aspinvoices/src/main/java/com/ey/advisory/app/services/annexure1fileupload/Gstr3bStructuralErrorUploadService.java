package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.STRUCTURAL_VALIDATIONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.data.entities.client.Gstr3bVerticalWebError;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.Gstr3bExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToGstr3BConvertion;
import com.ey.advisory.app.services.strcutvalidation.gstr3b.Gstr3BStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr3bStructuralErrorUploadService")
@Slf4j
public class Gstr3bStructuralErrorUploadService {

	@Autowired
	@Qualifier("Gstr3bBusinessErrorUploadService")
	private Gstr3bBusinessErrorUploadService gstr3bBusinessErrorUploadService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr3BStructValidationChain")
	private Gstr3BStructValidationChain gstr3BStructValidationChain;

	@Autowired
	@Qualifier("Gstr3BWebUploadErrorService")
	private Gstr3BWebUploadErrorService gstr3BWebUploadErrorService;

	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	@Autowired
	@Qualifier("Gstr3bExcelRepository")
	private Gstr3bExcelRepository gstr3bExcelRepository;

	@Autowired
	@Qualifier("SRFileToGstr3BConvertion")
	private SRFileToGstr3BConvertion sRFileToGstr3BConvertion;

	public void processData(List<Object[]> listOfGstr3BData, 
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<Gstr3bExcelEntity> strucErrorRecords = new ArrayList<>();
		List<Gstr3bExcelEntity> strucProcessedRecords = new ArrayList<>();

		List<Gstr3bExcelEntity> excelData = sRFileToGstr3BConvertion
				.convertSRFileToGstr3BExcel(listOfGstr3BData, updateFileStatus);

		LOGGER.debug("Starting Excel data Dumping process in to Table");
		List<Gstr3bExcelEntity> dumpExcelDatas = gstr3bExcelRepository
				.saveAll(excelData);

		LOGGER.debug("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = 
				gstr3BStructValidationChain
				.validation(listOfGstr3BData, dumpExcelDatas);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (Gstr3bExcelEntity id : dumpExcelDatas) {
			Long asEnterId = id.getId();
			ids.add(asEnterId);
			String InvKey = id.getInvKey();
			if (!structuralValKeys.contains(InvKey)) {
				processedKeys.add(InvKey);
			}
		}

		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			strucErrorRecords = gstr3bExcelRepository
					.getAllExcelData(structuralValKeys, ids);
			gstr3bExcelRepository.Gstr3bUpdateErrors(structuralValKeys, ids);
		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = gstr3bExcelRepository
					.getAllExcelData(processedKeys, ids);
		}
		LOGGER.error("strucProcessedRecords " + strucProcessedRecords.size());
		LOGGER.error("strucErrorRecords " + strucErrorRecords.size());

		if (strucErrorRecords.size() > 0 && !strucErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr3bVerticalWebError>> errorMap = 
					gstr3BWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			gstr3BWebUploadErrorService
					.storedGstr3bErrorRecords(strucErrorRecords, errorMap);
		}

		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			gstr3bBusinessErrorUploadService.processBusinessData(listOfGstr3BData,
					strucProcessedRecords, strucErrorRecords, updateFileStatus,
					ids);

		} else {
			totalRecords = (listOfGstr3BData.size() != 0) ? listOfGstr3BData.size()
					: 0;
			errorRecords = (strucErrorRecords.size() != 0)
					? strucErrorRecords.size() : 0;
			processedRecords = totalRecords - errorRecords;
			information = 0;

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			gstr1FileStatusRepository.save(updateFileStatus);
		}
	}
}