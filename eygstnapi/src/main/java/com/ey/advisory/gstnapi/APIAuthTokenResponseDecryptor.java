package com.ey.advisory.gstnapi;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIResponseDecryptor;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component("APIAuthTokenResponseDecryptor")
public class APIAuthTokenResponseDecryptor implements APIResponseDecryptor {

	@Override
	public APIResponse decrypt(
						APIParams params, 
						APIConfig config, 
						APIExecParties parties, 
						APIAuthInfo authInfo,
						String respJson, 
						Map<String, Object> context) {
		
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(respJson);
		GstnAPIAuthInfo gstnApiAuthInfo = (GstnAPIAuthInfo) authInfo;

		String appKeyString = gstnApiAuthInfo.getAppKey();
		// If the response is success, then SEK will be present in the
		// response json for the Auth request.
		String sek = jsonObject.get("sek").getAsString();
		String sk = CryptoUtils.decryptSek(sek, appKeyString);
		context.put(APIContextConstants.SESSION_KEY, sk);
		
		String authTokenEnc = jsonObject.get("auth_token").getAsString();
		String authToken = authTokenEnc; // Decrypt the auth token.
		
		// Get the Expiry in Seconds. If the 'expiry' element is not present,
		// assume the expiry to be 6 hours. This value is what we use to 
		// calculate the actual expiry time. If the expiry value is present
		// and is not an integer, then this block of code will throw 
		// an exception and  the auth token request will fail.
		/*int expiryInsecs = (jsonObject.get("expiry") != null) ?
					jsonObject.get("expiry").getAsInt() : 360;*/
		int expiryInMins = 720;//Changed to 12 hrs as per this US - 146126
		// Create an API response object and set the response JSON to it.
		GSTNAPIAuthTokenResponse apiResp = 
				new GSTNAPIAuthTokenResponse(sek, sk, authToken, expiryInMins);		
		apiResp.setResponse(respJson);		
		return apiResp;
	}	
}
