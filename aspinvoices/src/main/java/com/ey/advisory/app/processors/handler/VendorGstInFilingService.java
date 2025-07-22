package com.ey.advisory.app.processors.handler;

import java.util.List;

import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;

public interface VendorGstInFilingService {

	List<ReturnFilingGstnResponseDto> getReturnFilingStatusForGIvenYear(
			String year, List<String> vendorGstInList);
}
