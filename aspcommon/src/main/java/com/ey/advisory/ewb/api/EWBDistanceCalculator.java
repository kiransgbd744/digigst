package com.ey.advisory.ewb.api;

import org.javatuples.Pair;

import com.ey.advisory.ewb.dto.EwayBillRequestDto;

public interface EWBDistanceCalculator {

	Pair<EwayBillRequestDto, Boolean> calculatefinalDistance(
			EwayBillRequestDto requestDto);
}
