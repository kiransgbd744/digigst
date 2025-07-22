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
@Service("anx1ImpgStructure")
public class Anx1ImpgStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1ImpgEYFinalStructure")
	private Anx1ImpgEYFinalStructure anx1ImpgEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;

	public JsonElement anx1ImpgResp(
			List<Annexure1SummaryResp1Dto> impgEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultImpgEYList = 
				getDefaultImpgTotalEYStructure();

		List<Annexure1SummaryResp1Dto> impgEYList = anx1ImpgEYFinalStructure
				.getImpgEyList(defaultImpgEYList, impgEySummary);

		
	/*	Annexure1SummaryResp1Dto total = impgEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);

*/
		
		/*Annexure1SummaryResp1Dto inv = impgEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = impgEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = impgEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnImpgResult = gstnCalculation
				.addGstnImpgData(gstnResult);
		Annexure1SummaryResp1Dto impgTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,gstnImpgResult);
*/
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	//	retList.add(total);
		retList.addAll(impgEYList);

		JsonElement impgRespbody = gson.toJsonTree(retList);

		return impgRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumImpgResp(
			List<Annexure1SummaryResp1Dto> impgEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultImpgEYList = 
				getDefaultImpgEYStructure();

		List<Annexure1SummaryResp1Dto> impgEYList = anx1ImpgEYFinalStructure
				.getImpgEyList(defaultImpgEYList, impgEySummary);

		Annexure1SummaryResp1Dto inv = impgEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = impgEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = impgEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnImpgResult = gstnCalculation
				.addGstnImpgData(gstnResult);
		
		Annexure1SummaryResp1Dto impgTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,gstnImpgResult);

		return impgTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultImpgEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultImpgEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2cEy3JInv = new Annexure1SummaryResp1Dto();
		b2cEy3JInv.setTableSection("3J");
		b2cEy3JInv.setDocType("INV");
		b2cEy3JInv.setIndex(1);
		b2cEy3JInv = defaultStructureUtil.anx1DefaultStructure(b2cEy3JInv);

		Annexure1SummaryResp1Dto b2cEy3JCr = new Annexure1SummaryResp1Dto();
		b2cEy3JCr.setTableSection("3J");
		b2cEy3JCr.setDocType("CR");
		b2cEy3JCr.setIndex(3);
		b2cEy3JCr = defaultStructureUtil.anx1DefaultStructure(b2cEy3JCr);

		Annexure1SummaryResp1Dto b2cEy3JDr = new Annexure1SummaryResp1Dto();
		b2cEy3JDr.setTableSection("3J");
		b2cEy3JDr.setDocType("DR");
		b2cEy3JDr.setIndex(2);
		b2cEy3JDr = defaultStructureUtil.anx1DefaultStructure(b2cEy3JDr);

		defaultImpgEY.add(b2cEy3JInv);
		defaultImpgEY.add(b2cEy3JCr);
		defaultImpgEY.add(b2cEy3JDr);

		return defaultImpgEY;
	}

	private List<Annexure1SummaryResp1Dto> getDefaultImpgTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultImpgEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2cEy3JTotal = new Annexure1SummaryResp1Dto();
		b2cEy3JTotal.setTableSection("3J");
		b2cEy3JTotal.setDocType("total");
		b2cEy3JTotal.setIndex(0);
		b2cEy3JTotal = defaultStructureUtil.anx1DefaultStructure(b2cEy3JTotal);

		
		Annexure1SummaryResp1Dto b2cEy3JInv = new Annexure1SummaryResp1Dto();
		b2cEy3JInv.setTableSection("3J");
		b2cEy3JInv.setDocType("INV");
		b2cEy3JInv.setIndex(1);
		b2cEy3JInv = defaultStructureUtil.anx1DefaultStructure(b2cEy3JInv);

		Annexure1SummaryResp1Dto b2cEy3JCr = new Annexure1SummaryResp1Dto();
		b2cEy3JCr.setTableSection("3J");
		b2cEy3JCr.setDocType("CR");
		b2cEy3JCr.setIndex(3);
		b2cEy3JCr = defaultStructureUtil.anx1DefaultStructure(b2cEy3JCr);

		Annexure1SummaryResp1Dto b2cEy3JDr = new Annexure1SummaryResp1Dto();
		b2cEy3JDr.setTableSection("3J");
		b2cEy3JDr.setDocType("DR");
		b2cEy3JDr.setIndex(2);
		b2cEy3JDr = defaultStructureUtil.anx1DefaultStructure(b2cEy3JDr);

		defaultImpgEY.add(b2cEy3JInv);
		defaultImpgEY.add(b2cEy3JCr);
		defaultImpgEY.add(b2cEy3JDr);

		return defaultImpgEY;
	}

	
}
