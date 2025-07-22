/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * @author Siva.Reddy
 *
 */
@Service("Gstr1SimpleEcomSupSearchService")
public class Gstr1SimpleEcomSupSearchService implements SearchService {

	@Autowired
	@Qualifier("SimpleGstr1EcomSupSummarySectionFetcher")
	SimpleGstr1EcomSupSummarySectionFetcher fetcher;

	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		Map<String, List<Gstr1SummarySectionDto>> eySummaries = fetcher
				.fetch(req);
		
		Gstr1BasicSectionSummaryDto tbl14SectionSummary = new Gstr1BasicSectionSummaryDto();

		if (eySummaries.containsKey(GSTConstants.GSTR1_14)) {
			List<Gstr1SummarySectionDto> tbl14SectionList = eySummaries
					.get(GSTConstants.GSTR1_14);
			tbl14SectionSummary.setEySummary(tbl14SectionList);
		} else {
			tbl14SectionSummary
					.setEySummary(defaultDigiGSTValue(GSTConstants.GSTR1_14));
		}

		List<Gstr1SummarySectionDto> tbl14ofOneList = eySummaries
				.get(GSTConstants.GSTR1_14I);
		Gstr1BasicSectionSummaryDto tbl14ofOneSummary = new Gstr1BasicSectionSummaryDto();
		tbl14ofOneSummary.setEySummary(tbl14ofOneList);

		List<Gstr1SummarySectionDto> tbl14ofTwoList = eySummaries
				.get(GSTConstants.GSTR1_14II);
		Gstr1BasicSectionSummaryDto tbl14ofTwoSummary = new Gstr1BasicSectionSummaryDto();
		tbl14ofTwoSummary.setEySummary(tbl14ofTwoList);
		
		Gstr1BasicSectionSummaryDto tbl14AmdSectionSummary = new Gstr1BasicSectionSummaryDto();
		
		if (eySummaries.containsKey(GSTConstants.GSTR1_14A)) {
			List<Gstr1SummarySectionDto> tbl14AmdSectionList = eySummaries
					.get(GSTConstants.GSTR1_14A);
			tbl14AmdSectionSummary.setEySummary(tbl14AmdSectionList);
		} else {
			tbl14AmdSectionSummary
					.setEySummary(defaultDigiGSTValue(GSTConstants.GSTR1_14A));
		}

		Gstr1BasicSectionSummaryDto tbl14AmdOneSummary = new Gstr1BasicSectionSummaryDto();
		Gstr1BasicSectionSummaryDto tbl14AmdTwoSummary = new Gstr1BasicSectionSummaryDto();
		if (eySummaries.containsKey(GSTConstants.GSTR1_14AI)) {
			List<Gstr1SummarySectionDto> tblofOneList = eySummaries
					.get(GSTConstants.GSTR1_14AI);
			tbl14AmdOneSummary.setEySummary(tblofOneList);
		} else {
			tbl14AmdOneSummary
					.setEySummary(defaultDigiGSTValue(GSTConstants.GSTR1_14AI));
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_14AII)) {
			List<Gstr1SummarySectionDto> tblofTwoList = eySummaries
					.get(GSTConstants.GSTR1_14II);
			tbl14AmdTwoSummary.setEySummary(tblofTwoList);
		} else {
			tbl14AmdTwoSummary.setEySummary(
					defaultDigiGSTValue(GSTConstants.GSTR1_14AII));
		}

		Gstr1BasicSectionSummaryDto tbl15SectionSummary = new Gstr1BasicSectionSummaryDto();

		if (eySummaries.containsKey(GSTConstants.GSTR1_15)) {
			List<Gstr1SummarySectionDto> tblSectionList = eySummaries
					.get(GSTConstants.GSTR1_15);
			tbl15SectionSummary.setEySummary(tblSectionList);
		} else {
			tbl15SectionSummary
					.setEySummary(defaultDigiGSTValue(GSTConstants.GSTR1_15));
		}
		List<Gstr1SummarySectionDto> tbl15ofOneList = eySummaries
				.get(GSTConstants.GSTR1_15I);
		Gstr1BasicSectionSummaryDto tbl15ofOneSummary = new Gstr1BasicSectionSummaryDto();
		tbl15ofOneSummary.setEySummary(tbl15ofOneList);

		List<Gstr1SummarySectionDto> tbl15ofTwoList = eySummaries
				.get(GSTConstants.GSTR1_15II);
		Gstr1BasicSectionSummaryDto tbl15ofTwoSummary = new Gstr1BasicSectionSummaryDto();
		tbl15ofTwoSummary.setEySummary(tbl15ofTwoList);

		List<Gstr1SummarySectionDto> tbl15ofThreeList = eySummaries
				.get(GSTConstants.GSTR1_15III);
		Gstr1BasicSectionSummaryDto tbl15ofThreeSummary = new Gstr1BasicSectionSummaryDto();
		tbl15ofThreeSummary.setEySummary(tbl15ofThreeList);

		List<Gstr1SummarySectionDto> tbl15ofFourList = eySummaries
				.get(GSTConstants.GSTR1_15IV);
		Gstr1BasicSectionSummaryDto tbl15ofFourSummary = new Gstr1BasicSectionSummaryDto();
		tbl15ofFourSummary.setEySummary(tbl15ofFourList);

		Gstr1BasicSectionSummaryDto tbl15AmdOneSummary = new Gstr1BasicSectionSummaryDto();
		Gstr1BasicSectionSummaryDto tbl15AmdTwoSummary = new Gstr1BasicSectionSummaryDto();
		Gstr1BasicSectionSummaryDto tbl15AmdThreeSummary = new Gstr1BasicSectionSummaryDto();
		Gstr1BasicSectionSummaryDto tbl15AmdFourSummary = new Gstr1BasicSectionSummaryDto();

		Gstr1BasicSectionSummaryDto tbl15AmdOneSectionSummary = new Gstr1BasicSectionSummaryDto();
		if (eySummaries.containsKey(GSTConstants.GSTR1_15AI)) {
			List<Gstr1SummarySectionDto> tbl15AmdofOneSecList = eySummaries
					.get(GSTConstants.GSTR1_15AI);
			tbl15AmdOneSectionSummary.setEySummary(tbl15AmdofOneSecList);
		} else {
			tbl15AmdOneSectionSummary.setEySummary(
					defaultDigiGSTValue(GSTConstants.GSTR1_15AI));
		}
		
		if (eySummaries.containsKey(GSTConstants.GSTR1_15AIA)) {
			List<Gstr1SummarySectionDto> tbl15AmdofOneList = eySummaries
					.get(GSTConstants.GSTR1_15AIA);
			tbl15AmdOneSummary.setEySummary(tbl15AmdofOneList);
		} else {
			tbl15AmdOneSummary.setEySummary(
					defaultDigiGSTValue(GSTConstants.GSTR1_15AIA));
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_15AIB)) {
			List<Gstr1SummarySectionDto> tbl15AmdofTwoList = eySummaries
					.get(GSTConstants.GSTR1_15AIB);
			tbl15AmdTwoSummary.setEySummary(tbl15AmdofTwoList);
		} else {
			tbl15AmdTwoSummary.setEySummary(
					defaultDigiGSTValue(GSTConstants.GSTR1_15AIB));
		}
		
		Gstr1BasicSectionSummaryDto tbl15AmdTwoSectionSummary = new Gstr1BasicSectionSummaryDto();
		if (eySummaries.containsKey(GSTConstants.GSTR1_15AII)) {
			List<Gstr1SummarySectionDto> tbl15AmdofOneSecList = eySummaries
					.get(GSTConstants.GSTR1_15AII);
			tbl15AmdTwoSectionSummary.setEySummary(tbl15AmdofOneSecList);
		} else {
			tbl15AmdTwoSectionSummary.setEySummary(
					defaultDigiGSTValue(GSTConstants.GSTR1_15AII));
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_15AIIA)) {
			List<Gstr1SummarySectionDto> tbl15AmdofThreeList = eySummaries
					.get(GSTConstants.GSTR1_15AIIA);
			tbl15AmdThreeSummary.setEySummary(tbl15AmdofThreeList);
		} else {
			tbl15AmdThreeSummary.setEySummary(
					defaultDigiGSTValue(GSTConstants.GSTR1_15AIIA));
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_15AIIB)) {
			List<Gstr1SummarySectionDto> tbl15FourofThreeList = eySummaries
					.get(GSTConstants.GSTR1_15AIIB);
			tbl15AmdFourSummary.setEySummary(tbl15FourofThreeList);
		} else {
			tbl15AmdFourSummary.setEySummary(
					defaultDigiGSTValue(GSTConstants.GSTR1_15AIIB));
		}
		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		
		summary.setTbl14Sec(tbl14SectionSummary);
		summary.setTbl14ofOne(tbl14ofOneSummary);
		summary.setTbl14ofTwo(tbl14ofTwoSummary);
		summary.setTbl14AmdSec(tbl14AmdSectionSummary);
		summary.setTbl14AmdOne(tbl14AmdOneSummary);
		summary.setTbl14AmdTwo(tbl14AmdTwoSummary);
		summary.setTbl15ofOne(tbl15ofOneSummary);
		summary.setTbl15ofTwo(tbl15ofTwoSummary);
		summary.setTbl15ofThree(tbl15ofThreeSummary);
		summary.setTbl15dofFour(tbl15ofFourSummary);
		summary.setTbl15Sec(tbl15SectionSummary);
		summary.setTbl15AmdOne(tbl15AmdOneSummary);
		summary.setTbl15AmdTwo(tbl15AmdTwoSummary);
		summary.setTbl15AmdThree(tbl15AmdThreeSummary);
		summary.setTbl15AmdFour(tbl15AmdFourSummary);
		summary.setTbl15AmdOneSec(tbl15AmdOneSectionSummary);
		summary.setTbl15AmdTwoSec(tbl15AmdTwoSectionSummary);

		
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

	private List<Gstr1SummarySectionDto> defaultDigiGSTValue(
			String taxDocType) {
		List<Gstr1SummarySectionDto> listDto = new ArrayList<>();
		Gstr1SummarySectionDto secDto = new Gstr1SummarySectionDto();
		Gstr1SummarySectionDto obj = new Gstr1SummarySectionDto();
		obj.setTaxDocType(taxDocType);
		obj.setRecords(0);
		obj.setInvValue(new BigDecimal(0));
		obj.setIgst(new BigDecimal(0));
		obj.setCgst(new BigDecimal(0));
		obj.setSgst(new BigDecimal(0));
		obj.setCess(new BigDecimal(0));
		obj.setTaxableValue(new BigDecimal(0));
		obj.setTaxPayable(new BigDecimal(0));

		listDto.add(secDto);
		return listDto;
	}

}
