package com.ey.advisory.gstnapi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIAuthInfoLoader;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Component
public class GSTNAPIAuthInfoLoader implements APIAuthInfoLoader {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSTNAPIAuthInfoLoader.class);

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Override
	public APIAuthInfo loadApiAuthInfo(APIParams params, APIConfig config,
			APIExecParties parties, Map<String, Object> context) {

		// Get the GSTIN from the API End User details.
		GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
		GstnAPIGstinConfig gstnConfig = endUser.getGstinConfig();
		String gstin = gstnConfig.getGstin();

		// Return the Authentication/Authorization details for the GSTN API.
		return getGstnAuthInfo(params, gstin);

	}

	private GstnAPIAuthInfo getGstnAuthInfo(APIParams params, String gstin) {
		GstnAPIAuthInfo apiAuthInfo = persistenceManager.loadAPIAuthInfo(gstin,
				APIProviderEnum.GSTN.name());
		String id = params.getApiIdentifier();
		if (apiAuthInfo == null && !id.equals(APIIdentifiers.GET_OTP)
				&& !id.equals(APIIdentifiers.GET_GSP_AUTH_TOKEN)) {
			String msg = String.format("GSTN Auth Info not available for "
					+ "GSTIN '%s'. Either the Auth Token is not generated OR "
					+ "it is not refreshed. Please get the Auth Info by "
					+ "starting with OTP request.", gstin);
			LOGGER.error(msg);
			throw new APIException(msg);
		}
		return apiAuthInfo;
	}
}
