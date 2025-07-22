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
import com.ey.advisory.app.services.search.docsummarysearch.Anx1EXPWTEYFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1GstnCalculation;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1SEZTEYFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.SummaryTotalCalculation;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("anx1SEZTStructure")
public class Anx1SEZTStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1SEZTEYFinalStructure")
	private Anx1SEZTEYFinalStructure anx1SEZTEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;

	public JsonElement anx1SeztResp(
			List<Annexure1SummaryResp1Dto> expwtEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultSEZTEYList = 
				getDefaultSEZTTotalEYStructure();

		List<Annexure1SummaryResp1Dto> seztEYList = anx1SEZTEYFinalStructure
				.getSeztEyList(defaultSEZTEYList, expwtEySummary);

		/*Annexure1SummaryResp1Dto total = expwtEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);
		*/
	/*	Annexure1SummaryResp1Dto inv = seztEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = seztEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = seztEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rdr = seztEYList.stream()
				.filter(x -> "RDR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rnv = seztEYList.stream()
				.filter(x -> "RNV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rcr = seztEYList.stream()
				.filter(x -> "RCR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto addGstnSeztData = gstnCalculation.addGstnSeztData(gstnResult);
		
		Annexure1SummaryResp1Dto seztTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnSeztData);
*/
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	//	retList.add(total);
		retList.addAll(seztEYList);

		JsonElement seztRespbody = gson.toJsonTree(retList);

		return seztRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumSeztResp(
			List<Annexure1SummaryResp1Dto> expwtEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultSEZTEYList = 
				getDefaultSEZTEYStructure();

		List<Annexure1SummaryResp1Dto> seztEYList = anx1SEZTEYFinalStructure
				.getSeztEyList(defaultSEZTEYList, expwtEySummary);

		Annexure1SummaryResp1Dto inv = seztEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = seztEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = seztEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rdr = seztEYList.stream()
				.filter(x -> "RDR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rnv = seztEYList.stream()
				.filter(x -> "RNV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rcr = seztEYList.stream()
				.filter(x -> "RCR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto addGstnSeztData = gstnCalculation
				.addGstnSeztData(gstnResult);
		Annexure1SummaryResp1Dto seztTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnSeztData);

		return seztTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultSEZTEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultSeztEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3EInv = new Annexure1SummaryResp1Dto();
		b2bEy3EInv.setTableSection("3E");
		b2bEy3EInv.setDocType("INV");
		b2bEy3EInv.setIndex(1);
		b2bEy3EInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3EInv);

		Annexure1SummaryResp1Dto b2bEy3ECr = new Annexure1SummaryResp1Dto();
		b2bEy3ECr.setTableSection("3E");
		b2bEy3ECr.setDocType("CR");
		b2bEy3ECr.setIndex(3);
		b2bEy3ECr = defaultStructureUtil.anx1DefaultStructure(b2bEy3ECr);

		Annexure1SummaryResp1Dto b2bEy3EDr = new Annexure1SummaryResp1Dto();
		b2bEy3EDr.setTableSection("3E");
		b2bEy3EDr.setDocType("DR");
		b2bEy3EDr.setIndex(2);
		b2bEy3EDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3EDr);

		Annexure1SummaryResp1Dto b2bEy3ERnv = new Annexure1SummaryResp1Dto();
		b2bEy3ERnv.setTableSection("3E");
		b2bEy3ERnv.setDocType("RNV");
		b2bEy3ERnv.setIndex(4);
		b2bEy3ERnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3ERnv);

		Annexure1SummaryResp1Dto b2bEy3ERdr = new Annexure1SummaryResp1Dto();
		b2bEy3ERdr.setTableSection("3E");
		b2bEy3ERdr.setDocType("RDR");
		b2bEy3ERdr.setIndex(5);
		b2bEy3ERdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3ERdr);

		Annexure1SummaryResp1Dto b2bEy3ERcr = new Annexure1SummaryResp1Dto();
		b2bEy3ERcr.setTableSection("3E");
		b2bEy3ERcr.setDocType("RCR");
		b2bEy3ERcr.setIndex(6);
		b2bEy3ERcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3ERcr);

		defaultSeztEY.add(b2bEy3EInv);
		defaultSeztEY.add(b2bEy3ECr);
		defaultSeztEY.add(b2bEy3EDr);
		defaultSeztEY.add(b2bEy3ERnv);
		defaultSeztEY.add(b2bEy3ERdr);
		defaultSeztEY.add(b2bEy3ERcr);

		return defaultSeztEY;
	}

	private List<Annexure1SummaryResp1Dto> getDefaultSEZTTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultSeztEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3ETotal = new Annexure1SummaryResp1Dto();
		b2bEy3ETotal.setTableSection("3E");
		b2bEy3ETotal.setDocType("total");
		b2bEy3ETotal.setIndex(0);
		b2bEy3ETotal = defaultStructureUtil.anx1DefaultStructure(b2bEy3ETotal);
		
		Annexure1SummaryResp1Dto b2bEy3EInv = new Annexure1SummaryResp1Dto();
		b2bEy3EInv.setTableSection("3E");
		b2bEy3EInv.setDocType("INV");
		b2bEy3EInv.setIndex(1);
		b2bEy3EInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3EInv);

		Annexure1SummaryResp1Dto b2bEy3ECr = new Annexure1SummaryResp1Dto();
		b2bEy3ECr.setTableSection("3E");
		b2bEy3ECr.setDocType("CR");
		b2bEy3ECr.setIndex(3);
		b2bEy3ECr = defaultStructureUtil.anx1DefaultStructure(b2bEy3ECr);

		Annexure1SummaryResp1Dto b2bEy3EDr = new Annexure1SummaryResp1Dto();
		b2bEy3EDr.setTableSection("3E");
		b2bEy3EDr.setDocType("DR");
		b2bEy3EDr.setIndex(2);
		b2bEy3EDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3EDr);

		Annexure1SummaryResp1Dto b2bEy3ERnv = new Annexure1SummaryResp1Dto();
		b2bEy3ERnv.setTableSection("3E");
		b2bEy3ERnv.setDocType("RNV");
		b2bEy3ERnv.setIndex(4);
		b2bEy3ERnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3ERnv);

		Annexure1SummaryResp1Dto b2bEy3ERdr = new Annexure1SummaryResp1Dto();
		b2bEy3ERdr.setTableSection("3E");
		b2bEy3ERdr.setDocType("RDR");
		b2bEy3ERdr.setIndex(5);
		b2bEy3ERdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3ERdr);

		Annexure1SummaryResp1Dto b2bEy3ERcr = new Annexure1SummaryResp1Dto();
		b2bEy3ERcr.setTableSection("3E");
		b2bEy3ERcr.setDocType("RCR");
		b2bEy3ERcr.setIndex(6);
		b2bEy3ERcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3ERcr);

		defaultSeztEY.add(b2bEy3ETotal);
		defaultSeztEY.add(b2bEy3EInv);
		defaultSeztEY.add(b2bEy3ECr);
		defaultSeztEY.add(b2bEy3EDr);
		defaultSeztEY.add(b2bEy3ERnv);
		defaultSeztEY.add(b2bEy3ERdr);
		defaultSeztEY.add(b2bEy3ERcr);

		return defaultSeztEY;
	}

	
}
