package com.ey.advisory.app.services.docs;

import java.io.InputStream;
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

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.Anx1OutWardErrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.docs.dto.OutwardDocSaveRespDto;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1HeaderCheckService;
import com.ey.advisory.app.services.strcutvalidation.outward.Anx1OutwardDocStructuralValidatorChain;
import com.ey.advisory.app.services.validation.sales.SalesDocRulesValidatorService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.core.async.JobStatusConstants;

@Service("SRFileArrivalHandlerTest")
public class SRFileArrivalHandlerTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SRFileArrivalHandlerTest.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	
	@Autowired
	@Qualifier("SalesDocRulesValidatorService")
	private SalesDocRulesValidatorService salesDocRulesValidatorService;
	@Autowired
	@Qualifier("SRFileToOutwardTransDocConvertion")
	private SRFileToOutwardTransDocConvertion srFileToOutwardTransDocConvertion;

	@Autowired
	@Qualifier("SRFileToOutwardTransDocErrConvertion")
	private SRFileToOutwardTransDocErrConvertion srFileToOutwardTransDocErrConvertion;

	@Autowired
	private DocKeyGenerator<Anx1OutWardErrHeader, String> docKeyGen;

	@Autowired
	@Qualifier("DocErrorRepository")
	private DocErrorRepository docErrorRepository;

	@Autowired
	@Qualifier("DefaultDocSaveService")
	private DefaultDocSaveService srFileSaveService;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("OutwardDocStructuralValidator") private
	 * OutwardDocStructuralValidator outwardDocStructuralValidator;
	 */
	@Autowired
	@Qualifier("Anx1OutwardDocStructuralValidatorChain")
	private Anx1OutwardDocStructuralValidatorChain outwardDocStructuralValidator;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("SRFileHeaderCheckService")
	private Gstr1HeaderCheckService gstr1HeaderCheckService;

	@Autowired
	@Qualifier("anx1OutWardErrHeaderRepository")
	private Anx1OutWardErrHeaderRepository anx1OutWardErrHeaderRepository;

	@Autowired
	@Qualifier("DefaultDocErrorSaveService")
	private DefaultDocErrorSaveService defaultDocErrorSaveService;

	/**
	 * 
	 * @param message
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	public void processSRFile(Message message, AppExecContext context) {
		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.debug(fileArrivalMsg);

		DocumentKeyBuilder documentKeyBuilder = new DocumentKeyBuilder();
		Gstr1FileStatusEntity updateFileStatus = new Gstr1FileStatusEntity();
		try {
			// Extract the File Arrival message from the serialized Job params
			// object.
			// FileArrivalMsgDto msg = extractAndValidateMessage(message);

			// Join the file path and file name to get the file path.

			/*
			 * String fileName = msg.getFileName(); String fileFolder =
			 * msg.getFilePath();
			 */

			String file = message.getUserName();
			String file1 = "C:/umesha/outwardfile/" + file;
			String userName = message.getUserName();
			InputStream inputStream = null;

			/*
			 * try { Session openCmisSession =
			 * gstr1FileUploadUtil.getCmisSession();
			 * LOGGER.debug("openCmisSession " + openCmisSession); Document
			 * document = gstr1FileUploadUtil .getDocument(openCmisSession,
			 * fileName, fileFolder); LOGGER.debug("document name" +
			 * document.getName()); inputStream =
			 * document.getContentStream().getStream(); } catch (Exception e) {
			 * LOGGER.error("Exception occured in SR File Arrival Processor",
			 * e); }
			 */

			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(file);

			/*
			 * TabularDataSourceTraverser traverser = traverserFactory
			 * .getTraverser(fileName);
			 */

			TabularDataLayout layout = new DummyTabularDataLayout(109);

			// Add a dummy row handler that will keep counting the rows.
			OutwardTransDocRowHandler rowHandler = new OutwardTransDocRowHandler<String>(
					new DocumentKeyBuilder());
			long stTime = System.currentTimeMillis();
			// traverser.traverse(inputStream, layout, rowHandler, null);
			traverser.traverse(file1, layout, rowHandler, null);
			Object[] getHeaders = rowHandler.getHeaderData();
			Pair<Boolean, String> checkHeaderFormat = gstr1HeaderCheckService
					.validate(getHeaders);

			LOGGER.error("Header Checker Validations "
					+ checkHeaderFormat.getValue0() + " ,"
					+ checkHeaderFormat.getValue1());

			Map<String, List<Object[]>> documentMap = ((OutwardTransDocRowHandler<?>) rowHandler)
					.getDocumentMap();
			documentMap.entrySet().forEach(entry -> {
				String key = entry.getKey();
				LOGGER.debug("key " + key);
			});
			if (!documentMap.isEmpty()) {
				LOGGER.debug("Document Map  Length " + documentMap.size());
			}

			// Gstr1FileStatusEntity updateFileStatus =
			// gstr1FileStatusRepository.getFileName(fileName);

			updateFileStatus = gstr1FileStatusRepository.getFileName(
					"GSTR-1 Business rules negative data.xlsx");
			// Structural Validations
			Map<String, List<ProcessingResult>> processingResults = outwardDocStructuralValidator
					.validation(documentMap);

			List<String> listKeys = new ArrayList<>();
			for (String keys : processingResults.keySet()) {
				listKeys.add(keys);
			}

			LOGGER.error("structural Validations " + processingResults.values()
					+ "Keys " + processingResults.keySet());

			int totalRecords = 0;
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
						} else {
							List<Object[]> list = documentMap.get(keys);
							errDocMapObj.put(keys, list);
						}
					}

					// To save the structural validation for errors stored to
					// outwardRaw table
					if (!errDocMapObj.isEmpty()) {
						List<Anx1OutWardErrHeader> errorDocument = srFileToOutwardTransDocErrConvertion
								.convertSRFileToOutWardTransError(errDocMapObj,
										documentKeyBuilder, updateFileStatus,userName);

						defaultDocErrorSaveService.saveErrorRecord(
								processingResults, errorDocument);
					}

					if (!documentMapObj.isEmpty()) {
						List<OutwardTransDocument> documents = srFileToOutwardTransDocConvertion
								.convertSRFileToOutwardTransDoc(documentMapObj,
										documentKeyBuilder, updateFileStatus,userName);
						srFileSaveService.saveDocuments(documents);
					}

					totalRecords = documentMap.size();
					errorRecords = processingResults.size();
					information = 0;
					processedRecords = processedRecords - errorRecords;

					updateFileStatus.setTotal(new Integer(String.valueOf(totalRecords)));
					updateFileStatus.setProcessed(processedRecords);
					updateFileStatus.setError(errorRecords);
					updateFileStatus.setInformation(information);

					gstr1FileStatusRepository.save(updateFileStatus);

					long endTime = System.currentTimeMillis();

					long timeDiff = (endTime - stTime) / 1000L;

					LOGGER.debug(String.format("Time Taken to iterate over %d "
							+ "rows = %d seconds!!!", 0, timeDiff));
					LOGGER.debug("DONE!!");
				} else {
					List<OutwardTransDocument> documents = srFileToOutwardTransDocConvertion
							.convertSRFileToOutwardTransDoc(documentMap,
									documentKeyBuilder, updateFileStatus,userName);
					OutwardDocSaveRespDto saveDocuments = srFileSaveService
							.saveDocuments(documents);
					totalRecords = documents.size();
					errorRecords = processingResults.size();
					processedRecords = saveDocuments.getProcessingResults()
							.size();

					information = 0;

					updateFileStatus.setTotal(totalRecords);
					updateFileStatus.setProcessed(processedRecords);
					updateFileStatus.setError(errorRecords);
					updateFileStatus.setInformation(information);

					gstr1FileStatusRepository.save(updateFileStatus);

					long endTime = System.currentTimeMillis();

					long timeDiff = (endTime - stTime) / 1000L;

					LOGGER.debug(String.format("Time Taken to iterate over %d "
							+ "rows = %d seconds!!!", 0, timeDiff));
					LOGGER.debug("DONE!!");
				}

			} else {
				updateFileStatus.setTotal(totalRecords);
				updateFileStatus.setProcessed(processedRecords);
				updateFileStatus.setError(errorRecords);
				updateFileStatus.setInformation(information);
				updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
				gstr1FileStatusRepository.save(updateFileStatus);

			}

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
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
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}

		// Log the extracted info from the message. if (LOGGER.isDebugEnabled())
		{
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