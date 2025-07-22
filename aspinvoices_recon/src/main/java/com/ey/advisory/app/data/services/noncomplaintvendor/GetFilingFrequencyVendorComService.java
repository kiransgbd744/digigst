package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.List;

public interface GetFilingFrequencyVendorComService {

	public void getVendorFilingFrequency(List<String> vendorGstins, String financialYear,
			String complianceType);

}
