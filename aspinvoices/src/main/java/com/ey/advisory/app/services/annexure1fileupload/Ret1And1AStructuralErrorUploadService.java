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
import com.ey.advisory.app.data.entities.client.Ret1And1AExcelEntity;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.Ret1And1AExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToRet1And1AExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.ret1and1a.Ret1And1AStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Ret1And1AStructuralErrorUploadService")
public class Ret1And1AStructuralErrorUploadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1And1AStructuralErrorUploadService.class);

	@Autowired
	@Qualifier("Ret1And1ABusinessErrorUploadService")
	private Ret1And1ABusinessErrorUploadService ret1And1ABusinessErrorUploadService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Ret1And1AStructValidationChain")
	private Ret1And1AStructValidationChain ret1And1AStructValidationChain;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	@Autowired
	@Qualifier("Ret1And1AExcelRepository")
	private Ret1And1AExcelRepository ret1And1AExcelRepository;

	@Autowired
	@Qualifier("SRFileToRet1And1AExcelConvertion")
	private SRFileToRet1And1AExcelConvertion sRFileToRet1And1AExcelConvertion;

	public void processData(List<Object[]> ret1And1Adata,
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<Ret1And1AExcelEntity> strucErrorRecords = new ArrayList<>();
		List<Ret1And1AExcelEntity> strucProcessedRecords = new ArrayList<>();

		List<Ret1And1AExcelEntity> excelData = sRFileToRet1And1AExcelConvertion
				.convertSRFileToRet1And1AExcel(ret1And1Adata,updateFileStatus);

		LOGGER.debug("Starting Excel data Dumping process in to Table");
		List<Ret1And1AExcelEntity> dumpExcelDatas = ret1And1AExcelRepository
				.saveAll(excelData);

		LOGGER.debug("Ending Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults =
				ret1And1AStructValidationChain.validation(ret1And1Adata, 
						dumpExcelDatas);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (Ret1And1AExcelEntity id : dumpExcelDatas) {
			Long asEnterId = id.getId();
			ids.add(asEnterId);
			String ret1And1AInvKey = id.getRet1And1AInvkey();
			if (!structuralValKeys.contains(ret1And1AInvKey)) {
				processedKeys.add(ret1And1AInvKey);
			}
		}

		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			strucErrorRecords = ret1And1AExcelRepository
					.getAllExcelData(structuralValKeys, ids);
			ret1And1AExcelRepository.RET1UpdateErrors(structuralValKeys,
					ids);
		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = ret1And1AExcelRepository
					.getAllExcelData(processedKeys, ids);
		}
		LOGGER.debug("strucProcessedRecords " + strucProcessedRecords.size());
		LOGGER.debug("strucErrorRecords " + strucErrorRecords.size());

		if (strucErrorRecords.size() > 0 && !strucErrorRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Ann1VerticalWebError>> errorMap = 
					verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedTableRet1And1ARecords(strucErrorRecords, errorMap);
		}

		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			ret1And1ABusinessErrorUploadService.processBusinessData(
					ret1And1Adata, strucProcessedRecords, strucErrorRecords,
					updateFileStatus, ids);

		} else {
			totalRecords = (ret1And1Adata.size() != 0) ? ret1And1Adata.size()
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