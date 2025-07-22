/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface BasicGstr1ADocSummaryScreenHsnSectionDao {

	public abstract List<Gstr1SummaryCDSectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);

}
