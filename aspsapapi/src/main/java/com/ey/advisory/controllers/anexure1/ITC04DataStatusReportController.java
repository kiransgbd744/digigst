package com.ey.advisory.controllers.anexure1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.BasicCommonSecParamReports;
import com.ey.advisory.app.services.reports.ITC04DataStatusReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ITC04DataStatusReportController {

	@Autowired
	@Qualifier("ITC04DataStatusReportHandler")
	private ITC04DataStatusReportHandler itc04ReportHandler;
	
	@Autowired
	@Qualifier("BasicCommonSecParamReports")
	BasicCommonSecParamReports basicCommonSecParamReports;

	@RequestMapping(value = "/ui/itc04DataStatusReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void itc04DataStatusReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Anx1ReportSearchReqDto setDataSecurity = gson.fromJson(json,
					Anx1ReportSearchReqDto.class);
			Anx1ReportSearchReqDto criteria = basicCommonSecParamReports
					.setDataSecuritySearchParams(setDataSecurity);
			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.ITC04)) {
				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
					workbook = itc04ReportHandler
							.downloadApiProcessedReport(criteria);
					if (criteria.getStatus() != null && criteria.getStatus()
							.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
						fileName = "DataStatus_Active_Processed";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.INACTIVE)) {
						fileName = "DataStatus_InActive_Processed";
					}
				} else if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
					workbook = itc04ReportHandler
							.downloadDataStatusErrorReport(criteria);
					if (criteria.getStatus() != null && criteria.getStatus()
							.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
						fileName = "DataStatus_Active_Error";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.INACTIVE)) {
						fileName = "DataStatus_Inactive_Error";
					}
				}
				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					workbook = itc04ReportHandler
							.downloadDataStatusTotalRecordsReport(criteria);
					fileName = "DataStatus_TotalRecords";
				}
				if (workbook == null) {
					workbook = new Workbook();
				}
				if (fileName != null) {
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition", String.format(
							"attachment; filename=" + fileName + ".xlsx"));
					workbook.save(response.getOutputStream(), SaveFormat.XLSX);
					response.getOutputStream().flush();
				}
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			}

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
}
