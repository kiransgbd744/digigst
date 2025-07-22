package com.ey.advisory.app.services.jobs.gstr1;

import static com.ey.advisory.common.GSTConstants.VENDOR;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.VendorHeaderCheckService;
import com.ey.advisory.app.data.services.anx1.VendorFileUploadService;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
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
@Component("VendorFileUploadServiceImpl")
public class VendorFileUploadServiceImpl implements VendorFileUploadService {

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("VendorHeaderCheckService")
	private VendorHeaderCheckService vendorHeaderCheckService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorFileUploadServiceImpl.class);

	@Override
	public ResponseEntity<String> vendorProcessingFile(MultipartFile[] files,
			String fileType, String folderName, String userName,
			String uniqueName, String dataType, String groupCode,
			Long entityId) {
		LOGGER.error("Inside vendor file upload method and uniqueName is {}  ",
				uniqueName);

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
			TabularDataLayout layout = new DummyTabularDataLayout(7);

			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			long stTime = System.currentTimeMillis();
			LOGGER.info("Start Time " + stTime);
			traverser.traverse(inputStream, layout, rowHandler, null);

			Object[] getHeaders = rowHandler.getHeaderRow();

			Pair<Boolean, String> checkHeaderFormat = vendorHeaderCheckService
					.validate(getHeaders);
			if (checkHeaderFormat.getValue0()) {

				for (MultipartFile file : files) {
					String fileName = outwardFileUploadUtil.getFileName(file,
							fileType);
					Session openCmisSession = outwardFileUploadUtil
							.openCmisSession();
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
							createdFolder = root.createFolder(newFolderProps);
							LOGGER.debug("Folder Created");
						}
					} catch (CmisNameConstraintViolationException e) {
						LOGGER.error("Exception while creating folder", e);
					}

					// create a new file in the root folder
					Map<String, Object> properties = new HashMap<>();
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

							Document createdDocument = createdFolder
									.createDocument(properties, contentStream,
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

						String paramJson = "{\"filePath\":\""
								+ createdFolder.getName() + "\","
								+ "\"fileName\":\"" + fileName + "\","
								+ "\"entityId\":\"" + entityId + "\"}";

						if (uniqueName.equalsIgnoreCase(VENDOR)) {
							String jobCategory = JobConstants.MASTERVENDORFILEUPLOAD;
							gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
									jobCategory, userName);
						}
						java.util.List<Long> findByJobIds = asyncExecJobRepository
								.findByJobId(paramJson);
						for (Long jobId : findByJobIds) {
							AsyncExecJob asyncExceJob = asyncExecJobRepository
									.findByJobDetails(jobId);

							Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
									.getMasterFileStatusEntity(fileName,
											userName, asyncExceJob, fileType,
											dataType, entityId);
							fileStatus.setFileType(VENDOR);
							gstr1FileStatusRepository.save(fileStatus);
						}

					}
				}

				APIRespDto dto = new APIRespDto("Success",
						"Vendorfile uploaded successfully");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else {
				APIRespDto dto = new APIRespDto("Failed",
						"Please upload the correct Vendorfile.");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				String msg = "Please upload the correct Vendorfile.";
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				resp.add("resp", respBody);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Vendorfile uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading vendor files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
