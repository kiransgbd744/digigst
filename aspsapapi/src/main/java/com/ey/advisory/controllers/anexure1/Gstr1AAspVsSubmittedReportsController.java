package com.ey.advisory.controllers.anexure1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.gstr1a.Gstr1AAspProcessVsSubmitReportHandler;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 * 
 */

@RestController
@Slf4j
public class Gstr1AAspVsSubmittedReportsController {
	@Autowired
	@Qualifier("Gstr1AAspProcessVsSubmitReportHandler")
	private Gstr1AAspProcessVsSubmitReportHandler aspProcessVsSubmitReportHandler;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@RequestMapping(value = "/ui/aspGstr1APrVsSubmitDownloadReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void aspProcessedVsSubmitDownloadsreport(
			@RequestBody String jsonString, HttpServletResponse response) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(json,
					Gstr1ReviwSummReportsReqDto.class);

			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			Gstr1ReviwSummReportsReqDto setDataSecurity = basicCommonSecParamRSReports
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */
			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.ASPPROCESSEDSUBMITTED)) {
				String date = null;
				String time = null;
				String dateAndTime = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HH:mm:ss");
				DateTimeFormatter FOMATTER2 = DateTimeFormatter
						.ofPattern("dd-MM-yyyy HH:mm:ss");
				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);
				dateAndTime = FOMATTER2.format(istDateTimeFromUTC);
				String convertNow = date + time;
				String convertNow1 = date + " & " + time;
				criteria.setGenOfTimeAndDate(convertNow1);
				workbook = aspProcessVsSubmitReportHandler
						.findProcessVsSubmitReports(setDataSecurity);
				fileName = "GSTR1A_Processed DigiGST vs Submitted GSTN_" + convertNow;
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
