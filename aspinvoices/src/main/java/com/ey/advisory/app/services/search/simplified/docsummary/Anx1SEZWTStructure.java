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
import com.ey.advisory.app.services.search.docsummarysearch.Anx1GstnCalculation;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1SEZTEYFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1SEZWTEYFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.SummaryTotalCalculation;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("anx1SEZWTStructure")
public class Anx1SEZWTStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1SEZWTEYFinalStructure")
	private Anx1SEZWTEYFinalStructure anx1SEZWTEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;

	public JsonElement anx1SezwtResp(
			List<Annexure1SummaryResp1Dto> sezwtEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultSEZWTEYList = 
				getDefaultSEZWTTotalEYStructure();

		List<Annexure1SummaryResp1Dto> sezwtEYList = anx1SEZWTEYFinalStructure
				.getSezwtEyList(defaultSEZWTEYList, sezwtEySummary);

		/*Annexure1SummaryResp1Dto total = sezwtEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);*/
		
	/*	Annexure1SummaryResp1Dto inv = sezwtEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = sezwtEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = sezwtEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rdr = sezwtEYList.stream()
				.filter(x -> "RDR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rnv = sezwtEYList.stream()
				.filter(x -> "RNV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rcr = sezwtEYList.stream()
				.filter(x -> "RCR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto addGstnSezwtData = gstnCalculation
				.addGstnSezwtData(gstnResult);
		Annexure1SummaryResp1Dto sezwtTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnSezwtData);
*/
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
		//retList.add(total);
		retList.addAll(sezwtEYList);

		JsonElement sezwtRespbody = gson.toJsonTree(retList);

		return sezwtRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumSezwtResp(
			List<Annexure1SummaryResp1Dto> sezwtEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultSEZWTEYList = 
				getDefaultSEZWTEYStructure();

		List<Annexure1SummaryResp1Dto> sezwtEYList = anx1SEZWTEYFinalStructure
				.getSezwtEyList(defaultSEZWTEYList, sezwtEySummary);

		Annexure1SummaryResp1Dto inv = sezwtEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = sezwtEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = sezwtEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rdr = sezwtEYList.stream()
				.filter(x -> "RDR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rnv = sezwtEYList.stream()
				.filter(x -> "RNV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto rcr = sezwtEYList.stream()
				.filter(x -> "RCR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto addGstnSezwtData = gstnCalculation
				.addGstnSezwtData(gstnResult);
		
		Annexure1SummaryResp1Dto sezwtTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnSezwtData);

		return sezwtTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultSEZWTEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultSezwtEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3FInv = new Annexure1SummaryResp1Dto();
		b2bEy3FInv.setTableSection("3F");
		b2bEy3FInv.setDocType("INV");
		b2bEy3FInv.setIndex(1);
		b2bEy3FInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3FInv);

		Annexure1SummaryResp1Dto b2bEy3FCr = new Annexure1SummaryResp1Dto();
		b2bEy3FCr.setTableSection("3F");
		b2bEy3FCr.setDocType("CR");
		b2bEy3FCr.setIndex(3);
		b2bEy3FCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FCr);

		Annexure1SummaryResp1Dto b2bEy3FDr = new Annexure1SummaryResp1Dto();
		b2bEy3FDr.setTableSection("3F");
		b2bEy3FDr.setDocType("DR");
		b2bEy3FDr.setIndex(2);
		b2bEy3FDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FDr);

		Annexure1SummaryResp1Dto b2bEy3FRnv = new Annexure1SummaryResp1Dto();
		b2bEy3FRnv.setTableSection("3F");
		b2bEy3FRnv.setDocType("RNV");
		b2bEy3FRnv.setIndex(4);
		b2bEy3FRnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3FRnv);

		Annexure1SummaryResp1Dto b2bEy3FRdr = new Annexure1SummaryResp1Dto();
		b2bEy3FRdr.setTableSection("3F");
		b2bEy3FRdr.setDocType("RDR");
		b2bEy3FRdr.setIndex(5);
		b2bEy3FRdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FRdr);

		Annexure1SummaryResp1Dto b2bEy3FRcr = new Annexure1SummaryResp1Dto();
		b2bEy3FRcr.setTableSection("3F");
		b2bEy3FRcr.setDocType("RCR");
		b2bEy3FRcr.setIndex(6);
		b2bEy3FRcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FRcr);

		defaultSezwtEY.add(b2bEy3FInv);
		defaultSezwtEY.add(b2bEy3FCr);
		defaultSezwtEY.add(b2bEy3FDr);
		defaultSezwtEY.add(b2bEy3FRnv);
		defaultSezwtEY.add(b2bEy3FRdr);
		defaultSezwtEY.add(b2bEy3FRcr);

		return defaultSezwtEY;
	}
	
	private List<Annexure1SummaryResp1Dto> getDefaultSEZWTTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultSezwtEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3FTotal = new Annexure1SummaryResp1Dto();
		b2bEy3FTotal.setTableSection("3F");
		b2bEy3FTotal.setDocType("total");
		b2bEy3FTotal.setIndex(0);
		b2bEy3FTotal = defaultStructureUtil.anx1DefaultStructure(b2bEy3FTotal);

		
		Annexure1SummaryResp1Dto b2bEy3FInv = new Annexure1SummaryResp1Dto();
		b2bEy3FInv.setTableSection("3F");
		b2bEy3FInv.setDocType("INV");
		b2bEy3FInv.setIndex(1);
		b2bEy3FInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3FInv);

		Annexure1SummaryResp1Dto b2bEy3FCr = new Annexure1SummaryResp1Dto();
		b2bEy3FCr.setTableSection("3F");
		b2bEy3FCr.setDocType("CR");
		b2bEy3FCr.setIndex(3);
		b2bEy3FCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FCr);

		Annexure1SummaryResp1Dto b2bEy3FDr = new Annexure1SummaryResp1Dto();
		b2bEy3FDr.setTableSection("3F");
		b2bEy3FDr.setDocType("DR");
		b2bEy3FDr.setIndex(2);
		b2bEy3FDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FDr);

		Annexure1SummaryResp1Dto b2bEy3FRnv = new Annexure1SummaryResp1Dto();
		b2bEy3FRnv.setTableSection("3F");
		b2bEy3FRnv.setDocType("RNV");
		b2bEy3FRnv.setIndex(4);
		b2bEy3FRnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3FRnv);

		Annexure1SummaryResp1Dto b2bEy3FRdr = new Annexure1SummaryResp1Dto();
		b2bEy3FRdr.setTableSection("3F");
		b2bEy3FRdr.setDocType("RDR");
		b2bEy3FRdr.setIndex(5);
		b2bEy3FRdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FRdr);

		Annexure1SummaryResp1Dto b2bEy3FRcr = new Annexure1SummaryResp1Dto();
		b2bEy3FRcr.setTableSection("3F");
		b2bEy3FRcr.setDocType("RCR");
		b2bEy3FRcr.setIndex(6);
		b2bEy3FRcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3FRcr);

		defaultSezwtEY.add(b2bEy3FTotal);
		defaultSezwtEY.add(b2bEy3FInv);
		defaultSezwtEY.add(b2bEy3FCr);
		defaultSezwtEY.add(b2bEy3FDr);
		defaultSezwtEY.add(b2bEy3FRnv);
		defaultSezwtEY.add(b2bEy3FRdr);
		defaultSezwtEY.add(b2bEy3FRcr);

		return defaultSezwtEY;
	}

}
