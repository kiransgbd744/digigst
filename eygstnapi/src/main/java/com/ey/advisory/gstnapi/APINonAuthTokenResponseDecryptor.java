package com.ey.advisory.gstnapi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIResponseDecryptor;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Every GSTN success response (other than for the OTP request and Auth 
 * Token Request) will have a 'data' and a Encrypted Random Key ('rek'). GSTN
 * generates a random key for every every response and use it to encrypt the
 * response JSON data. The 'data' element is the content that is encrypted using 
 * the 'random key'. After encrypting the data using the random key (rk), GSTN
 * encrypts this random key itself with the session key (sk) that it sent to 
 * us as part of the Authentication Response. This encrypted random key is the
 * 'rek'. So, our first task is to get the 'rek', decrypt it using the 'sk' that
 * we'd stored as part of our authentication process with GSTN. Then, use this
 * decrypted 'rk' key to decrypt the content represented by the 'data' element
 * in the response JSON. This will yield the actual response JSON from GSTN.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("APINonAuthTokenResponseDecryptor")
public class APINonAuthTokenResponseDecryptor implements APIResponseDecryptor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(APINonAuthTokenResponseDecryptor.class);
	
	@Autowired
	@Qualifier("DefaultAPIResponseDecryptHelperImpl")
	private APIResponseDecryptHelper apiResponseDecryptHelper;

	@Override
	public APIResponse decrypt(
						APIParams params, 
						APIConfig config, 
						APIExecParties parties, 
						APIAuthInfo authInfo,
						String respJson, 
						Map<String, Object> context) {

		JsonParser jsonParser = new JsonParser();
		GstnAPIAuthInfo apiAuthInfo = (GstnAPIAuthInfo) authInfo;
		// Get the JSON object. If GSTN returns an invalid Json, throw
		// an exception.
		JsonObject jsonObject = null;
		try {
			jsonObject = (JsonObject) jsonParser.parse(respJson);
		} catch (JsonParseException ex) {
			String msg = "GSTN returned Invalid JSON as Response";
			LOGGER.error(msg, ex);
			throw new InvalidAPIResponseException(msg);
		}

		
		// Get the data and Random Encryption Key (REK) from the response.
		String data = jsonObject.get("data").getAsString();
		String rek = jsonObject.get("rek").getAsString();
		
		// Get the stored session key.
		String sk = apiAuthInfo.getSessionKey();
		
		APIResponse apiResp = new APIResponse();
		apiResp.setRek(rek);
		apiResp.setSk(sk);
		
		// Decrypt the data using the 'rek' and 'sk'. This method internally
		// decrypts the rek and gets the rk, using 'sk' as the key. Then it
		// uses the 'rk' to decrypt the 'data' and return the content.
		String resp = apiResponseDecryptHelper.decrypt(data, rek, sk);
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("Success Response is : '%s'", resp);
			LOGGER.info(msg);
		}
		
		// Set the decrypted response string as the response JSON. This can
		// be used by the caller of the API to do further processing.
		apiResp.setResponse(resp);
		return apiResp;

	}
	
	
}
