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
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.data.repositories.client.InwardError3I3HRepository;
import com.ey.advisory.app.data.repositories.client.InwardExcel3I3HRepository;
import com.ey.advisory.app.data.repositories.client.InwardTable3HRepository;
import com.ey.advisory.app.services.docs.InwardTable3H3IErrorConvertion;
import com.ey.advisory.app.services.docs.InwardTable3H3IExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.table3h3i.Table3h3iStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Inward3h3iStructuralErrorUploadService")
@Slf4j
public class Inward3h3iStructuralErrorUploadService {

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Inward3h3iBusinessErrorUploadService")
	private Inward3h3iBusinessErrorUploadService 
	                                      inward3h3iBusinessErrorUploadService;

	@Autowired
	@Qualifier("InwardTable3H3IErrorConvertion")
	private InwardTable3H3IErrorConvertion inwardTable3H3IErrorConvertion;

	@Autowired
	@Qualifier("InwardTable3H3IExcelConvertion")
	private InwardTable3H3IExcelConvertion inwardTable3H3IExcelConvertion;

	@Autowired
	@Qualifier("InwardTable3HRepository")
	private InwardTable3HRepository table3HRepository;

	@Autowired
	@Qualifier("InwardError3I3HRepository")
	private InwardError3I3HRepository anxError3HRepository;

	@Autowired
	@Qualifier("Table3h3iStructValidationChain")
	private Table3h3iStructValidationChain table3H3IStructValidationChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("InwardExcel3I3HRepository")
	private InwardExcel3I3HRepository inwardExcel3I3HRepository;

	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> table3H3I,
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<InwardTable3I3HExcelEntity> strucErrorRecords = new ArrayList<>();
		List<InwardTable3I3HExcelEntity> strucProcessedRecords = new ArrayList<>();
		
		List<InwardTable3I3HExcelEntity> excelData = inwardTable3H3IExcelConvertion
				.convertSRFileToOutward3H3IExcel(table3H3I, updateFileStatus);
		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<InwardTable3I3HExcelEntity> excelDatas = inwardExcel3I3HRepository
				.saveAll(excelData);
		LOGGER.error("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = 
				table3H3IStructValidationChain.validation(table3H3I,excelDatas);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (InwardTable3I3HExcelEntity id : excelDatas) {
			Long asEnteredId = id.getId();
			ids.add(asEnteredId);
			String table3h3iGstnKey = id.getTable3h3iInvKey();
			if (!structuralValKeys.contains(table3h3iGstnKey)) {
				processedKeys.add(table3h3iGstnKey);
			}
		}

		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			strucErrorRecords = inwardExcel3I3HRepository
					.getAllExcelData(structuralValKeys, ids);
			inwardExcel3I3HRepository
			.table3h3iUpdateErrors(structuralValKeys, ids);
		}

		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = inwardExcel3I3HRepository
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
					.storedTable3HErrorRecords(strucErrorRecords, errorMap);

		}

		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			inward3h3iBusinessErrorUploadService.processBusinessData(table3H3I,
					strucProcessedRecords, strucErrorRecords,
					updateFileStatus, ids);
		} else {
			totalRecords = (excelDatas.size() != 0) ? excelDatas.size() : 0;
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
	}}

	