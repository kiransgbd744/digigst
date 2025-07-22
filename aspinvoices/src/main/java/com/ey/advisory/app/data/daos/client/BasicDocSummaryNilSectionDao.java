package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;

/**
 * 
 * @author Balakrishna.S
 *
 */


public interface BasicDocSummaryNilSectionDao {

	public abstract List<Gstr1NilRatedSummarySectionDto> loadBasicSummarySection(
			String sectionType,
			List<String> sgstins,
			List<Long> entityIds,
			int fromTaxPeriod, 
			int toTaxPeriod);


}
