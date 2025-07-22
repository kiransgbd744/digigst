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

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.SumTableData;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Mohana.Dasari
 *
 */

@Service("SimplDocSummarySearchService")
public class SimplDocSummarySearchService implements SearchService {

	@Autowired
	@Qualifier("SimplDefaultGstr1BasicSummarySectionFetcher")
	private SimplBasicSummarySectionFetcher fetcher;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("sumTableData")
	private SumTableData sumTableData;

	@SuppressWarnings("unchecked")
	@Transactional(value = "clientTransactionManager")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		// Convert the incoming from and to taxperiods to derived tax periods
		// Both input tax periods will be in the format MMyyyy (e.g. 022019)
		int taxPeriod = 0;
		if (req.getTaxPeriod() != null && req.getTaxPeriod() != null) {
			taxPeriod = GenUtil.convertTaxPeriodToInt(req.getTaxPeriod());
		}
		/*
		 * List<Long> entityIds = new ArrayList<>(); List<String> gstins =
		 * req.getSgstins(); entityIds = req.getEntityId();
		 * 
		 * if (entityIds == null || entityIds.size() == 0) { String groupCode =
		 * TenantContext.getTenantId(); entityIds = entityInfoRepository
		 * .findEntityIdsByGroupCode(groupCode); }
		 */
		Annexure1SummaryDto summary = new Annexure1SummaryDto();

		Map<String, List<Annexure1SummarySectionDto>> eySummaries = fetcher
				.fetch(req);

		Map<String, List<Annexure1SummarySectionDto>> b2cEySummaries = fetcher
				.fetchb2c(req);

		List<Annexure1SummarySectionDto> _3aLists = b2cEySummaries.get("3A");
		List<Annexure1SummarySectionDto> _3aList = eySummaries.get("3A");
		List<Annexure1SummarySectionDto> aHorData = new ArrayList<>();
		if (_3aLists != null && _3aLists.size() > 0) {
			aHorData.addAll(_3aLists);
		}
		if (_3aList != null && _3aList.size() > 0) {
			aHorData.addAll(_3aList);

		}

		List<Annexure1SummarySectionDto> totalList = new ArrayList<>();
		if (aHorData != null && aHorData.size() > 0) {
			totalList = sumTableData.totalOutwardList(aHorData);
		}
		List<Annexure1SummarySectionDto> _3bList = eySummaries.get("3B");
		List<Annexure1SummarySectionDto> _3cList = eySummaries.get("3C");
		List<Annexure1SummarySectionDto> _3dList = eySummaries.get("3D");
		List<Annexure1SummarySectionDto> _3eList = eySummaries.get("3E");
		List<Annexure1SummarySectionDto> _3fList = eySummaries.get("3F");
		List<Annexure1SummarySectionDto> _3gList = eySummaries.get("3G");

		Annexure1BasicSummaryDto b2cSummary = new Annexure1BasicSummaryDto();
		b2cSummary.setEySummary(totalList);

		Annexure1BasicSummaryDto b2bSummary = new Annexure1BasicSummaryDto();
		b2bSummary.setEySummary(_3bList);

		Annexure1BasicSummaryDto exptSummary = new Annexure1BasicSummaryDto();
		exptSummary.setEySummary(_3cList);

		Annexure1BasicSummaryDto expwtSummary = new Annexure1BasicSummaryDto();
		expwtSummary.setEySummary(_3dList);

		Annexure1BasicSummaryDto seztSummary = new Annexure1BasicSummaryDto();
		seztSummary.setEySummary(_3eList);

		Annexure1BasicSummaryDto sezwtSummary = new Annexure1BasicSummaryDto();
		sezwtSummary.setEySummary(_3fList);

		Annexure1BasicSummaryDto deemedSummary = new Annexure1BasicSummaryDto();
		deemedSummary.setEySummary(_3gList);

		summary.setB2b(b2bSummary);
		summary.setB2c(b2cSummary);
		summary.setExpt(exptSummary);
		summary.setExpwt(expwtSummary);
		summary.setSezt(seztSummary);
		summary.setSezwt(sezwtSummary);
		summary.setDeemedExp(deemedSummary);

		List<Annexure1SummaryDto> list = new ArrayList<>();
		list.add(summary);

		return (SearchResult<R>) new SearchResult<Annexure1SummaryDto>(list);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
