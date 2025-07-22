/**
 * 
 */
package com.ey.advisory.ewb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.google.gson.Gson;

/**
 * @author Khalid1.Khan
 *
 */
@Component("CancelEWBImpl")
public class CancelEWBImpl implements CancelEWB{
	
	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;
	
	@Autowired
	private ERPReqRespLogHelper reqLogHelper;
	
	@Override
	public APIResponse cancelEwb(CancelEwbReqDto req, String gstin) {

		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
		String reqBody = gson.toJson(req);

		KeyValuePair<String, String> apiParam = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);
		APIParams apiParams = new APIParams(APIProviderEnum.EWB,
				APIIdentifiers.CANCEL_EWB, apiParam);
		reqLogHelper.logAppMessage(req.getEwbNo(),
				null, null, "Sending the Cancel EWB request to Framework");


		return apiExecutor.execute(apiParams, reqBody);

	}


}
