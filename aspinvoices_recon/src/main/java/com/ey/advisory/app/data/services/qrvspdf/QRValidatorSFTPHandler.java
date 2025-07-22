package com.ey.advisory.app.data.services.qrvspdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Saif.S
 *
 */

@Slf4j
@Service("QRValidatorSFTPHandler")
public class QRValidatorSFTPHandler {

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	QRUploadFileStatusRepo qrUploadFileStatusRepo;
	
	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPemtRepo;

	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	private static final String QR_VALIDATOR_SFTP_SOURCE = "ey.internal.qrvalidator.sftp.source";

	private static final String QR_VALIDATOR_SFTP_DESTINATION = "ey.internal.qrvalidator.sftp.destination";

	private static final List<String> FILE_EXT = ImmutableList
			.of(QRCodeValidatorConstants.PDF, QRCodeValidatorConstants.ZIP);

	public void sftpFilesDownload() {
		File tempDir = null;
		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.qrvalidator.sftp");

			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("temp directory name : %s", tempDir);
				LOGGER.debug(msg);
			}
			sftpService.downloadFiles(tempDir.getAbsolutePath(),
					configMap.get(QR_VALIDATOR_SFTP_SOURCE).getValue(),
					FILE_EXT);

			if (tempDir.list().length == 0) {
				String errMsg = String.format(
						"No files were taken from "
								+ "sftp source folder. Temp Directory  %s is empty",
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
			
			String groupCode = TenantContext.getTenantId();
			String optedAns="";
			
			if(!Strings.isNullOrEmpty(groupCode)){
				optedAns=groupConfigPemtRepo.findAnswerForQRCodeValidator(groupCode);
			}else{
				LOGGER.error("GroupCode must not be empty");
				return;
			}
			
			for (File file : downloadedFiles) {
				LocalDateTime now = LocalDateTime.now();
				String timeMilli = DATE_TIME_FORMAT.format(now);

				String uploadedFileName = FilenameUtils
						.removeExtension(file.getName());

				String extension = FilenameUtils.getExtension(file.getName());

				String fileNameToBeUploaded = uploadedFileName + "_" + timeMilli
						+ "." + extension;
				String folderName = extension
						.equalsIgnoreCase(QRCodeValidatorConstants.PDF)
								? QRCodeValidatorConstants.PDF_FOLDER
								: QRCodeValidatorConstants.ZIP_FOLDER;
				
				File newFile = new File(fileNameToBeUploaded);
				Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				/*String uploadFileWithFileName = DocumentUtility
						.uploadFileWithFileName(file, folderName,
								fileNameToBeUploaded);
				*/Pair<String,String> uploadFileWithFileName = DocumentUtility.uploadFile(newFile, folderName);
				
				LOGGER.debug("Transferred Successfully");

				if (uploadFileWithFileName == null)
					continue;
				// saving into file status table
				QRUploadFileStatusEntity entity = getFileStatusData(
						fileNameToBeUploaded, uploadFileWithFileName.getValue0(),optedAns);
				entity.setDocId(uploadFileWithFileName.getValue1());

				entity = qrUploadFileStatusRepo.save(entity);

				Long id = entity.getId();

				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("id", id);
				jobParams.addProperty("fileType", extension);
				jobParams.addProperty("optedAns", optedAns);
				jobParams.addProperty("uploadType", "sftpUpload");
				


				asyncJobsService.createJob(groupCode,
						JobConstants.QR_CODE_VALIDATOR, jobParams.toString(),
						"SYSTEM", 1L, null, null);

				uploadedFiles.add(file.getName());

			}
			boolean archivedFiles = sftpService.moveFiles(uploadedFiles,
					configMap.get(QR_VALIDATOR_SFTP_SOURCE).getValue(),
					configMap.get(QR_VALIDATOR_SFTP_DESTINATION).getValue());

			if (!archivedFiles) {
				LOGGER.error("Error while moving the files.");
			}
		} catch (Exception ex) {
			String msg = " Unexpected error occured while "
					+ " moving QR Validator files from sftp to cloud";
			LOGGER.error(msg, ex);

		} finally {
			if (tempDir != null) {
				GenUtil.deleteTempDir(tempDir);
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("QRValidatorSftpFiles").toFile();
	}

	public QRUploadFileStatusEntity getFileStatusData(String fileName,
			String uploadedDocName, String optedAns) {
		QRUploadFileStatusEntity entity = new QRUploadFileStatusEntity();
		entity.setUploadedBy("SFTP");
		entity.setDateOfUpload(LocalDate.now());
		entity.setFileName(fileName);
		entity.setFileStatus(QRCodeValidatorConstants.UPLOADED);// UPLOADED
		entity.setFilePath(uploadedDocName);
		entity.setCreatedBy("SYSTEM");
		entity.setCreatedOn(LocalDateTime.now());
		entity.setSource("SFTP");
		entity.setIsReverseInt(false);
		entity.setOptionOpted(optedAns);
		return entity;

	}
}
