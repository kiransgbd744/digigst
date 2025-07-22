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
import com.ey.advisory.app.data.entities.client.CewbExcelEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.CewbExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToCewbExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.cewb.CewbStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("CewbStructuralErrorUploadService")
@Slf4j
public class CewbStructuralErrorUploadService {

	@Autowired
	@Qualifier("CewbBusinessErrorUploadService")
	private CewbBusinessErrorUploadService cewbBusinessErrorUploadService; 

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("CewbStructValidationChain")
	private CewbStructValidationChain cewbStructValidationChain;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("CewbExcelRepository")
	private CewbExcelRepository cewbExcelRepository;

	@Autowired
	@Qualifier("SRFileToCewbExcelConvertion")
	private SRFileToCewbExcelConvertion sRFileToCewbExcelConvertion;

	public void processData(List<Object[]> listOfCewb,
			Gstr1FileStatusEntity updateFileStatus) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processData methood enter of CEWB");
		}
		String tenantCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tenant Id Is {}", tenantCode);
		}
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<CewbExcelEntity> strErrRecords = new ArrayList<>();
		List<CewbExcelEntity> strProcessRecords = new ArrayList<>();

		List<CewbExcelEntity> excelData = sRFileToCewbExcelConvertion
				.convertSRFileToCewbExcel(listOfCewb, updateFileStatus);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting Excel data Dumping process in to Table");
		}
		List<CewbExcelEntity> dumpExcelDatas = cewbExcelRepository
				.saveAll(excelData);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting Excel data Dumping process in to Table");
		}

		Map<String, List<ProcessingResult>> processingResults = cewbStructValidationChain
				.validation(listOfCewb, dumpExcelDatas);

		List<String> strErrorKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		for (CewbExcelEntity id : dumpExcelDatas) {
			String cewbInvKey = id.getCewbInvKey();
			if (!strErrorKeys.contains(cewbInvKey)) {
				strProcessRecords.add(id);
			} else {
				id.setError(true);
				strErrRecords.add(id);
			}
		}
		if (!strErrRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService.storedCewbRecords(strErrRecords,
					errorMap);
		}

		if (!strProcessRecords.isEmpty()) {
			cewbBusinessErrorUploadService.processBusinessData(listOfCewb,
					strProcessRecords, strErrRecords, updateFileStatus);

		} else {
			totalRecords = (!listOfCewb.isEmpty()) ? listOfCewb.size() : 0;
			errorRecords = (!strErrRecords.isEmpty()) ? strErrRecords.size()
					: 0;
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