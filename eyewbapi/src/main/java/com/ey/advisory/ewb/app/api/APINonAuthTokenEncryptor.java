package com.ey.advisory.ewb.app.api;

import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Component("EWBAPINonAuthTokenEncryptor")
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
			GstnAPIAuthInfo authInfo = persistenceManager.loadAPIAuthInfo(gstin,
					APIProviderEnum.EWB.name());
			if (authInfo != null) {
				sk = authInfo.getSessionKey();
			} else {
				throw new AuthTokenExpiredException("Auth Token has expired");
			}
			String encryptedData = new String(
					Base64.encodeBase64(CryptoUtils.encrypt(base64Str, sk)));
			String action = config.getAction();
			reqJson = "{\"action\":\"" + action + "\",\"data\" :\""
					+ encryptedData + "\"}";
		}
		return new APIReqParts(reqParts.getHeaders(), reqParts.getQueryParams(),
				reqJson);

	}

}
