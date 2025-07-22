package com.ey.advisory.controller.gstr2a;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.daos.client.gstr2.Gstr6AEligibleReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class DistributionReportsController {

	@Autowired
	@Qualifier("Gstr6AEligibleReportHandler")
	private Gstr6AEligibleReportHandler gstr6AEligibleReportHandler;

	/**
	 * Distribution Eligible Report Api 
	 * @param jsonString
	 * @param response
	 */
	
	@RequestMapping(value = "/ui/distributionEligibleReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void distributionEligibleReportsDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		
		
		Annexure1SummaryReqDto reqDto = gson.fromJson(json,
				Annexure1SummaryReqDto.class);

		try {
			Workbook workbook = gstr6AEligibleReportHandler
					.downloadEligibleDstbData(reqDto);
			String fileName = "Distribution_Eligible_Report";
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

	/**
	 * Distribution InEligible Report API 
	 * @param jsonString
	 * @param response
	 */
	
	@RequestMapping(value = "/ui/distributionIneligibleReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void distributionInEligibleReportsDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		
		
		Annexure1SummaryReqDto reqDto = gson.fromJson(json,
				Annexure1SummaryReqDto.class);

		try {
			Workbook workbook = gstr6AEligibleReportHandler
					.downloadIneligibleDstbData(reqDto);
			String fileName = "Distribution_Ineligible_Report";
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

	/**
	 * Redistribution Eligible Report API 
	 * @param jsonString
	 * @param response
	 */
	@RequestMapping(value = "/ui/redistributionEligibleReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void redistributionEligibleReportsDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		
		
		Annexure1SummaryReqDto reqDto = gson.fromJson(json,
				Annexure1SummaryReqDto.class);

		try {
			Workbook workbook = gstr6AEligibleReportHandler
					.downloadEligibleRedstbData(reqDto);
			String fileName = "Redistribution_Eligible_Report";
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
	
	
	/**
	 * Redistribution InEligible Report API 
	 * @param jsonString
	 * @param response
	 */
	@RequestMapping(value = "/ui/redistributionIneligibleReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void redistributionInEligibleReportsDownload(@RequestBody String jsonString,
			HttpServletResponse response) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		
		
		Annexure1SummaryReqDto reqDto = gson.fromJson(json,
				Annexure1SummaryReqDto.class);

		try {
			Workbook workbook = gstr6AEligibleReportHandler
					.downloadInEligibleRedstbData(reqDto);
			String fileName = "Redistribution_Ineligible_Report";
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
