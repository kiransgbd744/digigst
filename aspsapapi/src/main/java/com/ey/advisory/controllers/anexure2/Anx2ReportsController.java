package com.ey.advisory.controllers.anexure2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.common.BasicInwardParam;
import com.ey.advisory.app.docs.dto.Anx2InwardErrorRequestDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Anx2InwardErrorReportHandler;
import com.ey.advisory.app.services.reports.Anx2InwardProcessedReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Anx2ReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2ReportsController.class);
	

	@Autowired
	@Qualifier("Anx2InwardErrorReportHandler")
	private Anx2InwardErrorReportHandler anx2InwardErrorReportHandler;

	@Autowired
	@Qualifier("Anx2InwardProcessedReportHandler")
	private Anx2InwardProcessedReportHandler anx2InwardProcessedReportHandler;

	@Autowired
	@Qualifier("BasicInwardParam")
	BasicInwardParam basicInwardParam;

	@RequestMapping(value = "/ui/downloadAnx2InwardReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadProcessedReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			
			Anx2InwardErrorRequestDto criteria = gson.fromJson(json,
					Anx2InwardErrorRequestDto.class);
			
			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			Anx2InwardErrorRequestDto setDataSecurity = basicInwardParam
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */

			


				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
					workbook = anx2InwardErrorReportHandler
							.downloadErrorReport(setDataSecurity);
					
						fileName = "Anx2_InwardErrorReport";
					}
				
				
				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
					workbook = anx2InwardProcessedReportHandler
							.downloadProcessedReport(setDataSecurity);
					
					fileName = "Anx2_InwardProcessedReport";

					
				}
			
			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving "
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}

}