package com.ey.advisory.common;

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

import com.ey.advisory.app.data.entities.client.Gstr2FileStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2FileStatusRepository;
import com.ey.advisory.app.util.Gstr2FileUploadUtil;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Anand3.M This default GSTR2 upload represent service for uploading
 *         the files from various sources reading the data from document and it
 *         will store or sent excel file to Document Service
 *
 */
@Component("DefaultGstr2FileUploadService")
public class DefaultGstr2FileUploadService implements Gstr2FileUploadService {

	@Autowired
	@Qualifier("Gstr2FileUploadUtil")
	private Gstr2FileUploadUtil gstr2FileUploadUtil;

	@Autowired
	@Qualifier("Gstr2FileUploadJobInsertion")
	private Gstr2FileUploadJobInsertion gstr2FileUploadJobInsertion;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("Gstr2FileStatusRepository")
	private Gstr2FileStatusRepository gstr2FileStatusRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultGstr2FileUploadService.class);

	@Override
	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName)
			throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			for (MultipartFile file : files) {
				String fileName = gstr2FileUploadUtil.getFileName(file);
				Session openCmisSession = gstr2FileUploadUtil.getCmisSession();
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
					/*for (CmisObject obj : folders) {
						if (obj instanceof Folder) {
							Folder folder = (Folder) obj;
							if (folder.getName().equalsIgnoreCase(folderName)) {
								ItemIterable<CmisObject> children = folder
										.getChildren();
								for (CmisObject childDoc : children) {
									LOGGER.debug("Sub Folders :"
											+ childDoc.getName());
								}
								createdFolder = folder;
							} else {
								LOGGER.error("Creating Folder");
								createdFolder = root
										.createFolder(newFolderProps);
								LOGGER.error("Created Folder");
							}
						}
					}*/
					for (CmisObject obj : folders) {
						if (obj instanceof Folder) {
							Folder folder = (Folder) obj;
							LOGGER.debug("Folder Name  1" + folder.getName());
							LOGGER.debug("Folder Name  2" + folderName);
							if (folder.getName().equalsIgnoreCase(folderName)) {
								LOGGER.debug("Folder already exists");
								createdFolder = folder;
							}
						}
					}
					if (createdFolder != null) {
						LOGGER.debug("Creating Folder");
						createdFolder = root.createFolder(newFolderProps);
						LOGGER.debug("Folder Created");
					}
				} catch (CmisNameConstraintViolationException e) {
					LOGGER.error("Exception while creating folder", e);
				}

				if (createdFolder == null) {
					LOGGER.info("Creating Folder");
					createdFolder = root.createFolder(newFolderProps);
					LOGGER.info("Created Folder");
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
						ContentStream contentStream = openCmisSession
								.getObjectFactory().createContentStream(
										fileName, fileContent.length,
										"application/vnd.ms-excel", stream);

						LOGGER.debug("Creating document in the repo");

						Document createdDocument = createdFolder.createDocument(
								properties, contentStream,
								VersioningState.MAJOR);

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

					String paramJson = "{\"filePath\":\""
							+ createdFolder.getName() + "\","
							+ "\"fileName\":\"" + fileName + "\"}";

					if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2_RAW_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR2FILEUPLOAD;
						gstr2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2_B2CS_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR2B2CSFILEUPLOAD;
						gstr2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2_AT_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR2ATSFILEUPLOAD;
						gstr2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2_ATA_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR2ATASFILEUPLOAD;
						gstr2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2_INVOICE_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR2INVOICEFILEUPLOAD;
						gstr2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2_NIL_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR2NILFILEUPLOAD;
						gstr2FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
	
					java.util.List<Long> findByJobIds = asyncExecJobRepository
							.findByJobId(paramJson);
					for (Long jobId : findByJobIds) {
						AsyncExecJob asyncExceJob = asyncExecJobRepository
								.findByJobDetails(jobId);

						Gstr2FileStatusEntity fileStatus = gstr2FileUploadUtil
								.getGstr2FileStatus(fileName, userName,
										asyncExceJob);
						gstr2FileStatusRepository.save(fileStatus);
					}
					
				}
			}

			APIRespDto dto = new APIRespDto("Success",
					"File uploaded successfully");
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
