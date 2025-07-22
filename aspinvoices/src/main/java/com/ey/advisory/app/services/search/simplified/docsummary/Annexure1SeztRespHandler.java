package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.google.gson.JsonElement;

@Service("Annexure1SeztRespHandler")
public class Annexure1SeztRespHandler {
	
	
	@Autowired
	@Qualifier("anx1SEZTStructure")
	private Anx1SEZTStructure anx1SEZTStructure;

	public JsonElement handleSeztResp(
			List<Annexure1SummaryResp1Dto> seztSummaryRespList) {

		JsonElement seztRespBody = anx1SEZTStructure
				.anx1SeztResp(seztSummaryRespList);

		return seztRespBody;
	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumSeztResp(
			List<Annexure1SummaryResp1Dto> seztSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto seztRespBody = anx1SEZTStructure
				.anx1SumSeztResp(seztSummaryRespList,gstnResult);

		return seztRespBody;
	}
}
