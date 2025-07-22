package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.google.gson.JsonElement;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("Annexure1SummaryStructure")
public class Annexure1SummaryStructure {

	@Autowired
	@Qualifier("Annexure1B2BStructure")
	private Annexure1B2BStructure b2bStructure;
	
	@Autowired
	@Qualifier("Annexure1B2CStructure")
	private Annexure1B2CStructure b2cStructure;
	
	@Autowired
	@Qualifier("Annexure1ExptStructure")
	private Annexure1ExptStructure exptStructure;
	
	@Autowired
	@Qualifier("Annexure1ExpwtStructure")
	private Annexure1ExpwtStructure expwtStructure;
	
	@Autowired
	@Qualifier("Annexure1SeztStructure")
	private Annexure1SeztStructure seztStructure;
	
	@Autowired
	@Qualifier("Annexure1SezwtStructure")
	private Annexure1SezwtStructure sezwtStructure;
	
	@Autowired
	@Qualifier("Annexure1DeemExptStructure")
	private Annexure1DeemExptStructure deemExptStructure;
	

	public JsonElement b2bStructure(List<Annexure1SummarySectionDto> eySummary,
			Object obj) {
		return b2bStructure.b2bResp(eySummary, null);
	}
	
	public JsonElement b2cStructure(List<Annexure1SummarySectionDto> eySummary,
			Object obj) {
		return b2cStructure.b2cResp(eySummary, null);
	}
	
	public JsonElement exptStructure(List<Annexure1SummarySectionDto> eySummary,
			Object obj) {
		return exptStructure.exptResp(eySummary, null);
	}
	
	public JsonElement expwtStructure(
			List<Annexure1SummarySectionDto> eySummary, Object obj) {
		return expwtStructure.expwtResp(eySummary, null);
	}
	
	public JsonElement seztStructure(
			List<Annexure1SummarySectionDto> eySummary, Object obj) {
		return seztStructure.seztResp(eySummary, null);
	}
	
	public JsonElement sezwtStructure(
			List<Annexure1SummarySectionDto> eySummary, Object obj) {
		return sezwtStructure.sezwtResp(eySummary, null);
	}
	
	public JsonElement deemExptStructure(
			List<Annexure1SummarySectionDto> eySummary, Object obj) {
		return deemExptStructure.deemExptResp(eySummary, null);
	}


}
