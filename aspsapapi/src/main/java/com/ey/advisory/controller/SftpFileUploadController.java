package com.ey.advisory.controller;

import static com.ey.advisory.common.GSTConstants.ANN1_B2C_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_FOLDER_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_SHIPPING_BILL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_TABLE4_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.AT;
import static com.ey.advisory.common.GSTConstants.ATA_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.AT_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.B2C;
import static com.ey.advisory.common.GSTConstants.B2CS;
import static com.ey.advisory.common.GSTConstants.B2CS_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.COMPREHENSIVE_RAW;
import static com.ey.advisory.common.GSTConstants.FOLDER_NAME;
import static com.ey.advisory.common.GSTConstants.GSTR1;
import static com.ey.advisory.common.GSTConstants.HSNUPLOAD;
import static com.ey.advisory.common.GSTConstants.HSN_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INTEREST;
import static com.ey.advisory.common.GSTConstants.INTEREST_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INVOICE;
import static com.ey.advisory.common.GSTConstants.INVOICE_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INWARD;
import static com.ey.advisory.common.GSTConstants.NILNONEXMPT;
import static com.ey.advisory.common.GSTConstants.NIL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.OUTWARD;
import static com.ey.advisory.common.GSTConstants.RAW;
import static com.ey.advisory.common.GSTConstants.RAW_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.REFUNDS;
import static com.ey.advisory.common.GSTConstants.REFUNDS_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.RET;
import static com.ey.advisory.common.GSTConstants.RET1AND1A;
import static com.ey.advisory.common.GSTConstants.RET_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.RET_FOLDER_NAME;
import static com.ey.advisory.common.GSTConstants.SETOFFANDUTIL;
import static com.ey.advisory.common.GSTConstants.SETOFFANDUTIL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.SHIPPINGBILL;
import static com.ey.advisory.common.GSTConstants.TABLE3H3I;
import static com.ey.advisory.common.GSTConstants.TABLE3H3I_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.TABLE4;
import static com.ey.advisory.common.GSTConstants.TXPD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.services.gstr1fileupload.SftpFileUploadService;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;

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
public class SftpFileUploadController {

	@Autowired
	@Qualifier("DefaultSftpFileUploadService")
	private SftpFileUploadService sftpFileUploadService;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	/**
	 * Raw File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = { "/ui/webSftpRawUploadDocuments",
			"/ui/webSftpGstr1RawUploadDocuments",
			"/ui/webSftpEinvoiceRawUploadDocuments" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = RAW_FILE_NAME;
		String fileType = null;
		String dataType = null;
		String path = request.getServletPath();
		if ("/ui/webSftpRawUploadDocuments.do".equals(path)) {
			fileType = RAW;
			dataType = OUTWARD;
		}
		if ("/ui/webSftpGstr1RawUploadDocuments.do".equals(path)) {
			fileType = RAW;
			dataType = GSTR1;
		}
		if ("/ui/webSftpEinvoiceRawUploadDocuments.do".equals(path)) {
			fileType = COMPREHENSIVE_RAW;
			dataType = OUTWARD;
		}
		FileStatusPerfUtil.logEvent("FILE_UPLOAD_BEGIN", uniqueName);
		ResponseEntity<String> resp = sftpFileUploadService.uploadDocuments(
				files, folderName, uniqueName, userName, fileType, dataType);
		FileStatusPerfUtil.logEvent("FILE_UPLOAD_END", uniqueName);
		return resp;
	}

	/**
	 * B2cs Upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webSftpB2csUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadB2CSDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = B2CS_FILE_NAME;
		String fileType = B2CS;
		String dataType = GSTR1;
		return sftpFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

	/**
	 * Adavnce Received upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webSftpAtUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadAtDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = AT_FILE_NAME;
		String fileType = AT;
		String dataType = GSTR1;
		return sftpFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

	/**
	 * Advance adjustments upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webSftpTxpdUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadAtaDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = ATA_FILE_NAME;
		String fileType = TXPD;
		String dataType = GSTR1;
		return sftpFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

	/**
	 * Invoice upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webSftpInvoiceUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadInvoiceDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = INVOICE_FILE_NAME;
		String fileType = INVOICE;
		String dataType = GSTR1;
		return sftpFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

	/**
	 * Nil upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webSftpNilUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadNilDocuments(

			@RequestParam("file") MultipartFile[] files) throws Exception {
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = NIL_FILE_NAME;
		String fileType = NILNONEXMPT;
		String dataType = OUTWARD;
		return sftpFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

	/**
	 * Hsn Upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webSftpHsnUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> uploadHSNDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = FOLDER_NAME;
		String uniqueName = HSN_FILE_NAME;
		String fileType = HSNUPLOAD;
		String dataType = OUTWARD;
		return sftpFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

	@PostMapping(value = { "/ui/sftpInwardRawFileUploadDocuments",
			"/ui/sftpInward240RawFileUploadDocuments" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> anx2InwardRawFileUploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = GSTConstants.ANN2_FOLDER_NAME;
		String uniqueName = GSTConstants.ANN2_RAW_FILE_NAME;
		String fileType = GSTConstants.RAW;
		String dataType = GSTConstants.INWARD;
		String path = request.getServletPath();
		FileStatusPerfUtil.logEvent("FILE_UPLOAD_BEGIN", uniqueName);
		ResponseEntity<String> resp = null;

		if ("/ui/sftpInwardRawFileUploadDocuments.do".equals(path)) {
			resp = sftpFileUploadService.uploadDocuments(files, folderName,
					uniqueName, userName, fileType, dataType);
		} else {
			fileType = GSTConstants.COMPREHENSIVE_INWARD_RAW;
			uniqueName = GSTConstants.INWARD;
			resp = sftpFileUploadService.uploadDocuments(files, folderName,
					uniqueName, userName, fileType, dataType);
		}
		FileStatusPerfUtil.logEvent("FILE_UPLOAD_END", uniqueName);
		return resp;
	}

	/**
	 * Outward B2C File web uploads
	 */
	@RequestMapping(value = "/ui/outwardSftpB2c", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> b2cWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug("inside b2cWebUploads method and file type is {}  "
				+ "foldername is {} ", B2C, ANN1_FOLDER_NAME);

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);
		String fileType = B2C;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = ANN1_B2C_FILE_NAME;
		String dataType = OUTWARD;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);
	}

	/**
	 * Outward table 4 uploads
	 */

	@RequestMapping(value = "/ui/outwardSftpTable4", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> table4WebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug("inside table4WebUploads method and file type is {}  "
				+ "foldername is {} ", TABLE4, ANN1_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileType = TABLE4;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = ANN1_TABLE4_FILE_NAME;
		String dataType = OUTWARD;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * Outward table 3H AND 3I uploads
	 */

	@RequestMapping(value = "/ui/sftpTable3h3i", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> table3H3I(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug("inside table3H3I method and file type is {}  "
				+ "foldername is {} ", TABLE3H3I, ANN1_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileType = TABLE3H3I;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = TABLE3H3I_FILE_NAME;
		String dataType = INWARD;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * Outward Shipping Bill Details uploads
	 */

	@RequestMapping(value = "/ui/sftpShippingBills", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> shippingBillDetails(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug(
				"inside Shipping Bill Details  method and file type is {}  "
						+ "foldername is {} ",
				SHIPPINGBILL, ANN1_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileType = SHIPPINGBILL;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = ANN1_SHIPPING_BILL_FILE_NAME;
		String dataType = OUTWARD;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/*
	 * RET 1 VERTICAL UPLOADS
	 */

	/**
	 * RET - 1 AND 1A UPLOADS
	 */

	@RequestMapping(value = "/ui/sftpRet1And1AUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> ret1And1AWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug("inside ret1And1AWebUploads method and file type is {}  "
				+ "foldername is {} ", RET1AND1A, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileType = RET1AND1A;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = RET_FILE_NAME;
		String dataType = RET;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * RET - 1 AND 1A UPLOADS
	 */

	@RequestMapping(value = "/ui/sftpInterestAndLateFee", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> interstAndLateFee(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug("inside interstAndLateFee method and file type is {}  "
				+ "foldername is {} ", INTEREST, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileType = INTEREST;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = INTEREST_FILE_NAME;
		String dataType = RET;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * SET OFF AND UTILIZATIONS UPLOADS
	 */

	@RequestMapping(value = "/ui/sftpSetOffAndUtilizations", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> setOffAndUtilizations(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug("inside setOffAndUtilizations method and file type is {}  "
				+ "foldername is {} ", SETOFFANDUTIL, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileType = SETOFFANDUTIL;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = SETOFFANDUTIL_FILE_NAME;
		String dataType = RET;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * REFUNDS UPLOADS
	 */

	@RequestMapping(value = "/ui/sftpRefunds", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> refunds(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.debug("inside refunds method and file type is {}  "
				+ "foldername is {} ", REFUNDS, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileType = REFUNDS;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = REFUNDS_FILE_NAME;
		String dataType = RET;
		LOGGER.debug("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return sftpFileUploadService.uploadDocuments(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

}
