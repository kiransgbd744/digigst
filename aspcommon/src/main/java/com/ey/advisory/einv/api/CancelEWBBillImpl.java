package com.ey.advisory.einv.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIParams;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.dto.CancelEWBBillNICReqDto;
import com.google.gson.Gson;

/**
 * @author Siva Reddy
 *
 */
@Component("CancelEWBBillImpl")
public class CancelEWBBillImpl implements CancelEWBBill{

	@Autowired
	@Qualifier("DefaultEINVNonStubExecutor")
	private APIExecutor apiExecutor;
	
	@Override
	public APIResponse cancelEWBservice(CancelEWBBillNICReqDto req) {
		return callExecute(req);
	}

	private APIResponse callExecute(CancelEWBBillNICReqDto requestDto) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		String reqBody = gson.toJson(requestDto);
		KeyValuePair<String, String> apiParam = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, requestDto.getGstin());
		APIParams apiParams = new APIParams(APIProviderEnum.EINV,
				APIIdentifiers.CANCEL_EWB, apiParam);
		return apiExecutor.execute(apiParams, reqBody);
	}

}
