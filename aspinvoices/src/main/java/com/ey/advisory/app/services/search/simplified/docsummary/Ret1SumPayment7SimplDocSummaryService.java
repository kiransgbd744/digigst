package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Ret1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1PaymentSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author siva krishna
 *
 */
@Service("Ret1SumPayment7SimplDocSummaryService")
@Slf4j
public class Ret1SumPayment7SimplDocSummaryService implements SearchService{

	@Autowired
	@Qualifier("Ret1Payment7SimplDefaultGstr1BasicSummarySectionFetcher")
	Ret1Payment7SimplBasicSummarySectionFetcher fetcher;
	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		
		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
		
		
		Map<String, List<Ret1PaymentSummarySectionDto>> eySummaries = fetcher
				.fetch(req);
		LOGGER.debug("Section 8 Map Values ----> "+eySummaries);
		
		List<Ret1PaymentSummarySectionDto> payment_7List = eySummaries.get("7");
		Ret1BasicSectionSummaryDto payment_7dto = new Ret1BasicSectionSummaryDto();
		payment_7dto.setEySummaryPayment7(payment_7List);	
		
		
		List<Ret1PaymentSummarySectionDto> payment_7_1List = eySummaries.get("7(1)");
		Ret1BasicSectionSummaryDto payment_7_1dto = new Ret1BasicSectionSummaryDto();
		payment_7_1dto.setEySummaryPayment7(payment_7_1List);	
		
		List<Ret1PaymentSummarySectionDto> payment_7_2List = eySummaries.get("7(2)");
		Ret1BasicSectionSummaryDto payment_7_2dto = new Ret1BasicSectionSummaryDto();
		payment_7_2dto.setEySummaryPayment7(payment_7_2List);
		
		List<Ret1PaymentSummarySectionDto> payment_7_3List = eySummaries.get("7(3)");
		Ret1BasicSectionSummaryDto payment_7_3dto = new Ret1BasicSectionSummaryDto();
		payment_7_3dto.setEySummaryPayment7(payment_7_3List);
		
		List<Ret1PaymentSummarySectionDto> payment_7_4List = eySummaries.get("7(4)");
		Ret1BasicSectionSummaryDto payment_7_4dto = new Ret1BasicSectionSummaryDto();
		payment_7_4dto.setEySummaryPayment7(payment_7_4List);
		
		Ret1CompleteSummaryDto payment_7 = new Ret1CompleteSummaryDto();
		payment_7.setPayment_7(payment_7dto);
		payment_7.setPayment_7_1(payment_7_1dto);
		payment_7.setPayment7_2((payment_7_2dto));
		payment_7.setPayment7_3((payment_7_3dto));
		payment_7.setPayment7_4((payment_7_4dto));
		
		return (SearchResult<R>) new SearchResult<Ret1CompleteSummaryDto>(payment_7);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
