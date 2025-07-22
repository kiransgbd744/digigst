package com.ey.advisory.app.inward.einvoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.ims.handlers.GetIrnJsonProcessor;
import com.ey.advisory.app.ims.handlers.GetIrnJsonResponseProcessor;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetIrnListB2bSuccessHandler")
@Slf4j
public class GetIrnListB2bSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("DefaultGetIrnResponseProcessor")
	private GetIrnJsonProcessor defaultFactoryImpl;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		List<Long> resultIds = result.getSuccessIds();
		String ctxParams = result.getCtxParams();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
		        .getAsJsonObject();
		Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
		        Gstr1GetInvoicesReqDto.class);
		batchId = dto.getBatchId();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
				        "GET Call is Success for the batchId {} inside "
				                + "GetIrnListB2bSuccessHandler.java with dto {} ",
				        batchId, dto);
			}
			GetIrnJsonResponseProcessor processorType = defaultFactoryImpl
			        .getProcessorType(resultIds);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calling processorType::{}",
				        processorType.getClass().getName());
			}
			processorType.processJsonResponse(dto.getGstin(),
			        dto.getReturnPeriod(), batchId, resultIds, false,
			        dto.getType(), dto);

		} catch (Exception ex) {
			LOGGER.error(
			        "Error while parsing in GetIrnListB2bSuccessHandler handler {} ",
			        ex);

			batchUtil.updateById(batchId, APIConstants.FAILED, null,
			        "Error in GetIrnListB2bSuccessHandler", false);
			throw new AppException();
		}
	}

}