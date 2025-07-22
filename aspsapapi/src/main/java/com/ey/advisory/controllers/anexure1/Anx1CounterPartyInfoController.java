package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.anx1.counterparty.CounterPartyInfoResponseDto;
import com.ey.advisory.app.anx1.counterparty.CounterPartyInfoService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Anx1CounterPartyInfoController {

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("CounterPartyInfoServiceImpl")
	private CounterPartyInfoService counterPartyInfoService;

	@PostMapping(value = "/ui/getAllEntities", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllEntities(
			@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EntityServiceImpl"
						+ ".getAllEntities ,Parsing Input request";
				LOGGER.debug(msg);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			String groupCode = TenantContext.getTenantId();
			List<EntityInfoEntity> entityList = entityService
					.getAllEntities(groupCode);
			if (LOGGER.isDebugEnabled()) {
				String msg = "EntityServiceImpl"
						+ ".getAllEntities Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject gstinResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(entityList);
			gstinResp.add("entities", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End EntityServiceImpl"
						+ ".getAllEntities ,before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/getGSTINsForEntity", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTINsForEntity(
			@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EntityServiceImpl"
						+ ".getGSTINsForEntity ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			Long entityId = requestObject.has("entityId")
					? requestObject.get("entityId").getAsLong() : null;

			List<String> gstnsList = entityService.getGSTINsForEntity(entityId);
			if (LOGGER.isDebugEnabled()) {
				String msg = "EntityServiceImpl"
						+ ".getGSTINsForEntity Preparing Response Object";
				LOGGER.debug(msg);
			}
			List<GstinDto> gstinDtoList = gstnsList.stream().map(GstinDto::new)
					.collect(Collectors.toList());
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(gstinDtoList);
			gstinResp.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End EntityServiceImpl"
						+ ".getGSTINsForEntity ,before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/getCounterPartyInfo", 
						produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCounterPartyInfo(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin CounterPartyInfoServiceImpl"
						+ ".getCounterPartyInfo ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonArray gstins = new JsonArray();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.gsonInstanceWithExposeAndNull();
			Gson googleJson = new Gson();
			List<String> sgstins = null;
			
			Long entityId = requestObject.has("entityId") ? requestObject
					.get("entityId").getAsLong() : null;
					
				if ((requestObject.has("gstins"))
					&& (requestObject.getAsJsonArray("gstins").size() > 0)) {
						if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("As gstins are provided in request, not "
								+ "Invoking service to get gstins for entity");
						}
						gstins = requestObject.getAsJsonArray("gstins");
						Type listType = new TypeToken<ArrayList<String>>() {
						}.getType();
						sgstins = googleJson.fromJson(gstins, listType);
					}

			/*
			 * if gstin is not provided then the gstins will be loaded from the
			 * user context
			 */

			else {
				Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
				Map<String, String> outwardSecurityAttributeMap = 
					DataSecurityAttributeUtil.getOutwardSecurityAttributeMap();
				dataSecurityAttrMap = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
								outwardSecurityAttributeMap);
				if (CollectionUtils.isEmpty(sgstins)) {
					sgstins = dataSecurityAttrMap.get(OnboardingConstant.GSTIN);
				}
			}
			if (CollectionUtils.isEmpty(sgstins))
				throw new AppException("User Does not have any gstin");

			String taxPeriod = requestObject.get("taxPeriod").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = "CounterPartyInfoServiceImpl"
						+ ".getCounterPartyInfo,"
						+ "Input Request has been parsed and validated,"
						+ "calling CounterPartyInfoService ";
				LOGGER.debug(msg);
			}
			validateInput(taxPeriod, sgstins);
			List<CounterPartyInfoResponseDto> infoList = counterPartyInfoService
					.getCounterPartyInfo(sgstins, taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				String msg = "CounterPartyInfoServiceImpl"
					+ ".getCounterPartyInfo Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject gstinDetResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(infoList);
			gstinDetResp.add("gstinDet", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinDetResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End CounterPartyInfoServiceImpl"
					+ ".getCounterPartyInfo, before returning response";
				LOGGER.debug(msg);
			}
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	private void validateInput(String taxPeriod, List<String> sgstins) {

		String msg = null;
		if (StringUtils.isBlank(taxPeriod)) {
			msg = "Tax period cannot be empty.";
		} else if (sgstins == null || sgstins.isEmpty()) {
			msg = "At least one gstin should be selected.";
		}
		if (msg != null) {
			throw new AppException(msg);
		}

	}

}
