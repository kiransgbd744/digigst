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
import com.ey.advisory.app.docs.dto.Ret1RefundSummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.Ret1Refund8SimplDefaultSectionFetcherImpl;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Service("Ret1SumRefund8SimplDocSummaryService")
public class Ret1SumRefund8SimplDocSummaryService implements SearchService{

	@Autowired
	@Qualifier("Ret1Refund8SimplDefaultSectionFetcherImpl")
	Ret1Refund8SimplDefaultSectionFetcherImpl fetcher;
	
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		
	Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
		
		
		Map<String, List<Ret1RefundSummarySectionDto>> eySummaries = fetcher
				.fetch(req);
		
		List<Ret1RefundSummarySectionDto> refund8List = eySummaries.get("8");
		Ret1BasicSectionSummaryDto refund8t = new Ret1BasicSectionSummaryDto();
		refund8t.setEySummaryRefund8(refund8List);
		
		List<Ret1RefundSummarySectionDto> refund81List = eySummaries.get("8.1");
		Ret1BasicSectionSummaryDto refund8_1 = new Ret1BasicSectionSummaryDto();
		refund8_1.setEySummaryRefund8(refund81List);
		
		List<Ret1RefundSummarySectionDto> refund82List = eySummaries.get("8.2");
		Ret1BasicSectionSummaryDto refund8_2 = new Ret1BasicSectionSummaryDto();
		refund8_2.setEySummaryRefund8(refund82List);
		
		List<Ret1RefundSummarySectionDto> refund83List = eySummaries.get("8.3");
		Ret1BasicSectionSummaryDto refund8_3 = new Ret1BasicSectionSummaryDto();
		refund8_3.setEySummaryRefund8(refund83List);
		
		List<Ret1RefundSummarySectionDto> refund84List = eySummaries.get("8.4");
		Ret1BasicSectionSummaryDto refund8_4 = new Ret1BasicSectionSummaryDto();
		refund8_4.setEySummaryRefund8(refund84List);
		
	
		
		Ret1CompleteSummaryDto refund8 = new Ret1CompleteSummaryDto();
		refund8.setRefund_8(refund8t);
		refund8.setRefund_8_1(refund8_1);
		refund8.setRefund_8_2(refund8_2);
		refund8.setRefund_8_3(refund8_3);
		refund8.setRefund_8_4(refund8_4);
	
		return (SearchResult<R>) new SearchResult<Ret1CompleteSummaryDto>(refund8);
		
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
