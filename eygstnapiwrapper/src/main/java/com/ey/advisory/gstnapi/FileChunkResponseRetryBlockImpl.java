
package com.ey.advisory.gstnapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("FileChunkResponseRetryBlockImpl")
public class FileChunkResponseRetryBlockImpl implements RetryBlock {

	@Autowired
	@Qualifier("FileNumListProcessorImpl")
	private FileNumListProcessor fileProcesor;

	@Autowired
	@Qualifier("CallHandlersImpl")
	private CallHandlers callHandlers;

	@Autowired
	private APIExecEventsLogger logger;

	@Autowired
	APIResponseRepository apiRespRepo;

	@Autowired
	private LoadInvocationRequest loadInvocReq;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public Pair<Pair<ExecResult<Boolean>, ExecResult<Boolean>>, Map<String, Object>> retry(
			String params) {

		ExecResult<Boolean> callHandlerExecResult = null;
		Map<String, Object> retryCtxMap = new HashMap<>();
		Gson gson = new Gson();
		JsonObject obj =JsonParser.parseString(params).getAsJsonObject();
		JsonObject reqEntityObj = obj.get("requestEntity").getAsJsonObject();
		APIInvocationReqEntity req = gson.fromJson(reqEntityObj,
				APIInvocationReqEntity.class);
		int fileCount = obj.get("fileCount").getAsInt();
		String msg = String.format("Begining the FileChunk response processing "
				+ "with File params %d", fileCount);
		logger.logInfo(req.getId(), msg);
		loadInvocReq.loadInvocationRequest(req.getId(), retryCtxMap);
		List<String> fileNumList = prepareFileNumToBeFetched(fileCount);
		logger.logInfo(req.getId(), "FileNum prepared successfull");
		logger.logInfo(req.getId(), "size of the url is " + fileNumList.size());
		JsonObject paramsObj = JsonParser
				.parseString(reqEntityObj.get("apiParams").getAsString())
				.getAsJsonObject();
		APIParams apiParams = new Gson().fromJson(paramsObj.get("apiParams"),
				APIParams.class);
		logger.logInfo(req.getId(), "processing of files begin");
		ExecResult<Boolean> fileProcessorResult = fileProcesor
				.processTasksForUrls(fileNumList, apiParams, req.getId(),
						retryCtxMap)
				.getValue0();
		logger.logInfo(req.getId(), "processing of files completed");
		if (fileProcessorResult.isSuccess()) {
			logger.logInfo(req.getId(), "All the files are processed "
					+ "successfully, Calling success handler");
			List<Long> resultIds = apiRespRepo
					.getResultIdByInvocationId(req.getId());
			callHandlerExecResult = callSuccessHandler(req, resultIds,
					retryCtxMap);
		} else if (fileProcessorResult.getFailedFileNums() != null
				&& !fileProcessorResult.getFailedFileNums().isEmpty()) {
			LOGGER.error(
					"Processing Failed for following File Number {} for Invocation id {} and GroupCode {} ",
					fileProcessorResult.getFailedFileNums(), req.getId(),
					TenantContext.getTenantId());
			JsonObject jsonParams = new JsonObject();
			String failedFileNumsString = String.join(",",
					fileProcessorResult.getFailedFileNums());
			jsonParams.add("requestEntity", reqEntityObj);
			jsonParams.addProperty("failedFileNums", failedFileNumsString);
			jsonParams.addProperty("retryCount", 1);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					"RetryFailedFileChunkJob", jsonParams.toString(), "SYSTEM",
					50L, null, 5L);

			callHandlerExecResult = ExecResult.successResult(true,
					"Retry Handler Called Successfully");

		} else {
			logger.logInfo(req.getId(),
					"processing of files failed, Calling failure handler");
			apiRespRepo.updateRequestStatus(req.getId(),
					GstnApiWrapperConstants.FAILED);
			APIInvocationError error = createApiInvocationError(
					fileProcessorResult);

			callHandlerExecResult = callFailureHandler(req.getId(), error,
					req.getFailureHandler(), req.getApiParams(), retryCtxMap);
		}
		ExecResult<Boolean> defaultResult = ExecResult.successResult(true,
				"SuccessResponse");
		return new Pair<>(
				new Pair<>(defaultResult.toBoolResult(), callHandlerExecResult),
				retryCtxMap);

	}

	private List<String> prepareFileNumToBeFetched(int fileCount) {
		return IntStream.rangeClosed(1, fileCount).boxed().map(String::valueOf)
				.collect(Collectors.toList());
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
