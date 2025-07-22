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

@Component("PublicApiLogoutReqEncryptor")
public class PublicApiLogoutReqEncryptor implements APIReqPartsEncryptor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PublicApiLogoutReqEncryptor.class);

	@Override
	public APIReqParts encrypt(
				APIParams params, 
				APIConfig config, 
				APIExecParties parties, 
				APIReqParts reqParts,
				APIAuthInfo authInfo, 
				Map<String, Object> context) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "About to encrypt the Public LOGOUT API Request...";
				LOGGER.debug(msg);
			}
			GSTNAPIProvider apiProvider = (GSTNAPIProvider) parties
					.getApiProvider();
			PublicKey pubKey = apiProvider.getGstnPubKey();
			GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
			String gstnUserName = endUser.getGstinConfig().getGstinUserName();
			GstnAPIAuthInfo apiAuthInfo = (GstnAPIAuthInfo) authInfo;
			String appKey = apiAuthInfo.getAppKey();
			String encryptedAppkey = CryptoUtils.encryptAppKey(appKey, pubKey);
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{\"action\":\"LOGOUT\",\"username\" : \"");
			jsonBuilder.append(gstnUserName);
			jsonBuilder.append("\", \"app_key\":\"");
			jsonBuilder.append(encryptedAppkey);
			jsonBuilder.append("\", \"auth_token\":\"");
			jsonBuilder.append(apiAuthInfo.getGstnToken());
			jsonBuilder.append("\"}");
			String reqJson = jsonBuilder.toString();
			APIReqParts retReqParts = new APIReqParts(reqParts.getHeaders(),
					reqParts.getQueryParams(), reqJson);
			if (LOGGER.isDebugEnabled()) {
				String msg = "Encrypted the LOGOUT Request!!";
				LOGGER.debug(msg);
			}
			return retReqParts;
		} catch (Exception ex) {
			String errorMsg = "Exception while encrypting LOGOUT request";
			LOGGER.error(errorMsg, ex);
			throw new APIException(errorMsg);
		}

	}

}
