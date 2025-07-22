package com.ey.advisory.einv.app.api;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EINVAPIVersionCalculatorImpl")
public class EINVAPIVersionCalculatorImpl implements APIVersionCalculator{
	
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
