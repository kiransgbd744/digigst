/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Gstr1BasicDocSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicNilSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstn;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("Gstr1ASimpleDocGstnSummarySearchService")
public class Gstr1ASimpleDocGstnSummarySearchService implements SearchService {

	@Autowired
	@Qualifier("SimpleGstr1ABasicGstnSummarySectionFetcher")
	SimpleGstr1ABasicGstnSummarySectionFetcher fetcher;

	@Autowired
	@Qualifier("gstr1SummaryAtGstnImpl")
	Gstr1SummaryAtGstn gstr1SummaryGstnData;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		if ("UPDATEGSTIN".equalsIgnoreCase(req.getAction())) {

			String gstin = null;
			List<String> gstinList = null;
			Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {
					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						gstin = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}
				}
			}

			String groupCode = TenantContext.getTenantId();
			Gstr1GetInvoicesReqDto reqDto;
			if (gstinList != null && gstinList.size() > 0) {
				for (String gstn : gstinList) {

					reqDto = new Gstr1GetInvoicesReqDto();
					reqDto.setGstin(gstn);
					reqDto.setReturnPeriod(req.getTaxPeriod());
					gstr1SummaryGstnData.getGstr1ASummary(reqDto, groupCode);
					

				}

			}
		}

		Map<String, List<Gstr1SummarySectionDto>> eySummaries = fetcher
				.fetch(req);

		List<Gstr1SummarySectionDto> b2blist = eySummaries.get("B2B");
		Gstr1BasicSectionSummaryDto b2bSummary = new Gstr1BasicSectionSummaryDto();
		b2bSummary.setGstnSummary(b2blist);

		List<Gstr1SummarySectionDto> b2balist = eySummaries.get("B2BA");
		Gstr1BasicSectionSummaryDto b2baSummary = new Gstr1BasicSectionSummaryDto();
		b2baSummary.setGstnSummary(b2balist);

		List<Gstr1SummarySectionDto> b2cllist = eySummaries.get("B2CL");
		Gstr1BasicSectionSummaryDto b2clSummary = new Gstr1BasicSectionSummaryDto();
		b2clSummary.setGstnSummary(b2cllist);

		List<Gstr1SummarySectionDto> b2clalist = eySummaries.get("B2CLA");
		Gstr1BasicSectionSummaryDto b2claSummary = new Gstr1BasicSectionSummaryDto();
		b2claSummary.setGstnSummary(b2clalist);

		List<Gstr1SummarySectionDto> explist = eySummaries.get("EXP");
		Gstr1BasicSectionSummaryDto expSummary = new Gstr1BasicSectionSummaryDto();
		expSummary.setGstnSummary(explist);

		List<Gstr1SummarySectionDto> expalist = eySummaries.get("EXPA");
		Gstr1BasicSectionSummaryDto expaSummary = new Gstr1BasicSectionSummaryDto();
		expaSummary.setGstnSummary(expalist);

		List<Gstr1SummarySectionDto> b2cslist = eySummaries.get("B2CS");
		Gstr1BasicSectionSummaryDto b2csSummary = new Gstr1BasicSectionSummaryDto();
		b2csSummary.setGstnSummary(b2cslist);

		List<Gstr1SummarySectionDto> b2csalist = eySummaries.get("B2CSA");
		Gstr1BasicSectionSummaryDto b2csaSummary = new Gstr1BasicSectionSummaryDto();
		b2csaSummary.setGstnSummary(b2csalist);

		List<Gstr1SummarySectionDto> cdnrB9Lists = eySummaries.get("CDNR");
		Gstr1BasicSectionSummaryDto cdnrSummary = new Gstr1BasicSectionSummaryDto();
		cdnrSummary.setGstnSummary(cdnrB9Lists);

		List<Gstr1SummarySectionDto> cdnraB9Lists = eySummaries.get("CDNRA");
		Gstr1BasicSectionSummaryDto cdnraSummary = new Gstr1BasicSectionSummaryDto();
		cdnraSummary.setGstnSummary(cdnraB9Lists);

		List<Gstr1SummarySectionDto> cdnurB9Lists = eySummaries.get("CDNUR");
		Gstr1BasicSectionSummaryDto cdnurSummary = new Gstr1BasicSectionSummaryDto();
		cdnurSummary.setGstnSummary(cdnurB9Lists);

		List<Gstr1SummarySectionDto> cdnuraB9Lists = eySummaries.get("CDNURA");
		Gstr1BasicSectionSummaryDto cdnuraSummary = new Gstr1BasicSectionSummaryDto();
		cdnuraSummary.setGstnSummary(cdnuraB9Lists);

		List<Gstr1SummarySectionDto> atlist = eySummaries.get("AT");
		Gstr1BasicSectionSummaryDto atSummary = new Gstr1BasicSectionSummaryDto();
		atSummary.setGstnSummary(atlist);

		List<Gstr1SummarySectionDto> atalist = eySummaries.get("ATA");
		Gstr1BasicSectionSummaryDto ataSummary = new Gstr1BasicSectionSummaryDto();
		ataSummary.setGstnSummary(atalist);

		List<Gstr1SummarySectionDto> txpdlist = eySummaries.get("TXPD");
		Gstr1BasicSectionSummaryDto txpdSummary = new Gstr1BasicSectionSummaryDto();
		txpdSummary.setGstnSummary(txpdlist);

		List<Gstr1SummarySectionDto> txpdalist = eySummaries.get("TXPDA");
		Gstr1BasicSectionSummaryDto txpdaSummary = new Gstr1BasicSectionSummaryDto();
		txpdaSummary.setGstnSummary(txpdalist);

		List<Gstr1SummarySectionDto> hsnlist = eySummaries.get("HSN");
		Gstr1BasicSectionSummaryDto hsnSummary = new Gstr1BasicSectionSummaryDto();
		hsnSummary.setGstnSummary(hsnlist);

		List<Gstr1SummarySectionDto> hsnB2blist = eySummaries.get("HSN_B2B");
		Gstr1BasicSectionSummaryDto hsnB2bSummary = new Gstr1BasicSectionSummaryDto();
		hsnB2bSummary.setGstnSummary(hsnB2blist);
		
		List<Gstr1SummarySectionDto> hsnB2clist = eySummaries.get("HSN_B2C");
		Gstr1BasicSectionSummaryDto hsnB2cSummary = new Gstr1BasicSectionSummaryDto();
		hsnB2cSummary.setGstnSummary(hsnB2clist);
		
		Gstr1BasicSectionSummaryDto tbl14SecGstnSummary = new Gstr1BasicSectionSummaryDto();

		if (eySummaries.containsKey(GSTConstants.GSTR1_14)) {
			List<Gstr1SummarySectionDto> tbl14SecList = eySummaries
					.get(GSTConstants.GSTR1_14);
			tbl14SecGstnSummary.setGstnSummary(tbl14SecList);
		} else {
			tbl14SecGstnSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_14));
		}

		Gstr1BasicSectionSummaryDto tbl14ofOneSummary = new Gstr1BasicSectionSummaryDto();
		Gstr1BasicSectionSummaryDto tbl14ofTwoSummary = new Gstr1BasicSectionSummaryDto();
		if (eySummaries.containsKey(GSTConstants.GSTR1_14I)) {
			List<Gstr1SummarySectionDto> tblofOneList = eySummaries
					.get(GSTConstants.GSTR1_14I);
			tbl14ofOneSummary.setGstnSummary(tblofOneList);
		} else {
			tbl14ofOneSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_14I));
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_14II)) {
			List<Gstr1SummarySectionDto> tblofTwoList = eySummaries
					.get(GSTConstants.GSTR1_14II);
			tbl14ofTwoSummary.setGstnSummary(tblofTwoList);
		} else {
			tbl14ofTwoSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_14II));
		}
		Gstr1BasicSectionSummaryDto tbl14AmdSecGstnSummary = new Gstr1BasicSectionSummaryDto();

		if (eySummaries.containsKey(GSTConstants.GSTR1_14A)) {
			List<Gstr1SummarySectionDto> tbl14AmdSecList = eySummaries
					.get(GSTConstants.GSTR1_14A);
			tbl14AmdSecGstnSummary.setGstnSummary(tbl14AmdSecList);
		} else {
			tbl14AmdSecGstnSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_14A));
		}

		Gstr1BasicSectionSummaryDto tbl14AmdOneSummary = new Gstr1BasicSectionSummaryDto();
		Gstr1BasicSectionSummaryDto tbl14AmdTwoSummary = new Gstr1BasicSectionSummaryDto();
		if (eySummaries.containsKey(GSTConstants.GSTR1_14AI)) {
			List<Gstr1SummarySectionDto> tblofOneList = eySummaries
					.get(GSTConstants.GSTR1_14AI);
			tbl14AmdOneSummary.setGstnSummary(tblofOneList);
		} else {
			tbl14AmdOneSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_14AI));
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_14AII)) {
			List<Gstr1SummarySectionDto> tblofTwoList = eySummaries
					.get(GSTConstants.GSTR1_14II);
			tbl14AmdTwoSummary.setGstnSummary(tblofTwoList);
		} else {
			tbl14AmdTwoSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_14AII));
		}

		Gstr1BasicSectionSummaryDto tbl15SecGstnSummary = new Gstr1BasicSectionSummaryDto();

		if (eySummaries.containsKey(GSTConstants.GSTR1_15)) {
			List<Gstr1SummarySectionDto> tbl15SecList = eySummaries
					.get(GSTConstants.GSTR1_15);
			tbl15SecGstnSummary.setGstnSummary(tbl15SecList);
		} else {
			tbl15SecGstnSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_15));
		}

		Gstr1BasicSectionSummaryDto tbl15AmdOneSummary = new Gstr1BasicSectionSummaryDto();
		Gstr1BasicSectionSummaryDto tbl15AmdTwoSummary = new Gstr1BasicSectionSummaryDto();

		if (eySummaries.containsKey(GSTConstants.GSTR1_15AI)) {
			List<Gstr1SummarySectionDto> tbl15AmdofOneList = eySummaries
					.get(GSTConstants.GSTR1_15AI);
			tbl15AmdOneSummary.setGstnSummary(tbl15AmdofOneList);
		} else {
			tbl15AmdOneSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_15AI));
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_15AII)) {
			List<Gstr1SummarySectionDto> tbl15AmdofTwoList = eySummaries
					.get(GSTConstants.GSTR1_15AII);
			tbl15AmdTwoSummary.setGstnSummary(tbl15AmdofTwoList);
		} else {
			tbl15AmdTwoSummary
					.setGstnSummary(defaultGstnValue(GSTConstants.GSTR1_15AII));
		}

		// For Doc Issued
		Map<String, List<Gstr1SummaryDocSectionDto>> fetchDoc = fetcher
				.fetchDoc(req);

		List<Gstr1SummaryDocSectionDto> doclist = fetchDoc.get("DOC_ISSUE");
		Gstr1BasicDocSectionSummaryDto docSummary = new Gstr1BasicDocSectionSummaryDto();
		docSummary.setGstnSummary(doclist);

		Map<String, List<Gstr1SummaryNilSectionDto>> fetchNil = fetcher
				.fetchNil(req);
		List<Gstr1SummaryNilSectionDto> nilList = fetchNil.get("NILEXTNON");
		Gstr1BasicNilSectionSummaryDto nilSummary = new Gstr1BasicNilSectionSummaryDto();
		nilSummary.setGstnSummary(nilList);

		Gstr1CompleteSummaryDto summary = new Gstr1CompleteSummaryDto();
		summary.setB2b(b2bSummary);
		summary.setB2ba(b2baSummary);
		summary.setB2cl(b2clSummary);
		summary.setB2cla(b2claSummary);
		summary.setExp(expSummary);
		summary.setExpa(expaSummary);
		summary.setGstnB2cs(b2csSummary);
		summary.setGstnB2csa(b2csaSummary);
		summary.setGstnCdnr(cdnrSummary);
		summary.setGstnCdnra(cdnraSummary);
		summary.setGstnCdnur(cdnurSummary);
		summary.setGstnCdnura(cdnuraSummary);
		summary.setAt(atSummary);
		summary.setAta(ataSummary);
		summary.setTxpd(txpdSummary);
		summary.setTxpda(txpdaSummary);
		summary.setGstnHsn(hsnSummary);
		summary.setGstnHsnB2b(hsnB2bSummary);
		summary.setGstnHsnB2c(hsnB2cSummary);
		summary.setDocIssues(docSummary);
		summary.setNil(nilSummary);
		summary.setTbl14Sec(tbl14SecGstnSummary);
		summary.setTbl14ofOne(tbl14ofOneSummary);
		summary.setTbl14ofTwo(tbl14ofTwoSummary);
		summary.setTbl14AmdSec(tbl14AmdSecGstnSummary);
		summary.setTbl14AmdOne(tbl14AmdOneSummary);
		summary.setTbl14AmdTwo(tbl14AmdTwoSummary);
		summary.setTbl15Sec(tbl15SecGstnSummary);
		summary.setTbl15AmdOneSec(tbl15AmdOneSummary);
		summary.setTbl15AmdTwoSec(tbl15AmdTwoSummary);

		// List<Gstr1CompleteSummaryDto> list = new ArrayList<>();
		// list.add(summary);
		// return summary;

		return (SearchResult<R>) new SearchResult<Gstr1CompleteSummaryDto>(
				summary);

	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Gstr1SummarySectionDto> defaultGstnValue(String taxDocType) {
		List<Gstr1SummarySectionDto> listDto = new ArrayList<>();
		Gstr1SummarySectionDto secDto = new Gstr1SummarySectionDto();
		Gstr1SummarySectionDto obj = new Gstr1SummarySectionDto();
		obj.setTaxDocType(taxDocType);
		// obj.setRecords((GenUtil.getBigInteger(arr[1])).intValue());
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
