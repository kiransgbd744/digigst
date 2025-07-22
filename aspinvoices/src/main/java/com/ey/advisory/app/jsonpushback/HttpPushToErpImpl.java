/**
 * 
 */
package com.ey.advisory.app.jsonpushback;

import java.io.IOException;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("HttpPushToErpImpl")
public class HttpPushToErpImpl implements HttpPushToErp{
	
	@Autowired
	@Qualifier("GSTNHttpClient")
	private HttpClient httpClient;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	private String url;
	private String apiKey;
	private String apiSecret;
	
	@PostConstruct
	public void init() {
		Map<String, Config> configMap = configManager.getConfigs("ERPPUSH",
				"erppush");
		url = configMap.containsKey("erppush.gsp.url")
				? String.valueOf(
						configMap.get("erppush.gsp.url").getValue())
				: null;
		apiKey = configMap.containsKey("erppush.gsp.apikey")
				? String.valueOf(
						configMap.get("erppush.gsp.apikey").getValue())
				: null;
		apiSecret = configMap
				.containsKey("erppush.gsp.apisecret")
						? String.valueOf(
								configMap.get("erppush.gsp.apisecret")
										.getValue())
						: null;
	}

	@Override
	public void pushToErp(JsonPushBackDto pushBackJson) {
		Gson gson = new Gson();
		HttpResponse response = null;
		String resource = url;
		HttpPost httpPost = new HttpPost(resource);
		//httpPost.setHeader("Authorization", "Basic YjJidXNlcjppb2NsMTIzNA==");
		httpPost.setHeader("api_key",apiKey);
		httpPost.setHeader("api_secret", apiSecret);
		try {
			StringEntity entity = new StringEntity(gson.toJson(pushBackJson));
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			String respString = EntityUtils.toString(response.getEntity());
				LOGGER.error("statusCode" + statusCode);
				LOGGER.error("response" + respString);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException(e);
		}		
	}

}
