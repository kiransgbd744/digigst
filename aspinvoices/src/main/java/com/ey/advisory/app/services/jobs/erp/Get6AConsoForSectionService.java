package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionFinalDto;

public interface Get6AConsoForSectionService {

	public List<Long> findChunkIds(final String gstin,
			final String retPeriod, final String section, final Long batchId);

	public Get2AConsoForSectionFinalDto findERPGet6AConsoForSection(
			final String gstin, final String retPeriod, final String section,
			Long batchId, final Long erpBatchId);

}
