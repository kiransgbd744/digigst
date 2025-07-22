package com.ey.advisory.app.services.docs;

import java.io.InputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.annexure1fileupload.Inward3h3iStructuralErrorUploadService;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.app.services.search.datastatus.anx1.Table3H3IHeaderCheckService;
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

@Service("InwardTable3H3IFileArrivalHandler")
@Slf4j
public class InwardTable3H3IFileArrivalHandler {

	@Autowired
	@Qualifier("Table3H3IHeaderCheckService")
	private Table3H3IHeaderCheckService table3H3IHeaderCheckService;

	@Autowired
	@Qualifier("Inward3h3iStructuralErrorUploadService")
	private Inward3h3iStructuralErrorUploadService table3h3iStructuralErrorUploadService;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	public void processTable3H3IFile(Message message, AppExecContext context) {
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		LOGGER.error("processTable3H3IFile");

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.error(fileArrivalMsg);
		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.

		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();

		InputStream inputStream = null;

		try {
			Session openCmisSession = outwardFileUploadUtil.openCmisSession();
			LOGGER.error("openCmisSession " + openCmisSession);
			Document document = outwardFileUploadUtil
					.getDocument(openCmisSession, fileName, fileFolder);
			LOGGER.error("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in Table3H3I File Arrival Processor", e);
		}

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);

		TabularDataLayout layout = new DummyTabularDataLayout(37);
		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		traverser.traverse(inputStream, layout, rowHandler, null);

		Object[] getHeaders = rowHandler.getHeaderRow();
		Pair<Boolean, String> checkHeaderFormat = table3H3IHeaderCheckService
				.validate(getHeaders);

		LOGGER.error(
				"Header Checker Validations " + checkHeaderFormat.getValue0()
						+ " ," + checkHeaderFormat.getValue1());

		List<Object[]> listOftable3h3i = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();

		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		if (checkHeaderFormat.getValue0()) {
			LOGGER.error("Header Header Enter ");
			table3h3iStructuralErrorUploadService.processData(listOftable3h3i,
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
