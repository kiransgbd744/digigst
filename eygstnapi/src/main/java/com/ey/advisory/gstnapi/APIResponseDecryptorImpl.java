package com.ey.advisory.gstnapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIResponseDecryptor;

/**
 * The decryption techniques are different for the response for an Auth Token
 * and for a response for a regular request. The keys and algorithms used here
 * vary. Hence we need to route the response to the appropriate decryptor, based
 * on the type of request (for which we obtained the response). This class does
 * exactly this. Implemented as a decorator that routes the response JSON to the
 * appropriate handler for decrypting.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("APIResponseDecryptorImpl")
public class APIResponseDecryptorImpl implements APIResponseDecryptor {

	@Autowired
	@Qualifier("APIAuthTokenResponseDecryptor")
	APIResponseDecryptor apiAuthTokenResponseDecryptor;

	@Autowired
	@Qualifier("APIGSPAuthTokenResponseDecryptor")
	APIResponseDecryptor apiGSPAuthTokenResponseDecryptor;

	@Autowired
	@Qualifier("APINonAuthTokenResponseDecryptor")
	APIResponseDecryptor apiNonAuthTokenResponseRKDecryptor;

	@Override
	public APIResponse decrypt(APIParams params, APIConfig config,
			APIExecParties parties, APIAuthInfo authInfo, String respJson,
			Map<String, Object> context) {
		String id = params.getApiIdentifier();
		if (id.equals(APIIdentifiers.GET_AUTH_TOKEN)
				|| id.equals(APIIdentifiers.REFRESH_AUTH_TOKEN)) {
			return apiAuthTokenResponseDecryptor.decrypt(params, config,
					parties, authInfo, respJson, context);
		} else if (id.equals(APIIdentifiers.GET_OTP)
				|| id.equals(APIIdentifiers.GSTN_TAXPAYER_LOGOUT)|| id.equals(APIIdentifiers.EVC_SIGN_FILE)) {
			APIResponse apiResp = new APIResponse();
			apiResp.setResponse(respJson);
			return apiResp;
		} else if (id.equals(APIIdentifiers.GET_GSP_AUTH_TOKEN)) {
			return apiGSPAuthTokenResponseDecryptor.decrypt(params, config,
					parties, authInfo, respJson, context);
		} else {
			return apiNonAuthTokenResponseRKDecryptor.decrypt(params, config,
					parties, authInfo, respJson, context);
		}
	}
}
