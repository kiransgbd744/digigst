
package com.ey.advisory.app.services.docs.gstr2a;

import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1FileArrivalHandler")
public class Gstr1FileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1FileArrivalHandler.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileProcessService")
	private Gstr1FileProcessService gstr1FileProcessService;

	// @Transactional(value = "clientTransactionManager")
	public void processGstr1File(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processGstr1File");
		}
		String tenantCode = message.getGroupCode();
		TenantContext.setTenantId(tenantCode);
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
		Gstr1GetInvoicesReqDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		Long batchId = msg.getBatchId();

		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession {} ", openCmisSession);
			}
			Document document = gstr1FileUploadUtil.getDocument(openCmisSession,
					fileName, fileFolder);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name {} ", document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {

			String msg1 = "Exception occured in get gstr1 File Arrival Processor";
			e.printStackTrace();
			LOGGER.error(msg1, e);
			throw new APIException(msg1, e);
		}

		try {
			int count = gstr1FileProcessService.processGstr1File(inputStream,
					fileName, batchId);

			Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
					.getFileName(fileName);

			updateFileStatus.setTotal(count);
			updateFileStatus.setProcessed(count);
			updateFileStatus.setError(0);
			updateFileStatus.setInformation(0);
			updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);
			gstr1FileStatusRepository.save(updateFileStatus);
		} catch (Exception e) {

			String msg1 = "Error occured while processing the Gstr1 file:: ";
			e.printStackTrace();
			LOGGER.error(msg1, e);
			throw new APIException(msg1, e);
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
	private Gstr1GetInvoicesReqDto extractAndValidateMessage(Message message) {
		Gstr1GetInvoicesReqDto msg = GsonUtil.newSAPGsonInstance().fromJson(
				message.getParamsJson(), Gstr1GetInvoicesReqDto.class);
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
			LOGGER.error(logMsg);
		}
		return msg;
	}
}
