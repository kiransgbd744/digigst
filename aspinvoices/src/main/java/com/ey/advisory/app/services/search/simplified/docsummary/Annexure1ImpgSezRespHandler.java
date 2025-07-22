package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1ImpgSezStructure;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("annexure1ImpgSezRespHandler")
public class Annexure1ImpgSezRespHandler {

	@Autowired
	@Qualifier("anx1ImpgSezStructure")
	private Anx1ImpgSezStructure anx1ImpgSezStructure;

	public JsonElement handleImpgSezResp(
			List<Annexure1SummaryResp1Dto> impgSezSummaryRespList) {

		JsonElement impgSezRespBody = anx1ImpgSezStructure
				.anx1ImpgSezResp(impgSezSummaryRespList);

		return impgSezRespBody;
	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumImpgSezResp(
			List<Annexure1SummaryResp1Dto> impgSezSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto impgSezRespBody = anx1ImpgSezStructure
				.anx1SumImpgSezResp(impgSezSummaryRespList,gstnResult);

		return impgSezRespBody;
	}

}
