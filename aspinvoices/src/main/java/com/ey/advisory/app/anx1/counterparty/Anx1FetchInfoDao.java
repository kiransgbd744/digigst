package com.ey.advisory.app.anx1.counterparty;

import java.util.List;


public interface Anx1FetchInfoDao {
	
	public List<Anx1FetchInfoDto> findAnx1FetchInfoByGstins(List<String> gstins,
			String taxPeriod) ;
}
