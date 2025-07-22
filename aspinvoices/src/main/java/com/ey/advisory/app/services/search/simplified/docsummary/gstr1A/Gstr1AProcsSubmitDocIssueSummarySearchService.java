/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Gstr1BasicDocSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1AProcsSubmitDocIssueSummarySearchService")
public class Gstr1AProcsSubmitDocIssueSummarySearchService
		implements SearchService {

	@Autowired
	@Qualifier("Gstr1AProcsSubmitDocIssueSummarySectionFetcher")
	Gstr1AProcsSubmitDocIssueSummarySectionFetcher fetcher;

	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Gstr1SummaryDocSectionDto>> eySummaries = fetcher
				.fetch(req);

		List<Gstr1SummaryDocSectionDto> doclist = eySummaries.get("DOC_ISSUED");
		Gstr1BasicDocSectionSummaryDto docSummary = new Gstr1BasicDocSectionSummaryDto();
		docSummary.setEySummary(doclist);

		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setDocIssues(docSummary);

		List<Gstr1CompleteSummaryDto> list = new ArrayList<>();
		list.add(summary);

		return (SearchResult<R>) new SearchResult<Gstr1CompleteSummaryDto>(
				list);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
