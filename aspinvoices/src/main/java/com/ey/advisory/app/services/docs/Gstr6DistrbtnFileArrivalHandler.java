package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.Gstr6DistrbtnHeaderCheckService;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.Gstr6StructuralErrorUploadService;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
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
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;

/**
 *  
 *   
 * @author Balakrishna.S
 *
 */

@Component("Gstr6DistrbtnFileArrivalHandler")
public class Gstr6DistrbtnFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6DistrbtnFileArrivalHandler.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr6DistrbtnHeaderCheckService")
	private Gstr6DistrbtnHeaderCheckService gstr6HeaderCheckService;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr6StructuralErrorUploadService")
	private Gstr6StructuralErrorUploadService gstr6StructuralErrorUploadService;
	

	public void processProductFile(Message message, AppExecContext context) {

		LOGGER.error("processProductFile");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.error(fileArrivalMsg);

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		String fileId = msg.getDocId();
	//	Long entityId = msg.getEntityId();

		InputStream inputStream = null;

		Optional<Gstr1FileStatusEntity> gstr1FileStatusEntity = gstr1FileStatusRepository
				.findByDocId(fileId);
		try {
			Session openCmisSession = outwardFileUploadUtil.openCmisSession();
			LOGGER.error("openCmisSession " + openCmisSession);
			
			String docId = gstr1FileStatusEntity.get().getDocId();
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = outwardFileUploadUtil
						.getDocument(openCmisSession, fileName, fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			
			LOGGER.error("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error("Exception occured in onboarding Product "
					+ "File Arrival Processor", e);
		}
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(23);

		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		long stTime = System.currentTimeMillis();
		LOGGER.info("Start Time " + stTime);
		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();

		Pair<Boolean, String> checkHeaderFormat = gstr6HeaderCheckService
				.validate(getHeaders);

		LOGGER.error(
				"Header Checker Validations " + checkHeaderFormat.getValue0()
						+ " ," + checkHeaderFormat.getValue1());

		List<Object[]> productList = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();
		
		trim(productList);
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		ProcessingContext processingContext = new ProcessingContext();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		LOGGER.error("businessProcessedRecords ");
		if (checkHeaderFormat.getValue0()) {
			LOGGER.error("Header Checker Validations Enter  ");
			gstr6StructuralErrorUploadService.
			                          processData(productList,updateFileStatus,processingContext);	
		}
		
		else{
			LOGGER.error("Header Checker Validations Failed "+
		            checkHeaderFormat.getValue0()
			+" ,"+checkHeaderFormat.getValue1());
			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(processedRecords);
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
	        updateFileStatus.setFileStatus(JobStatusConstants.FAILED);
			gstr1FileStatusRepository.save(updateFileStatus);
			
		}
	}
	
		
	
	private void trim(List<Object[]> productList) {
		// TODO Auto-generated method stub
		
		for(Object[] obj:productList){
			obj[0]=trimObj(obj[0]);
			obj[1]=trimObj(obj[1]);
			obj[2]=trimObj(obj[2]);
			obj[3]=trimObj(obj[3]);
			obj[4]=trimObj(obj[4]);
			obj[5]=trimObj(obj[5]);
			obj[6]=trimObj(obj[6]);
			obj[7]=trimObj(obj[7]);
			obj[8]=trimObj(obj[8]);
			obj[9]=trimObj(obj[9]);
			obj[10]=trimObj(obj[10]);
			obj[11]=trimObj(obj[11]);
			obj[12]=trimObj(obj[12]);
			obj[13]=trimObj(obj[13]);
			obj[14]=trimObj(obj[14]);
			obj[15]=trimObj(obj[15]);
			obj[16]=trimObj(obj[16]);
			obj[17]=trimObj(obj[17]);
			obj[18]=trimObj(obj[18]);
			obj[19]=trimObj(obj[19]);
			obj[20]=trimObj(obj[20]);
			obj[21]=trimObj(obj[21]);
			obj[22]=trimObj(obj[22]);

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
		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}

	public static Object trimObj(Object obj) {
		
		return obj != null ? obj.toString().trim() : null;
		}
} 

