package com.ey.advisory.app.data.services.gstr9;

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
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstr2FileUploadJobInsertion;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HsnFileUploadService")
@Slf4j
public class Gstr9HsnFileUploadService {
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
	@Qualifier("Gstr9HsnHeaderCheckService")
	private Gstr9HsnHeaderCheckService gstr9HeaderCheckService;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType) {
		LOGGER.info(
				"Inside  gstr9 hsn file upload method and uniqueName is {}  ",
				uniqueName);

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			MultipartFile file1 = files[0];
			// Get the uploaded file name and a reference to the input stream of
			// the uploaded file.
			
			
			LOGGER.debug("About to get mimeType at ::{}",
					LocalDateTime.now());

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

			String fileName1 = StringUtils
					.cleanPath(file1.getOriginalFilename());
			InputStream inputStream = file1.getInputStream();
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName1);
			TabularDataLayout layout = new DummyTabularDataLayout(14);

			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			long stTime = System.currentTimeMillis();
			LOGGER.info("Start Time " + stTime);
			traverser.traverse(inputStream, layout, rowHandler, null);

			Object[] getHeaders = rowHandler.getHeaderRow();
			Pair<Boolean, String> checkHeaderFormat = gstr9HeaderCheckService
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
								OperationContext oc = openCmisSession.getDefaultContext();
								String query = String.format("cmis:name='%s'", folderName);
								ItemIterable<CmisObject> folders = openCmisSession
										.queryObjects("cmis:folder", query, false, oc);
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
							String docId = null;
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
									docId = createdDocument.getId();
									LOGGER.debug(
											"Created document in the repo {}",
											createdDocument.getId());
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
										+ "\"fileName\":\"" + fileName + "\"}";

								if (uniqueName.equalsIgnoreCase(
										GSTConstants.GSTR9_FILE_NAME)) {
									String jobCategory = JobConstants.GSTR9HSNFILEUPLOAD;
									gstr2FileUploadJobInsertion.fileUploadJob(
											paramJson, jobCategory, userName);
								}
								java.util.List<Long> findByJobIds = asyncExecJobRepository
										.findByJobId(paramJson);
								for (Long jobId : findByJobIds) {
									AsyncExecJob asyncExceJob = asyncExecJobRepository
											.findByJobDetails(jobId);

									Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
											.getGstr1FileStatus(fileName,
													userName, asyncExceJob,
													fileType, dataType, null);
									fileStatus.setFileType(
											GSTConstants.GSTR9_HSN);
									fileStatus.setDocId(docId);
									gstr1FileStatusRepository.save(fileStatus);
								}
							}

						}

						APIRespDto dto = new APIRespDto("Success",
								"GSTR-9 HSN file uploaded successfully");
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
					"GSTR-9 HSN file uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading GSTR-9 HSN file";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		return null;

	}
}
