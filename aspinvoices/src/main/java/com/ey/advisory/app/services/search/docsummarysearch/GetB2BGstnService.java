/*package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1B2bInvoicesEntity;
import com.ey.advisory.app.data.repositories.client.GstinInfoRepository;
import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.Gstr1B2bInvoicesAtGstnReqDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.app.services.gstr1.jobs.Gstr1AtAtaDataAtGstn;
import com.ey.advisory.app.services.gstr1.jobs.Gstr1AtAtaDataParser;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

@Component("GetB2BGstnService")
public class GetB2BGstnService implements SearchService {

	@Autowired
	@Qualifier("gstr1DataAtGstnImpl")
	Gstr1AtAtaDataAtGstn gstr1DataAtGstn;

	@Autowired
	@Qualifier("gstr1DataParserImpl")
	Gstr1AtAtaDataParser gstr1DataParser;
	
	@Autowired
	@Qualifier("gstinInfoRepository")
	GstinInfoRepository gstinInfoRepository;
	
	@Autowired
	@Qualifier("GstnDateCalculation")
	GstnDateCalculation gstnDateCalculation;
	
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		String groupCode = TenantContext.getTenantId();
		
		Gstr1SummaryReqDto request = (Gstr1SummaryReqDto) criteria;
		//List<Long> entityId = request.getEntityId();
		List<String> sgstins = request.getSgstins();
		String fromTaxPeriod = request.getFromTaxPeriod();
		String toTaxPeriod = request.getToTaxPeriod();
		
		if (null != request.getEntityId()
				&& !request.getEntityId().isEmpty()) {
			//List<String> sgstins = request.getSgstins();
			if (sgstins == null || sgstins.size() == 0) {
				sgstins = gstinInfoRepository
						.findByEntityId(request.getEntityId());
				request.setSgstins(sgstins);
			}
		}
		if(null ==request.getEntityId() && request.getEntityId().isEmpty()){
			if (sgstins == null || sgstins.size() == 0) {
			List<String> gstins=gstinInfoRepository.findByGroupCode(groupCode);
			request.setSgstins(gstins);
			}
		}
		
		
		String taxPeriodFrom = "01" + fromTaxPeriod;
		String taxPeriodTo = "01" + toTaxPeriod;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

		LocalDate startDate = LocalDate.parse(taxPeriodFrom, formatter);
		LocalDate endDate = LocalDate.parse(taxPeriodTo, formatter);

		List<String> ret_Period = gstnDateCalculation
				.findingReturnPeriods(startDate, endDate);
		List<String> ret_Period = getDatesBetweenUsingJava8(startDate, endDate);

		List<Gstr1B2bInvoicesAtGstnReqDto> listReqDto = new ArrayList<>();
		Gstr1B2bInvoicesAtGstnReqDto reqDto = null;
		
		if (sgstins != null && !sgstins.isEmpty()) {
			for (String gstn : sgstins) {
				for (String returnPeriod : ret_Period) {
					reqDto = new Gstr1B2bInvoicesAtGstnReqDto();
					
					reqDto.setGstin(gstn);
					reqDto.setReturnPeriod(returnPeriod);
					listReqDto.add(reqDto);
				}
			}

		} else {
			
			// error			
		}

		BigDecimal igst = new BigDecimal(0.0);
		BigDecimal sgst = new BigDecimal(0.0);
		BigDecimal cgst = new BigDecimal(0.0);
		BigDecimal cess = new BigDecimal(0.0);
		BigDecimal intValue = new BigDecimal(0.0);
		BigDecimal taxableValue = new BigDecimal(0.0);
		BigDecimal taxPayble = new BigDecimal(0.0);
		int records = 0;

		
		 * List<Gstr1B2bInvoicesAtGstnReqDto> listReqDto = new ArrayList<>();
		 * Gstr1B2bInvoicesAtGstnReqDto reqDto = new
		 * Gstr1B2bInvoicesAtGstnReqDto();
		 

		List<GetGstr1B2bInvoicesEntity> response = 
				new ArrayList<GetGstr1B2bInvoicesEntity>();
		List<Gstr1B2BGstnRespDto> getGstnB2BDetl = 
				new ArrayList<Gstr1B2BGstnRespDto>();
		Gstr1B2BGstnRespDto respDto = new Gstr1B2BGstnRespDto();
		List<Gstr1B2BGstnRespDto> responseDetails = 
				new ArrayList<Gstr1B2BGstnRespDto>();
		for (Gstr1B2bInvoicesAtGstnReqDto dto : listReqDto) {

			
			 * String apiResp = gstr1DataAtGstn.findB2bDataAtGstn(dto,
			 * groupCode); response = gstr1DataParser.parseB2bData(dto,
			 * apiResp);
			 
			String apiResp = gstr1DataAtGstn.findB2bDataAtGstn(dto, groupCode,
					"B2B");
			response = gstr1DataParser.parseB2bData(dto, apiResp, "B2B");
			
			 * String apiB2BAResp=gstr1DataAtGstn.findB2bDataAtGstn(dto,
			 * groupCode, "B2BA");
			 

			for (GetGstr1B2bInvoicesEntity entity : response) {
				records++;
				respDto = new Gstr1B2BGstnRespDto();

				if (entity.getTaxRate() != null) {
					taxPayble = taxPayble.add(entity.getTaxRate());
				}

				if (entity.getTaxableValue() != null) {
					taxableValue = taxableValue.add(entity.getTaxableValue());
				}

				if (entity.getInvValue() != null) {
					intValue = intValue.add(entity.getInvValue());
				}

				if (entity.getIgst() != null) {
					igst = igst.add(entity.getIgst());
				}

				if (entity.getSgst() != null) {
					sgst = sgst.add(entity.getSgst());
				}

				if (entity.getCgst() != null) {
					cgst = cgst.add(entity.getCgst());
				}

				if (entity.getCess() != null) {
					cess = cess.add(entity.getCess());
				}
			}
		}
		respDto.setTaxPayble(taxPayble);
		respDto.setTaxableValue(taxableValue);
		respDto.setInvValue(intValue);
		respDto.setIgst(igst);
		respDto.setSgst(sgst);
		respDto.setCgst(cgst);
		respDto.setCess(cess);
		respDto.setRecords(records);
		getGstnB2BDetl.add(respDto);
		return (SearchResult<R>) new SearchResult<Gstr1B2BGstnRespDto>(
				getGstnB2BDetl);

	}

	private List<String> getDatesBetweenUsingJava8(LocalDate startDate,
			LocalDate endDate) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		endDate = endDate.plusMonths(1);
		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		List<LocalDate> date = IntStream.iterate(0, i -> i + 1)
				.limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i))
				.collect(Collectors.toList());

		List<String> str = new ArrayList<String>();
		for (LocalDate d : date) {
			String date_to_string = formatter.format(d);
			String s23 = date_to_string.substring(2, 8);
			str.add(s23);
			// System.out.println(s23);
		}
		Set<String> s = new HashSet<String>(str);
		List l = new ArrayList<>(s);
		System.out.println(l);

		return l;
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Streams not supported yet!!");
	}

}
*/