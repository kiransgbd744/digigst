/*package com.ey.advisory.app.services.jobs.anx1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx1.Anx1GetInvoicesReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

*//**
 * 
 * @author Anand3.M
 *
 *//*

@Service("Anx1GetSezwpFailureHandler")
@Slf4j
public class Anx1GetSezwpFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Failed for the batchId {} inside "
								+ "Anx1GetSezwpFailureHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Anx1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Anx1GetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
					APIConstants.FAILED);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
	}
}
*/