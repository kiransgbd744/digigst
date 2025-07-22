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
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.Annexure11HeaderCheckService;
import com.ey.advisory.app.services.annexure1fileupload.Gstr3bStructuralErrorUploadService;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.DocumentIdContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Gstr3bFileArrivalHandler")
@Slf4j
public class Gstr3bFileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr3BHeaderCheckService")
	private Annexure11HeaderCheckService annexure11HeaderCheckService;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("Gstr3bStructuralErrorUploadService")
	private Gstr3bStructuralErrorUploadService gstr3bStructuralErrorUploadService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	public void processGstr3BFile(Message message, AppExecContext context) {
		/*String fileName = message.getUserName();
		String folderName = "C:/Users/Mahesh.golla/Desktop/softawres/EY/GSTR1/Test Data/" + fileName;*/

		LOGGER.error("processGstr3BFile");
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
		
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		
		InputStream inputStream = null;
		
		String docId = updateFileStatus.getDocId();
		Document document=null;
		try {
			Session openCmisSession = outwardFileUploadUtil.openCmisSession();
			LOGGER.error("openCmisSession " + openCmisSession);
			
			if (Strings.isNullOrEmpty(docId)) {
			DocumentIdContext.setDocumentId(docId);
			 document = outwardFileUploadUtil
					.getDocument(openCmisSession, fileName, fileFolder);
			 if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("documnet is not downloaded using doc id(GSTR-3B-> ITC04) ");
				}
			}
			else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("documnet is downloaded using doc id(GSTR-3B-> ITC04) ");
				}
			}
			DocumentIdContext.clearTenant();
			
			LOGGER.error("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in GSTR3B " + "File Arrival Processor",
					e);
		}
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(8);
		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		long stTime = System.currentTimeMillis();
		LOGGER.info("Start Time " + stTime);
		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();

		Pair<Boolean, String> checkHeaderFormat = annexure11HeaderCheckService
				.validate(getHeaders);

		LOGGER.error(
				"Header Checker Validations " + checkHeaderFormat.getValue0()
						+ " ," + checkHeaderFormat.getValue1());

		List<Object[]> listOfGstr3bData = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		LOGGER.error("businessProcessedRecords ");

		if (checkHeaderFormat.getValue0()) {

			gstr3bStructuralErrorUploadService.processData(listOfGstr3bData,
					updateFileStatus);
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
}
