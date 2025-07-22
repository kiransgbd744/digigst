package com.ey.advisory.app.services.search.gstnsavesubmit;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.GstnSaveAndSubmitDao;
import com.ey.advisory.app.data.views.client.GstnSaveAndSubmitView;
import com.ey.advisory.app.docs.dto.GstnSaveSubmitReqDto;
import com.ey.advisory.app.docs.dto.GstnSaveSubmitRespDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * This class is responsible for 
 * searching saved and submitted documents to GSTN
 * @author Mohana.Dasari
 *
 */
@Service("GstnSaveSubmitService")
public class GstnSaveSubmitService implements SearchService{
	
	@Autowired
	@Qualifier("GstnSaveAndSubmitDaoImpl")
	private GstnSaveAndSubmitDao gstnSaveAndSubmitDao;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstinInfoRepository;

	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		GstnSaveSubmitReqDto request = (GstnSaveSubmitReqDto)criteria;
		
		List<GstnSaveAndSubmitView> responseFromView = new ArrayList<>();
		List<GstnSaveSubmitRespDto> response = 
								new ArrayList<GstnSaveSubmitRespDto>();
		
		String returnPeriodFrom = request.getRetPeriodFrom();
		String returnPeriodTo = request.getRetPeriodTo();
		if((returnPeriodFrom != null && returnPeriodFrom.length() > 0)
				&& (returnPeriodTo != null && returnPeriodTo.length() > 0)) {
			responseFromView = gstnSaveAndSubmitDao
					.getGstnSaveAdnSubmitDocs(request);
		}
		
		if (responseFromView != null && responseFromView.size() > 0) {
			responseFromView.forEach(gstnSaveAndSubmitView -> {
				GstnSaveSubmitRespDto responseDto = new GstnSaveSubmitRespDto();
				String sgstin = gstnSaveAndSubmitView.getSgstin();
				responseDto.setAuthToken("Active");
				responseDto.setGstin(sgstin);
				String retPeriod = convertTaxPeriodToString(
						gstnSaveAndSubmitView.getDerivedRetPeriod());
				responseDto.setReturnPeriod(retPeriod);
				responseDto.setMakerId("A");
				responseDto.setAspProcessed(
									gstnSaveAndSubmitView.getAspProcessed());
				responseDto.setEntityId(
						gstinInfoRepository.findEntityIdByGstin(sgstin));
				
				responseDto.setAspError(gstnSaveAndSubmitView.getAspError());
				responseDto.setAspInfo(gstnSaveAndSubmitView.getAspInfo());
				responseDto.setAspRectify(0);
				responseDto.setAspTotal(gstnSaveAndSubmitView.getAspTotal());
				responseDto.setGstnProcessed(70);
				responseDto.setGstnError(gstnSaveAndSubmitView.getAspError());
				responseDto.setSaveStatus("Saved");
				responseDto.setStatusCode(30);
				responseDto.setReviewStatus("");
				responseDto.setArn("");
				LocalDate filingDate = LocalDate.of(2019, Month.JANUARY, 2);
				responseDto.setFilingDate(filingDate);
				response.add(responseDto);
			});
		}
		
		return (SearchResult<R>) 
				new SearchResult<GstnSaveSubmitRespDto>(response);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		return null;
	}
	
	public static String convertTaxPeriodToString(Integer taxPeriod) {
		String dateStr = taxPeriod.toString();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
		YearMonth ym = YearMonth.parse(dateStr, formatter);
		int year = ym.getYear();
		int monthValue = ym.getMonthValue();
		StringBuffer monthYear = new StringBuffer();
		String monthValueOf = String.valueOf(monthValue);
		String yearValueOf = String.valueOf(year);
		if(monthValue < 9){
			monthYear.append("0");
			monthYear.append(monthValueOf);
			monthYear.append(yearValueOf);
		}else{
			monthYear.append(monthValueOf);
			monthYear.append(yearValueOf);
		}
		return monthYear.toString();
	}	

}
