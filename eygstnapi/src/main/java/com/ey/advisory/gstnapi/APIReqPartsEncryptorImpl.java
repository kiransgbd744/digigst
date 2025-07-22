package com.ey.advisory.gstnapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIReqParts;
import com.ey.advisory.core.api.impl.APIReqPartsEncryptor;

@Component("APIReqPartsEncryptorImpl")
public class APIReqPartsEncryptorImpl implements APIReqPartsEncryptor {

	@Autowired
	@Qualifier("AuthTokenReqEncryptor")
	private APIReqPartsEncryptor authTokenReqEncryptor;
	
	@Autowired
	@Qualifier("OTPReqEncryptor")
	private APIReqPartsEncryptor otpReqEncryptor;
	
	@Autowired
	@Qualifier("TaxPayerLogoutReqEncryptor")
	private APIReqPartsEncryptor taxPayerLogoutReqEncryptor;
	
	@Autowired
	@Qualifier("PublicApiLogoutReqEncryptor")
	private APIReqPartsEncryptor publicLogoutReqEncryptor;
	
	@Autowired
	@Qualifier("RefreshTokenReqEncryptor")
	private APIReqPartsEncryptor refreshTokenReqEncryptor;

	@Autowired
	@Qualifier("APINonAuthTokenReqEncryptor")
	private APIReqPartsEncryptor nonAuthTokenReqEncryptor;
	
	@Autowired
	@Qualifier("GSPAuthTokenReqEncryptor")
	private APIReqPartsEncryptor gspAuthTokenReqEncryptor;

	@Override
	public APIReqParts encrypt(
					APIParams params, 
					APIConfig config, 
					APIExecParties parties, 
					final APIReqParts reqParts, 
					APIAuthInfo authInfo, 
					Map<String, Object> context) {


			if (params.getApiIdentifier()
					.equals(APIIdentifiers.GET_AUTH_TOKEN)) {
				return authTokenReqEncryptor.encrypt(params, config,
						parties, reqParts, authInfo, context);
			} else if (params.getApiIdentifier()
					.equals(APIIdentifiers.GET_OTP)) {
				return otpReqEncryptor.encrypt(params, 
						config, parties, reqParts, authInfo, context);
			} else if (params.getApiIdentifier()
					.equals(APIIdentifiers.GSTN_TAXPAYER_LOGOUT)) {
				return taxPayerLogoutReqEncryptor.encrypt(params, 
						config, parties, reqParts, authInfo, context);
			}else if (params.getApiIdentifier()
					.equals(APIIdentifiers.GSTN_PUBLICAPI_LOGOUT)) {
				return publicLogoutReqEncryptor.encrypt(params, 
						config, parties, reqParts, authInfo, context);
			} else if (params.getApiIdentifier()
					.equals(APIIdentifiers.REFRESH_AUTH_TOKEN)) {
				return refreshTokenReqEncryptor.encrypt(params,
						config, parties, reqParts, authInfo, context);
			} else if (params.getApiIdentifier()
					.equals(APIIdentifiers.GET_GSP_AUTH_TOKEN)) {
				return gspAuthTokenReqEncryptor.encrypt(params,
						config, parties, reqParts, authInfo, context);
			}
			// If it is not an Auth/OTP/Refresh request, then get it 
			// processed by the non-auth token encryptor.
			return nonAuthTokenReqEncryptor.encrypt(params, config,
					parties, reqParts, authInfo, context);

	}

}
