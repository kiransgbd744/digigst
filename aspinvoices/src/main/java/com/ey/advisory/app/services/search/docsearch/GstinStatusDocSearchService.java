package com.ey.advisory.app.services.search.docsearch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.GstinStatusDocumentDto;
import com.ey.advisory.app.docs.dto.einvoice.GstinStatusDocSearchReqDto;
import com.ey.advisory.app.services.docs.einvoice.GstinStatusDocSearchDataSecParams;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Service("GstinStatusDocSearchService")
public class GstinStatusDocSearchService {

	@Autowired
	@Qualifier("GstinStatusDocSearchDataSecParams")
	private GstinStatusDocSearchDataSecParams basicDocSearchDataSecParams;

	@Autowired
	@Qualifier("GstinStatusTotalCountService")
	private GstinStatusTotalCountService gstinStatusTotalCountService;

	@Autowired
	@Qualifier("GstinStatusDaoImpl")
	private GstinStatusDaoImpl gstinStatusDaoImpl;

	public <R> SearchResult<R> findNew(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		GstinStatusDocSearchReqDto searchParams = (GstinStatusDocSearchReqDto) criteria;

		searchParams = basicDocSearchDataSecParams
				.setDataSecuritySearchParams(searchParams);

		List<GstinStatusDocumentDto> resp = gstinStatusDaoImpl
				.getGstinStatusListing(searchParams, pageReq);

		Integer totalCount = gstinStatusTotalCountService
				.getgstinStatusCount(searchParams);

		return new SearchResult(resp, pageReq, totalCount);
	}

}
