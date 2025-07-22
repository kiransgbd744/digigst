package com.ey.advisory.app.services.gl.masterFile.uploads;

import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.base.Strings;

/**
 * 
 * @author Kiran s
 *
 */
@Service("GlMasterFileArrivalHandler")
public class GlMasterFileArrivalHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GlMasterFileArrivalHandler.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("GlMasterFileProcessService")
	private GlMasterFileProcessService glMasterFileProcessService;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glReconFileStatusRepository;

	public void processGlMasterFile(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("process GlMasterFile");
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

		Gstr1GetInvoicesReqDto dto = extractAndValidateMessage(message);
		String fileName = dto.getFileName();
		String fileFolder = dto.getFilePath();
		String docId = dto.getDocumentId();
		String fileType = dto.getFileType();
		Long id = dto.getFileId();
		InputStream inputStream = null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("File Name: " + fileName);
			LOGGER.debug("File Folder: " + fileFolder);
			LOGGER.debug("Document ID: " + docId);
			LOGGER.debug("File Type: " + fileType);
			LOGGER.debug("File ID: " + id);
		}

		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession " + openCmisSession);
			}
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = gstr1FileUploadUtil.getDocument(openCmisSession,
						fileName, fileFolder);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" inside if condition and document name"
							+ document.getName());
				}
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" inside else condition and document name"
							+ document.getName());
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("document name" + document.getName());
			}
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {

			String msg1 = "Exception occured in Gl Master File Arrival Processor";
			e.printStackTrace();
			LOGGER.error(msg1, e);
			throw new APIException(msg1, e);
		}

		try {
			glMasterFileProcessService.processGlMasterFile(inputStream,
					fileName, dto);
			// Update file status to "Processed"
			glReconFileStatusRepository.updateFileStatus(id, "Success", docId);

		} catch (Exception e) {
		    String msg1 = "Error occurred while processing the GlMaster file:: ";
		    e.printStackTrace();
		    LOGGER.error(msg1, e);

		    String errorMessage = e.getMessage();
		    if (errorMessage != null && errorMessage.length() > 500) {
		        errorMessage = errorMessage.substring(0, 500); // truncate to avoid DB size limits
		    }

		    // Update file status to "Failed" and set error description
		    glReconFileStatusRepository.updateFileStatusWithError(id,
		            JobStatusConstants.FAILED, null, errorMessage);

		    throw new APIException(msg1, e);
		    
		}
	}

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
