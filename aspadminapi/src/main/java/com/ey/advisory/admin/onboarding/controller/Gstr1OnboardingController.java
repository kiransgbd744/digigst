package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

import com.ey.advisory.admin.data.entities.client.FunctionalityMasterEntity;
import com.ey.advisory.admin.data.repositories.client.ELEntitlementRepository;
import com.ey.advisory.admin.data.repositories.client.FunctionalityMasterRepository;
import com.ey.advisory.admin.services.onboarding.AppPermissionService;
import com.ey.advisory.admin.services.onboarding.DataSecurityService;
import com.ey.advisory.admin.services.onboarding.ELEntitlementService;
import com.ey.advisory.admin.services.onboarding.ELRegistrationService;
import com.ey.advisory.admin.services.onboarding.OrganizationService;
import com.ey.advisory.admin.services.onboarding.UserCreateService;
import com.ey.advisory.app.services.gen.DefaultClientGroupService;
import com.ey.advisory.app.services.gen.EntityDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.AppPermissionReqDto;
import com.ey.advisory.core.dto.AppPermissionRespDto;
import com.ey.advisory.core.dto.AppProfileResp;
import com.ey.advisory.core.dto.DataPermissionRespDto;
import com.ey.advisory.core.dto.DataSecurityReqDto;
import com.ey.advisory.core.dto.DataSecurityRespDto;
import com.ey.advisory.core.dto.ELEntitlementDto;
import com.ey.advisory.core.dto.ELExtractRegDto;
import com.ey.advisory.core.dto.ElEntitlementHistoryReqDto;
import com.ey.advisory.core.dto.ElEntitlementReqDto;
import com.ey.advisory.core.dto.ElRegistrationReqDto;
import com.ey.advisory.core.dto.FunctionalityMasterDto;
import com.ey.advisory.core.dto.Messages;
import com.ey.advisory.core.dto.OrganizationDataResDto;
import com.ey.advisory.core.dto.OrganizationReqDto;
import com.ey.advisory.core.dto.OrganizationResDto;
import com.ey.advisory.core.dto.UserCreationDto;
import com.ey.advisory.core.dto.UserCreationReqDto;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Umesha.M
 *
 */
@RestController
public class Gstr1OnboardingController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1OnboardingController.class);

	private static final String NO_RECORD_FOUND = "No Record Found";

	private static final String RESP = "resp";

	private static final String HRD = "hrd";

	private static final String EXCEPTION_OCCUR = "Exception Occure: ";

	private static final String UPDATED_SUCCESSFULLY = "Updated Successfully";

	private static final String DELETED_SUCCESSFULLY = "Deleted Succefully";

	private static final String REQ = "req";

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private DefaultClientGroupService defaultClientGroupService;

	@Autowired
	@Qualifier("eLRegistrationServiceImpl")
	private ELRegistrationService eLRegistrationService;

	@Autowired
	@Qualifier("elEntitlementService")
	private ELEntitlementService elEntitlementService;

	@Autowired
	@Qualifier("userCreateService")
	private UserCreateService userCreateService;

	@Autowired
	@Qualifier("OrganizationServiceImpl")
	private OrganizationService organizationService;

	@Autowired
	@Qualifier("dataSecurityService")
	private DataSecurityService dataSecurityService;

	@Autowired
	@Qualifier("appPermissionService")
	private AppPermissionService appPermissionService;

	@Autowired
	@Qualifier("functionalityMasterRepository")
	private FunctionalityMasterRepository functionalityMasterRepository;

	@Autowired
	@Qualifier("elEntitlementRepository")
	private ELEntitlementRepository elEntitlementRepository;

	/**
	 * 
	 * @return
	 */
	@PostMapping(value = "/entities", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEntities() {
		LOGGER.error("Gstr1OnboardingController getEntities Begin");

		try {
			String groupCode = TenantContext.getTenantId();
			List<EntityDto> entitiesForGroup = defaultClientGroupService
					.getEntitiesForGroup(groupCode);
			Gson gson = new Gson();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(entitiesForGroup);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching entities";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param jsonReq
	 * @param req
	 * @return
	 */
	@PostMapping(value = "/getElRegistration.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getElRegistrationDetails(
			@RequestBody String jsonReq, HttpServletRequest req) {
		JsonObject resp = new JsonObject();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject().get(REQ).getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			ElRegistrationReqDto dto = gson.fromJson(requestObject,
					ElRegistrationReqDto.class);
			List<ELExtractRegDto> elExtractRegDtos = eLRegistrationService
					.getRegistrationDetails(dto);
			if (!elExtractRegDtos.isEmpty()) {
				JsonElement resBody = gson.toJsonTree(elExtractRegDtos);
				resp.add(HRD, gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add(RESP, resBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage());
			}
			resp.add(HRD, new Gson().toJsonTree(new APIRespDto(
					APIRespDto.getErrorStatus(), e.getMessage())));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"end of ELRegistrationServiceImpl.getRegistrationDetails "
							+ "{}",
					resp.toString());
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
	}

	/**
	 * @param jsonReq
	 * @return
	 */
	@PostMapping(value = "/updateElRegistration.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateGstnDetailsInfo(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonArray jsonObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<ElRegistrationReqDto>>() {
			}.getType();
			List<ElRegistrationReqDto> updateGstinInfoDetails = gson
					.fromJson(jsonObject, listType);
			String finalMsg = eLRegistrationService
					.updateGstnInfoDetails(updateGstinInfoDetails);
			if (finalMsg != null && !finalMsg.trim().isEmpty()) {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), finalMsg)));
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), UPDATED_SUCCESSFULLY)));
			}
		} catch (Exception e) {
			resp.add(HRD, new Gson().toJsonTree(
					new APIRespDto(APIRespDto.ERROR_STATUS, e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * @param jsonReq
	 * @return
	 */
	@PostMapping(value = "/deleteElRegistration.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteGstnDetalsInfo(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<ElRegistrationReqDto>>() {
			}.getType();
			List<ElRegistrationReqDto> elRegistrationReqDtos = gson
					.fromJson(requestObject, listType);
			eLRegistrationService.deleteGstnInfoDetails(elRegistrationReqDtos);
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD,
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
	}

	/**
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/getLatestElEntitlement.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getLatestElEntitlementDetails(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			ElEntitlementReqDto dto = gson.fromJson(requestObject,
					ElEntitlementReqDto.class);
			List<ELEntitlementDto> elEntitlementDtos = elEntitlementService
					.getLatestElEntDetails(dto);
			if (!elEntitlementDtos.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(elEntitlementDtos);
				resp.add(HRD, gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add(RESP, respBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD,
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * @param jsonReq
	 * @return
	 */
	@PostMapping(value = "/updateLatestEleEntitlement.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateLatestelEntDetails(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonArray = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<ElEntitlementReqDto>>() {
			}.getType();
			List<ElEntitlementReqDto> elEntitlementReqDtos = gson
					.fromJson(jsonArray, listType);
			Messages errorMsgs = elEntitlementService
					.updateLatestElEntDetails(elEntitlementReqDtos);

			if (errorMsgs.getMessages() != null
					&& !errorMsgs.getMessages().isEmpty()) {
				JsonElement jsonBody = gson.toJsonTree(errorMsgs);
				resp.add(RESP, jsonBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), UPDATED_SUCCESSFULLY)));
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD,
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This is method Get a El Entitlement History
	 * 
	 * @param jsonReq
	 * @return
	 */
	@PostMapping(value = "/getEleEntitlementHistory.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEleEntitlementHistory(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject jsonReqObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonObject();

			ElEntitlementHistoryReqDto elEntitlementHistoryReqDto = gson
					.fromJson(jsonReqObject, ElEntitlementHistoryReqDto.class);
			List<ELEntitlementDto> lElEntitleRespDtos = elEntitlementService
					.getEleEntitlementHistory(elEntitlementHistoryReqDto);
			if (!lElEntitleRespDtos.isEmpty()) {
				JsonElement jsonBody = gson.toJsonTree(lElEntitleRespDtos);
				resp.add(RESP, jsonBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD,
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This is Get UserAPI getting User Details
	 * 
	 * @param jsonString
	 * @return
	 */
	@PostMapping(value = "/getUserInfo.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUserInfo(@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject jsonReqObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			UserCreationReqDto userCreaReqDto = gson.fromJson(jsonReqObject,
					UserCreationReqDto.class);
			List<UserCreationDto> userCreationDtos = userCreateService
					.findUserDetails(userCreaReqDto);
			if (!userCreationDtos.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(userCreationDtos);
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
	 * This method Update User creation to User Details
	 * 
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/updateUserCreation.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateUserCreation(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<UserCreationReqDto>>() {
			}.getType();
			List<UserCreationReqDto> userCreationReqDtos = gson
					.fromJson(jsonReqObject, listType);

			for (UserCreationReqDto userCreationReqDto : userCreationReqDtos) {

				userCreationReqDto.setUserName(
						!Strings.isNullOrEmpty(userCreationReqDto.getUserName())
								? userCreationReqDto.getUserName().trim()
								: userCreationReqDto.getUserName());
				userCreationReqDto.setEmail(
						!Strings.isNullOrEmpty(userCreationReqDto.getEmail())
								? userCreationReqDto.getEmail().trim()
								: userCreationReqDto.getEmail());

			}
			String msg = userCreateService
					.updateUserCreation(userCreationReqDtos);
			resp.add(RESP, gson.toJsonTree(
					new APIRespDto(APIRespDto.getSuccessStatus(), msg)));
			LOGGER.error("Gstr1OnboardingController updateUserCreation End");
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This method delete user creation
	 * 
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/deleteUserCreation.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteUserCreation(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type typeList = new TypeToken<List<UserCreationReqDto>>() {
			}.getType();
			List<UserCreationReqDto> userCreationReqDtos = gson
					.fromJson(jsonReqObject, typeList);
			userCreateService.deleteUserCreation(userCreationReqDtos);
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD,
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This method Get Organization from Config table
	 * 
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/getOrganization.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOrganization(@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			OrganizationReqDto organizationReqDto = gson.fromJson(reqObject,
					OrganizationReqDto.class);
			List<OrganizationResDto> organizationResDtos = organizationService
					.getOrganization(organizationReqDto);
			if (!organizationResDtos.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(organizationResDtos);
				resp.add(RESP, respBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, new Gson().toJsonTree(new APIRespDto(
					APIRespDto.getErrorStatus(), e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This method Update Organization
	 * 
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/updateOrganization.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateOrganization(
			@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonArray jsonReqObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<OrganizationReqDto>>() {
			}.getType();
			List<OrganizationReqDto> organizationReqDtos = gson
					.fromJson(jsonReqObject, listType);
			organizationService.updateOrganization(organizationReqDtos);
			resp.add(HRD, gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getOrganizationData.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOrganizationData(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqJsonObj = (new JsonParser().parse(jsonReq))
					.getAsJsonObject().get(REQ).getAsJsonObject();
			OrganizationReqDto organizationReqDto = gson.fromJson(reqJsonObj,
					OrganizationReqDto.class);
			List<OrganizationDataResDto> organizationDataResDtos = organizationService
					.getOrganizationData(organizationReqDto);
			if (organizationDataResDtos != null) {
				JsonElement jsonBody = gson.toJsonTree(organizationDataResDtos);
				resp.add(RESP, jsonBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/addAttributes.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> addAttributes(@RequestBody String reqJson) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonArray jsonReqObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type listType = new TypeToken<List<OrganizationReqDto>>() {
			}.getType();
			List<OrganizationReqDto> organizationReqDtos = gson
					.fromJson(jsonReqObject, listType);
			String msg = organizationService
					.addOrganizationData(organizationReqDtos);
			resp.add(HRD, gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add(RESP, gson.toJsonTree(
					new APIRespDto(APIRespDto.getSuccessStatus(), msg)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteAttributes.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteAttributes(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObj = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonArray();
			Type list = new TypeToken<List<OrganizationReqDto>>() {
			}.getType();
			List<OrganizationReqDto> organizationReqDtos = gson
					.fromJson(jsonReqObj, list);
			String msg = organizationService
					.deleteOrganizationData(organizationReqDtos);
			resp.add(RESP, gson.toJsonTree(
					new APIRespDto(APIRespDto.getSuccessStatus(), msg)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This method uses Get Data Security based on the Data Security
	 * 
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/getDataSecurity.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDataSecurity(@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqJsonObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			DataSecurityReqDto dataSecurityReqDto = gson.fromJson(reqJsonObject,
					DataSecurityReqDto.class);
			List<DataSecurityRespDto> dataSecurityRespDtos = dataSecurityService
					.getDataSecurity(dataSecurityReqDto);
			if (!dataSecurityRespDtos.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(dataSecurityRespDtos);
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
	 * This method Get Data Permission based on Data permission request
	 * 
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/getDataPermission.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDataPermission(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqJsonObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			DataSecurityReqDto dataSecurityReqDto = gson.fromJson(reqJsonObject,
					DataSecurityReqDto.class);
			List<DataPermissionRespDto> dataPermissionRespDtos = dataSecurityService
					.getDataPermission(dataSecurityReqDto);
			if (!dataPermissionRespDtos.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(dataPermissionRespDtos);
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
	 * This method Update the Data Security table based on Data Permission
	 * Request
	 * 
	 * @param reqJson
	 * @return
	 */
	@PostMapping(value = "/updateDataPermission.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateDataPermission(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqJsonObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonObject();
			String loggedInUser = null;
			DataSecurityReqDto dataSecurityReqDto = gson.fromJson(reqJsonObject,
					DataSecurityReqDto.class);
			dataSecurityService.updateDataPermission(dataSecurityReqDto,
					loggedInUser);
			resp.add(RESP, new Gson().toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD, new Gson().toJsonTree(new APIRespDto(
					APIRespDto.getErrorStatus(), e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * This method Get All Data from DB Based on App Permission Request
	 * 
	 * @return
	 */
	@PostMapping(value = "/getAppPermission.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAppPermission(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqJsonObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get(REQ).getAsJsonObject();
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
	@PostMapping(value = "/updateAppPermission.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateAppPermission(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonReqObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get(REQ).getAsJsonArray();
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

	/**
	 * Get all functionality Description and Code
	 * 
	 * @return
	 */
	@PostMapping(value = "/getFunctionalityDesc.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getFunctionalityDesc() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<FunctionalityMasterEntity> functMasterEntities = functionalityMasterRepository
					.getFunctionalityMaster();
			if (!functMasterEntities.isEmpty()) {
				List<FunctionalityMasterDto> functionalityMasterDtos = new ArrayList<>();
				functMasterEntities.forEach(functMasterEntity -> {
					FunctionalityMasterDto functMasterDto = new FunctionalityMasterDto();
					functMasterDto
							.setFunctCode(functMasterEntity.getFunctCode());
					functMasterDto
							.setFunctDesc(functMasterEntity.getFunctDesc());
					functionalityMasterDtos.add(functMasterDto);
				});
				JsonElement jsonBody = gson.toJsonTree(functionalityMasterDtos);
				resp.add(RESP, jsonBody);
			} else {
				resp.add(HRD, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD,
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getProfilePermission.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProfilePermission() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			List<AppProfileResp> resps = appPermissionService
					.getProfilePermission();
			if (resps != null) {
				JsonElement jsonBody = gson.toJsonTree(resps);
				resp.add(RESP, jsonBody);
			} else {
				resp.add(HRD, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			LOGGER.error(EXCEPTION_OCCUR, e);
			resp.add(HRD,
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
