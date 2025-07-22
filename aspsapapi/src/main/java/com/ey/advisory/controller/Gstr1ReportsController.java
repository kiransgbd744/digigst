package com.ey.advisory.controller;

import java.util.List;

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
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.ReportSearchReqDto;
import com.ey.advisory.app.services.reports.gstr1.Gstr1ProcessedReportsService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Gstr1ReportsController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ReportsController.class);
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	
	@Autowired
	@Qualifier("Gstr1ProcessedReportServiceImpl")
	private Gstr1ProcessedReportsService gstr1ReportsService;

	@RequestMapping(value = "/ui/downloadProcessedReport1", 
			method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadProcessedReport1(HttpServletResponse response) {
		
		String jsonString = "{\"req\":{\"gstins\":[\"01ABCDS9546G5Z8\"],\"criteria\":\"\",\"receivFromDate\":\"2019-05-23\",\"receivToDate\":\"2019-05-23\",\"docFromDate\":null,\"docToDate\":null,\"returnFromDate\":null,\"returnToDate\":null,\"docNo\":null,\"entityId\":[\"1\"],\"gstnStatus\":[]}}";
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			ReportSearchReqDto criteria = gson.fromJson(json,
					ReportSearchReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getGstins();
				if (gstins == null || gstins.isEmpty()) {
					gstins = gstinInfoRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setGstins(gstins);
				}
			}
			Workbook workbook = gstr1ReportsService.find(criteria, null);
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String.format(
						"attachment; filename=ASP_ProcessedRecords.xlsx"));
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
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}

	
	@RequestMapping(value = "/ui/downloadProcessedReport", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadProcessedReport(@RequestBody String jsonString,
			HttpServletResponse response) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			ReportSearchReqDto criteria = gson.fromJson(json,
					ReportSearchReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getGstins();
				if (gstins == null || gstins.isEmpty()) {
					gstins = gstinInfoRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setGstins(gstins);
				}
			}
			Workbook workbook = gstr1ReportsService.find(criteria, null);
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String.format(
						"attachment; filename=ASP_ProcessedRecords.xlsx"));
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
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
	
	
	@RequestMapping(value = "/ui/downloadErrorReport", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadErrorReport(@RequestBody String jsonString,
			HttpServletResponse response) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			ReportSearchReqDto criteria = gson.fromJson(json,
					ReportSearchReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getGstins();
				if (gstins == null || gstins.isEmpty()) {
					gstins = gstinInfoRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setGstins(gstins);
				}
			}
			Workbook workbook = gstr1ReportsService.findError(criteria, null);
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=ASP_ErrorRecords.xlsx"));
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
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
	
	
	@RequestMapping(value = "/ui/downloadTotRecReport", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadTotRecReport(@RequestBody String jsonString,
			HttpServletResponse response) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			ReportSearchReqDto criteria = gson.fromJson(json,
					ReportSearchReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getGstins();
				if (gstins == null || gstins.isEmpty()) {
					gstins = gstinInfoRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setGstins(gstins);
				}
			}
			Workbook workbook = gstr1ReportsService.findTotRec(criteria, null);
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=ASP_TotRecRecords.xlsx"));
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
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
	
	@RequestMapping(value = "/ui/downloadInformationReport", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadInfoReport(@RequestBody String jsonString,
			HttpServletResponse response) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			ReportSearchReqDto criteria = gson.fromJson(json,
					ReportSearchReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getGstins();
				if (gstins == null || gstins.isEmpty()) {
					gstins = gstinInfoRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setGstins(gstins);
				}
			}
			Workbook workbook = gstr1ReportsService.findInfoRec(criteria, null);
			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=ASP_InfoRecords.xlsx"));
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
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}


}
