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
import com.ey.advisory.core.api.impl.APIResponseProcessor;

/**
 * Locate the post processor for each type of request and delegate the
 * processing to appropriate class.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("APIResponseProcessorImpl")
public class APIResponseProcessorImpl implements APIResponseProcessor {
	
	@Autowired
	@Qualifier("OTPResponseProcessor")
	private APIResponseProcessor otpRespProcessor;
	
	@Autowired
	@Qualifier("AuthTokenResponseProcessor")
	private APIResponseProcessor authTokenRespProcessor;
	
	@Autowired
	@Qualifier("RefreshTokenResponseProcessor")
	private APIResponseProcessor refreshTokenRespProcessor;

	@Autowired
	@Qualifier("NonAuthTokenRespProcessor")
	private APIResponseProcessor nonAuthTokenRespProcessor;
	
	@Autowired
	@Qualifier("GSPAuthTokenResponseProcessor")
	private APIResponseProcessor gspAuthTokenResponseProcessor;
		
	@Override
	public APIResponse processResponse(
						APIParams params, 
						APIConfig config, 
						APIExecParties parties,
						APIAuthInfo authInfo,
						APIResponse response,
						Map<String, Object> context) {
		
		String id = params.getApiIdentifier();
		
		APIResponseProcessor curProcessor = null;
		
		if (id.equals(APIIdentifiers.GET_OTP)) {
			curProcessor = otpRespProcessor;
		} else if (id.equals(APIIdentifiers.GET_AUTH_TOKEN)) {
			curProcessor = authTokenRespProcessor;
		} else if (id.equals(APIIdentifiers.REFRESH_AUTH_TOKEN)) { 
			curProcessor = authTokenRespProcessor;
		} else if (id.equals(APIIdentifiers.GET_GSP_AUTH_TOKEN)) { 
			curProcessor = gspAuthTokenResponseProcessor;
		} else {
			curProcessor = nonAuthTokenRespProcessor;			
		}
		return curProcessor.processResponse(params, config, 
				parties, authInfo, response, context);
	}

}
