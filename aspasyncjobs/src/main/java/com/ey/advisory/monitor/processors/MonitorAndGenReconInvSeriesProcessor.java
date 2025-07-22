package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.services.einvseries.GSTR1EinvSeriesCompService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorAndGenReconInvSeriesProcessor")
public class MonitorAndGenReconInvSeriesProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("EinvSeriRecPattServiceImpl")
	private GSTR1EinvSeriesCompService invServiceImpl;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		List<String> taxPeriods = new ArrayList<>();
		List<AsyncExecJob> invSerAsyncJobs = new ArrayList<>();
		try {
			List<String> activeGstinsList = gstNDetailRepository
					.getActiveGstins();
			LocalDateTime currDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());
			int currDay = currDate.getDayOfMonth();
			taxPeriods.add(GenUtil.getCurrentAndPrevTaxPeriod().getValue0());
			if (currDay >= 1 && currDay < 20) {
				taxPeriods
						.add(GenUtil.getCurrentAndPrevTaxPeriod().getValue1());
			}
			List<List<String>> gstnChunks = Lists.partition(activeGstinsList,
					10);
			for (List<String> gstnList : gstnChunks) {
				for (String taxPeriod : taxPeriods) {
					postEinvSerAsync(gstnList, taxPeriod, invSerAsyncJobs);
				}
			}
			if (!invSerAsyncJobs.isEmpty()) {
				asyncJobsService.createJobs(invSerAsyncJobs);
			}
		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	private void postEinvSerAsync(List<String> gstinList, String retPeriod,
			List<AsyncExecJob> invSerAsyncJobs) {
		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Active Gstin List {} ", gstinList);
		}
		String gstins = String.join(",", gstinList);
		JsonObject jobParamsObj = new JsonObject();
		jobParamsObj.addProperty("retPeriod", retPeriod);
		jobParamsObj.addProperty("gstins", gstins);
		invSerAsyncJobs.add(asyncJobsService.createJobAndReturn(groupCode,
				JobConstants.INITIATE_RECON_EINV_SERIES,
				jobParamsObj.toString(), "SYSTEM", 1L, null, null));
	}
}
