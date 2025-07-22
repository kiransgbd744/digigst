package com.ey.advisory.gstnapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParamType;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIHttpReqParamConfig;
import com.ey.advisory.core.api.impl.APIReqQueryParamsBuilder;
import com.ey.advisory.core.api.impl.APIVersionConfig;

@Component("APIReqQueryParamsBuilderImpl")
public class APIReqQueryParamsBuilderImpl implements APIReqQueryParamsBuilder {

	@Override
	public Map<String, String> buildQueryParamsMap(
					APIParams params,
					APIConfig config, 
					APIExecParties parties,
					APIAuthInfo authInfo,
					Map<String, Object> context) {
		Map<String, String> queryParamsMap = null;
			APIVersionConfig versionConfig = config.getConfigForVersion(
					params.getApiVersion());
			List<APIHttpReqParamConfig> paramsList =
					versionConfig.getExpectedUrlParams();
			queryParamsMap = new HashMap<>();
			for(APIHttpReqParamConfig paramConfig : paramsList){
				String paramVal = params.getAPIParamValue(
							paramConfig.getName(), APIParamType.URLPARAM);
				if(paramConfig.isMandatory() && paramVal == null) {
						throw new APIException("Mandatory Request URL Param "
								+ "is Missing " + paramConfig.getName());
				}
				if (!paramConfig.isMandatory() && paramVal == null) {
					continue;
				} 
				queryParamsMap.put(paramConfig.getName(), paramVal);
			}
			
		return queryParamsMap;
	}

}
