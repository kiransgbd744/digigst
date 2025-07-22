/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
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

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SimpleDocSummarySearchService")
public class Gstr1SimpleDocSummarySearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SimpleDocSummarySearchService.class);

	@Autowired
	@Qualifier("SimpleGstr1BasicSummarySectionFetcher")
	SimpleGstr1BasicSummarySectionFetcher fetcher;

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

		/**
		 * Here we are Aggregating CDNUR & CDNUR-EXPORTS & CDNUR-B2CL and
		 * showing in single Section as CDNUR
		 */

		List<Gstr1SummarySectionDto> cdnurList = eySummaries.get("CDNUR");
		/*
		 * Gstr1BasicSectionSummaryDto cdnurSummary = new
		 * Gstr1BasicSectionSummaryDto(); cdnurSummary.setEySummary(cdnurLists);
		 */
		List<Gstr1SummarySectionDto> cdnurEXPLists = eySummaries
				.get("CDNUR-EXPORTS");
		/*
		 * Gstr1BasicSectionSummaryDto cdnurExpSummary = new
		 * Gstr1BasicSectionSummaryDto(); cdnurSummary.setEySummary(cdnurLists);
		 */
		List<Gstr1SummarySectionDto> cdnurB2CLLists = eySummaries
				.get("CDNUR-B2CL");
		/*
		 * Gstr1BasicSectionSummaryDto cdnurB2clSummary = new
		 * Gstr1BasicSectionSummaryDto(); cdnurSummary.setEySummary(cdnurLists);
		 */

		List<Gstr1SummarySectionDto> cdnurLists = cdnurAggregate(cdnurList,
				cdnurEXPLists, cdnurB2CLLists);
		Gstr1BasicSectionSummaryDto cdnurSummary = new Gstr1BasicSectionSummaryDto();
		cdnurSummary.setEySummary(cdnurLists);

		List<Gstr1SummarySectionDto> cdnuraList = eySummaries.get("CDNURA");
		Gstr1BasicSectionSummaryDto cdnuraSummary = new Gstr1BasicSectionSummaryDto();
		cdnuraSummary.setEySummary(cdnuraList);

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

	public List<Gstr1SummarySectionDto> cdnurAggregate(
			List<Gstr1SummarySectionDto> cdnur,
			List<Gstr1SummarySectionDto> cdnurExp,
			List<Gstr1SummarySectionDto> cdnurB2cl) {

		Integer records = 0;

		String taxDocType;

		BigDecimal invValue = BigDecimal.ZERO;

		BigDecimal taxableValue = BigDecimal.ZERO;

		BigDecimal taxPayable = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;

		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cess = BigDecimal.ZERO;

		List<Gstr1SummarySectionDto> cdnurList = new ArrayList<>();

		if(cdnur != null){
		cdnurList.addAll(cdnur);
		}
		if(cdnurB2cl != null){
		cdnurList.addAll(cdnurB2cl);
		}
		if(cdnurExp != null){
		cdnurList.addAll(cdnurExp);
		}
		List<Gstr1SummarySectionDto> cdnurTotal = new ArrayList<>();

		Gstr1SummarySectionDto cdnurum = new Gstr1SummarySectionDto();
		if (cdnurList.size() > 0) {
			for (Gstr1SummarySectionDto cdnurAgg : cdnurList) {

				records = records + cdnurAgg.getRecords();
				invValue = invValue.add(cdnurAgg.getInvValue());
				taxableValue = taxableValue.add(cdnurAgg.getTaxableValue());
				taxPayable = taxPayable.add(cdnurAgg.getTaxPayable());
				igst = igst.add(cdnurAgg.getIgst());
				cgst = cgst.add(cdnurAgg.getCgst());
				sgst = sgst.add(cdnurAgg.getSgst());
				cess = cess.add(cdnurAgg.getCess());
			}
		}

		cdnurum.setTaxDocType("CDNUR");
		cdnurum.setRecords(records);
		cdnurum.setInvValue(invValue);
		cdnurum.setTaxableValue(taxableValue);
		cdnurum.setTaxPayable(taxPayable);
		cdnurum.setIgst(igst);
		cdnurum.setSgst(sgst);
		cdnurum.setCgst(cgst);
		cdnurum.setCess(cess);
		cdnurTotal.add(cdnurum);
		return cdnurTotal;

	}

}
