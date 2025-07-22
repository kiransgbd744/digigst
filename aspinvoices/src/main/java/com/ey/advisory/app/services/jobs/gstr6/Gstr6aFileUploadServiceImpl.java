package com.ey.advisory.app.services.jobs.gstr6;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
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

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
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
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.config.Config;
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
@Component("Gstr6aFileUploadServiceImpl")
public class Gstr6aFileUploadServiceImpl
		implements Gstr6aFileUploadService {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6aFileUploadServiceImpl.class);

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

	@Autowired
	private AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType) {
		LOGGER.info(
				"Inside get gstr6a file upload method and uniqueName is {}  ",
				uniqueName);

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			MultipartFile file1 = files[0];

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
						LOGGER.debug("Root Folder name is {}", root.getName());

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
							fileStatus.setFileType(GSTConstants.GSTR6A);
							fileStatus = gstr1FileStatusRepository
									.save(fileStatus);

							Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
							String fileNameArray[] = fileName.split("_");
							String gstin = fileNameArray[2];
							String taxPeriod = fileNameArray[3];
							dto.setGstin(gstin);
							dto.setReturnPeriod(taxPeriod);
							dto.setType("WEB_UPLOAD");
							dto.setFileId(fileStatus.getId());
							dto.setApiSection(
									APIConstants.GSTR6A_UPLOAD.toUpperCase());
							try {
								dto = createBatchAndSave(dto);
							} catch (Exception e) {
								LOGGER.error("Exception", e);
								APIRespDto dtos = new APIRespDto("Failed",
										"GSTR-6A file uploaded Failed");
								JsonObject resp = new JsonObject();

								JsonElement respBody = gson.toJsonTree(dtos);
								String msg = "Gstin is not onboarded";
								resp.add("hdr", new Gson()
										.toJsonTree(new APIRespDto("E", msg)));
								resp.add("resp", respBody);
								return new ResponseEntity<>(resp.toString(),
										HttpStatus.OK);

							}

							dto.setFilePath(createdFolder.getName());
							dto.setFileName(fileName);

							String paramJson = gson.toJson(dto);

							if (uniqueName.equalsIgnoreCase(
									GSTConstants.GSTR6A_FILE_NAME)) {
								String jobCategory = JobConstants.GSTR6AFILEUPLOAD;
								gstr2FileUploadJobInsertion.fileUploadJob(
										paramJson, jobCategory, userName);
							}

						}
					}
					APIRespDto dto = new APIRespDto("Success",
							"GSTR-6A file uploaded successfully");
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
					"GSTR-6A file uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading GSTR-6A file";
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.toString())));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		return null;

	}

	private Gstr1GetInvoicesReqDto createBatchAndSave(
			Gstr1GetInvoicesReqDto dto) throws Exception {

		Map<String, Config> configMap = configManager.getConfigs("GSTR6A",
				"gstr6a.manual.upload");
		boolean isManualUplaod = configMap.get("gstr6a.manual.upload") == null
				? false
				: Boolean.valueOf(
						configMap.get("gstr6a.manual.upload").getValue());

		if (isManualUplaod) {
			GSTNDetailEntity entity = gstnDetailRepo
					.findByGstinAndIsDeleteFalse(dto.getGstin());

			String paramValue = entityConfigRepo
					.findByOptedforAP(entity.getEntityId());
			paramValue = paramValue != null ? paramValue : "B";
			/**
			 * Answer B informs that No Handshake table insert
			 */
			if ("B".equals(paramValue)) {

				LOGGER.error(
						"I27 answer is B so No Hand shake table insert made for the Entity {} ",
						entity.getEntityId());
			} else {
				AutoReconStatusEntity reconStatus = new AutoReconStatusEntity();
				reconStatus.setGstin(dto.getGstin());
				reconStatus.setEntityId(entity.getEntityId());
				reconStatus.setDate(LocalDate.now());
				reconStatus
						.setGet2aStatus(APIConstants.INITIATED.toUpperCase());
				reconStatus.setGet2aInitaitedOn(LocalDateTime.now());
				reconStatus.setGetEvent("D");
				reconStatus.setNumOfTaxPeriods(1);
				autoReconStatusRepo.save(reconStatus);
			}
		}
		// InActivating Previous Batch Records
		batchRepo.softlyDelete(dto.getType().toUpperCase(),
				APIConstants.GSTR6A_UPLOAD.toUpperCase(), dto.getGstin(),
				dto.getReturnPeriod());
		try {
			GetAnx1BatchEntity batch = batchUtil.makeBatchGstr1(dto,
					dto.getType(), APIConstants.GSTR6A_UPLOAD.toUpperCase());
			// Save new Batch
			batch = batchRepo.save(batch);
			dto.setBatchId(batch.getId());
		} catch (Exception e) {
			throw new AppException("Exception", e);

		}
		return dto;

	}

}
