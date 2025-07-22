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
 * @author Mahesh.Golla
 *
 */
@Service("Ret1SumInteAndLateFeeImplDocSummaryService")
public class Ret1SumInteAndLateFeeImplDocSummaryService
		implements SearchService {
	
	
	@Autowired
	@Qualifier("Ret1InteAndLateDefaultFeeBasicSummarySectionFetcher")
	private Ret1InteAndLateDefaultFeeBasicSummarySectionFetcher fetcher;

	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
		
		
		Map<String, List<Ret1LateFeeSummarySectionDto>> eySummaries = fetcher
				.fetch(req);
		
		List<Ret1LateFeeSummarySectionDto> int_5List = eySummaries.get("5");
		Ret1BasicSectionSummaryDto int_5dto = new Ret1BasicSectionSummaryDto();
		int_5dto.setEySummaryforint6(int_5List);
		
		List<Ret1LateFeeSummarySectionDto> int_5_1List = eySummaries.get("5(1)");
		Ret1BasicSectionSummaryDto int_5_1dto = new Ret1BasicSectionSummaryDto();
		int_5_1dto.setEySummaryforint6(int_5_1List);
		
		List<Ret1LateFeeSummarySectionDto> int_5_2List = eySummaries.get("5(2)");
		Ret1BasicSectionSummaryDto int_5_2dto = new Ret1BasicSectionSummaryDto();
		int_5_2dto.setEySummaryforint6(int_5_2List);
		
		List<Ret1LateFeeSummarySectionDto> int_5_3List = eySummaries.get("5(3)");
		Ret1BasicSectionSummaryDto int_5_3dto = new Ret1BasicSectionSummaryDto();
		int_5_3dto.setEySummaryforint6(int_5_3List);
		
		List<Ret1LateFeeSummarySectionDto> int_5_4List = eySummaries.get("5(4)");
		Ret1BasicSectionSummaryDto int_5_4dto = new Ret1BasicSectionSummaryDto();
		int_5_4dto.setEySummaryforint6(int_5_4List);
		
		Ret1CompleteSummaryDto int_5 = new Ret1CompleteSummaryDto();
		int_5.setInt_6(int_5dto);
		int_5.setInt_6_1((int_5_1dto));
		int_5.setInt_6_2((int_5_2dto));
		int_5.setInt_6_3((int_5_3dto));
		int_5.setInt_6_4((int_5_4dto));
		
		return (SearchResult<R>) new SearchResult<Ret1CompleteSummaryDto>(int_5);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
