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
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.data.repositories.client.Gstr1ATAsEnteredRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService;
import com.ey.advisory.app.services.docs.SRFileToATExcelDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.advanceReceived.ARStructValidationChain;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1ARStructuralProcess")
@Slf4j
public class Gstr1ARStructuralProcess {
	@Autowired
	@Qualifier("ARStructValidationChain")
	private ARStructValidationChain arStructValidationChain;

	@Autowired
	@Qualifier("Gstr1ARBusinessProcess")
	private Gstr1ARBusinessProcess gstr1ARBusinessProcess;

	@Autowired
	@Qualifier("SRFileToATExcelDetailsConvertion")
	private SRFileToATExcelDetailsConvertion sRFileToATExcelDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1ATAsEnteredRepository")
	private Gstr1ATAsEnteredRepository gstr1ATAsEnteredRepository;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService")
	private VerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Transactional(value = "clientTransactionManager")
	public void processData(List<Object[]> listOfAt,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr1AsEnteredAREntity> strErrRecords = new ArrayList<>();
		List<Gstr1AsEnteredAREntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		LOGGER.debug("Enter into Structural process data method");

		List<Gstr1AsEnteredAREntity> excelData = sRFileToATExcelDetailsConvertion
				.convertSRFileToATExcel(listOfAt, updateFileStatus);
		LOGGER.error("Starting Excel data Dumping process in to Table");
		List<Gstr1AsEnteredAREntity> excelDataSave = gstr1ATAsEnteredRepository
				.saveAll(excelData);
		LOGGER.error("Starting Excel data Dumping process in to Table");

		Map<String, List<ProcessingResult>> processingResults = arStructValidationChain
				.validation(listOfAt, excelDataSave);

		List<String> strErrorKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		LOGGER.debug("Structural Validations Keys" + strErrorKeys);

		for (Gstr1AsEnteredAREntity id : excelDataSave) {
			String atInvKey = id.getInvAtKey();
			if (!strErrorKeys.contains(atInvKey)) {
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
					.storedErrorGstr1AtRecords(strErrRecords, errorMap);

		}
		if (strProcessRecords.size() > 0 && !strProcessRecords.isEmpty()) {
			gstr1ARBusinessProcess.processBusinessData(listOfAt,
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