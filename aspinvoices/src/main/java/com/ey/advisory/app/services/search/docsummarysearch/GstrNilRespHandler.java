package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.JsonElement;

@Service("GstrNilRespHandler")
public class GstrNilRespHandler {
	

	@Autowired
	@Qualifier("Gstr1SummaryStructure")
	private Gstr1SummaryStructure gstr1SummaryStructure;
	
	@Autowired
	@Qualifier("GstrSummarySectService")
	private GstrSummarySectService gstrSummarySectService;
	
	@Autowired
	@Qualifier("GstnDataRetrive")
	private GstnDataRetrive gstnDataRetrive;



	public JsonElement handleNilResp(List<? extends Gstr1SummaryDto> list,
			Gstr1SummaryReqDto gstr1SummaryRequest) {
		
		/*,Gstr1SummaryReqDto gstr1SummaryRequest*/
		
		List<Gstr1NilRatedSummarySectionDto> nilEySummary = new ArrayList<>();
		for (Gstr1SummaryDto gstr1Summary : list) {
			nilEySummary = gstr1Summary.getNil().getEySummary();
		}
		
		//TO-DO Get GSTN Live Data and add it to gstnList
				/**
				 *Start - Remove above lines once we got gstn live data 
				 */
		 
		SearchResult<Gstr1SummaryDto> b2claGstnResult = 
				(SearchResult<Gstr1SummaryDto>) gstrSummarySectService
				.find(gstr1SummaryRequest, null, Gstr1SummaryDto.class);
		
		List<? extends Gstr1SummaryDto> gstnListResp = b2claGstnResult.getResult();
		
		List<Gstr1NilRatedSummarySectionDto> gstnSummary = 
				new ArrayList<Gstr1NilRatedSummarySectionDto>();
		for (Gstr1SummaryDto gstr1Summary : gstnListResp) {
			gstnSummary = gstr1Summary.getNil().getGstnSummary();
		}
		List<Gstr1NilRatedSummarySectionDto> gstnList = gstnDataRetrive
				.getNilGstnData(gstnSummary);

		
	/*	List<Gstr1NilRatedSummarySectionDto> gstnList = 
				new ArrayList<Gstr1NilRatedSummarySectionDto>();
				Gstr1NilRatedSummarySectionDto gstn1 = 
						new Gstr1NilRatedSummarySectionDto();
				
				gstn1.setRecordCount(2);
				gstn1.setTotalExempted(new BigDecimal(1000.0));
				gstn1.setTotalNilRated(new BigDecimal(2000.00));
				gstn1.setTotalNonGST(new BigDecimal(210.00));
				gstnList.add(gstn1);
				
*/				/**
				 * End - Remove above lines once we got gstn live data
				 */

				JsonElement b2claRespBody = gstr1SummaryStructure
				.nilStructure(nilEySummary,gstnList);
	

				
				return b2claRespBody;
				
				
			}




}
