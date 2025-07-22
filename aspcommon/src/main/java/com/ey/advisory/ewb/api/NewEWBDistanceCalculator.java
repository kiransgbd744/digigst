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
@Component("NewEWBDistanceCalculator")
public class NewEWBDistanceCalculator implements EWBDistanceCalculator {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("CalculateDistanceUtilImpl")
	CalculateDistanceUtil calcDistUtil;

	@Override
	public Pair<EwayBillRequestDto, Boolean> calculatefinalDistance(
			EwayBillRequestDto requestDto) {
		EwayBillRequestDto finalrequestdto = null;
		Map<String, Config> configMap = configManager.getConfigs("EWB",
				"ewb.distance", TenantContext.getTenantId());

		boolean nicCalculate = configMap
				.get("ewb.distance.niccalculate") == null ? Boolean.FALSE
						: Boolean.valueOf(configMap
								.get("ewb.distance.niccalculate").getValue());

		boolean userInputOpt = configMap
				.get("ewb.distance.userinputoption") == null
						? Boolean.TRUE
						: Boolean.valueOf(
								configMap.get("ewb.distance.userinputoption")
										.getValue());
		boolean userInputAlways = configMap
				.get("ewb.distance.userinputalways") == null
						? Boolean.FALSE
						: Boolean.valueOf(
								configMap.get("ewb.distance.userinputalways")
										.getValue());
		if (userInputAlways) {
			finalrequestdto = requestDto;
		} else if (nicCalculate) {
			finalrequestdto = calculateDistance(requestDto);
		} else if (userInputOpt) {
			if (requestDto.getTransDistance() == null
					|| requestDto.getTransDistance() == 0)
				finalrequestdto = calculateDistance(requestDto);
			else
				finalrequestdto = requestDto;
		}
		return new Pair<>(finalrequestdto, false);
	}

	private EwayBillRequestDto calculateDistance(
			EwayBillRequestDto requestDto) {
		if (requestDto.getFromPincode() == null
				|| requestDto.getToPincode() == null) {
			String errMsg = "From/To pincode is missing";
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"calculating the distance mannualy for the given "
							+ "pincodes frompincode %s ,toPincode %s ",
					requestDto.getFromPincode(), requestDto.getToPincode());
			LOGGER.info(msg);
		}
		if (requestDto.getFromPincode().equals(requestDto.getToPincode())) {
			String onBoardingKey = "ewb.distance.input";
			Integer defaultDistance = EwbConstants.SAME_PINCODE_DISTANCE;
			String dbConfkey = "ewb.distance";
			if ("10".equalsIgnoreCase(requestDto.getSubSupplyType())) {
				onBoardingKey = "ewb.lnsdistance.input";
				defaultDistance = EwbConstants.LNS_SAME_PINCODE_DISTANCE;
				dbConfkey = "ewb.lnsdistance";
			}
			Map<String, Config> configMap = configManager.getConfigs("EWB",
					dbConfkey, TenantContext.getTenantId());
			Integer nicCalculate = configMap.get(onBoardingKey) == null
					? defaultDistance
					: Integer.valueOf(configMap.get(onBoardingKey).getValue());
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("the calculated distance is %s ",
						nicCalculate);
				LOGGER.info(msg);
			}
			requestDto.setAspDistance(nicCalculate);
			requestDto.setTransDistance(nicCalculate);
		} else {
			Integer distance = EwbConstants.DEFAULT_ZERO_DISTANCE;
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("the calculated distance is %s ",
						distance);
				LOGGER.info(msg);
			}
			requestDto.setAspDistance(distance);
			requestDto.setTransDistance(distance);
		}
		return requestDto;

	}

}
