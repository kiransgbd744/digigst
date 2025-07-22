package com.ey.advisory.common.counter.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.counter.CounterExecControlParams;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */

@Slf4j
@Component("GSTNPublicApiCounterServiceImpl")
public class GSTNPublicApiCounterServiceImpl
		implements GSTNPublicApiCounterService {

	@Autowired
	private CounterExecControlParams cntrlParams;

	@Autowired
	private GSTNPublicApiLimitFetcher defLimitFetcher;

	// Returns Limits assigned to particular group as a DTO
	@Override
	public PublicApiLimitDTO getLimitsForGroupCode(String groupCode) {

		try {
			if (cntrlParams.getLimitMap().containsKey(groupCode)) {

				Map<Integer, Integer> limitMap = new TreeMap<>(
						cntrlParams.getLimitMap().get(groupCode));

				return getLimits(limitMap, groupCode);
			} else {
				LOGGER.error("No Data for groupcode {}", groupCode);
				return null;
			}

		} catch (Exception e) {
			String msg = "Error while getting Details";
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage());
		}

	}

	// Saves Limits assigned to particular group as a DTO
	@Override
	public String saveLimitsForGroupCode(PublicApiLimitDTO dto) {

		try {

			String groupCode = dto.getGroupCode();
			String response = null;

			/* If user submits empty limits on UI, clear the limits assigned */
			// Start//
			if (dto.getLimits().isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request for deleting limits group {}",
							groupCode);
				}

				if (cntrlParams.getLimitMap().containsKey(groupCode)) {

					cntrlParams.getLimitMap().remove(groupCode);
					response = String.format("Limits cleared for %s",
							groupCode);
					return response;
				} else {
					response = String.format("No limits to be cleared for %s",
							groupCode);
					return response;
				}
			}
			// End//

			/* if user has sumitted limits save it as map */
			cntrlParams.getLimitMap().put(groupCode,
					createLimits(dto.getLimits()));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"SaveLimit request executed successfully for group {}",
						groupCode);
			}
			return "Data saved successfully";

		} catch (Exception e) {
			String msg = "Error while saving details";
			LOGGER.error(msg, e);
			throw new AppException(msg);

		}
	}

	/*
	 * Below method checks for given group code whether the public api call can
	 * be happened. And increments the usage.
	 */
	@Override
	public boolean isApiCallAllowedForGroupCode(String groupCode) {
		Integer limit = null;
		Integer usage = null;
		Integer day = getDayOfMonth();
		try {
			/*
			 * Check whether the grouCode exist and get the usage,else increment
			 * usage for the day
			 */
			if (cntrlParams.getUsageMap().containsKey(groupCode)) {
				Map<Integer, Integer> usageMap = cntrlParams.getUsageMap()
						.get(groupCode);

				if (usageMap.containsKey(day)) {
					usage = usageMap.get(day);
				} else {
					cntrlParams.getUsageMap().get(groupCode).put(day, 1);
					return true;
				}

			} else {
				Map<Integer, Integer> usageMap = new HashMap<>();
				usageMap.put(day, 1);
				cntrlParams.getUsageMap().put(groupCode, usageMap);
				return true;

			}
			/* Fetch the Default or assigned limit for a groupCode */
			if (cntrlParams.getLimitMap().containsKey(groupCode)) {

				Map<Integer, Integer> limitMap = cntrlParams.getLimitMap()
						.get(groupCode);

				if (limitMap.containsKey(day)) {
					limit = limitMap.get(day);
				} else {
					limit = defLimitFetcher.getDefaultLimit(groupCode);
				}
			} else {
				limit = defLimitFetcher.getDefaultLimit(groupCode);
			}

			/*
			 * Return false if usage has crossed the limits,else increment the
			 * usage and return true
			 */
			if (usage + 1 > limit) {
				return false;
			} else {
				cntrlParams.getUsageMap().get(groupCode).put(day, usage + 1);
				return true;
			}

		} catch (Exception e) {
			String msg = "Error while getting Api allowed details";
			LOGGER.error(msg, e);
			throw new AppException(msg);

		}

	}

	/* Returns usage and Limits for a given group code and day */
	@Override
	public Pair<Integer, Integer> getUsageAndLimitForGroupCode(
			String groupCode) {
		Integer limit = null;
		Integer usage = null;
		Integer day = getDayOfMonth();

		try {
			if (cntrlParams.getUsageMap().containsKey(groupCode)) {
				Map<Integer, Integer> usageMap = cntrlParams.getUsageMap()
						.get(groupCode);

				if (usageMap.containsKey(day)) {
					usage = usageMap.get(day);
				} else {
					usage = 0;
				}

			} else {
				usage = 0;

			}

			if (cntrlParams.getLimitMap().containsKey(groupCode)) {

				Map<Integer, Integer> limitMap = cntrlParams.getLimitMap()
						.get(groupCode);

				if (limitMap.containsKey(day)) {
					limit = limitMap.get(day);
				} else {
					limit = defLimitFetcher.getDefaultLimit(groupCode);
				}
			} else {
				limit = defLimitFetcher.getDefaultLimit(groupCode);
			}

			return new Pair<>(limit, usage);
		} catch (Exception e) {
			String msg = "Error while getting UsageAndLimit details";
			LOGGER.error(msg, e);
			throw new AppException(msg);

		}

	}

	/* Decrements the usage count if public call fails at GSP layer */
	@Override
	public void decrementUsageCountForGroupCode(String groupCode) {
		Integer usage = null;

		try {
			Integer day = getDayOfMonth();
			if (cntrlParams.getUsageMap().containsKey(groupCode)) {
				Map<Integer, Integer> usageMap = cntrlParams.getUsageMap()
						.get(groupCode);

				if (usageMap.containsKey(day)) {
					usage = usageMap.get(day);
					cntrlParams.getUsageMap().get(groupCode).put(day,
							usage - 1);
				} else {
					String errMsg = String.format(
							"Usage count for groupCode %s"
									+ " is zero hence no decrement for the day %d",
							groupCode, day);
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}

			} else {
				String errMsg = String.format("GroupCode doesn't exist to "
						+ "decrement the count for %s", groupCode);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);

			}
		} catch (Exception e) {
			String msg = String.format(
					"Error while decrementing Usage count for %s", groupCode);
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage());

		}

	}

	private Map<Integer, Integer> createLimits(List<LimitDTO> limits) {
		Map<Integer, Integer> limitMap = new TreeMap<>();
		for (LimitDTO limit : limits) {
			Integer fromDate = limit.getFromDate();
			Integer toDate = limit.getToDate();
			Integer limitCount = limit.getLimit();
			List<Integer> dateList = getDatesAsList(fromDate, toDate);
			dateList.forEach(date -> limitMap.put(date, limitCount));
		}
		return limitMap;
	}

	private List<Integer> getDatesAsList(Integer fromDate, Integer toDate) {
		IntStream stream = IntStream.rangeClosed(fromDate, toDate);
		return stream.boxed().collect(Collectors.toList());

	}

	private Integer getDayOfMonth() {

		LocalDateTime ist = EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.now());

		return ist.getDayOfMonth();

	}

	private PublicApiLimitDTO getLimits(Map<Integer, Integer> limitMap,
			String groupCode) {

		PublicApiLimitDTO dto = new PublicApiLimitDTO();
		List<LimitDTO> limitList = new ArrayList<>();

		try {
			String fy = GenUtil.getCurrentFinancialYear();
			List<Integer> result = limitMap.keySet().stream()
					.collect(Collectors.toList());

			Optional<Integer> firstKey = limitMap.keySet().stream().findFirst();
			Integer firstDate = null;
			Integer firstDateLimit = null;

			if (firstKey.isPresent()) {
				firstDate = firstKey.get();
				firstDateLimit = limitMap.get(firstDate);

			} else {
				LOGGER.error("No Data for groupcode {} in getLimits",
						groupCode);
				return null;
			}

			List<Integer> dateComb = new ArrayList<>();

			for (Integer date : result) {

				if (date.equals(firstDate)) {
					dateComb.add(date);
					continue;
				}

				if (date.equals(firstDate + 1)
						&& firstDateLimit.equals(limitMap.get(date))) {
					dateComb.add(date);
					firstDate = date;
				} else {

					creatJson(dateComb, firstDateLimit, limitList, fy);
					dateComb.clear();
					dateComb.add(date);
					firstDate = date;
					firstDateLimit = limitMap.get(date);
				}

			}
			creatJson(dateComb, firstDateLimit, limitList, fy);
			dto.setGroupCode(groupCode);
			dto.setLimits(limitList);

		} catch (Exception e) {

			String msg = "Error while parsing Details";
			LOGGER.error(msg, e);
			throw new AppException(msg);

		}
		return dto;
	}

	private void creatJson(List<Integer> dateComb, Integer limit,
			List<LimitDTO> limitList, String fy) {

		LimitDTO limitDto = new LimitDTO();
		limitDto.setFromDate(dateComb.get(0));
		limitDto.setToDate(dateComb.get(dateComb.size() - 1));
		limitDto.setLimit(limit);
		limitDto.setFy(fy);
		limitList.add(limitDto);
	}

}
