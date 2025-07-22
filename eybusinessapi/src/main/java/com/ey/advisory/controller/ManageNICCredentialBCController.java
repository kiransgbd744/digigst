package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.service.bc.api.CopyNICCredentialDto;
import com.ey.advisory.app.service.bc.api.EINVNICUserService;
import com.ey.advisory.app.service.bc.api.EWBNICUserService;
import com.ey.advisory.app.service.bc.api.NICCredentialDto;
import com.ey.advisory.app.service.bc.api.NICUserService;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ManageNICCredentialBCController {

	@Autowired
	@Qualifier("EWBNICUserServiceImpl")
	private EWBNICUserService ewbNICUserService;

	@Autowired
	@Qualifier("EINVNICUserServiceImpl")
	private EINVNICUserService enivNICUserService;

	@Autowired
	@Qualifier("NICUserService")
	private NICUserService nicUserService;

	@PostMapping(value = "/updateNICUsersBCAPI")
	public ResponseEntity<String> saveB2CQROnboardingParamsBCAPI(
			@RequestBody String jsonString, HttpServletResponse response)
			throws Exception {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = new JsonObject();
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
					einvnicUser.setClientId(user.getEinvClientId());
					einvnicUser.setClientSecret(user.getEinvClientSecret());
					einvnicUser.setUpdatedBy("SYSTEM");
					einvnicUser.setIdentifier("EINV");
					enivNICUserService.updateEINVNICUser(einvnicUser);
				}
				if (user.getEinvUserNameIRP() != null
						&& user.getEinvPasswordIRP() != null) {
					EINVNICUser einvnicUserIRP = new EINVNICUser();
					einvnicUserIRP.setGstin(user.getGstin());
					einvnicUserIRP.setNicUserName(user.getEinvUserNameIRP());
					einvnicUserIRP.setNicPassword(user.getEinvPasswordIRP());
					einvnicUserIRP.setClientId(user.getEinvClientIdIRP());
					einvnicUserIRP
							.setClientSecret(user.getEinvClientSecretIRP());
					einvnicUserIRP.setIdentifier("EYEINV");
					einvnicUserIRP.setUpdatedBy("SYSTEM");
					enivNICUserService.updateEINVNICUser(einvnicUserIRP);
				}
				if (user.getEwbUserName() != null
						&& user.getEwbPassword() != null) {
					EWBNICUser ewbnicUser = new EWBNICUser();
					ewbnicUser.setGstin(user.getGstin());
					ewbnicUser.setNicUserName(user.getEwbUserName());
					ewbnicUser.setNicPassword(user.getEwbPassword());
					ewbnicUser.setClientId(user.getEwbClientId());
					ewbnicUser.setClientSecret(user.getEwbClientSecret());
					ewbnicUser.setUpdatedBy("SYSTEM");
					ewbNICUserService.updateEWBNICUser(ewbnicUser);
				}

			});

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.addProperty("resp", "Saved Successfully");
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Updating the NICUserDetails"
									+ " to the server" + ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/copyNICUsers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> copyNICUsers(@RequestBody String jsonString,
			HttpServletRequest request) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = request.getAttribute("loggedInUser") != null
				? (String) request.getAttribute("loggedInUser")
				: "SYSTEM";
		try {
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			CopyNICCredentialDto copyDto = gson.fromJson(reqJson,
					CopyNICCredentialDto.class);

			String copyNICFlag = copyDto.getCopyNICFlag();

			if (copyNICFlag.equalsIgnoreCase("E-Invoice to E-WayBill")) {

				copyDto.getNicDetails().forEach(user -> {

					EWBNICUser ewbnicUser = new EWBNICUser();
					ewbnicUser.setGstin(user.getGstin());
					ewbnicUser.setNicUserName(
							user != null ? user.getEinvUserName() : null);
					ewbnicUser.setNicPassword(
							user != null ? user.getEinvPassword() : null);
					ewbnicUser.setClientId(
							user != null ? user.getEinvClientId() : null);
					ewbnicUser.setClientSecret(
							user != null ? user.getEinvClientSecret() : null);
					ewbnicUser.setUpdatedBy(userName);
					ewbNICUserService.updateEWBNICUser(ewbnicUser);

				});

			} else if (copyNICFlag.equalsIgnoreCase("E-WayBill to E-Invoice")) {

				copyDto.getNicDetails().forEach(user -> {

					EINVNICUser einvnicUser = new EINVNICUser();
					einvnicUser.setGstin(user.getGstin());
					einvnicUser.setNicUserName(
							user != null ? user.getEwbUserName() : null);
					einvnicUser.setNicPassword(
							user != null ? user.getEwbPassword() : null);
					einvnicUser.setClientId(
							user != null ? user.getEwbClientId() : null);
					einvnicUser.setClientSecret(
							user != null ? user.getEwbClientSecret() : null);
					einvnicUser.setUpdatedBy(userName);
					einvnicUser.setIdentifier("EINV");
					enivNICUserService.updateEINVNICUser(einvnicUser);

				});

			} else if (copyNICFlag
					.equalsIgnoreCase("E-Invoice to E-WayBill_IRP")) {

				copyDto.getNicDetails().forEach(user -> {

					EWBNICUser ewbnicUser = new EWBNICUser();
					ewbnicUser.setGstin(user.getGstin());
					ewbnicUser.setNicUserName(
							user != null ? user.getEinvUserNameIRP() : null);
					ewbnicUser.setNicPassword(
							user != null ? user.getEinvPasswordIRP() : null);
					ewbnicUser.setClientId(
							user != null ? user.getEinvClientIdIRP() : null);
					ewbnicUser.setClientSecret(
							user != null ? user.getEinvClientSecretIRP()
									: null);
					ewbnicUser.setUpdatedBy(userName);
					ewbNICUserService.updateEWBNICUser(ewbnicUser);

				});

			} else if (copyNICFlag
					.equalsIgnoreCase("E-WayBill to E-Invoice_IRP")) {

				copyDto.getNicDetails().forEach(user -> {

					EINVNICUser einvnicUser = new EINVNICUser();
					einvnicUser.setGstin(user.getGstin());
					einvnicUser.setNicUserName(
							user != null ? user.getEwbUserName() : null);
					einvnicUser.setNicPassword(
							user != null ? user.getEwbPassword() : null);
					einvnicUser.setClientId(
							user != null ? user.getEwbClientId() : null);
					einvnicUser.setClientSecret(
							user != null ? user.getEwbClientSecret() : null);
					einvnicUser.setUpdatedBy(userName);
					einvnicUser.setIdentifier("EYEINV");
					enivNICUserService.updateEINVNICUser(einvnicUser);

				});

			}

			String groupCode = TenantContext.getTenantId();
			String savStatus = "Data Saved Successfully.";

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.addProperty("resp", savStatus);
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

}
