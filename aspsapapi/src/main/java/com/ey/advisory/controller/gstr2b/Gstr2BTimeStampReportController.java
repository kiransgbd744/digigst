package com.ey.advisory.controller.gstr2b;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.gstr2b.Gstr2BTimeStampReport;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class Gstr2BTimeStampReportController {

	@Autowired
	@Qualifier("Gstr2BTimeStampReport")
	private Gstr2BTimeStampReport service;

	@PostMapping(value = "/ui/gstr2TimeStampReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getTimeStampReport(
			@RequestBody String jsonString, HttpServletResponse response) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2BTimeStampReportController"
						+ ".getTimeStampReport ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			Long entityId = reqJson.has("entityId")

					? reqJson.get("entityId").getAsLong() : 0L;

			String fy = reqJson.has("fy")

					? reqJson.get("fy").getAsString() : "";

			List<String> gstnsList = null; //Arrays.asList("33GSPTN0481G1ZA");
			try {
				List<Long> entityIds = new ArrayList<>();
				entityIds.add(entityId);
				Map<String, String> outwardSecurityAttributeMap = 
						DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = 
						DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);

			} catch (Exception ex) {
				String msg = String.format(
						"Error while fetching Gstins from entityId ", ex);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			String startMonth = "04";
			String endMonth = "03";
			String appendMonthYear = null;
			String appendMonthYear1 = null;
			if (fy != null && !fy.isEmpty()) {
				String[] arrOfStr = fy.split("-", 2);
				appendMonthYear = arrOfStr[0] + startMonth;
				appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
			}
			int derivedStartPeriod = Integer.parseInt(appendMonthYear);

			int derivedEndPeriod = Integer.parseInt(appendMonthYear1);

			String filePath = service.getGstr2BTimeStampReport(
					gstnsList, derivedStartPeriod, derivedEndPeriod);

			String fileFolder = ConfigConstants.GSTR2BREPORTS;

			Document document = DocumentUtility.downloadDocument(filePath,
					fileFolder);

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + filePath));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
				
			}catch(Exception ex){
				}
			}
}
