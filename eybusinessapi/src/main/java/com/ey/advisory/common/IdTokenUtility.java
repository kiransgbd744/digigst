package com.ey.advisory.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.javatuples.Triplet;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("IdTokenUtility")
public class IdTokenUtility {

	private static final String URL_ENCODED_TYPE = "application/x-www-form-urlencoded";

	private static final String INTERNAL_OAUTH_USERNAME = "oauth.internal.admin_username";

	private static final String INTERNAL_OAUTH_PWD = "oauth.internal.admin_password";

	private static final String INTERNAL_OAUTH_URL = "oauth.internal.ldap_url";

	ConfigManager configManager;
	private final Map<String, Boolean> map = new HashMap<>();

	public String getIdTokenValue(String username, String password) {
		configManager = StaticContextHolder.getBean("ConfigManagerImpl",
				ConfigManager.class);

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("BEGIN AuthenticationController generateAccessToken,"
					+ " About to load LDAP credentials from CONFIG Table");

		HttpClient httpClient = HttpClientBuilder.create()
				.disableRedirectHandling().build();
		try {
			Triplet<String, String, String> adminCreds = getLDAPAdminCredential();
			String adminUsername = adminCreds.getValue0();
			String adminpwd = adminCreds.getValue1();
			String adminUrl = adminCreds.getValue2();
			if (adminUsername.isEmpty() || adminpwd.isEmpty()) {
				String errMsg = "LDAP Admin userID and Password is not configured";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			String authorizationValue = createAuthorizationHeader(adminUsername,
					adminpwd);
			List<NameValuePair> form = new ArrayList<>();
			form.add(new BasicNameValuePair("grant_type", "password"));
			form.add(new BasicNameValuePair("username", username));
			form.add(new BasicNameValuePair("password", password));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form,
					Consts.UTF_8);
			HttpPost httpPost = new HttpPost(adminUrl);
			httpPost.setHeader("Authorization", authorizationValue);
			httpPost.setHeader("Content-Type", URL_ENCODED_TYPE);
			httpPost.setEntity(entity);
			HttpResponse resp = httpClient.execute(httpPost);
			String apiResponse = EntityUtils.toString(resp.getEntity());
			JsonObject respObj = new JsonParser().parse(apiResponse)
					.getAsJsonObject();

			if (respObj.has("id_token")) {
				map.put(String.format("%s-%s", username, password), true);
				return respObj.get("id_token").getAsString();

			} else {
				LOGGER.error("Id_Token Error resp {}", respObj);
				throw new AppException("id_token not available");
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Access Token", ex);
			throw new AppException(ex.getMessage());
		}

	}

	private String createAuthorizationHeader(String userName, String password) {
		String userNamePswrdStr = userName.concat(":").concat(password);
		String encodedStr = new String(
				Base64.encodeBase64(userNamePswrdStr.getBytes()));
		return "Basic ".concat(encodedStr);

	}

	private Triplet<String, String, String> getLDAPAdminCredential() {
		Map<String, Config> configMap = configManager.getConfigs("EYInternal",
				"oauth");
		String username = configMap.containsKey(INTERNAL_OAUTH_USERNAME)
				? configMap.get(INTERNAL_OAUTH_USERNAME).getValue() : "";
		String pwd = configMap.containsKey(INTERNAL_OAUTH_PWD)
				? configMap.get(INTERNAL_OAUTH_PWD).getValue() : "";
		String url = configMap.containsKey(INTERNAL_OAUTH_URL)
				? configMap.get(INTERNAL_OAUTH_URL).getValue() : "";
		return new Triplet<>(username, pwd, url);
	}
}
