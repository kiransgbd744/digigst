package com.ey.advisory.controller.dashboard.fiori;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.dashboard.fiori.EInvoiceDistributionDto;
import com.ey.advisory.app.services.dashboard.fiori.EInvoiceSummaryDto;
import com.ey.advisory.app.services.dashboard.fiori.EinvErrorDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.EinvGenerationDto1;
import com.ey.advisory.app.services.dashboard.fiori.EinvGenerationDto2;
import com.ey.advisory.app.services.dashboard.fiori.EinvHeaderDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.EinvIrnDto;
import com.ey.advisory.app.services.dashboard.fiori.EinvoiceFioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.EinvoiceStatusDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@RestController
@Slf4j
public class EinvoiceFioriDashboardController {

	@Autowired
	private EinvoiceFioriDashboardService dashboardService;

	@PostMapping(value = "/ui/getEinvSupGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSupGstins(@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			//Long entityId = requestObject.get("entityId").getAsLong();
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

	@PostMapping(value = "/ui/getEinvHeaders", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvHeaders(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			EinvHeaderDetailsDto headerDetails = dashboardService
					.getEinvHeaderDetails(supplierGstins,
							LocalDate.parse(fromSummDate),
							LocalDate.parse(toSummDate));

			String jsonEINV = gson.toJson(headerDetails);
			JsonElement einvHeaderElement = new JsonParser().parse(jsonEINV);
			resp.add("headerDetails", einvHeaderElement);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(headerDetails);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Header " + "Details";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getEinvAvgIrn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvAvgIrn(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			EinvIrnDto irn = dashboardService.getAvgIrnGenPerMonth(
					supplierGstins, LocalDate.parse(fromSummDate),
					LocalDate.parse(toSummDate));
			String jsonEINV = gson.toJson(irn);
			JsonElement jsonIrn = new JsonParser().parse(jsonEINV);
			resp.add("irnData", jsonIrn);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Einvoice Irn generation per month";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getEinvSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvSummary(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			Long entityId = requestObject.get("entityId").getAsLong();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			EInvoiceSummaryDto einvSummary = dashboardService
					.getEinvSummaryData(supplierGstins,
							LocalDate.parse(fromSummDate),
							LocalDate.parse(toSummDate), entityId);

			String jsonEINV = gson.toJson(einvSummary);
			JsonElement einvSummaryElement = new JsonParser().parse(jsonEINV);
			resp.add("einvSummary", einvSummaryElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Einvoice Summary";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getEinvDistribution", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvDistribution(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			List<EInvoiceDistributionDto> einvDistribution = dashboardService
					.getEinvDistributionData(supplierGstins,
							LocalDate.parse(fromSummDate),
							LocalDate.parse(toSummDate));
			String jsonEINV = gson.toJson(einvDistribution);
			JsonElement einvDistributionElement = new JsonParser()
					.parse(jsonEINV);
			resp.add("einvDistribution", einvDistributionElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Einvoice distribution";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getEinvGenTrendForTotal", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvGenTrendForTotal(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			List<EinvGenerationDto1> einvGenTrends = dashboardService
					.getEinvGenTredForGenAndTotal(supplierGstins,
							LocalDate.parse(fromSummDate),
							LocalDate.parse(toSummDate));
			String jsonEINV = gson.toJson(einvGenTrends);
			JsonElement einvGenTrendsElement = new JsonParser().parse(jsonEINV);
			resp.add("einvGenTrends", einvGenTrendsElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Einvoice Generation"
					+ " Trend over time for Generated and Total";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getEinvGenTrendForError", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvGenTrendForError(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			List<EinvGenerationDto2> einvGenTrends = dashboardService
					.getEinvGenTredForCanDupAndErr(supplierGstins,
							LocalDate.parse(fromSummDate),
							LocalDate.parse(toSummDate));
			String jsonEINV = gson.toJson(einvGenTrends);
			JsonElement einvGenTrendsElement = new JsonParser().parse(jsonEINV);
			resp.add("einvGenTrendsError", einvGenTrendsElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Einvoice Generation"
					+ " Trend over time for Cancelled, Duplicate and Error";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getEinvStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			List<EinvoiceStatusDto> einvoiceStatus = dashboardService
					.getEinvStatusTable(supplierGstins,
							LocalDate.parse(fromSummDate),
							LocalDate.parse(toSummDate));
			String jsonEINV = gson.toJson(einvoiceStatus);
			JsonElement einvoiceStatusElement = new JsonParser()
					.parse(jsonEINV);
			resp.add("einvoiceStatus", einvoiceStatusElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Einvoice Status";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getEinvErrorDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvErrorDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			List<String> supplierGstins;

			JsonArray supGstinArray = new JsonArray();
			String fromSummDate = requestObject.get("fromDate").getAsString();
			String toSummDate = requestObject.get("toDate").getAsString();
			supGstinArray = requestObject.getAsJsonArray("supplierGstins");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			supplierGstins = gson.fromJson(supGstinArray, listType);

			if(supplierGstins.isEmpty())
				throw new AppException("Please select atleast one Gstin");
			
			List<EinvErrorDetailsDto> errorDetails = dashboardService
					.getEinvErrorDetails(supplierGstins,
							LocalDate.parse(fromSummDate),
							LocalDate.parse(toSummDate));
			String jsonEINV = gson.toJson(errorDetails);
			JsonElement errorDetailsElement = new JsonParser().parse(jsonEINV);
			resp.add("errorDetails", errorDetailsElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Einvoice Error Details";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
