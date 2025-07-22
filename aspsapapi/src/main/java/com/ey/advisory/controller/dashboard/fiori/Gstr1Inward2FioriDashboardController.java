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
import com.ey.advisory.app.services.dashboard.fiori.Gstr1Inward2FioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.Gstr2aVsPurchaseRegisterDto;
import com.ey.advisory.app.services.dashboard.fiori.Inward2HeaderDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.LastRefreshedOnDto;
import com.ey.advisory.app.services.dashboard.fiori.LastUpdatedOnDto;
import com.ey.advisory.app.services.dashboard.fiori.Pr2a2bDataDto;
import com.ey.advisory.app.services.dashboard.fiori.Top10SuppliersDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@RestController
@Slf4j
public class Gstr1Inward2FioriDashboardController {

	@Autowired
	@Qualifier("Gstr1Inward2FioriDashboardServiceImpl")
	Gstr1Inward2FioriDashboardService dashboardService;

	@PostMapping(value = "/ui/getInward2HeaderData", produces = {
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Inward2HeaderDetailsDto> headerData = dashboardService
					.getInward2HeaderData(fy, listOfRecepGstin,
							listOfReturnPrds);

			String jsonEINV = gson.toJson(headerData);
			JsonElement grossInwardElement = new JsonParser().parse(jsonEINV);
			resp.add("headerData", grossInwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Inward2 Header Data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getPrVsGstr2aData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getprVsGstr2aData(
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Top10SuppliersDto> prVsGstr2aRecords = dashboardService
					.getprVsGstr2aRecords(fy, listOfRecepGstin,
							listOfReturnPrds);

			String jsonEINV = gson.toJson(prVsGstr2aRecords);
			JsonElement grossInwardElement = new JsonParser().parse(jsonEINV);
			resp.add("prVsGstr2a", grossInwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching inward2 pr "
					+ " vs gstr2a Records";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getPrVsGstr2bData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getprVsGstr2bData(
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Top10SuppliersDto> prVsGstr2bRecords = dashboardService
					.getprVsGstr2bRecords(fy, listOfRecepGstin,
							listOfReturnPrds);

			String jsonEINV = gson.toJson(prVsGstr2bRecords);
			JsonElement grossInwardElement = new JsonParser().parse(jsonEINV);
			resp.add("prVsGstr2b", grossInwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching inward2 pr "
					+ " vs Gstr2b Records";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getPurchaseRegisterVsGstr2b", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPurchaseRegisterVsGstr2b(
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);
			List<Gstr2aVsPurchaseRegisterDto> purchaseRegisterVsGstr2b = null;
			String jsonEINV = null;
			purchaseRegisterVsGstr2b = dashboardService
					.getPurchaseRegisterVsGstr2b(fy, listOfRecepGstin,
							listOfReturnPrds);
			jsonEINV = gson.toJson(purchaseRegisterVsGstr2b);

			JsonElement grossiInwardElement = new JsonParser().parse(jsonEINV);
			resp.add("purchaseRegisterVsGstr2b", grossiInwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching getPurchaseRegisterVsGstr2b";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getPurchaseRegisterVsGstr2a", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPurchaseRegisterVsGstr2a(
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);
			List<Gstr2aVsPurchaseRegisterDto> purchaseRegisterVsGstr2b = null;
			String jsonEINV = null;
			purchaseRegisterVsGstr2b = dashboardService
					.getPurchaseRegisterVsGstr2a(fy, listOfRecepGstin,
							listOfReturnPrds);
			jsonEINV = gson.toJson(purchaseRegisterVsGstr2b);

			JsonElement grossiInwardElement = new JsonParser().parse(jsonEINV);
			resp.add("purchaseRegisterVsGstr2a", grossiInwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching getPurchaseRegisterVsGstr2a";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/get2aVs2bVsPrSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> get2aVs2bVsPrSummary(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			String valueFlag = requestObject.get("valueFlag").getAsString();
			List<String> listOfRecepGstin;
			List<String> listOfReturnPrds;
			JsonArray recepGstinArray = new JsonArray();
			JsonArray returnPrdArray = new JsonArray();
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);
			List<B2bCntDto> get2aVs2bVsPrSummarySuppliersList = null;
			List<Gstr1FioriDashboardChartDto> get2aVs2bVsPrSummaryTaxableList = null;
			List<Gstr1FioriDashboardChartDto> get2aVs2bVsPrSummaryTotalTaxList = null;
			String jsonEINV = null;

			if (valueFlag.equalsIgnoreCase("TOP_SUPPLIERS")) {
				get2aVs2bVsPrSummarySuppliersList = dashboardService
						.get2aVs2bVsPrSummarySuppliers(fy, valueFlag,
								listOfRecepGstin, listOfReturnPrds);
				jsonEINV = gson.toJson(get2aVs2bVsPrSummarySuppliersList);
			} else if (valueFlag.equalsIgnoreCase("Taxable_Value")) {
				get2aVs2bVsPrSummaryTaxableList = dashboardService
						.get2aVs2bVsPrSummaryTaxable(fy, valueFlag,
								listOfRecepGstin, listOfReturnPrds);
				jsonEINV = gson.toJson(get2aVs2bVsPrSummaryTaxableList);
			} else if (valueFlag.equalsIgnoreCase("Total_Tax")) {
				get2aVs2bVsPrSummaryTotalTaxList = dashboardService
						.get2aVs2bVsPrSummaryTotalTax(fy, valueFlag,
								listOfRecepGstin, listOfReturnPrds);
				jsonEINV = gson.toJson(get2aVs2bVsPrSummaryTotalTaxList);
			}
			JsonElement grossInwardElement = new JsonParser().parse(jsonEINV);
			resp.add("get2aVs2bVsPrSummary", grossInwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching get2aVs2bVsPr Summary";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getPr2a2bData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPr2a2bData(
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Pr2a2bDataDto> taxLiabDataList = dashboardService
					.getPr2a2bData(fy, listOfRecepGstin, listOfReturnPrds);
			String jsonEINV = gson.toJson(taxLiabDataList);
			JsonElement pr2a2bDataElement = new JsonParser().parse(jsonEINV);
			resp.add("pr2a2bData", pr2a2bDataElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching tax liability data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getLastRefereshedOn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getLastRefereshedOn(
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<LastRefreshedOnDto> lastRefreshedDataList = dashboardService
					.getLastRefereshedOn(fy, listOfRecepGstin,
							listOfReturnPrds);
			String jsonEINV = gson.toJson(lastRefreshedDataList);
			JsonElement lastRefreshedDataElement = new JsonParser()
					.parse(jsonEINV);
			resp.add("lastRefreshedOn", lastRefreshedDataElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching LastRefreshed data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getReconLastUpdatedOn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconLastUpdatedOn(
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
			recepGstinArray = requestObject.getAsJsonArray("recepientGstins");
			returnPrdArray = requestObject.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<LastUpdatedOnDto> lastUpdatedOn = dashboardService
					.getReconLastUpdatedOn(fy, listOfRecepGstin,
							listOfReturnPrds);
			String jsonEINV = gson.toJson(lastUpdatedOn);
			JsonElement lastUpdatedOnElement = new JsonParser().parse(jsonEINV);
			resp.add("lastUpdatedOn", lastUpdatedOnElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching last updated on data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
