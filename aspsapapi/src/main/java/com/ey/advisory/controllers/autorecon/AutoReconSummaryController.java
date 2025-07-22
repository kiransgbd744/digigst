package com.ey.advisory.controllers.autorecon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoReconSummaryService;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoReconSummaryTabDto;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.IncrementalDataSummaryTabDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;
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

@Slf4j
@RestController
public class AutoReconSummaryController {

	@Autowired
	@Qualifier("AutoReconSummaryServiceImpl")
	private AutoReconSummaryService autoReconSummaryService;

	@PostMapping(value = "/ui/getAutoReconSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAutoReconSummary(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			String toTaxPeriod2A = null;
			String fromTaxPeriod2A = null;
			String toTaxPeriodPR = null;
			String fromTaxPeriodPR = null;
			String toReconDate = null;
			String fromReconDate = null;
			String criteria = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside getAutoReconSummary with request as " + " : %s",
						jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			JsonArray gstins = reqJson.getAsJsonArray("recipientGstins");

			Gson googleJson = new Gson();
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> recipientGstins = googleJson.fromJson(gstins,
					listType);

			if (reqJson.has("toTaxPeriodPR"))
				toTaxPeriodPR = reqJson.get("toTaxPeriodPR").getAsString();

			if (reqJson.has("fromTaxPeriodPR"))
				fromTaxPeriodPR = reqJson.get("fromTaxPeriodPR").getAsString();

			if (reqJson.has("toTaxPeriod2A"))
				toTaxPeriod2A = reqJson.get("toTaxPeriod2A").getAsString();

			if (reqJson.has("fromTaxPeriod2A"))
				fromTaxPeriod2A = reqJson.get("fromTaxPeriod2A").getAsString();

			if (reqJson.has("toReconDate"))
				toReconDate = reqJson.get("toReconDate").getAsString();

			if (reqJson.has("fromReconDate"))
				fromReconDate = reqJson.get("fromReconDate").getAsString();
			
			if (reqJson.has("criteria"))
				criteria = reqJson.get("criteria").getAsString();

			Long entityId = reqJson.get("entityId").getAsLong();

			if (toTaxPeriod2A != null && fromTaxPeriod2A != null
					&& toTaxPeriodPR != null && fromTaxPeriodPR != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"getAutoReconSummary for "
									+ "Parameters toReturnPeriod2A %s To "
									+ "fromReturnPeriod2A %s toReturnPeriodPR %s"
									+ " To fromReturnPeriodPR %s,   " + ": ",
							toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
							fromTaxPeriodPR);
					LOGGER.debug(msg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("getAutoReconSummary for "
							+ "Parameters fromDocDate %s To toDocDate  %s "
							+ ": ", fromReconDate, toReconDate);
					LOGGER.debug(msg);
				}
			}

			if (CollectionUtils.isEmpty(recipientGstins))
				throw new AppException("User did not select any gstin");

			AutoReconSummaryTabDto reconSummary = autoReconSummaryService
					.getReconSummaryDetails(recipientGstins, fromTaxPeriodPR,
							toTaxPeriodPR, fromTaxPeriod2A, toTaxPeriod2A,
							fromReconDate, toReconDate, entityId, criteria);

			String jsonReconReqData = gson.toJson(reconSummary);
			JsonElement jsonElement = new JsonParser().parse(jsonReconReqData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("summaryDetails", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Processing the getAutoReconSummary "
									+ ex));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getIncrementalDataSumm", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getIncrementalDataSummary(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside getIncrementalDataSummary with request as "
								+ " : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			JsonArray gstins = reqJson.getAsJsonArray("recipientGstins");
			Long entityId = reqJson.get("entityId").getAsLong();

			Gson googleJson = new Gson();
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> recipientGstins = googleJson.fromJson(gstins,
					listType);

			if (CollectionUtils.isEmpty(recipientGstins))
				throw new AppException("User did not select any gstin");

			IncrementalDataSummaryTabDto incrementalData = autoReconSummaryService
					.getIncremenatalDataSummaryDetails(recipientGstins,
							entityId);

			String jsonReconReqData = gson.toJson(incrementalData);
			JsonElement jsonElement = new JsonParser().parse(jsonReconReqData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("incrementalData", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Processing the getIncrementalDataSummary "
									+ ex));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getReconSummGstinData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconSummGstinData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside getReconSummGstinData with request as "
								+ " : %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			Long entityId = reqJson.get("entityId").getAsLong();

			List<Gstr2ReconSummaryStatusDto> gstinsData = autoReconSummaryService
					.getAutoReconGstinDetails(entityId);

			String jsonReconReqData = gson.toJson(gstinsData);
			JsonElement jsonElement = new JsonParser().parse(jsonReconReqData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("gstinsData", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Processing the getReconSummGstinData "
									+ ex));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
