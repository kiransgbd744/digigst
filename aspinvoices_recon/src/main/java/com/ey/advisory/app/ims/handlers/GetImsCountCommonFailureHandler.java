package com.ey.advisory.app.ims.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr2AGetInvoicesReqDto;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetImsCountCommonFailureHandler")
@Slf4j
public class GetImsCountCommonFailureHandler implements FailureHandler {

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
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET IMS Count is Failed for the batchId {} inside "
								+ "GetImsCountCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Gstr2AGetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr2AGetInvoicesReqDto.class);
		
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.FAILED, errorCode, errorDesc, false);
			
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new AppException(e);
		}
	}
}
