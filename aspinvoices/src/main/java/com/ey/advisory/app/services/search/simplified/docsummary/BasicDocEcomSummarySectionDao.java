/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
public interface BasicDocEcomSummarySectionDao {
	
	public abstract List<Annexure1SummarySectionEcomDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);


}
