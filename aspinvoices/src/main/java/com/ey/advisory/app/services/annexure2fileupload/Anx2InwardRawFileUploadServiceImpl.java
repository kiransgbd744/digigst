package com.ey.advisory.app.services.annexure2fileupload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.DatatypeConverter;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Anx2InwardRawFileUploadServiceImpl")
public class Anx2InwardRawFileUploadServiceImpl
		implements Anx2InwardRawFileUploadService {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Anx2FileUploadJobInsertion")
	private Anx2FileUploadJobInsertion anx2FileUploadJobInsertion;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2InwardRawFileUploadServiceImpl.class);

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	@Override
	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType, String source) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			for (MultipartFile file : files) {

				LOGGER.debug("About to get mimeType at ::{}",
						LocalDateTime.now());

				String mimeType = GenUtil.getMimeType(file.getInputStream());
				LOGGER.debug("Got mimeType {} at::{} ", mimeType,
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

				LOGGER.debug("Uploaded filename {} and ext {} at::{} ",
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
				FileStatusPerfUtil.logEvent(
						"INWARD_RAW_OPNE_CMIS_SESSION_BEGIN", uniqueName);
				Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
				FileStatusPerfUtil.logEvent("INWARD_RAW_OPNE_CMIS_SESSION_END",
						uniqueName);
				// access the root folder of the repository
				Folder root = openCmisSession.getRootFolder();
				LOGGER.debug("Root Folder name is {}", root.getName());
				
				String docId = null;
				Gstr1FileStatusEntity fileStatus = null;
				// create a new folder
				FileStatusPerfUtil.logEvent("INWARD_RAW_NEW_FOLDER_BEGIN",
						uniqueName);
				Map<String, String> newFolderProps = new HashMap<>();
				newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
				newFolderProps.put(PropertyIds.NAME, folderName);
				Folder createdFolder = null;
				try {
					OperationContext oc = openCmisSession.getDefaultContext();
					String query = String.format("cmis:name='%s'", folderName);
					ItemIterable<CmisObject> folders = openCmisSession
							.queryObjects("cmis:folder", query, false, oc);
					for (CmisObject obj : folders) {
						if (obj instanceof Folder) {
							Folder folder = (Folder) obj;
							LOGGER.debug("Folder Name1 {}", folder.getName());
							LOGGER.debug("Folder Name2 {}", folderName);
							if (folder.getName().equalsIgnoreCase(folderName)) {
								LOGGER.debug("Folder already exists");
								createdFolder = folder;
							}
						}
					}
					if (createdFolder == null) {
						createdFolder = root.createFolder(newFolderProps);
						LOGGER.debug("Folder Created");
					}
				} catch (CmisNameConstraintViolationException e) {
					LOGGER.error("Exception while creating folder", e);
				}
				FileStatusPerfUtil.logEvent("INWARD_RAW_NEW_FOLDER_END",
						uniqueName);
				// create a new file in the root folder
				FileStatusPerfUtil.logEvent("INWARD_RAW_NEW_FILE_BEGIN",
						uniqueName);
				Map<String, Object> properties = new HashMap<>();
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
				properties.put(PropertyIds.NAME, fileName);
				InputStream stream = null;
				if (!file.isEmpty()) {
					try {
						byte[] fileContent = file.getBytes();
						stream = new ByteArrayInputStream(fileContent);
						ContentStream contentStream = openCmisSession
								.getObjectFactory().createContentStream(
										fileName, fileContent.length,
										"application/vnd.ms-excel", stream);

						LOGGER.debug("Creating document in the repo");

						Document createdDocument = createdFolder.createDocument(
								properties, contentStream,
								VersioningState.MAJOR);
						
						docId = createdDocument.getId();
						
						LOGGER.debug("Created document in the repo {}",
								createdDocument.getId());
						LOGGER.debug("Created document name {} in the repo  ",
								createdDocument.getName());
					} catch (CmisNameConstraintViolationException e) {
						LOGGER.error(
								"Exception while creating the document in repo",
								e);
					} finally {
						if (stream != null) {
							stream.close();
						}
					}

					if (source != null && source.equalsIgnoreCase(
							JobStatusConstants.SFTP_WEB_UPLOAD)) {
						fileStatus = gstr1FileUploadUtil
								.getSftpFileStatus(fileName, userName, null,
										fileType, dataType, null);
						fileStatus.setDocId(docId);
						fileStatus.setTransformationStatus("No Transformation");
						fileStatus= gstr1FileStatusRepository.save(fileStatus);
					} else {
						
						String fileHash = null;
					if (fileType.equalsIgnoreCase(GSTConstants.COMPREHENSIVE_INWARD_RAW)) {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Inside fileHash comparison "
									+ file + "time" + LocalDateTime.now());
						}
						MessageDigest md = MessageDigest.getInstance("MD5");
						md.update(file.getBytes());
						byte[] digest = md.digest();
						fileHash = DatatypeConverter.printHexBinary(digest)
								.toUpperCase();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Created fileHash ->" + fileHash
									+ "for file ->" + file + "time->"
									+ LocalDateTime.now());
						}
						List<Long> logicalStateFile = gstr1FileStatusRepository
								.getLogicalFileIds(fileHash);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Already Existing logical state files"
											+ logicalStateFile.toString());
						}
						if (!logicalStateFile.isEmpty()) {
							String msg = "File processing already in progress.";
							APIRespDto dto = new APIRespDto("Failed", msg);
							JsonObject resp = new JsonObject();
							JsonElement respBody = gson.toJsonTree(dto);
							resp.add("hdr",gson.toJsonTree(APIRespDto.creatErrorResp()));
							resp.add("resp", respBody);
							LOGGER.error(msg);
							return new ResponseEntity<>(resp.toString(),
									HttpStatus.OK);
						}
						fileStatus = gstr1FileUploadUtil
								.getGstr1FileStatusFor2bpr(fileName,
										userName, null, fileType, dataType,
										null, fileHash);
						fileStatus.setDocId(docId);
						fileStatus.setTransformationStatus("No Transformation");
						fileStatus = gstr1FileStatusRepository.save(fileStatus);
					}else{
						fileStatus = gstr1FileUploadUtil
								.getGstr1FileStatus(fileName, userName, null,
										fileType, dataType, null);
						fileStatus.setDocId(docId);
						fileStatus = gstr1FileStatusRepository.save(fileStatus);
					}
					}
					FileStatusPerfUtil.logEvent("INWARD_RAW_NEW_FILE_END",
							uniqueName);
					String paramJson = "{\"filePath\":\"" + createdFolder.getName() + "\","
			                  + "\"fileName\":\"" + fileName + "\","
			                  + "\"docId\":\"" + docId + "\",\"fileId\":\"" + fileStatus.getId() + "\"}";

					if (uniqueName.equalsIgnoreCase(
							GSTConstants.ANN2_RAW_FILE_NAME)) {
						String jobCategory = JobConstants.ANX2INWARDRAWFILEUPLOAD;
						FileStatusPerfUtil.logEvent(
								"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_BEGIN",
								uniqueName);
						anx2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
						FileStatusPerfUtil.logEvent(
								"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_END",
								uniqueName);
						FileStatusPerfUtil.logEvent(
								"Anx2InwardRawFileUploadServiceImpl_INWARD_RAW_FILE_UPLOAD_JOB_END");
					}
					if (uniqueName.equalsIgnoreCase(GSTConstants.INWARD)) {
						String jobCategory = JobConstants.INWARD_FILE_UPLOADS;
						FileStatusPerfUtil.logEvent(
								"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_BEGIN",
								uniqueName);
						anx2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
						FileStatusPerfUtil.logEvent(
								"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_END",
								uniqueName);
						FileStatusPerfUtil.logEvent(
								"Anx2InwardRawFileUploadServiceImpl_INWARD_RAW_FILE_UPLOAD_JOB_END");
					}
					if (uniqueName.equalsIgnoreCase(GSTConstants.CROSS_ITC)) {
						String jobCategory = JobConstants.CROSS_FILE_UPLOADS;
						FileStatusPerfUtil.logEvent(
								"CROSS_ITC_FILE_UPLOAD_JOB_CREATION_BEGIN",
								uniqueName);
						anx2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
						FileStatusPerfUtil.logEvent(
								"CROSS_ITC_FILE_UPLOAD_JOB_CREATION_END",
								uniqueName);
						FileStatusPerfUtil.logEvent(
								"Anx2InwardRawFileUploadServiceImpl_CROSS_ITC_FILE_UPLOAD_JOB_END");

					}

					FileStatusPerfUtil.logEvent(
							"INWARD_RAW_FILE_STATUS_INSERT_BEGIN", uniqueName);
					FileStatusPerfUtil.logEvent(
							"INWARD_RAW_FILE_STATUS_INSERT_END", uniqueName);
				}
			}

			APIRespDto dto = new APIRespDto("Success",
					"File has been successfully uploaded. "
							+ "Please check the file details in File Status");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Inward raw file uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading inward raw files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	
	
		
}
