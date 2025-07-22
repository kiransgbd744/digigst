package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;

public interface VendoeSgstinService {

	
	public List<String> getSgstinsforCgstins(List<String> cgstins,
			String taxPeriod);
}
