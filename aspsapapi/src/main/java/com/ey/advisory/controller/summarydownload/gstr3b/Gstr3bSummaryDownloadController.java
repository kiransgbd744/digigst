package com.ey.advisory.controller.summarydownload.gstr3b;

import java.io.File;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.reports.Gstr3bSummaryDownloadService;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@RestController
@Slf4j
public class Gstr3bSummaryDownloadController {

	@Autowired
	@Qualifier("Gstr3bSummaryDownloadService")
	private Gstr3bSummaryDownloadService downloadService;
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@RequestMapping(value = "/ui/gstr3bSummaryReportWebDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {
		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr3bSummaryDownloadController");
		Gson gson = GsonUtil.newSAPGsonInstance();
		Workbook workbook = null;
		String fileName = null;
		File file = null;
		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();

			String fileId = json.get("fileId").getAsString();
			String type = json.get("type").getAsString();
			String[] updatedFileName = gstr1FileStatusRepository
					.getFileName(Long.valueOf(fileId)).split("_");
			String uploadedFileName = "";
			for(int i =2;i<updatedFileName.length;i++){
				uploadedFileName += updatedFileName[i];
				if(i<updatedFileName.length-1){
					uploadedFileName += "_";
				}
			}
			if (type.equalsIgnoreCase("error")) {
				workbook = downloadService.generateErrorReport(
						Integer.valueOf(fileId), uploadedFileName);
				fileName = DocumentUtility
						.getUniqueFileName("GSTR 3B_Error Report.xlsx");

				if (workbook != null) {
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition",
							String.format("attachment; filename=" + fileName));
					workbook.save(response.getOutputStream(), SaveFormat.XLSX);
					response.getOutputStream().flush();
				}
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			} else if (type.equalsIgnoreCase("processed")) {
				workbook = downloadService.generatePSDReport(
						Integer.valueOf(fileId), uploadedFileName);
				fileName = DocumentUtility
						.getUniqueFileName("GSTR 3B_Processed Report.xlsx");

				if (workbook != null) {
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition",
							String.format("attachment; filename=" + fileName));
					workbook.save(response.getOutputStream(), SaveFormat.XLSX);
					response.getOutputStream().flush();
				}
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			}

			else if (type.equalsIgnoreCase("totalrecords")) {
				workbook = downloadService.generateTotalReport(
						Integer.valueOf(fileId), uploadedFileName);
				fileName = DocumentUtility
						.getUniqueFileName("GSTR 3B_Total Report.xlsx");

				if (workbook != null) {
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition",
							String.format("attachment; filename=" + fileName));
					workbook.save(response.getOutputStream(), SaveFormat.XLSX);
					response.getOutputStream().flush();
				}
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			}

		} catch (Exception ex) {
			String msg = String.format("File Download failed");
			LOGGER.error(msg, ex);
		}

		finally {
			if (file != null && file.exists()) {
				try {
					FileUtils.deleteDirectory(file);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Deleted the Temp directory/Folder '%s'",
								file.getAbsolutePath()));
					}
				} catch (Exception ex) {
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							file.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}
		}

	}
}
