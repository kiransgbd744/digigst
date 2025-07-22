package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.Gstr3bQtrFilingPayloadMetaDataDto;

public interface Gstr3bQtrFilingMetadataRevIntService {

	public Gstr3bQtrFilingPayloadMetaDataDto payloadErrorInfoMsg(String type,
			String payloadId);

}
