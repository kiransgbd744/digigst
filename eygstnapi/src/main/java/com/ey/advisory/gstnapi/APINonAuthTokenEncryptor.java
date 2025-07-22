package com.ey.advisory.gstnapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.api.PayloadSizeExceededException;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIReqParts;
import com.ey.advisory.core.api.impl.APIReqPartsEncryptor;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

@Component("APINonAuthTokenReqEncryptor")
public class APINonAuthTokenEncryptor implements APIReqPartsEncryptor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(APINonAuthTokenEncryptor.class);

	private static final int MAX_PAYLOAD_SIZE = 5 * 1024 * 1024;

	final List<String> unUsedHdrs = ImmutableList.of("digigst_username",
			"api_key", "api_secret", "Content-Type", "access_token",
			"group_code");

	@Override
	public APIReqParts encrypt(APIParams params, APIConfig config,
			APIExecParties parties, APIReqParts reqParts, APIAuthInfo authInfo,
			Map<String, Object> context) {
		GstnAPIAuthInfo gstnApiAuthInfo = (GstnAPIAuthInfo) authInfo;
		String httpMethod = config.getConfigForVersion(params.getApiVersion())
				.getHttpMethod();
		String reqJson = "";
		try {
			if ("PUT".equals(httpMethod) || "POST".equals(httpMethod)) {
				String reqData = reqParts.getReqData();
				String identifier = params.getApiIdentifier();
				//For Sign&File Caller is expected to send the encrypted Payload
				if (GSTNAPIUtil.isSignAndFileRequest(identifier)) {
					reqJson = reqData;
				} else {
					String sk = gstnApiAuthInfo.getSessionKey();
					String encryptedData = CryptoUtils
							.encryptAPIReqData(reqData, sk);
					String hmac = CryptoUtils.hmacSHA256(reqData, sk);
					String action = config.getAction();
					if (!GSTNAPIUtil.isNewReturnApiRequest(identifier)) {
						// This is specific to New Returns API
						reqJson = "{\"action\":\"" + action + "\",\"data\" :\""
								+ encryptedData + "\",\"hmac\" :\"" + hmac
								+ "\"}";
					} else {
						Map<String, String> headersMap = new HashMap<>(
								reqParts.getHeaders());
						headersMap.keySet().removeAll(unUsedHdrs);
						String headersJsonString = new Gson()
								.toJson(headersMap);
						reqJson = "{\"action\":\"" + action + "\",\"data\" :\""
								+ encryptedData + "\",\"hmac\" :\"" + hmac
								+ "\",\"hdr\" :" + headersJsonString + "}";
					}
				}

				int dataLength = reqJson.length();
				if (dataLength > MAX_PAYLOAD_SIZE) {
					String msg = "PayLoad Size has been exceeded."
							+ " Size should be less than 5MB";
					LOGGER.error(msg);
					throw new PayloadSizeExceededException(msg);
				}
			}
			return new APIReqParts(reqParts.getHeaders(),
					reqParts.getQueryParams(), reqJson);
		} catch (Exception ex) {
			LOGGER.error("Error while encrypting the APINonAuth Request", ex);
		}

		return null;
	}

}
