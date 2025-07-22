package com.ey.advisory.gstnapi;

import java.security.PublicKey;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIReqParts;
import com.ey.advisory.core.api.impl.APIReqPartsEncryptor;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Component("RefreshTokenReqEncryptor")
public class RefreshTokenReqEncryptor implements APIReqPartsEncryptor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RefreshTokenReqEncryptor.class);

	@Override
	public APIReqParts encrypt(
					APIParams params,
					APIConfig config,
					APIExecParties parties,
					APIReqParts reqParts,
					APIAuthInfo authInfo,
					Map<String, Object> context) {
		try {
			GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
			GstnAPIAuthInfo gstnApiAuthInfo = (GstnAPIAuthInfo) authInfo;
			String gstnUserName = endUser.getGstinConfig().getGstinUserName();
			String authToken = gstnApiAuthInfo.getGstnToken();
			String appKey = CryptoUtils.generateAppKey();
			String sk = gstnApiAuthInfo.getSessionKey();
			String encryptedAppkey = CryptoUtils.encryptAPIReqData(appKey,
					sk);
			String encryptedUserName =
					CryptoUtils.encryptOtpReqData(gstnUserName, appKey);
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append(
					"{\"action\":\"REFRESHTOKEN\",\"username\" : \"");
			jsonBuilder.append(encryptedUserName);
			jsonBuilder.append("\", \"app_key\":\"");
			jsonBuilder.append(encryptedAppkey);
			jsonBuilder.append("\", \"auth_token\":\"");
			jsonBuilder.append(authToken);
			jsonBuilder.append("\"}");
			String reqJson = jsonBuilder.toString();
			return new APIReqParts(reqParts.getHeaders(),
					reqParts.getQueryParams(), reqJson);
		} catch (Exception ex) {
			String errorMsg = "Exception while Refreshing Auth Token Request";
			LOGGER.error(errorMsg, ex);
			throw new APIException(errorMsg);
		}

	}

}
