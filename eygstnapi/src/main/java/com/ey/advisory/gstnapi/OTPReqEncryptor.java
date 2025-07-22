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

@Component("OTPReqEncryptor")
public class OTPReqEncryptor implements APIReqPartsEncryptor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OTPReqEncryptor.class);

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
				String msg = "About to encrypt the OTP Request...";
				LOGGER.debug(msg);
			}
			GSTNAPIProvider apiProvider = (GSTNAPIProvider) parties
					.getApiProvider();
			GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
			String gstnUserName = endUser.getGstinConfig().getGstinUserName();
			String appKey = CryptoUtils.generateAppKey();
			PublicKey pubKey = apiProvider.getGstnPubKey();
			String encryptedAppkey = CryptoUtils.encryptAppKey(appKey, pubKey);
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{\"action\":\"OTPREQUEST\",\"username\" : \"");
			jsonBuilder.append(gstnUserName);
			jsonBuilder.append("\", \"app_key\":\"");
			jsonBuilder.append(encryptedAppkey);
			jsonBuilder.append("\"}");
			String reqJson = jsonBuilder.toString();
			context.put(GSTNAPIConstants.APP_KEY, appKey);
			APIReqParts retReqParts = new APIReqParts(reqParts.getHeaders(),
					reqParts.getQueryParams(), reqJson);
			if (LOGGER.isDebugEnabled()) {
				String msg = "Encrypted the OTP Request!!";
				LOGGER.debug(msg);
			}
			return retReqParts;
		} catch (Exception ex) {
			String errorMsg = "Exception while encrypting auth request";
			throw new APIException(errorMsg);
		}

	}

}
