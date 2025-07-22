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
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AB2CSAsEnteredRepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.doc.gstr1a.SRFileToGstr1aB2CSExelDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.b2cs.Gstr1aB2csStructValidationChain;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1aB2csStructuralProcess")
@Slf4j
public class Gstr1aB2csStructuralProcess {

	@Autowired
	@Qualifier("Gstr1aB2csStructValidationChain")
	private Gstr1aB2csStructValidationChain b2csStructValidationChain;

	@Autowired
	@Qualifier("Gstr1AB2csBusinessProcess")
	private Gstr1AB2csBusinessProcess gstr1aB2csBusinessProcess;

	@Autowired
	@Qualifier("SRFileToGstr1aB2CSExelDetailsConvertion")
	private SRFileToGstr1aB2CSExelDetailsConvertion sRFileToB2CSExelDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1AB2CSAsEnteredRepository")
	private Gstr1AB2CSAsEnteredRepository gstr1aB2CSAsEnteredRepository;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> listOfB2cs,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AAsEnteredB2csEntity> strErrRecords = new ArrayList<>();
		List<Gstr1AAsEnteredB2csEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		LOGGER.debug("Enter into Structural process data method");

		List<Gstr1AAsEnteredB2csEntity> excelData = sRFileToB2CSExelDetailsConvertion
				.convertSRFileToB2csExcel(listOfB2cs, updateFileStatus);
		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<Gstr1AAsEnteredB2csEntity> excelDataSave = gstr1aB2CSAsEnteredRepository
				.saveAll(excelData);
		LOGGER.error("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = b2csStructValidationChain
				.validation(listOfB2cs, excelDataSave);

		List<String> strErrorKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		LOGGER.debug("Structural Validations Keys" + strErrorKeys);
		for (Gstr1AAsEnteredB2csEntity id : excelDataSave) {
			String b2csInvKey = id.getInvB2csKey();
			if (!strErrorKeys.contains(b2csInvKey)) {
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
					.storedErrorGstr1B2csRecords(strErrRecords, errorMap);

		}
		if (strProcessRecords.size() > 0 && !strProcessRecords.isEmpty()) {
			gstr1aB2csBusinessProcess.processBusinessData(listOfB2cs,
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