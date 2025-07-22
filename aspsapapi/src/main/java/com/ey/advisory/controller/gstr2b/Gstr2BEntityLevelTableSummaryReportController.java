package com.ey.advisory.controller.gstr2b;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.gstr2b.summary.Gstr2BDetailsReportDownloadService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class Gstr2BEntityLevelTableSummaryReportController { 

	@Autowired
	@Qualifier("Gstr2BDetailsReportDownloadService")
	private Gstr2BDetailsReportDownloadService service;

	@PostMapping(value = "/ui/gstr2DetailsReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	
	public void getDetailsReport(@RequestBody String jsonString, 
			HttpServletResponse response)throws IOException {
		
		JsonArray gstins = new JsonArray();
		List<String> gstnsList = null;
		Gson googleJson = new Gson();
		JsonObject errorResp = new JsonObject();

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin Gstr2BDetailsReportDownloadService"
							+ ".getDetailsReport() method, Parsing Input "
							+ "request %s ", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();
			
			Long entityId = reqJson.has("entityId")
					? reqJson.get("entityId").getAsLong() : 0L;

			String fromTaxPeriod = reqJson.has("fromTaxPeriod")
					? reqJson.get("fromTaxPeriod").getAsString() : null;

			String toTaxPeriod = reqJson.has("toTaxPeriod")
					? reqJson.get("toTaxPeriod").getAsString() : null;

			if ((reqJson.has("gstin"))
					&& (reqJson.getAsJsonArray("gstin").size() > 0)) {
				gstins = reqJson.getAsJsonArray("gstin");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = googleJson.fromJson(gstins, listType);
			}						
			
			if (gstnsList == null || gstnsList.isEmpty()) {
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
			}

			Workbook workbook = service.writeToExcel(gstnsList, 
					fromTaxPeriod, toTaxPeriod);
			
			String fileName = DocumentUtility.getUniqueFileName(
					ConfigConstants.GSTR2B_DETAILS_REPORT);

			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String.format(
						"attachment; filename = "+ fileName));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}

		} catch (Exception ex) {
			String msg = "Error occured while generating "
					+ "GSTR 2B Entity Level Table wise summary Report report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();

		}
	}
}
