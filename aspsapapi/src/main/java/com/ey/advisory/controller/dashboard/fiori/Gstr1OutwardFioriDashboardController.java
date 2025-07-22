package com.ey.advisory.controller.dashboard.fiori;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.dashboard.fiori.Gstr1FioriDashboardChartDto;
import com.ey.advisory.app.services.dashboard.fiori.Gstr1OutwardFioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.MajorTaxPayFinalDto;
import com.ey.advisory.app.services.dashboard.fiori.TaxLiabilityDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.TaxLiabilityDetailsFinalDto;
import com.ey.advisory.app.services.dashboard.fiori.TaxRateWiseDistributionDto;
import com.ey.advisory.app.services.dashboard.fiori.TotalLiabilityDetailsDto;
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
public class Gstr1OutwardFioriDashboardController {

	@Autowired
	private Gstr1OutwardFioriDashboardService dashboardService;

	@PostMapping(value = "/ui/getSupGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSupGstins(@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			/*Long entityId = requestObject.get("entityId").getAsLong();*/
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

	@PostMapping(value = "/ui/getAllRtrnPrds", produces = {
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

	@PostMapping(value = "/ui/getGrossOutSup", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGrossOutwardSupplies(
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
			String flag = requestObject.get("flag").getAsString();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfSuppGstin = gson.fromJson(suppGstinArray, listType);
			listOfReturnPrds = gson.fromJson(returnPrdArray, listType);

			List<Gstr1FioriDashboardChartDto> grossOutwardList = dashboardService
					.getGrossOutwardSuppplies(fy, listOfSuppGstin,
							listOfReturnPrds, flag);
			String jsonEINV = gson.toJson(grossOutwardList);
			JsonElement grossOutwardElement = new JsonParser().parse(jsonEINV);
			resp.add("grossOutward", grossOutwardElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Gross Outward Supplies";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getMonthWiseTrend", produces = {
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

			List<Gstr1FioriDashboardChartDto> monthWiseTrendList = dashboardService
					.getMonthWiseTrendAnalysisList(fy, valueFlag,
							listOfSuppGstin, listOfReturnPrds);
			if (monthWiseTrendList != null && !monthWiseTrendList.isEmpty()) {
				Collections.sort(monthWiseTrendList,
						new Comparator<Gstr1FioriDashboardChartDto>() {
							public int compare(Gstr1FioriDashboardChartDto o1,
									Gstr1FioriDashboardChartDto o2) {
								String l = o1.getXAxis().substring(2, 6)
										.concat(o1.getXAxis().substring(0, 2));
								String r = o2.getXAxis().substring(2, 6)
										.concat(o2.getXAxis().substring(0, 2));
								return (l.compareTo(r));
							}
						});
			}
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

	@PostMapping(value = "/ui/getTopCustB2B", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTopCustomerB2B(
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

			List<Gstr1FioriDashboardChartDto> topCustomerB2bList = dashboardService
					.getTopCustomersB2BList(fy, listOfSuppGstin,
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

	@PostMapping(value = "/ui/getMajTaxPayingProds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMajorTaxPyingProducts(
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

			List<Gstr1FioriDashboardChartDto> majorTaxPayingProductList = dashboardService
					.getMajorTaxPayingProductsList(fy, listOfSuppGstin,
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

	@PostMapping(value = "/ui/getTaxRateWiseDist", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxRateWiseDistribution(
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

			List<TaxRateWiseDistributionDto> taxRateWiseDistList = dashboardService
					.getTaxRateWiseDistributionList(fy, listOfSuppGstin,
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

	@PostMapping(value = "/ui/getTotalLiabDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTotalLiabilityDetails(
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

			TotalLiabilityDetailsDto totalLiabDetails = dashboardService
					.getTotalLiabilityData(fy, listOfSuppGstin,
							listOfReturnPrds);

			String totalLiability = gson.toJson(totalLiabDetails);
			JsonElement totalLiabilityElement = new JsonParser()
					.parse(totalLiability);
			resp.add("totalLiabDetails", totalLiabilityElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Total Liability "
					+ "Details";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getTaxLiabDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxLiabilityDetails(
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

			List<TaxLiabilityDetailsDto> taxLiabDataList = dashboardService
					.getTaxLiabilityData(fy, listOfSuppGstin, listOfReturnPrds);
			String jsonEINV = gson.toJson(taxLiabDataList);
			JsonElement taxLiabDataElement = new JsonParser().parse(jsonEINV);
			resp.add("taxLiability", taxLiabDataElement);
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

	@PostMapping(value = "/ui/getTaxLiabIoclDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxLiabilityIoclDetails(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			JsonArray suppGstinArray = requestObject
					.getAsJsonArray("supplierGstins");
			JsonArray returnPrdArray = requestObject
					.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			List<String> listOfSuppGstin = gson.fromJson(suppGstinArray,
					listType);
			List<String> listOfReturnPrds = gson.fromJson(returnPrdArray,
					listType);

			List<TaxLiabilityDetailsFinalDto> taxLiabDataList = dashboardService
					.getTaxLiabilityIoclData(fy, listOfSuppGstin,
							listOfReturnPrds);
			String jsonEINV = gson.toJson(taxLiabDataList);
			JsonElement taxLiabDataElement = JsonParser.parseString(jsonEINV);
			resp.add("taxLiability", taxLiabDataElement);
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

	@PostMapping(value = "/ui/getMajorTaxPayingProduct", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMajorTaxPayingProduct(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String fy = requestObject.get("fy").getAsString();
			JsonArray suppGstinArray = requestObject
					.getAsJsonArray("supplierGstins");
			JsonArray returnPrdArray = requestObject
					.getAsJsonArray("returnPeriods");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			List<String> listOfSuppGstin = gson.fromJson(suppGstinArray,
					listType);
			List<String> listOfReturnPrds = gson.fromJson(returnPrdArray,
					listType);

			List<MajorTaxPayFinalDto> majorTaxPayFinalDto = dashboardService
					.getMajorTaxpayDetails(fy, listOfSuppGstin,
							listOfReturnPrds);
			String jsonEINV = gson.toJson(majorTaxPayFinalDto);
			JsonElement taxLiabDataElement =JsonParser.parseString(jsonEINV);
			resp.add("majorTaxPayProducts", taxLiabDataElement);
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

}
