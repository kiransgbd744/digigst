package com.ey.advisory.app.dms;

import static com.ey.advisory.common.GSTConstants.RAW_FILE_NAME;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.azure.fileshare.utils.AzureFileShareUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Component("DmsGstr1FileUploadServiceImpl")
@Slf4j
public class DmsGstr1FileUploadServiceImpl {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	@Autowired
	@Qualifier("AzureFileShareUtil")
	private AzureFileShareUtil azureFileShare;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository; 
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;
	
	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glReconFileStatusRepository;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType, String source,
			String transformationRule) throws Exception {
		
			LOGGER.error("Begining from uploadDocuments");
		
		Long fileId = null;
		File tempDir = createTempDir();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			
			String uuid = DmsUtils.generateUUID();
			for (MultipartFile file : files) {

				LOGGER.error("About to get mimeType at ::{}",
						LocalDateTime.now());

				String mimeType = GenUtil.getMimeType(file.getInputStream());
				LOGGER.error("Got mimeType {} at::{} ", mimeType,
						LocalDateTime.now());

				if (mimeType != null && !CSV_CONTENT_TYPE.contains(mimeType)) {
					String msg = "Invalid content in the uploaded file";
					APIRespDto dto = new APIRespDto("Failed", msg);
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					LOGGER.error(msg);
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}

				String ext = GenUtil.getFileExt(file);

				LOGGER.error("Uploaded filename {} and ext {} at::{} ",
						file.getOriginalFilename(), ext, LocalDateTime.now());

				if (!EXT_LIST.contains(ext)) {
					String msg = "Invalid file type.";
					APIRespDto dto = new APIRespDto("Failed", msg);
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					LOGGER.error(msg);
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}

				String fileName = gstr1FileUploadUtil.getFileName(file,
						fileType);
				// Extract rule name from the file name
				Gstr1FileStatusEntity fileStatus = null;
				String transformationRuleName="";
				String errorDesc = null;
				if (source.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					String[] fileParts = file.getOriginalFilename().split("_", 3);
					if (fileParts.length < 3) {
						errorDesc = "Incorrect File Name";
						fileStatus = gstr1FileUploadUtil.getDmsGstr1FileStatus(
								fileName, userName, null, fileType, dataType, null,
								source, transformationRule,
								"Transformation Failed",transformationRuleName,errorDesc);	
						gstr1FileStatusRepository.save(fileStatus);
						fileId = fileStatus.getId();
						gstr1FileStatusRepository.updateFileStatus(fileId, "Failed");
						throw new AppException("Incorrect File Name. File name should be in \"MAP_RuleName_FileName\" format.");
					}
					transformationRuleName = fileParts[1].toLowerCase();
				}

				
				// upload the file here
				File convertedFile = convertMultipartFileToFile(file);
				String uploadedFileName = azureFileShare
						.uploadFileToAzure(convertedFile, fileName, uuid);
				
				LOGGER.error("File uploaded to Azure File Share {} ", uploadedFileName);


				fileStatus = gstr1FileUploadUtil.getDmsGstr1FileStatus(
						fileName, userName, null, fileType, dataType, null,
						source, transformationRule,
						"Transformation in progress",transformationRuleName,errorDesc);
				
				fileStatus.setUuid(uuid);
				gstr1FileStatusRepository.save(fileStatus);
				fileId = fileStatus.getId();

				String paramJson = null;
				String jobName = GSTConstants.DMS_FILE_UPLOAD;

				if (uniqueName.equalsIgnoreCase(RAW_FILE_NAME)) {
					if (fileType
							.equalsIgnoreCase(GSTConstants.COMPREHENSIVE_RAW)) {
						PerfUtil.logEventToFile(
								PerfamanceEventConstants.FILE_PROCESSING,
								"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_START",
								"DmsGstr1FileUploadServiceImpl",
								"uploadDocuments", fileName);
						String jobCategory = GSTConstants.EINVOICEFILEUPLOAD;
						paramJson = "{\"filePath\":\"" + folderName + "\","
								+ "\"fileId\":\"" + fileStatus.getId() + "\","
								+ "\"fName\":\"" + fileName + "\","
								+ "\"jobCateg\":\"" + jobCategory + "\"}";

						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobName, userName);
						PerfUtil.logEventToFile(
								PerfamanceEventConstants.FILE_PROCESSING,
								"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_END",
								"DmsGstr1FileUploadServiceImpl",
								"uploadDocuments", fileName);
					} else if (fileType.equalsIgnoreCase(
							GSTConstants.COMPREHENSIVE_RAW_1A)) {
						PerfUtil.logEventToFile(
								PerfamanceEventConstants.FILE_PROCESSING,
								"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_START",
								"DmsGstr1FileUploadServiceImpl",
								"uploadDocuments", fileName);
						String jobCategory = GSTConstants.GSTR1AEINVOICEFILEUPLOAD;
						paramJson = "{\"filePath\":\"" + folderName + "\","
								+ "\"fileId\":\"" + fileStatus.getId() + "\","
								+ "\"fName\":\"" + fileName + "\","
								+ "\"jobCateg\":\"" + jobCategory + "\"}";
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobName, userName);
						PerfUtil.logEventToFile(
								PerfamanceEventConstants.FILE_PROCESSING,
								"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_END",
								"DmsGstr1FileUploadServiceImpl",
								"uploadDocuments", fileName);
					}
				} else if (uniqueName.equalsIgnoreCase(GSTConstants.INWARD)) {
					String jobCategory = JobConstants.INWARD_FILE_UPLOADS;
					FileStatusPerfUtil.logEvent(
							"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_BEGIN",
							uniqueName);
					paramJson = "{\"filePath\":\"" + folderName + "\","
							+ "\"fileId\":\"" + fileStatus.getId() + "\","
							+ "\"fName\":\"" + fileName + "\","
							+ "\"jobCateg\":\"" + jobCategory + "\"}";
					gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
							jobName, userName);
					FileStatusPerfUtil.logEvent(
							"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_END",
							uniqueName);
					FileStatusPerfUtil.logEvent(
							"DmsGstr1FileUploadServiceImpl_INWARD_RAW_FILE_UPLOAD_JOB_END");
				}
			}
			APIRespDto dto = new APIRespDto("Success",
					"File has been successfully uploaded. "
							+ "Please check the file details in File Status");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			
				LOGGER.error("Begining from uploadDocuments:{} ");
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			
			gstr1FileStatusRepository.updateTransformationStatus(fileId,
					"Transformation Failed");
			gstr1FileStatusRepository.updateFileStatus(fileId, "Failed");
			
			APIRespDto dto = new APIRespDto("Failed",
					"File uploaded Failed" + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error("Exception Occured:{}", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	public static File createTempDir() throws IOException {
		return Files.createTempDirectory("FileHashFolder").toFile();
	}

	public static File convertMultipartFileToFile(MultipartFile multipartFile)
			throws IOException {
		File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/"
				+ multipartFile.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(multipartFile.getBytes());
		}
		return convertedFile;
	}

	public static void deleteTempFile(File file) {
		if (file != null && file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				LOGGER.error("Temporary file deleted: {}",
						file.getAbsolutePath());
			} else {
				LOGGER.error(
						"Error occured while deleting Temporary file " + ": {}",
						file.getAbsolutePath());
			}
		}
	}
	
	
	public ResponseEntity<String> uploadGlDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType, String source,
			String transformationRule) throws Exception {
		
			LOGGER.error("Begining from uploadDocuments");
		
		Long fileId = null;
		File tempDir = createTempDir();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			
			String uuid = DmsUtils.generateUUID();
			for (MultipartFile file : files) {

				LOGGER.error("About to get mimeType at ::{}",
						LocalDateTime.now());

				String mimeType = GenUtil.getMimeType(file.getInputStream());
				LOGGER.error("Got mimeType {} at::{} ", mimeType,
						LocalDateTime.now());

				if (mimeType != null && !CSV_CONTENT_TYPE.contains(mimeType)) {
					String msg = "Invalid content in the uploaded file";
					APIRespDto dto = new APIRespDto("Failed", msg);
					resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					LOGGER.error(msg);
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}

				String ext = GenUtil.getFileExt(file);

				LOGGER.error("Uploaded filename {} and ext {} at::{} ",
						file.getOriginalFilename(), ext, LocalDateTime.now());

				if (!EXT_LIST.contains(ext)) {
					String msg = "Invalid file type.";
					APIRespDto dto = new APIRespDto("Failed", msg);
					resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					LOGGER.error(msg);
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}

				String fileName = gstr1FileUploadUtil.getFileName(file,
						fileType);
				// Extract rule name from the file name
				GlReconFileStatusEntity fileStatus = null;
				String transformationRuleName="";
				String errorDesc = null;
				if (source.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					MultipartFile file1 = files[0];
					String fileName1 = FilenameUtils.getName(file1.getOriginalFilename());
					boolean isDmsFile = fileName1.toLowerCase().startsWith("map_");
					String[] fileParts = file.getOriginalFilename().split("_", 3);
					if (fileParts.length < 3 || !(isDmsFile)) {
						errorDesc = "Incorrect File Name";
						
						fileStatus = new GlReconFileStatusEntity();
						 
						LocalDateTime localDate = LocalDateTime.now();
						fileStatus.setFileName(fileName);
						fileStatus.setFileType(fileType);
						fileStatus.setCraetedBy(userName);
						fileStatus.setUpdatedOn(localDate);
						fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
						fileStatus.setSource(source);
						fileStatus.setTransformationRule(transformationRule);
						fileStatus.setTransformationStatus("Transformation Failed");
						fileStatus.setTransformationRuleName(transformationRuleName);
						fileStatus.setIsActive(true);
						fileStatus.setPayloadId(uuid);

						fileId = fileStatus.getId();
						gstr1FileStatusRepository.updateFileStatus(fileId, "Failed");
						throw new AppException("Incorrect File Name. File name should be in \"MAP_RuleName_FileName\" format.");
					}
					transformationRuleName = fileParts[1].toLowerCase();
				}

				
				// upload the file here
				File convertedFile = convertMultipartFileToFile(file);
				String uploadedFileName = azureFileShare
						.uploadFileToAzure(convertedFile, fileName, uuid);
				
				LOGGER.error("File uploaded to Azure File Share {} ", uploadedFileName);


				fileStatus = new GlReconFileStatusEntity();
				 
				LocalDateTime localDate = LocalDateTime.now();
				fileStatus.setFileName(fileName);
				fileStatus.setFileType(fileType);
				fileStatus.setCraetedBy(userName);
				fileStatus.setUpdatedOn(localDate);
				fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
				fileStatus.setSource(source);
				fileStatus.setTransformationRule(transformationRule);
				fileStatus.setTransformationStatus("Transformation in progress");
				fileStatus.setTransformationRuleName(transformationRuleName);
				fileStatus.setIsActive(true);

				fileId = fileStatus.getId();
				
				
				fileStatus.setPayloadId(uuid);
				glReconFileStatusRepository.save(fileStatus);
				fileId = fileStatus.getId();

				String paramJson = null;
				String jobName = GSTConstants.DMS_FILE_UPLOAD;

				String jobCategory = JobConstants.GLRECON_DUMP_FILEUPLOAD;
				paramJson = "{\"filePath\":\"" + folderName + "\","
						+ "\"fileId\":\"" + fileStatus.getId() + "\","
						+ "\"fileName\":\"" + fileName + "\","
						+ "\"jobCateg\":\"" + jobCategory + "\"}";
				gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
						jobName, userName);

			APIRespDto dto = new APIRespDto("Success",
					"File has been successfully uploaded. "
							+ "Please check the file details in File Status");
			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			
				LOGGER.error("Begining from uploadDocuments:{} ");
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			
			gstr1FileStatusRepository.updateTransformationStatus(fileId,
					"Transformation Failed");
			gstr1FileStatusRepository.updateFileStatus(fileId, "Failed");
			
			APIRespDto dto = new APIRespDto("Failed",
					"File uploaded Failed" + e.getMessage());
			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error("Exception Occured:{}", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			GenUtil.deleteTempDir(tempDir);
			
		}
		
	}
}