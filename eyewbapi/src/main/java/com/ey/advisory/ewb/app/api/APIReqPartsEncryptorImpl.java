package com.ey.advisory.ewb.app.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIProviderEnum;

@Component("EWBAPIReqPartsEncryptorImpl")
public class APIReqPartsEncryptorImpl implements APIReqPartsEncryptor {
	
	
	@Autowired
	@Qualifier("EWBAuthTokenReqEncryptor")
	private APIReqPartsEncryptor nicAuthTokenEncryptor;
	
	@Autowired
	@Qualifier("EWBAPINonAuthTokenEncryptor")
	private APIReqPartsEncryptor nicNonAuthTokenEncryptor;

	@Override
	public APIReqParts encrypt(
			APIParams params, String version, APIConfig config, 
			APIExecParties parties, Map<String, Object> context,
			final APIReqParts reqParts) {
		
		
		if(params.apiProvider == APIProviderEnum.EWB) {
		
			if (params.getApiAction().equals(APIIdentifiers.GET_AUTH_TOKEN)) {
				return nicAuthTokenEncryptor.encrypt(params, version, config,
						parties, context, reqParts);
			}
			// If it is not an auth request, then get it processed by
			// the non-auth token encryptor.
			return nicNonAuthTokenEncryptor.encrypt(params, version, config,
					parties, context, reqParts);
		} else {
			// Handle the GSTN API case here and remove the following
			// throw statement.
			throw new APIException(
					"GSTN API support is not included currently");
		}

	}

}
