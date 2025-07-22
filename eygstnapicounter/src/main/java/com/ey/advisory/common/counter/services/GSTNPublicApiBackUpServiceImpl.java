package com.ey.advisory.common.counter.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.counter.CounterExecControlParams;
import com.ey.advisory.core.async.domain.master.GSTNPublicAPISummaryEntity;
import com.ey.advisory.core.async.repositories.master.GSTNPublicAPISummaryRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("GSTNPublicApiBackUpServiceImpl")
public class GSTNPublicApiBackUpServiceImpl
		implements GSTNPublicApiBackUpService {

	@Autowired
	private GSTNPublicAPISummaryRepository summaryRepo;

	@Autowired
	private CounterExecControlParams cntrlParams;

	/*
	 * Below method is responsible for creating entity list from Limits and
	 * Usage map and persist in DB. Same Table is being used to store both the
	 * maps. TYPE column is the distinguisher.
	 */
	@Override
	@Transactional(value = "masterTransactionManager")
	public void createBackUp() {
		List<GSTNPublicAPISummaryEntity> entityListToBackUp = new ArrayList<>();

		try {

			if (!cntrlParams.getLimitMap().isEmpty()) {

				cntrlParams.getLimitMap()
						.forEach((k, v) -> v.forEach((a, b) -> {
							GSTNPublicAPISummaryEntity e = new GSTNPublicAPISummaryEntity();
							e.setGroupCode(k);
							e.setDay(a);
							e.setCount(b.longValue());
							e.setActive(true);
							e.setType("LIMIT");
							e.setCreatedOn(LocalDateTime.now());
							entityListToBackUp.add(e);
						})

				);
			} else {
				LOGGER.error("No Limits to do back up at {}",
						LocalDateTime.now().toString());
			}

			if (!cntrlParams.getUsageMap().isEmpty()) {

				cntrlParams.getUsageMap()
						.forEach((k, v) -> v.forEach((a, b) -> {
							GSTNPublicAPISummaryEntity e = new GSTNPublicAPISummaryEntity();
							e.setGroupCode(k);
							e.setDay(a);
							e.setCount(b.longValue());
							e.setActive(true);
							e.setType("USAGE");
							e.setCreatedOn(LocalDateTime.now());
							entityListToBackUp.add(e);
						})

				);

			} else {
				LOGGER.error("No Usage to do back up at {}",
						LocalDateTime.now().toString());

			}

			// soft delete all the active records before persist
			int softDeletecount = summaryRepo.softDeleteActiveRecords();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Total number of softeDeleted records are {} at {}",
						softDeletecount, LocalDateTime.now());
			}

			if (!entityListToBackUp.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Total number of  records backing up are {} at {}",
							entityListToBackUp.size(), LocalDateTime.now());
				}

				summaryRepo.saveAll(entityListToBackUp);
			}
		} catch (Exception e) {
			String errMsg = "Error while doing Limits and Usage BackUp ";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	/* Below method loads the backed up data from DB */
	@Override
	public void loadBackUp() {

		List<GSTNPublicAPISummaryEntity> limitEntityList = summaryRepo
				.findByTypeAndIsActiveTrue("LIMIT");

		List<GSTNPublicAPISummaryEntity> usageEntityList = summaryRepo
				.findByTypeAndIsActiveTrue("USAGE");

		if (limitEntityList.isEmpty() && usageEntityList.isEmpty()) {
			LOGGER.warn(
					"Limits and Usage entities are empty to Load the back up at {} ",
					LocalDateTime.now());
			return;
		}
		Map<String, Map<Integer, Integer>> limitsMap = new HashMap<>();
		Map<String, Map<Integer, Integer>> usageMap = new HashMap<>();

		// Create usage Map
		usageEntityList.forEach(e -> {
			// check group already exists else make put
			if (usageMap.containsKey(e.getGroupCode())) {
				usageMap.get(e.getGroupCode()).put(e.getDay(),
						e.getCount().intValue());
			} else {
				Map<Integer, Integer> dayWiseUsageMap = new HashMap<>();
				dayWiseUsageMap.put(e.getDay(), e.getCount().intValue());
				usageMap.put(e.getGroupCode(), dayWiseUsageMap);
			}

		});

		if (!usageMap.isEmpty()) {
			cntrlParams.getUsageMap().putAll(usageMap);
		}

		// Create limit Map
		limitEntityList.forEach(e -> {

			if (limitsMap.containsKey(e.getGroupCode())) {

				limitsMap.get(e.getGroupCode()).put(e.getDay(),
						e.getCount().intValue());
			} else {
				Map<Integer, Integer> dayWiseLimitMap = new TreeMap<>();
				dayWiseLimitMap.put(e.getDay(), e.getCount().intValue());
				limitsMap.put(e.getGroupCode(), dayWiseLimitMap);
			}
		});
		if (!limitsMap.isEmpty()) {
			cntrlParams.getLimitMap().putAll(limitsMap);
		}

	}

}
