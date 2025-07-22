package com.ey.advisory.app.sftp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Service("GSTR2ASFTPHandler")
@Slf4j
public class GSTR2ASFTPHandler {

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static final String HYPHEN = "_";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository gstr1FileStatusRepository;
	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	private static final String GSTR2A_SFTP_SOURCE = "ey.internal.gstr2a.sftp.source";

	private static final String GSTR2A_SFTP_DESTINATION = "ey.internal.gstr2a.sftp.destination";

	private static final List<String> FILE_EXT = ImmutableList
			.of(JobStatusConstants.XLSX_TYPE, JobStatusConstants.CSV_TYPE);

	public void sftpFilesDownload() {
		File tempDir = null;
		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.gstr2a.sftp");

			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("temp directory name : %s", tempDir);
				LOGGER.debug(msg);
			}
			sftpService.downloadFiles(tempDir.getAbsolutePath(),
					configMap.get(GSTR2A_SFTP_SOURCE).getValue(), FILE_EXT);
			if (tempDir.list().length == 0) {
				String errMsg = String.format("Temp Directory  %s is empty",
						tempDir);
				LOGGER.error(errMsg);
				GenUtil.deleteTempDir(tempDir);
				return;
			}
			File[] downloadedFiles = tempDir.listFiles();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Total No of files downloaded to remote directory is {}",
						downloadedFiles.length);
			List<String> uploadedFiles = new ArrayList<>();
			for (File file : downloadedFiles) {
				LocalDateTime now = LocalDateTime.now();
				String timeMilli = DATE_TIME_FORMAT.format(now);
				String fileNameToBeUploaded = GSTConstants.GSTR2A_SFTP_USERRESPONSE_FILE
						+ HYPHEN + timeMilli + HYPHEN + file.getName();
				
				File newFile = new File(fileNameToBeUploaded);
				Files.move(file.toPath(), newFile.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				
				/*String uploadFileWithFileName = DocumentUtility
						.uploadFileWithFileName(file,
								ConfigConstants.GSTR2USERRESPONSEUPLOADS,
								fileNameToBeUploaded);*/
				
				Pair<String, String> uploadFileWithFileName = DocumentUtility
						.uploadFile(newFile, ConfigConstants.GSTR2USERRESPONSEUPLOADS);
				 
				if (uploadFileWithFileName == null)
					continue;
				// saving into file status table
				Gstr1FileStatusEntity fileStatus = getFileStatusData(
						fileNameToBeUploaded, GSTConstants.SYSTEM,
						"RECON_RESPONSE", GSTConstants.INWARD);
				fileStatus.setDocId(uploadFileWithFileName.getValue1());
				gstr1FileStatusRepository.save(fileStatus);

				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("fileId", fileStatus.getId());
				jsonParams.addProperty("fileName", fileNameToBeUploaded);
				jsonParams.addProperty("filePath",
						ConfigConstants.GSTR2USERRESPONSEUPLOADS);

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GSTR2A_SFTP_RESP_UPLOAD,
						jsonParams.toString(), "SYSTEM", 5L, 0L, 0L);

				uploadedFiles.add(file.getName());

			}
			boolean archivedFiles = sftpService.moveFiles(uploadedFiles,
					configMap.get(GSTR2A_SFTP_SOURCE).getValue(),
					configMap.get(GSTR2A_SFTP_DESTINATION).getValue());
			if (!archivedFiles) {
				LOGGER.error("Error while moving the files.");

			}
		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);

		} finally {
			if (tempDir != null) {
				GenUtil.deleteTempDir(tempDir);
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("Gstr2ASftpFiles").toFile();
	}

	public Gstr1FileStatusEntity getFileStatusData(String fileName,
			String userName, String fileType, String dataType) {
		Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();
		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setUpdatedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		fileStatus.setReceivedDate(localDate.toLocalDate());
		fileStatus.setSource(JobStatusConstants.SFTP_WEB_UPLOAD);
		fileStatus.setDataType(dataType);
		return fileStatus;
	}
}