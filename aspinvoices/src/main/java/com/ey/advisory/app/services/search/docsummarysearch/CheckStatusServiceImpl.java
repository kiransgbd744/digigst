package com.ey.advisory.app.services.search.docsummarysearch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.CheckStatusDaoImpl;
import com.ey.advisory.app.docs.dto.CheckStatusReqDto;
import com.ey.advisory.app.docs.dto.CheckStatusRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;

@Component("CheckStatusServiceImpl")
public class CheckStatusServiceImpl implements SearchService{

	@Autowired
	@Qualifier("CheckStatusDaoImpl")
	CheckStatusDaoImpl checkStatusDaoImpl;
	
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		
		CheckStatusReqDto request=(CheckStatusReqDto) criteria;
		List<String> sgstins=request.getSgstins();
		List<Long> entityId=request.getEntityId();
		String retPeriodFrom=request.getRetPeriodFrom();
		String retPeriodTo=request.getRetPeriodTo();
		
		List<Gstr1SaveBatchEntity> response=new ArrayList<Gstr1SaveBatchEntity>();
		List<CheckStatusRespDto> basicCheckStatus =
				                      new ArrayList<CheckStatusRespDto>();
		CheckStatusRespDto respDto=new CheckStatusRespDto();
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		StringBuilder build = new StringBuilder();
		if(sgstins != null && sgstins.size() > 0){
			//build.append(" AND SUPPLIER_GSTIN IN :gstin");
			build.append(" AND b.sgstin IN :gstin");
		}
		if(retPeriodFrom != null && retPeriodTo!=null){
			build.append(" AND b.derivedTaxperiod BETWEEN :periodFrom "
					+ "AND :periodTo ORDER BY "
					+ "b.derivedTaxperiod DESC");
		}
		
		String buildQuery = build.toString().substring(4);
		
		if ((retPeriodFrom != null && retPeriodFrom.length() > 0)
				&& (retPeriodTo!=null && retPeriodTo.length()>0)) {
			
			response=checkStatusDaoImpl
					.checkStatusSection(
							buildQuery, sgstins, retPeriodFrom, retPeriodTo);
			
		}
		for(Gstr1SaveBatchEntity entity:response){
			//String s = gson.toJson(entity, CheckStatusRespDto.class);
			respDto=new CheckStatusRespDto();
		//	LocalDateTime date=entity.getCreatedOn();
			LocalDateTime localDateTime = entity.getCreatedOn();
			LocalDate localDate = localDateTime.toLocalDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			String formatDateTime = localDateTime.format(formatter);
			respDto.setCreatedOn(localDate);
			respDto.setId(entity.getId());
			respDto.setSgstin(entity.getSgstin());
			respDto.setStatus(entity.getGstnStatus());
			respDto.setReturnPeriod(entity.getReturnPeriod());
		//	respDto.setTime(localDateTime.toLocalTime());
			respDto.setTime(formatDateTime);
			respDto.setRefId(entity.getRefId());
			respDto.setAction(entity.isDelete());
			basicCheckStatus.add(respDto);
		}
		return (SearchResult<R>) 
				new SearchResult<CheckStatusRespDto>(basicCheckStatus);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Streams not supported yet!!");
	}

}
