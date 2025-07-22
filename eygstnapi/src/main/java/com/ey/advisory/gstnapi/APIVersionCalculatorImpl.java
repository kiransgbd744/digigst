package com.ey.advisory.gstnapi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIVersionCalculator;

@Component("APIVersionCalculatorImpl")
public class APIVersionCalculatorImpl implements APIVersionCalculator{
	
	private static Logger LOGGER=
			LoggerFactory.getLogger(APIVersionCalculatorImpl.class);

	@Override
	public String getAPIVersion(APIParams params,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context) {
		
		if(params.isVersionSpecified()) {
			String specifiedVersion = params.getApiVersion();
			if(config.getActiveVersions().contains(specifiedVersion) ){
				return specifiedVersion;
			} else {
				String errorMsg = "Specified version is not active";
				LOGGER.error(errorMsg);
				throw new APIException(errorMsg);
			}
		} else {
			String currentVersion = config.getCurVersion();
			if(config.getActiveVersions().contains(currentVersion) ){
				return currentVersion;
			} else {
				String errorMsg = "Configured current version is not active";
				LOGGER.error(errorMsg);
				throw new APIException(errorMsg);
			}
		}
	}

}
