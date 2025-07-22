/**
 * 
 */
package com.ey.advisory.controller;

/**
 * @author KG712ZX
 *
 */

import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.ReturnFilingConfigEntity;
import com.ey.advisory.app.data.repositories.client.ReturnFilingConfigRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ReturnFilingFileDownloadController {
	
	@Autowired
	@Qualifier("ReturnFilingConfigRepository")
	ReturnFilingConfigRepository returnFilingConfigRepo;
	
	
	@GetMapping(value = "/ui/downloadReturnFilingDocument")
	public void downloadReturnStatusReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LOGGER.error("inside ExcelDownload method and file type is {} ",
				"Download Gstin Validatoe Report");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		Long requestId = Long.valueOf(request.getParameter("requestId"));
		ReturnFilingConfigEntity configEntity = returnFilingConfigRepo
				.findByRequestId(requestId);
		String fileName = configEntity.getFilePath();
		//String[] fileNametoPass = fileName.split("\\.");
	    //String sendFileName = fileNametoPass[0];
		String fileFolder = "ReturnFilingReport"; 

		/*Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);*/
		String docId = configEntity.getDocId();
		Document document = null;
		if (Strings.isNullOrEmpty(docId)) {
			document = DocumentUtility.downloadDocument(fileName,
					fileFolder);
		} else {
			document = DocumentUtility.downloadDocumentByDocId(docId);
		}

		if (document == null) {
			return;
		}

		InputStream inputStream = document.getContentStream().getStream();
		Workbook workbook = new Workbook(inputStream);
		String args[] = fileName.split("_");
		String finalFileName = args[0] + "_"+requestId+".xlsx";
		if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename = " + finalFileName));
			workbook.save(response.getOutputStream(), SaveFormat.XLSX);
		}

	}
	
	
}
