package com.ey.advisory.gstnapi;

import org.springframework.stereotype.Component;

@Component("DefaultAPIResponseDecryptHelperImpl")
public class DefaultAPIResponseDecryptHelperImpl
		implements APIResponseDecryptHelper {

	@Override
	public String decrypt(String data, String rek, String sk) {
		/**
		 * For Public Apis we will not get rek . We are getting rek as empty
		 * So, if rek receiving as empty then it is a public api. we just decode 
		 * the response
		 */
		if(!rek.trim().isEmpty()) {
			return CryptoUtils.decryptResponseJson(data, rek, sk);
		}
		return CryptoUtils.decryptPublicJsonResponse(data);
	}

}
