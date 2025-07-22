package com.ey.advisory.processors.test;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.common.GstnValidatorConstants;
import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;
import com.ey.advisory.app.services.docs.GstinValidFileArrivalHandler;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("GstinValidatorFileArrivalProcessor")
public class GstinValidatorFileArrivalProcessor implements TaskProcessor {

	@Autowired
	@Qualifier(value = "GstinValidatorConfigRepository")
	GstinValidatorConfigRepository gstinValidRepo;

	@Autowired
	@Qualifier("GstinValidFileArrivalHandler")
	private GstinValidFileArrivalHandler srFileArrivalHandler;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstin Validator Job"
							+ " executing for groupcode %s and params %s",
					groupcode, jsonString);
			LOGGER.debug(msg);
		}
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long requestId = json.get("requestID").getAsLong();
		String fileName = json.get("fileName").getAsString();
		String folderName = json.get("folderName").getAsString();
		boolean isEinvApplicable = json.get("isEinvApplicable").getAsBoolean();
		String docId = json.get("docId").getAsString();
		try {
			gstinValidRepo.updateGstinValidatorStatus(
					GstnValidatorConstants.INITIATED_STATUS, requestId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"The Gstin File Validator"
								+ " File Status Changed to Initiated for File Name: %s",
						fileName);
				LOGGER.debug(msg);

			}
			Pair<String, String> gstinValidatorOnFile = srFileArrivalHandler.gstinValidatorOnFile(requestId,
					fileName, folderName, isEinvApplicable,docId);
			String fName = gstinValidatorOnFile.getValue0();
			String fDocId = gstinValidatorOnFile.getValue1();
			if (fName != null && !fName.isEmpty()) {
				gstinValidRepo.updateGstinValidatorDetails(
						GstnValidatorConstants.GENERATED_STATUS,
						fName, LocalDateTime.now(), requestId, null,fDocId);

				GstinValidatorEntity entity = gstinValidRepo
						.findByRequestId(requestId);

				if ("SFTP".equalsIgnoreCase(entity.getCreatedBy())) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" inside SFTP Block {}",
								entity.toString());
					}

					String filePath = entity.getFilePath();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" inside SFTP Block filePath {}",
								filePath);
					}
					Document document = null;
					if (filePath != null) {

						// String fileName = confntity.getFilePath();
						String[] fileNametoPass = filePath.split("\\.");
						String sendFileName = fileNametoPass[0];
						String fileFolder = "GstinValidatorReport";

						document = DocumentUtility
								.downloadDocumentByDocId(entity.getDocId());
					/*	if (document == null) {
							document = DocumentUtility
									.downloadDocument(filePath, fileFolder);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(" document {}", sendFileName);
							}
						}
						*/
						File outputFile = new File(filePath);

						InputStream contentStream = document.getContentStream()
								.getStream();

						try (OutputStream outputStream = FileUtils
								.openOutputStream(outputFile)) {
							byte[] buffer = new byte[1024];
							int bytesRead;
							while ((bytesRead = contentStream
									.read(buffer)) != -1) {
								outputStream.write(buffer, 0, bytesRead);
							}
						}
						if (!outputFile.exists()) {
							outputFile.mkdirs();
						}
						sftpService.uploadFile(outputFile.getAbsolutePath(),
								"SFTP_ROOT/INTG_OUTBOUND/GSTN_VALIDATOR");
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("outputFile {}",
									outputFile.getAbsolutePath());
						}
					}

				}
			} else {
				gstinValidRepo.updateGstinValidatorDetails(
						GstnValidatorConstants.FAILED_STATUS, fName,
						LocalDateTime.now(), requestId, "FILE NAME IS NULL",null);
			}

		} catch (Exception ae) {
			LOGGER.error("Exception Occured while generating GSTIN Validator ",
					ae);
			String errMsg = ae.getMessage();
			gstinValidRepo.updateGstinValidatorDetails(
					GstnValidatorConstants.FAILED_STATUS, null,
					LocalDateTime.now(), requestId,
					errMsg.substring(0, Math.min(errMsg.length(), 100)),null);
			throw new AppException(ae);
		}

	}

}
