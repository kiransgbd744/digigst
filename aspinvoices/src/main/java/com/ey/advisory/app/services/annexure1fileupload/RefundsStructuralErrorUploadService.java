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
import com.ey.advisory.app.data.entities.client.RefundsExcelEntity;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.RefundExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToRefundExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.refund.RefundStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

@Component("RefundsStructuralErrorUploadService")
public class RefundsStructuralErrorUploadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RefundsStructuralErrorUploadService.class);

	@Autowired
	@Qualifier("RefundBusinessErrorUploadService")
	private RefundBusinessErrorUploadService refundBusinessErrorUploadService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("RefundStructValidationChain")
	private RefundStructValidationChain refundStructValidationChain;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	@Autowired
	@Qualifier("RefundExcelRepository")
	private RefundExcelRepository refundExcelRepository;

	@Autowired
	@Qualifier("SRFileToRefundExcelConvertion")
	private SRFileToRefundExcelConvertion sRFileToRefundExcelConvertion;

	public void processData(List<Object[]> refundList,
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<RefundsExcelEntity> strucErrorRecords = new ArrayList<>();
		List<RefundsExcelEntity> strucProcessedRecords = new ArrayList<>();

		List<RefundsExcelEntity> excelData = sRFileToRefundExcelConvertion
				.convertSRFileToRefundExcel(refundList,
						updateFileStatus);

		LOGGER.debug("Starting Excel data Dumping process in to Table");
		List<RefundsExcelEntity> dumpExcelDatas = refundExcelRepository
				.saveAll(excelData);

		LOGGER.debug("Ended Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = 
				refundStructValidationChain.validation(refundList, dumpExcelDatas);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (RefundsExcelEntity id : dumpExcelDatas) {
			Long asEnterId = id.getId();
			ids.add(asEnterId);
			String refundInvkey = id.getRefundInvkey();
			if (!structuralValKeys.contains(refundInvkey)) {
				processedKeys.add(refundInvkey);
			}
		}

		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			strucErrorRecords = refundExcelRepository
					.getAllExcelData(structuralValKeys, ids);
			refundExcelRepository.REFUNDUpdateErrors(structuralValKeys,
					ids);
		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = refundExcelRepository
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
					.storedREFUNDErrorRecords(strucErrorRecords, errorMap);
		}

		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			refundBusinessErrorUploadService.processBusinessData(refundList,
					strucProcessedRecords, strucErrorRecords, updateFileStatus,
					ids);

		} else {
			totalRecords = (refundList.size() != 0) ? refundList.size() : 0;
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