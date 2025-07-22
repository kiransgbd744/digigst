/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
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
@Service("ProcessSubmitDocSummarySearchService")
public class ProcessSubmitDocSummarySearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProcessSubmitDocSummarySearchService.class);

	@Autowired
	@Qualifier("ProcessSubmitBasicSummarySectionFetcher")
	ProcessSubmitBasicSummarySectionFetcher fetcher;

	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Gstr1SummarySectionDto>> eySummaries = fetcher
				.fetch(req);

		List<Gstr1SummarySectionDto> b2blist = eySummaries.get("B2B");
		Gstr1BasicSectionSummaryDto b2bSummary = new Gstr1BasicSectionSummaryDto();
		b2bSummary.setEySummary(b2blist);

		List<Gstr1SummarySectionDto> b2balist = eySummaries.get("B2BA");
		Gstr1BasicSectionSummaryDto b2baSummary = new Gstr1BasicSectionSummaryDto();
		b2baSummary.setEySummary(b2balist);

		List<Gstr1SummarySectionDto> b2cllist = eySummaries.get("B2CL");
		Gstr1BasicSectionSummaryDto b2clSummary = new Gstr1BasicSectionSummaryDto();
		b2clSummary.setEySummary(b2cllist);

		List<Gstr1SummarySectionDto> b2clalist = eySummaries.get("B2CLA");
		Gstr1BasicSectionSummaryDto b2claSummary = new Gstr1BasicSectionSummaryDto();
		b2claSummary.setEySummary(b2clalist);

		List<Gstr1SummarySectionDto> explist = eySummaries.get("EXPORTS");
		Gstr1BasicSectionSummaryDto expSummary = new Gstr1BasicSectionSummaryDto();
		expSummary.setEySummary(explist);

		List<Gstr1SummarySectionDto> expalist = eySummaries.get("EXPORTS-A");
		Gstr1BasicSectionSummaryDto expaSummary = new Gstr1BasicSectionSummaryDto();
		expaSummary.setEySummary(expalist);

		List<Gstr1SummarySectionDto> b2cslist = eySummaries.get("B2CS");
		Gstr1BasicSectionSummaryDto b2csSummary = new Gstr1BasicSectionSummaryDto();
		b2csSummary.setEySummary(b2cslist);

		List<Gstr1SummarySectionDto> b2csalist = eySummaries.get("B2CSA");
		Gstr1BasicSectionSummaryDto b2csaSummary = new Gstr1BasicSectionSummaryDto();
		b2csaSummary.setEySummary(b2csalist);

		List<Gstr1SummarySectionDto> cdnrB9Lists = eySummaries.get("CDNR");
		Gstr1BasicSectionSummaryDto cdnrSummary = new Gstr1BasicSectionSummaryDto();
		cdnrSummary.setEySummary(cdnrB9Lists);

		List<Gstr1SummarySectionDto> cdnraLists = eySummaries.get("CDNRA");
		Gstr1BasicSectionSummaryDto cdnraSummary = new Gstr1BasicSectionSummaryDto();
		cdnraSummary.setEySummary(cdnraLists);

		List<Gstr1SummarySectionDto> cdnurLists = eySummaries.get("CDNUR");
		Gstr1BasicSectionSummaryDto cdnurSummary = new Gstr1BasicSectionSummaryDto();
		cdnurSummary.setEySummary(cdnurLists);

		List<Gstr1SummarySectionDto> cdnuraList = eySummaries.get("CDNURA");
		Gstr1BasicSectionSummaryDto cdnuraSummary = new Gstr1BasicSectionSummaryDto();
		cdnuraSummary.setEySummary(cdnuraList);

		List<Gstr1SummarySectionDto> atlist = eySummaries.get("ADV REC");
		Gstr1BasicSectionSummaryDto atSummary = new Gstr1BasicSectionSummaryDto();
		atSummary.setEySummary(atlist);

		List<Gstr1SummarySectionDto> atalist = eySummaries.get("ADV REC-A");
		Gstr1BasicSectionSummaryDto ataSummary = new Gstr1BasicSectionSummaryDto();
		ataSummary.setEySummary(atalist);

		List<Gstr1SummarySectionDto> txpdlist = eySummaries.get("ADV ADJ");
		Gstr1BasicSectionSummaryDto txpdSummary = new Gstr1BasicSectionSummaryDto();
		txpdSummary.setEySummary(txpdlist);

		List<Gstr1SummarySectionDto> txpdalist = eySummaries.get("ADV ADJ-A");
		Gstr1BasicSectionSummaryDto txpdaSummary = new Gstr1BasicSectionSummaryDto();
		txpdaSummary.setEySummary(txpdalist);

		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setB2b(b2bSummary);
		summary.setB2ba(b2baSummary);
		summary.setB2cl(b2clSummary);
		summary.setB2cla(b2claSummary);
		summary.setExp(expSummary);
		summary.setExpa(expaSummary);
		summary.setB2cs(b2csSummary);
		summary.setB2csa(b2csaSummary);
		summary.setCdnr(cdnrSummary);
		summary.setCdnra(cdnraSummary);
		summary.setCdnur(cdnurSummary);
		summary.setCdnura(cdnuraSummary);
		summary.setAt(atSummary);
		summary.setAta(ataSummary);
		summary.setTxpd(txpdSummary);
		summary.setTxpda(txpdaSummary);

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
