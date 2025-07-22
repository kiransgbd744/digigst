/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;

/**
 * @author Khalid1.Khan
 *
 */
@Component("LoadResponseIdsImpl")
public class LoadResponseIdsImpl  implements LoadResponseIds{
	
	@Autowired
	APIResponseRepository apiRespRepo;

	@Override
	public Pair<ExecResult<List<Long>>, Map<String, Object>> 
	loadInvocationReponseIds(
			Long refReqId, Map<String, Object> retryCtxMap) {
		ExecResult<List<Long>> execResult = null;
		try {
			List<Long> successIds = 
					apiRespRepo.getResultIdByInvocationId
					(refReqId);
			if(!successIds.isEmpty()){
				execResult = ExecResult.successResult(successIds,"This Api is"
						+ " Already invoked with the same api params");
			}
			else{
				execResult = ExecResult.errorResult(
						ExecErrorCodes.LOAD_RESPONSE_IDS_FAILURE,
						"failed to fetch response ids");
			}
		} catch (Exception ex) {
			execResult = ExecResult.errorResult(
					ExecErrorCodes.LOAD_RESPONSE_IDS_FAILURE,
					"failed to fetch response ids",ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.SUCCESS_IDS,
					execResult);
		}
		return new Pair<>(
				execResult, retryCtxMap);

	}

}
