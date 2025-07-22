package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1B2CStructure;
import com.google.gson.JsonElement;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("Annexure1B2CRespHandler")
public class Annexure1B2CRespHandler {

	@Autowired
	@Qualifier("anx1B2CStructure")
	private Anx1B2CStructure anx1B2CStructure;

	public JsonElement handleB2CResp(
			List<Annexure1SummaryResp1Dto> b2cSummaryRespList) {

		JsonElement b2cRespBody = anx1B2CStructure
				.anx1B2CResp(b2cSummaryRespList);

		return b2cRespBody;
	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumB2CResp(
			List<Annexure1SummaryResp1Dto> b2cSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto b2cRespBody = anx1B2CStructure
				.anx1SumB2CResp(b2cSummaryRespList,gstnResult);

		return b2cRespBody;
	}

}
