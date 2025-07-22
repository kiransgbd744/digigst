package com.ey.advisory.app.dms;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 * 
 *         This service calls DMS API
 */

@Component("DmsApiCallServiceImpl")
@Slf4j
public class DmsApiCallServiceImpl {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public Pair<String,String> getAuthForDmsCall(String groupCode) {

		try {

			String accesToken = null;
			Map<String, Config> configMapDms = configManager
					.getConfigs("DMS_APP", "dms.details", "DEFAULT");

			String username = configMapDms.get("dms.details.username") == null
					? ""
					: configMapDms.get("dms.details.username").getValue();

			String password = configMapDms.get("dms.details.password") == null
					? ""
					: configMapDms.get("dms.details.password").getValue();

			String apiAccesskey = configMapDms
					.get("dms.details.apiaccesskey") == null ? ""
							: configMapDms.get("dms.details.apiaccesskey")
									.getValue();

			String getAuthRespBody = getAccessToken(username, password,
					apiAccesskey);

			JsonObject requestObject = JsonParser.parseString(getAuthRespBody)
					.getAsJsonObject();

			LOGGER.debug(" DMS getAccess Token Response Object {} ", requestObject);

			int statusCode = requestObject.get("status").getAsInt();

			if (statusCode == 1) {
				accesToken = requestObject.get("accessToken").getAsString();
			} else {
				String errMsg = "Access Token is not Available for DMS APP.";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			return new Pair<String, String>(accesToken, apiAccesskey);

		} catch (Exception ex) {
			LOGGER.error("Exception while calling DmsApiCallServiceImpl ", ex);
			throw new AppException(ex);
		}
			}

	
	public String getAccessToken(String userName, String password,
			String apiAccessKey) {
		try {
			Map<String, Config> configMap = configManager.getConfigs("DMS_APP",
					"dms.details", "DEFAULT");

			String authUrl = configMap.get("dms.details.authtoken") == null ? ""
					: configMap.get("dms.details.authtoken").getValue();

			HttpPost httpPost = new HttpPost(authUrl);
			httpPost.setHeader("username", userName);
			httpPost.setHeader("password", password);
			httpPost.setHeader("apiaccesskey", apiAccessKey);
			httpPost.setHeader("Content-Type", "application/json");

			LOGGER.debug(" DMS getAccessToken Requesting {} ", httpPost.getRequestLine());

			for (org.apache.http.Header header : httpPost.getAllHeaders()) {
				LOGGER.debug("Header: " + header.getName() + " = "
						+ header.getValue());
			}

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			LOGGER.debug("DMS getAccessToken responseBody  {} ", responseBody);

			return responseBody;
		} catch (Exception ex) {
			LOGGER.error("Exception in DmsApiCallServiceImpl "
					+ "DMS getAccessToken responseBody {}", ex);
			throw new AppException(ex);
		}

	}
}
