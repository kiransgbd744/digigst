package com.ey.advisory.app.services.jobs.erp.processedrecords;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;

public interface ProcessedRecordsPushErp {
	
	
	public Integer pushToErp(Anx1ProcessedRevRecordsDto dto,
			String destinationName, AnxErpBatchEntity batch);

}
