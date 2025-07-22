package com.ey.advisory.controller.gl.recon;

import static com.ey.advisory.common.GSTConstants.GL_DUMP_FILE_NAME;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.dms.DmsGstr1FileUploadServiceImpl;
import com.ey.advisory.app.glrecon.GlReconFileUploadService;
import com.ey.advisory.app.glrecon.GlReconGetFileUploadStatusService;
import com.ey.advisory.app.glrecon.GlReconSRFileCreationService;
import com.ey.advisory.app.glrecon.SFTPPushFileDao;
import com.ey.advisory.app.glrecon.SRFileCreationDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *	GL Dump File Upload and GL Initiate recon controller
 */

@Slf4j
@RestController
public class GLReconGLvsSRFileUploadController {

	private static final String FOLDER_NAME_GL = "GLReconWebUploads";
	@Autowired
	@Qualifier("GlReconFileUploadServiceImpl")
	private GlReconFileUploadService service;
	
	@Autowired
	CommonUtility commonUtility;
	
	@Autowired
	@Qualifier("GlRecongetFileUploadStatusServiceImpl")
	private GlReconGetFileUploadStatusService fileStatusService;
	
	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository GlReconFileStatusRepository;
	
	@Autowired
	@Qualifier("GlReconCreateSRFileServiceImpl")
	private GlReconSRFileCreationService srFileSftpPushService;
	
	@Autowired
	@Qualifier("SRFileCreationDaoImpl")
	private SRFileCreationDao srFileCreationService;
	
	@Autowired
	@Qualifier("SFTPPushFileDaoImpl")
	private SFTPPushFileDao sftpPushFileService;
	
	@Autowired
	@Qualifier("DmsGstr1FileUploadServiceImpl")
	private DmsGstr1FileUploadServiceImpl dmsGstr1FileUploadService;
	
	
	@PostMapping(value = { "/ui/webGlDumpFileUpload" }, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String ruleId,
			HttpServletRequest request) throws Exception {
		if((ruleId != null) && (!ruleId.isEmpty())) {
			return glReconDumpFileUploadApi(files, request, ruleId,JobStatusConstants.WEB_UPLOAD);
		} else {
			return glReconDumpFileUpload(files, request, ruleId);
		}
	}
	
	@PostMapping(value = { "/api/webGlDumpFileUploadApi" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadAPIDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request) throws Exception {
		return glReconDumpFileUploadApi(files, request, null,JobStatusConstants.SFTP_WEB_UPLOAD);
	}

	private ResponseEntity<String> glReconDumpFileUploadApi(MultipartFile[] files,
			HttpServletRequest request, String dmsRuleId,String source) throws Exception {
		
			LOGGER.error("Begining from GL file uploads Documents");
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"FILE_UPLOAD_START", "GLReconGLvsSRFileUploadController",
				"fileUploads", null);
		String tenantId = TenantContext.getTenantId();
		
			LOGGER.error("tenantId: " + tenantId);
		
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME_GL;
		String fileType = "GL Dump";

		String uniqueName = GL_DUMP_FILE_NAME;
		String dataType = null;
		
		ResponseEntity<String> resp = dmsGstr1FileUploadService.uploadGlDocuments(
				files, folderName, uniqueName, userName, fileType, dataType,
				source, dmsRuleId);

		return resp;
	}
	
	private ResponseEntity<String> glReconDumpFileUpload(MultipartFile[] files,
			HttpServletRequest request, String ruleId) throws Exception {
		
			LOGGER.error("Begining from GL file uploads Documents");
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"FILE_UPLOAD_START", "GLReconGLvsSRFileUploadController",
				"fileUploads", null);
		String tenantId = TenantContext.getTenantId();
		
			LOGGER.error("tenantId: " + tenantId);
		
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME_GL;
		String fileType = "GL Dump";

		ResponseEntity<String> resp = service.uploadDocuments(files, folderName,
				fileType, userName, ruleId);

		return resp;
	}

	@PostMapping(value = { "/ui/initiateGlRecon" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> InitiateReconcile(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();
		try {
			String toTaxPeriod = null;
			String fromTaxPeriod = null;
			String reconType = "GL Recon";
			String configId = null;
			String transactionType=null;

				String msg = String
						.format("Begin GLReconGLvsSRFileUploadController"
								+ ".InitiateReconcile() : %s", jsonString);
				LOGGER.error(msg);
			

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String entId = requestObject.get("entityId").getAsString();
			Long entityId = Long.valueOf(entId);
			JsonArray gstins = requestObject.getAsJsonArray("gstins");

			if (requestObject.has("toTaxPeriod")) {
				toTaxPeriod = requestObject.get("toTaxPeriod").getAsString();
			}
			if (requestObject.has("fromTaxPeriod")) {
				fromTaxPeriod = requestObject.get("fromTaxPeriod")
						.getAsString();
			}
			if (requestObject.has("reconType")) {
				reconType = requestObject.get("reconType").getAsString();
			}
			if (requestObject.has("transactionType")) {
				transactionType = requestObject.get("transactionType").getAsString();
			}
			
			
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			
			Gson googleJson = new Gson();
			
			ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);

			if (CollectionUtils.isEmpty(gstinlist))
				throw new AppException("User Does not have any gstin");
			
			if (toTaxPeriod != null && fromTaxPeriod != null) {
				
					 msg = String.format("InitiateGlMatching "
							+ "Parameters  " + "toReturnPeriod %s"
							+ " To fromReturnPeriod %s, reconType %s " + ": ",
							toTaxPeriod, fromTaxPeriod, reconType);
					LOGGER.error(msg);
				
			
		
			 configId = srFileSftpPushService.createSRFile(entityId, gstinlist, fromTaxPeriod.substring(0, 4)+fromTaxPeriod.substring(4,6), toTaxPeriod.substring(0, 4)+toTaxPeriod.substring(4,6),transactionType);
			}
			/*srFileCreationService.generateReports(Long.valueOf(configId));
			
			sftpPushFileService.sftpFilePushService(Long.valueOf(configId));
			*/
			
			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree("Recon Requested for Recon ID - "+configId);
			gstinDetResp.add("status", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			
				msg = String.format(
						"End Gstr2InitiateMatchingReconAPCheckController"
								+ ".InitiateReconcile() before returning response : %s",
						gstinDetResp);
				LOGGER.error(msg);
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception on GLReconGLvsSRFileUploadController" + ex;
			LOGGER.error(msg, ex);
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}	

}
