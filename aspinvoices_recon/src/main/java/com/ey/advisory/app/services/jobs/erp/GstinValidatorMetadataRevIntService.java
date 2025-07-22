package com.ey.advisory.app.services.jobs.erp;

import com.ey.advisory.app.docs.dto.erp.GstinValidatorPayloadMetaDataDto;

public interface GstinValidatorMetadataRevIntService {

	public GstinValidatorPayloadMetaDataDto payloadErrorInfoMsg(String type, String payloadId);

	/*public Integer pushToErp(final PayloadMetaDataDto dataDto, String destName,
			AnxErpBatchEntity entity, String type);*/
}
