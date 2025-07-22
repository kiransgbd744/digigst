package com.ey.advisory.app.services.docs.gstr7;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrHeaderEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.repositories.client.gstr7trans.Gstr7TransDocHeaderRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.docs.DocumentKeyBuilder;
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
 * @author Siva.Reddy
 *
 */

@Service("Gstr7TransSRFileArrivalHandler")
@Slf4j
public class Gstr7TransSRFileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr7TransDocHeaderConvertion")
	private Gstr7TransDocHeaderConvertion gstr7TransDocConvertion;

	@Autowired
	@Qualifier("Gstr7TransDocSaveServiceImpl")
	private Gstr7TransDocSaveService srFileSaveService;

	@Autowired
	@Qualifier("Gstr7TransStructuralChain")
	private Gstr7TransStructuralChain gstr7TransStructuralChain;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr7TransHeaderCheckServiceImpl")
	private Gstr7TransHeaderCheckService gstr7HeaderCheckService;

	@Autowired
	@Qualifier("Gstr7TransDocErrDocConvertion")
	private Gstr7TransDocErrDocConvertion gstr7TransDocErrConvertion;

	@Autowired
	@Qualifier("Gstr7TransDocErrorSaveServiceImpl")
	private Gstr7TransDocErrorSaveService gstr7TransDocErrorSaveService;

	@Autowired
	@Qualifier("Gstr7TransDocHeaderRepository")
	private Gstr7TransDocHeaderRepository docRepository;

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured while GSTR7 Transactional File: {}";

	private static final String CLASS_NAME = "Gstr7TransSRFileArrivalHandler";

	/**
	 * 
	 * @param message
	 * @param context
	 */
	@Transactional(value = "clientTransactionManager")
	public void processEInvoiceSRFile(Message message, AppExecContext context,
			List<AsyncExecJob> jobList) {

		try {
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
				String docId = updateFileStatus.getDocId();
				Document document = null;
				if (Strings.isNullOrEmpty(docId)) {
					document = DocumentUtility.downloadDocument(fileName,
							fileFolder);
				} else {
					document = DocumentUtility.downloadDocumentByDocId(docId);
				}
				if (document == null) {
					LOGGER.debug("Document is not available in repo");
					throw new AppException(
							"Document is not available in repo ");
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("document name:{}", document.getName());
				}
				inputStream = document.getContentStream().getStream();

			} catch (Exception e) {
				LOGGER.error("Exception occured in SR File Arrival Processor",
						e);
				throw new AppException(EXCEPTION_APP, e);
			}
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(47);

			Gstr7TransDocRowHandler<String> rowHandler = new Gstr7TransDocRowHandler<String>(
					new Gstr7TransDocumentKeyBuilder());

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"TRAVERSE_START", CLASS_NAME, "processEInvoiceSRFile",
					String.valueOf(updateFileStatus.getId()));
			traverser.traverse(inputStream, layout, rowHandler, null);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"TRAVERSE_END", CLASS_NAME, "processEInvoiceSRFile", null);

			Object[] getHeaders = rowHandler.getHeaderData();
			Pair<Boolean, String> checkHeaderFormat = null;
			if (getHeaders != null) {
				checkHeaderFormat = gstr7HeaderCheckService
						.validate(getHeaders);
			}
			Integer totalRecords = 0;
			Integer processedRecords = 0;
			Integer errorRecords = 0;
			Integer information = 0;
			if (checkHeaderFormat.getValue0()) {
				Map<String, List<Object[]>> documentMap = ((Gstr7TransDocRowHandler<?>) rowHandler)
						.getDocumentMap();
				if (!documentMap.isEmpty()) {
					LOGGER.debug(
							"No of unique invoices found:{} for outward 239 file id:{}, groupCode:{} ",
							documentMap.size(), updateFileStatus.getId(),
							TenantContext.getTenantId());
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"STRUCTURAL_VALIDATIONS_START", CLASS_NAME,
						"processEInvoiceSRFile", null);
				// Structural validation
				Map<String, List<ProcessingResult>> processingResults = gstr7TransStructuralChain
						.validation(documentMap);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"STRUCTURAL_VALIDATIONS_END", CLASS_NAME,
						"processEInvoiceSRFile", null);
				List<String> listKeys = new ArrayList<>();
				for (String keys : processingResults.keySet()) {
					listKeys.add(keys);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"SRFileArrivalHandler processSRFile List Keys: "
									+ listKeys);
				}
				if (!processingResults.isEmpty()) {
					Map<String, List<Object[]>> documentMapObj = new HashMap<>();
					Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
					for (String keys : documentMap.keySet()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"SRFileArrivalHandler processSRFile keys: "
											+ keys);
						}
						if (!listKeys.contains(keys)) {
							List<Object[]> list = documentMap.get(keys);
							documentMapObj.put(keys, list);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"SRFileArrivalHandler processSRFile "
												+ "documentMap.keySet Begining");
							}
						} else {
							List<Object[]> list = documentMap.get(keys);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"SRFileArrivalHandler processSRFile "
												+ "errDocMapObj.keySet Begining");
							}
							errDocMapObj.put(keys, list);
						}
					}

					// To save the structural validation for errors stored to
					// outwardRaw table
					try {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"SRFileArrivalHandler processSRFile saveErrDocAndDoc() Executed.");
						}
						saveErrDocAndDoc(documentKeyBuilder, documentMap,
								updateFileStatus, processingResults,
								documentMapObj, errDocMapObj, userName,
								jobList);
					} catch (Exception e) {
						LOGGER.error("Error Occured:{} ", e);
						updateFileStatus.setTotal(totalRecords);
						updateFileStatus.setProcessed(0);
						updateFileStatus.setError(0);
						updateFileStatus.setInformation(0);
						updateFileStatus
								.setFileStatus(JobStatusConstants.FAILED);
						gstr1FileStatusRepository.save(updateFileStatus);
						throw new AppException(EXCEPTION_APP, e);
					}

				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"SRFileArrivalHandler processSRFile Processor "
										+ "Begining");
					}
					try {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"SRFileArrivalHandler processSRFile saveDoc() Executed.");
						}
						saveDoc(documentKeyBuilder, documentMap,
								updateFileStatus, processingResults, userName,
								jobList);
					} catch (Exception e) {
						LOGGER.error(EXCEPTIONS, e);
						updateFileStatus.setTotal(totalRecords);
						updateFileStatus.setProcessed(0);
						updateFileStatus.setError(0);
						updateFileStatus.setInformation(0);
						updateFileStatus
								.setFileStatus(JobStatusConstants.FAILED);
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
					LOGGER.debug(
							"SRFileArrivalHandler processSRFile Failure End");
				}

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SRFileArrivalHandler processSRFile end");
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"COMPRENSIVE_SR_FILE_ARRIVAL_FILE_END",
					"ComprehensiveSRFileArrivalHandler",
					"processEInvoiceSRFile", null);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
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
			List<Gstr7TransDocHeaderEntity> documents = gstr7TransDocConvertion
					.convertSRFileToOutwardTransDoc(documentMap,
							documentKeyBuilder, updateFileStatus, userName);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_HANDLER_SAVE_DOCS_START", CLASS_NAME,
					"processEInvoiceSRFile", null);
			srFileSaveService.saveDocuments(documents, null, null, jobList);

			businessErrorCount = docRepository
					.businessValidationCount(updateFileStatus.getId());
			processedRecords = docRepository
					.processedCount(updateFileStatus.getId());
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SR_ARRIVAL_HANDLER_SAVE_DOCS_END", CLASS_NAME,
					"processEInvoiceSRFile", null);
			totalRecords = documents.size();
			errorRecords = processingResults.size() + businessErrorCount;
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
				List<Gstr7TransDocErrHeaderEntity> errorDocument = gstr7TransDocErrConvertion
						.convertSRFileToGstr7TransError(errDocMapObj,
								updateFileStatus, userName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"SRFileArrivalHandler processSRFile errDocMapObj"
									+ " befor saving Error");
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"SR_ARRIVAL_STRUCTURAL_META_ERROR_SAVE_START",
						CLASS_NAME, "processEInvoiceSRFile", null);
				gstr7TransDocErrorSaveService.saveErrorRecord(processingResults,
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
				List<Gstr7TransDocHeaderEntity> documents = gstr7TransDocConvertion
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
			processedRecords = docRepository
					.processedCount(updateFileStatus.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("businessErrorCount: {}", businessErrorCount);
			}
			totalRecords = documentMap.size();
			errorRecords = processingResults.size() + businessErrorCount;
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