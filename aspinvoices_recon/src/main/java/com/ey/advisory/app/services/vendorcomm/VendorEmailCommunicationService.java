package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.vendorcomm.dto.VendorEmailCommDto;

public interface VendorEmailCommunicationService {

	public Pair<List<VendorEmailCommDto>, Integer> getEmailCommunicationDetails(
			Long requestId,Long entityId, int pageSize, int pageNum);

	public String createReqPayloadForEmail(String json);

}
