/**
 * 
 */
package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.feedback.FeedBackSurveyEmailDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("SapEmailNotificationProcessor")
public class SapEmailNotificationProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SapEmailNotificationProcessor.class);

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = TenantContext.getTenantId();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		try {
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			Map<String, Config> configMap = configManager.getConfigs("FEEDBACK",
					"feedback.configuration", "DEFAULT");

			String tenantCode = TenantContext.getTenantId();
			String url = configMap != null
					&& configMap.get("feedback.configuration.azureUrl") != null
							? configMap.get("feedback.configuration.azureUrl")
									.getValue()
							: null;
			String primaryMail = configMap != null && configMap
					.get("feedback.configuration.primaryMail") != null
							? configMap
									.get("feedback.configuration.primaryMail")
									.getValue()
							: null;
			String secondaryMail = configMap != null && configMap
					.get("feedback.configuration.secondaryMail") != null
							? configMap
									.get("feedback.configuration.secondaryMail")
									.getValue()
							: null;
			List<String> secondaryMailList = secondaryMail != null 
			        ? Arrays.asList(secondaryMail.split(","))
			                : new ArrayList<>();
			FeedBackSurveyEmailDto emailDto = new FeedBackSurveyEmailDto();
			emailDto.setEmailTriggeredOn(
					json.get("emailTriggeredOn").getAsString());
			emailDto.setGroupCode(json.get("groupCode").getAsString());
			emailDto.setGroupName(json.get("groupName").getAsString());
			emailDto.setSapPrimaryEmail(primaryMail);
			emailDto.setSapSecondaryEmail(secondaryMailList);
			emailDto.setSource("Feedback");

			HttpClient httpClient = StaticContextHolder
					.getBean("InternalHttpClient", HttpClient.class);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-TENANT-ID", groupcode);

			StringEntity entity = new StringEntity(gson.toJson(emailDto));
			LOGGER.debug("emailDto ",emailDto);
			httpPost.setEntity(entity);
			LOGGER.debug("mail Sent");
			HttpResponse resp = httpClient.execute(httpPost);
			LOGGER.debug("response",resp);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());

			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				JsonObject reqJson = reqObject.get("hdr").getAsJsonObject();
				String respStatus = reqJson.get("status").getAsString();
				String emailStatus = null;

				if (respStatus.equalsIgnoreCase("S")) {
					emailStatus = "SENT";
				} else {
					emailStatus = "FAILED";
				}
				LOGGER.debug("respStatus " + respStatus);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while invoking Azure Vendor Email", ex);
		}
	}

}