/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.ProcessingStatus;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.repositories.master.GstnAPIGroupConfigRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("UrlListProcessorImpl")
public class UrlListProcessorImpl implements UrlListProcessor {

	@Autowired
	@Qualifier("ProcessTokenURLPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private GstnAPIGroupConfigRepository apiConfigRepo;

	@Autowired
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	private GstnAPIGstinConfigRepository gstnApiGstnConfigRepo;

	@Override
	public Pair<ExecResult<Boolean>, Map<String, Object>> processTasksForUrls(
			List<String> urls, APIParams apiParams, String rek, String sk,
			Long requestId, Map<String, Object> retryCtxMap) {
		ExecResult<Boolean> execResult = null;
		try {
			int noOfTasks = urls.size();
			Map<String, String> headerMap = getHeadersMap(apiParams);
			headerMap.put("groupCode", TenantContext.getTenantId());
			CountDownLatch latch = new CountDownLatch(noOfTasks);
			ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap = createTasksMap(
					urls);
			LOGGER.debug(
					"No.of URLS which are ready for processing {} and URLS are {}",
					noOfTasks, urls);
			List<Runnable> tasks = urls.stream()
					.map(url -> new ExecTokenTask(tasksMap, latch, url, rek, sk,
							requestId, headerMap))
					.collect(Collectors.toCollection(ArrayList::new));

			tasks.forEach(task -> execPool.execute(task));

			// Wait for all jobs to get completed.
			try {
				latch.await();
			} catch (InterruptedException ex) {
				String errMsg = "Exception while awaiting for URL Process to complete";
				LOGGER.error(errMsg, ex);
				Thread.currentThread().interrupt();
			}

			// Check for failed tasks from the pairs stored in the map.
			long failedTasks = tasksMap.entrySet().stream()
					.filter((entry) -> entry.getValue()
							.getValue0() != ProcessingStatus.COMPLETED)
					.count();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("NO OF FAILED TASK IS {}", failedTasks);
				LOGGER.debug("TaskMap is  {}", tasksMap);
			}

			Exception firstError = null;
			if (failedTasks > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Found atleast one failed URL,"
							+ " Hence Marking the Job as Failed");
				}
				firstError = tasksMap.entrySet().stream()
						.filter(entry -> entry.getValue().getValue1() != null)
						.findFirst().get() // Get can be called directly as we
											// know it exists.
						.getValue().getValue1();
				retryCtxMap.put(GstnApiWrapperConstants.URL_FETCH_ERROR,
						firstError);
				execResult = ExecResult.errorResult(
						GstnApiWrapperConstants.GSTN_ERROR,
						GstnApiWrapperConstants.URL_FETCH_ERROR,
						firstError.getMessage());
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Url processing is successfull, creating the"
							+ " success execution result");
				}
				execResult = ExecResult.successResult(true,
						"url processor executed");
			}

		} catch (Exception e) {
			LOGGER.error("Exception while Processing the Token URL's", e);
			execResult = ExecResult.errorResult(ExecErrorCodes.UNKNOWN_FAILURE,
					"Exception Occured", e);
		} finally {
			retryCtxMap.put(RetryMapKeysConstants.FETCH_URL_RESULT, execResult);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Before Returning, ExecResult is {},"
					+ " retryCtxMap is {}", execResult, retryCtxMap);
		}
		return new Pair<>(execResult, retryCtxMap);

	}

	private ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> createTasksMap(
			List<String> urls) {
		ConcurrentHashMap<String, Pair<ProcessingStatus, Exception>> tasksMap = new ConcurrentHashMap<>();
		urls.forEach((url) -> tasksMap.put(
				APIInvokerUtil.resolveProcessingTokenUrl(url),
				new Pair<ProcessingStatus, Exception>(
						ProcessingStatus.NOT_STARTED, null)));
		return tasksMap;
	}

	private Map<String, String> getHeadersMap(APIParams apiparams) {
		Map<String, String> map = new HashMap<>();
		GstnAPIGroupConfig apiGroupConfig = apiConfigRepo
				.findByGroupCode(TenantContext.getTenantId());
		String gstin = apiparams.getAPIParamValue("gstin");
		String returnPeriod = apiparams.getAPIParamValue("ret_period");
		String stCd = gstin.substring(0, 2);
		GstnAPIAuthInfo apiAuthInfo = gstinAPIAuthInfoRepository
				.findByGstinAndProviderName(gstin, APIProviderEnum.GSTN.name());
		GstnAPIGstinConfig gstnApiGstinConfig = gstnApiGstnConfigRepo
				.findByGstin(gstin);
		map.put(GstnApiWrapperConstants.DIGI_GST_USERNAME,
				apiGroupConfig.getDigiGstUserName());
		map.put(GstnApiWrapperConstants.ACCESS_TOKEN,
				apiGroupConfig.getGspToken());
		map.put(GstnApiWrapperConstants.API_KEY, apiGroupConfig.getApiKey());
		map.put(GstnApiWrapperConstants.API_SECRET,
				apiGroupConfig.getApiSecret());
		map.put(GstnApiWrapperConstants.IP_USER, "ip.usr");
		map.put(GstnApiWrapperConstants.AUTH_TOKEN, apiAuthInfo.getGstnToken());
		map.put(GstnApiWrapperConstants.USER_NAME,
				gstnApiGstinConfig.getGstinUserName());
		map.put(GstnApiWrapperConstants.STATE_CODE, stCd);
		map.put(GstnApiWrapperConstants.TXN, "returns");
		map.put(GstnApiWrapperConstants.GSTIN, gstin);
		map.put(GstnApiWrapperConstants.RETURN_PERIOD, returnPeriod);
		map.put(GstnApiWrapperConstants.CONTENT_TYPE,
				"application/json;charset=UTF-8");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("the request headers map for url download {} ", map);
		}
		return map;
	}
}
