package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1ImpgStructure;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("annexure1ImpgRespHandler")
public class Annexure1ImpgRespHandler {

	@Autowired
	@Qualifier("anx1ImpgStructure")
	private Anx1ImpgStructure anx1Impgtructure;

	public JsonElement handleImpgResp(
			List<Annexure1SummaryResp1Dto> impgSummaryRespList) {

		JsonElement impgRespBody = anx1Impgtructure
				.anx1ImpgResp(impgSummaryRespList);
				

		return impgRespBody;
	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumImpgResp(
			List<Annexure1SummaryResp1Dto> impgSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto impgRespBody = anx1Impgtructure
				.anx1SumImpgResp(impgSummaryRespList,gstnResult);

		return impgRespBody;
	}

}
