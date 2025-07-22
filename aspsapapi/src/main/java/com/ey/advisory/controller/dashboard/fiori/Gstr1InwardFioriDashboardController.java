package com.ey.advisory.controller.dashboard.fiori;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.dashboard.fiori.Gstr1FioriDashboardChartDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1FioriDashboardInwardChartDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1InwardFioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.LastUpdatedOnDto;
import com.ey.advisory.app.services.dashboard.fiori.TaxInwardDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.TaxRateWiseDistributionDto;
import com.ey.advisory.app.services.dashboard.fiori.TotalItcDetailsDto;
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
 * @author Ravindra V S
 *
 */
@RestController
@Slf4j
public class Gstr1InwardFioriDashboardController {

	@Autowired
	private Gstr1InwardFioriDashboardService dashboardService;

	@PostMapping(value = "/ui/getRecepGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRecepGstins(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			JsonArray entityIdArray = requestObject.getAsJsonArray("entityId");
			List<Long> entityIds = new ArrayList<>();

			for (JsonElement element : entityIdArray) {
				entityIds.add(element.getAsLong());
			}
			List<GstinDto> gstinList = dashboardService
					.getAllRecepientGstins(entityIds);
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
			String msg = "Unexpected error while fetching all Recepient Gstins";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getAllReturnPeriods", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllReturnPeriods(
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

	@PostMapping(value = "/ui/getGrossInSup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGrossInwardSupplies(
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
			String flag = requestObject.get("flag").getAsString();
			
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfRecepGstin = gson.fromJson(recepGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Gstr1FioriDashboardChartDto> grossOutwardList = dashboardService
					.getGrossInwardSuppplies(fy, listOfRecepGstin,
							listOfReturnPrds, flag);
			String jsonEINV = gson.toJson(grossOutwardList);
			JsonElement grossInwardElement = new JsonParser().parse(jsonEINV);
			resp.add("grossInward", grossInwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Gross Inward Supplies";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getMonthWiseTrendAnalysis", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMonthWiseTrendAnalysis(
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

			List<Gstr1FioriDashboardChartDto> monthWiseTrendList = dashboardService
					.getMonthWiseTrendAnalysisList(fy, valueFlag,
							listOfRecepGstin, listOfReturnPrds);
			String jsonEINV = gson.toJson(monthWiseTrendList);
			JsonElement monthWiseElement = new JsonParser().parse(jsonEINV);
			resp.add("monthWiseAnalysis", monthWiseElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching month wise trend analysis";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getTopCustomerB2B", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTopCustomerB2B(
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

			List<Gstr1FioriDashboardChartDto> topCustomerB2bList = dashboardService
					.getTopCustomersB2BList(fy, listOfRecepGstin,
							listOfReturnPrds);
			String jsonEINV = gson.toJson(topCustomerB2bList);
			JsonElement topCustomerB2bElement = new JsonParser()
					.parse(jsonEINV);
			resp.add("topCustomers", topCustomerB2bElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching top customer B2B data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getMajGoodsProc", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMajorGoodsProcurred(
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

			List<Gstr1FioriDashboardInwardChartDto> majorTaxPayingProductList = dashboardService
					.getMajorGoodsProcurred(fy, listOfRecepGstin,
							listOfReturnPrds);
			String jsonEINV = gson.toJson(majorTaxPayingProductList);
			JsonElement majTaxPayingProdElement = new JsonParser()
					.parse(jsonEINV);
			resp.add("majTaxPayingProds", majTaxPayingProdElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching major tax paying "
					+ "product data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getTaxRateWiseDistribution", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxRateWiseDistribution(
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

			List<TaxRateWiseDistributionDto> taxRateWiseDistList = dashboardService
					.getTaxRateWiseDistributionList(fy, listOfRecepGstin,
							listOfReturnPrds);

			String jsonEINV = gson.toJson(taxRateWiseDistList);
			JsonElement taxRateWiseDistElement = new JsonParser()
					.parse(jsonEINV);
			resp.add("taxRateWise", taxRateWiseDistElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching tax rate wise "
					+ "distribution data";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getTaxInwardDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxInwardDetails(
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

			List<TaxInwardDetailsDto> taxLiabDataList = dashboardService
					.getTaxInwardData(fy, listOfRecepGstin, listOfReturnPrds);
			String jsonEINV = gson.toJson(taxLiabDataList);
			JsonElement taxInwardDataElement = new JsonParser().parse(jsonEINV);
			resp.add("taxInwardDetails", taxInwardDataElement);
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

	@PostMapping(value = "/ui/getTotalITCDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTotalITCDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			String[] fy1 = fy.split("-");
			fy = fy1[0] + fy1[1];
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

			TotalItcDetailsDto totalItcDetails = dashboardService
					.getTotalItcData(fy, listOfRecepGstin, listOfReturnPrds);

			/*
			 * String totalItc = gson.toJson(totalItcDetails); JsonElement
			 * totalItcElement = new JsonParser().parse(totalItc);
			 * resp.add("totalItcDetails", totalItcElement);
			 */
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(totalItcDetails);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Total ITC "
					+ "Details";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getLastUpdatedOn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getLastUpdatedOn(
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
					.getLastUpdatedOn(fy, listOfRecepGstin, listOfReturnPrds);
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
