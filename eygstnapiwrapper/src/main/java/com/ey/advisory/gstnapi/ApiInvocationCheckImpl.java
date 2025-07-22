/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;

/**
 * @author Khalid1.Khan
 *
 */
@Component("ApiInvocationCheckImpl")
public class ApiInvocationCheckImpl implements ApiInvocationCheck {
	
	
	@Autowired
	private APIInvocationReqRepository reqRepo;
	
	@Autowired
	private APIExecEventsLogger logger;

	@Override
	public Pair<ExecResult<Long>, Map<String, Object>> IsAlreadyInvoked(
			APIInvocationReqEntity requestEntity,
			Map<String, Object> retryCtxMap) {
		ExecResult<Long> execResult = null;
		try {
			List<Long> ids = reqRepo.apiParamExistCheck(
					requestEntity.getApiParamsHashValue());
			if (!ids.isEmpty()) {
				execResult = ExecResult.successResult(ids.get(0),
						"This Api is Already "
						+ "invoked with the same api params");
			} else {
				execResult = ExecResult.errorResult(
						ExecErrorCodes.INVOC_EXISTENCE_CHK_FAILURE,
						"There is no successfull invocation history for "
								+ "the given api params");
			}
		} catch (Exception ex) {
			logger.logError("checking of invocation existence failed",
					ExecErrorCodes.INVOC_EXISTENCE_CHK_FAILURE);
			execResult = ExecResult.errorResult(
					ExecErrorCodes.INVOC_EXISTENCE_CHK_FAILURE, "", ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.INVOCATION_ID, execResult);
			
		}
		return new Pair<>(execResult,
				retryCtxMap);
	}

}
