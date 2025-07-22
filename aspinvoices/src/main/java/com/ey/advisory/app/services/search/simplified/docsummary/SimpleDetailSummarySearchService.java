package com.ey.advisory.app.services.search.simplified.docsummary;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Service("SimpleDetailSummarySearchService")
public class SimpleDetailSummarySearchService implements SearchService{

	
	@Autowired
	@Qualifier("Anx1OutwardDetailFetcher")
	Anx1OutwardDetailFetcher fetcher;
	
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Annexure1SummaryDto summary = new Annexure1SummaryDto();

		Map<String, List<Annexure1SummarySectionDto>> eySummaries = fetcher.fetch(req);

		List<Annexure1SummarySectionDto> _3aList = eySummaries.get("3A");
		
		List<Annexure1SummarySectionDto> _3bList = eySummaries.get("3B");
		List<Annexure1SummarySectionDto> _3cList = eySummaries.get("3C");
		List<Annexure1SummarySectionDto> _3dList = eySummaries.get("3D");
		List<Annexure1SummarySectionDto> _3eList = eySummaries.get("3E");
		List<Annexure1SummarySectionDto> _3fList = eySummaries.get("3F");
		List<Annexure1SummarySectionDto> _3gList = eySummaries.get("3G");

		Annexure1BasicSummaryDto b2cSummary = new Annexure1BasicSummaryDto();
		b2cSummary.setEySummary(_3aList);

		Annexure1BasicSummaryDto b2bSummary = new Annexure1BasicSummaryDto();
		b2bSummary.setEySummary(_3bList);

		Annexure1BasicSummaryDto exptSummary = new Annexure1BasicSummaryDto();
		exptSummary.setEySummary(_3cList);

		Annexure1BasicSummaryDto expwtSummary = new Annexure1BasicSummaryDto();
		expwtSummary.setEySummary(_3dList);

		Annexure1BasicSummaryDto seztSummary = new Annexure1BasicSummaryDto();
		seztSummary.setEySummary(_3eList);

		Annexure1BasicSummaryDto sezwtSummary = new Annexure1BasicSummaryDto();
		sezwtSummary.setEySummary(_3fList);

		Annexure1BasicSummaryDto deemedSummary = new Annexure1BasicSummaryDto();
		deemedSummary.setEySummary(_3gList);

		summary.setB2b(b2bSummary);
		summary.setB2c(b2cSummary);
		summary.setExpt(exptSummary);
		summary.setExpwt(expwtSummary);
		summary.setSezt(seztSummary);
		summary.setSezwt(sezwtSummary);
		summary.setDeemedExp(deemedSummary);

		List<Annexure1SummaryDto> list = new ArrayList<>();
		list.add(summary);

		return (SearchResult<R>) new SearchResult<Annexure1SummaryDto>(list);
	
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
