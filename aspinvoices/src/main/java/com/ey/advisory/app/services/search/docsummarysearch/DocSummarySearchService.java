package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedBasicSummary;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NillratedBasicSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Mahesh.Golla
 *
 */


@Service("docSummarySearchService")
public class DocSummarySearchService implements SearchService {

	@Autowired
	@Qualifier("DefaultGstr1BasicSummarySectionFetcher")
	private Gstr1BasicSummarySectionFetcher fetcher;
	
	@Autowired
	@Qualifier("DefaultGstr1BasicSummaryDocSectionFetcher")
	private DefaultGstr1BasicSummaryDocSectionFetcher docFetcher;
	
	@Autowired
	@Qualifier("DefaultGstr1BasicSummaryNilSectionFetcher")
	private DefaultGstr1BasicSummaryNilSectionFetcher nilFetcher;

	
	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		Gstr1SummaryReqDto req = (Gstr1SummaryReqDto) criteria;

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
		Gstr1SummaryDto summary = new Gstr1SummaryDto();
		Gstr1BasicSummaryDto b2bSummary = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> b2bEySummaries = fetcher.fetch("B2B",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		
		//Sort the list in Ascending Order
         Collections.sort(b2bEySummaries,
        		 new Comparator<Gstr1BasicSummarySectionDto>() {
			    @Override
			    public int compare(Gstr1BasicSummarySectionDto baseSummary1, 
			    		Gstr1BasicSummarySectionDto baseSummaery2) {
					            return baseSummary1.getTableSection().
					            	compareTo(baseSummaery2.getTableSection()); 
					        }
					    });
         
		b2bSummary.setEySummary(b2bEySummaries);

		// Fetch the B2CL summary.
		Gstr1BasicSummaryDto b2clSummaries = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> b2clEySummary = fetcher.fetch("B2CL",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		b2clSummaries.setEySummary(b2clEySummary);

		// Fetch the B2CS summary.
		Gstr1BasicSummaryDto bcsSummaries = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> b2csEySummary = fetcher.fetch("B2CS",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		bcsSummaries.setEySummary(b2csEySummary);

		// Fetch the B2CSA summary.
		 Gstr1BasicSummaryDto bcsaSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> b2csaEySummaries = fetcher
		 .fetch("B2CSA", "EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		 bcsaSummary.setEySummary(b2csaEySummaries);

		// Fetch the CDNR summary.
		Gstr1BasicSummaryDto cdnrSummary = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> cdnrEySummaries = fetcher.fetch(
				"CDNR", "EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		cdnrSummary.setEySummary(cdnrEySummaries);

		// Fetch the EXP summary.
		Gstr1BasicSummaryDto expSummary = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> expEySummaries = fetcher.fetch("EXP",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		expSummary.setEySummary(expEySummaries);

		// Fetch the HSN summary.
		Gstr1BasicSummaryDto hsnSummary = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> hsnEySummaries = fetcher.fetch("HSN",
				"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		hsnSummary.setEySummary(hsnEySummaries);

		// Fetch the B2BA summary.
		 Gstr1BasicSummaryDto b2baSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> b2baEySummaries = fetcher
		 .fetch("B2BA","EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);

		 b2baSummary.setEySummary(b2baEySummaries);

		// Fetch the B2CLA summary.
		 Gstr1BasicSummaryDto b2claSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> b2claEySummaries = fetcher
		 .fetch("B2CLA", "EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		 b2claSummary.setEySummary(b2claEySummaries);

		// Fetch the CDNRA summary.
		 Gstr1BasicSummaryDto cdnraSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> cdnraEySummaries = fetcher
		 .fetch("CDNRA", "EY",gstins,entityIds,  fromTaxPeriod, toTaxPeriod);
		 cdnraSummary.setEySummary(cdnraEySummaries);

		// Fetch the CDNUR summary.
		 Gstr1BasicSummaryDto cdnurSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> cdnurEySummaries = fetcher
		 .fetch("CDNUR", "EY", gstins,entityIds, fromTaxPeriod, toTaxPeriod);
		 cdnurSummary.setEySummary(cdnurEySummaries);

		// Fetch the CDNURA summary.
		 Gstr1BasicSummaryDto cdnuraSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> cdnuraEySummaries = fetcher
		 .fetch("CDNURA", "EY", gstins,entityIds, fromTaxPeriod, toTaxPeriod);
		 cdnuraSummary.setEySummary(cdnuraEySummaries);

		// Fetch the DOC SERIES
		// summary.
		 Gstr1DocIssuedBasicSummary docSummary =
		 new Gstr1DocIssuedBasicSummary();
		 List<Gstr1DocIssuedSummarySectionDto> docEySummary =
		 docFetcher.fetch("DOC",
		 "EY", gstins,entityIds, fromTaxPeriod, toTaxPeriod);
		 docSummary.setEySummary(docEySummary);
		

		// Fetch the EXPA summary.
		 Gstr1BasicSummaryDto expaSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> expaEySummaries = fetcher
		 .fetch("EXPA", "EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		 expaSummary.setEySummary(expaEySummaries);

		// Fetch the AT summary. (Advance Received)
		 Gstr1BasicSummaryDto atSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> atEySummaries = fetcher.fetch("AT",
		 "EY", gstins,entityIds,fromTaxPeriod, toTaxPeriod);
		 atSummary.setEySummary(atEySummaries);

		// Fetch the ATA summary. (Advance Received Amendment)
		 Gstr1BasicSummaryDto ataSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> ataEySummaries =
		 fetcher.fetch("ATA",
		 "EY", gstins,entityIds,fromTaxPeriod, toTaxPeriod);
		 ataSummary.setEySummary(ataEySummaries);

		// Fetch the NIL summary.
		 Gstr1NillratedBasicSummaryDto nilSummary =
		 new Gstr1NillratedBasicSummaryDto();
		List<Gstr1NilRatedSummarySectionDto> nilEySummary = nilFetcher.fetch(
				"NIL", "EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		 nilSummary.setEySummary(nilEySummary);

		// Fetch the TXPD summary. (Advance Adjusted)
		 Gstr1BasicSummaryDto txpdSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> txpdEySummaries = fetcher
		 .fetch("TXPD", "EY", gstins,entityIds, fromTaxPeriod, toTaxPeriod);
		 txpdSummary.setEySummary(txpdEySummaries);

		// Fetch the TXPDA summary. (Advance Adjusted Amendment)
		 Gstr1BasicSummaryDto txpdaSummary = new Gstr1BasicSummaryDto();
		 List<Gstr1BasicSummarySectionDto> txpdaEySummaries = fetcher
		 .fetch("TXPDA", "EY", gstins,entityIds, fromTaxPeriod, toTaxPeriod);
		 txpdaSummary.setEySummary(txpdaEySummaries);
		 
		 Gstr1BasicSummaryDto sezWOPSummary = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> sezwoEySummaries = fetcher.fetch(
				"SEWOP",
					"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
		//	hsnSummary.setEySummary(hsnEySummaries);
			sezWOPSummary.setEySummary(sezwoEySummaries);
		
		Gstr1BasicSummaryDto sezWPSummary = new Gstr1BasicSummaryDto();
		List<Gstr1BasicSummarySectionDto> sezwEySummaries = fetcher.fetch(
				"SEWP",
					"EY", gstins, entityIds, fromTaxPeriod, toTaxPeriod);
			//	hsnSummary.setEySummary(hsnEySummaries);
				sezWPSummary.setEySummary(sezwEySummaries);



		summary.setB2b(b2bSummary);
	    summary.setB2ba(b2baSummary);
		summary.setB2cl(b2clSummaries);
		summary.setBcla(b2claSummary);
		summary.setB2cs(bcsSummaries);
		summary.setB2csa(bcsaSummary);
		summary.setCdnr(cdnrSummary);
		summary.setExp(expSummary);
		summary.setHsn(hsnSummary);

		 summary.setCdnra(cdnraSummary);
		 summary.setCdnur(cdnurSummary);
		 summary.setCdnura(cdnuraSummary);
		 summary.setExpa(expaSummary);
		 summary.setAt(atSummary);
		 summary.setAta(ataSummary);
		 summary.setTxpd(txpdSummary);
		 summary.setTxpda(txpdaSummary);
		 summary.setDocIssued(docSummary);
		 summary.setNil(nilSummary);
		 summary.setSezwop(sezWOPSummary);
		 summary.setSezwp(sezWPSummary);

		List<Gstr1SummaryDto> list = new ArrayList<>();
		list.add(summary);

		return (SearchResult<R>) new SearchResult<Gstr1SummaryDto>(list);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}
	}
