package com.ey.advisory.controller.dashboard.fiori;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.gstr1.AsyncGstr2ProcessedRecordsReqDto;
import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;
import com.ey.advisory.app.services.dashboard.fiori.B2bCntDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1FioriDashboardChartDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr2aVsPurchaseRegisterDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr3BOutwardFioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.Gstr3bHeaderGraphDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.LastRefreshedOnDto;
import com.ey.advisory.app.services.dashboard.fiori.LastUpdatedOnDto;
import com.ey.advisory.app.services.dashboard.fiori.Pr2a2bDataDto;
import com.ey.advisory.app.services.dashboard.fiori.Top10SuppliersDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@RestController
@Slf4j
public class Gstr3BOutwardFioriDashboardController {

	@Autowired
	@Qualifier("Gstr3BOutwardFioriDashboardServiceImpl")
	Gstr3BOutwardFioriDashboardService dashboardService;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	AsyncJobsService asyncJobsService;
	
	@PostMapping(value = "/ui/getGstr3bHeaderGraphData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInward2HeaderData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<String> listOfRecepGstin;
			List<String> listOfReturnPrds;
			JsonArray recepGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			recepGstinArray = requestObject.getAsJsonArray("supplierGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			Gstr3bHeaderGraphDetailsDto headerData = dashboardService
					.getHeaderGraphData(fy, listOfRecepGstin,
							listOfReturnPrds);
			
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(headerData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching getGstr3bHeaderGraphData Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@PostMapping(value = "/ui/getGstr3bTableData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bTableData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<String> listOfRecepGstin;
			List<String> listOfReturnPrds;
			JsonArray recepGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			recepGstinArray = requestObject.getAsJsonArray("supplierGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Gstr3bMonthlyTrendTaxAmountsEntity> tableData = dashboardService
					.getTableData(fy, listOfRecepGstin,
							listOfReturnPrds);
			
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(tableData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching getGstr3bHeaderGraphData Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@PostMapping(value = "/ui/asyncgstr3BOutwrdDashbrdReport")
	public ResponseEntity<String> asyncgstr3BOutwrdDashbrdReport(
			@RequestBody String jsonParams) {
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String groupCode = TenantContext.getTenantId();

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			JsonObject requestObject = (new JsonParser()).parse(jsonParams)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<String> listOfRecepGstin;
			List<String> listOfReturnPrds;
			JsonArray recepGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			recepGstinArray = requestObject.getAsJsonArray("supplierGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);
		
			
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			
			entity.setGstins(GenUtil.convertStringToClob(convertToQueryFormat(listOfRecepGstin)));
			
			entity.setUsrAcs1(convertToQueryFormat(listOfReturnPrds));
			
			entity.setReportType("Liability Payment Details");
			entity.setDataType("GSTR3B");
			entity.setReportCateg("Dashboard");
			entity.setReportStatus("INITIATED");
			entity.setCreatedBy(userName);
			entity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			
			entity = fileStatusDownloadReportRepo.save(entity);
			Long id = entity.getId();
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			String reportType = "Liability Payment Details";
			jobParams.addProperty("reportType", reportType);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job calling for liability");
				LOGGER.debug(msg);
			}

			asyncJobsService.createJob(groupCode,
					JobConstants.Liability_Payment, jobParams.toString(),
					userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("async job end for liability");
				LOGGER.debug(msg);
			}

			
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in Async Report CSV Download Controller"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
	private String convertToQueryFormat(List<String> list) {

		String queryData = null;

		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}

		return queryData;

	}


}
