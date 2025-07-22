package com.ey.advisory.einv.app.api;

import java.util.Map;

public interface APIReqPartsEncryptor {

	public APIReqParts encrypt(APIParams params, 
			String version,	APIConfig config, 
			APIExecParties parties, Map<String, Object> context,
			APIReqParts reqParts);
}
