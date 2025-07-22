/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryEcomResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1EcomStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1RevStructure;
import com.google.gson.JsonElement;

/**
 * @author BalaKrishna S
 *
 */
@Service("annexure1EcomRespHandler")
public class Annexure1EcomRespHandler {

	@Autowired
	@Qualifier("anx1EcomStructure")
	private Anx1EcomStructure anx1EcomStructure;

	public JsonElement handleEcomResp(
			List<Annexure1SummaryEcomResp1Dto> ecomSummaryRespList) {

		JsonElement ecomRespBody = anx1EcomStructure
				.anx1EcomResp(ecomSummaryRespList);

		return ecomRespBody;
	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public List<Annexure1SummaryEcomResp1Dto> handleSumEcomResp(
			List<Annexure1SummaryEcomResp1Dto> ecomSummaryRespList) {

		List<Annexure1SummaryEcomResp1Dto> ecomRespBody = anx1EcomStructure
				.anx1SumEcomResp(ecomSummaryRespList);

		return ecomRespBody;
	}
}
