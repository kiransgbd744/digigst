package com.ey.advisory.app.processors.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;

public class VendorGstInFilingServiceImpl implements VendorGstInFilingService {

	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	@Override
	public List<ReturnFilingGstnResponseDto> getReturnFilingStatusForGIvenYear(
			String year, List<String> vendorGstInList) {
		List<ReturnFilingGstnResponseDto> returnFilingList = gstnReturnFiling
				.callGstnApi(vendorGstInList, year, true);
		return null;
	}

}
