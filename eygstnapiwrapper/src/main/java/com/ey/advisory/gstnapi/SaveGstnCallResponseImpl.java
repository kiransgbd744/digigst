/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

/**
 * @author Khalid1.Khan
 *
 */
@Component("SaveGstnCallResponseImpl")
public class SaveGstnCallResponseImpl implements SaveGstnCallResponse {

	@Autowired
	APIResponseRepository apiRespRepo;

	@Override
	public Pair<ExecResult<APIResponseEntity>, Map<String, Object>> saveResponse(
			APIResponse apiresp, Long refReqId,
			Map<String, Object> retryCtxMap) {
		ExecResult<APIResponseEntity> execResult = null;
		try {
			Clob responseClob = new javax.sql.rowset.serial.SerialClob(
					apiresp.getResponse().toCharArray());
			APIResponseEntity responseEntity = new APIResponseEntity(
					refReqId, responseClob,
					apiresp.isSuccess() ? "SUCCESS" : "FAILED",
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now(),
					GstnApiWrapperConstants.SYSTEM, LocalDateTime.now());
			apiRespRepo.save(responseEntity);
			execResult = ExecResult.successResult(responseEntity,
					"Response Entity Saved");

		} catch (Exception ex) {
			execResult = ExecResult.errorResult(
					ExecErrorCodes.API_RESP_PERSIST_FAILURE,
					"failed to save the response", ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.RESPONSE_ENTITY, execResult);
		}
		return new Pair<>(execResult, retryCtxMap);
	}

}
