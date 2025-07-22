package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyInvocControlRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.ewb.data.entities.clientBusiness.CounterPartyInvocControlEntity;
import com.ey.advisory.gstnapi.repositories.client.EWBNICUserRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 * 
 */

@Slf4j
@Component("MonitorCounterPartyEWBProcessor")
public class MonitorCounterPartyEWBProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	CounterPartyInvocControlRepository counterPartyInvocControlRepo;

	@Autowired
	EWBNICUserRepository ewbNICUserRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			LocalDate ewbGenDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now()).minusDays(1);
			List<CounterPartyInvocControlEntity> counterEntities = counterPartyInvocControlRepo
					.findByEwbGenDate(ewbGenDate);

			List<String> statusList = ImmutableList.of("FAILED");

			List<CounterPartyInvocControlEntity> eligibleEntities = counterEntities
					.stream().filter(o -> statusList.contains(o.getStatus()))
					.collect(Collectors.toList());

			List<String> counterGstins = counterEntities.stream()
					.map(o -> o.getGstin()).collect(Collectors.toList());

			eligibleEntities.forEach(o -> o.setStatus("POSTED"));

			if (!eligibleEntities.isEmpty())
				counterPartyInvocControlRepo.saveAll(eligibleEntities);

			List<String> distGstin = ewbNICUserRepo.getDistinctGstins();

			distGstin.removeAll(counterGstins);

			List<CounterPartyInvocControlEntity> saveEntities = distGstin
					.stream().map(o -> saveToControlTable(o))
					.collect(Collectors.toList());

			if (!saveEntities.isEmpty())
				saveEntities = counterPartyInvocControlRepo
						.saveAll(saveEntities);

			saveEntities.addAll(eligibleEntities);

			for (CounterPartyInvocControlEntity entity : saveEntities) {

				String gstin = entity.getGstin();

				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("controlId", entity.getId());
				jobParams.addProperty("gstin", gstin);
				jobParams.addProperty("date", ewbGenDate.toString());
				asyncJobsService.createJob(group.getGroupCode(),
						"GetCounterPartyEWB", jobParams.toString(), "SYSTEM",
						1L, null, null);
			}
		} catch (Exception e) {
			String msg = "Exception occured while monitoring ewb counter party";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	private CounterPartyInvocControlEntity saveToControlTable(String gstin) {

		CounterPartyInvocControlEntity obj = new CounterPartyInvocControlEntity();

		obj.setGstin(gstin);
		obj.setStatus("POSTED");
		obj.setCreatedBy("SYSTEM");
		obj.setCreatedOn(LocalDateTime.now());
		obj.setRevIntStatus("NOT_INITIATED");
		obj.setEwbGenDate(
				EYDateUtil.toISTDateTimeFromUTC(LocalDate.now()).minusDays(1));

		return obj;
	}

}
