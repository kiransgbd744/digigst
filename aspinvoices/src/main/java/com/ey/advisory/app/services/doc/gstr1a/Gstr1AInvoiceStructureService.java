package com.ey.advisory.app.services.doc.gstr1a;

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
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredInvEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AAsEnteredInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1AsEnteredInvoiceRepository;
import com.ey.advisory.app.services.annexure1fileupload.Gstr1aVerticalWebUploadErrorService;
import com.ey.advisory.app.services.annexure1fileupload.VerticalWebUploadErrorService;
import com.ey.advisory.app.services.docs.SRFileToInvoiceDetailsConvertion;
import com.ey.advisory.app.services.strcutvalidation.InvoiceFile.Gstr1AInvoiceSeriesStructValidationChain;
import com.ey.advisory.app.services.strcutvalidation.InvoiceFile.InvoiceSeriesStructValidationChain;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AInvoiceStructureService")
@Slf4j
public class Gstr1AInvoiceStructureService {

	@Autowired
	@Qualifier("Gstr1AAsEnteredInvoiceRepository")
	private Gstr1AAsEnteredInvoiceRepository gstr1AsEnteredInvoiceRepository;
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("SRFileToGstr1AInvoiceDetailsConvertion")
	private SRFileToGstr1AInvoiceDetailsConvertion sRFileToInvoiceDetailsConvertion;

	@Autowired
	@Qualifier("Gstr1AInvoiceSeriesStructValidationChain")
	private Gstr1AInvoiceSeriesStructValidationChain invoiceSeriesStructValidationChain;

	@Autowired
	@Qualifier("Gstr1aVerticalWebUploadErrorService")
	private Gstr1aVerticalWebUploadErrorService verticalWebUploadErrorService;

	@Autowired
	@Qualifier("Gstr1AInvoiceBusinessService")
	private Gstr1AInvoiceBusinessService gstr1InvoiceBusinessService;

	private static final String CLASS_NAME = "Gstr1InvoiceStructureService";

	@Transactional(value = "clientTransactionManager")
	public void invoiceStructrureProcessData(List<Object[]> listOfInvoices,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr1AAsEnteredInvEntity> strErrRecords = new ArrayList<>();
		List<Gstr1AAsEnteredInvEntity> strProcessRecords = new ArrayList<>();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		LOGGER.debug("Enter into Structural process data method");
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SAVING DATA_TO_ASENTERED_DTO_CONVERTION_START",
				CLASS_NAME, "invoiceStructrureProcessData", null);
		List<Gstr1AAsEnteredInvEntity> invoiceDoc = sRFileToInvoiceDetailsConvertion
				.convertSRFileToExcelInvoiceDoc(listOfInvoices,
						updateFileStatus);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SAVING DATA_TO_ASENTERED_DTO_CONVERTION_END",
				CLASS_NAME, "invoiceStructrureProcessData", null);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SAVING DATA_TO_ASENTERED_DB_SAVING_START",
				CLASS_NAME, "invoiceStructrureProcessData", null);

		List<Gstr1AAsEnteredInvEntity> excelDataSave = gstr1AsEnteredInvoiceRepository
				.saveAll(invoiceDoc);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SAVING DATA_TO_ASENTERED_DB_SAVING_END",
				CLASS_NAME, "invoiceStructrureProcessData", null);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SAVING STRUCTURAL VALIDATION_START", CLASS_NAME,
				"invoiceStructrureProcessData", null);
		Map<String, List<ProcessingResult>> processingResults = invoiceSeriesStructValidationChain
				.validation(listOfInvoices, excelDataSave);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SAVING STRUCTURAL VALIDATION_END", CLASS_NAME,
				"invoiceStructrureProcessData", null);
		List<String> strErrorKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			String errkey = keys.substring(0, keys.lastIndexOf('-'));
			strErrorKeys.add(errkey);
		}
		LOGGER.debug("Structural Validations Keys" + strErrorKeys);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SEPERATING_ERROR_AND_PROCESS RECORDS_START",
				CLASS_NAME, "invoiceStructrureProcessData", null);
		for (Gstr1AAsEnteredInvEntity id : excelDataSave) {
			String invKey = id.getInvoiceKey();
			if (!strErrorKeys.contains(invKey)) {
				strProcessRecords.add(id);
			} else {
				id.setError(true);
				strErrRecords.add(id);
			}
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_SEPERATING_ERROR_AND_PROCESS RECORDS_END",
				CLASS_NAME, "invoiceStructrureProcessData", null);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("strucErrorRecords size{} for fileId:{} ",
					strErrRecords.size(), updateFileStatus.getId());
			LOGGER.debug("strucProcessRecords size {} for fileId:{}",
					strProcessRecords.size(), updateFileStatus.getId());
		}
		if (!strErrRecords.isEmpty()) {
			// Keep the list of errors ready.
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ERROR MAP_START", CLASS_NAME,
					"invoiceStructrureProcessData", null);
			Map<String, List<Gstr1AAnn1VerticalWebError>> errorMap = verticalWebUploadErrorService
					.convertErrors(processingResults, STRUCTURAL_VALIDATIONS,
							updateFileStatus);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERIES_ERROR MAP_END", CLASS_NAME,
					"invoiceStructrureProcessData", null);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SAVING TO ERROR TABLE_START", CLASS_NAME,
					"invoiceStructrureProcessData", null);
			verticalWebUploadErrorService
					.storedErrorGstr1InvRecords(strErrRecords, errorMap);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SAVING TO ERROR TABLE_END", CLASS_NAME,
					"invoiceStructrureProcessData", null);

		}

		if (!strProcessRecords.isEmpty()) {
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_PROCESS BUSINESS DATA_START", CLASS_NAME,
					"invoiceStructrureProcessData", null);
			gstr1InvoiceBusinessService.processBusinessData(listOfInvoices,
					strProcessRecords, strErrRecords, updateFileStatus);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_PROCESS BUSINESS DATA_END", CLASS_NAME,
					"invoiceStructrureProcessData", null);
		} else {
			totalRecords = (excelDataSave.size() != 0) ? excelDataSave.size()
					: 0;
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