package com.ey.advisory.gstnapi;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.GSTNAPIUtil;

@Component("DefaultAPIGstinGroupCodeLocator")
public class DefaultAPIGstinGroupCodeLocator implements APIGstinGroupLocator {

	@Override
	public String locateGstin(APIParams params) {
		String identifier = params.getApiIdentifier();
		return GSTNAPIUtil.isPublicApiRelatedRequest(identifier)
				? APIConstants.DEFAULT_PUBLIC_API_GSTIN
				: params.getAPIParamValue(APIReqParamConstants.GSTIN);
	}

}
