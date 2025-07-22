package com.ey.advisory.app.services.jobs.gstr6;

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
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr6aFileArrivalHandler")
public class Gstr6aFileArrivalHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr6aFileArrivalHandler.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr6aFileProcessService")
	private Gstr6aFileProcessService gstr6aFileProcessService;
	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	public void processGstr6aFile(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processGstr6aFile");
		}
		String tenantCode = message.getGroupCode();
		TenantContext.setTenantId(tenantCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tenant Id Is {}", tenantCode);
		}

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'", message.getParamsJson());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(fileArrivalMsg);
		}

		Gstr1GetInvoicesReqDto msg = extractAndValidateMessage(message);

		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();
		Long batchId = msg.getBatchId();

		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession " + openCmisSession);
			}
			Document document = gstr1FileUploadUtil.getDocument(openCmisSession, fileName, fileFolder);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name" + document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {

			String msg1 = "Exception occured in gstr6a File Arrival Processor";
			e.printStackTrace();
			LOGGER.error(msg1, e);
			throw new APIException(msg1, e);
		}

		try {
			int count = gstr6aFileProcessService.processGstr2File(inputStream, fileName, batchId);

			Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository.getFileName(fileName);

			updateFileStatus.setTotal(count);
			updateFileStatus.setProcessed(count);
			updateFileStatus.setError(0);
			updateFileStatus.setInformation(0);
			updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);
			gstr1FileStatusRepository.save(updateFileStatus);
		} catch (Exception e) {
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			String msg1 = "Error occured while processing the Gstr6a file:: ";
			e.printStackTrace();
			LOGGER.error(msg1, e);
			throw new APIException(msg1, e);
		}
	}

	private Gstr1GetInvoicesReqDto extractAndValidateMessage(Message message) {
		Gstr1GetInvoicesReqDto msg = GsonUtil.newSAPGsonInstance().fromJson(message.getParamsJson(),
				Gstr1GetInvoicesReqDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format("Values received are -> " + "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(), message.getGroupCode());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error(String.format("%s %s", msg, logMsg));
			}
			throw new AppException(errMsg);
		}

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Message Details are -> Path: '%s', " + "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(), message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}
}
