/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface BasicGstr1ADocSummaryScreenSectionDao {

	public abstract List<Gstr1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);
}
