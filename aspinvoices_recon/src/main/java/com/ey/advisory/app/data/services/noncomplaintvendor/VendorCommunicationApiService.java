package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.List;

import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiRequestDto;

public interface VendorCommunicationApiService {

	public void persistGstnApiForSelectedFinancialYear(VendorAsyncApiRequestDto dto,
			List<String> vendorGstins,Long id, Long entityId);

}
