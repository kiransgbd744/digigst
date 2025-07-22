package com.ey.advisory.app.services.search.datastatussearch;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.DataStatusDao;
import com.ey.advisory.app.data.entities.client.DataStatusEntity;
import com.ey.advisory.app.docs.dto.DataStatusSearchRespDto;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Component("DataStatusnewService")
public class DataStatusnewService implements SearchService {

	
	@Autowired
	@Qualifier("DataStatusSearchScreenDao")
	DataStatusDao dataStatusDao;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		
		DataStatusSearchReqDto req = (DataStatusSearchReqDto) criteria;
		LocalDate dataRecvFrom=req.getDataRecvFrom();
		LocalDate dataRecvTo=req.getDataRecvTo();
		LocalDate fromDocdate=req.getDocDateFrom();
		LocalDate toDocdate=req.getDocDateTo();
		String fromReturnPeriod=req.getRetPeriodFrom();
		String toReturnPeriod=req.getRetPeriodTo();
		List<String> sgstins=req.getSgstins();
		List<Long> entityId=req.getEntityId();
		
		List<DataStatusEntity> response=new ArrayList<DataStatusEntity>();
		List<DataStatusSearchRespDto> basicDataStatus =
				                      new ArrayList<DataStatusSearchRespDto>();
		
		StringBuilder build = new StringBuilder();
		if(sgstins != null && sgstins.size() > 0){
			build.append(" AND SUPPLIER_GSTIN IN :gstin");
		}
		if(dataRecvFrom != null && dataRecvTo!=null){
			build.append(" AND RECEIVED_DATE BETWEEN :dataRecvFrom "
					+ "AND :dataRecvTo ORDER BY RECEIVED_DATE DESC");
		}else if(fromReturnPeriod != null && toReturnPeriod!=null){
			build.append(" AND DERIVED_RET_PERIOD BETWEEN "
					+ ":retPeriodFrom AND :retPeriodTo ORDER BY "
					+ "RECEIVED_DATE DESC");
		}else if(fromDocdate !=null && toDocdate !=null){
			build.append(" AND DOC_DATE BETWEEN :docDateFrom "
					+ "AND :docDateTo ORDER BY RECEIVED_DATE DESC");
		}
		String buildQuery = build.toString().substring(4);
		
		if ((fromReturnPeriod != null && fromReturnPeriod.length() > 0)
				&& (toReturnPeriod!=null && toReturnPeriod.length()>0)) {
			
			response=
					dataStatusDao.dataStatusSection(
							"DERRETPERIOD",
							buildQuery,
							sgstins, 
							fromReturnPeriod,
							toReturnPeriod);
	
			
		}else if ((dataRecvFrom != null && dataRecvFrom.lengthOfYear() > 0) 
				&& (dataRecvTo !=null && dataRecvTo.lengthOfYear()>0)) {
			response=dataStatusDao.dataStatusSection(
					           "DATARECV",buildQuery,sgstins, dataRecvFrom,
					           dataRecvTo);			
		}else if((fromDocdate !=null && fromDocdate.lengthOfYear()>0) 
				&& (toDocdate !=null && toDocdate.lengthOfYear()>0) ){
			response=dataStatusDao.dataStatusSection(
					    "DOCDATE",buildQuery,sgstins, fromDocdate, toDocdate);
			
		}
	
		Integer aspTotal=0;
		Integer errors=0;
		Integer info=0;
		Integer processed=0;
		LocalDate recvdate = null;
		LocalDate documentDate=null;
		Integer gstnProcessed = 0;
		Integer gstnError = 0;
		String aspStatus=null;
		Integer rectifier = 0;
		Integer sapTotal = 0;
		Integer diff = 0;

		if(response !=null && response.size()>0){
			int count=0;
			List<String> gstin=new ArrayList<String>();
			//read the first record and save to to comparison purpose
			recvdate = response.get(0).getReceivedDate();
			documentDate=response.get(0).getDocumentDate();	
			LocalDate compareDate=null;
			if(fromDocdate != null){
				compareDate = response.get(0).getDocumentDate();
			} else {
				compareDate = response.get(0).getReceivedDate();
			}
			for (DataStatusEntity ent : response) {
				count++;
				LocalDate entityDate=null;
				if(fromDocdate != null){
					entityDate = ent.getDocumentDate();
				} else {
					entityDate = ent.getReceivedDate();
				}
				
				if(compareDate.compareTo(entityDate)!=0){
					DataStatusSearchRespDto sectionDataStatus = 
							       new DataStatusSearchRespDto();
					sectionDataStatus.setAspTotal(aspTotal);
					sectionDataStatus.setAspError(errors);
					sectionDataStatus.setAspInfo(info);
					sectionDataStatus.setAspProcessed(processed);
					sectionDataStatus.setReceivedDate(recvdate);
					sectionDataStatus.setDocumentDate(documentDate);
					sectionDataStatus.setGstnError(gstnError);
					sectionDataStatus.setGstnProcessed(gstnProcessed);
					sectionDataStatus.setSapTotal(sapTotal);
					sectionDataStatus.setDiff(diff);
					sectionDataStatus.setRectifier(rectifier);
					sectionDataStatus.setAspStatus(aspStatus);
					basicDataStatus.add(sectionDataStatus);
					aspTotal=0;
					processed=0;
					errors=0;
					info=0;
					gstin=new ArrayList<String>();
					
					
				}
				aspTotal = aspTotal + ent.getAspTotal();
				processed = processed + ent.getAspProcessed();
				errors = errors + ent.getAspError();
				info = info + ent.getAspInfo();
				recvdate = ent.getReceivedDate();
				documentDate=ent.getDocumentDate();
				gstnProcessed= gstnProcessed + ent.getGstnProcessed();
				gstnError= gstnError + ent.getGstnError();
				aspStatus = ent.getAspStatus();
				compareDate=entityDate;
				if(response.size()==count){
					DataStatusSearchRespDto sectionDataStatus =
							                   new DataStatusSearchRespDto();
					sectionDataStatus.setAspTotal(aspTotal);
					sectionDataStatus.setAspError(errors);
					sectionDataStatus.setAspInfo(info);
					sectionDataStatus.setAspProcessed(processed);
					sectionDataStatus.setReceivedDate(recvdate);
					sectionDataStatus.setDocumentDate(documentDate);
					sectionDataStatus.setGstnError(gstnError);
					sectionDataStatus.setGstnProcessed(gstnProcessed);
					sectionDataStatus.setSapTotal(sapTotal);
					sectionDataStatus.setDiff(diff);
					sectionDataStatus.setRectifier(rectifier);
					sectionDataStatus.setAspStatus(aspStatus);
					basicDataStatus.add(sectionDataStatus);
				}
			}
		}else{
			return (SearchResult<R>) new SearchResult<>(basicDataStatus);
		}

		
		return (SearchResult<R>) 
				new SearchResult<DataStatusSearchRespDto>(basicDataStatus);

		
		// TODO Auto-generated method stub
		//return (SearchResult<R>) new SearchResult<DataStatusEntity>(response);

			}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Streams not supported yet!!");
	}
}