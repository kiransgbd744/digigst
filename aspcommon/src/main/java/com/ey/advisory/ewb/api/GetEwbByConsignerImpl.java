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

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("GetEwbByConsignerImpl")
@Slf4j
public class GetEwbByConsignerImpl implements GetEwbByConsigner {
	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public APIResponse getEwbByConsigner(String gstin, String docType,
			String docNo) {
		KeyValuePair<String, String> apiParam1 = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);
		KeyValuePair<String, String> apiParam2 = new KeyValuePair<>(
				APIReqParamConstants.DOC_TYPE, docType);
		KeyValuePair<String, String> apiParam3 = new KeyValuePair<>(
				APIReqParamConstants.DOC_NUM, docNo);
		APIParams apiParams = new APIParams(APIProviderEnum.EWB,
				APIIdentifiers.GET_EWB_BY_CONSIGNOR, "v1.03", apiParam1,
				apiParam2, apiParam3);
		return apiExecutor.execute(apiParams, null);
	}
}
