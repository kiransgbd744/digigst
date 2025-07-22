package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.AzureSapGroupMappingRepository;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.tasks.ExecTask;

@Component("RefreshAuthTokenProcessor")
public class RefreshAuthTokenProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RefreshAuthTokenProcessor.class);

	@Autowired
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	@Qualifier("DefaultGSTNAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("EyAsyncMiscIOPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private AzureSapGroupMappingRepository azureSapGroupRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			List<String> azureGroupCodes = azureSapGroupRepo
					.getDistinctGroupCodes();

			Date expiryStTime = new Date();
			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(expiryStTime); // sets calendar time/date
			cal.add(Calendar.HOUR_OF_DAY, 3); // adds 3 hour
			Date expiryEndTime = cal.getTime();
			List<GstnAPIAuthInfo> refreshableAuthInfoList = gstinAPIAuthInfoRepository
					.findAllByProviderNameAndGroupCodeNotInAndGstnTokenExpiryTimeBetween(
							APIProviderEnum.GSTN.name(), azureGroupCodes,
							expiryStTime, expiryEndTime,
							Sort.by(Sort.Direction.DESC,
									"gstnTokenExpiryTime"));
			
			if (refreshableAuthInfoList == null
					|| refreshableAuthInfoList.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("There are no eligible Gstin's for refresh");
				}
				return;
			}
			int noOfTasks = refreshableAuthInfoList.size();
			CountDownLatch latch = new CountDownLatch(noOfTasks);
			List<Runnable> tasks = refreshableAuthInfoList.stream()
					.map(authInfo -> new ExecTask(message, authInfo.getGstin(),
							authInfo.getGroupCode(), latch, apiExecutor))
					.collect(Collectors.toCollection(ArrayList::new));
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
