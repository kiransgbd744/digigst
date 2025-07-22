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
import com.ey.advisory.app.docs.dto.simplified.TaxSectionPaymentSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1APayment6SimplDocSummaryService")
public class Ret1APayment6SimplDocSummaryService implements SearchService{

		@Autowired
		@Qualifier("Ret1A6BasicSummarySectionFetcher")
		Ret1A6BasicSummarySectionFetcher fetcher;
		
		@SuppressWarnings("unchecked")
		@Transactional(value = "clientTransactionManager")
		@Override
		public <R> SearchResult<R> find(SearchCriteria criteria,
				PageRequest pageReq, Class<? extends R> retType) {
			
			Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
					
			Map<String, List<TaxSectionPaymentSummaryDto>> eySummaries = fetcher
					.fetch(req);
			
			List<TaxSectionPaymentSummaryDto> int_6List = eySummaries.get("6");
			Ret1BasicSectionSummaryDto int_6dto = new Ret1BasicSectionSummaryDto();
			int_6dto.setEySummaryPaymentTax6(int_6List);

			List<TaxSectionPaymentSummaryDto> int_61List = eySummaries.get("6(1)");
			Ret1BasicSectionSummaryDto int_61dto = new Ret1BasicSectionSummaryDto();
			int_61dto.setEySummaryPaymentTax6(int_61List);
			
			List<TaxSectionPaymentSummaryDto> int_62List = eySummaries.get("6(2)");
			Ret1BasicSectionSummaryDto int_62dto = new Ret1BasicSectionSummaryDto();
			int_62dto.setEySummaryPaymentTax6(int_62List);
			
			List<TaxSectionPaymentSummaryDto> int_63List = eySummaries.get("6(3)");
			Ret1BasicSectionSummaryDto int_63dto = new Ret1BasicSectionSummaryDto();
			int_63dto.setEySummaryPaymentTax6(int_63List);
			
			List<TaxSectionPaymentSummaryDto> int_64List = eySummaries.get("6(4)");
			Ret1BasicSectionSummaryDto int_64dto = new Ret1BasicSectionSummaryDto();
			int_64dto.setEySummaryPaymentTax6(int_64List);
					
			Ret1CompleteSummaryDto int_6 = new Ret1CompleteSummaryDto();
			
			int_6.setRet1APay6(int_6dto);
			int_6.setRet1APay6_1(int_61dto);
			int_6.setRet1APay6_2(int_62dto);
			int_6.setRet1APay6_3(int_63dto);
			int_6.setRet1APay6_4(int_64dto);
			
			return (SearchResult<R>) new SearchResult<Ret1CompleteSummaryDto>(int_6);
		}

		@Override
		public <R> Stream<R> find(SearchCriteria criteria,
				Class<? extends R> retType) {
			// TODO Auto-generated method stub
			return null;
		}

	
	
}
