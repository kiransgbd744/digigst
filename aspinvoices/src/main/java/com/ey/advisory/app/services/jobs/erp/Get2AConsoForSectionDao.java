package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

public interface Get2AConsoForSectionDao {

	public List<Object[]> findGet2AConsoForSection(final String gstin,
			final String retPeriod, final String section, final Long batchId,
			final Long erpBatchId);
}
