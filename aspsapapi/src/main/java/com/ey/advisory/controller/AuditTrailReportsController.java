package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.ey.advisory.app.docs.dto.AuditTrailReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.AuditTrailInwardReportHandler;
import com.ey.advisory.app.services.reports.AuditTrailOutwardReportHandler;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sujith.Nanga
 *
 */
@RestController
public class AuditTrailReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuditTrailReportsController.class);

	@Autowired
	@Qualifier("AuditTrailOutwardReportHandler")
	private AuditTrailOutwardReportHandler auditTrailOutwardReportHandler;

	@Autowired
	@Qualifier("AuditTrailInwardReportHandler")
	private AuditTrailInwardReportHandler auditTrailInwardReportHandler;

	@RequestMapping(value = "/ui/downloadAuditTrailReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadreviewSummReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			String fileName = null;
			Workbook workbook = null;
			AuditTrailReportsReqDto criteria = gson.fromJson(json,
					AuditTrailReportsReqDto.class);

			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.O)) {
				workbook = auditTrailOutwardReportHandler
						.downloadAuditOutward(criteria);

				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());

				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyy_MM_dd");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HH_mm_ss");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);
				
				fileName = "AuditTrail_Outward_"
				+criteria.getDocNum() +"_DetailedRecords_" 
						+ date + "" +"-T" + time;
				

			} else if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.I)) {
				workbook = auditTrailInwardReportHandler
						.downloadAuditInward(criteria);
				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());

				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyy_MM_dd");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HH_mm_ss");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);

				fileName = "AuditTrail_Inward_"
						+criteria.getDocNum() +"_DetailedRecords_" 
								+ date + "" +"-T" + time;
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
