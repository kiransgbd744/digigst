/**
 * 
 */
package com.ey.advisory.gstnapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("GenerateAuthTokenServiceImpl")
public class GenerateAuthTokenServiceImpl implements GenerateAuthTokenService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Override
	public boolean generateAuthToken(String gstin, String otpCode) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("generating auth token for gstin = " + gstin);
			}
			APIResponse response = callGstnForAuthToken(gstin, otpCode);
			if (response.isSuccess()) {
				String resp = response.getResponse();
				JsonObject respObj = (new JsonParser()).parse(resp)
						.getAsJsonObject();

				String res = respObj.has("status_cd")
						? respObj.get("status_cd").getAsString() : null;
				return ("1".equals(res));
			} 
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("The Error response from gstin is {}",
						response.getErrors());
			}
			return false;
		} catch (Exception e) {
			LOGGER.error("Exception Occured while calling Gstin to generate"
					+ " Auth Token", e);
			return false;
		}

	}

	private APIResponse callGstnForAuthToken(String gstin, String otpCode) {
		APIParam param1 = new APIParam("gstin", gstin);
		APIParam param3 = new APIParam("otp", otpCode);
		String apiIdentifier = gstin
				.equals(APIConstants.DEFAULT_PUBLIC_API_GSTIN)
						? APIIdentifiers.GET_GSP_AUTH_TOKEN
						: APIIdentifiers.GET_AUTH_TOKEN;
		APIParams authParams = new APIParams(TenantContext.getTenantId(),
				APIProviderEnum.GSTN, apiIdentifier, param1, param3);
		return apiExecutor.execute(authParams, "");

	}

}
