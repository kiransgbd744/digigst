package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.common.GstinReqTypDto;
import com.ey.advisory.app.docs.dto.ManageAuthTokenDetDto;
import com.ey.advisory.app.docs.dto.ManageAuthTokenDetReqDto;
import com.ey.advisory.app.services.manageauthtoken.AuthTokenDetailService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ManageAuthTokenDetController {

	@Autowired
	@Qualifier("authTokenDetailServiceImpl")
	AuthTokenDetailService authDetailService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstnDetailRepository;
	


	@PostMapping(value = "/ui/getAuthTokenDetails1", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAuthDetails(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("This is Auth Token Details Controller with "
							+ "Request Param %s :", jsonReq);
			LOGGER.debug(msg);
		}

		List<ManageAuthTokenDetDto> respArr = new ArrayList<ManageAuthTokenDetDto>();
		// This is Resp Dto For AuthToken Detail Screen
		Gson gson = GsonUtil.newSAPGsonInstance();
		ManageAuthTokenDetDto respDto = new ManageAuthTokenDetDto();
		respDto.setGstin("11SUPEY1234A110");
		respDto.setMobileNo("8883483784");
		respDto.setEmail("nike.sharma@orkut.com");
		respDto.setStatus("A");
		respDto.setAction(true);

		ManageAuthTokenDetDto obj = new ManageAuthTokenDetDto();
		obj.setGstin("11GSTID1234A223");
		obj.setMobileNo("7057443993");
		obj.setEmail("virat.k@gmail.com");
		obj.setStatus("A");
		obj.setAction(true);

		respArr.add(respDto);
		respArr.add(obj);

		JsonObject resp1 = new JsonObject();
		JsonElement respBody = gson.toJsonTree(respArr);
		resp1.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp1.add("resp", respBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);

	}

	@PostMapping(value = "/ui/getAuthTokenDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAuthTokDetails(
			@RequestBody String jsonReq) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin getAuthTokenDetails Controller ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject jsonReqObj = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Long entityId = jsonReqObj.get("entityId").getAsLong();
			  List<Long> list = new ArrayList<>();
		        list.add(entityId);
			//List<String> gstins = authTokenParamValidator(entityId);
			Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getInwardSecurityAttributeMap();
			dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(list,
							inwardSecurityAttributeMap);
			List<String> dataSecGstinList = dataSecurityAttrMap.get("GSTIN");
			
			List<ManageAuthTokenDetDto> authDetaResp = authDetailService
					.getAuthDetailsForGstins(dataSecGstinList);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(authDetaResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	@PostMapping(value = "/ui/getAuthTokenDetailsForGroup", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAuthTokenDetails(
			@RequestBody String jsonReq) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin getAuthTokenDetails Controller ,Parsing Input request";
				LOGGER.debug(msg);
			}

			List<String> gstins = null;
			List<ManageAuthTokenDetDto> authDetaResp = null;
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			ManageAuthTokenDetReqDto reqDto = gson.fromJson(
					requestObject.getAsJsonObject("req"),
					ManageAuthTokenDetReqDto.class);

			if (reqDto.getGstin() != null) {
				gstins = reqDto.getGstin();
			}
			List<String> entityIdList = reqDto.getEntityIds();

			List<Long> list = entityIdList.stream().map(Long::valueOf)
					.collect(Collectors.toList());

			/*if (gstins == null || gstins.isEmpty()) {
				gstins = gstnDetailRepository.findgstinByEntityIds(List);
			}*/
		
			if (gstins == null || gstins.isEmpty()) {
				List<String> gstinList = new ArrayList<>();
				for (Long entityId : list) {
					Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
					Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					dataSecurityAttrMap = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
									inwardSecurityAttributeMap);
					List<String> dataSecGstinList = dataSecurityAttrMap.get("GSTIN");
					gstinList.addAll(dataSecGstinList);
				}
				
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Begin getAuthTokenDetails "
							+ "Controller dataSecGstinList %s ", 
							gstinList);
					LOGGER.debug(msg);
				}
				
				authDetaResp = authDetailService
						.getAuthDetailsForGstins(gstinList);	
			}else{
				authDetaResp = authDetailService
						.getAuthDetailsForGstins(gstins);
			}
			
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(authDetaResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	public List<String> authTokenParamValidator(Long entityID) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin ManageAuthTokenValidation";
			LOGGER.debug(msg);
		}

		String msg = null;
		List<String> gstins = null;

		if (entityID == null) {
			msg = "EntityId Can not be empty";
			throw new AppException(msg);
		} else {
			gstins = gstnDetailRepository.findgstinByEntityId(entityID);
			if (gstins.isEmpty() || gstins == null) {
				msg = "No Corresponding Gstins for EntityID";
				throw new AppException(msg);
			}
		}
		return gstins;
	}
	@PostMapping(value = "/ui/getAllGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllGstins(
			@RequestBody String reqJson) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Start of ManageAuthTokenDetController.getAllGstins,"
					+ "request provided {}",
					reqJson);
		}
		try {
			JsonObject requestObject = JsonParser.parseString(reqJson)
					.getAsJsonObject();
			
			ManageAuthTokenDetReqDto reqDto = gson.fromJson(
					requestObject.getAsJsonObject("req"),
					ManageAuthTokenDetReqDto.class);
			
			List<String> entityIdList = reqDto.getEntityIds();

			List<Long> list = entityIdList.stream().map(Long::valueOf)
					.collect(Collectors.toList());
			//List<String> gstinList = null;
			
			List<String> gstinList = new ArrayList<>();
			for (Long entityId : list) {
				Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
				Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				dataSecurityAttrMap = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
								inwardSecurityAttributeMap);
				List<String> dataSecGstinList = dataSecurityAttrMap.get("GSTIN");
				gstinList.addAll(dataSecGstinList);
			}
		/*	if (gstinList == null || gstinList.isEmpty()) {
				gstinList = gstnDetailRepository.findgstinByEntityIds(List);
			}*/

			if (CollectionUtils.isEmpty(gstinList))
				throw new AppException("User Does not have any gstin");

			List<GstinReqTypDto> respList = new ArrayList<>();
			List<GSTNDetailEntity> gstinEntity = gstnDetailRepository
					.findRegTypeByGstinList(gstinList);
			Collections.sort(gstinEntity, (a, b) -> a.getGstin()
					.compareToIgnoreCase(b.getGstin()));
			for (GSTNDetailEntity entity : gstinEntity) {
				GstinReqTypDto dto = null;
				if (entity.getRegistrationType().equalsIgnoreCase("ISD")){
					dto = new GstinReqTypDto(entity.getGstin(), "ISD");
				}else if(entity.getRegistrationType().equalsIgnoreCase("REGULAR")){
					dto = new GstinReqTypDto(entity.getGstin(), "REGULAR");
				}else if(entity.getRegistrationType().equalsIgnoreCase("SEZ")){
					dto = new GstinReqTypDto(entity.getGstin(), "SEZ");
				}else if(entity.getRegistrationType().equalsIgnoreCase("SEZU")){
					dto = new GstinReqTypDto(entity.getGstin(), "SEZU");
				}else if(entity.getRegistrationType().equalsIgnoreCase("SEZD")){
					dto = new GstinReqTypDto(entity.getGstin(), "SEZD");
				}else if(entity.getRegistrationType().equalsIgnoreCase("TDS")){
					dto = new GstinReqTypDto(entity.getGstin(), "TDS");
				}else if(entity.getRegistrationType().equalsIgnoreCase("TCS")){
					dto = new GstinReqTypDto(entity.getGstin(), "TCS");
				}
			
				respList.add(dto);
			}

			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			gstinResp.add("gstinInfo", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End ManageAuthTokenDetController"
						+ ".getAllGstins, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(
					"Exception on ManageAuthTokenDetController ",
					e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(e.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
