/**
 * 
 */
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
@Component("GetHSNDetailsImpl")
public class GetHSNDetailsImpl implements GetHSNDetails{
	
	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;
	
	@Override
	public APIResponse getHSNDetails(
			String hsnCode, String gstin) {
		KeyValuePair<String, String> apiParam1 = new KeyValuePair<>(
				APIReqParamConstants.GET_HSNCODE, hsnCode);
		KeyValuePair<String, String> apiParam2 = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);
		APIParams apiParams = new APIParams(APIProviderEnum.EWB,
				APIIdentifiers.GET_HSN,"v1.03", apiParam1, apiParam2);
		return apiExecutor.execute(apiParams, null);
	}

}
