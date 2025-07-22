package com.ey.advisory.ewb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;

/**
 * @author Ravindra
 *
 */
@Component("GetGSTINDetailsImpl")
public class GetGSTINDetailsImpl implements GetGSTINDetails {

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;
	
	@Override
	public APIResponse getGSTINDetails(String getGstin, String userGstin) {
		KeyValuePair<String, String> apiParam1 = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, userGstin);
		KeyValuePair<String, String> apiParam2 = new KeyValuePair<>(
				APIReqParamConstants.GET_GSTIN, getGstin);
		APIParams apiParams = new APIParams(APIProviderEnum.EWB,
				APIIdentifiers.GET_GSTIN, apiParam1, apiParam2);
		return apiExecutor.execute(apiParams, null);
	}

}
