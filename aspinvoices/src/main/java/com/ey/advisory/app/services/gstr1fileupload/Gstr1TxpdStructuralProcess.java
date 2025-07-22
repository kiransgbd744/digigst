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
import com.ey.advisory.app.data.entities.client.Ann1VerticalWebError;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1AsEnterTxpdRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService;
import com.ey.advisory.app.services.docs.SRFileToTxpdDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.advanceAdjusted.AAStructValidationChain;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr1TxpdStructuralProcess")
@Slf4j
public class Gstr1TxpdStructuralProcess {

	@Autowired
	@Qualifier("AAStructValidationChain")
	private AAStructValidationChain aAStructValidationChain;

	@Autowired
	@Qualifier("Gstr1TxpdBusinessProcess")
	private Gstr1TxpdBusinessProcess gstr1TxpdBusinessProcess;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1AsEnterTxpdRepository")
	private Gstr1AsEnterTxpdRepository gstr1AsEnterTxpdRepository;

	@Autowired
	@Qualifier("SRFileToATADetailsConvertion")
	private SRFileToTxpdDetailsConvertion sRFileToATADetailsConvertion;

	 @Transactional(value = "clientTransactionManager")
	public void txpdStructureProcessData(List<Object[]> listOfAtas,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AsEnteredTxpdFileUploadEntity> strErrRecords = new ArrayList<>();
		List<Gstr1AsEnteredTxpdFileUploadEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		LOGGER.debug("Enter into Structural process data method");

		List<Gstr1AsEnteredTxpdFileUploadEntity> excelData = sRFileToATADetailsConvertion
				.convertSRFileToTxpdExcel(listOfAtas, updateFileStatus);
		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<Gstr1AsEnteredTxpdFileUploadEntity> excelDataSave = gstr1AsEnterTxpdRepository
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

		for (Gstr1AsEnteredTxpdFileUploadEntity id : excelDataSave) {
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
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
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
