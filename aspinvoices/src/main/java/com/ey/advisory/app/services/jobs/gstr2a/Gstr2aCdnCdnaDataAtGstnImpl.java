/*package com.ey.advisory.app.services.jobs.gstr2a;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr2aCdnInvoicesAtGstnReqDto;
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

	@Component("gstr2aCdnCdnaDataAtGstnImpl")
	public class Gstr2aCdnCdnaDataAtGstnImpl implements Gstr2aCdnCdnaDataAtGstn {


		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr2aCdnCdnaDataAtGstnImpl.class);

		@Autowired
		@Qualifier("DefaultNonStubExecutor")
		private APIExecutor apiExecutor;
		
		
		
		@Override
		public String findCdnCdnaDataAtGstn(Gstr2aCdnInvoicesAtGstnReqDto dto,
				String groupCode, String type) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"I am from findCdnCdnaDataAtGstn " + " Group Code: ",
						groupCode);
			}

			APIParam param1 = new APIParam("gstin", dto.getGstin());
			APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
			APIParams params = null;
			if (APIConstants.CDN.equals(type)) {
				params = new APIParams(groupCode, APIProviderEnum.GSTN,
						APIIdentifiers.GSTR2A_GET_CDN, param1, param2);
			} else {
				params = new APIParams(groupCode, APIProviderEnum.GSTN,
						APIIdentifiers.GSTR2A_GET_CDNA, param1, param2);
			}
			APIResponse response = apiExecutor.execute(params, null);
			if (!response.isSuccess()) {
				String msg = "Failed to get Gstr2 CDN from GSTN";
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error(msg);
				}
				JsonObject resp1 = new JsonObject();
				resp1.add("hrd", new Gson().toJsonTree(new APIRespDto("E", msg)));
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End of findCdnCdnaDataAtGstn Response: {}"
						+ response.getResponse());
			}
			return response.getResponse();
		}



}
*/