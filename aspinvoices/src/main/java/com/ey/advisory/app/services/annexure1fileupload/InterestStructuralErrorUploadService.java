package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.STRUCTURAL_VALIDATIONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Ann1VerticalWebError;
import com.ey.advisory.app.data.entities.client.InterestExcelEntity;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.InterestExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToIntersetExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.interest.InterestStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

@Component("InterestStructuralErrorUploadService")
public class InterestStructuralErrorUploadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InterestStructuralErrorUploadService.class);

	@Autowired
	@Qualifier("InterestBusinessErrorUploadService")
	private InterestBusinessErrorUploadService interestBusinessErrorUploadService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("InterestStructValidationChain")
	private InterestStructValidationChain interestStructValidationChain;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	@Autowired
	@Qualifier("InterestExcelRepository")
	private InterestExcelRepository interestExcelRepository;

	@Autowired
	@Qualifier("SRFileToIntersetExcelConvertion")
	private SRFileToIntersetExcelConvertion sRFileToIntersetExcelConvertion;

	public void processData(List<Object[]> listOfInterest,
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<InterestExcelEntity> strucErrorRecords = new ArrayList<>();
		List<InterestExcelEntity> strucProcessedRecords = new ArrayList<>();

		List<InterestExcelEntity> excelData = sRFileToIntersetExcelConvertion
				.convertSRFileToInterestExcel(listOfInterest, updateFileStatus);

		LOGGER.debug("Starting Excel data Dumping process in to Table");
		List<InterestExcelEntity> dumpExcelDatas = interestExcelRepository
				.saveAll(excelData);

		LOGGER.debug("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults =
				interestStructValidationChain
				.validation(listOfInterest, dumpExcelDatas);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (InterestExcelEntity id : dumpExcelDatas) {
			Long asEnterId = id.getId();
			ids.add(asEnterId);
			String InvKey = id.getInterestInvKey();
			if (!structuralValKeys.contains(InvKey)) {
				processedKeys.add(InvKey);
			}
		}

		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			strucErrorRecords = interestExcelRepository
					.getAllExcelData(structuralValKeys, ids);
			interestExcelRepository.INTERESTUpdateErrors(structuralValKeys,
					ids);
		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = interestExcelRepository
					.getAllExcelData(processedKeys, ids);
		}
		LOGGER.error("strucProcessedRecords " + strucProcessedRecords.size());
		LOGGER.error("strucErrorRecords " + strucErrorRecords.size());

		if (strucErrorRecords.size() > 0 && !strucErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedINTERESTErrorRecords(strucErrorRecords, errorMap);
		}

		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			interestBusinessErrorUploadService.processBusinessData(
					listOfInterest, strucProcessedRecords, strucErrorRecords,
					updateFileStatus, ids);

		} else {
			totalRecords = (listOfInterest.size() != 0) ? listOfInterest.size()
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