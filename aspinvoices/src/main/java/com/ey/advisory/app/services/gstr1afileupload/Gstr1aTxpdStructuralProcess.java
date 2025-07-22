package com.ey.advisory.app.services.gstr1afileupload;

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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AAsEnterTxpdRepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.doc.gstr1a.SRFileToGstr1ATxpdDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.advanceAdjusted.Gstr1AAAStructValidationChain;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1aTxpdStructuralProcess")
@Slf4j
public class Gstr1aTxpdStructuralProcess {

	@Autowired
	@Qualifier("Gstr1AAAStructValidationChain")
	private Gstr1AAAStructValidationChain aAStructValidationChain;

	@Autowired
	@Qualifier("Gstr1aTxpdBusinessProcess")
	private Gstr1aTxpdBusinessProcess gstr1TxpdBusinessProcess;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1AAsEnterTxpdRepository")
	private Gstr1AAsEnterTxpdRepository gstr1AsEnterTxpdRepository;

	@Autowired
	@Qualifier("SRFileToGstr1ATxpdDetailsConvertion")
	private SRFileToGstr1ATxpdDetailsConvertion sRFileToATADetailsConvertion;

	@Transactional(value = "clientTransactionManager")
	public void txpdStructureProcessData(List<Object[]> listOfAtas,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AAsEnteredTxpdFileUploadEntity> strErrRecords = new ArrayList<>();
		List<Gstr1AAsEnteredTxpdFileUploadEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		LOGGER.debug("Enter into Structural process data method");

		List<Gstr1AAsEnteredTxpdFileUploadEntity> excelData = sRFileToATADetailsConvertion
				.convertSRFileToTxpdExcel(listOfAtas, updateFileStatus);
		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<Gstr1AAsEnteredTxpdFileUploadEntity> excelDataSave = gstr1AsEnterTxpdRepository
				.saveAll(excelData);
		LOGGER.error("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = aAStructValidationChain
				.validation(listOfAtas, excelDataSave);

		List<String> strErrorKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		LOGGER.debug("Structural Validations Keys" + strErrorKeys);

		for (Gstr1AAsEnteredTxpdFileUploadEntity id : excelDataSave) {
			Long valuesId = id.getId();
			String txpdInvKey = id.getTxpdInvKey();
			if (!strErrorKeys.contains(txpdInvKey)) {
				strProcessRecords.add(id);
			} else {
				id.setError(true);
				strErrRecords.add(id);
			}
		}
		LOGGER.error("strucErrorRecords ", strErrRecords.size());
		LOGGER.error("strucProcessRecords ", strProcessRecords.size());

		if (strErrRecords.size() > 0 && !strErrRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedErrorGstr1TxpdRecords(strErrRecords, errorMap);
		}

		if (strProcessRecords.size() > 0 && !strProcessRecords.isEmpty()) {
			gstr1TxpdBusinessProcess.processTxpdBusinessData(listOfAtas,
					strProcessRecords, strErrRecords, updateFileStatus);
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
