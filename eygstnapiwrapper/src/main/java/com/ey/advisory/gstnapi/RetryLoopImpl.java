/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("RetryLoopImpl")
public class RetryLoopImpl implements RetryLoop {

	@Autowired
	@Qualifier("SuccessBlockImpl")
	private SuccessBlock successBlock;
	
	@Autowired
	@Qualifier("FailureBlockImpl")
	private FailureBlock failureBlock;

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	
	public void retryLoop(String retryBlockName, String params) {
		
		Gson gson = new Gson();
		JsonObject inpJson = (new JsonParser()).parse(params)
				.getAsJsonObject();
		Long reqId = inpJson.get("requestId").getAsLong();
		
		RetryProcessAbstractFactory factory = getFactory(retryBlockName, reqId);
		if(factory == null ) return ;
		while (true) {
			Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>, Map<String,
			Object>> executionResult = factory
					.getRetryBlock().retry(params);
			Map<String, Object> ctxRetryMap = executionResult.getValue1();
			ExecResult<Boolean> execResult = executionResult.getValue0()
					.getValue0();
			ExecResult<Boolean> handlerResult = executionResult.getValue0()
					.getValue1();

			RetryInfo retryInfo = (inpJson.has("retryInfo"))
					? gson.fromJson(inpJson.get("retryInfo").getAsJsonObject(),
							RetryInfo.class)
					: new RetryInfo(0, 0, LocalDateTime.now());
			if (execResult.isSuccess() && handlerResult.isSuccess()) {
				APILogger.logInfo(reqId, "the request is successfully "
						+ "processed,now calling the success block");
				if(successBlock.successTask(
						execResult, handlerResult,
						ctxRetryMap))
					APILogger.logInfo(reqId, "success block executed "
							+ "successfully");
				else 
					APILogger.logInfo(reqId, "success block execution failed ");
				break;
			} else {
				Pair<String, Boolean> failureAction = 
						performFailure(reqId, retryBlockName, retryInfo,
						ctxRetryMap, execResult, handlerResult, factory);
				params = failureAction.getValue0();
				if (!params.isEmpty() && params != null)
					inpJson = (new JsonParser()).parse(params)
							.getAsJsonObject();
				if (failureAction.getValue1())
					continue;
				break;
			}
		}
	}


	private RetryInfo updateRetryInfo(RetryInfo retryInfo,
			RetryAction retryAction) {
		return new RetryInfo(retryAction.isImmediateRetry()
				? retryInfo.getImmediateRetryCount() + 1 
						: retryInfo.getImmediateRetryCount() ,
				retryAction.isDelayedRetry()
					? retryInfo.getDelayedRetryCount() + 1 
					: retryInfo.getDelayedRetryCount(),
				retryInfo.getInitialExecAt());

	}

	
	
	private void createJobForRetry(String params) {
		asyncJobsService.createJob(TenantContext.getTenantId(),
				GstnApiJobConstants.PROCESS_GSTN_CALL,
				params, "", 1L, null, 1L);
	}
	
	
	
	private RetryProcessAbstractFactory getFactory(String retryBlockName,
			Long reqId) {
		try {
			return RetryProcessAbstractFactory
					.of(retryBlockName);
		} catch (Exception e) {
			String msg = String.format("Failed to load factory for the retry "
					+ "block = '%s' with requestId = %d", retryBlockName,
					reqId);
			APILogger.logError(reqId, msg, null, e);
			return null;	
		}
	}
	
	
	
	private String createInputJson(Long requestId, RetryInfo retryInfo) {
		Gson gson = new Gson();
		JsonObject newInpJson = new JsonObject();
		newInpJson.add("retryInfo", gson.toJsonTree(retryInfo));
		newInpJson.addProperty("requestId",
				requestId);
		return newInpJson.toString();
	}
	
	
	
	private String performImmediateRetry(RetryInfo retryInfo,
			RetryAction retryAction, Long reqId) {
		retryInfo = updateRetryInfo(retryInfo, retryAction);
		APILogger.logInfo(reqId,
				"executing immedite retry  with" + "retry info " + retryInfo);
		return createInputJson(reqId, retryInfo);
	}
	
	
	
	private String performDelayedRetry(RetryInfo retryInfo,
			RetryAction retryAction, Long reqId) {
		APILogger.logInfo(reqId, "executing delayed retry  with"
				+ "retry info " + retryInfo);
		retryInfo = updateRetryInfo(retryInfo, retryAction);
		return createInputJson(reqId, retryInfo);
	}
	
	
	
	private void executeFailureBlock(Long reqId, ExecResult<Boolean> execResult,
			ExecResult<Boolean> handlerResult,
			Map<String, Object> ctxRetryMap) {
		APILogger.logInfo(reqId, "marking the request as failed");
		failureBlock.failureTask(execResult, handlerResult,
				ctxRetryMap);
		
	}
	
	private Pair<String, Boolean> performFailure(Long reqId,
			String retryBlockName, 
			RetryInfo retryInfo, 
			Map<String, Object> ctxRetryMap,
			ExecResult<Boolean> execResult, 
			ExecResult<Boolean> handlerResult,
			RetryProcessAbstractFactory factory) {
		String params = "";
		RetryAction retryAction = factory.getPolicyManager()
				.evaluteRetryPolicy(retryBlockName, retryInfo,
						ctxRetryMap, execResult, handlerResult);
		APILogger.logInfo(reqId, "fetching the retry action from "
				+ "policy manager, retryAction " + retryAction);
		if (retryAction.isImmediateRetry()) {
			params = performImmediateRetry(retryInfo, retryAction,
					reqId);
			return new Pair<>(params, true);
		} else if (retryAction.isDelayedRetry()) {
			params = performDelayedRetry(retryInfo, retryAction, reqId);
			createJobForRetry(params);
			return new Pair<>(params, false);
		} else if (retryAction.isFailure()) {

			executeFailureBlock(reqId, execResult, handlerResult,
					ctxRetryMap);
			
		}
		return new Pair<>(params, false);
	}

}
