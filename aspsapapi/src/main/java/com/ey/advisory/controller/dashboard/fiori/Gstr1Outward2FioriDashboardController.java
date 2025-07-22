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

import com.ey.advisory.app.services.dashboard.fiori.B2bCntDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1FioriDashboardChartDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1Outward2FioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.Outward2HeaderDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.Outward2TurnOverDto;
import com.ey.advisory.app.services.dashboard.fiori.OutwardSupplyDetailsDto;
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
public class Gstr1Outward2FioriDashboardController {

	@Autowired
	@Qualifier("Gstr1Outward2FioriDashboardServiceImpl")
	private Gstr1Outward2FioriDashboardService dashboardService;

	@PostMapping(value = "/ui/getOutward2HeaderData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOutward2HeaderData(
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

			Outward2HeaderDetailsDto headerData = dashboardService
					.getOutward2HeaderData(fy, listOfSuppGstin,
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
			String msg = "Unexpected error while fetching Outward2 Header Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getPsdVsErrData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPsdVsErrData(
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

			List<B2bCntDto> prsdVsErrRecords = dashboardService
					.getPsdVsErrRecords(fy, listOfSuppGstin, listOfReturnPrds);

			String jsonEINV = gson.toJson(prsdVsErrRecords);
			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("psdVsErr", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching outward2 Processed "
					+ " vs Error Records";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getCompAnalysisData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCompAnalysisData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			String valueFlag = requestObject.get("valueFlag").getAsString();
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
			List<B2bCntDto> revCompAnalysisforB2b = null;
			List<Gstr1FioriDashboardChartDto> revenueCompAnalysisData = null;
			String jsonEINV = null;
			if (valueFlag.equalsIgnoreCase("B2B_CNT")) {
				revCompAnalysisforB2b = dashboardService
						.getRevenueComparativeAnalysisForB2b(fy, valueFlag,
								listOfSuppGstin, listOfReturnPrds);
				jsonEINV = gson.toJson(revCompAnalysisforB2b);
			} else {
				revenueCompAnalysisData = dashboardService
						.getRevenueComparativeAnalysis(fy, valueFlag,
								listOfSuppGstin, listOfReturnPrds);
				jsonEINV = gson.toJson(revenueCompAnalysisData);
			}

			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("revenueCompAnalysis", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching getCompAnalysisData";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getOutSuppDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOutwardSupplyDetails(
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

			List<OutwardSupplyDetailsDto> supplyDetails = dashboardService
					.getOutwardSuppliyDetails(fy, listOfSuppGstin,
							listOfReturnPrds);

			String jsonEINV = gson.toJson(supplyDetails);
			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("suppDetails", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching outward Supply "
					+ " Details";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getOut2SGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOut2SGstins(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			JsonArray entityIdArray = requestObject.getAsJsonArray("entityId");
			List<Long> entityIds = new ArrayList<>();

			for (JsonElement element : entityIdArray) {
				entityIds.add(element.getAsLong());
			}
			List<GstinDto> gstinList = dashboardService
					.getAllSupplierGstins(entityIds);
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

	@PostMapping(value = "/ui/getOut2RPrds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOut2RPrds(@RequestBody String jsonString) {
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

	@PostMapping(value = "/ui/getOut2TotalTurnOver", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOut2TotalTurnOver(
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

			Outward2TurnOverDto turnOverData = dashboardService
					.getTotalTurnOverAndTax(fy, listOfSuppGstin,
							listOfReturnPrds);

			String jsonEINV = gson.toJson(turnOverData);
			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("turnOverData", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Outward2 Total TurnOver and Tax";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
