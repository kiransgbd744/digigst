package com.ey.advisory.common;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.FailedBatchAlertReqDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FailedBatchAlertUtility {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private void sendAlertEmail(FailedBatchAlertReqDto reqDto) {
		try {
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			HttpPost httpPost = new HttpPost("https://uatvc.eyasp.in/asp-vcom/v1/client/sendFailedBatchAlert");
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-TENANT-ID", TenantContext.getTenantId());
			StringEntity entity = new StringEntity(gson.toJson(reqDto));
			httpPost.setEntity(entity);
			HttpResponse resp = httpClient.execute(httpPost);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				JsonObject reqJson = reqObject.get("hdr").getAsJsonObject();
				String respStatus = reqJson.get("status").getAsString();
				if (respStatus.equalsIgnoreCase("S")) {
					LOGGER.error("Alert Mail Sent.");
				} else {
					LOGGER.error("Alert Mail Failed.");
				}
			}
		} catch (Exception e) {
			String errMsg = String.format("Alert Mail Failed. for Group %s",
					TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
		}
	}

	public void prepareAndTriggerAlert(String payloadId, String moduleType,
			String excepMsg) {

		try {
			String groupCode = TenantContext.getTenantId();
			Map<String, Config> configMap = configManager
					.getConfigs("BatchAlert", "failed.revIntg", "DEFAULT");

			boolean isAlertReq = configMap
					.get("failed.revIntg.alertreq") == null ? false
							: Boolean.valueOf(configMap
									.get("failed.revIntg.alertreq").getValue());

			if (isAlertReq == false) {
				String logMsg = String.format("Email Alert is not Required.");
				LOGGER.error(logMsg);
				return;
			}

			String eligibleGrps = configMap != null && configMap
					.get("failed.revIntg.eligiblealert.groups") != null
							? configMap
									.get("failed.revIntg.eligiblealert.groups")
									.getValue()
							: null;
			if (Strings.isNullOrEmpty(eligibleGrps)) {
				String logMsg = String
						.format("There is no Eligible Grps Config in the Master to get Email Alert,"
								+ " Hence Email will not be sent.");
				LOGGER.error(logMsg);
				return;
			}

			List<String> grpDtls = Arrays.asList(eligibleGrps.split(","));

			if (!grpDtls.contains(groupCode)) {
				String logMsg = String
						.format("Group Code %s is not a Part of Failed Batch Eligible List, "
								+ "Hence Email will not be sent.", groupCode);
				LOGGER.error(logMsg);
				return;
			}
			
			String primaryEmail = "";
			String secondEmails = "";
			
			if(!moduleType.equalsIgnoreCase("AUTO_RECON"))
			{
			primaryEmail = configMap != null
					&& configMap.get("failed.revIntg.primaryEmail") != null
							? configMap.get("failed.revIntg.primaryEmail")
									.getValue()
							: null;
			secondEmails = configMap != null
					&& configMap.get("failed.revIntg.secondEmails") != null
							? configMap.get("failed.revIntg.secondEmails")
									.getValue()
							: null;
			}else
			{
				primaryEmail = configMap != null
						&& configMap.get("failed.autoRecon.primaryEmail") != null
								? configMap.get("failed.autoRecon.primaryEmail")
										.getValue()
								: null;
				secondEmails = configMap != null
						&& configMap.get("failed.autoRecon.secondEmails") != null
								? configMap.get("failed.autoRecon.secondEmails")
										.getValue()
								: null;
			}
		
			FailedBatchAlertReqDto reqDto = new FailedBatchAlertReqDto();
			reqDto.setGroupCode(groupCode);
			reqDto.setPayloadId(payloadId);
			reqDto.setModuleType(moduleType);
			reqDto.setPrimaryEmail(primaryEmail);
			reqDto.setSecondaryEmails(Arrays.asList(secondEmails.split(",")));
			reqDto.setErrMsg(excepMsg);
			sendAlertEmail(reqDto);
		} catch (Exception e) {
			String errMsg = String.format("Alert Mail Failed. for Group %s",
					TenantContext.getTenantId());
			LOGGER.error(errMsg, e);
		}
	}
}
