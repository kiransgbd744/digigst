package com.ey.advisory.admin.publicapi.limit.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("PublicApiLimitServiceImpl")
public class PublicApiLimitServiceImpl implements PublicApiLimitService {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	private Environment env;

	@Override
	public Pair<String, String> getLimitsForGroupCode(String groupCode) {

		try {

			String url = env.getProperty("public.api.counter.api");
			if (url == null) {

				String errMsg = "Counter app Get limit URL in "
						+ " properties is not configured";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);

			}

			HttpGet httpGet = new HttpGet(url +"getLimitsForGroup?groupCode="+ groupCode);

			HttpResponse resp = httpClient.execute(httpGet);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());

			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				String respStatus = reqObject.get("hdr").getAsString();

				if ("S".equalsIgnoreCase(respStatus)) {
					return new Pair<>("S",
							reqObject.get("resp").getAsJsonObject().toString());
				} else {
					return new Pair<>("E", reqObject.get("resp").getAsString());

				}

			} else {
				return new Pair<>("E", "Internal Server error");

			}
		} catch (Exception e) {
			String msg = "Error while getting Details";
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}

	@Override
	public Pair<String, String> saveLimitsForGroupCode(PublicApiLimitDTO dto) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String requestObject = gson.toJson(dto);
			String url = env.getProperty("public.api.counter.api");
			if (url == null) {
				String errMsg = "Counter app Save limit URL in "
						+ " properties is not configured";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			HttpPost httpPost = new HttpPost(url+"saveLimits");
			httpPost.setHeader("Content-Type", "application/json");
			StringEntity entity = new StringEntity(requestObject);
			httpPost.setEntity(entity);

			HttpResponse resp = httpClient.execute(httpPost);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());

			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				String respStatus = reqObject.get("hdr").getAsString();

				if ("S".equalsIgnoreCase(respStatus)) {
					return new Pair<>("S",
							reqObject.get("resp").getAsString());
				} else {
					return new Pair<>("E", reqObject.get("resp").getAsString());

				}

			} else {
				return new Pair<>("E", "Internal Server error");
			}
		} catch (Exception e) {
			String msg = "Error while getting Details";
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage());
		}
	}

}
