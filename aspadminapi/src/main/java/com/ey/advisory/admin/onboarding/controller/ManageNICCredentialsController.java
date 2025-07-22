package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.bcadmin.common.dto.NICCredentialDto;
import com.ey.advisory.bcadmin.common.service.EINVNICUserService;
import com.ey.advisory.bcadmin.common.service.EWBNICUserService;
import com.ey.advisory.bcadmin.common.service.NICUserService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstindto;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ManageNICCredentialsController {

	@Autowired
	@Qualifier("BcAdminEWBNICUserServiceImpl")
	private EWBNICUserService ewbNICUserService;

	@Autowired
	@Qualifier("BcAdminEINVNICUserServiceImpl")
	private EINVNICUserService enivNICUserService;

	@Autowired
	@Qualifier("BcAdminNICUserService")
	private NICUserService nicUserService;

	@PostMapping(value = "/getNICUsers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNICUsers(@RequestBody String header) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqObject = (new JsonParser()).parse(header)
					.getAsJsonObject();
			JsonObject hdrObject = reqObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();
			List<EINVNICUser> einvNICusersList = enivNICUserService
					.getAllEINVNICUsers();
			List<EWBNICUser> ewbNICUsersList = ewbNICUserService
					.getAllEWBNICUsers();
			List<NICCredentialDto> nicCredentialsList = nicUserService
					.getNICCredentials(einvNICusersList, ewbNICUsersList);
			nicCredentialsList
					.sort(Comparator.comparing(NICCredentialDto::getGstin));
			int totalCount = nicCredentialsList.size();
			List<List<NICCredentialDto>> requiredList = Lists
					.partition(nicCredentialsList, pageSize);
			int pages = 0;
			if (totalCount % pageSize == 0) {
				pages = totalCount / pageSize;
			} else {
				pages = totalCount / pageSize + 1;
			}

			String jsonEINV = null;
			if (pageNum <= pages) {
				jsonEINV = gson.toJson(requiredList.get(pageNum));
			} else {
				throw new AppException("Invalid Page Number");
			}
			JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("nicDetails", einvJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNum, pageSize, "S", "Successfully fetched NIC Credentials")));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Processing the getUserDetails request "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/updateNICUsers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateNICUsers(@RequestBody String jsonString,
			HttpServletRequest request) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = request.getAttribute("loggedInUser") != null
				? (String) request.getAttribute("loggedInUser") : "SYSTEM";
		try {
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			String nicString = reqJson.get("nicDetails").toString();
			Type nicStringList = new TypeToken<ArrayList<NICCredentialDto>>() {
			}.getType();
			ArrayList<NICCredentialDto> nicUserList = new Gson()
					.fromJson(nicString, nicStringList);
			nicUserList.forEach(user -> {
				if (user.getEinvUserName() != null
						&& user.getEinvPassword() != null) {
					EINVNICUser einvnicUser = new EINVNICUser();
					einvnicUser.setGstin(user.getGstin());
					einvnicUser.setNicUserName(user.getEinvUserName());
					einvnicUser.setNicPassword(user.getEinvPassword());
					einvnicUser.setUpdatedBy(userName);
					enivNICUserService.updateEINVNICUser(einvnicUser);
				}
				if (user.getEwbUserName() != null
						&& user.getEwbPassword() != null) {
					EWBNICUser ewbnicUser = new EWBNICUser();
					ewbnicUser.setGstin(user.getGstin());
					ewbnicUser.setNicUserName(user.getEwbUserName());
					ewbnicUser.setNicPassword(user.getEwbPassword());
					ewbnicUser.setUpdatedBy(userName);
					ewbNICUserService.updateEWBNICUser(ewbnicUser);
				}

			});

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Updating the NICUserDetails "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/getEinvUsers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEINVGstin() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<Gstindto> einvGstinList = enivNICUserService
					.getDistinctEINVGstin();
			String jsonEINV = gson.toJson(einvGstinList);
			JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("einvGstin", einvJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree("Exception while fetching the EINVGstin "
							+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@GetMapping(value = "/getEwbUsers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEWBGstin() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<Gstindto> einvGstinList = ewbNICUserService
					.getDistinctEWBGstin();
			String jsonEINV = gson.toJson(einvGstinList);
			JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("ewbGstin", einvJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree("Exception while fetching the EWBGstin "
							+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
