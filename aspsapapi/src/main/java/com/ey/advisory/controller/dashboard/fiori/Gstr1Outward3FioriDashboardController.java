package com.ey.advisory.controller.dashboard.fiori;

import java.lang.reflect.Type;
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

import com.ey.advisory.app.services.dashboard.fiori.GetLiabTableDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1FioriDashboardChartDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1Outward3FioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.Outward3HeaderDetailsDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@RestController
@Slf4j
public class Gstr1Outward3FioriDashboardController {

	@Autowired
	@Qualifier("Gstr1Outward3FioriDashboardServiceImpl")
	private Gstr1Outward3FioriDashboardService dashboardService;

	@PostMapping(value = "/ui/getOutward3HeaderData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOutward3HeaderData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<String> listOfSuppGstin;
			List<String> listOfReturnPrds;
			JsonArray suppGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			suppGstinArray = requestObject.getAsJsonArray("supplierGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfSuppGstin = gson.fromJson(suppGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			Outward3HeaderDetailsDto headerData = dashboardService
					.getOutward3HeaderData(fy, listOfSuppGstin,
							listOfReturnPrds);

			String jsonEINV = gson.toJson(headerData);
			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("headerData", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Outward3 Header Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getUtilSummData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUtilizationSummaryData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<String> listOfSuppGstin;
			List<String> listOfReturnPrds;
			JsonArray suppGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			suppGstinArray = requestObject.getAsJsonArray("supplierGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfSuppGstin = gson.fromJson(suppGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Gstr1FioriDashboardChartDto> utilSummData = dashboardService
					.getUtilizationSummData(fy, listOfSuppGstin,
							listOfReturnPrds);

			String jsonEINV = null;
			if (utilSummData == null)
				jsonEINV = gson.toJson("No Data Found");
			else
				jsonEINV = gson.toJson(utilSummData);

			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("utilSummData", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Utilization Summary Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getGstNetLiabData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstNetLiabilityDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<String> listOfSuppGstin;
			List<String> listOfReturnPrds;
			JsonArray suppGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			suppGstinArray = requestObject.getAsJsonArray("supplierGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfSuppGstin = gson.fromJson(suppGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Gstr1FioriDashboardChartDto> netLiabData = dashboardService
					.getGstNetLiabDetails(fy, listOfSuppGstin,
							listOfReturnPrds);

			String jsonEINV = null;
			if (netLiabData == null)
				jsonEINV = gson.toJson("No Data Found");
			else
				jsonEINV = gson.toJson(netLiabData);

			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("gstNetLiab", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching GST Net Liability Details";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@PostMapping(value = "/ui/getLiabTableData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getLiabilityTableData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<String> listOfSuppGstin;
			List<String> listOfReturnPrds;
			JsonArray suppGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			suppGstinArray = requestObject.getAsJsonArray("supplierGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfSuppGstin = gson.fromJson(suppGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<GetLiabTableDto> liabTableData = dashboardService
					.getLiabilityTbleDetails(fy, listOfSuppGstin,
							listOfReturnPrds);

			String jsonEINV = null;
			if (liabTableData == null)
				jsonEINV = gson.toJson("No Data Found");
			else
				jsonEINV = gson.toJson(liabTableData);

			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("liabTableData", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Liability Table Details";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@PostMapping(value = "/ui/getOut3SGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOut3SGstins(@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			Long entityId = requestObject.get("entityId").getAsLong();
			List<GstinDto> gstinList = dashboardService
					.getAllSupplierGstins(entityId);
			String jsonEINV = gson.toJson(gstinList);
			JsonElement gstinListElement = new JsonParser().parse(jsonEINV);
			resp.add("gstins", gstinListElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching all Supplier Gstins";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getOut3RPrds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOut3RPrds(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			List<FinancialYearDto> retrunPeriodsList = dashboardService
					.getAllReturnPeriods(fy);
			String jsonEINV = gson.toJson(retrunPeriodsList);
			JsonElement returnPeriodElement = new JsonParser().parse(jsonEINV);
			resp.add("returnPeriods", returnPeriodElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching all return Periods";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
