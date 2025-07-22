package com.ey.advisory.controllers.anexure1;

import jakarta.servlet.http.HttpServletResponse;

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
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Gstr2ConsolidatedAspErrorReportHandler;
import com.ey.advisory.app.services.reports.Gstr2PREntityLevelReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.controller.Anx1ReviewSummReportsController;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class Gstr2ReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummReportsController.class);

	@Autowired
	@Qualifier("Gstr2ConsolidatedAspErrorReportHandler")
	private Gstr2ConsolidatedAspErrorReportHandler gstr2AspErrorReportHandler;

	@Autowired
	@Qualifier("Gstr2ProcessedReportHandler")
	private Gstr2ProcessedReportHandler gstr2ProcessedReportHandler;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam commonSecParam;

	@Autowired
	@Qualifier("Gstr2PREntityLevelReportHandler")
	Gstr2PREntityLevelReportHandler gstr2PREntityLevelReportHandler;

	@RequestMapping(value = "/ui/gstr2PRReportsDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		// String groupcode = TenantContext.getTenantId();
		// TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr2ProcessedRecordsReqDto searchParams = gson.fromJson(json,
					Gstr2ProcessedRecordsReqDto.class);
			Gstr2ProcessedRecordsReqDto setDataSecurity = commonSecParam
					.setInwardGstr2PRSumDataSecuritySearchParams(searchParams);
			if (searchParams.getType() != null && searchParams.getType()
					.equalsIgnoreCase(DownloadReportsConstant.GSTR2ERROR)) {
				workbook = gstr2AspErrorReportHandler
						.downloadErrorReport(setDataSecurity);
				fileName = "GSTR2_Consolidated_ASP_Error_Report";

			} else if (DownloadReportsConstant.GSTR2PRENTITYLEVEL
					.equalsIgnoreCase(searchParams.getType())) {

				workbook = gstr2PREntityLevelReportHandler
						.downloadEntityLevelSummary(searchParams);
				fileName = "PR_Summary_Entity_Level_Summary";
			} else if (searchParams.getType() != null && searchParams.getType()
					.equalsIgnoreCase(DownloadReportsConstant.GSTR2PROCESSED)) {
				workbook = gstr2ProcessedReportHandler
						.downloadErrorReport(setDataSecurity);
				fileName = "GSTR2_ProcessedReport";
			} else {
				LOGGER.error("invalid request");
				throw new Exception("invalid request");
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
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}
}
