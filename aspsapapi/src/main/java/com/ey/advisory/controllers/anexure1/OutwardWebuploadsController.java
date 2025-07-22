package com.ey.advisory.controllers.anexure1;

import static com.ey.advisory.common.GSTConstants.ANN1_B2C_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_FOLDER_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_SHIPPING_BILL_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.ANN1_TABLE4_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.B2C;
import static com.ey.advisory.common.GSTConstants.INTEREST;
import static com.ey.advisory.common.GSTConstants.INTEREST_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.INWARD;
import static com.ey.advisory.common.GSTConstants.OUTWARD;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.annexure1fileupload.OutwardFileUploadService;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@RestController
public class OutwardWebuploadsController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardWebuploadsController.class);
	@Autowired
	@Qualifier("DefaultOutwardFileUploadService")
	private OutwardFileUploadService outwardFileUploadService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	/**
	 * Outward B2C File web uploads
	 */
	@RequestMapping(value = "/ui/outwardB2c", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> b2cWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error("inside b2cWebUploads method and file type is {}  "
				+ "foldername is {} ", B2C, ANN1_FOLDER_NAME);

		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		String fileType = B2C;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = ANN1_B2C_FILE_NAME;
		String dataType = OUTWARD;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);
	}

	/**
	 * Outward table 4 uploads
	 */

	@RequestMapping(value = "/ui/outwardTable4", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> table4WebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error("inside table4WebUploads method and file type is {}  "
				+ "foldername is {} ", TABLE4, ANN1_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileType = TABLE4;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = ANN1_TABLE4_FILE_NAME;
		String dataType = OUTWARD;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * Outward table 3H AND 3I uploads
	 */

	@RequestMapping(value = "/ui/table3h3i", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> table3H3I(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error("inside table3H3I method and file type is {}  "
				+ "foldername is {} ", TABLE3H3I, ANN1_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileType = TABLE3H3I;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = TABLE3H3I_FILE_NAME;
		String dataType = INWARD;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * Outward Shipping Bill Details uploads
	 */

	@RequestMapping(value = "/ui/shippingBills", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> shippingBillDetails(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error(
				"inside Shipping Bill Details  method and file type is {}  "
						+ "foldername is {} ",
				SHIPPINGBILL, ANN1_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileType = SHIPPINGBILL;
		String foldername = ANN1_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = ANN1_SHIPPING_BILL_FILE_NAME;
		String dataType = OUTWARD;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/*
	 * RET 1 VERTICAL UPLOADS
	 */

	/**
	 * RET - 1 AND 1A UPLOADS
	 */

	@RequestMapping(value = "/ui/ret1And1AUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> ret1And1AWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error("inside ret1And1AWebUploads method and file type is {}  "
				+ "foldername is {} ", RET1AND1A, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileType = RET1AND1A;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = RET_FILE_NAME;
		String dataType = RET;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * RET - 1 AND 1A UPLOADS
	 */

	@RequestMapping(value = "/ui/interestAndLateFee", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> interstAndLateFee(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error("inside interstAndLateFee method and file type is {}  "
				+ "foldername is {} ", INTEREST, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileType = INTEREST;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = INTEREST_FILE_NAME;
		String dataType = RET;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * SET OFF AND UTILIZATIONS UPLOADS
	 */

	@RequestMapping(value = "/ui/setOffAndUtilizations", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> setOffAndUtilizations(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error("inside setOffAndUtilizations method and file type is {}  "
				+ "foldername is {} ", SETOFFANDUTIL, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileType = SETOFFANDUTIL;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = SETOFFANDUTIL_FILE_NAME;
		String dataType = RET;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

	/**
	 * REFUNDS UPLOADS
	 */

	@RequestMapping(value = "/ui/refunds", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public ResponseEntity<String> refunds(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		LOGGER.error("inside refunds method and file type is {}  "
				+ "foldername is {} ", REFUNDS, RET_FOLDER_NAME);
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileType = REFUNDS;
		String foldername = RET_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String uniqueName = REFUNDS_FILE_NAME;
		String dataType = RET;
		LOGGER.error("Details of uploaded by {} and file type is {} ", userName,
				fileType);

		return outwardFileUploadService.fileUploadsAnnexure1(files, fileType,
				foldername, userName, uniqueName, dataType);

	}

}