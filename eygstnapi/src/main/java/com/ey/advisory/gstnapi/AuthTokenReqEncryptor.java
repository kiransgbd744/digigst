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

@Component("AuthTokenReqEncryptor")
public class AuthTokenReqEncryptor implements APIReqPartsEncryptor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthTokenReqEncryptor.class);

	@Override
	public APIReqParts encrypt(
					APIParams params,
					APIConfig config,
					APIExecParties parties,
					APIReqParts reqParts,
					APIAuthInfo authInfo,
					Map<String, Object> context) {
		try {
			GSTNAPIProvider apiProvider = (GSTNAPIProvider) parties
					.getApiProvider();
			GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
			GstnAPIAuthInfo gstnApiAuthInfo = (GstnAPIAuthInfo) authInfo;
			String gstin = endUser.getGstinConfig().getGstin();
			String gstnUserName = endUser.getGstinConfig().getGstinUserName();
			String otp = params.getAPIParamValue("otp");
			if (otp == null) {
				String msg = String.format("OTP is not available in API Params,"
						+ " OTP is mandatory to generate the"
						+ " Auth Token for GSTN '%S'", gstin);
				LOGGER.error(msg);
				throw new APIException(msg);
			}
			String appKey = gstnApiAuthInfo.getAppKey();
			PublicKey pubKey = apiProvider.getGstnPubKey();
			String encryptedAppkey = CryptoUtils.encryptAppKey(appKey, pubKey);
			String encryptedOtp = CryptoUtils.encryptOtpReqData(otp, appKey);
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{\"action\":\"AUTHTOKEN\",\"username\" : \"");
			jsonBuilder.append(gstnUserName);
			jsonBuilder.append("\", \"app_key\":\"");
			jsonBuilder.append(encryptedAppkey);
			jsonBuilder.append("\", \"otp\":\"");
			jsonBuilder.append(encryptedOtp);
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
