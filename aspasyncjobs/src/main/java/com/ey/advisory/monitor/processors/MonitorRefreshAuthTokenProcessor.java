package com.ey.advisory.monitor.processors;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.ey.advisory.common.RefreshAuthTokenHandler;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;

@Component("MonitorRefreshAuthTokenProcessor")
public class MonitorRefreshAuthTokenProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MonitorRefreshAuthTokenProcessor.class);

	@Autowired
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	@Qualifier("EyAsyncMiscIOPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private RefreshAuthTokenHandler authTokenHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {

			Date expiryStTime = new Date(); //10 am
			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(expiryStTime); // sets calendar time/date //8 am
			cal.add(Calendar.HOUR_OF_DAY, 2); // adds 2 hour// 10 am
			Date expiryEndTime = cal.getTime();
			List<GstnAPIAuthInfo> refreshableAuthInfoList = gstinAPIAuthInfoRepository
					.findAllByProviderNameInAndGstnTokenExpiryTimeLessThanEqual(
							Arrays.asList(APIProviderEnum.EINV.name(),
									APIProviderEnum.EYEINV.name(),
									APIProviderEnum.EWB.name()),
							expiryEndTime, Sort.by(Sort.Direction.DESC,
									"gstnTokenExpiryTime"));

			if (refreshableAuthInfoList == null
					|| refreshableAuthInfoList.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("There are no eligible Gstin's for refresh");
				}
				return;
			}
			authTokenHandler.refreshAuthTokenHandler(refreshableAuthInfoList,
					message);

		} catch (Exception ex) {
			String msg = "MultithreadedGroupsTaskProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

}
