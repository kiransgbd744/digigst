package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.entities.client.BaseGstr1SummaryEntity;

public interface BasicDocSummarySectionDao {

	public abstract List<BaseGstr1SummaryEntity> loadBasicSummarySection(
			String sectionType,
			List<String> sgstins,
			List<Long> entityIds,
			int fromTaxPeriod, 
			int toTaxPeriod);
}
