package com.ey.advisory.app.gstr2b.summary;

import java.util.List;

public interface Gstr2BSummaryDao {
	
	public List<Gstr2BSummaryDto> getSummaryResp(List<String> gstins,
			String toTaxPeriod, String fromTaxPeriod);	


}