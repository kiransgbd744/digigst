package com.ey.advisory.controller;

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
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.Ret1ReviewSumScreenDownloadHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Ret1SummaryDownloadReportsControler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1SummaryDownloadReportsControler.class);

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Autowired
	@Qualifier("Ret1ReviewSumScreenDownloadHandler")
	private Ret1ReviewSumScreenDownloadHandler ret1ReviewSumScreenDownloadHandler;

	@RequestMapping(value = "/ui/downloadRet1RSReports", method = RequestMethod.POST, produces = {
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
							DownloadReportsConstant.RET1REVIEWSUMMARY)) {
				Annexure1SummaryReqDto dto = new Annexure1SummaryReqDto();
				dto.setTaxPeriod(setDataSecurity.getTaxperiod());
				dto.setDataSecAttrs(setDataSecurity.getDataSecAttrs());
				dto.setEntityId(setDataSecurity.getEntityId());
				workbook = ret1ReviewSumScreenDownloadHandler
						.findRet1RSummScreen(dto);
				fileName = "RET-1_ReviewSummary_Screen_Download";
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
