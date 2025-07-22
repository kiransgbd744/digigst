package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1DefaultSummaryStructureUtil;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1EXPTEYFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1EXPWTEYFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1GstnCalculation;
import com.ey.advisory.app.services.search.docsummarysearch.SummaryTotalCalculation;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("anx1EXPWTStructure")
public class Anx1EXPWTStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1EXPWTEYFinalStructure")
	private Anx1EXPWTEYFinalStructure anx1EXPWTEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;

	public JsonElement anx1ExpwtResp(
			List<Annexure1SummaryResp1Dto> expwtEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultEXPWTEYList = 
				getDefaultEXPWTTotalEYStructure();

		List<Annexure1SummaryResp1Dto> expwtEYList = anx1EXPWTEYFinalStructure
				.getEXPWTEyList(defaultEXPWTEYList, expwtEySummary);
		
	/*	Annexure1SummaryResp1Dto total = expwtEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);
*/
	/*	Annexure1SummaryResp1Dto inv = expwtEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = expwtEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = expwtEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto expwtgstnResult = gstnCalculation
				.addGstnExpwtData(gstnResult);
		Annexure1SummaryResp1Dto expwtTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,expwtgstnResult);
*/
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	//	retList.add(total);
		retList.addAll(expwtEYList);

		JsonElement exptRespbody = gson.toJsonTree(retList);

		return exptRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumExpwtResp(
			List<Annexure1SummaryResp1Dto> expwtEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultEXPWTEYList = 
				getDefaultEXPWTEYStructure();

		List<Annexure1SummaryResp1Dto> expwtEYList = anx1EXPWTEYFinalStructure
				.getEXPWTEyList(defaultEXPWTEYList, expwtEySummary);

		Annexure1SummaryResp1Dto inv = expwtEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = expwtEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = expwtEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnExpwtResult = gstnCalculation
				.addGstnExpwtData(gstnResult);;
		Annexure1SummaryResp1Dto expwtTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,gstnExpwtResult);

		return expwtTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultEXPWTEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultExpwtEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3DInv = new Annexure1SummaryResp1Dto();
		b2bEy3DInv.setTableSection("3D");
		b2bEy3DInv.setDocType("INV");
		b2bEy3DInv.setIndex(1);
		b2bEy3DInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3DInv);

		Annexure1SummaryResp1Dto b2bEy3DCr = new Annexure1SummaryResp1Dto();
		b2bEy3DCr.setTableSection("3D");
		b2bEy3DCr.setDocType("CR");
		b2bEy3DCr.setIndex(3);
		b2bEy3DCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3DCr);

		Annexure1SummaryResp1Dto b2bEy3DDr = new Annexure1SummaryResp1Dto();
		b2bEy3DDr.setTableSection("3D");
		b2bEy3DDr.setDocType("DR");
		b2bEy3DDr.setIndex(2);
		b2bEy3DDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3DDr);

		defaultExpwtEY.add(b2bEy3DInv);
		defaultExpwtEY.add(b2bEy3DCr);
		defaultExpwtEY.add(b2bEy3DDr);

		return defaultExpwtEY;
	}
	private List<Annexure1SummaryResp1Dto> getDefaultEXPWTTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultExpwtEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3DTotal = new Annexure1SummaryResp1Dto();
		b2bEy3DTotal.setTableSection("3D");
		b2bEy3DTotal.setDocType("total");
		b2bEy3DTotal.setIndex(0);
		b2bEy3DTotal = defaultStructureUtil.anx1DefaultStructure(b2bEy3DTotal);

		
		Annexure1SummaryResp1Dto b2bEy3DInv = new Annexure1SummaryResp1Dto();
		b2bEy3DInv.setTableSection("3D");
		b2bEy3DInv.setDocType("INV");
		b2bEy3DInv.setIndex(1);
		b2bEy3DInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3DInv);

		Annexure1SummaryResp1Dto b2bEy3DCr = new Annexure1SummaryResp1Dto();
		b2bEy3DCr.setTableSection("3D");
		b2bEy3DCr.setDocType("CR");
		b2bEy3DCr.setIndex(3);
		b2bEy3DCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3DCr);

		Annexure1SummaryResp1Dto b2bEy3DDr = new Annexure1SummaryResp1Dto();
		b2bEy3DDr.setTableSection("3D");
		b2bEy3DDr.setDocType("DR");
		b2bEy3DDr.setIndex(2);
		b2bEy3DDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3DDr);

		defaultExpwtEY.add(b2bEy3DTotal);
		defaultExpwtEY.add(b2bEy3DInv);
		defaultExpwtEY.add(b2bEy3DCr);
		defaultExpwtEY.add(b2bEy3DDr);

		return defaultExpwtEY;
	}

}
