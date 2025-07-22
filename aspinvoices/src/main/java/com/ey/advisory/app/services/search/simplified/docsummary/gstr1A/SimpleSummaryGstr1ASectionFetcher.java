/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Siva.Reddy
 *
 */
public interface SimpleSummaryGstr1ASectionFetcher {

	public Map<String, List<Gstr1SummarySectionDto>> fetch(
			Annexure1SummaryReqDto req);
	
	public Map<String, List<Gstr1SummaryNilSectionDto>> fetchNil(
			Annexure1SummaryReqDto req);

	public Map<String, List<Gstr1SummaryDocSectionDto>> fetchDoc(
			Annexure1SummaryReqDto req);

}
