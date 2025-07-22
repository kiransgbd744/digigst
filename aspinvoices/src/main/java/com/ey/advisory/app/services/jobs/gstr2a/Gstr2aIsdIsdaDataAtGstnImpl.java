/*package com.ey.advisory.app.services.jobs.gstr2a;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.ey.advisory.app.docs.dto.Gstr2aIsdInvoicesAtGstnReqDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

*//**
 * 
 * @author Santosh.Gururaj
 *
 *//*

@Component("gstr2aIsdIsdaDataAtGstnImpl")
public class Gstr2aIsdIsdaDataAtGstnImpl implements Gstr2aIsdIsdaDataAtGstn {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aIsdIsdaDataAtGstnImpl.class);

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Override
	public String findIsdIsdaDataAtGstn(final Gstr2aIsdInvoicesAtGstnReqDto dto,
			final String groupCode, final String type) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findIsdIsdaDataAtGstn " + " Group Code: ",
					groupCode);
		}

		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParams params = null;
		if (APIConstants.ISD.equals(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR2A_GET_ISD, param1, param2);
		} else {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR2A_GET_ISDA, param1, param2);
		}
		APIResponse response = apiExecutor.execute(params, null);
		if (!response.isSuccess()) {
			String msg = "Failed to get Gstr2 ISD from GSTN";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(msg);
			}
			JsonObject resp1 = new JsonObject();
			resp1.add("hrd", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("End of findIsdIsdaDataAtGstn Response: {}"
					+ response.getResponse());
		}
		return response.getResponse();
	}

}
*/