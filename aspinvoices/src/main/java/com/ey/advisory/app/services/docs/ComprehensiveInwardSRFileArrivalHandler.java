package com.ey.advisory.app.services.docs;

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
import com.ey.advisory.app.asb.service.ASBProducerCommonService;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.services.docs.gstr2.Anx2RawFileToInwardTransDocConvertion;
import com.ey.advisory.app.services.docs.gstr2.ComprehensiveFileToInwardTransDocErrorConvertion;
import com.ey.advisory.app.services.docs.gstr2.ComprehensiveInwardDocumentKeyBuilder;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardDocSave240Service;
import com.ey.advisory.app.services.docs.gstr2.InwardDocumentKeyBuilder;
import com.ey.advisory.app.services.gstr2fileupload.Gstr2HeaderCheckService;
import com.ey.advisory.app.services.strcutvalidation.inward.InwardComprehensiveStructuralChain;
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
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("ComprehensiveInwardSRFileArrivalHandler")
@Slf4j
public class ComprehensiveInwardSRFileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Anx2RawFileToInwardTransDocConvertion")
	private Anx2RawFileToInwardTransDocConvertion anx2RawFileToInwardTransDocConvertion;

	@Autowired
	@Qualifier("DefaultInwardDocSave240Service")
	private DefaultInwardDocSave240Service defaultInwardDocSaveService;

	@Autowired
	@Qualifier("InwardComprehensiveStructuralChain")
	private InwardComprehensiveStructuralChain anx1InwardDocStructuralValidatorChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("InwardRawFileHeaderCheckService")
	private Gstr2HeaderCheckService gstr2HeaderCheckService;

	@Autowired
	@Qualifier("ComprehensiveFileToInwardTransDocErrorConvertion")
	private ComprehensiveFileToInwardTransDocErrorConvertion comprehensiveFileToInwardTransDocErrorConvertion;

	@Autowired
	@Qualifier("DefaultInwardDocErrorSaveService")
	private DefaultInwardDocErrorSaveService defaultInwardDocErrorSaveService;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;

	/*@Autowired
	private ASBProducerCommonService asbProducerService;
*/
	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";

	private static final String CLASS_NAME = "Anx2InwardRawFileArrivalHandler";
	private static final String METHOD_NAME = "processAnx2InwardRawFile";

	/**
	 * 
	 * @param message
	 * @param context
	 */

	@Transactional(value = "clientTransactionManager")
	public void processInwardSRFile(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("PRFileArrivalHandler processPRFile Begining");
		}

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(fileArrivalMsg);
		}

		InwardDocumentKeyBuilder docKeyBuilder = new InwardDocumentKeyBuilder();

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String userName = message.getUserName();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		FileStatusPerfUtil.setFileId(updateFileStatus.getId());
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession:{}", openCmisSession);
			}

			String docId = updateFileStatus.getDocId();
			Document document = null;

			if (Strings.isNullOrEmpty(docId)) {
				document = gstr1FileUploadUtil.getDocument(openCmisSession,
						fileName, fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			if (document == null)

			{
				LOGGER.debug("Document is not available in repo");
				throw new AppException("Document is not available in repo ");

			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name:{}", document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error("Exception occured in PRFileArrival Processor", e);
			throw new AppException(e);
		}

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(240);

		// Add a row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		ComprehensiveInwardTransDocRowHandler rowHandler = new ComprehensiveInwardTransDocRowHandler<String>(
				new ComprehensiveInwardDocumentKeyBuilder());

		FileStatusPerfUtil.setFileId(updateFileStatus.getId());
		// store the time TRAVERSE_BEGIN
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"TRAVERSE_START", CLASS_NAME, METHOD_NAME,
				String.valueOf(updateFileStatus.getId()));
		traverser.traverse(inputStream, layout, rowHandler, null);
		// store the time TRAVERSE_END
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"TRAVERSE_END", CLASS_NAME, METHOD_NAME,
				String.valueOf(updateFileStatus.getId()));

		Object[] getHeaders = rowHandler.getHeaderData();
		Pair<Boolean, String> checkHeaderFormat = gstr2HeaderCheckService
				.validate(getHeaders);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Header Checker Validations "
					+ checkHeaderFormat.getValue0() + " ,"
					+ checkHeaderFormat.getValue1());
		}

		Integer totalRecords = 0;
		Integer processedRecords = 0;
		Integer errorRecords = 0;
		Integer information = 0;

		if (checkHeaderFormat.getValue0()) {

			// store the time MAP_CREATION_BEGIN
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"MAP_CREATION_START", CLASS_NAME, METHOD_NAME, null);
			Map<String, List<Object[]>> documentMap = ((ComprehensiveInwardTransDocRowHandler<?>) rowHandler)
					.getDocumentMap();

			// store the time MAP_CREATION_END
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"MAP_CREATION_END", CLASS_NAME, METHOD_NAME, null);

			if (LOGGER.isDebugEnabled()) {

				if (!documentMap.isEmpty()) {
					LOGGER.debug("Document Map  Length {}", documentMap.size());
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Document Map:{}", documentMap);
				}
			}
			// Structural Validations
			// store the time STRUCT_VALIDATION_BEGIN
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCT_VALIDATION_START", CLASS_NAME, METHOD_NAME, null);
			Map<String, List<ProcessingResult>> processingResults = anx1InwardDocStructuralValidatorChain
					.validation(documentMap);
			// store time STRUCT_VALIDATION_END
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCT_VALIDATION_END", CLASS_NAME, METHOD_NAME, null);
			List<String> listKeys = new ArrayList<>();
			for (String keys : processingResults.keySet()) {
				listKeys.add(keys);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("structural Validations {} keys {}",
						processingResults.values(), processingResults.keySet());
			}
			if (!processingResults.isEmpty()) {
				Map<String, List<Object[]>> documentMapObj = new HashMap<>();
				Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
				for (String keys : documentMap.keySet()) {
					if (!listKeys.contains(keys)) {
						List<Object[]> list = documentMap.get(keys);
						documentMapObj.put(keys, list);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Anx2InwardRawFileArrivalHandler processAnx2InwardRawFile documentMap.keySet Begining");
						}
					} else {
						List<Object[]> list = documentMap.get(keys);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.info(
									"Anx2InwardRawFileArrivalHandler processAnx2InwardRawFile errDocMapObj.keySet Begining");
						}
						errDocMapObj.put(keys, list);
					}
				}

				// To save the structural validation for errors stored to
				// inwardRaw table

				try {
					saveErrDocAndDoc(docKeyBuilder, documentMap,
							updateFileStatus, processingResults, documentMapObj,
							errDocMapObj, userName);
				} catch (Exception e) {
					LOGGER.error("Error Occured:{} ", e);
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
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"PRFileArrivalHandler processPRFile Processor Begining");
				}
				try {
					saveDoc(docKeyBuilder, documentMap, updateFileStatus,
							processingResults, userName);

				} catch (Exception e) {
					LOGGER.error(EXCEPTION_APP);
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
							"PRFileArrivalHandler processPRFile Processor End");
				}
			}
		} else

		{
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"PRFileArrivalHandler processPRFile Failure Beging");
			}
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			updateFileStatus.setErrorDesc("Headers Mismatch");
			gstr1FileStatusRepository.save(updateFileStatus);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PRFileArrivalHandler processPRFile Failure End");
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("PRFileArrivalHandler processPRFile end");
		}
	}

	@Transactional(value = "clientTransactionManager")
	private void saveDoc(InwardDocumentKeyBuilder gstr2documentKeyBuilder,
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			String userName) {
		Integer totalRecords;
		Integer processedRecords;
		Integer errorRecords;
		Integer information;
		Integer businessErrorCount;
		try {
			// store the time SAVE_DOCS_BEGIN
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DOCS_START", CLASS_NAME, "saveDoc", null);
			List<InwardTransDocument> documents = anx2RawFileToInwardTransDocConvertion
					.convertAnx2RawFileToInwardTransDoc(documentMap,
							updateFileStatus, userName);
			defaultInwardDocSaveService.saveDocuments(documents, null, null);
			// store the time SAVE_DOCS_END
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DOCS_END", CLASS_NAME, "saveDoc", null);

			businessErrorCount = inwardTransDocRepository
					.businessValidationCount(updateFileStatus.getId());

			totalRecords = documents.size();
			errorRecords = processingResults.size() + businessErrorCount;
			processedRecords = totalRecords - errorRecords;
			information = 0;

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);

			gstr1FileStatusRepository.save(updateFileStatus);

	/*	asbProducerService.produceMessage("SAP_FILE_STATUS",
					"DTLDSAPFILESTATUS", "DigiSAP", TenantContext.getTenantId(),
					updateFileStatus.getId(), null);
			asbProducerService.produceMessage("SAP_PR_DOC", "DTLDSAPPRDOC",
					"DigiSAP", TenantContext.getTenantId(),
					updateFileStatus.getId(), null);
			asbProducerService.produceMessage("SAP_PR_ERROR", "DTLDSAPPRERROR",
					"DigiSAP", TenantContext.getTenantId(),
					updateFileStatus.getId(), null);*/

		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
	}

	@Transactional(value = "clientTransactionManager")
	private void saveErrDocAndDoc(
			InwardDocumentKeyBuilder gstr2documentKeyBuilder,
			Map<String, List<Object[]>> documentMap,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMapObj,
			Map<String, List<Object[]>> errDocMapObj, String userName) {
		Integer totalRecords;
		Integer processedRecords;
		Integer errorRecords;
		Integer information;
		Integer businessErrorCount;
		try {
			if (!errDocMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Anx2InwardRawFileArrivalHandler processAnx2InwardRawFile errDocMapObj Begining");
				}
				List<Anx2InwardErrorHeaderEntity> errorDocument = comprehensiveFileToInwardTransDocErrorConvertion
						.fileToInwardTransDocErrorCon(errDocMapObj,
								updateFileStatus, userName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Anx2InwardRawFileArrivalHandler processAnx2InwardRawFile errDocMapObj befor saving Error");
				}
				// store the time STRUCT_VALIDATION_ERR_SAVE_BEGIN
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"STRUCT_VALIDATION_ERR_SAVE_START", CLASS_NAME,
						"saveErrDocAndDoc", null);
				defaultInwardDocErrorSaveService
						.saveErrorRecord(processingResults, errorDocument);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info(
							"Anx2InwardRawFileArrivalHandler processAnx2InwardRawFile errDocMapObj End");
				}
				// store the time STRUCT_VALIDATION_ERR_SAVE_END
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"STRUCT_VALIDATION_ERR_SAVE_END", CLASS_NAME,
						"saveErrDocAndDoc", null);
			}

			if (!documentMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Anx2InwardRawFileArrivalHandler "
							+ "processAnx2InwardRawFile documentMapObj Begining");
				}
				List<InwardTransDocument> documents = anx2RawFileToInwardTransDocConvertion
						.convertAnx2RawFileToInwardTransDoc(documentMapObj,
								updateFileStatus, userName);
				defaultInwardDocSaveService.saveDocuments(documents, null,
						null);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Anx2InwardRawFileArrivalHandler "
							+ "processAnx2InwardRawFile documentMapObj End");
				}
			}
			// store time UPDATE_FILE_STATUS_BEGIN
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"UPDATE_FIL_STATUS_START", CLASS_NAME, "saveErrDocAndDoc",
					null);
			businessErrorCount = inwardTransDocRepository
					.businessValidationCount(updateFileStatus.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("businessErrorCount {}", businessErrorCount);
			}
			totalRecords = documentMap.size();
			errorRecords = processingResults.size() + businessErrorCount;
			processedRecords = totalRecords - errorRecords;
			information = 0;

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);

			gstr1FileStatusRepository.save(updateFileStatus);
			
			/*asbProducerService.produceMessage("SAP_FILE_STATUS",
					"DTLDSAPFILESTATUS", "DigiSAP", TenantContext.getTenantId(),
					updateFileStatus.getId(), null);
			asbProducerService.produceMessage("SAP_PR_DOC", "DTLDSAPPRDOC",
					"DigiSAP", TenantContext.getTenantId(),
					updateFileStatus.getId(), null);
			asbProducerService.produceMessage("SAP_PR_ERROR", "DTLDSAPPRERROR",
					"DigiSAP", TenantContext.getTenantId(),
					updateFileStatus.getId(), null);*/
			
			
			// store time UPDATE_FILE_STATUS_END
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"UPDATE_FIL_STATUS_END", CLASS_NAME, "saveErrDocAndDoc",
					null);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error(String.format("%s %s", msg, logMsg));
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