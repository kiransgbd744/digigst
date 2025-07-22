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
import com.ey.advisory.app.data.entities.client.SetOffAndUtilExcelEntity;
import com.ey.advisory.app.data.repositories.client.Ann1VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.SetOffAndUtilExcelRepository;
import com.ey.advisory.app.services.docs.SRFileToSetOffAndUtilExcelConvertion;
import com.ey.advisory.app.services.strcutvalidation.setoffandutil.SetOffAndUtilStructValidationChain;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

@Component("SetOffAndUtilStructuralErrorUploadService")
public class SetOffAndUtilStructuralErrorUploadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SetOffAndUtilStructuralErrorUploadService.class);

	@Autowired
	@Qualifier("SetOffBusinessErrorUploadService")
	private SetOffBusinessErrorUploadService setOffBusinessErrorUploadService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("SetOffAndUtilStructValidationChain")
	private SetOffAndUtilStructValidationChain setOffAndUtilStructValidationChain;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Ann1VerticalWebErrorRepo")
	private Ann1VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	@Autowired
	@Qualifier("SetOffAndUtilExcelRepository")
	private SetOffAndUtilExcelRepository setOffAndUtilExcelRepository;

	@Autowired
	@Qualifier("SRFileToSetOffAndUtilExcelConvertion")
	private SRFileToSetOffAndUtilExcelConvertion sRFileToSetOffAndUtilExcelConvertion;

	public void processData(List<Object[]> listOfSetOff,
			Gstr1FileStatusEntity updateFileStatus) {
		LOGGER.error("processData methood enter");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		List<SetOffAndUtilExcelEntity> strucErrorRecords = new ArrayList<>();
		List<SetOffAndUtilExcelEntity> strucProcessedRecords = new ArrayList<>();

		List<SetOffAndUtilExcelEntity> excelData = 
				sRFileToSetOffAndUtilExcelConvertion
				.convertSRFileToSetOffAndUtil(listOfSetOff, updateFileStatus);

		LOGGER.debug("Starting Excel data Dumping process in to Table");
		List<SetOffAndUtilExcelEntity> dumpExcelDatas = 
				setOffAndUtilExcelRepository.saveAll(excelData);

		LOGGER.debug("Ended Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults =
				setOffAndUtilStructValidationChain
				.validation(listOfSetOff, dumpExcelDatas);

		List<String> structuralValKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			structuralValKeys.add(errkey);
		}
		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (SetOffAndUtilExcelEntity id : dumpExcelDatas) {
			Long asEnterId = id.getId();
			ids.add(asEnterId);
			String table4InvKey = id.getSetOffInvKey();
			if (!structuralValKeys.contains(table4InvKey)) {
				processedKeys.add(table4InvKey);
			}
		}

		if (structuralValKeys.size() > 0 && !structuralValKeys.isEmpty()) {
			strucErrorRecords = setOffAndUtilExcelRepository
					.getAllExcelData(structuralValKeys, ids);
			setOffAndUtilExcelRepository.SetOffUpdateErrors(structuralValKeys,
					ids);
		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strucProcessedRecords = setOffAndUtilExcelRepository
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
					.storedSetOffErrorRecords(strucErrorRecords, errorMap);
		}

		if (strucProcessedRecords.size() > 0
				&& !strucProcessedRecords.isEmpty()) {
			setOffBusinessErrorUploadService.processBusinessData(listOfSetOff,
					strucProcessedRecords, strucErrorRecords, updateFileStatus,
					ids);

		} else {
			totalRecords = (listOfSetOff.size() != 0) ? listOfSetOff.size() : 0;
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