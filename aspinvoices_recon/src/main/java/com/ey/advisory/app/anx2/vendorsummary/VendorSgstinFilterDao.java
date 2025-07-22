package com.ey.advisory.app.anx2.vendorsummary;

import java.util.List;

public interface VendorSgstinFilterDao {

	public List<String> findSgstinsForCgstins(List<String> cgstins,
									String taxPeriod);
}
