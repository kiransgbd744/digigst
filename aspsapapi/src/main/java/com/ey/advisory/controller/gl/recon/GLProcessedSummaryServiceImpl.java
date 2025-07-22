package com.ey.advisory.controller.gl.recon;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Component("GLProcessedSummaryServiceImpl")
public class GLProcessedSummaryServiceImpl implements SearchService {

	@Autowired
	@Qualifier("GLProcessedRecordsFetchDaoImpl")
	private GLProcessedRecordsFetchDaoImpl glProcessedRecordsFetchDaoImpl;

	@Transactional(value = "clientTransactionManager")
	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		GLProcessedSummaryReqDto req = (GLProcessedSummaryReqDto) criteria;
		
		List<GLProcessedRecordsRespDto> processedRespDtos = glProcessedRecordsFetchDaoImpl
				.loadEinvProcessedRecords(req);
		return (SearchResult<R>) new SearchResult<GLProcessedRecordsRespDto>(
				processedRespDtos);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
