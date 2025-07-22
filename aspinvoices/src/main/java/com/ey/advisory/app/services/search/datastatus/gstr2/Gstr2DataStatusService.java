package com.ey.advisory.app.services.search.datastatus.gstr2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Gstr2DataStatusDao;
import com.ey.advisory.app.data.entities.client.Gstr2DataStatusEntity;
import com.ey.advisory.app.docs.dto.DataStatusSearchRespDto;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Component("Gstr2DataStatusService")
public class Gstr2DataStatusService implements SearchService {
	
	
	@Autowired
	@Qualifier("Gstr2DataStatusDaoImpl")
	private Gstr2DataStatusDao gstr2DataStatusDao;
	

	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		
		DataStatusSearchReqDto req = (DataStatusSearchReqDto) criteria;
		LocalDate recivedFromDate=req.getDataRecvFrom();
		LocalDate recivedToDate=req.getDataRecvTo();
		LocalDate docFromdate=req.getDocDateFrom();
		LocalDate docTodate=req.getDocDateTo();
		String retunPeriodFrom=req.getRetPeriodFrom();
		String retunPeriodTo=req.getRetPeriodTo();
		List<String> sgstins=req.getSgstins();
		
		//List<Long> entityId=req.getEntityId();
		
		
		List<Gstr2DataStatusEntity> gstr2Response = new ArrayList<>();
		List<DataStatusSearchRespDto> basicGstr2DataStatus =
                                      new ArrayList<>();
		
		StringBuilder build = new StringBuilder();
		
		if(sgstins != null && !sgstins.isEmpty()){
			build.append(" AND SUPPLIER_GSTIN IN :gstin");
		}
		
		if(recivedFromDate != null && recivedToDate != null){
			build.append(" AND RECEIVED_DATE BETWEEN :recivedFromDate "
					+ "AND :recivedToDate ORDER BY RECEIVED_DATE DESC");
		}
		
		else if(retunPeriodFrom != null && retunPeriodTo != null){
			build.append(" AND DERIVED_RET_PERIOD BETWEEN "
					+ ":retunPeriodFrom AND :retunPeriodTo ORDER BY "
					+ "RECEIVED_DATE DESC");
		}
		
		else if(docFromdate !=null && docTodate !=null){
			build.append(" AND DOC_DATE BETWEEN :docFromdate "
					+ "AND :docTodate ORDER BY RECEIVED_DATE DESC");
		}
		String buildQuery = build.toString().substring(4);
		
		
		
		if ((retunPeriodFrom != null && retunPeriodFrom.length() > 0)
				&& (retunPeriodTo !=null && retunPeriodTo.length()>0)) {
			
			gstr2Response = gstr2DataStatusDao.dataGstr2StatusSection(
							"RETURN_PERIOD",
							buildQuery,
							sgstins, 
							retunPeriodFrom,
							retunPeriodTo);
	
			
		}
		
		else if ((recivedFromDate != null && recivedFromDate.lengthOfYear() > 0) 
				&& (recivedToDate !=null && recivedToDate.lengthOfYear()>0)) {
			
			gstr2Response = gstr2DataStatusDao.dataGstr2StatusSection(
					           "DATA_RECEIVED",
					           buildQuery,sgstins, 
					           recivedFromDate,
					           recivedToDate);			
		}
		
		else if((docFromdate !=null && docFromdate.lengthOfYear()>0) 
				&& (docTodate !=null && docTodate.lengthOfYear()>0) ){
			
			gstr2Response = gstr2DataStatusDao.dataGstr2StatusSection(
					    "DOC_DATE",
					    buildQuery,
					    sgstins, 
					    docFromdate, 
					    docTodate);
			
		}
		
		
		Integer aspTotal=0;
		Integer errors=0;
		Integer info=0;
		Integer processed=0;
		LocalDate recvdate = null;
		LocalDate documentDate=null;
		Integer gstnProcessed = 0;
		Integer gstnError = 0;
		Integer rectifier = 0;
		Integer sapTotal = 0;
		Integer diff = 0;
		Integer returnPeriod =0;
		
		
		if(gstr2Response != null && !gstr2Response.isEmpty()){
			
			int count=0;
			
			//read the first record and save to to comparison purpose
			
			recvdate = gstr2Response.get(0).getReceivedDate();
			documentDate = gstr2Response.get(0).getDocumentDate();
			returnPeriod = gstr2Response.get(0).getDerivedRetPeriod();
			
			LocalDate compareDate=null;
			Integer compareReturnP =0;
			
			if(docFromdate != null){
				compareDate = gstr2Response.get(0).getDocumentDate();
			} 
			else if(retunPeriodFrom != null){
				compareReturnP = gstr2Response.get(0).getDerivedRetPeriod();
			}
			
			else {
				compareDate = gstr2Response.get(0).getReceivedDate();
			}
			
			for (Gstr2DataStatusEntity ent : gstr2Response) {
				count++;
				LocalDate entityDate=null;
				Integer entityReturnP = 0;
				if(docFromdate != null){
					entityDate = ent.getDocumentDate();
				} 
				else if(retunPeriodFrom != null){
					entityReturnP = ent.getDerivedRetPeriod();
				}
				else {
					entityDate = ent.getReceivedDate();
				}
				if(compareDate != null && entityDate != null){
				if(compareDate.compareTo(entityDate) != 0){
					
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
					basicGstr2DataStatus.add(sectionDataStatus);
					aspTotal=0;
					processed=0;
					errors=0;
					info=0;
				}
				}
				if(compareReturnP.compareTo(entityReturnP) != 0){
					
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
						basicGstr2DataStatus.add(sectionDataStatus);
						aspTotal=0;
						processed=0;
						errors=0;
						info=0;

						
						
					}

				aspTotal = aspTotal + ent.getAspTotal();
				processed = processed + ent.getAspProcessed();
				errors = errors + ent.getAspError();
				info = info + ent.getAspInfo();
				recvdate = ent.getReceivedDate();
				documentDate=ent.getDocumentDate();
				
				compareDate=entityDate;
				compareReturnP = entityReturnP;
				
				if(gstr2Response.size()==count){
					
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
					basicGstr2DataStatus.add(sectionDataStatus);
				}
			}
		}
		else{
			return (SearchResult<R>) new SearchResult<>(basicGstr2DataStatus);
		}

		
		return (SearchResult<R>) 
				new SearchResult<>(basicGstr2DataStatus);

			}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		return null;
	}

}
