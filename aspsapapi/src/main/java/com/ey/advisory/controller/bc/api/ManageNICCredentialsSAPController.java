package com.ey.advisory.controller.bc.api;

/**
 * @author vishal.verma
 *
 */

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.service.bc.api.CopyNICCredentialDto;
import com.ey.advisory.app.service.bc.api.EINVNICUserService;
import com.ey.advisory.app.service.bc.api.EWBNICUserService;
import com.ey.advisory.app.service.bc.api.NICCredentialDto;
import com.ey.advisory.app.service.bc.api.NICUserService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstindto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.domain.client.EINVNICUser;
import com.ey.advisory.gstnapi.domain.client.EWBNICUser;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ManageNICCredentialsSAPController {

	@Autowired
	@Qualifier("EWBNICUserServiceImpl")
	private EWBNICUserService ewbNICUserService;

	@Autowired
	@Qualifier("EINVNICUserServiceImpl")
	private EINVNICUserService enivNICUserService;

	@Autowired
	@Qualifier("NICUserService")
	private NICUserService nicUserService;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	Map<String, String> authTokenStatusMap = null;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@PostMapping(value = "/ui/getNICUsers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNICUsers(@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		Map<String, Config> configMap = configManager.getConfigs("EINVSource",
				"einv.source.enabled", TenantContext.getTenantId());

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqJson = requestObject.getAsJsonObject("req");

			Long entityId = reqJson.get("entityId").getAsLong();

			List<String> gstiNsForEntity = gSTNDetailRepository
					.findgstinByEntityId(entityId);

			Map<String, String> stateNamesMap = entityService
					.getStateNames(gstiNsForEntity);

			Map<String, String> regType = entityService
					.getRegType(gstiNsForEntity);

			authTokenStatusMap = authTokenService
					.getAuthTokenStatusForGstins(gstiNsForEntity);

			List<EINVNICUser> einvNICusersList = enivNICUserService
					.getAllEINVNICUsers(gstiNsForEntity);
			List<EWBNICUser> ewbNICUsersList = ewbNICUserService
					.getAllEWBNICUsers(gstiNsForEntity);
			List<NICCredentialDto> nicCredentialsList = nicUserService
					.getNICCredentials(einvNICusersList, ewbNICUsersList,
							authTokenStatusMap, stateNamesMap, gstiNsForEntity,
							configMap, regType);
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
							pageNum, pageSize, "S",
							"Successfully fetched NIC Credentials")));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(
					"Exception while Processing the getUserDetails request "
							+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/updateNICUsers", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateNICUsers(@RequestBody String jsonString,
			HttpServletRequest request) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = request.getAttribute("loggedInUser") != null
				? (String) request.getAttribute("loggedInUser")
				: "SYSTEM";
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
					einvnicUser.setUpdatedBy(userName);
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
					einvnicUserIRP.setUpdatedBy(userName);
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
					ewbnicUser.setUpdatedBy(userName);
					ewbNICUserService.updateEWBNICUser(ewbnicUser);
				}

			});

			String groupCode = TenantContext.getTenantId();
			String savStatus = "Data Saved Successfully on Cloud";

			boolean isEligibleforBcAPIPush = bcAPIPushReq(groupCode);
			if (isEligibleforBcAPIPush) {

				String bcAPIPushFrankfurtStatus = callSaveOnBoardingBCAPI(
						reqJson, groupCode,
						"bcapi.update.nic.user.url.frankfurt");
				if (bcAPIPushFrankfurtStatus.equalsIgnoreCase("Success")) {
					savStatus = "Data Saved Successfully";
					LOGGER.debug("Records Saved in BC frankfurt", reqJson);
				} else {
					savStatus = "Data Not Saved Successfully";
				}

				/*
				 * String bcAPIPushAmsterdamStatus = callSaveOnBoardingBCAPI(
				 * reqJson, groupCode, "bcapi.update.nic.user.url.amsterdam");
				 * if (bcAPIPushAmsterdamStatus.equalsIgnoreCase("Success")) {
				 * savStatus = "Data Saved Successfully";
				 * LOGGER.debug("Records Saved in BC amsterdam", reqJson); }
				 * else { savStatus = "Data Not Saved Successfully"; }
				 */
			}

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

	@GetMapping(value = "/ui/getEinvUsers", produces = {
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

	@GetMapping(value = "/ui/getEwbUsers", produces = {
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

	@PostMapping(value = "/ui/getNICUserByGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNICUserByGstin(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject hdrObject = reqObject.getAsJsonObject("req");
			String gstin = hdrObject.get("gstin").getAsString();

			EINVNICUser einvNICUser = enivNICUserService
					.getEinvUserByGstin(gstin);

			EWBNICUser ewbNICUser = ewbNICUserService
					.getEwbNicUserByGstin(gstin);
			NICCredentialDto nicCredentialsList = null;
			if (einvNICUser == null && ewbNICUser == null) {
				String errMsg = String
						.format("No Data Found for GSTIN - " + gstin);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			} else {
				nicCredentialsList = nicUserService
						.getNICCredentialByGstin(einvNICUser, ewbNICUser);
			}
			String jsonEINV = gson.toJson(nicCredentialsList);
			JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("nicDetails", einvJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	private boolean bcAPIPushReq(String groupCode) {

		Map<String, Config> configMap = configManager.getConfigs("BCAPI",
				"non.sap.clients", "DEFAULT");

		if (!configMap.containsKey("non.sap.clients")) {
			return false;
		}
		String groupsAvailable = configMap.get("non.sap.clients").getValue();

		return groupsAvailable.contains(groupCode);
	}

	private String callSaveOnBoardingBCAPI(JsonObject reqJson, String groupCode,
			String apiIdentifer) {
		String apiStatus = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			if (!env.containsProperty(apiIdentifer)) {
				String msg = "Bcapi save url is not configured";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			HttpPost httpPost = new HttpPost(env.getProperty(apiIdentifer));
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("groupCode", TenantContext.getTenantId());
			StringEntity entity = new StringEntity(reqJson.toString());
			httpPost.setEntity(entity);

			HttpResponse resp = httpClient.execute(httpPost);

			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("BCAPI Return Response  {}", apiResp);
			}
			if (httpStatusCd == 200) {
				apiStatus = "SUCCESS";
			} else {
				apiStatus = "FAILED";
			}
			return apiStatus;
		} catch (Exception ex) {
			LOGGER.error("ManageNICCredentialsSAPController - "
					+ "Exception while calling BCAPI ", ex);
			return "FAILED";
		}

	}

	@PostMapping(value = "/ui/copyNICUsers", produces = {
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
			String savStatus = "Data Saved Successfully on Cloud";

			boolean isEligibleforBcAPIPush = bcAPIPushReq(groupCode);
			if (isEligibleforBcAPIPush) {

				String bcAPIPushFrankfurtStatus = callSaveOnBoardingBCAPI(
						reqJson, groupCode,
						"bcapi.update.nic.user.url.frankfurt");
				if (bcAPIPushFrankfurtStatus.equalsIgnoreCase("Success")) {
					savStatus = "Data Saved Successfully";
					LOGGER.debug("Records Saved in BC frankfurt", reqJson);
				} else {
					savStatus = "Data Not Saved Successfully";
				}

				/*
				 * String bcAPIPushAmsterdamStatus = callSaveOnBoardingBCAPI(
				 * reqJson, groupCode, "bcapi.update.nic.user.url.amsterdam");
				 * if (bcAPIPushAmsterdamStatus.equalsIgnoreCase("Success")) {
				 * savStatus = "Data Saved Successfully";
				 * LOGGER.debug("Records Saved in BC amsterdam", reqJson); }
				 * else { savStatus = "Data Not Saved Successfully"; }
				 */
			}

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
