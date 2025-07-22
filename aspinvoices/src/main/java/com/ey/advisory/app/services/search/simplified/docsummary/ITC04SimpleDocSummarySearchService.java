package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.simplified.ITC04SummaryRespDto;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("ITC04SimpleDocSummarySearchService")
public class ITC04SimpleDocSummarySearchService implements SearchService {

	@Autowired
	@Qualifier("SimpleITC04BasicSummarySectionFetcher")
	SimpleITC04BasicSummarySectionFetcher fetcher;

	public static final String m2jwsold = "M2JW (Section 4)";
	public static final String jw2m = "JW2M (Section 5A)";
	public static final String otherjw2m = "OtherJW2M (Section 5B)";
	public static final String m2jwsoldFrom = "M2JWSoldfromJW (Section 5C)";

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(value = "clientTransactionManager")
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {

		ITC04RequestDto req = (ITC04RequestDto) criteria;
		Map<String, List<ITC04SummaryRespDto>> fetch = fetcher.fetch(req);

		List<ITC04SummaryRespDto> m2jws = fetch.get(m2jwsold);
		List<ITC04SummaryRespDto> jw2mlist = fetch.get(jw2m);
		List<ITC04SummaryRespDto> otherlist = fetch.get(otherjw2m);
		List<ITC04SummaryRespDto> m2jwslist = fetch.get(m2jwsoldFrom);

		List<ITC04SummaryRespDto> m2jwsFinal = new ArrayList<>();
		ITC04SummaryRespDto m2jwsdto = new ITC04SummaryRespDto();
		
		List<ITC04SummaryRespDto> jw2mFinal = new ArrayList<>();
		ITC04SummaryRespDto jw2mdto = new ITC04SummaryRespDto();
		
		if(m2jws == null){
			m2jwsdto.setTable(m2jwsold);
			m2jwsdto = itc04DefaultStructure(m2jwsdto);
			m2jwsFinal.add(m2jwsdto);
			
		}else{
			
			m2jwsFinal.addAll(m2jws);
			
		}
		if(jw2mlist == null){
			jw2mdto.setTable(jw2m);
			jw2mdto = itc04DefaultStructure(jw2mdto);
			jw2mFinal.add(jw2mdto);
			
		}else{
			
			jw2mFinal.addAll(jw2mlist);
			
		}
		
		List<ITC04SummaryRespDto> otherlistFinal = new ArrayList<>();
		ITC04SummaryRespDto otherDto = new ITC04SummaryRespDto();
		
		if(otherlist == null){
			otherDto.setTable(otherjw2m);
			otherDto = itc04DefaultStructure(otherDto);
			otherlistFinal.add(otherDto);
			
		}else{
			
			otherlistFinal.addAll(otherlist);
			
		}
		
		List<ITC04SummaryRespDto> m2jwslistFinal = new ArrayList<>();
		ITC04SummaryRespDto m2jwsFromDto = new ITC04SummaryRespDto();
		
		if(m2jwslist == null){
			m2jwsFromDto.setTable(m2jwsoldFrom);
			m2jwsFromDto = itc04DefaultStructure(m2jwsFromDto);
			m2jwslistFinal.add(m2jwsFromDto);
			
		}else{
			
			m2jwslistFinal.addAll(m2jwslist);
			
		}
		
		List<ITC04SummaryRespDto> list = new ArrayList<>();
		list.addAll(m2jwsFinal);
		list.addAll(jw2mFinal);
		list.addAll(otherlistFinal);
		list.addAll(m2jwslistFinal);
		return (SearchResult<R>) new SearchResult<ITC04SummaryRespDto>(list);

	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ITC04SummaryRespDto itc04DefaultStructure(
			ITC04SummaryRespDto m2jwsDto) {

		m2jwsDto.setAspCount(0);
		m2jwsDto.setAspTaxableValue(new BigDecimal("0.0"));
		m2jwsDto.setGstnCount(0);
		m2jwsDto.setGstnTaxableValue(new BigDecimal("0.0"));
		m2jwsDto.setDiffCount(0);
		m2jwsDto.setDiffTaxableValue(new BigDecimal("0.0"));
		
		return m2jwsDto;
	}

}
