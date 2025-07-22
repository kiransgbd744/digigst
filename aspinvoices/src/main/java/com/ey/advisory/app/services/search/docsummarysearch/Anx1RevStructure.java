package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("anx1RevStructure")
public class Anx1RevStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1RevEYFinalStructure")
	private Anx1RevEYFinalStructure anx1RevEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;


	public JsonElement anx1RevResp(
			List<Annexure1SummaryResp1Dto> revEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultRev3hEYList = 
				getDefaultRevhTotalEYStructure();

		// Adding Default values if there is no data based on section
		List<Annexure1SummaryResp1Dto> revChEYList = anx1RevEYFinalStructure
				.getRevhEyList(defaultRev3hEYList, revEySummary);

		/*Annexure1SummaryResp1Dto total = revEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);
*/
		
/*		Annexure1SummaryResp1Dto slf = revChEYList.stream()
				.filter(x -> "SLF".equals(x.getDocType())).findAny()
				.orElse(null);
		Annexure1SummaryResp1Dto cr = revChEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = revChEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		// Total Count for Each Section
		
		Annexure1SummarySectionDto gstnRevResult = gstnCalculation
				.addGstnRevData(gstnResult);
		Annexure1SummaryResp1Dto revTotal = summaryTotalCalculation
				.totalThreeDocCalculation(slf, dr, cr,gstnRevResult);
*/
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
		//retList.add(total);
		retList.addAll(revChEYList);

		JsonElement revRespbody = gson.toJsonTree(retList);

		return revRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumRevResp(
			List<Annexure1SummaryResp1Dto> revEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		List<Annexure1SummaryResp1Dto> defaultRev3hEYList = 
				getDefaultRevhEYStructure();

		// Adding Default values if there is no data based on section
		List<Annexure1SummaryResp1Dto> revChEYList = anx1RevEYFinalStructure
				.getRevhEyList(defaultRev3hEYList, revEySummary);

		Annexure1SummaryResp1Dto slf = revChEYList.stream()
				.filter(x -> "SLF".equals(x.getDocType())).findAny()
				.orElse(null);
		Annexure1SummaryResp1Dto cr = revChEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = revChEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		// Total Count for Each Section
		Annexure1SummarySectionDto gstnRevResult = gstnCalculation
				.addGstnRevData(gstnResult);
		Annexure1SummaryResp1Dto revTotal = summaryTotalCalculation
				.totalThreeDocCalculation(slf, dr, cr,gstnRevResult);

		return revTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultRevhEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultRevhEY = new ArrayList<>();

		Annexure1SummaryResp1Dto revEy3HSLF = new Annexure1SummaryResp1Dto();
		revEy3HSLF.setTableSection("3H");
		revEy3HSLF.setDocType("SLF");
		revEy3HSLF.setIndex(1);
		revEy3HSLF = defaultStructureUtil.anx1DefaultStructure(revEy3HSLF);

		Annexure1SummaryResp1Dto revEy3HCr = new Annexure1SummaryResp1Dto();
		revEy3HCr.setTableSection("3H");
		revEy3HCr.setDocType("CR");
		revEy3HCr.setIndex(3);
		revEy3HCr = defaultStructureUtil.anx1DefaultStructure(revEy3HCr);

		Annexure1SummaryResp1Dto revEy3HDr = new Annexure1SummaryResp1Dto();
		revEy3HDr.setTableSection("3H");
		revEy3HDr.setDocType("DR");
		revEy3HDr.setIndex(2);
		revEy3HDr = defaultStructureUtil.anx1DefaultStructure(revEy3HDr);

		defaultRevhEY.add(revEy3HDr);
		defaultRevhEY.add(revEy3HCr);
		defaultRevhEY.add(revEy3HSLF);

		return defaultRevhEY;
	}

	private List<Annexure1SummaryResp1Dto> getDefaultRevhTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultRevhEY = new ArrayList<>();

		Annexure1SummaryResp1Dto revEy3HTotal = new Annexure1SummaryResp1Dto();
		revEy3HTotal.setTableSection("3H");
		revEy3HTotal.setDocType("total");
		revEy3HTotal.setIndex(0);
		revEy3HTotal = defaultStructureUtil.anx1DefaultStructure(revEy3HTotal);
		
		Annexure1SummaryResp1Dto revEy3HSLF = new Annexure1SummaryResp1Dto();
		revEy3HSLF.setTableSection("3H");
		revEy3HSLF.setDocType("SLF");
		revEy3HSLF.setIndex(1);
		revEy3HSLF = defaultStructureUtil.anx1DefaultStructure(revEy3HSLF);

		Annexure1SummaryResp1Dto revEy3HCr = new Annexure1SummaryResp1Dto();
		revEy3HCr.setTableSection("3H");
		revEy3HCr.setDocType("CR");
		revEy3HCr.setIndex(3);
		revEy3HCr = defaultStructureUtil.anx1DefaultStructure(revEy3HCr);

		Annexure1SummaryResp1Dto revEy3HDr = new Annexure1SummaryResp1Dto();
		revEy3HDr.setTableSection("3H");
		revEy3HDr.setDocType("DR");
		revEy3HDr.setIndex(2);
		revEy3HDr = defaultStructureUtil.anx1DefaultStructure(revEy3HDr);

		defaultRevhEY.add(revEy3HDr);
		defaultRevhEY.add(revEy3HCr);
		defaultRevhEY.add(revEy3HSLF);

		return defaultRevhEY;
	}
}
