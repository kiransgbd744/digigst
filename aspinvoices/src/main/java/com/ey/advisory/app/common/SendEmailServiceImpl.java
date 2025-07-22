package com.ey.advisory.app.common;

import java.time.LocalDateTime;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrEmailLogEntity;
import com.ey.advisory.app.data.repositories.client.GstrEmailLogRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("SendEmailServiceImpl")
public class SendEmailServiceImpl implements SendEmailService {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("GstrEmailLogRepository")
	private GstrEmailLogRepository emailLogRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public boolean sendEmail(GstrEmailDetailsDto emailDto) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			String jsonDto = gson.toJson(emailDto);
			JsonObject reqObject= null;
			// persisting payload
			persistLogs(jsonDto, emailDto.getNotfnCode(),
					emailDto.getReturnType());

			HttpPost httpPost = new HttpPost(
					env.getProperty("gstrEmail.azure.url"));
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-TENANT-ID", "sp0002");
			StringEntity entity = new StringEntity(jsonDto.toString());
			httpPost.setEntity(entity);

			HttpResponse resp = httpClient.execute(httpPost);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			LOGGER.debug("resp {} ",apiResp);
			if (httpStatusCd == 200) {
				if (apiResp.equalsIgnoreCase("Success")) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Email Sent Successfully for ::{}",
								jsonDto);
					}
					return true;
				} else {
					LOGGER.error("Email Failed to Send:: {}", jsonDto);
					return false;
				}
			} else {
				LOGGER.error("Email Failed to Send with statusCode {}, {}",
						httpStatusCd, jsonDto);
				LOGGER.error("Recieved error response from azure:{}", apiResp);
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Exception while sending Email", e);
			return false;
		}
	}

	private void persistLogs(String jsonDto, String notificationCode,
			String returnType) {
		GstrEmailLogEntity logEntity = new GstrEmailLogEntity();
		logEntity.setNotificationCode(notificationCode);
		logEntity.setReturnType(returnType);
		logEntity
				.setReqPayload(GenUtil.convertStringToClob(jsonDto.toString()));
		logEntity.setCreatedOn(LocalDateTime.now());
		emailLogRepo.save(logEntity);
	}

}
