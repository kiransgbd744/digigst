package com.ey.advisory.app.data.services.anx1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.daos.client.Gstr1ProcessedRecordsFetchDao;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1AProcessedRecordsFetchDao;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1ProcessedRecordsFetchService")
public class Gstr1ProcessedRecordsFetchService implements SearchService {

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsFetchDaoImpl")
	private Gstr1ProcessedRecordsFetchDao gstr1ProcessedRecordsFetchDao;
	
	@Autowired
	@Qualifier("Gstr1AProcessedRecordsFetchDaoImpl")
	private Gstr1AProcessedRecordsFetchDao gstr1AProcessedRecordsFetchDao;
	
	
	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;
	
	@Transactional(value = "clientTransactionManager")
	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		Gstr1ProcessedRecordsReqDto req = (Gstr1ProcessedRecordsReqDto) criteria;
		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParamsForOutward(req);
		
		List<Gstr1ProcessedRecordsRespDto> processedRespDtos = new ArrayList<>();
		if(reqDto.getReturnType()!=null && reqDto.getReturnType().equalsIgnoreCase("GSTR1A"))
		{
			 processedRespDtos = gstr1AProcessedRecordsFetchDao
						.loadGstr1ProcessedRecords(reqDto);
				
		}
		
		else
		{
		 processedRespDtos = gstr1ProcessedRecordsFetchDao
				.loadGstr1ProcessedRecords(reqDto);
		
		}
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("processedRespDtos List is ->{}" + processedRespDtos.size());
				}
	
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
