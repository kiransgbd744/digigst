package com.ey.advisory.app.services.docs.gstr2;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr2FileStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.Gstr2FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.docs.InwardTransDocRowHandler;
import com.ey.advisory.app.services.strcutvalidation.purchase.InwardDocStructuralValidatorChain;
import com.ey.advisory.app.util.Gstr2FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;

@Service("Gstr2RawFileArrivalHandler")
public class Gstr2RawFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2RawFileArrivalHandler.class);
	
	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;
	
	@Autowired
	@Qualifier("Gstr2FileUploadUtil")
	private Gstr2FileUploadUtil gstr2FileUploadUtil;
	
	@Autowired
	@Qualifier("Gstr2RawFileToInwardTransDocConvertion")
	private Gstr2RawFileToInwardTransDocConvertion gstr2RawFileToInwardTransDocConvertion;
	
	@Autowired
	@Qualifier("DefaultInwardDocSaveService")
	private DefaultInwardDocSaveService  defaultInwardDocSaveService;
	
	@Autowired
	@Qualifier("InwardDocStructuralValidatorChain")
	private InwardDocStructuralValidatorChain inwardDocStructuralValidatorChain;
	
	@Autowired
	@Qualifier("Gstr2FileStatusRepository")
	private Gstr2FileStatusRepository gstr2FileStatusRepository;

	/**
	 * 
	 * @param message
	 * @param context
	 */
	public void processGstr2RawFile(Message message, AppExecContext context) {

    String fileArrivalMsg = String.format("Gstr2 Raw File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.debug(fileArrivalMsg);
		
		InwardDocumentKeyBuilder gstr2documentKeyBuilder = new InwardDocumentKeyBuilder();

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		/*String oriFileName = fileName.substring(0,
				fileName.lastIndexOf("."));*/
		//String userName = message.getUserName();
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr2FileUploadUtil.getCmisSession();
			LOGGER.debug("openCmisSession "+openCmisSession);
			Document document = gstr2FileUploadUtil
					.getDocument(openCmisSession,fileName, fileFolder);
			LOGGER.debug("document name"+document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
		LOGGER.error("Exception occured in Gstr2 Raw File Arrival Processor", e);
		}
		
		/*String filePath = new StringJoiner("/").add(fileFolder).add(fileName)
				.toString();*/
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(65);

		// Add a dummy row handler that will keep counting the rows.
		RowHandler rowHandler = new InwardTransDocRowHandler<String>(
				new InwardDocumentKeyBuilder());
		long stTime = System.currentTimeMillis();
		traverser.traverse(inputStream, layout, rowHandler, null);
		Map<String, List<Object[]>> documentMap = 
				((InwardTransDocRowHandler<?>) rowHandler)
				.getDocumentMap();
		documentMap.entrySet().forEach(entry -> {
			String key = entry.getKey();
			LOGGER.debug("key "+key);
		});
	/*	if(!documentMap.isEmpty()){
				LOGGER.debug("Document Map  Length "+documentMap.size());
		}}
	*/	Gstr2FileStatusEntity updateFileStatus = 
				gstr2FileStatusRepository.getFileName(fileName);
		
		// Structural Validations
		Map<String, List<ProcessingResult>> processingResults = 
				inwardDocStructuralValidatorChain.validation(documentMap);
		if(processingResults.isEmpty()){
		List<InwardTransDocument> documents = 
				gstr2RawFileToInwardTransDocConvertion
				.convertGstr2RawFileToInwardTransDoc(documentMap,
						gstr2documentKeyBuilder,updateFileStatus);
		
		
		defaultInwardDocSaveService.saveDocuments(documents, null, null);
		
		}	
		long endTime = System.currentTimeMillis();

		long timeDiff = (endTime - stTime) / 1000L;
		
			LOGGER.debug(String.format(
					"Time Taken to iterate over %d " + "rows = %d seconds!!!",
					0, timeDiff));
			LOGGER.debug("DONE!!");

	}

	/**
	 * Validate the Input JSON to check if 'path', 'fileName' and Group Code are
	 * actually present within the JSON. If not, throw an EWB Exception so that
	 * the file processing status is marked as 'Failed'. Otherwise, extract
	 * these values and return them.
	 * 
	 * @param jsonMessage
	 * The 'Message' instance passed by the AsyncExecution framework
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