/**
 * 
 */
package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
public interface BasicGstr1DocGstnSummaryScreenSectionDao {
	
	public abstract List<Gstr1SummarySectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto req);

	public abstract List<Gstr1SummaryNilSectionDto> loadBasicSummarySectionNil(
			Annexure1SummaryReqDto req);
	
	public abstract List<Gstr1SummaryDocSectionDto> loadBasicSummaryDocSection(
			Annexure1SummaryReqDto req);
	

}
