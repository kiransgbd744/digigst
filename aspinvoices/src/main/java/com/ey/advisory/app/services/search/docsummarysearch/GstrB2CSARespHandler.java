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

@Service("GstrB2CSARespHandler")
public class GstrB2CSARespHandler {
	

	@Autowired
	@Qualifier("Gstr1SummaryStructure")
	private Gstr1SummaryStructure gstr1SummaryStructure;
	
	@Autowired
	@Qualifier("GstrSummarySectService")
	private GstrSummarySectService gstrSummarySectService;
	
	@Autowired
	@Qualifier("GstnDataRetrive")
	private GstnDataRetrive gstnDataRetrive;



	public JsonElement handleB2CSAResp(List<? extends Gstr1SummaryDto> list,
			Gstr1SummaryReqDto gstr1SummaryRequest) {
		
		List<Gstr1BasicSummarySectionDto> b2csaEySummary = new ArrayList<>();
		for (Gstr1SummaryDto gstr1Summary : list) {
			b2csaEySummary = gstr1Summary.getB2csa().getEySummary();
		}
		
		//TO-DO Get GSTN Live Data and add it to gstnList
				/**
				 *Start - Remove above lines once we got gstn live data 
				 */
		
		SearchResult<Gstr1SummaryDto> b2csaGstnResult = 
				(SearchResult<Gstr1SummaryDto>) gstrSummarySectService
				.find(gstr1SummaryRequest, null, Gstr1SummaryDto.class);
		
		List<? extends Gstr1SummaryDto> gstnListResp = b2csaGstnResult.getResult();
		
		List<Gstr1BasicSummarySectionDto> gstnSummary = new ArrayList<>();
		for (Gstr1SummaryDto gstr1Summary : gstnListResp) {
			gstnSummary = gstr1Summary.getB2csa().getGstnSummary();
		}
		List<Gstr1B2BGstnRespDto> gstnList = gstnDataRetrive.getGstnData(gstnSummary);

		      
		/*		List<Gstr1B2BGstnRespDto> gstnList=new ArrayList<>();
				Gstr1B2BGstnRespDto gstn1 = new Gstr1B2BGstnRespDto();
			//	gstn1.setRecords(2);
				gstn1.setTaxPayble(new BigDecimal("100"));
				gstn1.setTaxableValue(new BigDecimal("400"));
			//	gstn1.setInvValue(new BigDecimal("18"));
				gstn1.setIgst(new BigDecimal("1800"));
				gstn1.setCgst(new BigDecimal("240"));
				gstn1.setSgst(new BigDecimal("12000"));
				gstn1.setCess(new BigDecimal("200"));
				gstnList.add(gstn1);
*/				
				/**
				 * End - Remove above lines once we got gstn live data
				 */

				JsonElement b2csaRespBody = gstr1SummaryStructure
				.b2csaStructure(b2csaEySummary,gstnList);
	

				
				return b2csaRespBody;
				
				
			}
}
