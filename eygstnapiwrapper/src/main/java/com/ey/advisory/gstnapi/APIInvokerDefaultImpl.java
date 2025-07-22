package com.ey.advisory.gstnapi;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("APIInvokerDefaultImpl")
@Slf4j
public class APIInvokerDefaultImpl implements APIInvoker {
	
	@Autowired
	private APIInvocationReqRepository reqRepo;
	
	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("GstinGetStatusServiceImpl")
	private GstinGetStatusService gstinGetStatusService;
	

	@Override
	public APIInvocationResult invoke(APIParams params, String reqData,
			String successHandler, String failureHandler, String ctxParams) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to create invocation Request with params " 
			+ params);
		}
		Gson gson = new Gson();
		JsonElement apiParams =  gson.toJsonTree(params);
		JsonObject ctxObj = new JsonParser().parse(ctxParams).getAsJsonObject();
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("apiParams", apiParams);
		jsonObj.add("ctxParams", gson.toJsonTree(ctxObj));
		APIInvocationReqEntity invocReq = createInvocReq(params,
				jsonObj.toString(), successHandler, failureHandler);
		String userName = GstnApiWrapperConstants.SYSTEM;
		String groupCode = TenantContext.getTenantId();
		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("requestId",invocReq.getId() );
		
		//gstinGetStatusService.saveOrUpdateGSTNGetStatus(apiParams.toString(), "INITIATED", null);
		
		createJob(invocReq.getId(), groupCode,
				GstnApiJobConstants.PROCESS_GSTN_CALL,
				jsonParams.toString(), userName,
				GstnApiJobConstants.DEFAULT_JOB_PRIORITY);
		return new APIInvocationResult(invocReq.getId());
	}


	private AsyncExecJob createJob(Long reqId, String groupCode, String jobName,
			String params, String userName, long priority) {
		try {
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					jobName, params, userName, priority, null, null);
			String msg = String.format("A job is created to "
					+ "process the request with job id = %d", job.getJobId());
			APILogger.logInfo(reqId, msg);
			return job;
		} catch (Exception e) {
			String msg = String.format("Failed to create Invocation Job (%s) "
					+ "for reqId = %d", jobName, reqId);
			APILogger.logError(reqId, msg);
			throw e;
		}
		
	}
	
	private APIInvocationReqEntity createInvocReq(APIParams apiParams,
			String params, String successHandler, String failureHandler) {
		Gson gson = new Gson();
		String apiParamsHash = Hashing.sha256()
				  .hashString(gson.toJson(params), StandardCharsets.UTF_8)
				  .toString();
		APIInvocationReqEntity invocationReqEntity = new APIInvocationReqEntity(
				apiParams.getAPIParamValue("gstin"), 
				apiParams.getApiIdentifier(),
				params, apiParamsHash,
				GstnApiWrapperConstants.SUBMITTED,
				successHandler, failureHandler,
				"SYSTEM", LocalDateTime.now(), "SYSTEM", LocalDateTime.now());	
		APIInvocationReqEntity reqEntity = reqRepo.save(invocationReqEntity);
		String msg = String.format("Invoc Req created with reqId = %d And Api"
				+ " Id = '%s'", invocationReqEntity.getId(),
				invocationReqEntity.getApiIdentifier());
		APILogger.logInfo(invocationReqEntity.getId(), msg);
		return reqEntity;
		
	}

}
