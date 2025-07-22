/**
 * this controller has created to test the hard coded data for 
 * file uploading
 */
package com.ey.advisory.controllers.anexure2;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.Workbook;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class FileUploadController {

	@RequestMapping(value = "/ui/anx2FileUpload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public String fileUploads(@RequestParam("file") MultipartFile file)
			throws Exception {
		LOGGER.error("inside fileUploads method and file type is {}  "
				+ "foldername is {} ", "Anx2ReconReports");
		Workbook workbook = new Workbook(file.getInputStream());
		workbook.setFileName(file.getOriginalFilename());
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		// String userName = SecurityContext.getUser() != null?
		// (SecurityContext.getUser().getUserPrincipalName() != null?
		// SecurityContext.getUser().getUserPrincipalName(): "SYSTEM"):
		// "SYSTEM";
		String fName = "Anx2ReconReports";
		return DocumentUtility.uploadDocument(workbook, fName, "XLSX");

	}

}
