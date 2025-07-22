/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.ProcessingStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("FileNumListProcessorImpl")
public class FileNumListProcessorImpl implements FileNumListProcessor {
	
	@Autowired
	@Qualifier("ProcessTokenURLPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private APIExecEventsLogger logger;

	@Override
	public Pair<ExecResult<Boolean>, Map<String, Object>> processTasksForUrls(
			List<String> fileNums, APIParams apiParams, Long requestId,
			Map<String, Object> retryCtxMap) {
		ExecResult<Boolean> execResult = null;
		try {
			boolean isRetry = (boolean) retryCtxMap.getOrDefault("isRetry",
					false);
			int noOfTasks = fileNums.size();
			logger.logInfo(requestId,
					String.format(
							"FileNums to be processed are %s for retry %s",
							fileNums, isRetry));
			CountDownLatch latch = new CountDownLatch(noOfTasks);
			ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap = createTasksMap(
					fileNums);

			List<Runnable> tasks = fileNums.stream()
					.map(fileNum -> new ExecFileResponseTask(fileNum, tasksMap,
							latch, apiParams, requestId,
							TenantContext.getTenantId()))
					.collect(Collectors.toCollection(ArrayList::new));
			logger.logInfo(requestId,
					String.format(
							"Prepared Runnable File Tasks,"
									+ " About to submit to Threadpool for retry %s",
							isRetry));
			tasks.forEach(task -> execPool.execute(task));
			logger.logInfo(requestId,
					String.format(
							"Submitted %d Tasks to ThreadPool,"
									+ " Waiting for Threads to Complete the execution for retry %s",
							tasks.size(), isRetry));
			// Wait for all jobs to get completed.
			try {
				latch.await();
			} catch (InterruptedException ex) {
				String errMsg = "Exception while awaiting"
						+ " for File URL Process to complete";
				LOGGER.error(errMsg, ex);
				Thread.currentThread().interrupt();
			}
			logger.logInfo(requestId,
					"All Runnable File Tasks are completed its execution");
			// Check for failed tasks from the pairs stored in the map.
			long failedTasks = tasksMap.entrySet().stream()
					.filter((entry) -> entry.getValue()
							.getValue0() != ProcessingStatus.COMPLETED)
					.count();

			Exception firstError = null;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Total TaskMap is  {}", tasksMap);
				LOGGER.debug("NO OF FAILED TASK IS {}", failedTasks);
			}
			if (failedTasks > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Found atleast one failed FileNum,"
							+ " Hence Marking the Job as Failed");
				}
				// Get can be called directly as we know it exists.
				firstError = tasksMap.entrySet().stream()
						.filter(entry -> entry.getValue().getValue1() != null)
						.findFirst().map(entry -> entry.getValue().getValue1())
					    .orElseThrow(() -> new IllegalStateException
					    		("No matching entry found in tasksMap"));
				retryCtxMap.put(GstnApiWrapperConstants.FILE_FETCH_ERROR,
						firstError);
				List<String> failedFileNums = tasksMap.entrySet().stream()
						.filter((entry) -> entry.getValue()
								.getValue0() != ProcessingStatus.COMPLETED)
						.map(fileNum -> fileNum.getKey())
						.collect(Collectors.toList());
				execResult = ExecResult.errorResult(
						GstnApiWrapperConstants.GSTN_ERROR,
						GstnApiWrapperConstants.FILE_FETCH_ERROR,
						firstError.getMessage());
				execResult.setFailedFileNums(failedFileNums);
				LOGGER.error("Failed File Num {}", failedFileNums);
			} else {
				execResult = ExecResult.successResult(true,
						"File Response processor executed");
			}

		} catch (Exception e) {
			LOGGER.error("Exception while Processing the GSTN FileTokens", e);
			execResult = ExecResult.errorResult(ExecErrorCodes.UNKNOWN_FAILURE,
					"Exception Occured", e);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.FETCH_FILE_RESULT,
					execResult);
		}
		return new Pair<>(execResult, retryCtxMap);

	}

	private ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> createTasksMap(
			List<String> fileNums) {
		ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap = new ConcurrentHashMap<>();
		fileNums.forEach((fileNum) -> tasksMap.put(fileNum,
				new Pair<ProcessingStatus, Exception>(
						ProcessingStatus.NOT_STARTED, null)));
		return tasksMap;
	}

}
