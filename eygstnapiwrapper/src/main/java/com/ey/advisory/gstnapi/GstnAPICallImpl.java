/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Khalid1.Khan
 *
 */
@Component("GstnAPICallImpl")
public class GstnAPICallImpl implements GstnApiCall{
	
	@Autowired
	APIResponseRepository apiRespRepo;
	
	@Autowired
	@Qualifier("DefaultGSTNAPIExecutor")
	APIExecutor apiExecutor;



	public Pair<ExecResult<APIResponse>, Map<String, Object>> callGstnApi(
			String apiParams, Map<String, Object> retryCtxMap) {
		ExecResult<APIResponse> execResult = null;
		try {
			JsonObject apiParamsJson = (new JsonParser()).parse(apiParams)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIParams params = gson.fromJson(apiParamsJson.get("apiParams"),
					APIParams.class);
			APIResponse apiresp = apiExecutor.execute(params, "");
			if(apiresp.isSuccess()){
				execResult = ExecResult.successResult(apiresp, "SuccessResponse");
			}
			else{
				execResult = ExecResult.errorResult(
						apiresp.getError().getErrorCode(),
						apiresp.getError().getErrorCode()
						+apiresp.getError().getErrorDesc());
			}
		} catch (Exception ex) {
			execResult = ExecResult.errorResult(
					ExecErrorCodes.UNKNOWN_FAILURE,
					"Exception Occured inside gstn api call: "
					+ ex.getMessage() ,ex);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.RESPONSE_ENTITY,
					execResult);
		}
		return new Pair<>(
				execResult, retryCtxMap);

	}

	
	
	
	

}
