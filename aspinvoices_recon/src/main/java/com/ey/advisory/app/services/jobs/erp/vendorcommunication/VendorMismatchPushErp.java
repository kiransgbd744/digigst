package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;

/**
 * 
 * @author vishal.verma
 *
 */

public interface VendorMismatchPushErp {

	public Integer pushToErp(VendorMismatchRevRecordsDto dto,
			String destinationName, AnxErpBatchEntity batch);
} 