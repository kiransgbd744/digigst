package com.ey.advisory.einv.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIParams;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.APIResponse;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva Reddy
 *
 */
@Slf4j
@Component("GetEWBDetailsByIrnImpl")
public class GetEWBDetailsByIrnImpl implements GetEWBDetailsByIrn {

	@Autowired
	@Qualifier("DefaultEINVAPIExecutor")
	private APIExecutor apiExecutor;

	private static final List<String> ERROR_LIST = ImmutableList.of("2143",
			"2148", "4003");

	@Override
	public APIResponse getEWBDetailsByIrn(String irnNo, String gstin) {
		return callExecute(irnNo, gstin);
	}

	private APIResponse callExecute(String irnNo, String gstin) {
		KeyValuePair<String, String> gstinstr = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);

		KeyValuePair<String, String> irnNostr = new KeyValuePair<>(
				APIReqParamConstants.IRN, irnNo);

		KeyValuePair<String, String> irpParam = new KeyValuePair<>(
				APIReqParamConstants.IRP.toLowerCase(), "NIC1");

		APIParams apiParams = new APIParams(APIProviderEnum.EINV,
				APIIdentifiers.GET_EWBDETAILSBYIRN, irnNostr, gstinstr,
				irpParam);

		APIResponse response = apiExecutor.execute(apiParams, null);

		if (!response.isSuccess() && ERROR_LIST
				.contains(response.getErrors().get(0).getErrorCode())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"About to retry with NIC2 for ErrorList {} For GroupCode {} ",
						response.getErrors(), TenantContext.getTenantId());
			}
			response = retryWithAlternativeSource(apiParams, gstinstr, irnNostr,
					irpParam);
		}
		return response;
	}

	private APIResponse retryWithAlternativeSource(APIParams apiParams,
			KeyValuePair<String, String> gstinstr,
			KeyValuePair<String, String> irnNostr,
			KeyValuePair<String, String> irpParam) {

		irpParam = new KeyValuePair<>(APIReqParamConstants.IRP.toLowerCase(),
				"NIC2");
		apiParams = new APIParams(APIProviderEnum.EINV,
				APIIdentifiers.GET_EWBDETAILSBYIRN, irnNostr, gstinstr,
				irpParam);
		APIResponse response = apiExecutor.execute(apiParams, null);
		return response;
	}
}
