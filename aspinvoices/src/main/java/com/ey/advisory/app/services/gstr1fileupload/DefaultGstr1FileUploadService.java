package com.ey.advisory.app.services.gstr1fileupload;

import static com.ey.advisory.common.GSTConstants.ATA_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ATA_FILE_NAME_1A;
import static com.ey.advisory.common.GSTConstants.AT_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.AT_FILE_NAME_1A;
import static com.ey.advisory.common.GSTConstants.B2CS_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.B2CS_FILE_NAME_1A;
import static com.ey.advisory.common.GSTConstants.EINVOICE_RECON_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.HSN_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.HSN_FILE_NAME_1A;
import static com.ey.advisory.common.GSTConstants.INVOICE_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INVOICE_FILE_NAME_1A;
import static com.ey.advisory.common.GSTConstants.NIL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.NIL_FILE_NAME_1A;
import static com.ey.advisory.common.GSTConstants.RAW_FILE_NAME;
import static com.ey.advisory.common.JobConstants.EINVOICERECONFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1AATASFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1AATSFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1AB2CSFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1AHSNFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1AINVOICEFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1ANILFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1ATASFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1ATSFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1B2CSFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1FILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1HSNFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1INVOICEFILEUPLOAD;
import static com.ey.advisory.common.JobConstants.GSTR1NILFILEUPLOAD;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.ImsReconStatusConfigRepository;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
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
 * @author Mahesh.Golla
 *
 */
/*
 * This default GSTR1 upload represent service for uploading the files from
 * various sources reading the data from document and it will store or sent
 * excel file to Document Service
 */
@Component("DefaultGstr1FileUploadService")
@Slf4j
public class DefaultGstr1FileUploadService implements Gstr1FileUploadService {

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

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	@Autowired
	@Qualifier("ImsReconStatusConfigRepository")
	ImsReconStatusConfigRepository imsReconRepo;
	
	private static List<String> autoimsStatusList = Arrays
			.asList("INITIATED", "INPROGRESS");
			
	@Override
	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType, String source) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		File tempDir = createTempDir();
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
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"OUTWARD_RAW_CMIS_CONNECT_START",
						"DefaultGstr1FileUploadService", "uploadDocuments",
						null);
				Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("openCmisSession:{} ", openCmisSession);
					LOGGER.debug("folderName:{} ", folderName);
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"OUTWARD_RAW_CMIS_CONNECT_END",
						"DefaultGstr1FileUploadService", "uploadDocuments",
						null);
				// access the root folder of the repository
				Folder root = openCmisSession.getRootFolder();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Root Folder name is:{} ", root.getName());
				}
				
				String docId = null;
				// create a new folder
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"OUTWARD_RAW_CREATE_NEW_FOLDER_START",
						"DefaultGstr1FileUploadService", "uploadDocuments",
						null);
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
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("Folder already exists");
								}

								createdFolder = folder;

							}
						}
					}
					if (createdFolder == null) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Creating Folder");
						}
						createdFolder = root.createFolder(newFolderProps);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Folder Created");
						}
					}
				} catch (CmisNameConstraintViolationException e) {
					LOGGER.error("Exception while creating folder", e);
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"OUTWARD_RAW_CREATE_NEW_FOLDER_END",
						"DefaultGstr1FileUploadService", "uploadDocuments",
						null);
				// create a new file in the root folder
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"OUTWARD_RAW_CREATE_NEW_FILE_START",
						"DefaultGstr1FileUploadService", "uploadDocuments",
						null);
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
				properties.put(PropertyIds.NAME, fileName);
				InputStream stream = null;
				if (!file.isEmpty()) {
					try {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("File upload process start " + file);
						}

						byte[] fileContent = file.getBytes();
						stream = new ByteArrayInputStream(fileContent);
						ContentStream contentStream = openCmisSession
								.getObjectFactory().createContentStream(
										fileName, fileContent.length,
										"application/vnd.ms-excel", stream);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Creating document in the repo");
						}

						Document createdDocument = createdFolder.createDocument(
								properties, contentStream,
								VersioningState.MAJOR);
						docId = createdDocument.getId();
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Created document in the repo "
									+ createdDocument.getId());
							LOGGER.debug(
									"Created document name " + "in the repo  "
											+ createdDocument.getName());
						}
					} catch (CmisNameConstraintViolationException e) {
						LOGGER.error(
								"Exception while creating the document in repo",
								e);
					} finally {
						if (stream != null) {
							stream.close();
						}
					}
					Gstr1FileStatusEntity fileStatus = null;
					if (source != null && source.equalsIgnoreCase(
							JobStatusConstants.SFTP_WEB_UPLOAD)) {
						fileStatus = gstr1FileUploadUtil.getSftpFileStatus(
								fileName, userName, null, fileType, dataType,
								null);
						fileStatus.setDocId(docId);
						fileStatus.setTransformationStatus("No Transformation");
						gstr1FileStatusRepository.save(fileStatus);
					} else {
						String fileHash = null;
						if (fileType.equalsIgnoreCase(
								GSTConstants.COMPREHENSIVE_RAW)) {

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Inside fileHash comparison "
										+ file + "time" + LocalDateTime.now());
							}

							String tempFileName = tempDir.getAbsolutePath()
									+ File.separator + "FILE_HASH_NAME" + ext;

							File tempFile = new File(tempFileName);
							file.transferTo(tempFile);

							MessageDigest md = MessageDigest.getInstance("MD5");
							md.update(Files.readAllBytes(tempFile.toPath()));
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
								resp.add("hdr", gson.toJsonTree(
										APIRespDto.creatErrorResp()));
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
							gstr1FileStatusRepository.save(fileStatus);
						} else {
							fileStatus = gstr1FileUploadUtil.getGstr1FileStatus(
									fileName, userName, null, fileType,
									dataType, null);
							fileStatus.setDocId(docId);
							if(uniqueName.equalsIgnoreCase(
									GSTConstants.GSTR2B_PR_USERRESPONSE_FILE_NAME) || uniqueName.equalsIgnoreCase(
											GSTConstants.GSTR2B_PR_IMS_USERRESPONSE_FILE_NAME))
							{
								fileStatus.setEntityId(source!=null?Long.valueOf(source):null);
							}
							gstr1FileStatusRepository.save(fileStatus);
						}
					}
					PerfUtil.logEventToFile(
							PerfamanceEventConstants.FILE_PROCESSING,
							"OUTWARD_RAW_CREATE_NEW_FOLDER_END",
							"DefaultGstr1FileUploadService", "uploadDocuments",
							null);
					String paramJson = "{\"filePath\":\""
							+ createdFolder.getName() + "\"," + "\"fileId\":\""
							+ fileStatus.getId() + "\"," + "\"fileName\":\""
							+ fileName + "\"}";

					if (uniqueName.equalsIgnoreCase(RAW_FILE_NAME)) {
						if (fileType.equalsIgnoreCase(
								GSTConstants.COMPREHENSIVE_RAW)) {
							PerfUtil.logEventToFile(
									PerfamanceEventConstants.FILE_PROCESSING,
									"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_START",
									"DefaultGstr1FileUploadService",
									"uploadDocuments", fileName);
							String jobCategory = GSTConstants.EINVOICEFILEUPLOAD;
							gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
									jobCategory, userName);
							PerfUtil.logEventToFile(
									PerfamanceEventConstants.FILE_PROCESSING,
									"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_END",
									"DefaultGstr1FileUploadService",
									"uploadDocuments", fileName);
						} else if (fileType.equalsIgnoreCase(
								GSTConstants.COMPREHENSIVE_RAW_1A)) {
							PerfUtil.logEventToFile(
									PerfamanceEventConstants.FILE_PROCESSING,
									"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_START",
									"DefaultGstr1FileUploadService",
									"uploadDocuments", fileName);
							String jobCategory = GSTConstants.GSTR1AEINVOICEFILEUPLOAD;
							gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
									jobCategory, userName);
							PerfUtil.logEventToFile(
									PerfamanceEventConstants.FILE_PROCESSING,
									"OUTWARD_RAW_INSERT_ASYNC_FILE_UPLOAD_JOB_END",
									"DefaultGstr1FileUploadService",
									"uploadDocuments", fileName);
						} else {
							String jobCategory = GSTR1FILEUPLOAD;
							gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
									jobCategory, userName);
						}
					} else if (uniqueName.equalsIgnoreCase(B2CS_FILE_NAME)) {
						String jobCategory = GSTR1B2CSFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName
							.equalsIgnoreCase(EINVOICE_RECON_FILE_NAME)) {
						String jobCategory = EINVOICERECONFILEUPLOAD;
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
					} else if (uniqueName
							.equalsIgnoreCase(GSTConstants.GSTR3B_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR3BFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName
							.equalsIgnoreCase(GSTConstants.CEWB_FILE_NAME)) {
						String jobCategory = JobConstants.CEWBFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}

					else if (uniqueName
							.equalsIgnoreCase(GSTConstants.ITC04_FILE_NAME)) {
						String jobCategory = JobConstants.ITC04FILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName
							.equalsIgnoreCase(GSTConstants.OLD_NIL_FILE_NAME)) {
						String jobCategory = JobConstants.GSTR1NILOLDFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2X_FOLDER_NAME)) {
						String jobCategory = JobConstants.GSTR2XFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					}

					else if (uniqueName.equalsIgnoreCase(GSTConstants.GSTR9)) {
						String jobCategory = JobConstants.GSTR9_INWARD_OURD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.REVERSAL_180_DAYS_RESPONSE_FILE_NAME)) {
						String jobCategory = JobConstants.REVERSAL_180_DAYS_RESPONSE;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2A_USERRESPONSE_FILE_NAME)) {

						/*String jobCategory = JobConstants.GSTR2A_USER_RESP_UPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);*/
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2B_PR_USERRESPONSE_FILE_NAME)) {
						JsonObject jsonParams = new JsonObject();
						jsonParams.addProperty("fileId", fileStatus.getId());
						jsonParams.addProperty("filePath",
								createdFolder.getName());
						jsonParams.addProperty("fileName", fileName);
						jsonParams.addProperty("entityId",
								Long.valueOf(source));
						
						
					/*	gstr1FileUploadJobInsertion.fileUploadJob(
								jsonParams.toString(),
								JobConstants.GSTR2B_PR_USER_RESP_UPLOAD,
								userName);*/
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2A_ERP_USERRESPONSE_FILE_NAME)) {

					/*	gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								JobConstants.GSTR2A_SFTP_RESP_UPLOAD, userName);*/
					} else if (uniqueName.equalsIgnoreCase(B2CS_FILE_NAME_1A)) {
						String jobCategory = GSTR1AB2CSFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(AT_FILE_NAME_1A)) {
						String jobCategory = GSTR1AATSFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(ATA_FILE_NAME_1A)) {
						String jobCategory = GSTR1AATASFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(INVOICE_FILE_NAME_1A)) {
						String jobCategory = GSTR1AINVOICEFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					} else if (uniqueName.equalsIgnoreCase(NIL_FILE_NAME_1A)) {
						String jobCategory = GSTR1ANILFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);

					} else if (uniqueName.equalsIgnoreCase(HSN_FILE_NAME_1A)) {
						String jobCategory = GSTR1AHSNFILEUPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
						
						//IMS
					} else if (uniqueName.equalsIgnoreCase(
							GSTConstants.IMS_USERRESPONSE_FILE_NAME)) {
						
						// monitor ims changes
						int imstatusCount = imsReconRepo
								.findByStatusIn(autoimsStatusList);

						if (imstatusCount != 0) {
							String msg = String.format(
									"Auto IMS action based on Auto recon parameters is in progress,"
									+ " Please try after sometime.");
							LOGGER.error(msg);
							APIRespDto dto1 = new APIRespDto("Information",msg);
							JsonObject resp1 = new JsonObject();
							JsonElement respBody = gson.toJsonTree(dto1);
							resp1.add("hdr",
									gson.toJsonTree(APIRespDto.createSuccessResp()));
							resp1.add("resp", respBody);
							
							return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
						}
						
						String jobCategory = JobConstants.IMS_RESPONSE_UPLOAD;
						gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
								jobCategory, userName);
					}else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2B_PR_IMS_USERRESPONSE_FILE_NAME)) {
						JsonObject jsonParams = new JsonObject();
						jsonParams.addProperty("fileId", fileStatus.getId());
						jsonParams.addProperty("filePath",
								createdFolder.getName());
						jsonParams.addProperty("fileName", fileName);
						jsonParams.addProperty("entityId",
								Long.valueOf(source));
						
					/*	gstr1FileUploadJobInsertion.fileUploadJob(
								jsonParams.toString(),
								JobConstants.GSTR2B_PR_USER_RESP_UPLOAD,
								userName);*/
					}
					//2apr ims recon response
					else if (uniqueName.equalsIgnoreCase(
							GSTConstants.GSTR2A_PR_IMS_USERRESPONSE_FILE_NAME)) {
						JsonObject jsonParams = new JsonObject();
						jsonParams.addProperty("fileId", fileStatus.getId());
						jsonParams.addProperty("filePath",
								createdFolder.getName());
						jsonParams.addProperty("fileName", fileName);
						
					/*	gstr1FileUploadJobInsertion.fileUploadJob(
								jsonParams.toString(),
								JobConstants.GSTR2A_PR_IMS_USER_RESP_UPLOAD,
								userName);*/
					}

					PerfUtil.logEventToFile(
							PerfamanceEventConstants.FILE_PROCESSING,
							"OUTWARD_RAW_FILE_STATUS_INSERT_START",
							"DefaultGstr1FileUploadService", "uploadDocuments",
							fileName);
					PerfUtil.logEventToFile(
							PerfamanceEventConstants.FILE_PROCESSING,
							"OUTWARD_RAW_FILE_STATUS_INSERT_END",
							"DefaultGstr1FileUploadService", "uploadDocuments",
							fileName);
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
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	/*
	 * public static void main(String args[]) { String filePath =
	 * "C://Users//AZ956YX//OneDrive - EY//Desktop"; File file = new
	 * File(filePath);
	 * 
	 * FileInputStream input = new FileInputStream(file); MultipartFile
	 * multipartFile = new MockMultipartFile("file", file.getName(),
	 * "text/plain", IOUtils.toByteArray(input));
	 * 
	 * File tempDir = null;; String tempFileName = tempDir.getAbsolutePath() +
	 * File.separator + "FILE_HASH_NAME"+".csv";
	 * 
	 * File tempFile = new File(tempFileName); file.transferTo(tempFile);
	 * 
	 * MessageDigest md = MessageDigest.getInstance("MD5");
	 * md.update(Files.readAllBytes(tempFile.toPath())); byte[] digest =
	 * md.digest(); String fileHash = DatatypeConverter.printHexBinary(digest)
	 * .toUpperCase();
	 * 
	 * }
	 */

	public static File createTempDir() throws IOException {
		return Files.createTempDirectory("FileHashFolder").toFile();
	}
}