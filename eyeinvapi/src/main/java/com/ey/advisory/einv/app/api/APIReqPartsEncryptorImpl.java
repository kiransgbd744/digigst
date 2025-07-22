package com.ey.advisory.einv.app.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("EINVAPIReqPartsEncryptorImpl")
public class APIReqPartsEncryptorImpl implements APIReqPartsEncryptor {

	@Autowired
	@Qualifier("EINVAuthTokenReqEncryptor")
	private APIReqPartsEncryptor nicAuthTokenEncryptor;

	@Autowired
	@Qualifier("EINVAPINonAuthTokenEncryptor")
	private APIReqPartsEncryptor nicNonAuthTokenEncryptor;

	@Override
	public APIReqParts encrypt(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, final APIReqParts reqParts) {

		if (params.getApiAction().equals(APIIdentifiers.GET_EINV_AUTH_TOKEN)) {
			return nicAuthTokenEncryptor.encrypt(params, version, config,
					parties, context, reqParts);
		}
		// If it is not an auth request, then get it processed by
		// the non-auth token encryptor.
		return nicNonAuthTokenEncryptor.encrypt(params, version, config,
				parties, context, reqParts);

	}

}
