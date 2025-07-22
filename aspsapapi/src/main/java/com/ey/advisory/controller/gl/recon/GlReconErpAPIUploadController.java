package com.ey.advisory.controller.gl.recon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.DmsRuleMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.app.dms.DmsUtils;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.azure.fileshare.utils.AzureFileShareUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@RestController
@Slf4j
public class GlReconErpAPIUploadController {
	private static final String FOLDER_NAME_GL = "GLReconWebUploads";
	final long MAX_SIZE = 5 * 1024 * 1024;

	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glFileStatusRepo;

	@Autowired
	AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("AzureFileShareUtil")
	private AzureFileShareUtil azureFileShare;
	
	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;
	
	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;
	
	@Autowired
	@Qualifier("DmsRuleMasterRepository")
	private DmsRuleMasterRepository dmsRuleMasterRepository;

	@PostMapping(value = "/api/getGlDumpData", produces = {
			MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> getGlDumpData(
			@RequestParam("file") MultipartFile inputfile,
			ServletRequest request) {
		JsonObject resp = new JsonObject();

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String companyCode = req.getHeader("companyCode");
		String ruleName = req.getHeader("ruleName");
		String sourceId = req.getHeader("sourceId");
		String refId = null;
		try {
			String uuid = DmsUtils.generateUUID();
			User user = SecurityContext.getUser();
			String userName = user != null
					? user.getUserPrincipalName()
					: "SYSTEM";
			String fileType = "GL Dump";
			String folderName = FOLDER_NAME_GL;

			if (Strings.isNullOrEmpty(payloadId)
					|| Strings.isNullOrEmpty(ruleName)) {
				String msg = "One or more mandatory attributes are missing";
				LOGGER.error(msg);

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);

				JsonObject respMsg = new JsonObject();
				respMsg.addProperty("errCode", "ER0001");
				respMsg.addProperty("errMsg", msg);

				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", new Gson().toJsonTree(respMsg));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			File tempDir = Files.createTempDir();

			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());

			if (inputfile.getSize() > MAX_SIZE) {
				String msg = "File size exceeds the 5MB limit";
				LOGGER.error(msg);

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);

				JsonObject respMsg = new JsonObject();
				respMsg.addProperty("errCode", "ER0002");
				respMsg.addProperty("errMsg", msg);

				resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", new Gson().toJsonTree(respMsg));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (Strings.isNullOrEmpty(extension)
					|| (!"xlsx".equalsIgnoreCase(extension)
							&& !"csv".equalsIgnoreCase(extension)
							&& !"txt".equalsIgnoreCase(extension))) {
				String msg = "Invalid File Format";
				LOGGER.error(msg);

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);

				JsonObject respMsg = new JsonObject();
				respMsg.addProperty("errCode", "ER0003");
				respMsg.addProperty("errMsg", msg);

				resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", new Gson().toJsonTree(respMsg));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.BAD_REQUEST);
			}

			String tempFileName = tempDir.getAbsolutePath() + File.separator
					+ inputfile.getOriginalFilename();
			File tempFile = new File(tempFileName);
			inputfile.transferTo(tempFile);
				//uploading DMS file
			Pair<String, String> uploadedDocDetails = DocumentUtility
					.uploadFile(tempFile, "GLRECONFILES");
			String erpFiledocId = uploadedDocDetails.getValue1();
			//calling DMS
			String fileName = gstr1FileUploadUtil.getFileName(inputfile, fileType);
			File convertedFile = convertMultipartFileToFile(inputfile);
			String uploadedFileName = azureFileShare
					.uploadFileToAzure(convertedFile, fileName, uuid);
			LOGGER.error("File uploaded to Azure File Share {} ",
					uploadedFileName);
			
			
			Pair<String, Long> statusEntry = createFileStatusEntry(payloadId, sourceId, companyCode,
			        ruleName, erpFiledocId, userName, fileType, fileName, ruleName,uuid);

			 refId = statusEntry.getValue0(); // or getKey()
			Long fileId = statusEntry.getValue1();         // or getValue()

			
			LOGGER.error("File uploaded to Azure File Share {} ", uploadedFileName);
			String paramJson = null;
			String groupCode = TenantContext.getTenantId();
			String uniqueFileName = tempFileName;
			String jobName = GSTConstants.DMS_FILE_UPLOAD;
			String jobCategory = JobConstants.GLRECON_DUMP_FILEUPLOAD;

			paramJson = "{\"filePath\":\"" + folderName + "\","
					+ "\"fileId\":\"" + fileId + "\","
					+ "\"fileName\":\"" + fileName + "\","
					+ "\"jobCateg\":\"" + jobCategory + "\"}";
			gstr1FileUploadJobInsertion.fileUploadJob(paramJson,
					jobName, userName);

	
			String msg = "File has been uploaded successfully";
			LOGGER.error(msg);
			JsonObject hdrMsg = new JsonObject();
			hdrMsg.addProperty("status", "S");
			hdrMsg.addProperty("payloadId", payloadId);
			JsonObject respMsg = new JsonObject();
			respMsg.addProperty("refId", refId);
			respMsg.addProperty("msg", msg);

			resp.add("hdr", new Gson().toJsonTree(hdrMsg));
			resp.add("resp", new Gson().toJsonTree(respMsg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Exception while uploading file";
			LOGGER.error(msg);
			JsonObject hdrMsg = new JsonObject();
			hdrMsg.addProperty("status", "E");
			hdrMsg.addProperty("payloadId", payloadId);
			JsonObject respMsg = new JsonObject();
			respMsg.addProperty("errMsg", msg);

			resp.add("hdr", new Gson().toJsonTree(hdrMsg));
			resp.add("resp", new Gson().toJsonTree(respMsg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}

	private Pair<String, Long> createFileStatusEntry(String payloadId, String sourceId,
	        String companyCode, String ruleMap, String erpDocId,
	        String userName, String fileType, String fileName,
	        String ruleName,String uuid) {
		
		 Long ruleId = dmsRuleMasterRepository.findRuleIdByRuleName(ruleName);
	    GlReconFileStatusEntity apiStatus = new GlReconFileStatusEntity();
	  //  UUID uuid = UUID.randomUUID();
	    LocalDateTime localDate = LocalDateTime.now();

	    apiStatus.setSource("API");
	    apiStatus.setErpDmsGlDumpDocId(erpDocId);
	    apiStatus.setFileStatus(JobStatusConstants.UPLOADED);
	    apiStatus.setTransformationRuleName(ruleName);
	    apiStatus.setErpPayloadId(payloadId);
	    apiStatus.setErpSourceId(sourceId);
	    apiStatus.setErpCompanyCode(companyCode);
	    apiStatus.setPayloadId(uuid);
	    apiStatus.setIsActive(false);
	    apiStatus.setCraetedBy(userName);
	    apiStatus.setUpdatedOn(localDate);
	    apiStatus.setFileType(fileType);
	    apiStatus.setFileName(fileName);
	    apiStatus.setTransformationRule(ruleId.toString());
	    apiStatus.setTransformationStatus("Transformation in progress");

	    glFileStatusRepo.save(apiStatus);

	    return new Pair<>(apiStatus.getPayloadId(), apiStatus.getId());
	}

	
	public static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
		File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(multipartFile.getBytes());
		}
		return convertedFile;
	}

}
