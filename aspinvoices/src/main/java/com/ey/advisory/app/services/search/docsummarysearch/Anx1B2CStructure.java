package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

@Service("anx1B2CStructure")
public class Anx1B2CStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1B2CEYFinalStructure")
	private Anx1B2CEYFinalStructure anx1B2CEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;
	

	public JsonElement anx1B2CResp(
			List<Annexure1SummaryResp1Dto> b2cEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultB2cEYList = 
				getDefaultB2CDetailEYStructure();


		
		List<Annexure1SummaryResp1Dto> b2cEYList = anx1B2CEYFinalStructure
				.getB2CEyList(defaultB2cEYList, b2cEySummary);

		/*List<Annexure1SummaryResp1Dto> total = b2cEySummary
				.stream().filter(p -> "total".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		*/
		/*Annexure1SummaryResp1Dto total = b2cEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);*/
		
		
	/*	Annexure1SummaryResp1Dto inv = b2cEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = b2cEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = b2cEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);*/

		/*Annexure1SummarySectionDto addGstnB2cData = gstnCalculation
				.addGstnB2cData(gstnResult);
*/		
		/*Annexure1SummaryResp1Dto b2cTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,addGstnB2cData);*/
		
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
		//retList.add(total);
		retList.addAll(b2cEYList);
		// String json = gson.toJson(b2bTotal);

		JsonElement b2cRespbody = gson.toJsonTree(retList);

		return b2cRespbody;

	}

	// This method is used For Calculating Total Summary
	public Annexure1SummaryResp1Dto anx1SumB2CResp(
			List<Annexure1SummaryResp1Dto> b2cEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultB2cEYList = 
				getDefaultB2CEYStructure();

		List<Annexure1SummaryResp1Dto> b2cEYList = anx1B2CEYFinalStructure
				.getB2CEyList(defaultB2cEYList, b2cEySummary);

		Annexure1SummaryResp1Dto inv = b2cEYList.stream()
				.filter(x -> "INV".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto cr = b2cEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = b2cEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto addGstnB2cData = gstnCalculation
				.addGstnB2cData(gstnResult);
		
		Annexure1SummaryResp1Dto b2cTotal = summaryTotalCalculation
				.totalThreeDocCalculation(inv, dr, cr,addGstnB2cData);

		return b2cTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultB2CEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultB2CEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2cEy3AInv = new Annexure1SummaryResp1Dto();
		b2cEy3AInv.setTableSection("3A");
		b2cEy3AInv.setDocType("INV");
		b2cEy3AInv.setIndex(1);
		b2cEy3AInv = defaultStructureUtil.anx1DefaultStructure(b2cEy3AInv);

		Annexure1SummaryResp1Dto b2cEy3ACr = new Annexure1SummaryResp1Dto();
		b2cEy3ACr.setTableSection("3A");
		b2cEy3ACr.setDocType("CR");
		b2cEy3ACr.setIndex(3);
		b2cEy3ACr = defaultStructureUtil.anx1DefaultStructure(b2cEy3ACr);

		Annexure1SummaryResp1Dto b2cEy3ADr = new Annexure1SummaryResp1Dto();
		b2cEy3ADr.setTableSection("3A");
		b2cEy3ADr.setDocType("DR");
		b2cEy3ADr.setIndex(2);
		b2cEy3ADr = defaultStructureUtil.anx1DefaultStructure(b2cEy3ADr);

		defaultB2CEY.add(b2cEy3AInv);
		defaultB2CEY.add(b2cEy3ACr);
		defaultB2CEY.add(b2cEy3ADr);

		return defaultB2CEY;
	}

	private List<Annexure1SummaryResp1Dto> getDefaultB2CDetailEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultB2CEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2cEy3ATotal = new Annexure1SummaryResp1Dto();
		b2cEy3ATotal.setTableSection("3A");
		b2cEy3ATotal.setDocType("total");
		b2cEy3ATotal.setIndex(0);
		
		Annexure1SummaryResp1Dto b2cEy3AInv = new Annexure1SummaryResp1Dto();
		b2cEy3AInv.setTableSection("3A");
		b2cEy3AInv.setDocType("INV");
		b2cEy3AInv.setIndex(1);
		b2cEy3AInv = defaultStructureUtil.anx1DefaultStructure(b2cEy3AInv);

		Annexure1SummaryResp1Dto b2cEy3ACr = new Annexure1SummaryResp1Dto();
		b2cEy3ACr.setTableSection("3A");
		b2cEy3ACr.setDocType("CR");
		b2cEy3ACr.setIndex(3);
		b2cEy3ACr = defaultStructureUtil.anx1DefaultStructure(b2cEy3ACr);

		Annexure1SummaryResp1Dto b2cEy3ADr = new Annexure1SummaryResp1Dto();
		b2cEy3ADr.setTableSection("3A");
		b2cEy3ADr.setDocType("DR");
		b2cEy3ADr.setIndex(2);
		b2cEy3ADr = defaultStructureUtil.anx1DefaultStructure(b2cEy3ADr);

		defaultB2CEY.add(b2cEy3ATotal);
		defaultB2CEY.add(b2cEy3AInv);
		defaultB2CEY.add(b2cEy3ACr);
		defaultB2CEY.add(b2cEy3ADr);

		return defaultB2CEY;
	}

	
}
