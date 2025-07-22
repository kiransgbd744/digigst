package com.ey.advisory.app.services.gstr1;

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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.gstr2afileupload.Gstr2aB2bFileUploadService;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstr2FileUploadJobInsertion;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("Gstr1FileUploadServiceImpl")
public class Gstr1FileUploadServiceImpl implements Gstr2aB2bFileUploadService {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1FileUploadServiceImpl.class);

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
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside get gstr1 file upload method and uniqueName is {}  ",
					uniqueName);
		}

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			MultipartFile file1 = files[0];
			// Get the uploaded file name and a reference to the input stream of
			// the uploaded file.
			String fileName1 = StringUtils
					.cleanPath(file1.getOriginalFilename());
			InputStream inputStream = file1.getInputStream();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName1);
			TabularDataLayout layout = new DummyTabularDataLayout(15);

			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			long stTime = System.currentTimeMillis();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Start Time " + stTime);
			}
			traverser.traverse(inputStream, layout, rowHandler, null);

			Object[] getHeaders = rowHandler.getHeaderRow();

			List<Object[]> anxList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			if (!anxList.isEmpty() && anxList.size() > 0) {
				{
					for (MultipartFile file : files) {
						String fileName = gstr1FileUploadUtil.getFileName(file,
								fileType);
						Session openCmisSession = gstr1FileUploadUtil
								.getCmisSession();
						// access the root folder of the repository
						Folder root = openCmisSession.getRootFolder();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Root Folder name is {}",
									root.getName());
						}

						// create a new folder
						Map<String, String> newFolderProps = new HashMap<>();
						newFolderProps.put(PropertyIds.OBJECT_TYPE_ID,
								"cmis:folder");
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
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("Folder Name1 {}",
												folder.getName());
									}
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("Folder Name2 {}",
												folderName);
									}
									if (folder.getName()
											.equalsIgnoreCase(folderName)) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug(
													"Folder already exists");
										}
										createdFolder = folder;
									}
								}
							}
							if (createdFolder == null) {
								createdFolder = root
										.createFolder(newFolderProps);
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("Folder Created");
								}
							}
						} catch (CmisNameConstraintViolationException e) {
							LOGGER.error("Exception while creating folder", e);
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
								stream = new ByteArrayInputStream(fileContent);
								ContentStream contentStream = openCmisSession
										.getObjectFactory().createContentStream(
												fileName, fileContent.length,
												"application/vnd.ms-excel",
												stream);
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Creating document in the repo");
								}

								Document createdDocument = createdFolder
										.createDocument(properties,
												contentStream,
												VersioningState.MAJOR);
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Created document in the repo {}",
											createdDocument.getId());
								}
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Created document name {} in the repo  ",
											createdDocument.getName());
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

							Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
									.getGstr2aFileStatus(fileName, userName,
											fileType, dataType, null);
							fileStatus.setFileType(GSTConstants.GSTR1);

							fileStatus = gstr1FileStatusRepository
									.save(fileStatus);

							Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
							dto.setType("WEB_UPLOAD");
							dto.setFileId(fileStatus.getId());
							dto.setApiSection(APIConstants.GSTR1_UPLOAD.toUpperCase());

							dto = createBatchAndSave(dto);

							dto.setFilePath(createdFolder.getName());
							dto.setFileName(fileName);

							String paramJson = gson.toJson(dto);

							/*
							 * String paramJson = "{\"filePath\":\"" +
							 * createdFolder.getName() + "\"," +
							 * "\"fileName\":\"" + fileName + "\"}";
							 */
							if (uniqueName.equalsIgnoreCase(
									GSTConstants.GSTR1_FILE_NAME)) {
								String jobCategory = JobConstants.GSTR1EINVFILEUPLOAD;
								gstr2FileUploadJobInsertion.fileUploadJob(
										paramJson, jobCategory, userName);
							}
							/*
							 * java.util.List<Long> findByJobIds =
							 * asyncExecJobRepository .findByJobId(paramJson);
							 * for (Long jobId : findByJobIds) { AsyncExecJob
							 * asyncExceJob = asyncExecJobRepository
							 * .findByJobDetails(jobId);
							 * 
							 * Gstr1FileStatusEntity fileStatus =
							 * gstr1FileUploadUtil .getGstr1FileStatus(fileName,
							 * userName, asyncExceJob, fileType, dataType,
							 * null);
							 * fileStatus.setFileType(GSTConstants.GSTR1);
							 * gstr1FileStatusRepository.save(fileStatus); }
							 */

						}
					}

					APIRespDto dto = new APIRespDto("Success",
							"GSTR-1 EInvoice file uploaded successfully");
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					resp.add("resp", respBody);
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
			}
		}

		catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"GSTR-1 EInvoice file uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading GSTR-1 EInvoice file";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		return null;

	}

	private Gstr1GetInvoicesReqDto createBatchAndSave(
			Gstr1GetInvoicesReqDto dto) {

		// InActivating Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR1_UPLOAD.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1EInvoice(dto,
				dto.getType(), APIConstants.GSTR1_UPLOAD.toUpperCase());
		// Save new Batch
		batch = batchRepo.save(batch);
		dto.setBatchId(batch.getId());
		return dto;

	}
}