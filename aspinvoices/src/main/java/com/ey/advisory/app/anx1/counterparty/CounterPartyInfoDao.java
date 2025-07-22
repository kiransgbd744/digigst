package com.ey.advisory.app.anx1.counterparty;

import java.util.List;

public interface CounterPartyInfoDao {
	

	public List<CounterPartyInfoDto> getAllCounterPartyInfo(String taxPeriod,
		List<String> sgstin, List<String> tableSection, List<String> docTypes);

}
