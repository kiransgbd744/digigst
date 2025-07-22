/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Siva
 *
 */
public interface BasicGstr1ADocGstnSummaryScreenSectionDao {
	
	public abstract List<Gstr1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);

	public abstract List<Gstr1SummaryNilSectionDto> loadBasicSummarySectionNil(
			Annexure1SummaryReqDto req);
	
	public abstract List<Gstr1SummaryDocSectionDto> loadBasicSummaryDocSection(
			Annexure1SummaryReqDto req);
	

}
