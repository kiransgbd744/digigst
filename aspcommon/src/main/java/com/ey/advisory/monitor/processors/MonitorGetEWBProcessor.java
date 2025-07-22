/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyEwbBillRepository;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.ewb.data.entities.clientBusiness.CounterPartyEwbBillEntity;
import com.ey.advisory.processing.message.GetCounterPartyMsg;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 * 
 */
@Slf4j
@Component("MonitorGetEWBProcessor")
public class MonitorGetEWBProcessor extends DefaultMultiTenantTaskProcessor {

	@Autowired
	CounterPartyEwbBillRepository counterPartyEwbBillRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Started Monitoring GETEWB for group {}",
					group.getGroupCode());
		Pageable pageReq = PageRequest.of(0, 150, Direction.ASC, "id");

		List<String> statusList = Arrays.asList("FAILED", "NOT_INITIATED");

		List<CounterPartyEwbBillEntity> getCounterPartyList = counterPartyEwbBillRepo
				.findByFetchStatusIn(statusList, pageReq);

		List<List<CounterPartyEwbBillEntity>> ewbChunks = Lists
				.partition(getCounterPartyList, 50);

		for (List<CounterPartyEwbBillEntity> ewbList : ewbChunks) {

			postEwbGetJob(ewbList, group.getGroupCode());

		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Completed Monitoring GETEWB for group {}",
					group.getGroupCode());
	}

	private void postEwbGetJob(List<CounterPartyEwbBillEntity> ewbList,
			String groupCode) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		List<GetCounterPartyMsg> msgList = ewbList.stream()
				.map(o -> convertToMsg(o)).collect(Collectors.toList());

		asyncJobsService.createJob(groupCode, "GETEWB", gson.toJson(msgList).toString(),
				"SYSTEM", 1L, null, null);

		List<Long> idList = ewbList.stream().map(o -> o.getId())
				.collect(Collectors.toList());

		counterPartyEwbBillRepo.updateEWBStatus(idList);

	}

	private GetCounterPartyMsg convertToMsg(CounterPartyEwbBillEntity o) {

		GetCounterPartyMsg obj = new GetCounterPartyMsg();
		obj.setEwbNo(o.getEwbNo());
		obj.setDate(o.getEwbGenDate());
		obj.setGstin(o.getClientGstin());

		return obj;
	}

}
