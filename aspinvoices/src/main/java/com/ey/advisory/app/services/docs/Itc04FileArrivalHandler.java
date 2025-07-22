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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04HeaderErrorsEntity;
import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.Annexure11HeaderCheckService;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.services.itc04.Itc04DefaultDocSaveService;
import com.ey.advisory.app.services.strcutvalidation.itc04.Itc04StructuralValidatorChain;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
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

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Itc04FileArrivalHandler")
@Slf4j
public class Itc04FileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Itc04HeaderCheckService")
	private Annexure11HeaderCheckService annexure11HeaderCheckService;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Itc04StructuralValidatorChain")
	private Itc04StructuralValidatorChain itc04StructuralValidatorChain;

	@Autowired
	@Qualifier("SRFileToItc04TransDocErrConvertion")
	private SRFileToItc04TransDocErrConvertion sRFileToItc04TransDocErrConvertion;

	@Autowired
	@Qualifier("SRFileToItc04TransDocConvertion")
	private SRFileToItc04TransDocConvertion sRFileToItc04TransDocConvertion;

	@Autowired
	@Qualifier("Itc04DefaultDocErrorSaveService")
	private Itc04DefaultDocErrorSaveService itc04DefaultDocErrorSaveService;

	@Autowired
	@Qualifier("Itc04DefaultDocSaveService")
	private Itc04DefaultDocSaveService itc04DefaultDocSaveService;

	@Autowired
	@Qualifier("Itc04DocRepository")
	private Itc04DocRepository itc04DocRepository;

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";

	private static final String CLASS_NAME = "Itc04FileArrivalHandler";
	private static final String METHOD_NAME = "processItc04File";


	@Transactional(value = "clientTransactionManager")
	public void processItc04File(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("processItc04File");
		String tenantCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Tenant Id Is {}", tenantCode);
		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(fileArrivalMsg);
		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String userName = message.getUserName();

		InputStream inputStream = null;

		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		
		try {
			Session openCmisSession = outwardFileUploadUtil.openCmisSession();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("openCmisSession " + openCmisSession);
			Document document = outwardFileUploadUtil
					.getDocument(openCmisSession, fileName, fileFolder);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in ITC04 " + "File Arrival Processor",
					e);
		}
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(64);
		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")

		Itc04DocRowHandler rowHandler = new Itc04DocRowHandler<String>(
				new DocumentKeyBuilder());
		long stTime = System.currentTimeMillis();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Start Time " + stTime);
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"TRAVERSE_START", CLASS_NAME, METHOD_NAME,
				String.valueOf(updateFileStatus.getId()));
		
		traverser.traverse(inputStream, layout, rowHandler, null);
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"TRAVERSE_END", CLASS_NAME, METHOD_NAME,
				String.valueOf(updateFileStatus.getId()));

		Object[] getHeaders = rowHandler.getHeaderData();

		Pair<Boolean, String> checkHeaderFormat = annexure11HeaderCheckService
				.validate(getHeaders);

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Header Checker Validations "
					+ checkHeaderFormat.getValue0() + " ,"
					+ checkHeaderFormat.getValue1());

	
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("businessProcessedRecords ");

		if (checkHeaderFormat.getValue0()) {
			DocumentKeyBuilder documentKeyBuilder = new DocumentKeyBuilder();
			
			// store the time MAP_CREATION_BEGIN
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"MAP_CREATION_START", CLASS_NAME, METHOD_NAME, null);
			Map<String, List<Object[]>> documentMap = ((Itc04DocRowHandler<?>) rowHandler)
					.getDocumentMap();
			
			// store the time MAP_CREATION_END
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"MAP_CREATION_END", CLASS_NAME, METHOD_NAME, null);
			
			
			// store the time STRUCT_VALIDATION_BEGIN
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"STRUCT_VALIDATION_START", CLASS_NAME, METHOD_NAME, null);
			Map<String, List<ProcessingResult>> processingResults = itc04StructuralValidatorChain
					.validation(documentMap);
			
			// store time STRUCT_VALIDATION_END
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"STRUCT_VALIDATION_END", CLASS_NAME, METHOD_NAME, null);
						
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
							LOGGER.debug(
									"Itc04FileArrivalHandler processSRFile documentMap.keySet Begining");
						}
					} else {
						List<Object[]> list = documentMap.get(keys);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Itc04FileArrivalHandler processSRFile errDocMapObj.keySet Begining");
						}
						errDocMapObj.put(keys, list);
					}
				}

				// To save the structural validation for errors stored to
				// outwardRaw table
				try {

					saveErrDocAndDoc(documentKeyBuilder, documentMap,
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
							"Itc04FileArrivalHandler processSRFile Processor Begining");
				}
				try {
					saveDoc(documentKeyBuilder, documentMap, updateFileStatus,
							processingResults, userName);
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
							"Itc04FileArrivalHandler processSRFile Processor End");
				}
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Itc04FileArrivalHandler processSRFile Failure Beging");
			}
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			gstr1FileStatusRepository.save(updateFileStatus);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Itc04FileArrivalHandler processSRFile Failure End");
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Itc04FileArrivalHandler processSRFile end");
		}
	}

	private void saveDoc(DocumentKeyBuilder documentKeyBuilder,
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
			
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_ENTITY_CONVERTION_START", CLASS_NAME, "saveDoc",
					null);
			List<Itc04HeaderEntity> documents = sRFileToItc04TransDocConvertion
					.convertSRFileToItc04TransDocs(documentMap,
							documentKeyBuilder, updateFileStatus, userName);
			
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_ENTITY_CONVERTION_END", CLASS_NAME, "saveDoc",
					String.valueOf(documents.size()));

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_HANDLER_SAVE_DOCS_START", CLASS_NAME, "saveDoc",
					null);
			FileStatusPerfUtil.logEvent("SR_ARRIVAL_HANDLER_SAVE_DOCS_BEGIN");
			itc04DefaultDocSaveService.saveItc04Documents(documents);
			
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_HANDLER_SAVE_DOCS_END", CLASS_NAME, "saveDoc",
					null);
			businessErrorCount = itc04DocRepository
					.businessValidationCount(updateFileStatus.getId());
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
							"Itc04FileArrivalHandler processSRFile errDocMapObj Begining");
				}
				PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
						"ITC04_FILE_ERROR_ENTITY_CONVERTION_START", CLASS_NAME, METHOD_NAME,
						null);

				List<Itc04HeaderErrorsEntity> errorDocument = sRFileToItc04TransDocErrConvertion
						.convertSRFileToItc04TransError(errDocMapObj,
								documentKeyBuilder, updateFileStatus, userName);
				
				PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
						"ITC04_FILE_ERROR_ENTITY_CONVERTION_END", CLASS_NAME, METHOD_NAME,
						String.valueOf(errorDocument.size()));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Itc04FileArrivalHandler processSRFile errDocMapObj befor saving Error");
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_META_ERROR_SAVE_START",
						CLASS_NAME, "saveErrDocAndDoc", null);
				itc04DefaultDocErrorSaveService
						.saveErrorRecord(processingResults, errorDocument);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_META_ERROR_SAVE_END", CLASS_NAME,
						"saveErrDocAndDoc", null);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Itc04FileArrivalHandler processSRFile errDocMapObj End");
				}
			}

			if (!documentMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Itc04FileArrivalHandler processSRFile documentMapObj Begining");
				}
				List<Itc04HeaderEntity> documents = sRFileToItc04TransDocConvertion
						.convertSRFileToItc04TransDocs(documentMapObj,
								documentKeyBuilder, updateFileStatus, userName);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_SAVE_DOCS_START", CLASS_NAME,
						"saveErrDocAndDoc", String.valueOf(documents.size()));
				itc04DefaultDocSaveService.saveItc04Documents(documents);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_SAVE_DOCS_END", CLASS_NAME,
						"saveErrDocAndDoc", null);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Itc04FileArrivalHandler processSRFile documentMapObj End");
				}
			}
			
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"UPDATE_FIL_STATUS_START", CLASS_NAME, "saveErrDocAndDoc",
					null);
			businessErrorCount = itc04DocRepository
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