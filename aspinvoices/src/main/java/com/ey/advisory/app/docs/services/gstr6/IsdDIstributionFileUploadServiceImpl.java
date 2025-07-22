/*package com.ey.advisory.app.docs.services.gstr6;

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
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.Gstr6DistrbtnHeaderCheckService;
import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadUtil;
import com.ey.advisory.app.services.docs.TraverserFactory;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadUtil;
import com.ey.advisory.app.services.jobs.gstr1.JobConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component("IsdDIstributionFileUploadServiceImpl")
public class IsdDIstributionFileUploadServiceImpl implements IsdDIstributionFileUploadService{

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Gstr6DistrbtnHeaderCheckService")
	Gstr6DistrbtnHeaderCheckService gstr6DistrbtnHeaderCheckService;

	@Autowired
	@Qualifier("OutwardFileUploadUtil")
	private OutwardFileUploadUtil outwardFileUploadUtil;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IsdDIstributionFileUploadServiceImpl.class);

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList.of(
			"text/csv", "text/plain", "application/x-tika-ooxml",
			"application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("csv", "xlsx",
			"xlsm");

	@Override
	public ResponseEntity<String> fileUploadsAnnexure1(MultipartFile[] files,
			String fileType, String foldername, String userName,
			String uniqueName, String dataType) {
		// TODO Auto-generated method stub

		LOGGER.error(
				"inside fileUploadsAnnexure1 method and uniqueName is {}  ",
				uniqueName);

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
				newFolderProps.put(PropertyIds.NAME, foldername);
				Folder createdFolder = null;
				try {
					ItemIterable<CmisObject> folders = root.getChildren();
					for (CmisObject obj : folders) {
						if (obj instanceof Folder) {
							Folder folder = (Folder) obj;
							if (folder.getName().equalsIgnoreCase(foldername)) {
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
					
						String jobCategory = JobConstants.ISDDISTRIBUTIONFILEUPLOAD;
						
						gstr1FileUploadJobInsertion.fileUploadJob(
								paramJson, jobCategory, userName);

					java.util.List<Long> findByJobIds = asyncExecJobRepository
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
*/