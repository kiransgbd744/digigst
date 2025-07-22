package com.ey.advisory.app.services.gstr1fileupload;

import static com.ey.advisory.common.GSTConstants.ANN1_B2C_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_SHIPPING_BILL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_TABLE4_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ATA_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.AT_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.B2C;
import static com.ey.advisory.common.GSTConstants.B2CS_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.CUSTOMER;
import static com.ey.advisory.common.GSTConstants.HSN_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INTEREST;
import static com.ey.advisory.common.GSTConstants.INTEREST_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INVOICE_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.MASTER_CUSTOMER_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.NIL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.RAW_FILE_NAME;
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
import static com.ey.advisory.common.JobConstants.GSTR1ATASFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1ATSFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1B2CSFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1FILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1HSNFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1INVOICEFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1NILFILEUPLOAD;

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

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
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
/*
 * This default GSTR1 upload represent service for uploading the files from
 * various sources reading the data from document and it will store or sent
 * excel file to Document Service
 */
@Component("DefaultSftpFileUploadService")
public class DefaultSftpFileUploadService implements SftpFileUploadService {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultSftpFileUploadService.class);

	@Override
	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments:{} ");
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			for (MultipartFile file : files) {
				String fileName = gstr1FileUploadUtil.getFileName(file,
						fileType);
				FileStatusPerfUtil.logEvent("CMIS_CONNECT_BEGIN", uniqueName);
				Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
				FileStatusPerfUtil.logEvent("CMIS_CONNECT_END", uniqueName);
				// access the root folder of the repository
				Folder root = openCmisSession.getRootFolder();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Root Folder name is:{} ", root.getName());
				}
				// create a new folder
				FileStatusPerfUtil.logEvent("CREATE_NEW_FOLDER_BEGIN", uniqueName);
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
				FileStatusPerfUtil.logEvent("CREATE_NEW_FOLDER_BEGIN", uniqueName);
				// create a new file in the root folder
				FileStatusPerfUtil.logEvent("CREATE_NEW_FILE_BEGIN", uniqueName);
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
					FileStatusPerfUtil.logEvent("CREATE_NEW_FILE_END", uniqueName);
					String paramJson = "{\"filePath\":\""
							+ createdFolder.getName() + "\","
							+ "\"fileName\":\"" + fileName + "\"}";

					if (uniqueName.equalsIgnoreCase(RAW_FILE_NAME)) {
						if (fileType.equalsIgnoreCase(
								GSTConstants.COMPREHENSIVE_RAW)) {
						FileStatusPerfUtil.logEvent("INSERT_ASYNC_FILE_UPLOAD_JOB_BEGIN ",uniqueName);
							String jobCategory = GSTConstants.EINVOICEFILEUPLOAD;
							gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
									jobCategory, userName);
							FileStatusPerfUtil.logEvent("INSERT_ASYNC_FILE_UPLOAD_JOB_BEGIN ",uniqueName);
						}
						else{
							String jobCategory = GSTR1FILEUPLOAD;
							gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
									jobCategory, userName);
						}
					} 
					
					else if (uniqueName.equalsIgnoreCase(GSTConstants.INWARD)) {
						String jobCategory = JobConstants.INWARD_FILE_UPLOADS;
						FileStatusPerfUtil.logEvent(
								"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_BEGIN",
								uniqueName);
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
						FileStatusPerfUtil.logEvent(
								"INWARD_RAW_FILE_UPLOAD_JOB_CREATION_END",
								uniqueName);
						FileStatusPerfUtil.logEvent(
								"DefaultSftpFileUploadService_INWARD_RAW_FILE_UPLOAD_JOB_END");
					}
				
					
					else if (uniqueName.equalsIgnoreCase(B2CS_FILE_NAME)) {
						String jobCategory = GSTR1B2CSFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(AT_FILE_NAME)) {
						String jobCategory = GSTR1ATSFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}

					else if (uniqueName.equalsIgnoreCase(ATA_FILE_NAME)) {
						String jobCategory = GSTR1ATASFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(INVOICE_FILE_NAME)) {
						String jobCategory = GSTR1INVOICEFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(NIL_FILE_NAME)) {
						String jobCategory = GSTR1NILFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(HSN_FILE_NAME)) {
						String jobCategory = GSTR1HSNFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}
					else if (uniqueName.equalsIgnoreCase(
							GSTConstants.ANN2_RAW_FILE_NAME)) {
						String jobCategory = JobConstants.ANX2INWARDRAWFILEUPLOAD;
						FileStatusPerfUtil.logEvent("FILE_UPLOAD_JOB_CREATION_BEGIN",uniqueName);
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
						FileStatusPerfUtil.logEvent("FILE_UPLOAD_JOB_CREATION_END",uniqueName);
						FileStatusPerfUtil.logEvent("Anx2InwardRawFileUploadServiceImpl_INWARD_RAW_FILE_UPLOAD_JOB_END");
					}
					else if(uniqueName.equalsIgnoreCase(ANN1_B2C_FILE_NAME)) {
						String jobCategory = B2C;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					else if(uniqueName.equalsIgnoreCase(ANN1_TABLE4_FILE_NAME)) {
						String jobCategory = TABLE4;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}

					else if(uniqueName.equalsIgnoreCase(TABLE3H3I_FILE_NAME)) {
						String jobCategory = TABLE3H3I;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}

					else if(uniqueName
							.equalsIgnoreCase(ANN1_SHIPPING_BILL_FILE_NAME)) {
						String jobCategory = SHIPPINGBILL;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					else if(uniqueName
							.equalsIgnoreCase(RET_FILE_NAME)) {
						String jobCategory = RET1AND1A;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					else if(uniqueName
							.equalsIgnoreCase(INTEREST_FILE_NAME)) {
						String jobCategory = INTEREST;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					else if(uniqueName
							.equalsIgnoreCase(SETOFFANDUTIL_FILE_NAME)) {
						String jobCategory = SETOFFANDUTIL;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}
					
					else if(uniqueName
							.equalsIgnoreCase(REFUNDS_FILE_NAME)) {
						String jobCategory = REFUNDS;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}

					else if(uniqueName
							.equalsIgnoreCase(MASTER_CUSTOMER_FILE_NAME)) {
						String paramJsonCustomer = "{\"filePath\":\""
								+ createdFolder.getName() + "\","
								+ "\"fileName\":\"" + fileName + "\"}";
						String jobCategory = CUSTOMER;
						gstr1FileUploadJobInsertion.fileUploadJob(
								paramJsonCustomer, jobCategory, userName);

					}
					FileStatusPerfUtil.logEvent("FILE_STATUS_INSERT_BEGIN", uniqueName);
					java.util.List<Long> findByJobIds = asyncExecJobRepository
							.findByJobId(paramJson);
					for (Long jobId : findByJobIds) {
						AsyncExecJob asyncExceJob = asyncExecJobRepository
								.findByJobDetails(jobId);

						Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
								.getSftpFileStatus(fileName, userName,
										asyncExceJob, fileType, dataType, null);
						gstr1FileStatusRepository.save(fileStatus);
					}
					FileStatusPerfUtil.logEvent("FILE_STATUS_INSERT_END", uniqueName);
				}
				APIRespDto dto = new APIRespDto("Success",
						"File has been successfully uploaded. "
								+ "Please check the file details in File Status");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Begining from uploadDocuments:{} ");
				}
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
			LOGGER.error("Exception Occured:{}", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}