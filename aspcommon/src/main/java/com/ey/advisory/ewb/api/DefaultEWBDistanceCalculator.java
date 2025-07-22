package com.ey.advisory.ewb.api;

import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;

@Component("DefaultEWBDistanceCalculator")
public class DefaultEWBDistanceCalculator implements EWBDistanceCalculator {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private OldEWBDistanceCalculator oldCalculator;

	@Autowired
	private NewEWBDistanceCalculator newCalculator;

	@Override
	public Pair<EwayBillRequestDto, Boolean> calculatefinalDistance(
			EwayBillRequestDto requestDto) {
		Map<String, Config> configMap = configManager.getConfigs("EWB",
				"ewb.distance", TenantContext.getTenantId());

		String distMechanism = configMap != null
				&& configMap.get("ewb.distance.mechanism") != null
						? configMap.get("ewb.distance.mechanism").toString()
						: "NEW";

		if (distMechanism.equalsIgnoreCase("NEW")) {
			return newCalculator.calculatefinalDistance(requestDto);
		}
		else{
			return oldCalculator.calculatefinalDistance(requestDto);
			
		}
	}

}
