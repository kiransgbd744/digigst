package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Ret1AspDetailSectionDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspDetailRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1AspDetailService")
public class Ret1AspDetailService implements SearchService {

	@Autowired
	@Qualifier("Ret1AspDetailSectionDaoImpl")
	Ret1AspDetailSectionDaoImpl loadData;

	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		Ret1SummaryReqDto req = (Ret1SummaryReqDto) criteria;
		List<Ret1AspDetailRespDto> loadResp = loadData
				.loadBasicSummarySection(req);
		return (SearchResult<R>) new SearchResult<Ret1AspDetailRespDto>(
				loadResp);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
