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

@Service("anx1EXPTStructure")
public class Anx1EXPTStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1EXPTEYFinalStructure")
	private Anx1EXPTEYFinalStructure anx1EXPTEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;

	public JsonElement anx1ExptResp(
			List<Annexure1SummaryResp1Dto> exptEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultEXPTEYList = getDefaultEXPTTotalEYStructure();

		List<Annexure1SummaryResp1Dto> exptEYList = anx1EXPTEYFinalStructure
				.getEXPTEyList(defaultEXPTEYList, exptEySummary);
		
		/*Annexure1SummaryResp1Dto total = exptEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);
*/
	/*	Annexure1SummaryResp1Dto inv = exptEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = exptEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = exptEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnexpResult = gstnCalculation
				.addGstnExpData(gstnResult);
		
		Annexure1SummaryResp1Dto exptTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,gstnexpResult);
*/
		
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	//	retList.add(total);
		retList.addAll(exptEYList);

		JsonElement exptRespbody = gson.toJsonTree(retList);

		return exptRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumExptResp(
			List<Annexure1SummaryResp1Dto> exptEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultEXPTEYList = getDefaultEXPTEYStructure();

		List<Annexure1SummaryResp1Dto> exptEYList = anx1EXPTEYFinalStructure
				.getEXPTEyList(defaultEXPTEYList, exptEySummary);

		Annexure1SummaryResp1Dto inv = exptEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = exptEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = exptEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnexpResult = gstnCalculation
				.addGstnExpData(gstnResult);
		Annexure1SummaryResp1Dto exptTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,gstnexpResult);

		return exptTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultEXPTEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultExptEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3CInv = new Annexure1SummaryResp1Dto();
		b2bEy3CInv.setTableSection("3C");
		b2bEy3CInv.setDocType("INV");
		b2bEy3CInv.setIndex(1);
		b2bEy3CInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3CInv);

		Annexure1SummaryResp1Dto b2bEy3CCr = new Annexure1SummaryResp1Dto();
		b2bEy3CCr.setTableSection("3C");
		b2bEy3CCr.setDocType("CR");
		b2bEy3CCr.setIndex(3);
		b2bEy3CCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3CCr);

		Annexure1SummaryResp1Dto b2bEy3CDr = new Annexure1SummaryResp1Dto();
		b2bEy3CDr.setTableSection("3C");
		b2bEy3CDr.setDocType("DR");
		b2bEy3CDr.setIndex(2);
		b2bEy3CDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3CDr);

		defaultExptEY.add(b2bEy3CInv);
		defaultExptEY.add(b2bEy3CCr);
		defaultExptEY.add(b2bEy3CDr);

		return defaultExptEY;
	}
	private List<Annexure1SummaryResp1Dto> getDefaultEXPTTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultExptEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3CTotal = new Annexure1SummaryResp1Dto();
		b2bEy3CTotal.setTableSection("3C");
		b2bEy3CTotal.setDocType("total");
		b2bEy3CTotal.setIndex(0);
		b2bEy3CTotal = defaultStructureUtil.anx1DefaultStructure(b2bEy3CTotal);

		Annexure1SummaryResp1Dto b2bEy3CInv = new Annexure1SummaryResp1Dto();
		b2bEy3CInv.setTableSection("3C");
		b2bEy3CInv.setDocType("INV");
		b2bEy3CInv.setIndex(1);
		b2bEy3CInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3CInv);

		Annexure1SummaryResp1Dto b2bEy3CCr = new Annexure1SummaryResp1Dto();
		b2bEy3CCr.setTableSection("3C");
		b2bEy3CCr.setDocType("CR");
		b2bEy3CCr.setIndex(3);
		b2bEy3CCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3CCr);

		Annexure1SummaryResp1Dto b2bEy3CDr = new Annexure1SummaryResp1Dto();
		b2bEy3CDr.setTableSection("3C");
		b2bEy3CDr.setDocType("DR");
		b2bEy3CDr.setIndex(2);
		b2bEy3CDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3CDr);

		defaultExptEY.add(b2bEy3CTotal);
		defaultExptEY.add(b2bEy3CInv);
		defaultExptEY.add(b2bEy3CCr);
		defaultExptEY.add(b2bEy3CDr);

		return defaultExptEY;
	}

	
}
