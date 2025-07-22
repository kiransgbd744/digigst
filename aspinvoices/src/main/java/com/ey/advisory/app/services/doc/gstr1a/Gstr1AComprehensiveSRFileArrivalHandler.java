package com.ey.advisory.app.services.doc.gstr1a;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnx1OutWardErrHeader;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.docs.ComprehenceTransDocRowHandler;
import com.ey.advisory.app.services.docs.DocumentKeyBuilder;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1HeaderCheckService;
import com.ey.advisory.app.services.strcutvalidation.einvoice.ComprehensiveStructuralChain;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Service("Gstr1AComprehensiveSRFileArrivalHandler")
@Slf4j
public class Gstr1AComprehensiveSRFileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("SRFileToGstr1AComprehensiveOutwardTransDocConvertion")
	private SRFileToGstr1AComprehensiveOutwardTransDocConvertion sRFileToComprehensiveTransDocConvertion;

	@Autowired
	@Qualifier("Gstr1AEInvoiceDefaultDocSaveService")
	private Gstr1AEInvoiceDocSaveService srFileSaveService;

	@Autowired
	@Qualifier("ComprehensiveStructuralChain")
	private ComprehensiveStructuralChain comprehensiveStructuralChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("EInvoiceSRFileHeaderCheckService")
	private Gstr1HeaderCheckService gstr1HeaderCheckService;

	@Autowired
	@Qualifier("SRFileToGstr1AComprehensiveOutwardTransDocErrConvertion")
	private SRFileToGstr1AComprehensiveOutwardTransDocErrConvertion srFileToOutwardTransDocErrConvertion;

	@Autowired
	@Qualifier("DefaultGstr1ADocErrorSaveService")
	private DefaultGstr1ADocErrorSaveService defaultDocErrorSaveService;

	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	private DocRepositoryGstr1A docRepository;

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";

	private static final String CLASS_NAME = "ComprehensiveSRFileArrivalHandler";

	/**
	 * 
	 * @param message
	 * @param context
	 */
	@Transactional(value = "clientTransactionManager")
	public void processEInvoiceSRFile(Message message, AppExecContext context,
			List<AsyncExecJob> jobList) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"COMPRENSIVE_SR_FILE_ARRIVAL_FILE_START", CLASS_NAME,
				"processEInvoiceSRFile", null);

		if (LOGGER.isDebugEnabled()) {
			String fileArrivalMsg = String.format(
					"SR File Arrived - Message is: '%s'",
					message.getParamsJson());
			LOGGER.debug(fileArrivalMsg);
		}

		DocumentKeyBuilder documentKeyBuilder = new DocumentKeyBuilder();

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();

		String userName = message.getUserName();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		if (updateFileStatus == null) {
			String errMsg = String.format(
					"No Record available for the file Name %s", fileName);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
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
			LOGGER.error("Exception occured in SR File Arrival Processor", e);
			throw new AppException(EXCEPTION_APP, e);
		}
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(239);

		// Add a dummy row handler that will keep counting the rows.
		ComprehenceTransDocRowHandler rowHandler = new ComprehenceTransDocRowHandler<String>(
				new DocumentKeyBuilder());

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"TRAVERSE_START", CLASS_NAME, "processEInvoiceSRFile",
				String.valueOf(updateFileStatus.getId()));
		traverser.traverse(inputStream, layout, rowHandler, null);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"TRAVERSE_END", CLASS_NAME, "processEInvoiceSRFile", null);

		Object[] getHeaders = rowHandler.getHeaderData();
		Pair<Boolean, String> checkHeaderFormat = null;
		if (getHeaders != null) {
			checkHeaderFormat = gstr1HeaderCheckService.validate(getHeaders);
		}
		Integer totalRecords = 0;
		Integer processedRecords = 0;
		Integer errorRecords = 0;
		Integer information = 0;
		if (checkHeaderFormat.getValue0()) {
			Map<String, List<Object[]>> documentMap = ((ComprehenceTransDocRowHandler<?>) rowHandler)
					.getDocumentMap();
			if (!documentMap.isEmpty()) {
				LOGGER.debug(
						"No of unique invoices found:{} for outward 239 file id:{}, groupCode:{} ",
						documentMap.size(), updateFileStatus.getId(),
						TenantContext.getTenantId());
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_VALIDATIONS_START", CLASS_NAME,
					"processEInvoiceSRFile", null);
			Map<String, List<ProcessingResult>> processingResults = comprehensiveStructuralChain
					.validation(documentMap);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_VALIDATIONS_END", CLASS_NAME,
					"processEInvoiceSRFile", null);
			List<String> listKeys = new ArrayList<>();
			for (String keys : processingResults.keySet()) {
				listKeys.add(keys);
			}
			if (!processingResults.isEmpty()) {
				Map<String, List<Object[]>> documentMapObj = new HashMap<>();
				Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
				for (String keys : documentMap.keySet()) {
					if (!listKeys.contains(keys)) {
						List<Object[]> list = documentMap.get(keys);
						documentMapObj.put(keys, list);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("SRFileArrivalHandler processSRFile "
									+ "documentMap.keySet Begining");
						}
					} else {
						List<Object[]> list = documentMap.get(keys);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("SRFileArrivalHandler processSRFile "
									+ "errDocMapObj.keySet Begining");
						}
						errDocMapObj.put(keys, list);
					}
				}

				// To save the structural validation for errors stored to
				// outwardRaw table
				try {
					
					if(LOGGER.isDebugEnabled())
					{
						LOGGER.debug("errDocMapObj {}  ",errDocMapObj);
					}
					saveErrDocAndDoc(documentKeyBuilder, documentMap,
							updateFileStatus, processingResults, documentMapObj,
							errDocMapObj, userName, jobList);
				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
					updateFileStatus.setTotal(totalRecords);
					updateFileStatus.setProcessed(0);
					updateFileStatus.setError(0);
					updateFileStatus.setInformation(0);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					gstr1FileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP, e);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SRFileArrivalHandler processSRFile Processor "
							+ "Begining");
				}
				try {
					saveDoc(documentKeyBuilder, documentMap, updateFileStatus,
							processingResults, userName, jobList);
				} catch (Exception e) {
					LOGGER.error(EXCEPTIONS, e);
					updateFileStatus.setTotal(totalRecords);
					updateFileStatus.setProcessed(0);
					updateFileStatus.setError(0);
					updateFileStatus.setInformation(0);
					updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
					gstr1FileStatusRepository.save(updateFileStatus);
					throw new AppException(EXCEPTION_APP);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("DONE!!");
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"SRFileArrivalHandler processSRFile Processor End");
				}
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"SRFileArrivalHandler processSRFile Failure Beging");
			}
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			updateFileStatus.setErrorDesc("Headers Mismatch");
			gstr1FileStatusRepository.save(updateFileStatus);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SRFileArrivalHandler processSRFile Failure End");
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SRFileArrivalHandler processSRFile end");
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"COMPRENSIVE_SR_FILE_ARRIVAL_FILE_END",
				"ComprehensiveSRFileArrivalHandler", "processEInvoiceSRFile",
				null);
	}

	private void saveDoc(DocumentKeyBuilder documentKeyBuilder,
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			String userName, List<AsyncExecJob> jobList) {
		Integer totalRecords;
		Integer processedRecords;
		Integer errorRecords;
		Integer information;
		Integer businessErrorCount;
		try {
			List<Gstr1AOutwardTransDocument> documents = sRFileToComprehensiveTransDocConvertion
					.convertSRFileToOutwardTransDoc(documentMap,
							documentKeyBuilder, updateFileStatus, userName);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_HANDLER_SAVE_DOCS_START", CLASS_NAME,
					"processEInvoiceSRFile", null);
			srFileSaveService.saveDocuments(documents, null, null, jobList);

			businessErrorCount = docRepository
					.businessValidationCount(updateFileStatus.getId());
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_HANDLER_SAVE_DOCS_END", CLASS_NAME,
					"processEInvoiceSRFile", null);
			totalRecords = documents.size();
			errorRecords = processingResults.size() + businessErrorCount;
			processedRecords = totalRecords - errorRecords;
			information = 0;

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);

			gstr1FileStatusRepository.save(updateFileStatus);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
	}

	@Transactional(value = "clientTransactionManager")
	private void saveErrDocAndDoc(DocumentKeyBuilder documentKeyBuilder,
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMapObj,
			Map<String, List<Object[]>> errDocMapObj, String userName,
			List<AsyncExecJob> jobList) {
		Integer totalRecords;
		Integer processedRecords;
		Integer errorRecords;
		Integer information;
		Integer businessErrorCount;
		try {
			if (!errDocMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SRFileArrivalHandler processSRFile "
							+ "errDocMapObj Begining");
				}
				List<Gstr1AAnx1OutWardErrHeader> errorDocument = srFileToOutwardTransDocErrConvertion
						.convertSRFileToOutWardTransError(errDocMapObj,
								documentKeyBuilder, updateFileStatus, userName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"SRFileArrivalHandler processSRFile errDocMapObj"
									+ " befor saving Error");
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_META_ERROR_SAVE_START",
						CLASS_NAME, "processEInvoiceSRFile", null);
				defaultDocErrorSaveService.saveErrorRecord(processingResults,
						errorDocument);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_META_ERROR_SAVE_END", CLASS_NAME,
						"processEInvoiceSRFile", null);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SRFileArrivalHandler processSRFile "
							+ "errDocMapObj End");
				}
			}

			if (!documentMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"SRFileArrivalHandler processSRFile documentMapObj "
									+ "Begining");
				}
				List<Gstr1AOutwardTransDocument> documents = sRFileToComprehensiveTransDocConvertion
						.convertSRFileToOutwardTransDoc(documentMapObj,
								documentKeyBuilder, updateFileStatus, userName);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_SAVE_DOCS_START", CLASS_NAME,
						"processEInvoiceSRFile", null);
				srFileSaveService.saveDocuments(documents, null, null, jobList);

				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_SAVE_DOCS_END", CLASS_NAME,
						"processEInvoiceSRFile", null);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"SRFileArrivalHandler processSRFile documentMapObj "
									+ "End");
				}
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_FILESTATUS_SAVE_START", CLASS_NAME,
					"processEInvoiceSRFile", null);

			businessErrorCount = docRepository
					.businessValidationCount(updateFileStatus.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("businessErrorCount: {}", businessErrorCount);
			}
			totalRecords = documentMap.size();
			errorRecords = processingResults.size() + businessErrorCount;
			processedRecords = totalRecords - errorRecords;
			information = 0;
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			gstr1FileStatusRepository.save(updateFileStatus);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_FILESTATUS_SAVE_END", CLASS_NAME,
					"processEInvoiceSRFile", null);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP, e);
		}
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
	public FileArrivalMsgDto extractAndValidateMessage(Message message) {
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance()
				.fromJson(message.getParamsJson(), FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("%s %s :{}", msg, logMsg));
			}
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