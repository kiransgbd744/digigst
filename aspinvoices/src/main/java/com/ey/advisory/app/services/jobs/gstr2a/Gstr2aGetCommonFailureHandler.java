/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr2aGetCommonFailureHandler")
@Slf4j
public class Gstr2aGetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510", "RET11416", "RET11417","RTNECO-01");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Failed for the batchId {} inside "
								+ "Gstr2aGetCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Long batchId = ctxParamsObj.get("batchId").getAsLong();
			LOGGER.error(
					"Error Code {} for Batch Id {} for GroupCode {} ",
					errorCode, batchId, TenantContext.getTenantId());
			if (ERROR_CODES.contains(errorCode)) {
				LOGGER.error(
						"Inside Success with No Data block for Batch Id {} for GroupCode {} ",
						batchId, TenantContext.getTenantId());
				
				batchUtil.updateById(batchId, APIConstants.SUCCESS_WITH_NO_DATA,
						errorCode, errorDesc, false);
			} else {
				LOGGER.error(
						"Inside Failed block for Batch Id {} for GroupCode {} ",
						batchId, TenantContext.getTenantId());
				batchUtil.updateById(batchId,
						APIConstants.FAILED, errorCode, errorDesc, false);
			}
		} catch (Exception e) {
			String errMsg = "Exception while marking the Status of GSTR2A in batch Table";
			LOGGER.error(errMsg, e);
			throw new AppException(e);
		}
	}
}
