package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.services.onboarding.AppPermissionService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.AppPermissionReqDto;
import com.ey.advisory.core.dto.AppPermissionRespDto;
import com.ey.advisory.core.dto.UserCreationReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Sakshi.jain
 *
 */
@RestController
public class GroupLevelOnboardingController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupLevelOnboardingController.class);

	private static final String NO_RECORD_FOUND = "No Record Found";

	private static final String RESP = "resp";

	private static final String HRD = "hrd";

	private static final String EXCEPTION_OCCUR = "Exception Occure: ";

	private static final String UPDATED_SUCCESSFULLY = "Updated Successfully";

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userInfoRepo;

	@Autowired
	@Qualifier("GroupLevelAppPermissionServiceImpl")
	private AppPermissionService appPermissionService;


	/**
	 * This is Get UserAPI getting Active User Details
	 * 
	 * @param jsonString
	 * @return
	 */
	@PostMapping(value = "/getGroupUserInfo.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUserInfo(@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject jsonReqObject = JsonParser.parseString(reqJson)
					.getAsJsonObject().get("req").getAsJsonObject();
			UserCreationReqDto userCreaReqDto = gson.fromJson(jsonReqObject,
					UserCreationReqDto.class);
			List<UserCreationEntity> userInfoEntity = userInfoRepo
					.getActiveUsers();

			/*
			 * List<UserCreationDto> userCreationDtos = userCreateService
			 * .findUserDetails(userCreaReqDto);
			 */

			if (!userInfoEntity.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(userInfoEntity);
				resp.add(HRD, gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add(RESP, respBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add(HRD, new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);
			resp.add(HRD, new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method Get All Data from DB Based on App Permission Request
	 * 
	 * @return
	 */
	@PostMapping(value = "/getGroupLevelAppPermission.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAppPermission(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqJsonObject = JsonParser.parseString(reqJson)
					.getAsJsonObject().get("req").getAsJsonObject();
			AppPermissionReqDto apPermReqDto = gson.fromJson(reqJsonObject,
					AppPermissionReqDto.class);
			List<AppPermissionRespDto> appPermRespDtos = appPermissionService
					.getPermissions(apPermReqDto);

			appPermRespDtos.sort(
					Comparator.comparing(AppPermissionRespDto::getPermCode));

			if (!appPermRespDtos.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(appPermRespDtos);
				resp.add(RESP, respBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This method uses Update App Permission User
	 * 
	 * @param jsonReq
	 * @return
	 */
	@PostMapping(value = "/updateGroupLevelAppPermission.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateAppPermission(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<AppPermissionReqDto>>() {
			}.getType();
			List<AppPermissionReqDto> appPermissionReqDtos = gson
					.fromJson(jsonReqObject, listType);

			appPermissionService.updatePermissions(appPermissionReqDtos);
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
