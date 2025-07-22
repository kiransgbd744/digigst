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
import com.ey.advisory.app.data.entities.client.OutwardTable4ExcelEntity;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.OutwardTable4ExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToOutwardTable4ExcelConvertion;
import com.ey.advisory.app.services.docs.SRFileToOutwardTableDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.table4.Table4StructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Component("Table4StructuralErrorUploadService")
@Slf4j
public class Table4StructuralErrorUploadService {

	@Autowired
	@Qualifier("Table4BusinessErrorUploadService")
	private Table4BusinessErrorUploadService table4BusinessErrorUploadService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Table4StructValidationChain")
	private Table4StructValidationChain table4StructValidationChain;

	@Autowired
	@Qualifier("SRFileToOutwardTableDetailsConvertion")
	private SRFileToOutwardTableDetailsConvertion sRFileToOutwardTableDetailsConvertion;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	@Autowired
	@Qualifier("OutwardTable4ExcelRepository")
	private OutwardTable4ExcelRepository outwardTable4ExcelRepository;

	@Autowired
	@Qualifier("SRFileToOutwardTable4ExcelConvertion")
	private SRFileToOutwardTable4ExcelConvertion sRFileToOutwardTable4ExcelConvertion;

	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> table4List,
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<OutwardTable4ExcelEntity> strucErrorRecords = new ArrayList<>();
		List<OutwardTable4ExcelEntity> strucProcessedRecords = new ArrayList<>();

		List<OutwardTable4ExcelEntity> excelData = sRFileToOutwardTable4ExcelConvertion
				.convertSRFileToOutwardTable4Excel(table4List,
						updateFileStatus);

		LOGGER.debug("Starting Excel data Dumping process in to Table");
		List<OutwardTable4ExcelEntity> dumpExcelDatas = outwardTable4ExcelRepository
				.saveAll(excelData);

		LOGGER.debug("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = 
				table4StructValidationChain.validation(table4List, dumpExcelDatas);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (OutwardTable4ExcelEntity id : dumpExcelDatas) {
			Long asEnterId = id.getId();
			ids.add(asEnterId);
			String table4InvKey = id.getTable4Invkey();
			if (!structuralValKeys.contains(table4InvKey)) {
				processedKeys.add(table4InvKey);
			}
		}

		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			strucErrorRecords = outwardTable4ExcelRepository
					.getAllExcelData(structuralValKeys, ids);
			outwardTable4ExcelRepository.table4UpdateErrors(structuralValKeys,
					ids);
		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = outwardTable4ExcelRepository
					.getAllExcelData(processedKeys, ids);
		}
		LOGGER.error("strucProcessedRecords " + strucProcessedRecords.size());
		LOGGER.error("strucErrorRecords " + strucErrorRecords.size());

		if (strucErrorRecords.size() > 0 && !strucErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedTable4ErrorRecords(strucErrorRecords, errorMap);
		}

		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			table4BusinessErrorUploadService.processBusinessData(table4List,
					strucProcessedRecords, strucErrorRecords, updateFileStatus,
					ids);

		} else {
			totalRecords = (table4List.size() != 0) ? table4List.size() : 0;
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