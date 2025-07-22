/**
 * 
 */
package com.ey.advisory.processors.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.APIExecEventsLogger;
import com.ey.advisory.gstnapi.APIInvocationError;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.APILogger;
import com.ey.advisory.gstnapi.CallHandlers;
import com.ey.advisory.gstnapi.ExecResult;
import com.ey.advisory.gstnapi.FileNumListProcessor;
import com.ey.advisory.gstnapi.GstnApiWrapperConstants;
import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("RetryFailedFileChunkResponseProcessor")
public class RetryFailedFileChunkResponseProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("CallHandlersImpl")
	private CallHandlers callHandlers;

	@Autowired
	private APIExecEventsLogger logger;

	@Autowired
	APIResponseRepository apiRespRepo;

	@Autowired
	@Qualifier("FileNumListProcessorImpl")
	private FileNumListProcessor fileProcesor;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void execute(Message message, AppExecContext context) {

		Gson gson = new Gson();
		Map<String, Object> retryCtxMap = new HashMap<>();
		try {
			String jsonString = message.getParamsJson();
			JsonObject requestParams = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject reqEntityObj = requestParams.get("requestEntity")
					.getAsJsonObject();
			APIInvocationReqEntity req = gson.fromJson(reqEntityObj,
					APIInvocationReqEntity.class);
			Long invocationId = req.getId();
			String failedFileNums = requestParams.get("failedFileNums")
					.getAsString();
			JsonObject paramsObj = JsonParser
					.parseString(reqEntityObj.get("apiParams").getAsString())
					.getAsJsonObject();
			APIParams apiParams = new Gson()
					.fromJson(paramsObj.get("apiParams"), APIParams.class);
			int retryCount = requestParams.get("retryCount").getAsInt();

			List<String> failedFileNumList = Arrays
					.stream(failedFileNums.split(","))
					.collect(Collectors.toList());
			retryCtxMap.put("isRetry", true);
			ExecResult<Boolean> fileProcessorResult = fileProcesor
					.processTasksForUrls(failedFileNumList, apiParams,
							invocationId, retryCtxMap)
					.getValue0();

			if (fileProcessorResult.isSuccess()) {
				logger.logInfo(invocationId, "All the files are processed "
						+ "successfully, Calling success handler");
				List<Long> resultIds = apiRespRepo
						.getResultIdByInvocationId(invocationId);
				callSuccessHandler(req, resultIds, retryCtxMap);
			} else if (fileProcessorResult.getFailedFileNums() != null
					&& !fileProcessorResult.getFailedFileNums().isEmpty()
					&& retryCount < 4) {
				LOGGER.error(
						"Processing Failed for following File Number {} for Invocation Id {} GroupCode {} ",
						fileProcessorResult.getFailedFileNums(), req.getId(),
						TenantContext.getTenantId());
				JsonObject jsonParams = new JsonObject();
				String failedFileNumsString = String.join(",",
						fileProcessorResult.getFailedFileNums());
				jsonParams.add("requestEntity", reqEntityObj);
				jsonParams.addProperty("failedFileNums", failedFileNumsString);
				jsonParams.addProperty("retryCount", retryCount + 1);
				asyncJobsService.createJob(TenantContext.getTenantId(),
						"RetryFailedFileChunkJob", jsonParams.toString(),
						"SYSTEM", 50L, null, 5L);
			} else {
				logger.logInfo(invocationId,
						"processing of files failed, Calling failure handler");
				apiRespRepo.updateRequestStatus(invocationId,
						GstnApiWrapperConstants.FAILED);
				APIInvocationError error = createApiInvocationError(
						fileProcessorResult);

				callFailureHandler(invocationId, error, req.getFailureHandler(),
						req.getApiParams(), retryCtxMap);
			}
		} catch (Exception e) {
			String msg = String.format(
					"Error occured in InvocationReqProcessor "
							+ "for jobId = %d with message [%s]",
					message.getId(), e.getMessage());
			APILogger.logError(msg, null, e);
			LOGGER.error(msg, e);
			throw new AppException(e);
		}
	}

	private ExecResult<Boolean> callSuccessHandler(
			APIInvocationReqEntity requestEntity, List<Long> ids,
			Map<String, Object> retryCtxMap) {
		return callHandlers
				.callSuccessHandler(requestEntity.getApiParams(), ids,
						requestEntity.getId(),
						requestEntity.getSuccessHandler(), retryCtxMap)
				.getValue0();

	}

	private <T> APIInvocationError createApiInvocationError(
			ExecResult<T> execResult) {
		return APIInvokerUtil.createErrorObject(execResult.getErrMsg(),
				execResult.getExecErrCode(), execResult.getExtErrCode(),
				execResult.getException());

	}

	private ExecResult<Boolean> callFailureHandler(Long requestId,
			APIInvocationError error, String failureHandler, String apiParams,
			Map<String, Object> retryCtxMap) {
		return callHandlers.callFailureHandler(requestId, error, failureHandler,
				apiParams, retryCtxMap).getValue0();

	}

}
