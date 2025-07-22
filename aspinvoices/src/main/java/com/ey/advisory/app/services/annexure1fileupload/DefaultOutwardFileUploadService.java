package com.ey.advisory.app.services.annexure1fileupload;

import static com.ey.advisory.common.GSTConstants.ANN1_B2C_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_SHIPPING_BILL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_TABLE4_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.B2C;
import static com.ey.advisory.common.GSTConstants.CUSTOMER;
import static com.ey.advisory.common.GSTConstants.INTEREST;
import static com.ey.advisory.common.GSTConstants.INTEREST_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.MASTER_CUSTOMER_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.REFUNDS;
import static com.ey.advisory.common.GSTConstants.REFUNDS_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.RET1AND1A;
import static com.ey.advisory.common.GSTConstants.RET_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.SETOFFANDUTIL;
import static com.ey.advisory.common.GSTConstants.SETOFFANDUTIL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.SHIPPINGBILL;
import static com.ey.advisory.common.GSTConstants.TABLE3H3I;
import static com.ey.advisory.common.GSTConstants.TABLE3H3I_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.TABLE4;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("DefaultOutwardFileUploadService")
public class DefaultOutwardFileUploadService
		implements OutwardFileUploadService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultOutwardFileUploadService.class);
	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Override
	public ResponseEntity<String> fileUploadsAnnexure1(MultipartFile[] files,
			String fileType, String folderName, String userName,
			String uniqueName, String dataType) {
		LOGGER.error(
				"inside fileUploadsAnnexure1 method and uniqueName is {}  ",
				uniqueName);

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			for (MultipartFile file : files) {
				String fileName = outwardFileUploadUtil.getFileName(file,
						fileType);
				Session openCmisSession = outwardFileUploadUtil
						.openCmisSession();
				// access the root folder of the repository
				Folder root = openCmisSession.getRootFolder();
				LOGGER.error("Root Folder name is ", root.getName());

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

						LOGGER.error("Creating document in the repo");

						Document createdDocument = createdFolder.createDocument(
								properties, contentStream,
								VersioningState.MAJOR);

						LOGGER.error("Created document in the repo "
								+ createdDocument.getId());
						LOGGER.error("Created document name " + "in the repo  "
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

					if (uniqueName.equalsIgnoreCase(ANN1_B2C_FILE_NAME)) {
						String jobCategory = B2C;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					if (uniqueName.equalsIgnoreCase(ANN1_TABLE4_FILE_NAME)) {
						String jobCategory = TABLE4;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}

					if (uniqueName.equalsIgnoreCase(TABLE3H3I_FILE_NAME)) {
						String jobCategory = TABLE3H3I;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}

					if (uniqueName
							.equalsIgnoreCase(ANN1_SHIPPING_BILL_FILE_NAME)) {
						String jobCategory = SHIPPINGBILL;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					if (uniqueName
							.equalsIgnoreCase(RET_FILE_NAME)) {
						String jobCategory = RET1AND1A;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					if (uniqueName
							.equalsIgnoreCase(INTEREST_FILE_NAME)) {
						String jobCategory = INTEREST;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					if (uniqueName
							.equalsIgnoreCase(SETOFFANDUTIL_FILE_NAME)) {
						String jobCategory = SETOFFANDUTIL;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					if (uniqueName
							.equalsIgnoreCase(REFUNDS_FILE_NAME)) {
						String jobCategory = REFUNDS;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}

					if (uniqueName
							.equalsIgnoreCase(MASTER_CUSTOMER_FILE_NAME)) {
						String paramJsonCustomer = "{\"filePath\":\""
								+ createdFolder.getName() + "\","
								+ "\"fileName\":\"" + fileName + "\"}";
						String jobCategory = CUSTOMER;
						gstr1FileUploadJobInsertion.fileUploadJob(
								paramJsonCustomer, jobCategory, userName);

					}

					List<Long> findByJobIds = asyncExecJobRepository
							.findByJobId(paramJson);
					for (Long jobId : findByJobIds) {
						AsyncExecJob asyncExceJob = asyncExecJobRepository
								.findByJobDetails(jobId);

						Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
								.getGstr1FileStatus(fileName, userName,
										asyncExceJob, fileType, dataType, null);
						gstr1FileStatusRepository.save(fileStatus);
					}
				}
				APIRespDto dto = new APIRespDto("Sucess",
						"File has been successfully uploaded. "
								+ "Please check the file details in File Status");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
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
