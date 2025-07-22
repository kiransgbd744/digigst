package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.VendorMasterApiPayloadMetaDataDto;

public interface VendorMasterApiMetadataRevIntService {

	public VendorMasterApiPayloadMetaDataDto payloadErrorInfoMsg(String type,
			String payloadId);

}
