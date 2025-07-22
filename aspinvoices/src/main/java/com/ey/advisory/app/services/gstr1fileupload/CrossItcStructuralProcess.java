package com.ey.advisory.app.services.gstr1fileupload;

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
import com.ey.advisory.app.data.entities.client.CrossItcAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.CrossITCAsEnteredRepository;
import com.ey.advisory.app.services.annexure1fileupload.CrossItcBusinessErrorUploadService;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService2;
import com.ey.advisory.app.services.docs.SRFileToCrossItcDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.crossitc.CrossItcStructValidationChain;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("CrossItcStructuralProcess")
@Slf4j
public class CrossItcStructuralProcess {

	@Autowired
	@Qualifier("CrossItcStructValidationChain")
	private CrossItcStructValidationChain crossItcStructValidationChain;

	@Autowired
	@Qualifier("CrossItcBusinessErrorUploadService")
	private CrossItcBusinessErrorUploadService crossItcBusinessErrorUploadService;

	@Autowired
	@Qualifier("SRFileToCrossItcDetailsConvertion")
	private SRFileToCrossItcDetailsConvertion sRFileToCrossItcDetailsConvertion;

	@Autowired
	@Qualifier("CrossITCAsEnteredRepository")
	private CrossITCAsEnteredRepository crossITCAsEnteredRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> crossItcLists,
			Gstr1FileStatusEntity updateFileStatus) {

		List<CrossItcAsEnteredEntity> strErrRecords = new ArrayList<>();
		List<CrossItcAsEnteredEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Enter into Structural process data method");

		List<CrossItcAsEnteredEntity> excelData = sRFileToCrossItcDetailsConvertion
				.convertSRFileToCrossItcExcel(crossItcLists, updateFileStatus);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Starting Excel data Dumping process in to Table");
		List<CrossItcAsEnteredEntity> excelDataSave = crossITCAsEnteredRepository
				.saveAll(excelData);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = crossItcStructValidationChain
				.validation(crossItcLists, excelDataSave);

		List<String> strErrorKeys = new ArrayList<>();

		for (String k : processingResults.keySet()) {
			String errkey = k.substring(0, k.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		for (CrossItcAsEnteredEntity id : excelData) {
			String cewbInvKey = id.getCrossItcDocKey();
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
			verticalWebUploadErrorService
					.storedErrorCrossItcRecords(strErrRecords, errorMap);
		}
		if (strProcessRecords.size() > 0 && !strProcessRecords.isEmpty()) {
			crossItcBusinessErrorUploadService.processBusinessData(
					crossItcLists, strProcessRecords, strErrRecords,
					updateFileStatus);
		} else {
			totalRecords = (excelData.size() != 0) ? excelData.size() : 0;
			errorRecords = (strErrRecords.size() != 0) ? strErrRecords.size()
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