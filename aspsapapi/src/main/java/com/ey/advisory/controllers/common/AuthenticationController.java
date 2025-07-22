package com.ey.advisory.controllers.common;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonCryptoUtils;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthenticationController {

	private static final String URL_ENCODED_TYPE = "application/x-www-form-urlencoded";

	private static final String INTERNAL_OAUTH_USERNAME = "oauth.internal.admin_username";

	private static final String INTERNAL_OAUTH_PWD = "oauth.internal.admin_password";

	private static final String INTERNAL_OAUTH_URL = "oauth.internal.ldap_url";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthenticationController.class);
	
	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@PostMapping(value = "/api/generateAccessToken", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateAccessToken(
			HttpServletRequest httpRequest) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("BEGIN AuthenticationController generateAccessToken,"
					+ " About to load LDAP credentials from CONFIG Table");

		HttpClient httpClient = HttpClientBuilder.create()
				.disableRedirectHandling().build();
		JsonObject json = new JsonObject();
		JsonParser parser = new JsonParser();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			Triplet<String, String, String> adminCreds =
					getLDAPAdminCredential();
			String adminUsername = adminCreds.getValue0();
			String adminpwd = adminCreds.getValue1();
			String adminUrl = adminCreds.getValue2();
			if (adminUsername.isEmpty() || adminpwd.isEmpty()) {
				String errMsg =
						"LDAP Admin userID and Password is not configured";
				LOGGER.error(errMsg);
				json.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				json.add("resp", gson.toJsonTree(errMsg));
				return new ResponseEntity<>(json.toString(), HttpStatus.OK);
			}
			String authorizationHeader = httpRequest.getHeader("Authorization");
			Pair<String, String> authDetails = extractAndGetUserNamePswrd(
					authorizationHeader);
			String authorizationValue = createAuthorizationHeader(adminUsername,
					adminpwd);

			List<NameValuePair> form = new ArrayList<>();
			form.add(new BasicNameValuePair("grant_type", "password"));
			form.add(new BasicNameValuePair("username",
					authDetails.getValue0()));
			form.add(new BasicNameValuePair("password",
					authDetails.getValue1()));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form,
					Consts.UTF_8);
			HttpPost httpPost = new HttpPost(adminUrl);
			httpPost.setHeader("Authorization", authorizationValue);
			httpPost.setHeader("Content-Type", URL_ENCODED_TYPE);
			httpPost.setEntity(entity);
			HttpResponse resp = httpClient.execute(httpPost);
			String response = EntityUtils.toString(resp.getEntity());
			json.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			json.add("resp", gson.toJsonTree(parser.parse(response)));
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Access Token", ex);
			json.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			json.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getSACDashboardUrl", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSACDashboardUrl(
			HttpServletRequest httpRequest) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("BEGIN  getSACDashboardUrl,"
					+ " About to load URL from User Object");

		JsonObject json = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String response = null;
		try {
			User user = SecurityContext.getUser();
			String sacUrl = user.getSacDashboardUrl();
			if (sacUrl == null) {
				response = "Logged In User doesn't have SAC Dashboard Access";
				json.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			} else {
				response = sacUrl;
				json.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			}
			json.add("resp", gson.toJsonTree(response));
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while Exracting SAC Dasboard Url", ex);
			json.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			json.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
		}
	}

	private Pair<String, String> extractAndGetUserNamePswrd(
			String authorizationHeader) {
		String[] authDetails = authorizationHeader.split("\\s");
		String encodedUserNamePswrd = authDetails[1];
		String decodedUserNamePswrd = new String(
				Base64.decodeBase64(encodedUserNamePswrd));
		String[] decodedUserNameAndPswrdArray = decodedUserNamePswrd.split(":");
		String decodedUserName = decodedUserNameAndPswrdArray[0];
		String decodedPswrd = decodedUserNameAndPswrdArray[1];
		return new Pair<>(decodedUserName, decodedPswrd);
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
	
	@GetMapping(value = "/generateEncMessage", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEncMessage(
			HttpServletRequest request) {

		String msg = request.getParameter("msg");
		if (msg == null || msg.isEmpty()) {
			String errMsg = "Message is mandatory to perform Encryption";
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		JsonObject resp = new JsonObject();
		Gson gson = new Gson();
		String encAESKey = env.getProperty("aes.internal.security.key");
		try {
			String encodedMsg = Base64
					.encodeBase64String(msg.getBytes(StandardCharsets.UTF_8));
			String encMsg = CommonCryptoUtils.encrypt(encodedMsg, encAESKey);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(encMsg));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			LOGGER.error("Exception while ecryption of Password", ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

}
