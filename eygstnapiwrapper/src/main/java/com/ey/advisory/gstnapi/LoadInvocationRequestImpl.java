/**
 * 
 */
package com.ey.advisory.gstnapi;

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
@Component("LoadInvocationRequestImpl")
public class LoadInvocationRequestImpl implements LoadInvocationRequest{
	@Autowired
	APIInvocationReqRepository aiReqRepo;
	
	@Autowired
	private APIExecEventsLogger logger;

	public Pair<ExecResult<APIInvocationReqEntity>, Map<String, Object>>
			loadInvocationRequest(Long id, Map<String, Object> retryCtxMap) {
		APIInvocationReqEntity apiReqEntity = null;
		ExecResult<APIInvocationReqEntity> execResult = null;
		try {
			apiReqEntity = aiReqRepo.findById(id).orElseThrow(
					() -> new Exception("requested entity does not exist"));
			execResult = ExecResult.successResult(apiReqEntity,
					"InvocationRequestLoaded");
			logger.logInfo(id, "InvocationRequest Loaded Successfully");
		} catch (Exception ex) {
			logger.logError("InvocationRequest Loading Failed", 
					ExecErrorCodes.INVOCATION_REQ_LOAD_FAILURE);
			execResult = ExecResult.errorResult(
					ExecErrorCodes.INVOCATION_REQ_LOAD_FAILURE,
					"InvocationRequest Loading Failed", ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.API_INVOCATION_REQ_ENTITY,
					apiReqEntity);	
		}
		return new Pair<>(execResult, retryCtxMap);
	}

}
