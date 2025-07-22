package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Ret1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
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
@Service("Ret1ASimplDocSummaryService")
public class Ret1ASimplDocSummaryService implements SearchService{

	@Autowired
	@Qualifier("Ret1ASimplDefaultGstr1BasicSummarySectionFetcher")
	
	Ret1ASimplDefaultGstr1BasicSummarySectionFetcher fetcher;
	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		
		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
		
		
		Map<String, List<Ret1SummarySectionDto>> eySummaries = fetcher
				.fetch(req);

		
		List<Ret1SummarySectionDto> a3List = eySummaries.get("A3");
		Ret1BasicSectionSummaryDto a_3aList = new Ret1BasicSectionSummaryDto();
		a_3aList.setEySummary(a3List);
		
		List<Ret1SummarySectionDto> a31List = eySummaries.get("3A");
		Ret1BasicSectionSummaryDto a_3a1List = new Ret1BasicSectionSummaryDto();
		a_3a1List.setEySummary(a31List);
		List<Ret1SummarySectionDto> a32List = eySummaries.get("3C");
		Ret1BasicSectionSummaryDto a_32aList = new Ret1BasicSectionSummaryDto();
		a_32aList.setEySummary(a32List);
		List<Ret1SummarySectionDto> a33List = eySummaries.get("3D");
		Ret1BasicSectionSummaryDto a_33aList = new Ret1BasicSectionSummaryDto();
		a_33aList.setEySummary(a33List);
		List<Ret1SummarySectionDto> a34List = eySummaries.get("3A4");
		Ret1BasicSectionSummaryDto a_34aList = new Ret1BasicSectionSummaryDto();
		a_34aList.setEySummary(a34List);
		
		List<Ret1SummarySectionDto> a_a3bList = eySummaries.get("B3");
		Ret1BasicSectionSummaryDto a_3bList = new Ret1BasicSectionSummaryDto();
		a_3bList.setEySummary(a_a3bList);
		
		List<Ret1SummarySectionDto> a_a3b1List = eySummaries.get("3H");
		Ret1BasicSectionSummaryDto a_3b1List = new Ret1BasicSectionSummaryDto();
		a_3b1List.setEySummary(a_a3b1List);
		List<Ret1SummarySectionDto> a_a3b2List = eySummaries.get("3I");
		Ret1BasicSectionSummaryDto a_3b2List = new Ret1BasicSectionSummaryDto();
		a_3b2List.setEySummary(a_a3b2List);
		
		List<Ret1SummarySectionDto> c_3CList = eySummaries.get("C3");
		Ret1BasicSectionSummaryDto a_3cList = new Ret1BasicSectionSummaryDto();
		a_3cList.setEySummary(c_3CList);
		
		List<Ret1SummarySectionDto> c_3C1List = eySummaries.get("3C1");
		Ret1BasicSectionSummaryDto a_3c1List = new Ret1BasicSectionSummaryDto();
		a_3c1List.setEySummary(c_3C1List);
		
		List<Ret1SummarySectionDto> a_a33List = eySummaries.get("D3");
		Ret1BasicSectionSummaryDto a_3a3List = new Ret1BasicSectionSummaryDto();
		a_3a3List.setEySummary(a_a33List);
		
		List<Ret1SummarySectionDto> d_3D1List = eySummaries.get("3D1");
		Ret1BasicSectionSummaryDto d_3D1 = new Ret1BasicSectionSummaryDto();
		d_3D1.setEySummary(d_3D1List);

		List<Ret1SummarySectionDto> d_3D2List = eySummaries.get("3D2");
		Ret1BasicSectionSummaryDto d_3D2 = new Ret1BasicSectionSummaryDto();
		d_3D2.setEySummary(d_3D2List);

		List<Ret1SummarySectionDto> d_3D3List = eySummaries.get("3D3");
		Ret1BasicSectionSummaryDto d_3D3 = new Ret1BasicSectionSummaryDto();
		d_3D3.setEySummary(d_3D3List);

		List<Ret1SummarySectionDto> d_3D4List = eySummaries.get("3D4");
		Ret1BasicSectionSummaryDto d_3D4 = new Ret1BasicSectionSummaryDto();
		d_3D4.setEySummary(d_3D4List);
		
		List<Ret1SummarySectionDto> a_a34List = eySummaries.get("E3");
		Ret1BasicSectionSummaryDto a_3a4List = new Ret1BasicSectionSummaryDto();
		a_3a4List.setEySummary(a_a34List);
		
		List<Ret1SummarySectionDto> a_4AList = eySummaries.get("A4");
		Ret1BasicSectionSummaryDto a_4A = new Ret1BasicSectionSummaryDto();
		a_4A.setEySummary(a_4AList);
		List<Ret1SummarySectionDto> a_4A1List = eySummaries.get("3H");
		Ret1BasicSectionSummaryDto a_4A1 = new Ret1BasicSectionSummaryDto();
		a_4A1.setEySummary(a_4A1List);
		List<Ret1SummarySectionDto> a_4A2List = eySummaries.get("3I");
		Ret1BasicSectionSummaryDto a_4A2 = new Ret1BasicSectionSummaryDto();
		a_4A2.setEySummary(a_4A2List);

		List<Ret1SummarySectionDto> a_4A3List = eySummaries.get("3J");
		Ret1BasicSectionSummaryDto a_4A3 = new Ret1BasicSectionSummaryDto();
		a_4A3.setEySummary(a_4A3List);
		List<Ret1SummarySectionDto> a_4A4List = eySummaries.get("3K");
		Ret1BasicSectionSummaryDto a_4A4 = new Ret1BasicSectionSummaryDto();
		a_4A4.setEySummary(a_4A4List);
		List<Ret1SummarySectionDto> a_4A5List = eySummaries.get("4A5");
		Ret1BasicSectionSummaryDto a_4A5 = new Ret1BasicSectionSummaryDto();
		a_4A5.setEySummary(a_4A5List);
		List<Ret1SummarySectionDto> b_4BList = eySummaries.get("B4");
		Ret1BasicSectionSummaryDto b_4B = new Ret1BasicSectionSummaryDto();
		b_4B.setEySummary(b_4BList);
		
		
		List<Ret1SummarySectionDto> b_4B1List = eySummaries.get("4B1");
		Ret1BasicSectionSummaryDto b_4B1 = new Ret1BasicSectionSummaryDto();
		b_4B1.setEySummary(b_4B1List);

		List<Ret1SummarySectionDto> b_4B2List = eySummaries.get("4B2");
		Ret1BasicSectionSummaryDto b_4B2 = new Ret1BasicSectionSummaryDto();
		b_4B2.setEySummary(b_4B2List);
		
		List<Ret1SummarySectionDto> add4a4b = add4A4B(a_4AList,b_4BList);
		Ret1BasicSectionSummaryDto c_4c = new Ret1BasicSectionSummaryDto();
		c_4c.setEySummary(add4a4b);
		Ret1CompleteSummaryDto a_3a = new Ret1CompleteSummaryDto();
		
		a_3a.setA3Total(a_3aList);
		a_3a.setB2c_a(a_3a1List);
		a_3a.setExpwt_a(a_32aList);
		a_3a.setExpwot_a(a_33aList);
		a_3a.setPriorPeriod_a(a_34aList);
		
		a_3a.setA_inwardsupplies(a_3bList);
		a_3a.setInwatd_3h(a_3b1List);
		a_3a.setInwatd_3i(a_3b2List);
		
		a_3a.setCrdr_a(a_3cList);
		a_3a.setOthers_a(a_3c1List);
		
	
		a_3a.setA_suppliesNoLiability(a_3a3List);
		a_3a.setA_nil(d_3D1);
		a_3a.setA_nonGst(d_3D2);
		a_3a.setA_outwardsupplies(d_3D3);
		a_3a.setA_sezTodta(d_3D4);
		
		a_3a.setNLlibility_a(a_3a4List);
		
		a_3a.setA_itcNOtherClaims(a_3a3List);
		a_3a.setSec4_3h(a_4A1);
		a_3a.setSec4_3i(a_4A2);
		a_3a.setSec4_3j(a_4A3);
		a_3a.setSec4_3k(a_4A4);
		a_3a.setSec4_4a5(a_4A5);
		
		a_3a.setReverser_a(b_4B);
		a_3a.setEi_a(b_4B1);
		a_3a.setItc_a(b_4B2);
		a_3a.setC_4C(c_4c);
		return (SearchResult<R>) new SearchResult<Ret1CompleteSummaryDto>(a_3a);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Ret1SummarySectionDto> add4A4B(List<Ret1SummarySectionDto> a4,
			List<Ret1SummarySectionDto> b4) {

		Ret1SummarySectionDto a4FinalDto = new Ret1SummarySectionDto();
		List<Ret1SummarySectionDto> a4FinalDtolist = new ArrayList<Ret1SummarySectionDto>();
		
		Ret1SummarySectionDto a4dto = new Ret1SummarySectionDto();
		Ret1SummarySectionDto b4dto = new Ret1SummarySectionDto();
		if(a4 != null && a4.size()>0){
		 a4dto = a4.get(0);
		}
		if(b4 != null && b4.size()>0){
		 b4dto = b4.get(0);
		}
		
		a4FinalDto.setTable("C4");
		a4FinalDto.setSupplyType("itcAvailable");
		a4FinalDto.setTaxableValue(
				subMethod(a4dto.getTaxableValue(), b4dto.getTaxableValue()));
		a4FinalDto
				.setAspIgst(subMethod(a4dto.getAspIgst(), b4dto.getAspIgst()));

		a4FinalDto
				.setAspCgst(subMethod(a4dto.getAspCgst(), b4dto.getAspCgst()));
		a4FinalDto
				.setAspSgst(subMethod(a4dto.getAspSgst(), b4dto.getAspSgst()));
		a4FinalDto
				.setAspCess(subMethod(a4dto.getAspCess(), b4dto.getAspCess()));
		a4FinalDtolist.add(a4FinalDto);
		return a4FinalDtolist;

	}
	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}
}
