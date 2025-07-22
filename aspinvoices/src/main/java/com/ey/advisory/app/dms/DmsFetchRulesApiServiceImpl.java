package com.ey.advisory.app.dms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.DmsRuleMasterEntity;
import com.ey.advisory.admin.data.repositories.client.DmsRuleMasterRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.domain.master.DmsUserDetails;
import com.ey.advisory.core.async.repositories.master.DmsUserDetailsRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.RuleAnsDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ashutosh.kar
 *
 */

@Slf4j
@Component("DmsFetchRulesApiServiceImpl")
public class DmsFetchRulesApiServiceImpl {
	
	public static final String LOGIN_FAILED_MSG = "Error encountered in fetching the rules. " + 
            "Please verify the credentials provided/reach out to central team.";

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private DmsApiCallServiceImpl apiCallService;

	@Autowired
	@Qualifier("DmsLoginApiServiceImpl")
	private DmsLoginApiServiceImpl dmsLoginApiServiceImpl;

	@Autowired
	@Qualifier("DmsUserDetailsRepository")
	private DmsUserDetailsRepository userDetailsMasterRepo;

	@Autowired
	private DmsRuleMasterRepository dmsRuleMasterRepo;

	public String getFetchRules(String groupCode) {
		try {
			Optional<DmsUserDetails> userDetailsOpt = userDetailsMasterRepo.findByGroupCode(groupCode);
			if (!userDetailsOpt.isPresent()) {
				LOGGER.error("GroupCode not found: {}", groupCode);
				throw new AppException();
			}

			DmsUserDetails userDetails = userDetailsOpt.get();
			String cookie = userDetails.getJwtToken();
			LocalDateTime tokenGenTime = userDetails.getTokenGenTime();
			LocalDateTime tokenExpTime = userDetails.getTokenExpTime();

			if (cookie == null || (tokenExpTime != null
					&& Duration.between(tokenGenTime, LocalDateTime.now()).toMinutes() >= 25)) {
				LOGGER.info("JWT token expired or missing. Fetching a new token for groupCode: {}", groupCode);
				String dmsLoginAccess = dmsLoginApiServiceImpl.getDmsLoginAccess(groupCode , null);
				if ("Success".equalsIgnoreCase(dmsLoginAccess)) {
					userDetails = userDetailsMasterRepo.findByGroupCode(groupCode)
							.orElseThrow(() -> new AppException("Failed to retrieve updated JWT token"));
					cookie = userDetails.getJwtToken();
				}
				else if("Login Failed!".equalsIgnoreCase(dmsLoginAccess)){
					LOGGER.error("LogIn Failed with the provided Credential in FetchRules Api{}");
					return LOGIN_FAILED_MSG;
				}
				else {// if failed
					LOGGER.info("DmsFetchRulesApiServiceImpl,Unable to generate cookie in " 
							+ "DMS login api {}", groupCode);
				}
			}

			Gson gson = GsonUtil.newSAPGsonInstance();
			Pair<String, String> authForDmsCall = apiCallService.getAuthForDmsCall(groupCode);
			String accesToken = authForDmsCall.getValue0();
			String apiAccesskey = authForDmsCall.getValue1();//this is wrong



			// Call DMS Fetch Rules API
			LOGGER.debug("Calling DMS Fetch Rules API for groupCode: {}", groupCode);
			String jsonResponse = callDmsFetchRulesApi(groupCode, gson, accesToken, apiAccesskey, cookie);

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser()
							.getUserPrincipalName() != null)
									? SecurityContext.getUser()
											.getUserPrincipalName()
									: "SYSTEM";
			// Process Response
			JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

			if (!jsonObject.has("saved_mapping_rules") || jsonObject.get("saved_mapping_rules").isJsonNull()) {
				LOGGER.warn("No saved_mapping_rules found in DMS API response for groupCode: {}", groupCode);
				return "No rules found";
			}

			JsonArray savedMappingRules = jsonObject.getAsJsonArray("saved_mapping_rules");

			if (savedMappingRules.size() > 0) {
				LOGGER.debug("Soft deleting existing rules for groupCode: {}", groupCode);
				dmsRuleMasterRepo.softDeleteAllActive(); // ✅ Only delete if new rules exist

				List<DmsRuleMasterEntity> newRules = new ArrayList<>();
				for (JsonElement ruleElement : savedMappingRules) {
					JsonObject ruleObj = ruleElement.getAsJsonObject();

					DmsRuleMasterEntity rule = new DmsRuleMasterEntity();
					rule.setRuleId(ruleObj.get("rule_id").getAsLong());
					rule.setRuleName(ruleObj.has("rule_name") && !ruleObj.get("rule_name").isJsonNull()
							? ruleObj.get("rule_name").getAsString()
							: null);
					rule.setCreatedOn(LocalDateTime.now());
					rule.setActive(true);
					// user logged in
					rule.setCreatedBy(userName);

					newRules.add(rule);
				}

				// Save new rules
				LOGGER.debug("Saving {} new rules for groupCode: {}", newRules.size(), groupCode);
				dmsRuleMasterRepo.saveAll(newRules);
			} else {
				LOGGER.warn("No new rules received for groupCode: {}", groupCode);
			}

			return "Success";

		} catch (Exception ex) {
			LOGGER.error("Error occurred while fetching rules from DMS for groupCode {}: {}", groupCode,
					ex.getMessage(), ex);
			throw new AppException("Failed to fetch DMS rules", ex);
		}

	}

	private String callDmsFetchRulesApi(String groupCode, Gson gson, String accesToken, String apiAccesskey,
			String cookie) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		Map<String, Config> configMap = configManager.getConfigs("DMS_APP", "dms.details", "DEFAULT");

		String url = configMap.get("dms.details.fetchRule.url").getValue();

		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("cookie", cookie);
		httpPost.setHeader("apiaccesskey", apiAccesskey);
		httpPost.setHeader("accesstoken", accesToken);
		httpPost.setHeader("Content-Type", "application/json");

		JsonObject obj = new JsonObject();
		StringEntity entity = new StringEntity(gson.toJson(obj).toString());
		httpPost.setEntity(entity);

		LOGGER.debug("Request sent to DMS Fetch Rules - {}", entity.getContent());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DMS API Request URL: {}", url);
			LOGGER.debug("Access Token: {}", accesToken);
			LOGGER.debug("API Access Key: {}", apiAccesskey);
			LOGGER.debug("Cookie: {}", cookie);
			for (org.apache.http.Header header : httpPost.getAllHeaders()) {
				LOGGER.debug("Request Header: {} = {}", header.getName(), header.getValue());
			}
		}

		HttpResponse resp = httpClient.execute(httpPost);
		Integer httpStatusCd = resp.getStatusLine().getStatusCode();
		String apiResp = EntityUtils.toString(resp.getEntity());

		if (httpStatusCd == 200) {
			LOGGER.debug("Received success response from DMS Fetch Rules API: {}", apiResp);
			return apiResp;
		} else {
			LOGGER.error("Received failure response from DMS Fetch Rules API, response: {}", apiResp);
			throw new AppException("Failed to fetch rules from DMS API");
		}
	}

	public List<RuleAnsDto> getViewRules() {
		List<RuleAnsDto> rules = new ArrayList<>();

		try {
			List<DmsRuleMasterEntity> dmsRules = dmsRuleMasterRepo.findByIsActiveTrue();
			for (DmsRuleMasterEntity entity : dmsRules) {
				RuleAnsDto dto = new RuleAnsDto();
				dto.setRuleNo(entity.getRuleId());
				dto.setRuleName(entity.getRuleName());
				rules.add(dto);
			}

		} catch (Exception e) {
			LOGGER.error("Error while fetching rules from database for groupCode {}: {}", e.getMessage(), e);
			throw new AppException("Failed to fetch rules. Please try again later.", e);
		}

		return rules;
	}

}
