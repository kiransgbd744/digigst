package com.ey.advisory.app.services.jobs.gstr8;

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
import com.ey.advisory.app.util.Anx1GetBatchUtil;
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
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Ravindra V S
 *
 */
@Component("Gstr8aFileUploadServiceImpl")
public class Gstr8aFileUploadServiceImpl
		implements Gstr8aFileUploadService {

	public static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr8aFileUploadServiceImpl.class);

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr2FileUploadJobInsertion")
	private Gstr2FileUploadJobInsertion gstr2FileUploadJobInsertion;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;


	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	@Override
	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType) throws Exception {

		LOGGER.info(
				"Inside get Inward Einvoice file upload method and uniqueName is {}  ",
				uniqueName);

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			MultipartFile file1 = files[0];
			
			LOGGER.debug("About to get mimeType at ::{}", LocalDateTime.now());

			String mimeType = GenUtil.getMimeType(file1.getInputStream());
			LOGGER.debug("Got mimeType {} at::{} ", mimeType,
					LocalDateTime.now());
			String docId = null;
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
			TabularDataLayout layout = new DummyTabularDataLayout(15);

			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			long stTime = System.currentTimeMillis();
			LOGGER.info("Start Time " + stTime);
			traverser.traverse(inputStream, layout, rowHandler, null);

			List<Object[]> anxList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			if (!anxList.isEmpty() && anxList.size() > 0) {
				{
					for (MultipartFile file : files) {
						String fileName = gstr1FileUploadUtil.getFileName(file,
								fileType);
						Session openCmisSession = gstr1FileUploadUtil
								.getCmisSession();
						Folder root = openCmisSession.getRootFolder();
						LOGGER.debug("Root Folder name is {}", root.getName());

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
									LOGGER.debug("Folder Name2 {}", folderName);
									if (folder.getName()
											.equalsIgnoreCase(folderName)) {
										LOGGER.debug("Folder already exists");
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
							LOGGER.error("Exception while creating folder", e);
						}

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

								LOGGER.debug("Creating document in the repo");

								Document createdDocument = createdFolder
										.createDocument(properties,
												contentStream,
												VersioningState.MAJOR);

								LOGGER.debug("Created document in the repo {}",
										createdDocument.getId());
								LOGGER.debug(
										"Created document name {} in the repo  ",
										createdDocument.getName());
								docId = createdDocument.getId();
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
									.getGstr2bFileStatus(fileName, userName,
											fileType, dataType, null);
							fileStatus.setFileType(GSTConstants.GSTR8A);
							fileStatus.setDocId(docId);
							fileStatus = gstr1FileStatusRepository
									.save(fileStatus);

							Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
							
					        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

							String fileNameArray[] = fileNameWithoutExtension.split("_");
							String gstin = fileNameArray[4];
							String fy = fileNameArray[5];
							

							dto.setGstin(gstin);
							dto.setReturnPeriod(GenUtil
									.getFinancialPeriodFromFY(GenUtil.getFormattedFy(fy)));
							dto.setFinYear(fy);
							dto.setFileId(fileStatus.getId());
							dto.setDocumentId(docId);
							dto.setApiSection(
									GSTConstants.GSTR8A.toUpperCase());

							dto.setFilePath(createdFolder.getName());
							dto.setFileName(fileName);

							LOGGER.debug("dto {}",
									dto);
							String paramJson = gson.toJson(dto);
							LOGGER.debug("paramJson {}",
									paramJson);
							if (uniqueName.equalsIgnoreCase(
									GSTConstants.GSTR8A_FILE_NAME)) {
								LOGGER.debug("Job creation started with {}",
										paramJson);
								String jobCategory = JobConstants.GSTR8AFILEUPLOAD;
								gstr2FileUploadJobInsertion.fileUploadJob(
										paramJson, jobCategory, userName);
							}
						}
					}

					APIRespDto dto = new APIRespDto("Success",
							"Gstr8a file uploaded successfully");
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
					"Gstr8a file uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading Gstr8a file";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		return null;
	}
	
	

}
