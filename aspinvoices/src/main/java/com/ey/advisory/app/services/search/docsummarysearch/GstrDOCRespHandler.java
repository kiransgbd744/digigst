package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.JsonElement;

@Service("GstrDOCRespHandler")
public class GstrDOCRespHandler {
	
	@Autowired
	@Qualifier("Gstr1SummaryStructure")
	private Gstr1SummaryStructure gstr1SummaryStructure;
	
	@Autowired
	@Qualifier("GstrSummarySectService")
	private GstrSummarySectService gstrSummarySectService;
	
	@Autowired
	@Qualifier("GstnDataRetrive")
	private GstnDataRetrive gstnDataRetrive;



	public JsonElement handleDocResp(List<? extends Gstr1SummaryDto> list,
			Gstr1SummaryReqDto gstr1SummaryRequest) {
		
		List<Gstr1DocIssuedSummarySectionDto> docEySummary = new ArrayList<>();
		for (Gstr1SummaryDto gstr1Summary : list) {
			docEySummary = gstr1Summary.getDocIssued().getEySummary();
		}
		SearchResult<Gstr1SummaryDto> docGstnResult = (SearchResult<Gstr1SummaryDto>) 
				gstrSummarySectService.find(gstr1SummaryRequest, null, Gstr1SummaryDto.class);

		List<? extends Gstr1SummaryDto> gstnListResp = docGstnResult
				.getResult();

		List<Gstr1DocIssuedSummarySectionDto> gstnSummary = 
				new ArrayList<Gstr1DocIssuedSummarySectionDto>();
		for (Gstr1SummaryDto gstr1Summary : gstnListResp) {
			gstnSummary = gstr1Summary.getDocIssued().getGstnSummary();
		}
		List<Gstr1DocIssuedSummarySectionDto> gstnList = gstnDataRetrive
				.getDocGstnData(gstnSummary);

/*		List<Gstr1DocIssuedSummarySectionDto> gstnList=new ArrayList<>();
		Gstr1DocIssuedSummarySectionDto gstn1 = new Gstr1DocIssuedSummarySectionDto();
		gstn1.setRecords(0);
		gstn1.setTotalIssued(0);
		gstn1.setNetIssued(0);
		gstn1.setCancelled(0);
		gstnList.add(gstn1);
		
*/		/**
		 * End - Remove above lines once we got gstn live data
		 */

	//	JsonElement docRespBody = gstr1SummaryStructure.docStructure(docEySummary, gstnList);
		
		JsonElement docRespBody = gstr1SummaryStructure
				.docStructure(docEySummary, gstnList);


		
		return docRespBody;
		
		
	}




}
