package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.Annexure11HeaderCheckService;
import com.ey.advisory.app.services.annexure1fileupload.CewbStructuralErrorUploadService;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("CewbFileArrivalHandler")
@Slf4j
public class CewbFileArrivalHandler {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("CewbHeaderCheckService")
	private Annexure11HeaderCheckService annexure11HeaderCheckService;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("CewbStructuralErrorUploadService")
	private CewbStructuralErrorUploadService cewbStructuralErrorUploadService; 

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Transactional(value = "clientTransactionManager")
	public void processCewbFileFile(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCewbFileFile of CEWB");
		}
		String tenantCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tenant Id Is {}", tenantCode);
		}
		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(fileArrivalMsg);
		}
		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();

		InputStream inputStream = null;

		try {
			Session openCmisSession = outwardFileUploadUtil.openCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession " + openCmisSession);
			}
			Document document = outwardFileUploadUtil
					.getDocument(openCmisSession, fileName, fileFolder);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name" + document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error("Exception occured in CEWB "
					+ "File Arrival Processor", e);
		}
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(9);
		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();

		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();

		Pair<Boolean, String> checkHeaderFormat = annexure11HeaderCheckService
				.validate(getHeaders);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Header Checker Validation of CEWB file"
					+ checkHeaderFormat.getValue0() + " ,"
					+ checkHeaderFormat.getValue1());
		}

		List<Object[]> listOfCewb = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;
		LOGGER.error("struProcessedRecords ");
		Boolean headersTrue = checkHeaderFormat.getValue0();

		if (headersTrue) {
			cewbStructuralErrorUploadService.processData(listOfCewb,
					updateFileStatus);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Header Checker Failed of CEWB");
			}
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
