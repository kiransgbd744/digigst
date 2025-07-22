package com.ey.advisory.app.services.gstr1afileupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ANilNonExemptedAsEnteredEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ANilNonExtAsRepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.doc.gstr1a.SRFileToGstr1ANilDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.NilNonExpt.Gstr1ANilNonExptStructValidationChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ANilNonExtStructuralProcess")
@Slf4j
public class Gstr1ANilNonExtStructuralProcess {

	@Autowired
	@Qualifier("Gstr1ANilNonExmtBusinessProcess")
	private Gstr1ANilNonExmtBusinessProcess gstr1NilNonExmtBusinessProcess;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1ANilNonExtAsRepository")
	private Gstr1ANilNonExtAsRepository gstr1NilNonExtAsRepository;

	@Autowired
	@Qualifier("SRFileToGstr1ANilDetailsConvertion")
	private SRFileToGstr1ANilDetailsConvertion sRFileToNilDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1ANilNonExptStructValidationChain")
	private Gstr1ANilNonExptStructValidationChain nilNonExptStructValidationChain;

	@Transactional(value = "clientTransactionManager")
	public void nilNonExtStructureProcessData(List<Object[]> listOfNil,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1ANilNonExemptedAsEnteredEntity> strErrRecords = new ArrayList<>();
		List<Gstr1ANilNonExemptedAsEnteredEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		LOGGER.debug("Enter into Structural process data method");

		List<Gstr1ANilNonExemptedAsEnteredEntity> excelData = sRFileToNilDetailsConvertion
				.convertSRFileToNilExcelDoc(listOfNil, updateFileStatus);

		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<Gstr1ANilNonExemptedAsEnteredEntity> excelDataSave = gstr1NilNonExtAsRepository
				.saveAll(excelData);
		LOGGER.error("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = nilNonExptStructValidationChain
				.validation(listOfNil, excelDataSave);

		List<String> strErrorKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		LOGGER.debug("Structural Validations Keys" + strErrorKeys);

		List<Long> ids = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (Gstr1ANilNonExemptedAsEnteredEntity id : excelDataSave) {
			ids.add(id.getId());
			String txpdInvKey = id.getNKey();
			if (!strErrorKeys.contains(txpdInvKey)) {
				processedKeys.add(txpdInvKey);
			}
		}

		if (strErrorKeys.size() > 0 && !strErrorKeys.isEmpty()) {
			strErrRecords = gstr1NilNonExtAsRepository
					.getAllExcelData(strErrorKeys, ids);
			gstr1NilNonExtAsRepository.invUpdateStrError(strErrorKeys, ids);

		}
		if (processedKeys.size() > 0 && !processedKeys.isEmpty()) {
			strProcessRecords = gstr1NilNonExtAsRepository
					.getAllExcelData(processedKeys, ids);
		}

		LOGGER.error("strucErrorRecords ", strErrRecords.size());
		LOGGER.error("strucProcessRecords ", strProcessRecords.size());

		if (strErrRecords.size() > 0 && !strErrRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults,
							GSTConstants.STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedErrorGstr1NilRecords(strErrRecords, errorMap);

		}

		if (strProcessRecords.size() > 0 && !strProcessRecords.isEmpty()) {
			gstr1NilNonExmtBusinessProcess.processTxpdBusinessData(listOfNil,
					strProcessRecords, strErrRecords, updateFileStatus, ids);
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
