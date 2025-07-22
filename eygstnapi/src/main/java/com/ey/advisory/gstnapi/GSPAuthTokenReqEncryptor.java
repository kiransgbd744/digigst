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

@Component("GSPAuthTokenReqEncryptor")
public class GSPAuthTokenReqEncryptor implements APIReqPartsEncryptor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSPAuthTokenReqEncryptor.class);

	@Override
	public APIReqParts encrypt(APIParams params, APIConfig config,
			APIExecParties parties, APIReqParts reqParts, APIAuthInfo authInfo,
			Map<String, Object> context) {
		try {
			GSTNAPIProvider apiProvider = (GSTNAPIProvider) parties
					.getApiProvider();
			GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
			String appKey = CryptoUtils.generateAppKey();
			String gstin = endUser.getGstinConfig().getGstin();
			String gstnUserName = endUser.getGstinConfig().getGstinUserName();
			String password = endUser.getGstinConfig().getPassword();
			if (password == null || password.isEmpty()) {
				String msg = String.format("Password is not Configured in DB,"
						+ " Password is mandatory to generate the"
						+ " Public Auth Token for GSTN '%S'", gstin);
				LOGGER.error(msg);
				throw new APIException(msg);
			}
			context.put(APIContextConstants.APP_KEY, appKey);
			PublicKey pubKey = apiProvider.getGstnPubKey();
			String encryptedAppkey = CryptoUtils.encryptAppKey(appKey, pubKey);
			String encryptedPassword = CryptoUtils
					.encryptPassword(password.getBytes(), pubKey);
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{\"action\":\"ACCESSTOKEN\",\"username\" : \"");
			jsonBuilder.append(gstnUserName);
			jsonBuilder.append("\", \"app_key\":\"");
			jsonBuilder.append(encryptedAppkey);
			jsonBuilder.append("\", \"password\":\"");
			jsonBuilder.append(encryptedPassword);
			jsonBuilder.append("\"}");
			String reqJson = jsonBuilder.toString();
			return new APIReqParts(reqParts.getHeaders(),
					reqParts.getQueryParams(), reqJson);
		} catch (Exception ex) {
			String errorMsg = "Exception while encrypting Auth Token Request";
			LOGGER.error(errorMsg, ex);
			throw new APIException(errorMsg);
		}

	}

}
