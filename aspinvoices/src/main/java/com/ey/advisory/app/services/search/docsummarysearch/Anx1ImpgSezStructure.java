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

@Service("anx1ImpgSezStructure")
public class Anx1ImpgSezStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1ImpgSezEYFinalStructure")
	private Anx1ImpgSezEYFinalStructure anx1ImpgSezEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;


	public JsonElement anx1ImpgSezResp(
			List<Annexure1SummaryResp1Dto> impgSezEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultImpgSezEYList = 
				getDefaultImpgTotalSezEYStructure();

		List<Annexure1SummaryResp1Dto> impgSezEYList = anx1ImpgSezEYFinalStructure
				.getImpgSezEyList(defaultImpgSezEYList, impgSezEySummary);

		/*Annexure1SummaryResp1Dto total = impgSezEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);
*/
		
	/*	Annexure1SummaryResp1Dto inv = impgSezEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = impgSezEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = impgSezEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnImpgSezResult = gstnCalculation
				.addGstnImpgData(gstnResult);
		
		Annexure1SummaryResp1Dto impgSezTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,gstnImpgSezResult);
*/
		
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	//	retList.add(total);
		retList.addAll(impgSezEYList);

		JsonElement impgSezRespbody = gson.toJsonTree(retList);

		return impgSezRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumImpgSezResp(
			List<Annexure1SummaryResp1Dto> impgSezEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		List<Annexure1SummaryResp1Dto> defaultImpgSezEYList = 
				getDefaultImpgSezEYStructure();

		List<Annexure1SummaryResp1Dto> impgSezEYList = anx1ImpgSezEYFinalStructure
				.getImpgSezEyList(defaultImpgSezEYList, impgSezEySummary);

		Annexure1SummaryResp1Dto inv = impgSezEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = impgSezEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = impgSezEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnImpgSezResult = gstnCalculation
				.addGstnImpgSezData(gstnResult);
		
		Annexure1SummaryResp1Dto impgSezTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,gstnImpgSezResult);

		return impgSezTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultImpgSezEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultImpgSezEY = new ArrayList<>();

		Annexure1SummaryResp1Dto impgszEy3KInv = new Annexure1SummaryResp1Dto();
		impgszEy3KInv.setTableSection("3K");
		impgszEy3KInv.setDocType("INV");
		impgszEy3KInv.setIndex(1);
		impgszEy3KInv = defaultStructureUtil
				.anx1DefaultStructure(impgszEy3KInv);

		Annexure1SummaryResp1Dto impgszEy3KCr = new Annexure1SummaryResp1Dto();
		impgszEy3KCr.setTableSection("3K");
		impgszEy3KCr.setDocType("CR");
		impgszEy3KCr.setIndex(3);
		impgszEy3KCr = defaultStructureUtil.anx1DefaultStructure(impgszEy3KCr);

		Annexure1SummaryResp1Dto impgszEy3KDr = new Annexure1SummaryResp1Dto();
		impgszEy3KDr.setTableSection("3K");
		impgszEy3KDr.setDocType("DR");
		impgszEy3KDr.setIndex(2);
		impgszEy3KDr = defaultStructureUtil.anx1DefaultStructure(impgszEy3KDr);

		defaultImpgSezEY.add(impgszEy3KInv);
		defaultImpgSezEY.add(impgszEy3KCr);
		defaultImpgSezEY.add(impgszEy3KDr);

		return defaultImpgSezEY;
	}

	private List<Annexure1SummaryResp1Dto> getDefaultImpgTotalSezEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultImpgSezEY = new ArrayList<>();

		Annexure1SummaryResp1Dto impgszEy3KTotal = new Annexure1SummaryResp1Dto();
		impgszEy3KTotal.setTableSection("3K");
		impgszEy3KTotal.setDocType("total");
		impgszEy3KTotal.setIndex(0);
		impgszEy3KTotal = defaultStructureUtil
				.anx1DefaultStructure(impgszEy3KTotal);
		
		Annexure1SummaryResp1Dto impgszEy3KInv = new Annexure1SummaryResp1Dto();
		impgszEy3KInv.setTableSection("3K");
		impgszEy3KInv.setDocType("INV");
		impgszEy3KInv.setIndex(1);
		impgszEy3KInv = defaultStructureUtil
				.anx1DefaultStructure(impgszEy3KInv);

		Annexure1SummaryResp1Dto impgszEy3KCr = new Annexure1SummaryResp1Dto();
		impgszEy3KCr.setTableSection("3K");
		impgszEy3KCr.setDocType("CR");
		impgszEy3KCr.setIndex(3);
		impgszEy3KCr = defaultStructureUtil.anx1DefaultStructure(impgszEy3KCr);

		Annexure1SummaryResp1Dto impgszEy3KDr = new Annexure1SummaryResp1Dto();
		impgszEy3KDr.setTableSection("3K");
		impgszEy3KDr.setDocType("DR");
		impgszEy3KDr.setIndex(2);
		impgszEy3KDr = defaultStructureUtil.anx1DefaultStructure(impgszEy3KDr);

		defaultImpgSezEY.add(impgszEy3KTotal);
		defaultImpgSezEY.add(impgszEy3KInv);
		defaultImpgSezEY.add(impgszEy3KCr);
		defaultImpgSezEY.add(impgszEy3KDr);

		return defaultImpgSezEY;
	}
}
