package com.ey.advisory.controller.gstr1einvrecon;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author siva.reddy
 *
 */
@RestController
@Slf4j
public class Gstr1GSTINDeleteDataController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	@PostMapping(value = "/ui/gstr1GSTINDeleteData", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr1GSTINDeleteDataFileUpload(
			@RequestParam("file") MultipartFile file) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			LOGGER.debug(
					"Inside Gstr1GSTINDeleteData and file type is {}  "
							+ "foldername is {} ",
					"Gstr1GSTINDeleteData",
					ConfigConstants.GSTR1GSTINDELETE_DATA);

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
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();

			String fileName = gstr1FileUploadUtil.getFileName(file, "DELETE_GSTN");

			LOGGER.debug("openCmisSession:{} ", openCmisSession);
			LOGGER.debug("folderName:{} ", ConfigConstants.GSTR1GSTINDELETE_DATA);
		
			// access the root folder of the repository
			Folder root = openCmisSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Root Folder name is:{} ", root.getName());
			}

			Map<String, String> newFolderProps = new HashMap<>();
			newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			newFolderProps.put(PropertyIds.NAME, ConfigConstants.GSTR1GSTINDELETE_DATA);
			Folder createdFolder = null;
			try {
				OperationContext oc = openCmisSession.getDefaultContext();
				String query = String.format("cmis:name='%s'",
						ConfigConstants.GSTR1GSTINDELETE_DATA);
				ItemIterable<CmisObject> folders = openCmisSession
						.queryObjects("cmis:folder", query, false, oc);
				for (CmisObject obj : folders) {
					if (obj instanceof Folder) {
						Folder folder = (Folder) obj;
						if (folder.getName().equalsIgnoreCase(ConfigConstants.GSTR1GSTINDELETE_DATA)) {
							LOGGER.debug("Folder already exists");
							createdFolder = folder;
						}
					}
				}
				if (createdFolder == null) {
					LOGGER.debug("Creating Folder");
					createdFolder = root.createFolder(newFolderProps);
					LOGGER.debug("Folder Created");
				}
			} catch (CmisNameConstraintViolationException e) {
				LOGGER.error("Exception while creating folder", e);
			}
			
			// create a new file in the root folder
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties.put(PropertyIds.NAME, fileName);
			InputStream stream = null;
			String docId = null;
			if (!file.isEmpty()) {
				try {
					LOGGER.debug("File upload process start " + file);
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
					LOGGER.debug("Created document in the repo "
							+ createdDocument.getId());
					LOGGER.debug("Created document name " + "in the repo  "
							+ createdDocument.getName());
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

			String groupCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", groupCode);
			String uniqueFileName = fileName;
			if (uniqueFileName == null) {
				throw new AppException(
						"Unexpected " + "error while uploading  file");
			}

			Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

			LocalDateTime localDate = LocalDateTime.now();
			fileStatus.setFileName(fileName);
			fileStatus.setFileType("DELETE_GSTN");
			fileStatus.setUpdatedBy(userName);
			fileStatus.setUpdatedOn(localDate);
			fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
			fileStatus.setReceivedDate(localDate.toLocalDate());
			fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
			fileStatus.setDataType("GSTR1");
			fileStatus.setDocId(docId);
			fileStatus = fileStatusRepository.save(fileStatus);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileStatus.getId());
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.GSTR1GSTINDELETE_DATA);

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1_GSTIN_DELETE_DATA, jsonParams.toString(),
					userName, 1L, null, null);

			APIRespDto dto = new APIRespDto("Success",
					"File has been successfully uploaded. "
							+ "Please check the file details in File Status");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Begining from uploadDocuments:{} ");
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"File uploaded Failed " + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

}
