package com.ey.advisory.einv.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIParams;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.APIResponse;

/**
 * @author Siva Reddy
 *
 */
@Component("GetSyncGSTINDetailsImpl")
public class GetSyncGSTINDetailsImpl implements GetSyncGSTINDetails {

	@Autowired
	@Qualifier("DefaultEINVAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public APIResponse getSyncGSTINDetails(String syncGstin, String gstin) {
		return callExecute(syncGstin, gstin);
	}

	private APIResponse callExecute(String syncGstin, String gstin) {
		KeyValuePair<String, String> gstinstr = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);

		KeyValuePair<String, String> syncGstinStr = new KeyValuePair<>(
				APIReqParamConstants.GET_GSTIN, syncGstin);

		APIParams apiParams = new APIParams(APIProviderEnum.EINV,
				APIIdentifiers.GET_SYNCGSTINFROMCP, syncGstinStr, gstinstr);
		return apiExecutor.execute(apiParams, null);
	}

}
