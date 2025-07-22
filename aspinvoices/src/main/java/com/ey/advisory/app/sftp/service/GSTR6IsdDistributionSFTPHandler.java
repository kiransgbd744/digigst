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
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Service("GSTR26IsdDistributionSFTPHandler")
@Slf4j
public class GSTR6IsdDistributionSFTPHandler {

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

	private static final String GSTR6_ISD_DSITRIBUTION_SFTP_SOURCE = "ey.internal.gstr6.isd.distribution.sftp.source";

	private static final String GSTR6_ISD_DSITRIBUTION_SFTP_DESTINATION = "ey.internal.gstr6.isd.distribution.sftp.destination";

	private static final List<String> FILE_EXT = ImmutableList
			.of(JobStatusConstants.XLSX_TYPE, JobStatusConstants.CSV_TYPE);

	public void sftpFilesDownload() {
		File tempDir = null;
		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.gstr6.isd.distribution.sftp");

			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("temp directory name : %s", tempDir);
				LOGGER.debug(msg);
			}
			sftpService.downloadFiles(
					tempDir.getAbsolutePath(), configMap
							.get(GSTR6_ISD_DSITRIBUTION_SFTP_SOURCE).getValue(),
					FILE_EXT);
			if (tempDir.list().length == 0) {
				String errMsg = String.format("Temp Directory  %s is empty",
						tempDir);//add groupcode
				LOGGER.error(errMsg);
				GenUtil.deleteTempDir(tempDir);
				return;
			}
			File[] downloadedFiles = tempDir.listFiles();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Total No of files downloaded to remote directory is {}",
						downloadedFiles.length);//groupcode
			List<String> uploadedFiles = new ArrayList<>();
			for (File file : downloadedFiles) {
				LocalDateTime now = LocalDateTime.now();
				String timeMilli = DATE_TIME_FORMAT.format(now);
				String fileNameToBeUploaded = GSTConstants.ISD + HYPHEN
						+ timeMilli + HYPHEN + file.getName();
				File newFile = new File(fileNameToBeUploaded);
				
				Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				Pair<String, String> uploadFileWithFileName = DocumentUtility
						.uploadFile(newFile, GSTConstants.GSTR6_FOLDER_NAME);
				if (uploadFileWithFileName == null)
					continue;
				// saving into file status table
				Gstr1FileStatusEntity fileStatus = getFileStatusData(
						uploadFileWithFileName.getValue0(), GSTConstants.SYSTEM,
						GSTConstants.ISD, GSTConstants.INWARD,uploadFileWithFileName.getValue1());
				gstr1FileStatusRepository.save(fileStatus);

				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("fileId", fileStatus.getId());
				jsonParams.addProperty("fileName",
						uploadFileWithFileName.getValue0());
				jsonParams.addProperty("filePath",
						GSTConstants.GSTR6_FOLDER_NAME);
				jsonParams.addProperty("source", "SFTPSend");
				jsonParams.addProperty("docId",
						uploadFileWithFileName.getValue1());

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.ISDDISTRIBUTIONFILEUPLOAD,
						jsonParams.toString(), "SYSTEM", 5L, 0L, 0L);

				uploadedFiles.add(file.getName());

			}
			boolean archivedFiles = sftpService.moveFiles(uploadedFiles,
					configMap.get(GSTR6_ISD_DSITRIBUTION_SFTP_SOURCE)
							.getValue(),
					configMap.get(GSTR6_ISD_DSITRIBUTION_SFTP_DESTINATION)
							.getValue());
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
		return Files.createTempDirectory("Gstr6IsdDistributionSftpFiles")
				.toFile();
	}

	public Gstr1FileStatusEntity getFileStatusData(String fileName,
			String userName, String fileType, String dataType, String docId) {
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
		fileStatus.setDocId(docId);
		return fileStatus;
	}
}