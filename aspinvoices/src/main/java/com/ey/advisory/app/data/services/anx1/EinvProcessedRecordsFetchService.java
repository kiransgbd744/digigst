package com.ey.advisory.app.data.services.anx1;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.daos.client.EinvoicProcessedRecordsFetchDaoImpl;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Component("EinvProcessedRecordsFetchService")
public class EinvProcessedRecordsFetchService implements SearchService {

	@Autowired
	@Qualifier("EinvoicProcessedRecordsFetchDaoImpl")
	private EinvoicProcessedRecordsFetchDaoImpl gstr1ProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Transactional(value = "clientTransactionManager")
	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		EinvoiceProcessedReqDto req = (EinvoiceProcessedReqDto) criteria;
		EinvoiceProcessedReqDto reqDto = processedRecordsCommonSecParam
				.setEinvDataSecuritySearchParams(req);
		List<Gstr1ProcessedRecordsRespDto> processedRespDtos = gstr1ProcessedRecordsFetchDao
				.loadEinvProcessedRecords(reqDto);
		return (SearchResult<R>) new SearchResult<Gstr1ProcessedRecordsRespDto>(
				processedRespDtos);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
