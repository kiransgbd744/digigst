package com.ey.advisory.einv.app.api;

import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.gson.JsonObject;

@Component("EINVAPINonAuthTokenEncryptor")
public class APINonAuthTokenEncryptor implements APIReqPartsEncryptor {

	public APINonAuthTokenEncryptor() {
	}

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Override
	public APIReqParts encrypt(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, APIReqParts reqParts) {
		String httpMethod = config.getConfigForVersion(version).getHttpMethod();
		String reqJson = "";
		if ("POST".equals(httpMethod)) {
			String sk = "";
			String gstin = params.getApiParamValue(APIReqParamConstants.GSTIN);
			String reqData = reqParts.getReqData();
			String base64Str = Base64
					.encodeBase64String(reqData.getBytes(Charsets.UTF_8));
			String apiProvider = CommonUtil.getApiProviderEnum(params).name();
			GstnAPIAuthInfo authInfo = persistenceManager.loadAPIAuthInfo(gstin,
					apiProvider);
			if (authInfo != null && !authInfo.isExpired()) {
				sk = authInfo.getSessionKey();
			} else {
				throw new AuthTokenExpiredException("Auth Token has expired");
			}
			String encryptedData = new String(
					Base64.encodeBase64(CryptoUtils.encrypt(base64Str, sk)));
			JsonObject jsonObj = new JsonObject();
			if (APIIdentifiers.CANCEL_EWB.equals(params.getApiAction())) {
				jsonObj.addProperty("action", "CANEWB");
			}
			jsonObj.addProperty("Data", encryptedData);
			reqJson = jsonObj.toString();
		}
		return new APIReqParts(reqParts.getHeaders(), reqParts.getQueryParams(),
				reqParts.getPathParams(), reqJson);

	}

}
