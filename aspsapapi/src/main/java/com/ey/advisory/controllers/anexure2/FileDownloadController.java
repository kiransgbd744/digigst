package com.ey.advisory.controllers.anexure2;

import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.asprecon.ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconConfigRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class FileDownloadController {

	@Autowired
	@Qualifier("ReconConfigRepository")
	ReconConfigRepository reconConfigRepo;

	@GetMapping(value = "/ui/downloadDocument")
	public void fileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Anx2ReconReports");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);
		String configId = request.getParameter("configId");
		ReconConfigEntity configEntity = reconConfigRepo
				.findByConfigId(Long.valueOf(configId));
		String fileName = configEntity.getFilePath();
		String fileFolder = "Anx2ReconReports";

		Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);

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

	}
	
	@GetMapping(value = "/ui/downloadExcelDocument")
	public void ExcelDownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LOGGER.error("inside ExcelDownload method and file type is {} ",
				"Anx2ReconReports");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		String configId = request.getParameter("configId");
		ReconConfigEntity configEntity = reconConfigRepo
				.findByConfigId(Long.valueOf(configId));
		String fileName = configEntity.getFilePath();
		String fileFolder = "Anx2ReconReports";

		Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);

		if (document == null) {
			return;
		}

		InputStream inputStream = document.getContentStream().getStream();
		Workbook workbook = new Workbook(inputStream);
         
		if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename = " 
			+ fileName+".xlsx"));
			workbook.save(response.getOutputStream(), SaveFormat.XLSX);
		}

	}

}
