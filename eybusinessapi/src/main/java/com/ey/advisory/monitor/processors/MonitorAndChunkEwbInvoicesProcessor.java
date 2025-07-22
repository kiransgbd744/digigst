package com.ey.advisory.monitor.processors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.MonitorCommonUtility;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("MonitorAndChunkEwbInvoicesProcessor")
public class MonitorAndChunkEwbInvoicesProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private MonitorCommonUtility monCommUtility;

	final List<String> apiType = ImmutableList
			.of(BusinessCriticalConstants.GENEWB_V3);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		try {
			monCommUtility.postCanorGenEwbJob(apiType, group, "auto.drafting.chunkSize.genewb");
		} catch (Exception ex) {
			String msg = "Exception occured in periodic job of BcAPI";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}
}
