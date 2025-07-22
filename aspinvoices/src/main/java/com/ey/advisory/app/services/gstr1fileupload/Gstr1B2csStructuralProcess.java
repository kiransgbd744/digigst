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
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSAsEnteredRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService;
import com.ey.advisory.app.services.docs.SRFileToB2CSExelDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.b2cs.B2csStructValidationChain;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr1B2csStructuralProcess")
@Slf4j
public class Gstr1B2csStructuralProcess {

	@Autowired
	@Qualifier("B2csStructValidationChain")
	private B2csStructValidationChain b2csStructValidationChain;

	@Autowired
	@Qualifier("Gstr1B2csBusinessProcess")
	private Gstr1B2csBusinessProcess gstr1B2csBusinessProcess;

	@Autowired
	@Qualifier("SRFileToB2CSExelDetailsConvertion")
	private SRFileToB2CSExelDetailsConvertion sRFileToB2CSExelDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1B2CSAsEnteredRepository")
	private Gstr1B2CSAsEnteredRepository gstr1B2CSAsEnteredRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> listOfB2cs,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AsEnteredB2csEntity> strErrRecords = new ArrayList<>();
		List<Gstr1AsEnteredB2csEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		LOGGER.debug("Enter into Structural process data method");

		List<Gstr1AsEnteredB2csEntity> excelData = sRFileToB2CSExelDetailsConvertion
				.convertSRFileToB2csExcel(listOfB2cs, updateFileStatus);
		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<Gstr1AsEnteredB2csEntity> excelDataSave = gstr1B2CSAsEnteredRepository
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
		for (Gstr1AsEnteredB2csEntity id : excelDataSave) {
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
			Map<String, List<Ann1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedErrorGstr1B2csRecords(strErrRecords, errorMap);

		}
		if (strProcessRecords.size() > 0 && !strProcessRecords.isEmpty()) {
			gstr1B2csBusinessProcess.processBusinessData(listOfB2cs,
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