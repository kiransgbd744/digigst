package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr2AGetInvoicesReqDto;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetIrnListCommonFailureHandler")
@Slf4j
public class GetIrnListCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private static final List<String> ERROR_CODES = ImmutableList.of("EINV30107",
			"EINV30108");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET IRN List is Failed for the batchId {} inside "
								+ "GetIrnListCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Gstr2AGetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr2AGetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			if (ERROR_CODES.contains(errorCode)) {
				//TODO
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.SUCCESS_WITH_NO_DATA, errorCode, errorDesc, false);
			} else if ("EINV30130".equalsIgnoreCase(errorCode)) {

				LOGGER.error("Received EINV30130 as response from GSTN");
			}else {
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.FAILED, errorCode, errorDesc, false);
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new AppException(e);
		}
	}
}
