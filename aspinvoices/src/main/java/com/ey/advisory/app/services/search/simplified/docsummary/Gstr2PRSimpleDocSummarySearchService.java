package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr2PRBasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr2PRCompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Gstr2PRSimpleDocSummarySearchService")
public class Gstr2PRSimpleDocSummarySearchService implements SearchService{

	@Autowired
	@Qualifier("SimpleGstr2PRSummarySectionFetcher")
	SimpleGstr2PRSummarySectionFetcher fetcher;
	
	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		Gstr2ProcessedRecordsReqDto req = (Gstr2ProcessedRecordsReqDto) criteria;
		Map<String, List<Gstr2PRSummarySectionDto>> eySummaries = fetcher.fetch(req);
		
		Gstr2PRCompleteSummaryDto summary = new Gstr2PRCompleteSummaryDto();
		
		List<Gstr2PRSummarySectionDto> b2blist = eySummaries.get("1-B2B");
		Gstr2PRBasicSectionSummaryDto b2bSummary = new Gstr2PRBasicSectionSummaryDto();
		b2bSummary.setEySummary(b2blist);
		
		List<Gstr2PRSummarySectionDto> b2balist = eySummaries.get("2-B2BA");
		Gstr2PRBasicSectionSummaryDto b2baSummary = new Gstr2PRBasicSectionSummaryDto();
		b2baSummary.setEySummary(b2balist);
		
		List<Gstr2PRSummarySectionDto> cdnlist = eySummaries.get("3-CDN");
		Gstr2PRBasicSectionSummaryDto cdnSummary = new Gstr2PRBasicSectionSummaryDto();
		cdnSummary.setEySummary(cdnlist);
		
		List<Gstr2PRSummarySectionDto> cdnalist = eySummaries.get("4-CDNA");
		Gstr2PRBasicSectionSummaryDto cdnaSummary = new Gstr2PRBasicSectionSummaryDto();
		cdnaSummary.setEySummary(cdnalist);
		
		List<Gstr2PRSummarySectionDto> isdlist = eySummaries.get("5-ISD");
		Gstr2PRBasicSectionSummaryDto isdSummary = new Gstr2PRBasicSectionSummaryDto();
		isdSummary.setEySummary(isdlist);
		
		List<Gstr2PRSummarySectionDto> isdalist = eySummaries.get("6-ISDA");
		Gstr2PRBasicSectionSummaryDto isdaSummary = new Gstr2PRBasicSectionSummaryDto();
		isdaSummary.setEySummary(isdalist);
		
		List<Gstr2PRSummarySectionDto> impslist = eySummaries.get("10-IMP");
		Gstr2PRBasicSectionSummaryDto impsSummary = new Gstr2PRBasicSectionSummaryDto();
		impsSummary.setEySummary(impslist);
		
		List<Gstr2PRSummarySectionDto> impsalist = eySummaries.get("11-IMPA");
		Gstr2PRBasicSectionSummaryDto impsaSummary = new Gstr2PRBasicSectionSummaryDto();
		impsaSummary.setEySummary(impsalist);
				
		List<Gstr2PRSummarySectionDto> rcurdlist = eySummaries.get("12-RCURD");
		Gstr2PRBasicSectionSummaryDto rcurdSummary = new Gstr2PRBasicSectionSummaryDto();
		rcurdSummary.setEySummary(rcurdlist);
		
		List<Gstr2PRSummarySectionDto> rcurdalist = eySummaries.get("13-RCURDA");
		Gstr2PRBasicSectionSummaryDto rcurdaSummary = new Gstr2PRBasicSectionSummaryDto();
		rcurdaSummary.setEySummary(rcurdalist);
		
		List<Gstr2PRSummarySectionDto> rcmadvlist = eySummaries.get("14-RCMADV");
		Gstr2PRBasicSectionSummaryDto rcmadvSummary = new Gstr2PRBasicSectionSummaryDto();
		rcmadvSummary.setEySummary(rcmadvlist);

		
		summary.setB2b(b2bSummary);
		summary.setB2ba(b2baSummary);
		summary.setCdn(cdnSummary);
		summary.setCdna(cdnaSummary);
		summary.setIsd(isdSummary);
		summary.setIsda(isdaSummary);
		summary.setImp(impsSummary);
		summary.setImpa(impsaSummary);
		summary.setRcurd(rcurdSummary);
		summary.setRcurda(rcurdaSummary);
		summary.setRcmadv(rcmadvSummary);
		
		return (SearchResult<R>) new SearchResult<Gstr2PRCompleteSummaryDto>(
				summary);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
