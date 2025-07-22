package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;

/**
 * 
 * @author Balakrishna.S
 *
 */

public interface BasicDocSummaryDocSectionDao {
	

	public abstract List<Gstr1DocIssuedSummarySectionDto> loadBasicSummarySection(
			String sectionType,
			List<String> sgstins,
			List<Long> entityIds,
			int fromTaxPeriod, 
			int toTaxPeriod);


}
