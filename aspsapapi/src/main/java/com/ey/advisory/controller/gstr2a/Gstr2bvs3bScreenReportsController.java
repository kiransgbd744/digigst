package com.ey.advisory.controller.gstr2a;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr2bVsGstr3bProcessSummaryReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Gstr2bvs3bProcessedRecScreenHandler;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Gstr2bvs3bScreenReportsController {

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("Gstr2bvs3bProcessedRecScreenHandler")
	private Gstr2bvs3bProcessedRecScreenHandler gstr2bvs3bProcessedRecScreenHandler;

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@RequestMapping(value = "/ui/gstr2bvs3bScreenDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr2bvs3bReportDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr2bVsGstr3bProcessSummaryReqDto criteria = gson.fromJson(json,
					Gstr2bVsGstr3bProcessSummaryReqDto.class);

			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);
			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR2BVS3BPRSUMMARY)) {
				workbook = gstr2bvs3bProcessedRecScreenHandler
						.getGstr2bvs3bSumTablesReports(criteria);
				fileName = "DashboardSummary_GSTR2BvsGSTR3B_"
						+ criteria.getTaxPeriodFrom() + "_"
						+ criteria.getTaxPeriodTo() + "_"
						+ format.format(convertISDDate);
			} else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR2BVS3BREVIEWSUMMARY)) {
				Map<String, List<String>> dataSecReqMap = criteria
						.getDataSecAttrs();
				String gstin = criteria.getGstin();
				workbook = gstr2bvs3bProcessedRecScreenHandler
						.getGstr2bvs3bRevSumTablesReports(criteria);
				fileName = gstin + "_GSTR2BvsGSTR3B_"
						+ criteria.getTaxPeriodFrom() + "_"
						+ criteria.getTaxPeriodTo() + "_"
						+ format.format(convertISDDate);
			}

			else {
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
