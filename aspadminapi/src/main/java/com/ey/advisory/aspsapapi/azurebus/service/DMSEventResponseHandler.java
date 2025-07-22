package com.ey.advisory.aspsapapi.azurebus.service;

import static com.ey.advisory.common.GSTConstants.FOLDER_NAME;

import java.io.File;
import java.util.Optional;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.DmsRequestLogRepository;
import com.ey.advisory.app.dms.DmsRequestLog;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadService;
import com.ey.advisory.common.BlobStoreUtility;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DMSEventResponseHandler {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("DefaultGstr1FileUploadService")
	private Gstr1FileUploadService gstr1FileUploadService;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	@Autowired
	@Qualifier("DmsRequestLogRepository")
	private DmsRequestLogRepository logRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	BlobStoreUtility blobStoreUtility;

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glReconFileStatusRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final String CONF_KEY = "dms.details.blob.keyVaultSecretName";
	private static final String CONF_CATEG = "DMS_BLOB";

	public void messageHandler(ServiceBusReceivedMessageContext context) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.error(" dms context {} ", context);

		try {
			String inputString = context.getMessage().getBody().toString();
			LOGGER.error("message from DMS {} ", inputString);
			DmsEventRequestDto requestDto = gson.fromJson(inputString, DmsEventRequestDto.class);
			TenantContext.setTenantId(requestDto.getGroupCode());
			Optional<DmsRequestLog> logEntity = logRepository.findByUuid(requestDto.getUuid());
			logRepository.updateCallBackResponse(requestDto.getUuid(), inputString);

			if (requestDto.getJobCategory().equalsIgnoreCase(JobConstants.GLRECON_DUMP_FILEUPLOAD)) {

				Optional<GlReconFileStatusEntity> entity1 = glReconFileStatusRepository
						.getFileBasedonPayloadId(requestDto.getUuid());
				Long fileId = 0L;
				if (entity1.get() != null) {
					fileId = entity1.get().getId();
				}
				if (requestDto.getMessage() != null
						&& requestDto.getMessage().equalsIgnoreCase("File Transformed Successfully")) {

					glReconFileStatusRepository.updateTransformationStatus(fileId, "Transformation Successful");
					String jobCategory = logEntity.get().getJobCategory();
					// String fileName = requestDto.getUuid() + ".xlsx";
					String fileName = requestDto.getFilename();
					Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
					String blobSecretName = "";
					if (config != null) {
						blobSecretName = config.getValue();
						LOGGER.error("BlobSecretName {} ", blobSecretName);
					}

					File file = blobStoreUtility.downloadSummaryReport(fileName, blobSecretName);

					User user = SecurityContext.getUser();
					String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
					String folderName = FOLDER_NAME;

					Pair<String, String> pair = DocumentUtility.uploadFile(file, folderName);
					String docId = pair.getValue1();

					String groupCode = TenantContext.getTenantId();
					JsonObject jsonParams = new JsonObject();
					jsonParams.addProperty("fileId", fileId);
					jsonParams.addProperty("fileName", fileName);
					jsonParams.addProperty("filePath", folderName);
					jsonParams.addProperty("source", JobStatusConstants.WEB_UPLOAD);
					jsonParams.addProperty("docId", entity1.get().getDocId());
					asyncJobsService.createJob(groupCode, JobConstants.GLRECON_DUMP_FILEUPLOAD, jsonParams.toString(),
							userName, 1L, null, null);

					glReconFileStatusRepository.updateDocIdAndStatus(fileId, docId, "Uploaded");
				} else {
					glReconFileStatusRepository.updateTransformationStatus(fileId, "Transformation Failed");
					glReconFileStatusRepository.updateStatus(fileId, "Failed");
				}

			} else {
				Optional<Gstr1FileStatusEntity> entity = gstr1FileStatusRepository
						.getFileBasedonUuid(requestDto.getUuid());

				Long fileId = 0L;
				if (entity.get() != null) {
					fileId = entity.get().getId();
				}
				if (requestDto.getMessage() != null
						&& requestDto.getMessage().equalsIgnoreCase("File Transformed Successfully")) {

					gstr1FileStatusRepository.updateTransformationStatus(fileId, "Transformation Successful");
					String jobCategory = logEntity.get().getJobCategory();
					// String fileName = requestDto.getUuid() + ".xlsx";
					String fileName = requestDto.getFilename();
					Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
					String blobSecretName = "";
					if (config != null) {
						blobSecretName = config.getValue();
						LOGGER.error("BlobSecretName {} ", blobSecretName);
					}

					File file = blobStoreUtility.downloadSummaryReport(fileName, blobSecretName);

					User user = SecurityContext.getUser();
					String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
					String folderName = FOLDER_NAME;

					Pair<String, String> pair = DocumentUtility.uploadFile(file, folderName);
					String docId = pair.getValue1();
					String paramJson = "{\"filePath\":\"" + folderName + "\"," + "\"fileName\":\"" + fileName + "\","
							+ "\"docId\":\"" + docId + "\",\"fileId\":\"" + fileId + "\"}";
					
					gstr1FileStatusRepository.updateDocId(fileId, docId);
					gstr1FileUploadJobInsertion.fileUploadJob(paramJson, jobCategory, userName);
				} else {
					gstr1FileStatusRepository.updateTransformationStatus(fileId, "Transformation Failed");
					gstr1FileStatusRepository.updateFileStatus(fileId, "Failed");
				}
			}
			context.complete();
		} catch (Exception e) {
			LOGGER.error("Exception while retrieving the Details, Hence abandoning the Message.", e);
			context.abandon();
		} finally {
			TenantContext.clearTenant();
		}
	}
}