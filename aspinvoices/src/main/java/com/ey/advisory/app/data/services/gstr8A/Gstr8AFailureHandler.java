/**
 * 
 */
package com.ey.advisory.app.data.services.gstr8A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Service("Gstr8AFailureHandler")
@Slf4j
public class Gstr8AFailureHandler implements FailureHandler {
	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510", "RET11416", "RET11417", "RET2B1013",
			"RET2B1016", "RET2B1017", "RET2B1018", "RET2B1023", "RET2B1015",
			"RT-9AG-1019");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {

		String errorCode = result.getError().getErrorCode();
		String errorDesc = result.getError().getErrorDesc();
		String ctxParams = result.getCtxParams();
		JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
				.getAsJsonObject();
		String gstin = ctxParamsObj.get("gstin").getAsString();
		String fy = ctxParamsObj.get("fy").getAsString();
		Long batchId = ctxParamsObj.get("batchId").getAsLong();
		String[] fyArr = fy.split("-");
		String taxPeriod = "0320" + fyArr[1];
		
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET Call is Failed for the batchId {} inside "
						+ "Gstr8A Failure Handler", batchId);
			}
			if (ERROR_CODES.contains(errorCode)) {
				gstinGetStatusRepo.updateGetGstnStatus(false, APIConstants.SUCCESS_WITH_NO_DATA,
						LocalDateTime.now(), errorDesc, gstin, taxPeriod,
						APIConstants.GSTR8A, "GET8A");
				batchUtil.updateById(batchId, APIConstants.SUCCESS_WITH_NO_DATA,
						errorCode, errorDesc, false);
			} else {
				gstinGetStatusRepo.updateGetGstnStatus(false, APIConstants.FAILED,
						LocalDateTime.now(), errorDesc, gstin, taxPeriod,
						APIConstants.GSTR8A, "GET8A");
				batchUtil.updateById(batchId, APIConstants.FAILED, errorCode,
						errorDesc, false);
			}
		} catch (Exception ex) {
			gstinGetStatusRepo.updateGetGstnStatus(false, APIConstants.FAILED,
					LocalDateTime.now(), ex.getMessage().length() > 500
							? ex.getMessage().substring(0, 499)
							: ex.getMessage(), gstin, taxPeriod,
					APIConstants.GSTR8A, "GET8A");
			batchUtil.updateById(batchId, APIConstants.FAILED, null,
					ex.getMessage(), false);
			LOGGER.error("Exception while handling GSTR2BFailureHandler", ex);
			throw new APIException(ex.getLocalizedMessage());
		}
	}

}
