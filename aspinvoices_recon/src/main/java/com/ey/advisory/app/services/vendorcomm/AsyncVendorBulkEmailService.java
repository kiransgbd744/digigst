package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.vendorcomm.dto.VendorEmailCommDto;

public interface AsyncVendorBulkEmailService {
	

	public Pair<List<VendorEmailCommDto>, Integer> getEmailCommDetails(
			Long requestId, Long entityId);
 
	public String createRequestPayloadForEmail(List<VendorEmailCommDto> vendorEmailCommDtoListPair, Long entityId, String reconType);
	
	public String sendAll(Long id, Long entityId, Long requestId);
}
