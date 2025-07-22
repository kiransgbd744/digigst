package com.ey.advisory.app.anx1.counterparty;

import java.util.List;

public interface CounterPartyInfoService {


	public List<CounterPartyInfoResponseDto> getCounterPartyInfo( 
			List<String> sgstinList, String taxPeriod);
	
}
