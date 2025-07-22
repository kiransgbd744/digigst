package com.ey.advisory.task.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyInvocControlRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EwbStatusInputDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.ewb.data.entities.clientBusiness.CounterPartyInvocControlEntity;
import com.ey.advisory.gstnapi.repositories.client.EWBNICUserRepository;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 * 
 */

@Slf4j
@Component("PostManualCounterPartyEWBProcessor")
public class PostManualCounterPartyEWBProcessor implements TaskProcessor {

	List<String> statusList = ImmutableList.of("FAILED");

	@Autowired
	CounterPartyInvocControlRepository counterPartyInvocControlRepo;

	@Autowired
	EWBNICUserRepository ewbNICUserRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		List<AsyncExecJob> getContParEwbAsyncJob = new ArrayList<>();
		try {
			EwbStatusInputDto criteria = gson.fromJson(json,
					EwbStatusInputDto.class);
			if (criteria.getGstins().isEmpty()
					&& Strings.isNullOrEmpty(criteria.getFromdate())
					&& Strings.isNullOrEmpty(criteria.getToDate())) {
				String msg = "GSTINs, From Date and To Date are mandatory to execute the job.";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			List<String> gstinList = criteria.getGstins();
			LocalDate fromDate = LocalDate.parse(criteria.getFromdate());
			LocalDate toDate = LocalDate.parse(criteria.getToDate());

			boolean isEligibletoPost = isSixMonthsApart(fromDate, toDate);

			if (!isEligibletoPost) {
				String msg = "From Date and To Date Difference is more than 6 months, So Job will not run";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			List<LocalDate> dates = new ArrayList<>();
			if (fromDate.isBefore(toDate)) {
				while (fromDate.isBefore(toDate)) {
					dates.add(fromDate);
					fromDate = fromDate.plusDays(1);
				}
				dates.add(fromDate);
			} else {
				String msg = "From Date and To Date Criteria Not Met, So Job will not run";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			for (LocalDate ewbDate : dates) {
				List<CounterPartyInvocControlEntity> counterEntities = counterPartyInvocControlRepo
						.findByEwbGenDate(ewbDate);
				List<CounterPartyInvocControlEntity> eligibleEntities = counterEntities
						.stream()
						.filter(o -> statusList.contains(o.getStatus()))
						.collect(Collectors.toList());
				if (!eligibleEntities.isEmpty()) {
					List<String> counterGstins = counterEntities.stream()
							.map(o -> o.getGstin())
							.collect(Collectors.toList());
					eligibleEntities.forEach(o -> o.setStatus("POSTED"));
					counterPartyInvocControlRepo.saveAll(eligibleEntities);
					gstinList.removeAll(counterGstins);
				}
				List<CounterPartyInvocControlEntity> saveEntities = gstinList
						.stream().map(o -> saveToControlTable(o, ewbDate))
						.collect(Collectors.toList());
				if (!saveEntities.isEmpty()) {
					saveEntities = counterPartyInvocControlRepo
							.saveAll(saveEntities);
				}
				saveEntities.addAll(eligibleEntities);
				for (CounterPartyInvocControlEntity entity : saveEntities) {
					String gstin = entity.getGstin();
					JsonObject jobParams = new JsonObject();
					jobParams.addProperty("controlId", entity.getId());
					jobParams.addProperty("gstin", gstin);
					jobParams.addProperty("date", ewbDate.toString());
					getContParEwbAsyncJob.add(asyncJobsService
							.createJobAndReturn(TenantContext.getTenantId(),
									"GetCounterPartyEWB", jobParams.toString(),
									"SYSTEM", 1L, null, null));
				}
			}

			if (!getContParEwbAsyncJob.isEmpty()) {
				List<List<AsyncExecJob>> jobChunks = Lists
						.partition(getContParEwbAsyncJob, 30);
				for (List<AsyncExecJob> jobChunk : jobChunks) {
					asyncJobsService.createJobs(jobChunk);
				}
			}

		} catch (Exception e) {
			String msg = "Exception occured while posting manual ewb counter party";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	private CounterPartyInvocControlEntity saveToControlTable(String gstin,
			LocalDate ewbDate) {

		CounterPartyInvocControlEntity obj = new CounterPartyInvocControlEntity();

		obj.setGstin(gstin);
		obj.setStatus("POSTED");
		obj.setCreatedBy("SYSTEM");
		obj.setCreatedOn(LocalDateTime.now());
		obj.setRevIntStatus("NOT_INITIATED");
		obj.setEwbGenDate(EYDateUtil.toISTDateTimeFromUTC(ewbDate));

		return obj;
	}

	private static boolean isSixMonthsApart(LocalDate fromDate,
			LocalDate ToDate) {

		Period period = Period.between(fromDate, ToDate);

		System.out.println(period.toTotalMonths());
		return period.toTotalMonths() <= 6;
	}

}
