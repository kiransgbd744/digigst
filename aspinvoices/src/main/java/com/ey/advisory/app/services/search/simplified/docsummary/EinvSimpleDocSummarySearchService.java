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
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;
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
@Service("EinvSimpleDocSummarySearchService")
public class EinvSimpleDocSummarySearchService implements SearchService {

	
	@Autowired
	@Qualifier("EinvoiceBasicSummarySectionFetcher")
	EinvoiceBasicSummarySectionFetcher fetcher;

	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		EinvoiceProcessedReqDto req = (EinvoiceProcessedReqDto) criteria;


		Map<String, List<Gstr1SummarySectionDto>> eySummaries = fetcher
				.fetch(req);

		List<Gstr1SummarySectionDto> b2blist = eySummaries.get("B2B");
		Gstr1BasicSectionSummaryDto b2bSummary = new Gstr1BasicSectionSummaryDto();
		b2bSummary.setEySummary(b2blist);

		
		List<Gstr1SummarySectionDto> explist = eySummaries.get("EXPORTS");
		Gstr1BasicSectionSummaryDto expSummary = new Gstr1BasicSectionSummaryDto();
		expSummary.setEySummary(explist);

		List<Gstr1SummarySectionDto> cdnrB9Lists = eySummaries.get("CDNR");
		Gstr1BasicSectionSummaryDto cdnrSummary = new Gstr1BasicSectionSummaryDto();
		cdnrSummary.setEySummary(cdnrB9Lists);
		
		
		
		List<Gstr1SummarySectionDto> cdnurList = eySummaries.get("CDNUR");
		List<Gstr1SummarySectionDto> cdnurEXPLists = eySummaries
				.get("CDNUR-EXPORTS");
		List<Gstr1SummarySectionDto> cdnurB2CLLists = eySummaries
				.get("CDNUR-B2CL");
		
		List<Gstr1SummarySectionDto> cdnurLists = cdnurAggregate(cdnurList,
				cdnurEXPLists, cdnurB2CLLists);
		
		
		
	//	List<Gstr1SummarySectionDto> cdnurLists = eySummaries.get("CDNUR");
		Gstr1BasicSectionSummaryDto cdnurSummary = new Gstr1BasicSectionSummaryDto();
		cdnurSummary.setEySummary(cdnurLists);
			
		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setB2b(b2bSummary);
		summary.setExp(expSummary);
		summary.setCdnr(cdnrSummary);
		summary.setCdnur(cdnurSummary);
		
		

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

		Integer aspRecords = 0;

		String aspTaxDocType;

		BigDecimal aspInvValue = BigDecimal.ZERO;

		BigDecimal aspTaxableValue = BigDecimal.ZERO;

		BigDecimal aspTaxPayable = BigDecimal.ZERO;
		BigDecimal aspIgst = BigDecimal.ZERO;
		BigDecimal aspCgst = BigDecimal.ZERO;

		BigDecimal aspSgst = BigDecimal.ZERO;
		BigDecimal aspCess = BigDecimal.ZERO;
		
		Integer gstRecords = 0;

		String gstTaxDocType;

		BigDecimal gstInvValue = BigDecimal.ZERO;

		BigDecimal gstTaxableValue = BigDecimal.ZERO;

		BigDecimal gstTaxPayable = BigDecimal.ZERO;
		BigDecimal gstIgst = BigDecimal.ZERO;
		BigDecimal gstCgst = BigDecimal.ZERO;

		BigDecimal gstSgst = BigDecimal.ZERO;
		BigDecimal gstCess = BigDecimal.ZERO;

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

				aspRecords = aspRecords + cdnurAgg.getAspCount();
				aspInvValue = aspInvValue.add(cdnurAgg.getAspInvoiceValue());
				aspTaxableValue = aspTaxableValue.add(cdnurAgg.getAspTaxableValue());
				aspTaxPayable = aspTaxPayable.add(cdnurAgg.getAspTaxPayble());
				aspIgst = aspIgst.add(cdnurAgg.getAspIgst());
				aspCgst = aspCgst.add(cdnurAgg.getAspCgst());
				aspSgst = aspSgst.add(cdnurAgg.getAspSgst());
				aspCess = aspCess.add(cdnurAgg.getAspCess());
				
				gstRecords = gstRecords + cdnurAgg.getGstnCount();
				gstInvValue = gstInvValue.add(cdnurAgg.getGstnInvoiceValue());
				gstTaxableValue = gstTaxableValue.add(cdnurAgg.getGstnTaxableValue());
				gstTaxPayable = gstTaxPayable.add(cdnurAgg.getGstnTaxPayble());
				gstIgst = gstIgst.add(cdnurAgg.getGstnIgst());
				gstCgst = gstCgst.add(cdnurAgg.getGstnCgst());
				gstSgst = gstSgst.add(cdnurAgg.getGstnSgst());
				gstCess = gstCess.add(cdnurAgg.getGstnCess());
			}
		}

		
		
		cdnurum.setTaxDocType("CDNUR");
		cdnurum.setAspCount(aspRecords);
		cdnurum.setAspInvoiceValue(aspInvValue);
		cdnurum.setAspTaxableValue(aspTaxableValue);
		cdnurum.setAspTaxPayble(aspTaxPayable);
		cdnurum.setAspIgst(aspIgst);
		cdnurum.setAspSgst(aspCgst);
		cdnurum.setAspCgst(aspSgst);
		cdnurum.setAspCess(aspCess);
		
		cdnurum.setGstnCount(gstRecords);
		cdnurum.setGstnInvoiceValue(gstInvValue);
		cdnurum.setGstnTaxableValue(gstTaxableValue);
		cdnurum.setGstnTaxPayble(gstTaxPayable);
		cdnurum.setGstnIgst(gstIgst);
		cdnurum.setGstnSgst(gstSgst);
		cdnurum.setGstnCgst(gstCgst);
		cdnurum.setGstnCess(gstCess);
		
		cdnurTotal.add(cdnurum);
		return cdnurTotal;

	}

	

}
