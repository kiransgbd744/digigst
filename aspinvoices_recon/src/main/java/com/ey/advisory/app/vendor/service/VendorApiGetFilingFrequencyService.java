package com.ey.advisory.app.vendor.service;

import java.util.List;

/**
 * 
 * @author vishal.verma
 *
 */
public interface VendorApiGetFilingFrequencyService {

	public void getFilingFrequency(String financialYear,
			List<String> gstin, String payloadId);

}
