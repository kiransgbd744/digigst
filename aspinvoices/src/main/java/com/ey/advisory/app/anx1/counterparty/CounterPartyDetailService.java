package com.ey.advisory.app.anx1.counterparty;

import java.util.List;

public interface CounterPartyDetailService {
	
	public List<CounterPartyDetailsDto> getAllCounterPartyDetails(
			List<String> sgstin,String taxPeriod);

}
