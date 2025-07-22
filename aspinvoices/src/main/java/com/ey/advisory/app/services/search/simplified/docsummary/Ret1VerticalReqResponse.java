package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.Ret1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1VerticalSummaryRespDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Ret1VerticalReqResponse")
public class Ret1VerticalReqResponse {

	@Autowired
	@Qualifier("Ret1AspVerticalSummaryService")
	Ret1AspVerticalSummaryService aspVertical;
	
	@Autowired
	@Qualifier("Ret1aAspVerticalSummaryService")
	Ret1aAspVerticalSummaryService ret1aAspVertical;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1VerticalReqResponse.class);
	
	Gson gson = GsonUtil.newSAPGsonInstance();
	
	@Transactional(value = "clientTransactionManager")
	public JsonElement retReqRespDetails(Ret1SummaryReqDto req){
		
		
	List<Ret1AspVerticalSummaryDto> aspResult = aspVertical.find(req);
	List<Ret1AspVerticalSummaryDto> gstnDetails = aspVertical.findgstnDetails(req);
	List<Ret1VerticalSummaryRespDto> VerticalDetails = aspVertical.findVerticalDetails(req);
	
	
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
	
	@Transactional(value = "clientTransactionManager")
	public JsonElement ret1AReqRespDetails(Ret1SummaryReqDto req){
		
		
		List<Ret1AspVerticalSummaryDto> aspResult = ret1aAspVertical.find(req);
		List<Ret1AspVerticalSummaryDto> gstnDetails = ret1aAspVertical.findgstnDetails(req);
		List<Ret1VerticalSummaryRespDto> VerticalDetails = ret1aAspVertical.findVerticalDetails(req);
		
		
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

}
