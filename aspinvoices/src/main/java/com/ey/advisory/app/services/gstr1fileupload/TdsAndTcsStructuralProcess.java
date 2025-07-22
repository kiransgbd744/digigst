package com.ey.advisory.app.services.gstr1fileupload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.Gstr2XExcelTcsTdsRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCSAndTCSADetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTDSAndTDSADetailsAtGstnRepository;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService2;
import com.ey.advisory.app.services.docs.SRFileToTcsTdsConvertion;
import com.ey.advisory.app.services.strcutvalidation.tcstds.TcsTdsStructValidationChain;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("TdsAndTcsStructuralProcess")
@Slf4j
public class TdsAndTcsStructuralProcess {

	@Autowired
	@Qualifier("TdsTcsBusinessProcess")
	private TdsTcsBusinessProcess tdsTcsBusinessProcess;

	@Autowired
	@Qualifier("VerticalWebUploadErrorService2")
	private VerticalWebUploadErrorService2 verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr2XExcelTcsTdsRepository")
	private Gstr2XExcelTcsTdsRepository gstr2XExcelTcsTdsRepository;

	@Autowired
	@Qualifier("SRFileToTcsTdsConvertion")
	private SRFileToTcsTdsConvertion sRFileToTcsTdsConvertion;

	@Autowired
	@Qualifier("TcsTdsStructValidationChain")
	private TcsTdsStructValidationChain tcsTdsStructValidationChain;

	@Autowired
	@Qualifier("Gstr2xGetTCSAndTCSADetailsAtGstnRepository")
	private Gstr2xGetTCSAndTCSADetailsAtGstnRepository gstr2xTcsTcsa;

	@Autowired
	@Qualifier("Gstr2xGetTDSAndTDSADetailsAtGstnRepository")
	private Gstr2xGetTDSAndTDSADetailsAtGstnRepository gstr2xTdsTdsa;

	@Transactional(value = "clientTransactionManager")
	public void tcsTdsStructureProcessData(List<Object[]> listOfTcsTds,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr2XExcelTcsTdsEntity> strErrRecords = new ArrayList<>();
		List<Gstr2XExcelTcsTdsEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Enter into Structural process data method");
		}
		List<Gstr2XExcelTcsTdsEntity> excelData = sRFileToTcsTdsConvertion
				.convertSRFileToTcsTdsExcelDocOld(listOfTcsTds,
						updateFileStatus);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting Excel data Dumping process in to Table");
		}

		List<Gstr2XExcelTcsTdsEntity> excelDataSave = gstr2XExcelTcsTdsRepository
				.saveAll(excelData);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting Excel data Dumping process in to Table");
		}

		Map<String, List<ProcessingResult>> processingResults = tcsTdsStructValidationChain
				.validation(listOfTcsTds, excelDataSave);

		List<String> strErrorKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Structural Validations Keys" + strErrorKeys);
		}

		for (Gstr2XExcelTcsTdsEntity tcsTdsRecords : excelDataSave) {
			String tcsTdsKey = tcsTdsRecords.getDocKey();
			if (!strErrorKeys.contains(tcsTdsKey)) {
				strProcessRecords.add(tcsTdsRecords);
			} else {
				tcsTdsRecords.setError(true);
				tcsTdsRecords.setDelete(false);
				strErrRecords.add(tcsTdsRecords);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.error("strucErrorRecords ", strErrRecords.size());
			LOGGER.error("strucProcessRecords ", strProcessRecords.size());
		}

		if (!strErrRecords.isEmpty()) {
			// Keep the list of errors ready.
			Map<String, List<VerticalWebErrorTable>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults,
							GSTConstants.STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			verticalWebUploadErrorService
					.storedErrorGstr2xRecords(strErrRecords, errorMap);

		}

		if (!strProcessRecords.isEmpty()) {
			tdsTcsBusinessProcess.processTdsTcsBusinessPData(strProcessRecords,
					strErrRecords, updateFileStatus);
		} else {
			totalRecords = (!excelData.isEmpty()) ? excelData.size() : 0;
			errorRecords = (!strErrRecords.isEmpty()) ? strErrRecords.size()
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
