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

import com.ey.advisory.app.docs.dto.Gstr1BasicNilSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SimpleNilSummarySearchService")
public class Gstr1SimpleNilSummarySearchService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SimpleNilSummarySearchService.class);

	@Autowired
	SimpleGstr1BasicDocIssueSummarySectionFetcher fetcher;

	@SuppressWarnings("null")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Gstr1SummaryNilSectionDto>> eySummaries = fetcher
				.fetchNil(req);

		List<Gstr1SummaryNilSectionDto> nilList = new ArrayList<>();
		List<Gstr1SummaryNilSectionDto> nilList_Asp = eySummaries.get("ASP_NILEXTNON");
		Gstr1BasicNilSectionSummaryDto nilSummary = new Gstr1BasicNilSectionSummaryDto();
		
		if(nilList_Asp == null || nilList_Asp.size()==0){
		
			Gstr1SummaryNilSectionDto  dto = new Gstr1SummaryNilSectionDto();
			dto.setTaxDocType("ASP_NILEXTNON");
			dto.setAspExempted(new BigDecimal("0"));
			dto.setAspNitRated(new BigDecimal("0"));
			dto.setAspNonGst(new BigDecimal("0"));
			nilList.add(dto);
			
		}else {
			 nilList.addAll(nilList_Asp);
		 }
	

		List<Gstr1SummaryNilSectionDto> nilList_Ui = eySummaries.get("UI_NILEXTNON");
		
		if(nilList_Ui == null || nilList_Ui.size()==0){
			
			Gstr1SummaryNilSectionDto  dto = new Gstr1SummaryNilSectionDto();
			dto.setTaxDocType("UI_NILEXTNON");
			dto.setAspExempted(new BigDecimal("0"));
			dto.setAspNitRated(new BigDecimal("0"));
			dto.setAspNonGst(new BigDecimal("0"));
			nilList.add(dto);
			
		}else {
			 nilList.addAll(nilList_Ui);
		 }

		
	//	Gstr1BasicNilSectionSummaryDto nilSummary = new Gstr1BasicNilSectionSummaryDto();
	//	nilSummary.setEySummary(nilList_Ui);
 //nilList.addAll(nilList_Asp);
 //nilList.addAll(nilList_Ui);
 
 
 		
	nilSummary.setEySummary(nilList);
		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setNil(nilSummary);

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
