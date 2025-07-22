package com.ey.advisory.app.services.jobs.ret;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("RetGetCommonFailureHandler")
public class RetGetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Failed for the batchId {} inside "
								+ "RetGetCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			RetGetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					RetGetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
					APIConstants.FAILED, errorCode, errorDesc, false);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}
	}
}
