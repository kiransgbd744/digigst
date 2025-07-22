package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1HeaderCheckService;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1InvoiceStructureService;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("InvoiceFileArrivalHandler")
public class InvoiceFileArrivalHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SRFileArrivalHandler.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("InvoiceHeaderCheckService")
	private Gstr1HeaderCheckService gstr1HeaderCheckService;

	@Autowired
	@Qualifier("Gstr1InvoiceStructureService")
	private Gstr1InvoiceStructureService gstr1InvoiceStructureService;

	private static final String CLASS_NAME = "InvoiceFileArrivalHandler";

	public void processInvoiceFile(Message message, AppExecContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_FILE_ARRIVAL_FILE_START", CLASS_NAME,
				"processInvoiceFile", null);
		if (LOGGER.isDebugEnabled()) {
			String fileArrivalMsg = String.format(
					"File Arrived - Message is: '%s'", message.getParamsJson());
			LOGGER.debug(fileArrivalMsg);
		}
		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		if(updateFileStatus == null) {
			String errMsg = String.format(
					"No Record available for the file Name %s", fileName);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			LOGGER.debug("openCmisSession " + openCmisSession);
			String docId = updateFileStatus.getDocId();
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = gstr1FileUploadUtil.getDocument(openCmisSession,
						fileName, fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			if (document == null) {
				LOGGER.debug("Document is not available in repo");
				throw new AppException("Document is not available in repo ");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name:{}", document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error("Exception occured in invoice File Arrival Processor",
					e);
		}

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(9);
		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();
		Pair<Boolean, String> checkHeaderFormat = gstr1HeaderCheckService
				.validate(getHeaders);

		List<Object[]> listOfInvoices = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		if (checkHeaderFormat.getValue0()) {
			gstr1InvoiceStructureService.invoiceStructrureProcessData(
					listOfInvoices, updateFileStatus);
		} else {
			LOGGER.error("invoice series Headers misMatch for the file id{}",
					updateFileStatus.getId());
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			gstr1FileStatusRepository.save(updateFileStatus);

		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INVOICE_SERIES_FILE_ARRIVAL_FILE_END", CLASS_NAME,
				"processInvoiceFile", null);
	}

	/**
	 * Validate the Input JSON to check if 'path', 'fileName' and Group Code are
	 * actually present within the JSON. If not, throw an EWB Exception so that
	 * the file processing status is marked as 'Failed'. Otherwise, extract
	 * these values and return them.
	 * 
	 * @param jsonMessage
	 *            The 'Message' instance passed by the AsyncExecution framework
	 * 
	 */
	private FileArrivalMsgDto extractAndValidateMessage(Message message) {
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance()
				.fromJson(message.getParamsJson(), FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}

		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.debug(logMsg);
		}
		return msg;
	}

}
