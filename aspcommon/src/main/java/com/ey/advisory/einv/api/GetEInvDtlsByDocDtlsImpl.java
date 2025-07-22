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
@Component("GetEInvDtlsByDocDtlsImpl")
public class GetEInvDtlsByDocDtlsImpl implements GetEInvDtlsByDocDtls {

	@Autowired
	@Qualifier("DefaultEINVAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public APIResponse getEInvDtlsByDocDetails(String docNum, String docType,
			String docDate,String gstin) {
		return callExecute(docNum,docType, docDate,gstin);
	}

	private APIResponse callExecute(String docNum, String docType,
			String docDate,String gstin) {

		KeyValuePair<String, String> gstinstr = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);

		KeyValuePair<String, String> docNumstr = new KeyValuePair<>(
				APIReqParamConstants.INVDOC_NUM, docNum);

		KeyValuePair<String, String> docTypestr = new KeyValuePair<>(
				APIReqParamConstants.INVDOC_TYPE, docType);

		KeyValuePair<String, String> docDatestr = new KeyValuePair<>(
				APIReqParamConstants.INVDOC_DATE, docDate);

		APIParams apiParams = new APIParams(APIProviderEnum.EINV,
				APIIdentifiers.GET_EINVBYDOCDETAILS, docTypestr, docNumstr,
				docDatestr,gstinstr);
		return apiExecutor.execute(apiParams, null);
	}

}
