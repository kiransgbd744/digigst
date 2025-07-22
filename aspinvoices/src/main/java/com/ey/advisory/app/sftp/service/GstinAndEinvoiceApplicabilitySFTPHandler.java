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

import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;


@Service("GstinAndEinvoiceApplicabilitySFTPHandler")
@Slf4j
public class GstinAndEinvoiceApplicabilitySFTPHandler {

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;
	
	@Autowired
	@Qualifier("GstinAndEinvoiceApplicabilitySFTPServiceImpl")
	private GstinAndEinvoiceApplicabilitySFTPService gstinValidatorSftpService;
	
	private static final String HYPHEN = "_";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository gstr1FileStatusRepository;
	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	private static final String GSTINVALIDATOR_SFTP_SOURCE = "ey.internal.gstinvalidator.sftp.source";

	private static final String GSTINVALIDATOR_SFTP_DESTINATION = "ey.internal.gstinvalidator.sftp.destination";

	private static final List<String> FILE_EXT = ImmutableList
			.of(JobStatusConstants.XLSX_TYPE);

	
	public void sftpFilesDownload() {
		
		File tempDir = null;
		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.gstinvalidator.sftp");

			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("temp directory name : %s", tempDir);
				LOGGER.debug(msg);
			}
			sftpService.downloadFiles(
					tempDir.getAbsolutePath(), configMap
							.get(GSTINVALIDATOR_SFTP_SOURCE).getValue(),
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
			//MultipartFile[] files=null;
			//String isEinvApplicable=null;
			
			for (File file : downloadedFiles) {
				LocalDateTime now = LocalDateTime.now();
				String timeMilli = DATE_TIME_FORMAT.format(now);
				String arr[] = file.getName().split("\\.");
				String fileNameToBeUploaded =arr[0] + HYPHEN + timeMilli + "."+ arr[1];
				
				File newFile = new File(fileNameToBeUploaded);
				Files.move(file.toPath(), newFile.toPath(),
						StandardCopyOption.REPLACE_EXISTING);

				/*
				 * String uploadFileWithFileName = DocumentUtility
				 * .uploadFileWithFileName(file, folderName,
				 * fileNameToBeUploaded);
				 */Pair<String, String> uploadFileWithFileName = DocumentUtility
						.uploadFile(newFile, GSTConstants.GSTIN_VALIDATOR_UPLOAD_FOLDER);
				 
				 /*
				String uploadFileWithFileName = DocumentUtility
						.uploadFileWithFileName(file,
								GSTConstants.GSTIN_VALIDATOR_UPLOAD_FOLDER,
								fileNameToBeUploaded);
				*/if (uploadFileWithFileName == null)
					continue;
				
			gstinValidatorSftpService.gstinValidSftpFileUploads(newFile);
			
			uploadedFiles.add(file.getName());
			}
			boolean archivedFiles = sftpService.moveFiles(uploadedFiles,
					configMap.get(GSTINVALIDATOR_SFTP_SOURCE)
							.getValue(),
					configMap.get(GSTINVALIDATOR_SFTP_DESTINATION)
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
		return Files.createTempDirectory("GSTIN_E-invoiceApplicability")
				.toFile();
	}

}