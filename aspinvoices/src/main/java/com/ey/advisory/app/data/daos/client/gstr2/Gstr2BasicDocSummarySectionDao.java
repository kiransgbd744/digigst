package com.ey.advisory.app.data.daos.client.gstr2;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr2.BaseGstr2SummaryEntity;

public interface Gstr2BasicDocSummarySectionDao {
	
	public abstract List<BaseGstr2SummaryEntity> loadBasicSummarySection(
			String sectionType,
			List<String> sgstins,
			List<Long> entityIds,
			int fromTaxPeriod, 
			int toTaxPeriod);


}
