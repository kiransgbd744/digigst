/**
 * 
 */
package com.ey.advisory.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.einv.app.api.EINVAPIExecutor;
import com.ey.advisory.ewb.app.api.EWBAPIExecutor;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.tasks.ExecTask;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("RefreshAuthTokenHandler")
public class RefreshAuthTokenHandler {

	@Autowired
	@Qualifier("EINVAPIExecutor")
	EINVAPIExecutor einvAPIExecutor;

	@Autowired
	@Qualifier("EWBAPIExecutor")
	EWBAPIExecutor ewbAPIExecutor;

	@Autowired
	@Qualifier("EyAsyncMiscIOPool")
	private ThreadPoolTaskExecutor execPool;

	public void refreshAuthTokenHandler(
			List<GstnAPIAuthInfo> refreshableAuthInfoList, Message message) {
		try {
			int noOfTasks = refreshableAuthInfoList.size();
			List<GstnAPIAuthInfo> ewbGstinAuthInfo = refreshableAuthInfoList
					.stream()
					.filter(o -> o.getProviderName()
							.equalsIgnoreCase(APIProviderEnum.EWB.name()))
					.collect(Collectors.toCollection(ArrayList::new));
			List<GstnAPIAuthInfo> einvGstinAuthInfo = refreshableAuthInfoList
					.stream()
					.filter(o -> !o.getProviderName()
							.equalsIgnoreCase(APIProviderEnum.EWB.name()))
					.collect(Collectors.toCollection(ArrayList::new));

			CountDownLatch latch = new CountDownLatch(noOfTasks);
			List<Runnable> tasks = new ArrayList<>();

			tasks.addAll(einvGstinAuthInfo.stream()
					.map(authInfo -> new ExecTask(message, authInfo.getGstin(),
							authInfo.getGroupCode(), latch, null,
							einvAPIExecutor, authInfo.getProviderName()))
					.collect(Collectors.toCollection(ArrayList::new)));

			tasks.addAll(ewbGstinAuthInfo.stream()
					.map(authInfo -> new ExecTask(message, authInfo.getGstin(),
							authInfo.getGroupCode(), latch, ewbAPIExecutor,
							null, authInfo.getProviderName()))
					.collect(Collectors.toCollection(ArrayList::new)));

			tasks.forEach(task -> {
				execPool.execute(task);
			});
			// Wait for all jobs to get completed.
			awaitForTasksToComplete(latch);
		} catch (Exception ex) {
			String msg = "MultithreadedGroupsTaskProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

	private void awaitForTasksToComplete(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			String msg = "MultithreadedGroupsTaskProcessor got interrupted."
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
}