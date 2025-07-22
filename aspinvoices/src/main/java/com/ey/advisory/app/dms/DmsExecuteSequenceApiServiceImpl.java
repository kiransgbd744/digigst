package com.ey.advisory.app.dms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.hibernate.PropertyNotFoundException;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.DmsRuleMasterRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.DmsRequestLogRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyVaultUtility;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.DmsUserDetails;
import com.ey.advisory.core.async.repositories.master.DmsUserDetailsRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("DmsExecuteSequenceApiServiceImpl")
public class DmsExecuteSequenceApiServiceImpl {
	
	private static final String URL_ENCODED_TYPE = "application/x-www-form-urlencoded";

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
	
	private DmsUserDetailsRepository userDetailsMasterRepo;

	@Autowired
	private DmsRuleMasterRepository dmsRuleMasterRepo;

	@Autowired
	private DmsRequestLogRepository dmsLogRepo;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	private Environment env;

	public String getExecSequenceApi(String groupCode, Long ruleId, String uuid,
			Long fileId, String fileName, String jobCateg ,String ruleName) {
		try {
			gstr1FileStatusRepository.updateFileStatus(fileId, JobStatusConstants.IN_PROGRESS);
			Optional<DmsUserDetails> userDetailsOpt = userDetailsMasterRepo
					.findByGroupCode(groupCode);
			if (!userDetailsOpt.isPresent()) {
				LOGGER.error("GroupCode not found for DMS onboarding: {}", groupCode);
				throw new AppException();
			}

			DmsUserDetails userDetails = userDetailsOpt.get();
			String cookie = userDetails.getJwtToken();
			LocalDateTime tokenGenTime = userDetails.getTokenGenTime();
			LocalDateTime tokenExpTime = userDetails.getTokenExpTime();

			if (cookie == null || (tokenExpTime != null
					&& Duration.between(tokenGenTime, LocalDateTime.now())
							.toMinutes() >= 25)) {
				LOGGER.info(
						"JWT token expired or missing. Fetching a new token for groupCode: {}",
						groupCode);
				if ("Success".equalsIgnoreCase(
						dmsLoginApiServiceImpl.getDmsLoginAccess(groupCode, fileId))) {
					userDetails = userDetailsMasterRepo
							.findByGroupCode(groupCode)
							.orElseThrow(() -> new AppException(
									"Failed to retrieve updated JWT token"));
					cookie = userDetails.getJwtToken();
				}
			}

			Gson gson = GsonUtil.newSAPGsonInstance();
			Pair<String, String> authForDmsCall = apiCallService
					.getAuthForDmsCall(groupCode);
			String accesToken = authForDmsCall.getValue0();
			String apiAccesskey = authForDmsCall.getValue1();

			// Call DMS EXEC API
			LOGGER.info("Calling DMS FetchExecution Seq API for groupCode: {}",
					groupCode);
			String jsonResponse = callDmsExecSeqApi(groupCode, gson, accesToken,
					apiAccesskey, cookie, ruleId, uuid, fileId, fileName,
					jobCateg, ruleName);

			saveRequestLog(uuid, jobCateg, jsonResponse, null);

			return jsonResponse;

		} catch (Exception ex) {
			gstr1FileStatusRepository.updateFileStatus(fileId, JobStatusConstants.FAILED);
			LOGGER.error(
					"Error occurred while FetchExecution from DMS for groupCode {}: {}",
					groupCode, ex.getMessage(), ex);
			throw new AppException("Failed to execute File api in DMS", ex);
		}

	}

	private String callDmsExecSeqApi(String groupCode, Gson gson,
			String accesToken, String apiAccesskey, String cookie, Long ruleId,
			String uuid, Long fileId, String fileName, String jobCateg ,String ruleName)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		Map<String, Config> configMap = configManager.getConfigs("DMS_APP", "dms.details", "DEFAULT");

		String url = configMap.get("dms.details.exec.seq.url").getValue();

		Map<String, Config> asbConfigMap = configManager.getConfigs("DMS_ASB", "dms.asb", "DEFAULT");
		Map<String, Config> blobConfigMap = configManager.getConfigs("DMS_BLOB", "dms.details", "DEFAULT");

		if (ruleId != null) {
			ruleName = dmsRuleMasterRepo.findByRuleId(ruleId);
		}

		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("cookie", cookie);
		httpPost.setHeader("apiaccesskey", apiAccesskey);
		httpPost.setHeader("accesstoken", accesToken);
		// httpPost.setHeader("Content-Type", "multipart/form-data");

		List<NameValuePair> form = new ArrayList<>();
		if (ruleId != null) {
			String sRuleId = String.valueOf(ruleId);
			form.add(new BasicNameValuePair("etl_active_rule", sRuleId));
		}
		form.add(new BasicNameValuePair("uid", uuid));
		form.add(new BasicNameValuePair("rule_name", ruleName));
		form.add(new BasicNameValuePair("mode", "i"));
		form.add(new BasicNameValuePair("targetCntrName",
				getValueFromMap(blobConfigMap, "dms.details.blob.containerName")));
		form.add(new BasicNameValuePair("targetFileName", fileName));
		form.add(new BasicNameValuePair("targetStrAccName",
				getValueFromMap(blobConfigMap, "dms.details.blob.accountName")));

		String targetStrAccKey;
		try {
			targetStrAccKey = KeyVaultUtility
					.getKey(getValueFromMap(blobConfigMap, "dms.details.blob.keyVaultSecretName"));
		} catch (Exception e) {
			LOGGER.error("Error retrieving storage account key: {}", e.getMessage(), e);
			throw new AppException("Error retrieving storage account key", e);
		}
		form.add(new BasicNameValuePair("targetStrAccKey", targetStrAccKey));

		String connectionStrSecretName;
		String sharedAccessKey;
		try {
			connectionStrSecretName = getValueFromMap(asbConfigMap, "dms.asb.keyvault.connectionStr");
			String accessKeySecretName = getValueFromMap(asbConfigMap, "dms.asb.keyvault.secretname");

			sharedAccessKey = KeyVaultUtility.getKey(accessKeySecretName);

		} catch (Exception e) {
			LOGGER.error("Error retrieving Azure Service Bus connection details: {}", e.getMessage(), e);
			throw new AppException("Error retrieving Azure Service Bus connection details", e);
		}
		String eventHubConnectionStr = String.format(connectionStrSecretName, sharedAccessKey);

		form.add(new BasicNameValuePair("eventHubConnectionStr", eventHubConnectionStr));
		form.add(new BasicNameValuePair("eventHubQueueName", getValueFromMap(asbConfigMap, "dms.asb.req.queue")));
		form.add(new BasicNameValuePair("eventHubGroupCode", groupCode));
		form.add(new BasicNameValuePair("eventHubJobCategory", jobCateg));
		form.add(new BasicNameValuePair("fileId", String.valueOf(fileId)));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

		httpPost.setHeader("Content-Type", URL_ENCODED_TYPE);
		httpPost.setEntity(entity);

		LOGGER.info("Request Body: {}", form);

		String contentType = entity.getContentType().getValue();
		LOGGER.info("Generated Content-Type: {}", contentType);

		LOGGER.info("Request sent to DMS Exec Seq API - {}", entity.getContent());
		HttpResponse resp = httpClient.execute(httpPost);
		Integer httpStatusCd = resp.getStatusLine().getStatusCode();
		String apiResp = EntityUtils.toString(resp.getEntity());

		if (httpStatusCd == 200) {
			LOGGER.info("Received success response from DMS Exec Seq API: {}", apiResp);
			return apiResp;
		} else {
			LOGGER.error("Received failure response from DMS Exec Seq API. HTTP Status: {}, Response: {}", httpStatusCd,
					apiResp);
			gstr1FileStatusRepository.updateTransformationStatus(fileId, "Transformation Failed");
			gstr1FileStatusRepository.updateFileStatus(fileId, JobStatusConstants.FAILED);
			String errorDesc = DmsUtils.extractErrorMessage(apiResp);
			LOGGER.error("Extracted error message: {}", errorDesc);
			gstr1FileStatusRepository.updateErrorDesc(fileId, errorDesc);
			
			LOGGER.error("Received failure response from DMS Exec Seq API, response: {}", apiResp);
			throw new AppException("Failed to fetch Exec Seq API. HTTP Status: " + httpStatusCd);
		}
	}

	public String getProp(String key) {
		if (env.containsProperty(key))
			return env.getProperty(key);
		else
			throw new PropertyNotFoundException(
					"Could not find the Property with key:" + key);
	}

	private void saveRequestLog(String uuid, String jobCategory,
			String execFileResponse, String callbackResponse) {
		try {
			DmsRequestLog log = new DmsRequestLog();
			log.setUuid(uuid);
			log.setJobCategory(jobCategory);
			log.setExecFileResponse(execFileResponse);
			log.setCallbackResponse(callbackResponse);
			log.setCreatedOn(LocalDateTime.now());
			log.setUpdatedOn(LocalDateTime.now());

			DmsRequestLog savedLog = dmsLogRepo.save(log);

		} catch (Exception e) {
			LOGGER.error("Error saving DmsRequestLog: {}", e.getMessage());
			throw new AppException("Error saving DmsRequestLog", e);
		}
	}
	
	private String getValueFromMap(Map<String, Config> configMap, String key) {
	    return Optional.ofNullable(configMap.get(key))
	            .map(Config::getValue)
	            .orElseThrow(() -> new AppException("Missing config key: " + key));
	}
}