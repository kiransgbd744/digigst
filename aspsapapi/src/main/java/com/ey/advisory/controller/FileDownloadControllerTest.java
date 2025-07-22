package com.ey.advisory.controller;

import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.FileDownloadDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class FileDownloadControllerTest {

	@PostMapping(value = "/ui/downloadAnx1Files", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public void fileDownloads(HttpServletResponse response,
			@RequestBody String fileDownload) throws Exception {
		LOGGER.error("inside fileDownloads method and file type is {} ",
				"Anx2Files");
		JsonObject jsonReqObj = (new JsonParser().parse(fileDownload)
				.getAsJsonObject());

		JsonObject json = jsonReqObj.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		FileDownloadDto dtoFileName = gson.fromJson(json,
				FileDownloadDto.class);

		String fileName = dtoFileName.getFileName();
		String fileFolder = "Annexure1Webuploads";
		if (fileName != null) {
			try {
				Document document = DocumentUtility.downloadDocument(fileName,
						fileFolder);

				if (document == null) {
					return;
				}
				InputStream inputStream = document.getContentStream()
						.getStream();
				Workbook workbook = new Workbook(inputStream);
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			} catch (Exception e) {
				Document document = DocumentUtility.downloadDocument(fileName,
						                                          fileFolder);

				if (document == null) {
					return;
				}
				InputStream inputStream = document.getContentStream()
						.getStream();
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();

			}
		}
	}
}
