package com.ey.advisory.app.services.docs.gstr2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.gstr2.Gstr2BasicSummaryDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2BasicSummarySectionFetcher;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2NilBasicSummaryDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2NilBasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2SummaryDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2SummaryReqDto;
import com.ey.advisory.common.GenUtil;

/**
 * Gstr2 Review Summary Service
 * @author Balakrishna.S
 *
 */

@Service("Gstr2DocSummarySearchService")
public class Gstr2DocSummarySearchService {
	
	@Autowired
	@Qualifier("DefaultGstr2BasicSummarySectionFetcher")
	private Gstr2BasicSummarySectionFetcher fetcher;


/*	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
*/	
	public Gstr2SummaryDto find(Gstr2SummaryReqDto criteria) {
	
		Gstr2SummaryReqDto req = (Gstr2SummaryReqDto) criteria;

		// Convert the incoming from and to taxperiods to derived tax periods
		// Both input tax periods will be in the format MMyyyy (e.g. 022019)
		int fromTaxPeriod = 0;
		int toTaxPeriod = 0;
		if(req.getFromTaxPeriod() != null && req.getToTaxPeriod() != null){
		fromTaxPeriod = GenUtil.convertTaxPeriodToInt(req.getFromTaxPeriod());
			toTaxPeriod = GenUtil.convertTaxPeriodToInt(req.getToTaxPeriod());
		}
		List<String> gstins = req.getSgstins();
		List<Long> entityIds = req.getEntityId();
		
		// Fetch the B2B summary
	  Gstr2SummaryDto summary = new Gstr2SummaryDto();
	/*	Gstr2BasicSummaryDto b2bSummary = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> b2bEySummaries = fetcher.fetch("B2B",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		
		Collections.sort(b2bEySummaries,
        		 new Comparator<Gstr2BasicSummarySectionDto>() {
			    @Override
			    public int compare(Gstr2BasicSummarySectionDto baseSummary1, 
			    		Gstr2BasicSummarySectionDto baseSummaery2) {
					            return baseSummary1.getTableSection().
					            	compareTo(baseSummaery2.getTableSection()); 
					        }
					    });
         
		b2bSummary.setEySummary(b2bEySummaries);*/
		
		/*	Gstr2BasicSummaryDto cdnrSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> cdnrEySummary = fetcher.fetch("CDNR",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		cdnrSummaries.setEySummary(cdnrEySummary);
		
		Gstr2BasicSummaryDto cdnurSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> cdnurEySummary = fetcher.fetch("CDNUR",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		cdnurSummaries.setEySummary(cdnurEySummary);
		
		Gstr2BasicSummaryDto ab2bSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> ab2bEySummary = fetcher.fetch("AB2B",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		ab2bSummaries.setEySummary(ab2bEySummary);
		
		Gstr2BasicSummaryDto ab2burSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> ab2burEySummary = fetcher.fetch("AB2B",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		ab2burSummaries.setEySummary(ab2burEySummary);
		
		Gstr2BasicSummaryDto aiosSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> aiosEySummary = fetcher.fetch("AIOS",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		aiosSummaries.setEySummary(aiosEySummary);
		
		Gstr2BasicSummaryDto b2burSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> b2burEySummary = fetcher.fetch("B2BUR",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		b2burSummaries.setEySummary(b2burEySummary);
		
		Gstr2BasicSummaryDto acdnrSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> acdnrEySummary = fetcher.fetch("ACDNR",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		acdnrSummaries.setEySummary(acdnrEySummary);
		
		Gstr2BasicSummaryDto acdnurSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> acdnurEySummary = fetcher.fetch("ACDNUR",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		acdnurSummaries.setEySummary(acdnurEySummary);
		
		Gstr2BasicSummaryDto igcgSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> igcgEySummary = fetcher.fetch("IGCG",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		igcgSummaries.setEySummary(igcgEySummary);
		
		Gstr2BasicSummaryDto imosSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> imosEySummary = fetcher.fetch("IMOS",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		imosSummaries.setEySummary(imosEySummary);
		
		Gstr2BasicSummaryDto aiogSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> aiogEySummary = fetcher.fetch("AIOG",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		aiogSummaries.setEySummary(aiogEySummary);
		
		Gstr2NilBasicSummaryDto nilSummaries = new Gstr2NilBasicSummaryDto();
		List<Gstr2NilBasicSummarySectionDto> nilEySummary = fetcher.fetch("NIL",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		nilSummaries.setEySummary(nilEySummary);
		
		Gstr2BasicSummaryDto apSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> apEySummary = fetcher.fetch("AP",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		apSummaries.setEySummary(apEySummary);
		
		Gstr2BasicSummaryDto atSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> atEySummary = fetcher.fetch("AT",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		atSummaries.setEySummary(atEySummary);
		
		Gstr2BasicSummaryDto apaSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> apaEySummary = fetcher.fetch("APA",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		apaSummaries.setEySummary(apaEySummary);
		
		Gstr2BasicSummaryDto ataSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> ataEySummary = fetcher.fetch("ATA",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		ataSummaries.setEySummary(ataEySummary);
		
		Gstr2BasicSummaryDto itcrSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> itcrEySummary = fetcher.fetch("ITCR",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		itcrSummaries.setEySummary(itcrEySummary);

		Gstr2BasicSummaryDto aitcrSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> aitcrEySummary = fetcher.fetch("AITCR",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		aitcrSummaries.setEySummary(aitcrEySummary);
		
		Gstr2BasicSummaryDto hsnSummaries = new Gstr2BasicSummaryDto();
		List<Gstr2BasicSummarySectionDto> hsnEySummary = fetcher.fetch("HSN",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		hsnSummaries.setEySummary(hsnEySummary);
*/		
		List<Gstr2BasicSummarySectionDto> dto1 = new ArrayList<>();
		Gstr2BasicSummarySectionDto dto =new Gstr2BasicSummarySectionDto();
		dto.setTableSection("-");
		dto.setCess(BigDecimal.ZERO);
		dto.setCgst(BigDecimal.ZERO);
		dto.setIgst(BigDecimal.ZERO);
		dto.setInvValue(BigDecimal.ZERO);
		dto.setItcCess(BigDecimal.ZERO);
		dto.setItcCgst(BigDecimal.ZERO);
		dto.setItcIgst(BigDecimal.ZERO);
		dto.setItcSgst(BigDecimal.ZERO);
		dto.setRecords(0);
		dto.setSgst(BigDecimal.ZERO);
		dto.setTaxableValue(BigDecimal.ZERO);
		dto.setTaxPayble(BigDecimal.ZERO);
		dto1.add(dto);
		
		
		Gstr2BasicSummaryDto summary1 = new Gstr2BasicSummaryDto();
		summary1.setEySummary(dto1);
		summary1.setA2Summary(dto1);
		summary1.setA2PRSummary(dto1);
		//summary1.setA2PRSummary(dto1);
		summary1.setDiff1Summary(dto1);
		summary1.setDiff2Summary(dto1);
		
		
		summary.setB2b(summary1);
		summary.setCdnr(summary1);
		summary.setCdnur(summary1);
		summary.setAb2b(summary1);
		summary.setAb2bur(summary1);
		summary.setAios(summary1);
		summary.setB2bur(summary1);
		summary.setAcdnr(summary1);
		summary.setAcdnur(summary1);
		summary.setIgcg(summary1);
		summary.setImos(summary1);
		summary.setAiog(summary1);
		summary.setAp(summary1);
		summary.setApa(summary1);
		summary.setAt(summary1);
		summary.setAta(summary1);
		summary.setItcr(summary1);
		summary.setAitcr(summary1);
		summary.setHsn(summary1);
		
		Gstr2NilBasicSummaryDto sumDto = new Gstr2NilBasicSummaryDto();
		List<Gstr2NilBasicSummarySectionDto> nilList = new ArrayList<>();
		Gstr2NilBasicSummarySectionDto nildto = new Gstr2NilBasicSummarySectionDto();
		sumDto.setEySummary(nilList);
		sumDto.setA2Summary(nilList);
		sumDto.setA2prSummary(nilList);
		sumDto.setDiff1Summary(nilList);
		sumDto.setDiff2Summary(nilList);
		
		nildto.setRecordCount(0);
		nildto.setTableSection("-");
		nildto.setTotalExempted(BigDecimal.ZERO);
		nildto.setTotalNilRated(BigDecimal.ZERO);
		nildto.setTotalNonGST(BigDecimal.ZERO);
		nilList.add(nildto);
		summary.setNil(sumDto);
		
	/*	summary.setB2b(b2bSummary);
		summary.setCdnr(cdnrSummaries);
		summary.setCdnur(cdnurSummaries);
		summary.setAb2b(ab2bSummaries);
		summary.setAb2bur(ab2burSummaries);
		summary.setAios(aiosSummaries);
		summary.setB2bur(b2burSummaries);
		summary.setAcdnr(acdnrSummaries);
		summary.setAcdnur(acdnurSummaries);
		summary.setIgcg(igcgSummaries);
		summary.setImos(imosSummaries);
		summary.setAiog(aiogSummaries);
		summary.setAp(apSummaries);
		summary.setApa(apaSummaries);
		summary.setAt(atSummaries);
		summary.setAta(ataSummaries);
		summary.setItcr(itcrSummaries);
		summary.setAitcr(aitcrSummaries);
		summary.setHsn(hsnSummaries);
*/		
		List<Gstr2SummaryDto> list = new ArrayList<>();
		list.add(summary);

	//	return (SearchResult<R>) new SearchResult<Gstr2SummaryDto>(list);
		
		return summary;


	}
}
