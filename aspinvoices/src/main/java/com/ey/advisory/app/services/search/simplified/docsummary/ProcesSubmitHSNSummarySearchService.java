/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Gstr1BasicCDSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@Service("ProcesSubmitHSNSummarySearchService")
public class ProcesSubmitHSNSummarySearchService implements SearchService{

	@Autowired
	@Qualifier("ProcsSubmitSummaryHsnSectionFetcher")
	ProcsSubmitSummaryHsnSectionFetcher fetcher;
	
	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		
		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;


		Map<String, List<Gstr1SummaryCDSectionDto>> eySummaries = fetcher
				.fetch(req);

		List<Gstr1SummaryCDSectionDto> hsnlist = eySummaries.get("HSN");
		Gstr1BasicCDSectionSummaryDto hsnSummary = new Gstr1BasicCDSectionSummaryDto();
		hsnSummary.setEySummary(hsnlist);

		
		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setHsn(hsnSummary);
		
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
