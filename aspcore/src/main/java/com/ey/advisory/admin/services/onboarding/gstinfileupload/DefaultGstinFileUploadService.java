package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.services.gstin.jobs.GSTinConstants;
import com.ey.advisory.admin.services.gstin.jobs.GstinJobConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
/*
 * This default GSTIN upload represent service for uploading the files from
 * various sources reading the data from document and it will store or sent
 * excel file to Document Service
 */
@Component("DefaultGstinFileUploadService")
public class DefaultGstinFileUploadService implements GstinFileUploadService {

	@Autowired
	@Qualifier("GstinFileUploadUtil")
	private GstinFileUploadUtil gstinFileUploadUtil;

	@Autowired
	@Qualifier("GstinFileUploadJobInsertion")
	private GstinFileUploadJobInsertion gstinFileUploadJobInsertion;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGstinFileUploadService.class);

	@Override
	public ResponseEntity<String> uploadDocuments(MultipartFile[] files, String folderName, String uniqueName)
			throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			for (MultipartFile file : files) {
				String fileName = gstinFileUploadUtil.getFileName(file);
				Session openCmisSession = gstinFileUploadUtil.getCmisSession();
				// access the root folder of the repository
				Folder root = openCmisSession.getRootFolder();
				LOGGER.debug("Root Folder name is ", root.getName());

				// create a new folder
				Map<String, String> newFolderProps = new HashMap<String, String>();
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
								ItemIterable<CmisObject> children = folder.getChildren();
								for (CmisObject childDoc : children) {
									LOGGER.debug("Sub Folders :" + childDoc.getName());
								}
								createdFolder = folder;
								 break; // Exit loop as folder is found
							} /*else {
								LOGGER.error("Creating Folder");
								createdFolder = root.createFolder(newFolderProps);
								LOGGER.error("Created Folder");
							}*/
						}
					}
					  // If folder is not found, create it
				    if (createdFolder == null) {
				        LOGGER.error("Folder not found, creating a new folder.");
				        createdFolder = root.createFolder(newFolderProps);
				        LOGGER.debug("Created new folder: " + folderName);
				    }
				} catch (CmisNameConstraintViolationException e) {
					LOGGER.error("Exception while creating folder", e);
				}
				// Ensure createdFolder is not null before proceeding
				if (createdFolder == null) {
				    throw new RuntimeException("Failed to find or create the folder: " + folderName);
				}

				// create a new file in the root folder
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
				properties.put(PropertyIds.NAME, fileName);
				InputStream stream = null;
				if (!file.isEmpty()) {
					try {
						byte[] fileContent = file.getBytes();
						stream = new ByteArrayInputStream(fileContent);
						ContentStream contentStream = openCmisSession.getObjectFactory().createContentStream(fileName,
								fileContent.length, "application/vnd.ms-excel", stream);

						LOGGER.debug("Creating document in the repo");

						Document createdDocument = createdFolder.createDocument(properties, contentStream,
								VersioningState.MAJOR);

						LOGGER.debug("Created document in the repo " + createdDocument.getId());
						LOGGER.debug("Created document name " + "in the repo " + createdDocument.getName());
					} catch (CmisNameConstraintViolationException e) {
						LOGGER.error("Exception while creating the document in repo", e);
					} finally {
						if (stream != null) {
							stream.close();
						}
					}

					String paramJson = "{\"filePath\":\"" + createdFolder.getName() + "\"," + "\"fileName\":\""
							+ fileName + "\"}";

					if (uniqueName.equalsIgnoreCase(GSTinConstants.ELREGISTRATION_FILE_NAME)) {
						String jobCategory = GstinJobConstants.ELREGISTRAION;
						gstinFileUploadJobInsertion.elRegitrationFileUpload(paramJson, jobCategory);
					} else if (uniqueName.equalsIgnoreCase(GSTinConstants.ELENTITLEMENT1_FILE_NAME)) {
						String jobCategory = GstinJobConstants.ELENTITLEMENT1;
						gstinFileUploadJobInsertion.elEntitlement1FileUpload(paramJson, jobCategory);
					} else if (uniqueName.equalsIgnoreCase(GSTinConstants.ELENTITLEMENT2_FILE_NAME)) {
						String jobCategory = GstinJobConstants.ELENTITLEMENT2;
						gstinFileUploadJobInsertion.elEntitlement1FileUpload(paramJson, jobCategory);
					} else if (uniqueName.equalsIgnoreCase(GSTinConstants.USERCREATION_FILE_NAME)) {
						String jobCategory = GstinJobConstants.USERCREATION;
						gstinFileUploadJobInsertion.elEntitlement1FileUpload(paramJson, jobCategory);
					}
				}
			}

			/*
			 * try { String uploadDir = "C:/DataFiles/"+folderName; File
			 * fileDirectory = new File(uploadDir + fileName);
			 * LOGGER.debug("fileDirectory---------------------" + uploadDir +
			 * "-->" + fileDirectory); file.transferTo(fileDirectory); } catch
			 * (Exception e) { LOGGER.error("File to copy the file::"+ e); } }
			 */

			APIRespDto dto = new APIRespDto("Sucess", "File uploaded successfully");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", "File uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}
	}
}
