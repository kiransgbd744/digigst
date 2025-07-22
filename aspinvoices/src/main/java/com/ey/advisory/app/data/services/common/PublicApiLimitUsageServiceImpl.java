package com.ey.advisory.app.data.services.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.LimitUsageDTO;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
@Component("PublicApiLimitUsageServiceImpl")
public class PublicApiLimitUsageServiceImpl
		implements PublicApiLimitUsageService {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	private Environment env;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	public Pair<String, String> getLimitAndUsageForGroupCode(String groupCode) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			String url = env.getProperty("public.api.counter.api");
			if (url == null) {
				LOGGER.error("Counter app get Usage & limit count URL in "
						+ "Application.properties is empty");
				return null;
			}

			HttpGet httpGet = new HttpGet(
					url + "getLimitAndUsageCount?groupCode=" + groupCode);

			HttpResponse resp = httpClient.execute(httpGet);
			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());

			if (httpStatusCd == 200) {
				JsonObject reqObject = (new JsonParser()).parse(apiResp)
						.getAsJsonObject();
				String respStatus = reqObject.get("hdr").getAsString();

				if ("S".equalsIgnoreCase(respStatus)) {
					LimitUsageDTO dto = new LimitUsageDTO();
					Integer usage = reqObject.get("usage").getAsInt();
					Integer limit = reqObject.get("limit").getAsInt();
					dto.setLimit(limit);
					dto.setUsage(usage);
					dto.setRemainingCnt(limit - usage);
					dto.setStatusOn(getStandardIST());
					JsonElement respBody = gson.toJsonTree(dto);

					return new Pair<>("S", respBody.toString());
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

	private String getStandardIST() {
		LocalDateTime ist = EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.now());
		return FORMATTER.format(ist);
	}

}
