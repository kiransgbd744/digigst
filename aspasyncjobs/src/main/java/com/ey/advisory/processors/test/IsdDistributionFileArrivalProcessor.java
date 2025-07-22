package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.docs.IsdDistrbtnFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityNotFoundException;

@Component("IsdDistributionFileArrivalProcessor")
public class IsdDistributionFileArrivalProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IsdDistributionFileArrivalProcessor.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("IsdDistrbtnFileArrivalHandler")
	private IsdDistrbtnFileArrivalHandler isdDistributionFileArrivalHandler;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		// TODO Auto-generated method stub

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		String source = json.get("source").getAsString();
		String fileId = json.get("fileId").getAsString();
		String docId = json.get("docId").getAsString();
		LOGGER.debug("Inward GSTR6 Distribution file Arrived");
		isdDistributionFileArrivalHandler.processProductFile(message, context);

		Optional<Gstr1FileStatusEntity> fileStatusEntity = gstr1FileStatusRepository
				.findByDocId(docId);
		
		if (fileStatusEntity.isPresent()) {
			Gstr1FileStatusEntity entity = fileStatusEntity.get();
			if (source.equalsIgnoreCase("SFTPSend")
					&& (!entity.getFileStatus().equalsIgnoreCase(JobStatusConstants.FAILED))) {
				String fileName = entity.getFileName();

				if (entity.getProcessed() > 0) {
					DownloadReport(fileId, "processed", fileName);
				}
				if (entity.getError() > 0) {
					DownloadReport(fileId, "error", fileName);
				}
			}
		} else {
			LOGGER.error("Gstr1FileStatusEntity not found for docId: {}", docId);
			throw new EntityNotFoundException("Gstr1FileStatusEntity not found for docId: " + docId);
		}

		LOGGER.debug("Gstr6 Distribution file Arrival processed");

	}

	private void DownloadReport(String fileId, String reportType,
			String fileName) {

		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
		entity.setFileId(Long.valueOf(fileId));
		entity.setReportType(reportType);
		entity.setReportCateg("SFTPSend");
		entity.setDataType("Inward");
		entity.setStatus("active");
		entity.setCreatedBy(userName);
		entity.setCreatedDate(LocalDateTime.now());
		entity.setUpldFileName(fileName);

		entity = fileStatusDownloadReportRepo.save(entity);

		Long id = entity.getId();

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Successfully saved to DB with Report Id : %d", id);
			LOGGER.debug(msg);
		}
		JsonObject jobParams = new JsonObject();
		jobParams.addProperty("id", id);
		jobParams.addProperty("source", "SFTPSend");
		asyncJobsService.createJob(groupCode, JobConstants.ISD_RECON,
				jobParams.toString(), userName, 1L, null, null);

	}
}
