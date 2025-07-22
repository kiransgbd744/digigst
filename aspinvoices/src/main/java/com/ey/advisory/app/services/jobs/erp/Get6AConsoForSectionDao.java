package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

public interface Get6AConsoForSectionDao {

	public List<Object[]> findGet6AConsoForSection(final String gstin,
			final String retPeriod, final String section, final Long batchId,
			final Long erpBatchId);
}
