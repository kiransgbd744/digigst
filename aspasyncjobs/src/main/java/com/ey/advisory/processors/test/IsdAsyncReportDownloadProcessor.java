package com.ey.advisory.processors.test;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("IsdAsyncReportDownloadProcessor")
public class IsdAsyncReportDownloadProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("IsdReconAsyncReportDownloadServiceImpl")
	AsyncReportDownloadService asyncReportDownloadService;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();
		String source = json.get("source").getAsString();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside Isd Async Report Download processor with Report id : %d",
					id);
			LOGGER.debug(msg);
		}

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		if (LOGGER.isDebugEnabled()) {
			String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
			LOGGER.debug(msg);
		}
		Pair<String,String> uploadedDocName = null;
		try {
			asyncReportDownloadService.generateReports(id);

			if (source.equalsIgnoreCase("SFTPSend")) {

				Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
						.findById(id);

				String filePath = null;
				String docId = null;
				if (entity.isPresent()) {
					FileStatusDownloadReportEntity fileStatus = entity.get();
					if (fileStatus.getFilePath() != null) {
						filePath = fileStatus.getFilePath();
						docId = fileStatus.getDocId();

					}
				}else {
				    LOGGER.error("FileStatusDownloadReportEntity not found for ID: {}", id);
				    throw new EntityNotFoundException("FileStatusDownloadReportEntity not found for ID: " + id);
				}
				if (filePath != null) {
					
					Document document = null;
					if(Strings.isNullOrEmpty(docId)){
						document = DocumentUtility
								.downloadDocument(filePath, "Anx1FileStatusReport");
					} else {
						document = DocumentUtility.downloadDocumentByDocId(docId);
					}
					
					File outputFile = new File(filePath);

					InputStream contentStream = document
							.getContentStream().getStream();

					try (OutputStream outputStream = FileUtils
							.openOutputStream(outputFile)) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = contentStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, bytesRead);
						}
					}
					if (!outputFile.exists()) {
						outputFile.mkdirs();
					}
					sftpService.uploadFile(outputFile.getAbsolutePath(),
							"SFTP_ROOT/INTG_OUTBOUND/GSTR6_AUTO_CREDIT_DISTRIBUTION");
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("outputFile {}",
								outputFile.getAbsolutePath());
					}
				}

			}
		} catch (Exception ex) {
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}
