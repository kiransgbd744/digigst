/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;

/**
 * @author Khalid1.Khan
 *
 */
public class UpdateInvocationIdImpl implements UpdateInvocationId {

	@Autowired
	APIInvocationReqRepository aiReqRepo;

	@Override
	public Pair<ExecResult<Boolean>, Map<String, Object>> updateINvocationId(
			Long invocationId, Long requestId,
			Map<String, Object> retryCtxMap) {
		ExecResult<Boolean> execResult = null;
		try {
			if (aiReqRepo.updateRefReqId(invocationId, requestId)) {
				execResult = ExecResult.successResult(true,
						"invocationIdUpdateSuccessfull");
			} else {
				execResult = ExecResult.errorResult(
						ExecErrorCodes.INVOC_STATUS_UPDATE_FAILURE,
						"Updation of the invocationId to the request "
						+ "table is failed");
			}
		} catch (Exception ex) {
			execResult = ExecResult.errorResult(
					ExecErrorCodes.INVOC_STATUS_UPDATE_FAILURE,
					"Updation of the invocationId to the request "
					+ "table is failed",ex);

		} finally {
			retryCtxMap.put(RetryMapKeysConstants.HANDLER_VERIFICATION,
					execResult);	
		}
		return new Pair<>(
				execResult, retryCtxMap);
	}

}
