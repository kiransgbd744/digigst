package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.docs.dto.erp.BCAPIOutwardPayloadErrorItemDto;

public interface BCAPIPaylodRevereseFeedService {

	public List<BCAPIOutwardPayloadErrorItemDto> getObjectPayload(
			String payloadId);
}
