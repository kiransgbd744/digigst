package com.ey.advisory.app.services.gstr7fileupload;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */

@Slf4j
@Service("Gstr7FileArrivalHandler")
public class Gstr7FileArrivalHandler {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr7HeaderCheckService")
	private Gstr7HeaderCheckService gstr7HeaderCheckService;

	@Autowired
	@Qualifier("Gstr7TdsFileProcessService")
	private Gstr7TdsFileProcessService gstr7TdsFileProcessService;

	@Transactional(value = "clientTransactionManager")
	public void processGstr7File(Message message, AppExecContext context) {
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("processTdsFile");
		}
		String tenantCode = message.getGroupCode();
		TenantContext.setTenantId(tenantCode);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Tenant Id Is {}", tenantCode);
		}

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'", message.getParamsJson());
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(fileArrivalMsg);
		}

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String docIdCon = msg.getDocId();
		
		ProcessingContext processingContext = new ProcessingContext();
		InputStream inputStream = null;
		Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = gstr1FileStatusRepository
				.findByDocId(docIdCon);
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("openCmisSession " + openCmisSession);
			}
			String docId = gstr1FileStatusEntity.get().getDocId();
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = gstr1FileUploadUtil.getDocument(openCmisSession, fileName, fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("document name" + document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			String msg1="Exception occured in Gstr7 tds" + "File Arrival Processor";
			e.printStackTrace();
			LOGGER.error(msg1,e);
			throw new APIException(msg1,e);
			
		}
		TabularDataSourceTraverser traverser = traverserFactory.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(27);

		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		long stTime = System.currentTimeMillis();
		LOGGER.info("Start Time " + stTime);
		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();

		Pair<Boolean, String> checkHeaderFormat = gstr7HeaderCheckService.validate(getHeaders);

		LOGGER.error(
				"Header Checker Validations " + checkHeaderFormat.getValue0() + " ," + checkHeaderFormat.getValue1());

		List<Object[]> gstr7TdsList = ((FileUploadDocRowHandler<?>) rowHandler).getFileUploadList();
		exponentialConversation(gstr7TdsList);
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository.getFileName(fileName);

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		if (checkHeaderFormat.getValue0()) {
			gstr7TdsFileProcessService.validateAndProcessGstr7TdsFileData(gstr7TdsList, updateFileStatus,
					gstr1FileStatusRepository, processingContext);
		} else {
			LOGGER.error("Header Checker Failed ");
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			gstr1FileStatusRepository.save(updateFileStatus);
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
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance().fromJson(message.getParamsJson(),
				FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format("Values received are -> " + "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(), message.getGroupCode());
			if (LOGGER.isDebugEnabled()) {
			LOGGER.error(String.format("%s %s", msg, logMsg));
			}
			throw new AppException(errMsg);
		}

		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Message Details are -> Path: '%s', " + "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(), message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}
	
	public void exponentialConversation(List<Object[]> asEnterTdsList){
		for(Object[] entity:asEnterTdsList){
			String conNum = String.valueOf(CommonUtility.exponentialAndZeroCheck(entity[11]));
			if (conNum != null && !conNum.isEmpty()) {
				if (conNum.length() > 20) {
					conNum = conNum.substring(0, 20);
				}
			}
			entity[11]=conNum;
		}
	}
}
