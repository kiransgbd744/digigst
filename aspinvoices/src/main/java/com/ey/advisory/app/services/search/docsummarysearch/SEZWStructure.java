package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("SEZWStructure")
public class SEZWStructure {
	

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("SEZWEYFinalStructure")
	private SEZWEYFinalStructure sezwEYFinalStructure;
	
	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement sezwResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultSezwEYList = getDefaultSEZWEYStructure();

		List<Gstr1SummaryRespDto> sezwEYList = sezwEYFinalStructure
				.getSEZWEyList(defaultSezwEYList, eySummaryListFromView);
		
			
		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(sezwEYList);
		
		
		
		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);
		
		Map<String,JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("ey", eyRespBody);
		JsonElement sezwRespbody = gson.toJsonTree(combinedMap);
		
		return sezwRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultSEZWEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultSEZWEY = new ArrayList<>();

		Gstr1SummaryRespDto sezwEy6B = new Gstr1SummaryRespDto();
		sezwEy6B.setTableSection("6B");
		sezwEy6B = defaultStructureUtil.defaultB2CLStructure(sezwEy6B);

		
		defaultSEZWEY.add(sezwEy6B);
		

		return defaultSEZWEY;

	}

}
