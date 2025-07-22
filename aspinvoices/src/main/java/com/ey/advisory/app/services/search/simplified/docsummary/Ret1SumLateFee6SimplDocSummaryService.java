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
import com.ey.advisory.app.docs.dto.Ret1LateFeeSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author siva krishna
 *
 */
@Service("Ret1SumLateFee6SimplDocSummaryService")
public class Ret1SumLateFee6SimplDocSummaryService implements SearchService{

	@Autowired
	@Qualifier("Ret1Int_6_SimplDefaultGstr1BasicSummarySectionFetcher")
	Ret1int6SimplBasicSummarySectionFetcher fetcher;
	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		
		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
		
		
		Map<String, List<Ret1LateFeeSummarySectionDto>> eySummaries = fetcher
				.fetch(req);
		
		List<Ret1LateFeeSummarySectionDto> int_6List = eySummaries.get("6");
		Ret1BasicSectionSummaryDto int_6dto = new Ret1BasicSectionSummaryDto();
		int_6dto.setEySummaryforint6(int_6List);
		
		List<Ret1LateFeeSummarySectionDto> int_6_1List = eySummaries.get("6(1)");
		Ret1BasicSectionSummaryDto int_6_1dto = new Ret1BasicSectionSummaryDto();
		int_6_1dto.setEySummaryforint6(int_6_1List);
		
		List<Ret1LateFeeSummarySectionDto> int_6_2List = eySummaries.get("6(2)");
		Ret1BasicSectionSummaryDto int_6_2dto = new Ret1BasicSectionSummaryDto();
		int_6_2dto.setEySummaryforint6(int_6_2List);
		
		List<Ret1LateFeeSummarySectionDto> int_6_3List = eySummaries.get("6(3)");
		Ret1BasicSectionSummaryDto int_6_3dto = new Ret1BasicSectionSummaryDto();
		int_6_3dto.setEySummaryforint6(int_6_3List);
		
		List<Ret1LateFeeSummarySectionDto> int_6_4List = eySummaries.get("6(4)");
		Ret1BasicSectionSummaryDto int_6_4dto = new Ret1BasicSectionSummaryDto();
		int_6_4dto.setEySummaryforint6(int_6_4List);
		
		Ret1CompleteSummaryDto int_6 = new Ret1CompleteSummaryDto();
		int_6.setInt_6(int_6dto);
		int_6.setInt_6_1((int_6_1dto));
		int_6.setInt_6_2((int_6_2dto));
		int_6.setInt_6_3((int_6_3dto));
		int_6.setInt_6_4((int_6_4dto));
		
		return (SearchResult<R>) new SearchResult<Ret1CompleteSummaryDto>(int_6);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
