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

import com.ey.advisory.app.docs.dto.Gstr1BasicCDSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SimpleHSNSummarySearchService")
public class Gstr1SimpleHSNSummarySearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SimpleHSNSummarySearchService.class);

	@Autowired
	@Qualifier("SimpleGstr1BasicSummaryHsnSectionFetcher")
	SimpleGstr1BasicSummaryHsnSectionFetcher fetcher;

	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Gstr1SummaryCDSectionDto>> eySummaries = fetcher
				.fetch(req);

		List<Gstr1SummaryCDSectionDto> hsnlist = new ArrayList<>();
		List<Gstr1SummaryCDSectionDto> hsnlist_Asp = eySummaries.get("HSN_ASP");
		Gstr1BasicCDSectionSummaryDto hsnSummary = new Gstr1BasicCDSectionSummaryDto();

		if (hsnlist_Asp == null || hsnlist_Asp.size() == 0) {

			Gstr1SummaryCDSectionDto dto = new Gstr1SummaryCDSectionDto();
			dto.setTaxDocType("HSN_ASP");
			dto.setAspCess(new BigDecimal("0"));
			dto.setAspCgst(new BigDecimal("0"));
			dto.setAspCount(0);
			dto.setAspIgst(new BigDecimal("0"));
			dto.setAspInvoiceValue(new BigDecimal("0"));
			dto.setAspSgst(new BigDecimal("0"));
			dto.setAspTaxableValue(new BigDecimal("0"));
			dto.setAspTaxPayble(new BigDecimal("0"));
			hsnlist.add(dto);
		} else {
			hsnlist.addAll(hsnlist_Asp);
		}

		List<Gstr1SummaryCDSectionDto> hsnlist_Asp_b2b = eySummaries.get("HSN_ASP_B2B");

		if (hsnlist_Asp_b2b == null || hsnlist_Asp_b2b.size() == 0) {

			Gstr1SummaryCDSectionDto dto = new Gstr1SummaryCDSectionDto();
			dto.setTaxDocType("HSN_ASP_B2B");
			dto.setAspCess(new BigDecimal("0"));
			dto.setAspCgst(new BigDecimal("0"));
			dto.setAspCount(0);
			dto.setAspIgst(new BigDecimal("0"));
			dto.setAspInvoiceValue(new BigDecimal("0"));
			dto.setAspSgst(new BigDecimal("0"));
			dto.setAspTaxableValue(new BigDecimal("0"));
			dto.setAspTaxPayble(new BigDecimal("0"));
			hsnlist.add(dto);
		} else {
			hsnlist.addAll(hsnlist_Asp_b2b);
		}
		
		List<Gstr1SummaryCDSectionDto> hsnlist_asp_b2c = eySummaries.get("HSN_ASP_B2C");

		if (hsnlist_asp_b2c == null || hsnlist_asp_b2c.size() == 0) {

			Gstr1SummaryCDSectionDto dto = new Gstr1SummaryCDSectionDto();
			dto.setTaxDocType("HSN_ASP_B2C");
			dto.setAspCess(new BigDecimal("0"));
			dto.setAspCgst(new BigDecimal("0"));
			dto.setAspCount(0);
			dto.setAspIgst(new BigDecimal("0"));
			dto.setAspInvoiceValue(new BigDecimal("0"));
			dto.setAspSgst(new BigDecimal("0"));
			dto.setAspTaxableValue(new BigDecimal("0"));
			dto.setAspTaxPayble(new BigDecimal("0"));
			hsnlist.add(dto);
		} else {
			hsnlist.addAll(hsnlist_asp_b2c);
		}
		
		List<Gstr1SummaryCDSectionDto> hsnlist_Ui = eySummaries.get("HSN_UI");

		if (hsnlist_Ui == null || hsnlist_Ui.size() == 0) {

			Gstr1SummaryCDSectionDto dto = new Gstr1SummaryCDSectionDto();
			dto.setTaxDocType("HSN_UI");
			dto.setAspCess(new BigDecimal("0"));
			dto.setAspCgst(new BigDecimal("0"));
			dto.setAspCount(0);
			dto.setAspIgst(new BigDecimal("0"));
			dto.setAspInvoiceValue(new BigDecimal("0"));
			dto.setAspSgst(new BigDecimal("0"));
			dto.setAspTaxableValue(new BigDecimal("0"));
			dto.setAspTaxPayble(new BigDecimal("0"));
			hsnlist.add(dto);
		} else {
			hsnlist.addAll(hsnlist_Ui);
		}
		
		List<Gstr1SummaryCDSectionDto> hsnlist_ui_b2b = eySummaries.get("B2B");

		if (hsnlist_ui_b2b == null || hsnlist_ui_b2b.size() == 0) {

			Gstr1SummaryCDSectionDto dto = new Gstr1SummaryCDSectionDto();
			dto.setTaxDocType("B2B");
			dto.setAspCess(new BigDecimal("0"));
			dto.setAspCgst(new BigDecimal("0"));
			dto.setAspCount(0);
			dto.setAspIgst(new BigDecimal("0"));
			dto.setAspInvoiceValue(new BigDecimal("0"));
			dto.setAspSgst(new BigDecimal("0"));
			dto.setAspTaxableValue(new BigDecimal("0"));
			dto.setAspTaxPayble(new BigDecimal("0"));
			hsnlist.add(dto);
		} else {
			hsnlist.addAll(hsnlist_ui_b2b);
		}
		
		List<Gstr1SummaryCDSectionDto> hsnlist_ui_b2c = eySummaries.get("B2C");

		if (hsnlist_ui_b2c == null || hsnlist_ui_b2c.size() == 0) {

			Gstr1SummaryCDSectionDto dto = new Gstr1SummaryCDSectionDto();
			dto.setTaxDocType("B2C");
			dto.setAspCess(new BigDecimal("0"));
			dto.setAspCgst(new BigDecimal("0"));
			dto.setAspCount(0);
			dto.setAspIgst(new BigDecimal("0"));
			dto.setAspInvoiceValue(new BigDecimal("0"));
			dto.setAspSgst(new BigDecimal("0"));
			dto.setAspTaxableValue(new BigDecimal("0"));
			dto.setAspTaxPayble(new BigDecimal("0"));
			hsnlist.add(dto);
		} else {
			hsnlist.addAll(hsnlist_ui_b2c);
		}


		// Gstr1BasicCDSectionSummaryDto hsnSummary = new
		// Gstr1BasicCDSectionSummaryDto();
		// hsnSummary.setEySummary(hsnlist_Ui);

		
		
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
