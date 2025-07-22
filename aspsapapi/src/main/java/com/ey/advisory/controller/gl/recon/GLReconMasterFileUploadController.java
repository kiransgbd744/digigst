package com.ey.advisory.controller.gl.recon;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.data.entities.client.asprecon.GlReconReportConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.glrecon.GlFileStatusDto;
import com.ey.advisory.app.glrecon.GlFileStatusReqDto;
import com.ey.advisory.app.glrecon.GlReconFileUploadService;
import com.ey.advisory.app.glrecon.GlReconGetFileUploadStatusService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain GL Recon MASTER FILES upload, Download and Uploaded File
 *         Status controller
 */

@Slf4j
@RestController
public class GLReconMasterFileUploadController {

	private static final String FOLDER_NAME_GL = "GLReconWebUploads";
	@Autowired
	@Qualifier("GlReconFileUploadServiceImpl")
	private GlReconFileUploadService service;

	@Autowired
	@Qualifier("GlRecongetFileUploadStatusServiceImpl")
	private GlReconGetFileUploadStatusService fileStatusService;

	@Autowired
	private GlReconFileStatusRepository glReconFileStatusRepo;

	@Autowired
	private GLReconReportConfigRepository glReconConfigRepo;

	@PostMapping(value = { "/ui/webBusinessTypeDocument",
			"/ui/webDocumentTypeDocument", "/ui/webSupplyTypeDocument",
			"/ui/webGLTypeDocument", "/ui/webTaxTypeDocument",
			"/ui/webGLmappingDocument" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String entityId,
			HttpServletRequest request) throws Exception {
		return glReconFileUpload(files, request, Long.valueOf(entityId));
	}

	private ResponseEntity<String> glReconFileUpload(MultipartFile[] files,
			HttpServletRequest request, Long entityId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from GL file uploads Documents");
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"FILE_UPLOAD_START", "Gstr1FileUploadController", "fileUploads",
				null);
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName()
				: "P2001353321";
		String folderName = FOLDER_NAME_GL;
		String fileType = null;
		String path = request.getServletPath();

		if ("/ui/webBusinessTypeDocument.do".equals(path)) {
			fileType = "Business_Unit_code";
		}
		if ("/ui/webDocumentTypeDocument.do".equals(path)) {
			fileType = "Document_type";
		}
		if ("/ui/webSupplyTypeDocument.do".equals(path)) {
			fileType = "Supply_Type";
		}
		if ("/ui/webGLTypeDocument.do".equals(path)) {
			fileType = "GL_Code_Mapping_Master_GL";
		}
		if ("/ui/webTaxTypeDocument.do".equals(path)) {
			fileType = "Tax_code";
		}
		if ("/ui/webGLmappingDocument.do".equals(path)) {
			fileType = "GL_dump_mapping_file";
		}

		ResponseEntity<String> resp = service.uploadDocuments(files, folderName,
				fileType, userName, entityId.toString());

		return resp;
	}

	@PostMapping(value = { "/ui/glFileStatus" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> fileStatus(@RequestBody String jsonString) {
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqObject = requestObject.get("req").getAsJsonObject();

			GlFileStatusReqDto fileSatusDto = gson.fromJson(reqObject,
					GlFileStatusReqDto.class);
			/*
			 * Long entityId = reqObject.get("entityId").getAsLong(); Boolean
			 * flag = reqObject.get("flag").getAsBoolean();
			 */
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			if (userName == null)
				throw new AppException("Invalid User");
			Pair<List<GlFileStatusDto>, Integer> filesEntry = fileStatusService
					.getFileUploadStatus(fileSatusDto, pageNum, pageSize);

			if (LOGGER.isDebugEnabled()) {
				String msg = "InitiateReconReportRequestStatusServiceImpl"
						+ ".getReportRequestStatus Preparing Response Object";
				LOGGER.debug(msg);
			}

			String responseData = gson.toJson(filesEntry.getValue0());
			JsonElement jsonElement = JsonParser.parseString(responseData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("GlReportsData", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(
							filesEntry.getValue1(), pageNum, pageSize, "S",
							"Successfully fetched GL fileUploads records")));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/glFileDownload")
	public void glFileDownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside glFileDownload method");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		try {
			String fileName = request.getParameter("fileName");

			String fileFolder = FOLDER_NAME_GL;
			String docId = null;
			List<GlReconFileStatusEntity> entity = glReconFileStatusRepo
					.findAllByFileName(fileName);

			Document document = null;

			docId= entity.get(0).getDocId();
			if (!Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}else {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			}

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error in downloading the file" + ex);
			throw new AppException(ex);
		}
	}

	@GetMapping(value = "/ui/glReconFileDownload")
	public void glReconFileDownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside glReconFileDownload method");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		try {
			String fileName = request.getParameter("fileName");
			
			LOGGER.error("filename-> {} ", fileName);
			
			String fileFolder = FOLDER_NAME_GL;
			List<GlReconReportConfigEntity> entity = glReconConfigRepo
					.findAllByFilePath(fileName);
			String docId = entity.get(0).getDocId();

			Document document = null;
			
			LOGGER.error("docId -->{} ",docId);
			
			if (!Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}else {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			}

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error in downloading the file" + ex);
			throw new AppException(ex);
		}
	}

}
