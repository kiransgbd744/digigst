package com.ey.advisory.controller;

import static com.ey.advisory.common.GSTConstants.AT;
import static com.ey.advisory.common.GSTConstants.ATA_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.AT_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.B2CS;
import static com.ey.advisory.common.GSTConstants.B2CS_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.COMPREHENSIVE_RAW;
import static com.ey.advisory.common.GSTConstants.COMPREHENSIVE_RAW_1A;
import static com.ey.advisory.common.GSTConstants.EINVOICE_RECON;
import static com.ey.advisory.common.GSTConstants.EINVOICE_RECON_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.EINVOICE_RECON_FOLDER;
import static com.ey.advisory.common.GSTConstants.FOLDER_NAME;
import static com.ey.advisory.common.GSTConstants.GSTR1;
import static com.ey.advisory.common.GSTConstants.GSTR1A;
import static com.ey.advisory.common.GSTConstants.HSNUPLOAD;
import static com.ey.advisory.common.GSTConstants.HSN_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INVOICE;
import static com.ey.advisory.common.GSTConstants.INVOICE_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.NILNONEXMPT;
import static com.ey.advisory.common.GSTConstants.NIL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.OUTWARD;
import static com.ey.advisory.common.GSTConstants.OUTWARD_1A;
import static com.ey.advisory.common.GSTConstants.RAW;
import static com.ey.advisory.common.GSTConstants.RAW_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.TXPD;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.ImsReconStatusConfigRepository;
import com.ey.advisory.app.dms.DmsGstr1FileUploadServiceImpl;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
/*
 * This Class represent controller for uploading files from various sources and
 * upload into Document Repository
 */
@RestController
@Slf4j
public class AllFileUploadController {

	@Autowired
	@Qualifier("DefaultGstr1FileUploadService")
	private Gstr1FileUploadService gstr1FileUploadService;
	
	@Autowired
	@Qualifier("DmsGstr1FileUploadServiceImpl")
	private DmsGstr1FileUploadServiceImpl dmsGstr1FileUploadService;

	@Autowired
	@Qualifier("ImsReconStatusConfigRepository")
	ImsReconStatusConfigRepository imsReconRepo;
	
	private static List<String> autoimsStatusList = Arrays
			.asList("INITIATED", "INPROGRESS");
	
	/**
	 * Raw File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = { "/ui/webRawUploadDocuments",
			"/ui/webGstr1RawUploadDocuments",
			"/ui/webEinvoiceRawUploadDocuments" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request, 
			@RequestParam("file-data") String dmsRuleId) throws Exception {
		return fileUploads(files, request, dmsRuleId);
	}

	@PostMapping(value = {
			"/ui/webGstr1AEinvoiceRawUploadDocuments" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadGstr1ADocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request,
			@RequestParam("file-data") String dmsRuleId) throws Exception {
		return fileUploads(files, request, dmsRuleId);
	}
	
	@PostMapping(value = { "/api/webRawUploadDocumentsApi",
			"/api/webGstr1RawUploadDocumentsApi",
			"/api/webEinvoiceRawUploadDocumentsApi" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadAPIDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request) throws Exception {
		return fileUploads(files, request, null);
	}

	private ResponseEntity<String> fileUploads(MultipartFile[] files,
			HttpServletRequest request, String dmsRuleId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"FILE_UPLOAD_START", "Gstr1FileUploadController", "fileUploads",
				null);
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = RAW_FILE_NAME;
		String fileType = null;
		String dataType = null;
		String source = null;
		String path = request.getServletPath();
		boolean isDmsFile=false;
		if ("/ui/webRawUploadDocuments.do".equals(path)) {
			fileType = RAW;
			dataType = OUTWARD;
		}
		if ("/ui/webGstr1RawUploadDocuments.do".equals(path)) {
			fileType = RAW;
			dataType = GSTR1;
		}
		if ("/ui/webEinvoiceRawUploadDocuments.do".equals(path)) {
			fileType = COMPREHENSIVE_RAW;
			dataType = OUTWARD;
			source =  JobStatusConstants.WEB_UPLOAD;
		}
		if ("/api/webRawUploadDocumentsApi.do".equals(path)) {
			fileType = RAW;
			dataType = OUTWARD;
			source = JobStatusConstants.SFTP_WEB_UPLOAD;
		}
		if ("/api/webGstr1RawUploadDocumentsApi.do".equals(path)) {
			fileType = RAW;
			dataType = GSTR1;
			source = JobStatusConstants.SFTP_WEB_UPLOAD;
		}
		if ("/api/webEinvoiceRawUploadDocumentsApi.do".equals(path)) {
			fileType = COMPREHENSIVE_RAW;
			dataType = OUTWARD;
			source = JobStatusConstants.SFTP_WEB_UPLOAD;
			MultipartFile file = files[0];
			String fileName = FilenameUtils.getName(file.getOriginalFilename());
			isDmsFile = fileName.toLowerCase().startsWith("map_");
		}

		if ("/ui/webGstr1AEinvoiceRawUploadDocuments.do".equals(path)) {
			fileType = COMPREHENSIVE_RAW_1A;
			dataType = OUTWARD_1A;
			source =  JobStatusConstants.WEB_UPLOAD;
		}
		
		ResponseEntity<String> resp = null;

		if (((COMPREHENSIVE_RAW.equalsIgnoreCase(fileType)
		        && OUTWARD.equalsIgnoreCase(dataType)) ||
		     (COMPREHENSIVE_RAW_1A.equalsIgnoreCase(fileType)
		        && OUTWARD_1A.equalsIgnoreCase(dataType))) 
		    && (((dmsRuleId != null) && (!dmsRuleId.isEmpty())) || (isDmsFile))){

		 resp = dmsGstr1FileUploadService.uploadDocuments(
				files, folderName, uniqueName, userName, fileType, dataType,
				source, dmsRuleId);
		 LOGGER.error("OUTWARD_DMSPATH-IF Block executed");
		}
		else {
		 resp = gstr1FileUploadService.uploadDocuments(
				files, folderName, uniqueName, userName, fileType, dataType,
				source);
		 LOGGER.error("OUTWARD_DIGIPATH-ELSE Block executed");
		}
		
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"FILE_UPLOAD_END", "Gstr1FileUploadController", "fileUploads",
				null);
		return resp;
	}

	/**
	 * Gstr1A Upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = { "/ui/webGstr1aB2csUploadDocuments",
			"/ui/webGstr1aAtUploadDocuments","/ui/webGstr1aTxpdUploadDocuments",
			"/ui/webGstr1aInvoiceUploadDocuments","/ui/webGstr1aNilUploadDocuments",
			"/ui/webGstr1aHsnUploadDocuments"}, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadB2CSDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request) throws Exception {
		return gstr1aOutwardfileUploads(files, request);
	}

	private ResponseEntity<String> gstr1aOutwardfileUploads(MultipartFile[] files,
			HttpServletRequest request) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"FILE_UPLOAD_START", "Gstr1FileUploadController", "fileUploads",
				null);
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String source = JobStatusConstants.WEB_UPLOAD;		
		String uniqueName = null;
		String fileType = null;
		String dataType = GSTR1A;
		
		String path = request.getServletPath();
		if ("/ui/webGstr1aB2csUploadDocuments.do".equals(path)) {
			fileType = GSTConstants.B2CS;
			uniqueName = GSTConstants.B2CS_FILE_NAME_1A;
		}
		if ("/ui/webGstr1aAtUploadDocuments.do".equals(path)) {
			fileType = GSTConstants.AT;
			uniqueName = GSTConstants.AT_FILE_NAME_1A;
		}
		if ("/ui/webGstr1aTxpdUploadDocuments.do".equals(path)) {
			fileType = GSTConstants.TXPD;
			uniqueName = GSTConstants.ATA_FILE_NAME_1A;
		}
		if ("/ui/webGstr1aInvoiceUploadDocuments.do".equals(path)) {
			fileType = GSTConstants.INVOICE;
			uniqueName = GSTConstants.INVOICE_FILE_NAME_1A;
		}
		if ("/ui/webGstr1aNilUploadDocuments.do".equals(path)) {
			fileType = GSTConstants.NILNONEXMPT;
			uniqueName = GSTConstants.NIL_FILE_NAME_1A;
		}
		if ("/ui/webGstr1aHsnUploadDocuments.do".equals(path)) {
			fileType = GSTConstants.HSNUPLOAD;
			uniqueName = GSTConstants.HSN_FILE_NAME_1A;
		}

		ResponseEntity<String> resp = gstr1FileUploadService.uploadDocuments(
				files, folderName, uniqueName, userName, fileType, dataType,
				source);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"FILE_UPLOAD_END", "Gstr1FileUploadController", "fileUploads",
				null);
		return resp;
	}

	/**
	 * B2CS upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */
	
	@PostMapping(value = "/ui/webB2csUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadB2CSDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();

		String folderName = FOLDER_NAME;
		String uniqueName = B2CS_FILE_NAME;
		String fileType = B2CS;
		String dataType = GSTR1;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Adavnce Received upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webAtUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadAtDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = AT_FILE_NAME;
		String fileType = AT;
		String dataType = GSTR1;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Advance adjustments upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webTxpdUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadAtaDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = ATA_FILE_NAME;
		String fileType = TXPD;
		String dataType = GSTR1;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Invoice upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webInvoiceUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadInvoiceDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = INVOICE_FILE_NAME;
		String fileType = INVOICE;
		String dataType = GSTR1;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Nil upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webOldNilUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadNilOldDocuments(

			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = GSTConstants.OLD_NIL_FILE_NAME;
		String fileType = NILNONEXMPT;
		String dataType = GSTR1;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Nil upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webNilUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadNilDocuments(

			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = NIL_FILE_NAME;
		String fileType = NILNONEXMPT;
		String dataType = GSTR1;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Hsn Upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webHsnUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadHSNDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = HSN_FILE_NAME;
		String fileType = HSNUPLOAD;
		String dataType = GSTR1;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Hsn Upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/gstr3bWebUploads", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> GSTR3BWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = GSTConstants.GSTR_3B_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR3B_FILE_NAME;
		String fileType = GSTConstants.GSTR3B_ITC_4243;
		String dataType = GSTConstants.GSTR3B;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Cewb webupload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/cewbWebUploads", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> cewbBWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = GSTConstants.CEWB_FOLDER_NAME;
		String uniqueName = GSTConstants.CEWB_FILE_NAME;
		String fileType = GSTConstants.CEWB_FILE;
		String dataType = GSTConstants.EWB;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * itc04WebUploads
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = { "/ui/itc04WebUploads",
			"/api/itc04WebUploads" }, produces = {
					MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> itc04WebUploads(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request) throws Exception {

		String source = null;
		String path = request.getServletPath();
		if ("/api/itc04WebUploads.do".equals(path)) {
			source = JobStatusConstants.SFTP_WEB_UPLOAD;
		}
		String fileType = GSTConstants.ITC04_FILE;
		String dataType = GSTConstants.OTHERS;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = GSTConstants.ITC04_FOLDER_NAME;
		String uniqueName = GSTConstants.ITC04_FILE_NAME;

		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, source);

	}

	/**
	 * TdsTcsWebUploads
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/tcsAndTdsWebUploads", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> TcsAndTdsWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = GSTConstants.GSTR2X_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2X_FOLDER_NAME;
		String fileType = APIConstants.TCSANDTDS;
		String dataType = GSTConstants.OTHERS;
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * Einvoice_Recon Upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webEinvoiceReconUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadEinvoiceReconDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();

		String folderName = EINVOICE_RECON_FOLDER;
		String uniqueName = EINVOICE_RECON_FILE_NAME;
		String fileType = EINVOICE_RECON;
		String dataType = GSTR1;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * GSTR-9 Inward and Outward file uploads
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/gstr9InwardOutwardWebUploads", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> gstr9InwardOutwardWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		// String userName = httpServletRequest.getUserPrincipal().getName();

		String folderName = GSTConstants.GSTR9_FOLDER_NAME_INOUT;
		String uniqueName = GSTConstants.GSTR9;
		String fileType = GSTConstants.INWARD_OUTWARD;
		String dataType = GSTConstants.GSTR9;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * 180 days reversal response upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/reversal180DaysResponseUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> reversal180ResponseUpload(
			@RequestParam("file") MultipartFile[] files) throws Exception {

		String folderName = GSTConstants.REVERSAL_180_DAYS_RESPONSE_FOLDER;
		String uniqueName = GSTConstants.REVERSAL_180_DAYS_RESPONSE_FILE_NAME;
		String fileType = GSTConstants.REVERSAL_180_DAYS_RESPONSE;
		String dataType = GSTConstants.GSTR3B;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * GSTR2A 177 format response upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/gstr2AResponseUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> gstr2AResponseUpload(
			@RequestParam("file") MultipartFile[] files) throws Exception {

		String folderName = ConfigConstants.GSTR2USERRESPONSEUPLOADS;
		String uniqueName = GSTConstants.GSTR2A_USERRESPONSE_FILE_NAME;
		String fileType = "RECON_RESPONSE";
		String dataType = GSTConstants.INWARD;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

	/**
	 * GSTR2BPR 171 format response upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/gstr2bprResponseUpload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2bprResponseUpload(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") Long entityId) throws Exception {

		if (entityId == null) {
			Gson gson = GsonUtil.newSAPGsonInstance();

			APIRespDto dto = new APIRespDto("Failed", "EntityID is empty");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "EntityID is empty";
			LOGGER.error(msg);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

		String folderName = ConfigConstants.GSTR2BPR_USERRESPONSE_UPLOADS;
		String uniqueName = GSTConstants.GSTR2B_PR_USERRESPONSE_FILE_NAME;
		String fileType = "2BPR_RECON_RESPONSE";
		String dataType = GSTConstants.INWARD;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, entityId.toString());
	}

	/**
	 * GSTR2A 95 format response upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/gstr2AERPResponseUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> gstr2AERPResponseUpload(
			@RequestParam("file") MultipartFile[] files) throws Exception {

		String folderName = ConfigConstants.GSTR2USERRESPONSEUPLOADS;
		String uniqueName = GSTConstants.GSTR2A_ERP_USERRESPONSE_FILE_NAME;
		String fileType = "RECON_RESPONSE_ERP";
		String dataType = GSTConstants.INWARD;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}
	
	//IMS Response Upload
	@PostMapping(value = "/ui/imsResponseUpload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstrImsResponseUpload(
			@RequestParam("file") MultipartFile[] files) throws Exception {

		String folderName = ConfigConstants.IMS_USERRESPONSE_UPLOADS;
		String uniqueName = GSTConstants.IMS_USERRESPONSE_FILE_NAME;
		String fileType = "IMS";
		String dataType = GSTConstants.INWARD;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}
	
	

	/**
	 * GSTR2BPR+IMS format response upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/gstr2bprImsResponseUpload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2bprImsResponseUpload(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") Long entityId) throws Exception {

		if (entityId == null) {
			Gson gson = GsonUtil.newSAPGsonInstance();

			APIRespDto dto = new APIRespDto("Failed", "EntityID is empty");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "EntityID is empty";
			LOGGER.error(msg);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

		// monitor ims changes
		int imstatusCount = imsReconRepo
				.findByStatusIn(autoimsStatusList);

		if (imstatusCount != 0) {
			String msg = String.format(
					"Auto IMS action based on Auto recon parameters is in progress,"
					+ " Please try after sometime.");
			LOGGER.error(msg);
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto dto1 = new APIRespDto("Information",msg);
			JsonObject resp1 = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto1);
			resp1.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp1.add("resp", respBody);
			
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		}
		String folderName = ConfigConstants.GSTR2BPR_IMS_USERRESPONSE_UPLOADS;
		String uniqueName = GSTConstants.GSTR2B_PR_IMS_USERRESPONSE_FILE_NAME;
		String fileType = "2BPR_IMS_RECON_RESPONSE";
		String dataType = GSTConstants.INWARD;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, entityId.toString());
	}


	/**
	 * GSTR2APR+IMS format response upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/gstr2aprImsResponseUpload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2aprImsResponseUpload(
			@RequestParam("file") MultipartFile[] files) throws Exception {

		// monitor ims changes
		int imstatusCount = imsReconRepo
				.findByStatusIn(autoimsStatusList);

		if (imstatusCount != 0) {
			String msg = String.format(
					"Auto IMS action based on Auto recon parameters is in progress,"
					+ " Please try after sometime.");
			LOGGER.error(msg);
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto dto1 = new APIRespDto("Information",msg);
			JsonObject resp1 = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto1);
			resp1.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp1.add("resp", respBody);
			
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		}
		String folderName = ConfigConstants.GSTR2APR_IMS_USERRESPONSE_UPLOADS;
		String uniqueName = GSTConstants.GSTR2A_PR_IMS_USERRESPONSE_FILE_NAME;
		String fileType = "2APR_IMS_RECON_RESPONSE"; //"2APR_IMS_RECON_RESPONSE";
		String dataType = GSTConstants.INWARD;
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		return gstr1FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

}
