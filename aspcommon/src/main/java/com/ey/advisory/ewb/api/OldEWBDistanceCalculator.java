package com.ey.advisory.ewb.api;

import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.common.CalculateDistanceUtil;
import com.ey.advisory.ewb.common.EwbConstants;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("OldEWBDistanceCalculator")
public class OldEWBDistanceCalculator implements EWBDistanceCalculator {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("CalculateDistanceUtilImpl")
	CalculateDistanceUtil calcDistUtil;

	@Override
	public Pair<EwayBillRequestDto, Boolean> calculatefinalDistance(
			EwayBillRequestDto requestDto) {

		Map<String, Config> configMap = configManager.getConfigs("EWB",
				"ewb.distance", TenantContext.getTenantId());
		boolean calcDistance = configMap != null
				&& configMap.get("ewb.distance.isautocalcenabled") != null
						? "true".equalsIgnoreCase(
								configMap.get("ewb.distance.isautocalcenabled")
										.getValue())
						: Boolean.FALSE;
		boolean retry = configMap != null
				&& configMap.get("ewb.distance.isretryenabled") != null
						? "true".equalsIgnoreCase(configMap
								.get("ewb.distance.isretryenabled").getValue())
						: Boolean.FALSE;
		Integer markUpDistance = configMap != null
				&& configMap.get("ewb.distance.markup") != null
						? Integer.parseInt(
								configMap.get("ewb.distance.markup").getValue())
						: Integer.MIN_VALUE;
		if (calcDistance)
			calculateDistance(requestDto, markUpDistance);
		if (requestDto.getTransDistance() == null
				|| requestDto.getTransDistance() == 0) {
			retry = true;
			requestDto.setTransDistance(EwbConstants.DEFAULT_ZERO_DISTANCE);
		}
		return new Pair<EwayBillRequestDto, Boolean>(requestDto, retry);
	}

	private void calculateDistance(EwayBillRequestDto requestDto,
			Integer markUpDistance) {
		if (requestDto.getFromPincode() == null
				|| requestDto.getToPincode() == null) {
			String errMsg = "From/To pincode is missing";
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"calculating the distance mannualy for " + "the given "
							+ "pincodes frompincode %s ,toPincode %s ",
					requestDto.getFromPincode(), requestDto.getToPincode());
			LOGGER.info(msg);
		}
		if (requestDto.getFromPincode().equals(requestDto.getToPincode())) {
			String onBoardingKey = "ewb.distance.input";
			Integer defaultDistance = EwbConstants.SAME_PINCODE_DISTANCE;

			if ("LNS".equalsIgnoreCase(requestDto.getSubSupplyType())) {
				onBoardingKey = "ewb.lnsdistance.input";
				defaultDistance = EwbConstants.LNS_SAME_PINCODE_DISTANCE;
			}
			Map<String, Config> configMap = configManager.getConfigs("EWB",
					"ewb.distance", TenantContext.getTenantId());
			Integer nicCalculate = configMap.get(onBoardingKey) == null
					? defaultDistance
					: Integer.valueOf(configMap.get(onBoardingKey).getValue());
			requestDto.setAspDistance(nicCalculate);
			requestDto.setTransDistance(nicCalculate);
			return;
		}

		Integer distance = calcDistUtil.calculateDistance(
				requestDto.getFromPincode().toString(),
				requestDto.getToPincode().toString());
		if (distance == null || distance < 0) {
			distance = requestDto.getTransDistance() == null
					? EwbConstants.DEFAULT_ZERO_DISTANCE
					: requestDto.getTransDistance();
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("the calculated distance is %s ",
					distance);
			LOGGER.info(msg);
		}
		if (!"3".equals(requestDto.getTransMode())) {
			Integer markup = (distance * markUpDistance) / 100;
			distance = distance + markup;
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"the calculated distance after adding, markup is %s ",
					distance);
			LOGGER.info(msg);
		}
		requestDto.setAspDistance(distance);
		requestDto.setTransDistance(distance);
	}

}
