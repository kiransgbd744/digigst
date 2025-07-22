package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.JsonElement;

@Service("Gstr1B2BRespHandler")
public class Gstr1B2BRespHandler {
	
	
	@Autowired
	@Qualifier("Gstr1SummaryStructure")
	private Gstr1SummaryStructure gstr1SummaryStructure;
	
	/*@Autowired
	@Qualifier("GetB2BGstnService")
	private GetB2BGstnService b2bGstnService;*/
	
	@Autowired
	@Qualifier("GstrSummarySectService")
	private GstrSummarySectService gstrSummarySectService;
	
	@Autowired
	@Qualifier("GstnDataRetrive")
	private GstnDataRetrive gstnDataRetrive;

	public JsonElement handleB2BResp(List<? extends Gstr1SummaryDto> list,
			Gstr1SummaryReqDto gstr1SummaryRequest) {
		
		/*Gstr1SummaryReqDto gstr1SummaryRequest*/
		List<Gstr1BasicSummarySectionDto> b2bEySummary = new ArrayList<>();
		for (Gstr1SummaryDto gstr1Summary : list) {
			b2bEySummary = gstr1Summary.getB2b().getEySummary();
		}
		
	
		
		//TO-DO uncomment below line to get live date
		/*SearchResult<Gstr1B2BGstnRespDto> b2bGstnResult = 
				(SearchResult<Gstr1B2BGstnRespDto>) b2bGstnService
				.find(gstr1SummaryRequest, null, Gstr1B2BGstnRespDto.class);
		
		List<? extends Gstr1B2BGstnRespDto> gstnList = new ArrayList<>();
		gstnList = b2bGstnResult.getResult();*/
		
		SearchResult<Gstr1SummaryDto> b2bGstnResult = 
				(SearchResult<Gstr1SummaryDto>) gstrSummarySectService
				.find(gstr1SummaryRequest, null, Gstr1SummaryDto.class);
		
		List<? extends Gstr1SummaryDto> gstnListResp = b2bGstnResult.getResult();
		
		List<Gstr1BasicSummarySectionDto> gstnSummary = new ArrayList<>();
		for (Gstr1SummaryDto gstr1Summary : gstnListResp) {
			gstnSummary = gstr1Summary.getB2b().getGstnSummary();
		}
		List<Gstr1B2BGstnRespDto> gstnList = gstnDataRetrive.getGstnData(gstnSummary);
		
		//Gstr1BasicSummarySectionDto summary=new Gstr1BasicSummarySectionDto();
		/*List<Gstr1B2BGstnRespDto> gstnList=new ArrayList<>();
		Gstr1B2BGstnRespDto dto=new Gstr1B2BGstnRespDto();
		for(Gstr1BasicSummarySectionDto sumDto : gstnSummary)
		{
			dto.setInvValue(sumDto.getInvValue());
			dto.setIgst(sumDto.getIgst());
			dto.setCgst(sumDto.getCgst());
			dto.setSgst(sumDto.getSgst());
			dto.setTaxableValue(sumDto.getTaxableValue());
			dto.setTaxPayble(sumDto.getTaxPayble());
			dto.setCess(sumDto.getCess());
			dto.setRecords(sumDto.getRecords());
		}
		gstnList.add(dto);*/
		
		/**
		 * Start - Remove below lines once we got gstn live data
		 */
		
	/*	List<Gstr1B2BGstnRespDto> gstnList=new ArrayList<>();
		Gstr1B2BGstnRespDto gstn1 = new Gstr1B2BGstnRespDto();
		gstn1.setRecords(20);
		gstn1.setTaxPayble(new BigDecimal("100"));
		gstn1.setTaxableValue(new BigDecimal("400"));
		gstn1.setInvValue(new BigDecimal("18"));
		gstn1.setIgst(new BigDecimal("1800"));
		gstn1.setCgst(new BigDecimal("24000"));
		gstn1.setSgst(new BigDecimal("12000"));
		gstn1.setCess(new BigDecimal("1700"));
		gstnList.add(gstn1);
*/		
		/**
		 * End - Remove above lines once we got gstn live data
		 */

		JsonElement b2bRespBody = gstr1SummaryStructure
				.b2bStructure(b2bEySummary, gstnList);

		return b2bRespBody;

	}

}
