/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

/**
 * @author Khalid1.Khan
 *
 */
@Component("FailureBlockImpl")
public class FailureBlockImpl implements FailureBlock{

	@Autowired
	APIInvocationReqRepository aiReqRepo;

	@Autowired
	APIResponseRepository apiRespRepo;

	@Override
	public boolean failureTask(ExecResult<Boolean> execResult,
			ExecResult<Boolean> handlerResult, Map<String, Object> map) {
		APIInvocationReqEntity requestEntity = map.containsKey(
				RetryMapKeysConstants.API_INVOCATION_REQ_ENTITY)
				? (APIInvocationReqEntity)map.get(
						RetryMapKeysConstants.API_INVOCATION_REQ_ENTITY)
				: null;
		if(requestEntity != null){
			aiReqRepo.updateStatus(GstnApiWrapperConstants.FAILED,
					requestEntity.getId());
			return true;
		}
		return false;
		
	}

}
