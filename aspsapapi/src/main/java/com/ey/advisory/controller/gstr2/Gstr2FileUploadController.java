package com.ey.advisory.controller.gstr2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.data.repositories.client.Gstr2FileStatusRepository;
import com.ey.advisory.app.util.Gstr2FileUploadUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.Gstr2FileUploadService;
import com.ey.advisory.common.SecurityContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author Anand3.M This controller will be invoked from the GSTR2 PR Data
 *         upload status from UI.
 */
@RestController
public class Gstr2FileUploadController {

	@Autowired
	@Qualifier("DefaultGstr2FileUploadService")
	private Gstr2FileUploadService gstr2FileUploadService;

	@Autowired
	@Qualifier("Gstr2FileUploadUtil")
	private Gstr2FileUploadUtil gstr2FileUploadUtil;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	@Qualifier("Gstr2FileStatusRepository")
	private Gstr2FileStatusRepository gstr2FileStatusRepository;

	/**
	 * Gstr2 Raw File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/webRawGstr2UploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> webRawGstr2UploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.GSTR2_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2_RAW_FILE_NAME;

		return gstr2FileUploadService.uploadDocuments(files, folderName, uniqueName, userName);
	}

	/**
	 * Gstr2 B2cs Upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webB2csGstr2UploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> webB2csGstr2UploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.GSTR2_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2_B2CS_FILE_NAME;

		return gstr2FileUploadService.uploadDocuments(files, folderName, uniqueName, userName);
	}

	/**
	 * Gstr2 Nil upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webNilGstr2UploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> webNilGstr2UploadDocuments(

			@RequestParam("file") MultipartFile[] files) throws Exception {
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.GSTR2_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2_NIL_FILE_NAME;

		return gstr2FileUploadService.uploadDocuments(files, folderName, uniqueName, userName);
	}

	/**
	 * Gstr2 Advance Received upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webAtGstr2UploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> webAtGstr2UploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.GSTR2_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2_AT_FILE_NAME;

		return gstr2FileUploadService.uploadDocuments(files, folderName, uniqueName, userName);
	}

	/**
	 * Gstr2 Advance adjustments upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webAtaGstr2UploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> webAtaGstr2UploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.GSTR2_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2_ATA_FILE_NAME;

		return gstr2FileUploadService.uploadDocuments(files, folderName, uniqueName, userName);
	}

	/**
	 * Gstr2 Invoice upload
	 * 
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/webInvoiceGstr2UploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> webInvoiceGstr2UploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.GSTR2_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2_INVOICE_FILE_NAME;

		return gstr2FileUploadService.uploadDocuments(files, folderName, uniqueName, userName);
	}

}
