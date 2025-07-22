/**
 * 
 */
package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
public interface AdvBasicGstr1DocSummaryScreenSectionDao {

	public abstract List<Gstr1SummarySectionDto> loadBasicSummaryATSection(
			Annexure1SummaryReqDto req);
	/*public abstract List<Gstr1SummarySectionDto> loadBasicSummaryATASection(
			Annexure1SummaryReqDto req);
	
	public abstract List<Gstr1SummarySectionDto> loadBasicSummaryTXPDSection(
			Annexure1SummaryReqDto req);
	
	public abstract List<Gstr1SummarySectionDto> loadBasicSummaryTXPDASection(
			Annexure1SummaryReqDto req);
	*/
	
}
