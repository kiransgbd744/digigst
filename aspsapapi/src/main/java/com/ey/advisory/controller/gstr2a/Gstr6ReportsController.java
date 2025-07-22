package com.ey.advisory.controller.gstr2a;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.daos.client.gstr2.Gstr6ConsolidatedAspErrorReportHandler;
import com.ey.advisory.app.data.daos.client.gstr2.Gstr6EntityLevelReportHandler;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Gstr6ProcessedRecScreenHandler;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Gstr6ReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6ReportsController.class);

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	@Qualifier("Gstr6ConsolidatedAspErrorReportHandler")
	private Gstr6ConsolidatedAspErrorReportHandler gstr6AspErrorReportHandler;

	@Autowired
	@Qualifier("Gstr6ProcessedRecScreenHandler")
	private Gstr6ProcessedRecScreenHandler gstr6ProcessedRecScreenHandler;

	@Autowired
	@Qualifier("Gstr6EntityLevelReportHandler")
	private Gstr6EntityLevelReportHandler gstr6EntityLevelReportHandler;

	@RequestMapping(value = "/ui/gstr6ReportsDownloads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr6ReportsDownloads(
			@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		// String groupcode = TenantContext.getTenantId();
		// TenantContext.setTenantId(groupcode);
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject dataObj = new JsonObject();
		try {
			String fileName = null;
			Workbook workbook = null;
			Gstr6SummaryRequestDto criteria = gson.fromJson(json,
					Gstr6SummaryRequestDto.class);
			Gstr6SummaryRequestDto setDataSecurity = basicGstr6SecCommonParam
					.setDataSecuritySearchParams(criteria);
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.GSTR6ASPERROR)) {
				workbook = gstr6AspErrorReportHandler
						.downloadErrorReport(setDataSecurity);
				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());

				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyyMMdd");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HHmmssms");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);
				fileName = "GSTR6_" + "DigiGST_Error_Records" + "_"
						+ criteria.getTaxPeriod() + "_" + date + "T"
						+ time.substring(0, 9);
			} else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR6ENTITYLEVELSUMM)) {
				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyyMMdd");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HHmmssms");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC).substring(0, 9);

				workbook = gstr6EntityLevelReportHandler
						.downloadGstr6EntityLevelSummary(setDataSecurity);
				fileName = "GSTR-6_Entity_Level_Summary" + "_"
						+ criteria.getTaxPeriod() + "_" + date + "T" + time;
			} else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR6PROCESSRECSCREEN)) {

				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyyMMdd");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HHmmssms");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);

				workbook = gstr6ProcessedRecScreenHandler
						.getGstr6SummTablesReports(setDataSecurity);
				fileName = "GSTR-6_Processed_Records_Screen_Download" + "_"
						+ criteria.getTaxPeriod() + "_" + date + "T"
						+ time.substring(0, 9);
			} else if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR6REVIEWSUMMARY)) {

				String date = null;
				String time = null;
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				LocalDateTime istDateTimeFromUTC = EYDateUtil
						.toISTDateTimeFromUTC(now);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("yyyyMMdd");

				DateTimeFormatter FOMATTER1 = DateTimeFormatter
						.ofPattern("HHmmssms");

				date = FOMATTER.format(istDateTimeFromUTC);
				time = FOMATTER1.format(istDateTimeFromUTC);

				Annexure1SummaryReqDto dto = new Annexure1SummaryReqDto();

				dto.setTaxPeriod(setDataSecurity.getTaxPeriod());
				dto.setDataSecAttrs(setDataSecurity.getDataSecAttrs());
				dto.setEntityId(setDataSecurity.getEntityId());
				workbook = gstr6ProcessedRecScreenHandler
						.getGstr6RevSummTablesReports(dto);
				fileName = "GSTR-6_Review_Summary_Screen_Download" + "_"
						+ criteria.getTaxPeriod() + "_" + date + "T"
						+ time.substring(0, 9);
			}

			else {
				LOGGER.error("invalid request");
				throw new Exception("invalid request");
			}
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				dataObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				dataObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
			}

		} catch (Exception ex) {
			dataObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			dataObj.addProperty("resp", ex.getMessage());
			String msg = "Error occured while generateConsReport ";
			LOGGER.error(msg, ex);
			try {
				response.flushBuffer();
			} catch (IOException e) {
				String errMsg = "Exception occurred while flushing the response buffer";
				LOGGER.error(errMsg, ex);
			}
			return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
		}
	}

}
