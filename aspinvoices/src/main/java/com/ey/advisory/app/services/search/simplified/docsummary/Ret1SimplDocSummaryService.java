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
@Service("Ret1SimplDocSummaryService")
public class Ret1SimplDocSummaryService implements SearchService {

	@Autowired
	@Qualifier("Ret1SimplDefaultGstr1BasicSummarySectionFetcher")
	Ret1SimplDefaultGstr1BasicSummarySectionFetcher fetcher;

	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Ret1SummarySectionDto>> eySummaries = fetcher
				.fetch(req);

		// Total 3A -- 3A+3B+----
		
		List<Ret1SummarySectionDto> a3TotList = eySummaries.get("A3");
		Ret1BasicSectionSummaryDto a_3aTotalList = new Ret1BasicSectionSummaryDto();
		a_3aTotalList.setEySummary(a3TotList);

		
		List<Ret1SummarySectionDto> a3List = eySummaries.get("3A");
		Ret1BasicSectionSummaryDto a_3aList = new Ret1BasicSectionSummaryDto();
		a_3aList.setEySummary(a3List);

		List<Ret1SummarySectionDto> a_a31List = eySummaries.get("3B");
		Ret1BasicSectionSummaryDto a_3a1List = new Ret1BasicSectionSummaryDto();
		a_3a1List.setEySummary(a_a31List);

		List<Ret1SummarySectionDto> a_a32List = eySummaries.get("3C");
		Ret1BasicSectionSummaryDto a_3a2List = new Ret1BasicSectionSummaryDto();
		a_3a2List.setEySummary(a_a32List);

		List<Ret1SummarySectionDto> a_a33List = eySummaries.get("3D");
		Ret1BasicSectionSummaryDto a_3a3List = new Ret1BasicSectionSummaryDto();
		a_3a3List.setEySummary(a_a33List);

		List<Ret1SummarySectionDto> a_a34List = eySummaries.get("3E");
		Ret1BasicSectionSummaryDto a_3a4List = new Ret1BasicSectionSummaryDto();
		a_3a4List.setEySummary(a_a34List);

		List<Ret1SummarySectionDto> a_a35List = eySummaries.get("3F");
		Ret1BasicSectionSummaryDto a_3a5List = new Ret1BasicSectionSummaryDto();
		a_3a5List.setEySummary(a_a35List);

		List<Ret1SummarySectionDto> a_a36List = eySummaries.get("3G");
		Ret1BasicSectionSummaryDto a_3a6List = new Ret1BasicSectionSummaryDto();
		a_3a6List.setEySummary(a_a36List);

		List<Ret1SummarySectionDto> a_a37List = eySummaries.get("3A8");
		Ret1BasicSectionSummaryDto a_3a7List = new Ret1BasicSectionSummaryDto();
		a_3a7List.setEySummary(a_a37List);

		// 3B Total 3B= 3H+3I
		
		List<Ret1SummarySectionDto> b_3BList = eySummaries.get("B3");
		Ret1BasicSectionSummaryDto b_3B = new Ret1BasicSectionSummaryDto();
		b_3B.setEySummary(b_3BList);
		
		
		List<Ret1SummarySectionDto> b_3B1List = eySummaries.get("3H");
		Ret1BasicSectionSummaryDto b_3B1 = new Ret1BasicSectionSummaryDto();
		b_3B1.setEySummary(b_3B1List);

		List<Ret1SummarySectionDto> b_3B2List = eySummaries.get("3J");
		Ret1BasicSectionSummaryDto b_3B2 = new Ret1BasicSectionSummaryDto();
		b_3B2.setEySummary(b_3B2List);

		// 3C Total = 3C1-3C2+3C3-3C4+3C5

		List<Ret1SummarySectionDto> c_3CList = eySummaries.get("C3");
		Ret1BasicSectionSummaryDto c_3C = new Ret1BasicSectionSummaryDto();
		c_3C.setEySummary(c_3CList);

		
		List<Ret1SummarySectionDto> c_3C1List = eySummaries.get("3C1");
		Ret1BasicSectionSummaryDto c_3C1 = new Ret1BasicSectionSummaryDto();
		c_3C1.setEySummary(c_3C1List);

		List<Ret1SummarySectionDto> c_3C2List = eySummaries.get("3C2");
		Ret1BasicSectionSummaryDto c_3C2 = new Ret1BasicSectionSummaryDto();
		c_3C2.setEySummary(c_3C2List);

		List<Ret1SummarySectionDto> c_3C3List = eySummaries.get("3C3");
		Ret1BasicSectionSummaryDto c_3C3 = new Ret1BasicSectionSummaryDto();
		c_3C3.setEySummary(c_3C3List);

		List<Ret1SummarySectionDto> c_3C4List = eySummaries.get("3C4");
		Ret1BasicSectionSummaryDto c_3C4 = new Ret1BasicSectionSummaryDto();
		c_3C4.setEySummary(c_3C4List);

		List<Ret1SummarySectionDto> c_3C5List = eySummaries.get("3C5");
		Ret1BasicSectionSummaryDto c_3C5 = new Ret1BasicSectionSummaryDto();
		c_3C5.setEySummary(c_3C5List);

	    // 3D Total

		List<Ret1SummarySectionDto> d_3DList = eySummaries.get("D3");
		Ret1BasicSectionSummaryDto d_3D = new Ret1BasicSectionSummaryDto();
		d_3D.setEySummary(d_3DList);

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

		
		List<Ret1SummarySectionDto> e_3EList = eySummaries.get("E3");
		Ret1BasicSectionSummaryDto e_3E = new Ret1BasicSectionSummaryDto();
		e_3E.setEySummary(e_3EList);
		
		// 4A Total 

		List<Ret1SummarySectionDto> a_4AList = eySummaries.get("A4");
		Ret1BasicSectionSummaryDto a_4A = new Ret1BasicSectionSummaryDto();
		a_4A.setEySummary(a_4AList);
		
		
		List<Ret1SummarySectionDto> a_4A1List = eySummaries.get("4A1");
		Ret1BasicSectionSummaryDto a_4A1 = new Ret1BasicSectionSummaryDto();
		a_4A1.setEySummary(a_4A1List);
		
		List<Ret1SummarySectionDto> a_4A2List = eySummaries.get("4A2");
		Ret1BasicSectionSummaryDto a_4A2 = new Ret1BasicSectionSummaryDto();
		a_4A2.setEySummary(a_4A2List);

		List<Ret1SummarySectionDto> a_4A3List = eySummaries.get("4A3");
		Ret1BasicSectionSummaryDto a_4A3 = new Ret1BasicSectionSummaryDto();
		a_4A3.setEySummary(a_4A3List);
		List<Ret1SummarySectionDto> a_4A4List = eySummaries.get("4A4");
		Ret1BasicSectionSummaryDto a_4A4 = new Ret1BasicSectionSummaryDto();
		a_4A4.setEySummary(a_4A4List);
		List<Ret1SummarySectionDto> a_4A5List = eySummaries.get("3H");
		Ret1BasicSectionSummaryDto a_4A5 = new Ret1BasicSectionSummaryDto();
		a_4A5.setEySummary(a_4A5List);

		List<Ret1SummarySectionDto> a_4A6List = eySummaries.get("3I");
		Ret1BasicSectionSummaryDto a_4A6 = new Ret1BasicSectionSummaryDto();
		a_4A6.setEySummary(a_4A6List);

		List<Ret1SummarySectionDto> a_4A7List = eySummaries.get("3J");
		Ret1BasicSectionSummaryDto a_4A7 = new Ret1BasicSectionSummaryDto();
		a_4A7.setEySummary(a_4A7List);

		List<Ret1SummarySectionDto> a_4A8List = eySummaries.get("3K");
		Ret1BasicSectionSummaryDto a_4A8 = new Ret1BasicSectionSummaryDto();
		a_4A8.setEySummary(a_4A8List);

		List<Ret1SummarySectionDto> a_4A9List = eySummaries.get("ISD");
		Ret1BasicSectionSummaryDto a_4A9 = new Ret1BasicSectionSummaryDto();
		a_4A9.setEySummary(a_4A9List);
		List<Ret1SummarySectionDto> a_4A10List = eySummaries.get("4A10");
		Ret1BasicSectionSummaryDto a_4A10 = new Ret1BasicSectionSummaryDto();
		a_4A10.setEySummary(a_4A10List);

		List<Ret1SummarySectionDto> a_4A11List = eySummaries.get("4A11");
		Ret1BasicSectionSummaryDto a_4A11 = new Ret1BasicSectionSummaryDto();
		a_4A11.setEySummary(a_4A11List);

		// 4B Total 
		List<Ret1SummarySectionDto> b_4BList = eySummaries.get("B4");
		Ret1BasicSectionSummaryDto b_4B = new Ret1BasicSectionSummaryDto();
		b_4B.setEySummary(b_4BList);

		
		List<Ret1SummarySectionDto> b_4B1List = eySummaries.get("4B1");
		Ret1BasicSectionSummaryDto b_4B1 = new Ret1BasicSectionSummaryDto();
		b_4B1.setEySummary(b_4B1List);

		List<Ret1SummarySectionDto> b_4B2List = eySummaries.get("4B2");
		Ret1BasicSectionSummaryDto b_4B2 = new Ret1BasicSectionSummaryDto();
		b_4B2.setEySummary(b_4B2List);

		List<Ret1SummarySectionDto> b_4B3List = eySummaries.get("4B3");
		Ret1BasicSectionSummaryDto b_4B3 = new Ret1BasicSectionSummaryDto();
		b_4B3.setEySummary(b_4B3List);

		List<Ret1SummarySectionDto> b_4B4List = eySummaries.get("4B4");
		Ret1BasicSectionSummaryDto b_4B4 = new Ret1BasicSectionSummaryDto();
		b_4B4.setEySummary(b_4B4List);

		List<Ret1SummarySectionDto> b_4B5List = eySummaries.get("4B5");
		Ret1BasicSectionSummaryDto b_4B5 = new Ret1BasicSectionSummaryDto();
		b_4B5.setEySummary(b_4B5List);

		List<Ret1SummarySectionDto> c_4CList = eySummaries.get("C4");
		Ret1BasicSectionSummaryDto c_4C = new Ret1BasicSectionSummaryDto();
		c_4C.setEySummary(c_4CList);

		//4D Total 
		List<Ret1SummarySectionDto> d_4DList = eySummaries.get("D4");
		Ret1BasicSectionSummaryDto d_4D = new Ret1BasicSectionSummaryDto();
		d_4D.setEySummary(d_4DList);

		
		List<Ret1SummarySectionDto> d_4D1List = eySummaries.get("4D1");
		Ret1BasicSectionSummaryDto d_4D1 = new Ret1BasicSectionSummaryDto();
		d_4D1.setEySummary(d_4D1List);

		List<Ret1SummarySectionDto> d_4D2List = eySummaries.get("4D2");
		Ret1BasicSectionSummaryDto d_4D2 = new Ret1BasicSectionSummaryDto();
		d_4D2.setEySummary(d_4D2List);

		// 4E Total 
		
		List<Ret1SummarySectionDto> e_4cTotList = eySummaries.get("E4");			
		Ret1BasicSectionSummaryDto e_4E = new Ret1BasicSectionSummaryDto();
		e_4E.setEySummary(e_4cTotList);

		List<Ret1SummarySectionDto> e_4E1List = eySummaries.get("4E1");
		Ret1BasicSectionSummaryDto e_4E1 = new Ret1BasicSectionSummaryDto();
		e_4E1.setEySummary(e_4E1List);

		List<Ret1SummarySectionDto> e_4E2List = eySummaries.get("4E2");
		Ret1BasicSectionSummaryDto e_4E2 = new Ret1BasicSectionSummaryDto();
		e_4E2.setEySummary(e_4E2List);

		List<Ret1SummarySectionDto> e_4E3List = eySummaries.get("4E3");
		Ret1BasicSectionSummaryDto e_4E3 = new Ret1BasicSectionSummaryDto();
		e_4E3.setEySummary(e_4E3List);
		
		// 5 Section Total 

		List<Ret1SummarySectionDto> ey5List = eySummaries.get("5");
		Ret1BasicSectionSummaryDto ey5 = new Ret1BasicSectionSummaryDto();
		ey5.setEySummary(ey5List);
		
		List<Ret1SummarySectionDto> ey51List = eySummaries.get("TDS");
		Ret1BasicSectionSummaryDto ey51 = new Ret1BasicSectionSummaryDto();
		ey51.setEySummary(ey51List);
		List<Ret1SummarySectionDto> ey52List = eySummaries.get("TCS");
		Ret1BasicSectionSummaryDto ey52 = new Ret1BasicSectionSummaryDto();
		ey52.setEySummary(ey52List);

		Ret1CompleteSummaryDto a_3a = new Ret1CompleteSummaryDto();
		//a_3aTotalList
		a_3a.setA3_tot(a_3aTotalList);
		a_3a.setB2c(a_3aList);
		a_3a.setB2b(a_3a1List);
		a_3a.setExpwot(a_3a3List);
		a_3a.setExpwt(a_3a2List);
		a_3a.setSezwot(a_3a5List);
		a_3a.setSezwt(a_3a4List);
		a_3a.setDeemedExp(a_3a6List);
		a_3a.setpPeriod(a_3a7List);
		
		a_3a.setB3_Tot(b_3B);
		a_3a.setRev(b_3B1);
		a_3a.setImps(b_3B2);
		
	//	c_3C
		a_3a.setC3_Tot(c_3C);
		a_3a.setDr(c_3C1);
		a_3a.setCr(c_3C2);
		a_3a.setAdvRec(c_3C3);
		a_3a.setAdvAdj(c_3C4);
		a_3a.setOtherRed(c_3C5);
		
		a_3a.setD_3D(d_3D);
		a_3a.setD_3D1(d_3D1);
		a_3a.setD_3D2(d_3D2);
		a_3a.setD_3D3(d_3D3);
		a_3a.setD_3D4(d_3D4);
		a_3a.setE_3E(e_3E);
		
		a_3a.setA_4A(a_4A);
		a_3a.setA_4A1(a_4A1);
		a_3a.setA_4A2(a_4A2);
		a_3a.setA_4A3(a_4A3);
		a_3a.setA_4A4(a_4A4);
		a_3a.setA_4A5(a_4A5);
		a_3a.setA_4A6(a_4A6);
		a_3a.setA_4A7(a_4A7);
		a_3a.setA_4A8(a_4A8);
		a_3a.setA_4A9(a_4A9);
		a_3a.setA_4A10(a_4A10);
		a_3a.setA_4A11(a_4A11);
		
		a_3a.setB_4B(b_4B);
		a_3a.setB_4B1(b_4B1);
		a_3a.setB_4B2(b_4B2);
		a_3a.setB_4B3(b_4B3);
		a_3a.setB_4B4(b_4B4);
		a_3a.setB_4B5(b_4B5);
		a_3a.setC_4C(c_4C);
		
		a_3a.setD_4D(d_4D);
		a_3a.setD_4D1(d_4D1);
		a_3a.setD_4D2(d_4D2);
		
		a_3a.setE_4E(e_4E);
		a_3a.setE_4E1(e_4E1);
		a_3a.setE_4E2(e_4E2);
		a_3a.setE_4E3(e_4E3);
		
		a_3a.setTdtc_5(ey5);
		a_3a.setTdtc_51(ey51);
		a_3a.setTdtc_52(ey52);
		return (SearchResult<R>) new SearchResult<Ret1CompleteSummaryDto>(a_3a);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
