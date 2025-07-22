/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryEcomResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @author BalaKrishna S
 *
 */
@Service("anx1EcomStructure")
public class Anx1EcomStructure {
	
	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1EcomEYFinalStructure")
	private Anx1EcomEYFinalStructure anx1EcomEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;

	public JsonElement anx1EcomResp(
			List<Annexure1SummaryEcomResp1Dto> ecomEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryEcomResp1Dto> defaultEcom4EYList = getDefaultEcomEYStructure();

		List<Annexure1SummaryEcomResp1Dto> ecomEYList = anx1EcomEYFinalStructure
				.getEcomEyList(defaultEcom4EYList, ecomEySummary);

		JsonElement ecomRespbody = gson.toJsonTree(ecomEYList);

		return ecomRespbody;

	}
	
	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public List<Annexure1SummaryEcomResp1Dto> anx1SumEcomResp(
			List<Annexure1SummaryEcomResp1Dto> ecomEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryEcomResp1Dto> defaultEcom4EYList = getDefaultEcomEYStructure();

		List<Annexure1SummaryEcomResp1Dto> ecomEYList = anx1EcomEYFinalStructure
				.getEcomEyList(defaultEcom4EYList, ecomEySummary);

		return ecomEYList;

	}


	private List<Annexure1SummaryEcomResp1Dto> getDefaultEcomEYStructure() {

		List<Annexure1SummaryEcomResp1Dto> defaultEcomEY = new ArrayList<>();

		Annexure1SummaryEcomResp1Dto ecomtable4 = new Annexure1SummaryEcomResp1Dto();
		ecomtable4.setTableSection("4");
		ecomtable4 = defaultStructureUtil.anx1EcomDefaultStructure(ecomtable4);

		
		defaultEcomEY.add(ecomtable4);
				return defaultEcomEY;
	}


}
