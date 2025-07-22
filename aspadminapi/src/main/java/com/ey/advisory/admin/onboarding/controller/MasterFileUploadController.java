package com.ey.advisory.admin.onboarding.controller;

import static com.ey.advisory.common.GSTConstants.CUSTOMER;
import static com.ey.advisory.common.GSTConstants.MASTER;
import static com.ey.advisory.common.GSTConstants.MASTER_CUSTOMER_FILE_NAME;
import static com.ey.advisory.common.GSTConstants.MASTER_FOLDER_NAME;
import static com.ey.advisory.common.GSTConstants.VENDOR;
import static com.ey.advisory.common.GSTConstants.VENDOR_RAW_FILE_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.CustomerSaveApiService;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.CustomerWebUploadService;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.VendorWebUploadService;
import com.ey.advisory.app.services.annexure1fileupload.MasterUploadService;
import com.ey.advisory.app.services.annexure2fileupload.ItemWebUploadService;
import com.ey.advisory.app.services.annexure2fileupload.ProductWebUploadService;
import com.ey.advisory.app.services.jobs.gstr1.VendorFileUploadServiceImpl;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author Mahesh.Golla
 *
 */

/*
 * This class is responsible for uploading the master web uploads into hana
 * cloud database
 */

@RestController
public class MasterFileUploadController {
	public final static Logger LOGGER = LoggerFactory.getLogger(MasterFileUploadController.class);

	@Autowired
	@Qualifier("CustomerWebUploadService")
	private CustomerWebUploadService customerWebUploadService;

	@Autowired
	@Qualifier("ProductWebUploadServiceImpl")
	private ProductWebUploadService productWebUploadService;

	@Autowired
	@Qualifier("ItemWebUploadServiceImpl")
	private ItemWebUploadService itemWebUploadService;

	@Autowired
	@Qualifier("VendorWebUploadService")
	private VendorWebUploadService vendorWebUploadService;

	@Autowired
	@Qualifier("VendorFileUploadServiceImpl")
	private VendorFileUploadServiceImpl vendorFileUploadServiceImpl;

	@Autowired
	@Qualifier("CustomerSaveApiService")
	private CustomerSaveApiService customerSaveApiService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	@Qualifier("DefaultCustomerFileUploadService")
	private MasterUploadService masterUploadService;

	/**
	 * For Customer Master
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/customerMasterWebUpload", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> customerMaster(@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") Long entityId) throws Exception {
		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Groud Code of this application", groupCode);
		}
		String fileType = CUSTOMER;
		String foldername = MASTER_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String uniqueName = MASTER_CUSTOMER_FILE_NAME;
		String dataType = MASTER;

		LOGGER.error("Details of uploaded by {} and file type is {} ", userName, fileType);

		return masterUploadService.fileUploadsCustomer(files, fileType, foldername, userName, uniqueName, dataType,
				entityId);

	}

	/**
	 * For Customer Master Save Api
	 * 
	 * @param jsonString
	 * @return ResponseEntity<String>
	 */
	@PostMapping(value = "/saveMasterCustomer", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveCustomer(@RequestBody String jsonString) {
		LOGGER.info("method is {} saveCustomer");
		String groupCode = TenantContext.getTenantId();
		LOGGER.info("groupCode {} is set", groupCode);
		try {
			JsonObject json = customerSaveApiService.saveCustomer(jsonString);
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(json);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while Fetching  Latest status";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * For Item Master
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/itemMasterWebUpload", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> itemMaster(@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String filedata) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		String groupCode = TenantContext.getTenantId();
		String[] entityIdAndName = filedata.split("-");
		// First element is Entity Id.
		Long entityId = Long.parseLong(entityIdAndName[0]);
		String fileType = GSTConstants.ITEM;
		String folderName = GSTConstants.MASTER_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String uniqueName = GSTConstants.MASTER_ITEM_FILE_NAME;
		String dataType = MASTER;

		LOGGER.error("Details of uploaded by {} and file type is {} ", userName, fileType);

		return itemWebUploadService.itemProcessingFile(files, folderName, uniqueName, fileType, userName, dataType,
				groupCode, entityId);
	}

	/**
	 * For Product Master
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/productMasterWebUpload", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> productMaster(@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String filedata) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		String groupCode = TenantContext.getTenantId();
		String[] entityIdAndName = filedata.split("-");
		// First element is Entity Id.
		Long entityId = Long.parseLong(entityIdAndName[0]);
		String fileType = GSTConstants.PRODUCT;
		String folderName = GSTConstants.MASTER_FOLDER_NAME;
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String uniqueName = GSTConstants.MASTER_PRODUCT_FILE_NAME;
		String dataType = MASTER;

		LOGGER.error("Details of uploaded by {} and file type is {} ", userName, fileType);

		return productWebUploadService.productProcessingFile(files, folderName, uniqueName, fileType, userName,
				dataType, groupCode, entityId);
	}

	/**
	 * For Vendor Master
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/vendorMasterWebUpload", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> vendorMaster(@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String filedata) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		String groupCode = TenantContext.getTenantId();
		String[] entityIdAndName = filedata.split("-");
		// First element is Entity Id.
		Long entityId = Long.parseLong(entityIdAndName[0]);

		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = MASTER_FOLDER_NAME;
		String uniqueName = VENDOR_RAW_FILE_NAME;
		String fileType = VENDOR;
		String dataType = MASTER;

		return vendorFileUploadServiceImpl.vendorProcessingFile(files, folderName, uniqueName, userName, fileType,
				dataType, groupCode, entityId);
	}
}
