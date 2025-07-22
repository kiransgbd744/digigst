package com.ey.advisory.app.glrecon;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
/*
 * This default GLRecon upload represent service for uploading the files from
 * various sources reading the data from document and it will store or sent
 * excel file to Document Service
 */
@Component("GlReconFileUploadServiceImpl")
@Slf4j
public class GlReconFileUploadServiceImpl implements GlReconFileUploadService {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository GlReconFileStatusRepository;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList
			.of("application/x-tika-ooxml", "application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("xlsx");

	@Override
	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String fileType, String userName, String ruleId)
			throws Exception {
			LOGGER.error("Begining from uploadDocuments");
		Gson gson = GsonUtil.newSAPGsonInstance();
		GlReconFileStatusEntity fileStatus = null;

		try {

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
				// 180742--Gl multi user
				if (fileType.equalsIgnoreCase("GL Dump"))
					GlReconFileStatusRepository.updateFileTypeIsActive(fileType,
							userName);
				else
					GlReconFileStatusRepository
							.updateFileTypeisActive(fileType);

				Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
					LOGGER.error("openCmisSession:{} ", openCmisSession);
					LOGGER.error("folderName:{} ", folderName);

				// access the root folder of the repository
				Folder root = openCmisSession.getRootFolder();
					LOGGER.error("Root Folder name is:{} ", root.getName());
				// create a new folder
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
							if (folder.getName().equalsIgnoreCase(folderName)) {
									LOGGER.error("Folder already exists");

								createdFolder = folder;

							}
						}
					}
					if (createdFolder == null) {
							LOGGER.error("Creating Folder");
						
						createdFolder = root.createFolder(newFolderProps);
						
							LOGGER.error("Folder Created");
						
					}
				} catch (CmisNameConstraintViolationException e) {
					LOGGER.error("Exception while creating folder", e);
				}
				String id = null;
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
				properties.put(PropertyIds.NAME, fileName);
				InputStream stream = null;
				if (!file.isEmpty()) {
					try {
							LOGGER.error("File upload process start " + file);
					

						byte[] fileContent = file.getBytes();
						stream = new ByteArrayInputStream(fileContent);
						ContentStream contentStream = openCmisSession
								.getObjectFactory().createContentStream(
										fileName, fileContent.length,
										"application/vnd.ms-excel", stream);
						
							LOGGER.error("Creating document in the repo");
						

						Document createdDocument = createdFolder.createDocument(
								properties, contentStream,
								VersioningState.MINOR);
							LOGGER.error("Created document in the repo "
									+ createdDocument.getId());
							LOGGER.error(
									"Created document name " + "in the repo  "
											+ createdDocument.getName());
						
						id = createdDocument.getId();

					} catch (CmisNameConstraintViolationException e) {
						LOGGER.error(
								"Exception while creating the document in repo",
								e);
					} finally {
						if (stream != null) {
							stream.close();
						}
					}

				}

				fileStatus = getGLFileStatus(fileName, userName, fileType,
						ruleId, id);

				GlReconFileStatusRepository.save(fileStatus);
				String groupCode = TenantContext.getTenantId();

				String uniqueFileName = fileName;

				if (uniqueFileName == null) {
					throw new AppException(
							"Unexpected " + "error while uploading  file");
				}

				if (fileType.equalsIgnoreCase("GL Dump")) {
					JsonObject jsonParams = new JsonObject();
					jsonParams.addProperty("fileId", fileStatus.getId());
					jsonParams.addProperty("fileName", uniqueFileName);
					jsonParams.addProperty("filePath", folderName);
					jsonParams.addProperty("source",
							JobStatusConstants.WEB_UPLOAD);
					jsonParams.addProperty("docId", id);
					asyncJobsService.createJob(groupCode,
							JobConstants.GLRECON_DUMP_FILEUPLOAD,
							jsonParams.toString(), userName, 1L, null, null);

				}
				// GlReconFileStatusRepository.updateFileStatus(fileStatus.getId(),
				// "Success", id);

				APIRespDto dto = new APIRespDto("Success",
						"File uploaded successfully");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				
					LOGGER.error("Begining from uploadDocuments:{} ");
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			APIRespDto dto = new APIRespDto("Failed", "File uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Invalid File Content, "
					+ "File Content is not of type Multipart";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(),
					HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"File uploaded Failed" + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);

			GlReconFileStatusRepository.updateFileStatus(fileStatus.getId(),
					"Failed", null);

			LOGGER.error("Exception Occured:{}", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	public GlReconFileStatusEntity getGLFileStatus(String fileName,
			String userName, String fileType, String ruleId, String docId) {
		GlReconFileStatusEntity fileStatus = new GlReconFileStatusEntity();

		LocalDateTime localDate = LocalDateTime.now();
		fileStatus.setFileName(fileName);
		fileStatus.setFileType(fileType);
		fileStatus.setCraetedBy(userName);
		fileStatus.setUpdatedOn(localDate);
		fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
		if(ruleId == null || ruleId.isEmpty()){
			fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
		} else {
			fileStatus.setSource("SFTP");
		}
		fileStatus.setDocId(docId);
		fileStatus.setIsDelete(false);

		fileStatus.setIsActive(true);

		return fileStatus;
	}
}