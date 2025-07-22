package com.ey.advisory.app.services.gstr7fileupload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
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
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstr2FileUploadJobInsertion;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr7FileUploadServiceImpl")
@Slf4j
public class Gstr7FileUploadServiceImpl implements Gstr7FileUploadService {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr2FileUploadJobInsertion")
	private Gstr2FileUploadJobInsertion gstr2FileUploadJobInsertion;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr7HeaderCheckService")
	private Gstr7HeaderCheckService gstr7HeaderCheckService;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType, String source) {
		LOGGER.info(
				"Inside get gstr7 file upload method and uniqueName is {}  ",
				uniqueName);

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			MultipartFile file1 = files[0];
			// Get the uploaded file name and a reference to the input stream of
			// the uploaded file.

			LOGGER.debug("About to get mimeType at ::{}", LocalDateTime.now());

			String mimeType = GenUtil.getMimeType(file1.getInputStream());
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

			String ext = GenUtil.getFileExt(file1);

			LOGGER.debug("Uploaded filename {} and ext {} at::{} ",
					file1.getOriginalFilename(), ext, LocalDateTime.now());

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
			String docId = null;
			String fileName1 = StringUtils
					.cleanPath(file1.getOriginalFilename());
			InputStream inputStream = file1.getInputStream();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName1);
			TabularDataLayout layout = new DummyTabularDataLayout(27);

			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			long stTime = System.currentTimeMillis();
			LOGGER.info("Start Time " + stTime);
			traverser.traverse(inputStream, layout, rowHandler, null);

			Object[] getHeaders = rowHandler.getHeaderRow();
			Pair<Boolean, String> checkHeaderFormat = gstr7HeaderCheckService
					.validate(getHeaders);
			if (checkHeaderFormat.getValue0()) {

				List<Object[]> anxList = ((FileUploadDocRowHandler<?>) rowHandler)
						.getFileUploadList();

				if (!anxList.isEmpty() && anxList.size() > 0) {
					{
						for (MultipartFile file : files) {
							String fileName = gstr1FileUploadUtil
									.getFileName(file, fileType);
							Session openCmisSession = gstr1FileUploadUtil
									.getCmisSession();
							// access the root folder of the repository
							Folder root = openCmisSession.getRootFolder();
							LOGGER.debug("Root Folder name is {}",
									root.getName());

							// create a new folder
							Map<String, String> newFolderProps = new HashMap<>();
							newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
									"cmis:folder");
							newFolderProps.put(PropertyIds.NAME, folderName);
							Folder createdFolder = null;
							try {
								OperationContext oc = openCmisSession
										.getDefaultContext();
								String query = String.format("cmis:name='%s'",
										folderName);
								ItemIterable<CmisObject> folders = openCmisSession
										.queryObjects("cmis:folder", query,
												false, oc);
								for (CmisObject obj : folders) {
									if (obj instanceof Folder) {
										Folder folder = (Folder) obj;
										LOGGER.debug("Folder Name1 {}",
												folder.getName());
										LOGGER.debug("Folder Name2 {}",
												folderName);
										if (folder.getName()
												.equalsIgnoreCase(folderName)) {
											LOGGER.debug(
													"Folder already exists");
											createdFolder = folder;
										}
									}
								}
								if (createdFolder == null) {
									createdFolder = root
											.createFolder(newFolderProps);
									LOGGER.debug("Folder Created");
								}
							} catch (CmisNameConstraintViolationException e) {
								LOGGER.error("Exception while creating folder",
										e);
							}

							// create a new file in the root folder
							Map<String, Object> properties = new HashMap<>();
							properties.put(PropertyIds.OBJECT_TYPE_ID,
									"cmis:document");
							properties.put(PropertyIds.NAME, fileName);
							InputStream stream = null;
							if (!file.isEmpty()) {
								try {
									byte[] fileContent = file.getBytes();
									stream = new ByteArrayInputStream(
											fileContent);
									ContentStream contentStream = openCmisSession
											.getObjectFactory()
											.createContentStream(fileName,
													fileContent.length,
													"application/vnd.ms-excel",
													stream);

									LOGGER.debug(
											"Creating document in the repo");

									Document createdDocument = createdFolder
											.createDocument(properties,
													contentStream,
													VersioningState.MAJOR);

									LOGGER.debug(
											"Created document in the repo {}",
											createdDocument.getId());
									docId = createdDocument.getId();
									LOGGER.debug(
											"Created document name {} in the repo  ",
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

								String paramJson = "{\"filePath\":\""
										+ createdFolder.getName() + "\","
										+ "\"fileName\":\"" + fileName + "\","
										+ "\"docId\":\"" + docId + "\"}";

								if (uniqueName.equalsIgnoreCase(
										GSTConstants.GSTR7_FILE_NAME)) {
									String jobCategory = JobConstants.GSTR7FILEUPLOAD;
									gstr2FileUploadJobInsertion.fileUploadJob(
											paramJson, jobCategory, userName);
								}
								java.util.List<Long> findByJobIds = asyncExecJobRepository
										.findByJobId(paramJson);
								for (Long jobId : findByJobIds) {
									AsyncExecJob asyncExceJob = asyncExecJobRepository
											.findByJobDetails(jobId);

									if (source != null
											&& source.equalsIgnoreCase(
													JobStatusConstants.SFTP_WEB_UPLOAD)) {
										Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
												.getSftpFileStatusGstr7(
														fileName, userName,
														asyncExceJob, fileType,
														dataType, null, docId);
										gstr1FileStatusRepository
												.save(fileStatus);
									} else {
										Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
												.getGstr6FileStatus(fileName,
														userName, asyncExceJob,
														fileType, dataType,
														null, docId);
										fileStatus.setFileType(
												GSTConstants.GSTR7_TDS);
										gstr1FileStatusRepository
												.save(fileStatus);
									}
								}

							}
						}

						APIRespDto dto = new APIRespDto("Success",
								"GSTR-7 file uploaded successfully");
						JsonObject resp = new JsonObject();
						JsonElement respBody = gson.toJsonTree(dto);
						resp.add("hdr", gson
								.toJsonTree(APIRespDto.createSuccessResp()));
						resp.add("resp", respBody);
						return new ResponseEntity<>(resp.toString(),
								HttpStatus.OK);
					}
				}

			} else {
				APIRespDto dto = new APIRespDto("Failed",
						"Please upload the correct file.");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				String msg = "Please upload the correct file";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				resp.add("resp", respBody);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
		}

		catch (

		Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"GSTR-7 file uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading GSTR-7 file";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		return null;

	}

	public ResponseEntity<String> uploadGstr7TransDocuments(
			MultipartFile[] files, String folderName, String uniqueName,
			String userName, String fileType, String dataType, String source) {
		LOGGER.info(
				"Inside get gstr7 file upload method and uniqueName is {}  ",
				uniqueName);

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			File tempDir = GenUtil.createTempDir();
			MultipartFile file = files[0];
			// Get the uploaded file name and a reference to the input stream of
			// the uploaded file.

			LOGGER.debug("About to get mimeType at ::{}", LocalDateTime.now());

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

			String fileName = gstr1FileUploadUtil.getFileName(file, fileType);
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("openCmisSession:{} ", openCmisSession);
				LOGGER.debug("folderName:{} ", folderName);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"OUTWARD_RAW_CMIS_CONNECT_END",
					"DefaultGstr1FileUploadService", "uploadDocuments", null);
			// access the root folder of the repository
			Folder root = openCmisSession.getRootFolder();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Root Folder name is:{} ", root.getName());
			}

			String docId = null;
			// create a new folder
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"OUTWARD_RAW_CREATE_NEW_FOLDER_START",
					"DefaultGstr1FileUploadService", "uploadDocuments", null);
			Map<String, String> newFolderProps = new HashMap<>();
			newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			newFolderProps.put(PropertyIds.NAME, folderName);
			Folder createdFolder = null;
			try {
				ItemIterable<CmisObject> folders = root.getChildren();
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
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"OUTWARD_RAW_CREATE_NEW_FOLDER_END",
					"DefaultGstr1FileUploadService", "uploadDocuments", null);
			// create a new file in the root folder
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"OUTWARD_RAW_CREATE_NEW_FILE_START",
					"DefaultGstr1FileUploadService", "uploadDocuments", null);
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
							.getObjectFactory().createContentStream(fileName,
									fileContent.length,
									"application/vnd.ms-excel", stream);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Creating document in the repo");
					}

					Document createdDocument = createdFolder.createDocument(
							properties, contentStream, VersioningState.MAJOR);
					docId = createdDocument.getId();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Created document in the repo "
								+ createdDocument.getId());
						LOGGER.debug("Created document name " + "in the repo  "
								+ createdDocument.getName());
					}
				} catch (CmisNameConstraintViolationException e) {
					LOGGER.error(
							"Exception while creating the document in repo", e);
				} finally {
					if (stream != null) {
						stream.close();
					}
				}
				Gstr1FileStatusEntity fileStatus = null;
				if (source != null && source
						.equalsIgnoreCase(JobStatusConstants.SFTP_WEB_UPLOAD)) {
					fileStatus = gstr1FileUploadUtil.getSftpFileStatus(fileName,
							userName, null, fileType, dataType, null);
					fileStatus.setDocId(docId);
					gstr1FileStatusRepository.save(fileStatus);
				} else {
					String fileHash = null;
					if (fileType.equalsIgnoreCase(
							GSTConstants.GSTR7_TRANS_FILE_NAME)) {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Inside fileHash comparison " + file
									+ "time" + LocalDateTime.now());
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
							LOGGER.debug("Already Existing logical state files"
									+ logicalStateFile.toString());
						}
						if (!logicalStateFile.isEmpty()) {
							String msg = "File processing already in progress.";
							APIRespDto dto = new APIRespDto("Failed", msg);
							JsonObject resp = new JsonObject();
							JsonElement respBody = gson.toJsonTree(dto);
							resp.add("hdr", gson
									.toJsonTree(APIRespDto.creatErrorResp()));
							resp.add("resp", respBody);
							LOGGER.error(msg);
							return new ResponseEntity<>(resp.toString(),
									HttpStatus.OK);
						}
						fileStatus = gstr1FileUploadUtil
								.getGstr1FileStatusFor2bpr(fileName, userName,
										null, GSTConstants.GSTR7_TRANSACTIONAL,
										dataType, null, fileHash);
						fileStatus.setDocId(docId);
						gstr1FileStatusRepository.save(fileStatus);
					} else {
						fileStatus = gstr1FileUploadUtil.getGstr1FileStatus(
								fileName, userName, null, fileType, dataType,
								null);
						fileStatus.setDocId(docId);

						gstr1FileStatusRepository.save(fileStatus);
					}
				}
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"OUTWARD_RAW_CREATE_NEW_FOLDER_END",
						"DefaultGstr1FileUploadService", "uploadDocuments",
						null);
				String paramJson = "{\"filePath\":\"" + createdFolder.getName()
						+ "\"," + "\"fileId\":\"" + fileStatus.getId() + "\","
						+ "\"fileName\":\"" + fileName + "\"}";

				String jobCategory = JobConstants.GSTR7TRANS_FILE_PROCESSING;
				gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
						jobCategory, userName);

			}
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
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			String msg = "Unexpected error while uploading GSTR-7 file";
			LOGGER.error(msg, e);
			APIRespDto dto = new APIRespDto("Failed",
					"GSTR-7 file uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
