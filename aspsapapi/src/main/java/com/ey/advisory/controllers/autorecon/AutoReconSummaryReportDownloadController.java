package com.ey.advisory.controllers.autorecon;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoReconSummaryReportDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class AutoReconSummaryReportDownloadController {

	@Autowired
	AutoReconSummaryReportDownloadService autoReconSummaryReportDownloadService;

	@PostMapping(value = "/ui/getAutoReconSummaryReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAutoReconSummaryReport(
			@RequestBody String jsonString, HttpServletResponse response) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Workbook workbook = null;
		JsonObject dataObj = new JsonObject();
		try {
			String fileName = null;
			String toTaxPeriod2A = null;
			String fromTaxPeriod2A = null;
			String toTaxPeriodPR = null;
			String fromTaxPeriodPR = null;
			String toReconDate = null;
			String fromReconDate = null;
			String criteria = null;
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside getAutoReconSummaryReport with request as "
								+ " : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			JsonArray gstins = reqJson.getAsJsonArray("recipientGstins");

			Gson googleJson = new Gson();
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> recipientGstins = googleJson.fromJson(gstins,
					listType);

			if (reqJson.has("toTaxPeriodPR"))
				toTaxPeriodPR = reqJson.get("toTaxPeriodPR").getAsString();

			if (reqJson.has("fromTaxPeriodPR"))
				fromTaxPeriodPR = reqJson.get("fromTaxPeriodPR").getAsString();

			if (reqJson.has("toTaxPeriod2A"))
				toTaxPeriod2A = reqJson.get("toTaxPeriod2A").getAsString();

			if (reqJson.has("fromTaxPeriod2A"))
				fromTaxPeriod2A = reqJson.get("fromTaxPeriod2A").getAsString();

			if (reqJson.has("toReconDate"))
				toReconDate = reqJson.get("toReconDate").getAsString();

			if (reqJson.has("fromReconDate"))
				fromReconDate = reqJson.get("fromReconDate").getAsString();
			
			if (reqJson.has("criteria"))
				criteria = reqJson.get("criteria").getAsString();

			Long entityId = reqJson.get("entityId").getAsLong();

			if (toTaxPeriod2A != null && fromTaxPeriod2A != null
					&& toTaxPeriodPR != null && fromTaxPeriodPR != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"getAutoReconSummaryReport for "
									+ "Parameters toReturnPeriod2A %s To "
									+ "fromReturnPeriod2A %s toReturnPeriodPR %s"
									+ " To fromReturnPeriodPR %s,   " + ": ",
							toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
							fromTaxPeriodPR);
					LOGGER.debug(msg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("getAutoReconSummaryReport for "
							+ "Parameters fromDocDate %s To toDocDate  %s "
							+ ": ", fromReconDate, toReconDate);
					LOGGER.debug(msg);
				}
			}

			if (CollectionUtils.isEmpty(recipientGstins))
				throw new AppException("User did not select any gstin");

			workbook = autoReconSummaryReportDownloadService
					.getReconSummaryReport(recipientGstins, fromTaxPeriodPR,
							toTaxPeriodPR, fromTaxPeriod2A, toTaxPeriod2A,
							fromReconDate, toReconDate, entityId, criteria);
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

			fileName = "AutoReconSummary" + "_" + date + "T" + time;

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
			String msg = "Error occured while getAutoReconSummaryReport ";
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
