package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Ret1LateFeeDetailSectionDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeDetailSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Service("Ret1ALateFeeDetailService")
public class Ret1ALateFeeDetailService implements SearchService{

	@Autowired
	@Qualifier("Ret1LateFeeDetailSectionDaoImpl")
	Ret1LateFeeDetailSectionDaoImpl lateFeeData;
	
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
				Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
				List<Ret1LateFeeDetailSummaryDto> lateFeeLoadData = lateFeeData
						.loadBasicSummarySectionRet1A(req);

				return (SearchResult<R>) new SearchResult<Ret1LateFeeDetailSummaryDto>(
						lateFeeLoadData);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
