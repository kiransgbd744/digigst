package com.ey.advisory.app.services.doc.gstr1a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1HsnSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1AspVerticalSummaryService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AVerticalReqResponse")
public class Gstr1AVerticalReqResponse {

	@Autowired
	@Qualifier("Gstr1AspVerticalSummaryService")
	Gstr1AspVerticalSummaryService aspVertical;
	
	@Autowired
	@Qualifier("Gstr1AAspDocVerticalSummaryService")
	Gstr1AAspDocVerticalSummaryService aspDocVertical;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AVerticalReqResponse.class);
	
	Gson gson = GsonUtil.newSAPGsonInstance();
	public JsonElement gstr1ReqRespDetails(Annexure1SummaryReqDto req){
		
	//	List<Ret1AspVerticalSummaryDto> aspResult = new ArrayList<>();
	//	List<Ret1AspVerticalSummaryDto> gstnDetails = new ArrayList<>();
		
		List<Ret1AspVerticalSummaryDto>	aspResult = aspVertical.find(req);
		
		
		List<Ret1AspVerticalSummaryDto> gstnDetails = aspVertical.findgstnDetails(req);
		
		List<Gstr1VerticalSummaryRespDto> VerticalDetails = aspVertical.findVerticalDetails(req);
		
		
		JsonElement aspRespBody = gson.toJsonTree(aspResult);
		JsonElement gstnRespBody = gson.toJsonTree(gstnDetails);
		JsonElement vertRespBody = gson.toJsonTree(VerticalDetails);
		Map<String, JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("summary", aspRespBody);
		combinedMap.put("gstnView", gstnRespBody);
		combinedMap.put("verticalData", vertRespBody);
		JsonElement respBody = gson.toJsonTree(combinedMap);
		
		
		return respBody;
	
	}
	
	
	/**
	 * This method shows all doc Summary & Vertical Data 
	 * 
	 * 
	 */
public JsonElement gstr1DocReqRespDetails(Annexure1SummaryReqDto req){
		
		List<Gstr1SummaryDocSectionDto>	aspResult = aspDocVertical.find(req);
		
		
	//	List<Ret1AspVerticalSummaryDto> gstnDetails = aspVertical.findgstnDetails(req);
		
		List<Gstr1SummaryDocSectionDto> VerticalDetails = aspDocVertical.findVerticalDetails(req);
		
		
		JsonElement aspRespBody = gson.toJsonTree(aspResult);
	//	JsonElement gstnRespBody = gson.toJsonTree(gstnDetails);
		JsonElement vertRespBody = gson.toJsonTree(VerticalDetails);
		Map<String, JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("summary", aspRespBody);
	//	combinedMap.put("gstnView", gstnRespBody);
		combinedMap.put("verticalData", vertRespBody);
		JsonElement respBody = gson.toJsonTree(combinedMap);
		
		
		return respBody;
	
	}

/**
 * For Gstr1 Review Summary HSN Popup Screen Implementation
 * @param req
 * @return
 */

public JsonElement gstr1HsnReqRespDetails(Annexure1SummaryReqDto req){
	
	List<Gstr1HsnSummarySectionDto>	aspResult = aspDocVertical.findHsn(req);
	
	
//	List<Ret1AspVerticalSummaryDto> gstnDetails = aspVertical.findgstnDetails(req);
	
//	List<Gstr1SummaryDocSectionDto> VerticalDetails = docVertical.docBasicSummarySection(req);
	
	
	JsonElement aspRespBody = gson.toJsonTree(aspResult);
//	JsonElement gstnRespBody = gson.toJsonTree(gstnDetails);
//	JsonElement vertRespBody = gson.toJsonTree(VerticalDetails);
	Map<String, JsonElement> combinedMap = new HashMap<>();
	
	combinedMap.put("verticalData", aspRespBody);
//	combinedMap.put("gstnView", gstnRespBody);
//	combinedMap.put("verticalData", vertRespBody);
	JsonElement respBody = gson.toJsonTree(combinedMap);
	
	
	return respBody;

}
}
