package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1InwardSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.SumTableData;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("Anx1InwardDetailSummarySearchService")
public class Anx1InwardDetailSummarySearchService implements SearchService {

	@Autowired
	@Qualifier("Anx1BasicInwardSummarySectionFetcherImpl")
	private Anx1BasicInwardDetailSummarySectionFetcher fetcher;

	@Autowired
	@Qualifier("sumTableData")
	private SumTableData sumTableData;

	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Annexure1SummaryDto summary = new Annexure1SummaryDto();

		Map<String, List<Annexure1SummarySectionDto>> eySummaries = fetcher
				.fetchInward(req);

		List<Annexure1SummarySectionDto> _3jList = eySummaries.get("3J");
		List<Annexure1SummarySectionDto> _3KList = eySummaries.get("3K");
		List<Annexure1SummarySectionDto> _3IList = eySummaries.get("3I");
		List<Annexure1SummarySectionDto> _3HList = eySummaries.get("3H");
		

		
		Annexure1BasicSummaryDto impsiSummary = new Annexure1BasicSummaryDto();
		impsiSummary.setEySummary(_3IList);

		Annexure1BasicSummaryDto impgSummary = new Annexure1BasicSummaryDto();
		impgSummary.setEySummary(_3jList);
		Annexure1BasicSummaryDto impgSezSummary = new Annexure1BasicSummaryDto();
		impgSezSummary.setEySummary(_3KList);

		Annexure1BasicSummaryDto revChargeSummary = new Annexure1BasicSummaryDto();
		revChargeSummary.setEySummary(_3HList);

		summary.setImpg(impgSummary);
		summary.setImpgSez(impgSezSummary);
		summary.setImps(impsiSummary);
		summary.setRev(revChargeSummary);

		List<Annexure1SummaryDto> list = new ArrayList<>();
		list.add(summary);

		return (SearchResult<R>) new SearchResult<Annexure1SummaryDto>(
				list);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
