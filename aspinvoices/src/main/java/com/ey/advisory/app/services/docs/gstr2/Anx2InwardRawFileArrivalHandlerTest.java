package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.services.docs.DefaultInwardDocErrorSaveService;
import com.ey.advisory.app.services.docs.InwardTransDocRowHandler;
import com.ey.advisory.app.services.gstr2fileupload.Gstr2HeaderCheckService;
import com.ey.advisory.app.services.strcutvalidation.inward.Anx1InwardDocStructuralValidatorChain;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("Anx2InwardRawFileArrivalHandlerTest")
public class Anx2InwardRawFileArrivalHandlerTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2InwardRawFileArrivalHandler.class);

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
	@Qualifier("DefaultInwardDocSaveService")
	private DefaultInwardDocSaveService defaultInwardDocSaveService;

	@Autowired
	@Qualifier("Anx1InwardDocStructuralValidatorChain")
	private Anx1InwardDocStructuralValidatorChain anx1InwardDocStructuralValidatorChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Anx2InwardRawFileHeaderCheckService")
	private Gstr2HeaderCheckService gstr2HeaderCheckService;

	@Autowired
	@Qualifier("Anx2RawFileToInwardTransDocErrorConvertion")
	private Anx2RawFileToInwardTransDocErrorConvertion anx2RawFileToInwardTransDocErrorConvertion;

	@Autowired
	@Qualifier("DefaultInwardDocErrorSaveService")
	private DefaultInwardDocErrorSaveService defaultInwardDocErrorSaveService;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";

	/**
	 * 
	 * @param message
	 * @param context
	 */

	@SuppressWarnings("rawtypes")
	public void processAnx2InwardRawFile(Message message,
			AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("PRFileArrivalHandler processPRFile Begining");
		}

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(fileArrivalMsg);
		}

		String userName = message.getUserName();
		InwardDocumentKeyBuilder docKeyBuilder = new InwardDocumentKeyBuilder();

		// Extract the File Arrival message from the serialized Job params
		// object.
		// FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = message.getUserName();
		String fileFolder = "C:/umesha/InwardRawFile/" + fileName;

		// InputStream inputStream = null;
		// try {
		// Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
		// LOGGER.debug("openCmisSession " + openCmisSession);
		// Document document = gstr1FileUploadUtil.getDocument(openCmisSession,
		// fileName, fileFolder);
		// LOGGER.debug("document name" + document.getName());
		// inputStream = document.getContentStream().getStream();
		// } catch (Exception e) {
		// LOGGER.error(
		// "Exception occured in Anx2InwardRawFileArrivl Processor",
		// e);
		// }

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(115);

		// Add a row handler that will keep counting the rows.
		InwardTransDocRowHandler rowHandler = new InwardTransDocRowHandler<String>(
				new InwardDocumentKeyBuilder());

		traverser.traverse(fileFolder, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderData();
		Pair<Boolean, String> checkHeaderFormat = gstr2HeaderCheckService
				.validate(getHeaders);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Header Checker Validations "
					+ checkHeaderFormat.getValue0() + " ,"
					+ checkHeaderFormat.getValue1());
		}

		Map<String, List<Object[]>> documentMap = ((InwardTransDocRowHandler<?>) rowHandler)
				.getDocumentMap();
		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			LOGGER.debug("key {}", key);
		});
		if (!documentMap.isEmpty()) {
			LOGGER.debug("Document Map  Length {}", documentMap.size());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Document Map:{}", documentMap);
		}
		// Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
		// .getFileName(fileName);

		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName("InwardRaw.xlsx");
		FileStatusPerfUtil.setFileId(updateFileStatus.getId());

		// Structural Validations
		Map<String, List<ProcessingResult>> processingResults = anx1InwardDocStructuralValidatorChain
				.validation(documentMap);

		List<String> listKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			listKeys.add(keys);
		}
		FileStatusPerfUtil.logEvent("TRAVERSE_BEGIN");
		LOGGER.info("structural Validations {} keys {}",
				processingResults.values(), processingResults.keySet());

		Integer totalRecords = 0;
		Integer processedRecords = 0;
		Integer errorRecords = 0;
		Integer information = 0;

		if (checkHeaderFormat.getValue0()) {

			if (!processingResults.isEmpty()) {
				Map<String, List<Object[]>> documentMapObj = new HashMap<>();
				Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
				for (String keys : documentMap.keySet()) {
					if (!listKeys.contains(keys)) {
						List<Object[]> list = documentMap.get(keys);
						documentMapObj.put(keys, list);
						LOGGER.info("Anx2InwardRawFileArrivalHandler "
								+ "processAnx2InwardRawFile "
								+ "documentMap.keySet Begining");
					} else {
						List<Object[]> list = documentMap.get(keys);
						LOGGER.info("Anx2InwardRawFileArrivalHandler "
								+ "processAnx2InwardRawFile "
								+ "errDocMapObj.keySet Begining");
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
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"PRFileArrivalHandler processPRFile Failure Beging");
			}
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			gstr1FileStatusRepository.save(updateFileStatus);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PRFileArrivalHandler processPRFile Failure End");
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("PRFileArrivalHandler processPRFile end");
		}
	}

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
			List<InwardTransDocument> documents = anx2RawFileToInwardTransDocConvertion
					.convertAnx2RawFileToInwardTransDoc(documentMap,
							updateFileStatus, userName);
			defaultInwardDocSaveService.saveDocuments(documents, null, null);

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
					LOGGER.info("Anx2InwardRawFileArrivalHandler "
							+ "processAnx2InwardRawFile errDocMapObj Begining");
				}
				List<Anx2InwardErrorHeaderEntity> errorDocument = anx2RawFileToInwardTransDocErrorConvertion
						.convertAnx2RawFileToInWardTransError(errDocMapObj,
								updateFileStatus, userName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Anx2InwardRawFileArrivalHandler "
							+ "processAnx2InwardRawFile errDocMapObj befor "
							+ "saving Error");
				}
				defaultInwardDocErrorSaveService
						.saveErrorRecord(processingResults, errorDocument);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Anx2InwardRawFileArrivalHandler "
							+ "processAnx2InwardRawFile errDocMapObj End");
				}
			}

			if (!documentMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Anx2InwardRawFileArrivalHandler "
							+ "processAnx2InwardRawFile documentMapObj Begining");
				}
				List<InwardTransDocument> documents = anx2RawFileToInwardTransDocConvertion
						.convertAnx2RawFileToInwardTransDoc(documentMapObj,
								updateFileStatus, userName);
				defaultInwardDocSaveService.saveDocuments(documents, null, null);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Anx2InwardRawFileArrivalHandler "
							+ "processAnx2InwardRawFile documentMapObj End");
				}
			}
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
