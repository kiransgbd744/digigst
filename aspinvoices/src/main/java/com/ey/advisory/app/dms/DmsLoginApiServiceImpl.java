/**
 * 
 */
package com.ey.advisory.app.dms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.apache.http.Header;
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

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.domain.master.DmsUserDetails;
import com.ey.advisory.core.async.repositories.master.DmsUserDetailsRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("DmsLoginApiServiceImpl")
public class DmsLoginApiServiceImpl {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private DmsApiCallServiceImpl apiCallService;

	@Autowired
	@Qualifier("DmsUserDetailsRepository")
	private DmsUserDetailsRepository userDetailsMasterRepo;
	
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	public String getDmsLoginAccess(String groupCode , Long fileId) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			Pair<String, String> authForDmsCall = apiCallService
					.getAuthForDmsCall(groupCode);

			String accesToken = authForDmsCall.getValue0();
			String apiAccesskey = authForDmsCall.getValue1();

			String responseMsg = callDmsLoginApi(groupCode, gson, accesToken, apiAccesskey, fileId);

			if (responseMsg != null && !responseMsg.equalsIgnoreCase("Login Failed!") 
					&& !responseMsg.isEmpty()) {
				// Update Repository only if a valid cookie is received
				updateUserDetailsWithToken(groupCode, responseMsg);
				return "Success";
			} else {
				LOGGER.error("DMS Login failed for groupCode: {}. Response: {}", 
						groupCode, responseMsg);
				return responseMsg;
			}

		} catch (Exception ex) {
			LOGGER.error("Error occured while invoking DmsLoginApiServiceImpl"
					+ ".getLoginAccess() {}", ex);
			throw new AppException(ex);
		}
	}

	private String callDmsLoginApi(String groupCode, Gson gson,
			String accesToken, String apiAccesskey , Long fileId)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException  {
		Map<String, Config> configMap = configManager.getConfigs("DMS_APP",
				"dms.details", "DEFAULT");// glrecon.sendConfigId -> dms.url

		String url = configMap.get("dms.details.login.url").getValue();

		Optional<DmsUserDetails> adminEnteredDetails = userDetailsMasterRepo
				.findByGroupCode(groupCode);

		String userName = adminEnteredDetails.get().getUserName();
		String password = adminEnteredDetails.get().getPassword();

		HttpPost httpPost = new HttpPost(url);
		JsonObject obj = new JsonObject();
		obj.addProperty("username", userName);
		obj.addProperty("password", password);

		httpPost.setHeader("apiaccesskey", apiAccesskey);
		httpPost.setHeader("accesstoken", accesToken);

		/*
		 * JsonObject req = new JsonObject(); req.add("req", obj);
		 */
		LOGGER.debug("request send to DSM Login req- {} ", obj);
		httpPost.setHeader("Content-Type", "application/json");

		StringEntity entity = new StringEntity(gson.toJson(obj).toString());
		httpPost.setEntity(entity);

		LOGGER.debug("request send to DSM Login - {} ", entity.getContent());

		HttpResponse resp = httpClient.execute(httpPost);

		Integer httpStatusCd = resp.getStatusLine().getStatusCode();
		String apiResp = EntityUtils.toString(resp.getEntity());
		String cookieValue = null;
		if (httpStatusCd == 200) {

			Header cookie = resp.getFirstHeader("Set-Cookie");
			if (cookie != null) {
				cookieValue = cookie.getValue();

				LOGGER.debug("Received success response from DMS Login apiResp "
						+ " cookie {} {}", apiResp, cookieValue);
			} else {
				LOGGER.error("Received failure response from DMS Login, "
						+ "the response is {}", apiResp);
			}
			return cookieValue;
		}else {
			LOGGER.error("Received failure response from DMS Login API. HTTP Status: {}, Response: {}", httpStatusCd,
					apiResp);
			String errorDesc = DmsUtils.extractErrorMessage(apiResp);
			Optional.ofNullable(fileId).filter(id -> id > 0)
		    .ifPresentOrElse(
		        id -> gstr1FileStatusRepository.updateErrorDesc(id, errorDesc),
		        () -> LOGGER.error("Invalid File ID ({}), skipping error description update.", fileId)
		    );
			return errorDesc;
		}
	}

	private void updateUserDetailsWithToken(String groupCode, String jwtToken) {

		Optional<DmsUserDetails> userDetailsOpt = userDetailsMasterRepo
				.findByGroupCode(groupCode);

		if (userDetailsOpt.isPresent()) {
			DmsUserDetails userDetails = userDetailsOpt.get();
			LocalDateTime tokenGenTime = LocalDateTime.now();
			LocalDateTime tokenExpTime = tokenGenTime.plusMinutes(25);
			userDetails.setJwtToken(jwtToken);
			userDetails.setTokenGenTime(tokenGenTime);
			userDetails.setTokenExpTime(tokenExpTime);
			userDetails.setModifiedOn(LocalDateTime.now());

			userDetailsMasterRepo.save(userDetails);
		} else {
			throw new AppException(
					"User details not found for groupCode: " + groupCode);
		}
	}
}
