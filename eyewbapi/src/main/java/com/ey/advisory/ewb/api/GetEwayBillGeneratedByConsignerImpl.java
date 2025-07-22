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
@Component("GetEwayBillGeneratedByConsignerImpl")
public class GetEwayBillGeneratedByConsignerImpl
		implements GetEwayBillGeneratedByConsigner {

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public APIResponse getEwayBillGeneratedByConsigner(String docType,
			String docNo, String gstin) {
		KeyValuePair<String, String> apiParam1 = new KeyValuePair<>(
				APIReqParamConstants.DOC_TYPE, docType);
		KeyValuePair<String, String> apiParam2 = new KeyValuePair<>(
				APIReqParamConstants.DOC_NUM, docNo);
		KeyValuePair<String, String> apiParam3 = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);
		APIParams apiParams = new APIParams(APIProviderEnum.EWB,
				APIIdentifiers.GET_EWAY_BILL_GENERATED_BY_CONSIGNER, apiParam1, apiParam2,
				apiParam3);
		return apiExecutor.execute(apiParams, null);
	}

}
