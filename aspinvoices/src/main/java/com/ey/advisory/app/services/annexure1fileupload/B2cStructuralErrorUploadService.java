package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.STRUCTURAL_VALIDATIONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Ann1VerticalWebError;
import com.ey.advisory.app.data.entities.client.OutwardB2cExcelEntity;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.OutwardB2cExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToOutwardB2CExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.b2c.B2cStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("B2cStructuralErrorUploadService")
@Slf4j
public class B2cStructuralErrorUploadService {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("B2cStructValidationChain")
	private B2cStructValidationChain b2cStructValidationChain;

	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	@Autowired
	@Qualifier("OutwardB2cExcelRepository")
	private OutwardB2cExcelRepository outwardB2cExcelRepository;

	@Autowired
	@Qualifier("SRFileToOutwardB2CExcelConvertion")
	private SRFileToOutwardB2CExcelConvertion sRFileToOutwardB2CExcelConvertion;

	@Autowired
	@Qualifier("B2cBusinessErrorUploadService")
	private B2cBusinessErrorUploadService b2cBusinessErrorUploadService;
	
	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> listOfB2c,
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		List<OutwardB2cExcelEntity> structuralErrorsRecords = new ArrayList<>();
		List<OutwardB2cExcelEntity> strucProcessedRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		List<OutwardB2cExcelEntity> excelData = sRFileToOutwardB2CExcelConvertion
				.convertSRFileToOutwardB2cExcel(listOfB2c, updateFileStatus);

		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<OutwardB2cExcelEntity> saveExcelAll = outwardB2cExcelRepository
				.saveAll(excelData);
		LOGGER.error("Ending Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults =
				b2cStructValidationChain.validation(listOfB2c, saveExcelAll);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}

		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (OutwardB2cExcelEntity id : saveExcelAll) {
			ids.add(id.getId());
			String b2cInvKey = id.getB2cInvKey();
			if (!structuralValKeys.contains(b2cInvKey)) {
				processedKeys.add(b2cInvKey);
			}
		}
		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			structuralErrorsRecords = outwardB2cExcelRepository
					.getAllExcelData(structuralValKeys, ids);
			outwardB2cExcelRepository
					.b2cUpdateStructuralError(structuralValKeys, ids);

		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = outwardB2cExcelRepository
					.getAllExcelData(processedKeys, ids);
		}

		LOGGER.error("strucErrorRecords ", structuralErrorsRecords.size());
		LOGGER.error("strucProcessRecords ", strucProcessedRecords.size());

		if (structuralErrorsRecords.size() > 0
				&& !structuralErrorsRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);

			verticalWebUploadErrorService
					.storedErrorRecords(structuralErrorsRecords, errorMap);

		}
		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			b2cBusinessErrorUploadService.processBusinessData(listOfB2c,
					strucProcessedRecords, structuralErrorsRecords,
					updateFileStatus, ids);
		} else {
			totalRecords = (saveExcelAll.size() != 0) ? saveExcelAll.size() : 0;
			errorRecords = (structuralErrorsRecords.size() != 0)
					? structuralErrorsRecords.size() : 0;
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