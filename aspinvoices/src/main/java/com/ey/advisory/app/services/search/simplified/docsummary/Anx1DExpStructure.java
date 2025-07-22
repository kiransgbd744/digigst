package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1DExpEYFinalStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1DefaultSummaryStructureUtil;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1GstnCalculation;
import com.ey.advisory.app.services.search.docsummarysearch.SummaryTotalCalculation;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("anx1DExpStructure")
public class Anx1DExpStructure {
	

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1DExpEYFinalStructure")
	private Anx1DExpEYFinalStructure anx1DExpEYFinalStructure;
	
	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;
		

	public JsonElement anx1DexpResp(
			List<Annexure1SummaryResp1Dto> dexpEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultSEZWTEYList = 
				getDefaultDExpTotalEYStructure();

		List<Annexure1SummaryResp1Dto> dexpEYList = anx1DExpEYFinalStructure
				.getDExpEyList(defaultSEZWTEYList, dexpEySummary);

		
	/*	Annexure1SummaryResp1Dto total = dexpEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);*/
		
		/*Annexure1SummaryResp1Dto inv = dexpEYList.stream()                        
                .filter(x -> "INV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto cr = dexpEYList.stream()                        
                .filter(x -> "CR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto dr = dexpEYList.stream()                        
                .filter(x -> "DR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto rdr = dexpEYList.stream()                        
                .filter(x -> "RDR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null); 
		
		Annexure1SummaryResp1Dto rnv = dexpEYList.stream()                        
                .filter(x -> "RNV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto rcr = dexpEYList.stream()                        
                .filter(x -> "RCR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummarySectionDto addGstnDEData = gstnCalculation
				.addGstnB2cData(gstnResult);
	//	Annexure1SummarySectionDto addGstnB2cData = null;
		
		Annexure1SummaryResp1Dto deemExpTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnDEData);
		*/
		 List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	     //   retList.add(total);
	        retList.addAll(dexpEYList);
		
		JsonElement dexpRespbody = gson.toJsonTree(retList);

		return dexpRespbody;

	}
	
	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumDexpResp(
			List<Annexure1SummaryResp1Dto> dexpEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultSEZWTEYList = 
				getDefaultDExpEYStructure();

		List<Annexure1SummaryResp1Dto> dexpEYList = anx1DExpEYFinalStructure
				.getDExpEyList(defaultSEZWTEYList, dexpEySummary);

		
		Annexure1SummaryResp1Dto inv = dexpEYList.stream()                        
                .filter(x -> "INV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto cr = dexpEYList.stream()                        
                .filter(x -> "CR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto dr = dexpEYList.stream()                        
                .filter(x -> "DR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto rdr = dexpEYList.stream()                        
                .filter(x -> "RDR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null); 
		
		Annexure1SummaryResp1Dto rnv = dexpEYList.stream()                        
                .filter(x -> "RNV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto rcr = dexpEYList.stream()                        
                .filter(x -> "RCR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummarySectionDto addGstnDEData = gstnCalculation
				.addGstnB2cData(gstnResult);
		
		Annexure1SummaryResp1Dto deemExpTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnDEData);
		
		
		return deemExpTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultDExpEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultDexpEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3GInv = new Annexure1SummaryResp1Dto();
		b2bEy3GInv.setTableSection("3G");
		b2bEy3GInv.setDocType("INV");
		b2bEy3GInv.setIndex(1);
		b2bEy3GInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3GInv);

		Annexure1SummaryResp1Dto b2bEy3GCr = new Annexure1SummaryResp1Dto();
		b2bEy3GCr.setTableSection("3G");
		b2bEy3GCr.setDocType("CR");
		b2bEy3GCr.setIndex(3);
		b2bEy3GCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GCr);

		Annexure1SummaryResp1Dto b2bEy3GDr = new Annexure1SummaryResp1Dto();
		b2bEy3GDr.setTableSection("3G");
		b2bEy3GDr.setDocType("DR");
		b2bEy3GDr.setIndex(2);
		b2bEy3GDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GDr);

		Annexure1SummaryResp1Dto b2bEy3GRnv = new Annexure1SummaryResp1Dto();
		b2bEy3GRnv.setTableSection("3G");
		b2bEy3GRnv.setDocType("RNV");
		b2bEy3GRnv.setIndex(4);
		b2bEy3GRnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3GRnv);

		Annexure1SummaryResp1Dto b2bEy3GRdr = new Annexure1SummaryResp1Dto();
		b2bEy3GRdr.setTableSection("3G");
		b2bEy3GRdr.setDocType("RDR");
		b2bEy3GRdr.setIndex(5);
		b2bEy3GRdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GRdr);

		Annexure1SummaryResp1Dto b2bEy3GRcr = new Annexure1SummaryResp1Dto();
		b2bEy3GRcr.setTableSection("3G");
		b2bEy3GRcr.setDocType("RCR");
		b2bEy3GRcr.setIndex(6);
		b2bEy3GRcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GRcr);

		defaultDexpEY.add(b2bEy3GInv);
		defaultDexpEY.add(b2bEy3GCr);
		defaultDexpEY.add(b2bEy3GDr);
		defaultDexpEY.add(b2bEy3GRnv);
		defaultDexpEY.add(b2bEy3GRdr);
		defaultDexpEY.add(b2bEy3GRcr);

		return defaultDexpEY;
	}

	private List<Annexure1SummaryResp1Dto> getDefaultDExpTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultDexpEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3GTotal = new Annexure1SummaryResp1Dto();
		b2bEy3GTotal.setTableSection("3G");
		b2bEy3GTotal.setDocType("total");
		b2bEy3GTotal.setIndex(0);
		b2bEy3GTotal = defaultStructureUtil.anx1DefaultStructure(b2bEy3GTotal);
		
		Annexure1SummaryResp1Dto b2bEy3GInv = new Annexure1SummaryResp1Dto();
		b2bEy3GInv.setTableSection("3G");
		b2bEy3GInv.setDocType("INV");
		b2bEy3GInv.setIndex(1);
		b2bEy3GInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3GInv);

		Annexure1SummaryResp1Dto b2bEy3GCr = new Annexure1SummaryResp1Dto();
		b2bEy3GCr.setTableSection("3G");
		b2bEy3GCr.setDocType("CR");
		b2bEy3GCr.setIndex(3);
		b2bEy3GCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GCr);

		Annexure1SummaryResp1Dto b2bEy3GDr = new Annexure1SummaryResp1Dto();
		b2bEy3GDr.setTableSection("3G");
		b2bEy3GDr.setDocType("DR");
		b2bEy3GDr.setIndex(2);
		b2bEy3GDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GDr);

		Annexure1SummaryResp1Dto b2bEy3GRnv = new Annexure1SummaryResp1Dto();
		b2bEy3GRnv.setTableSection("3G");
		b2bEy3GRnv.setDocType("RNV");
		b2bEy3GRnv.setIndex(4);
		b2bEy3GRnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3GRnv);

		Annexure1SummaryResp1Dto b2bEy3GRdr = new Annexure1SummaryResp1Dto();
		b2bEy3GRdr.setTableSection("3G");
		b2bEy3GRdr.setDocType("RDR");
		b2bEy3GRdr.setIndex(5);
		b2bEy3GRdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GRdr);

		Annexure1SummaryResp1Dto b2bEy3GRcr = new Annexure1SummaryResp1Dto();
		b2bEy3GRcr.setTableSection("3G");
		b2bEy3GRcr.setDocType("RCR");
		b2bEy3GRcr.setIndex(6);
		b2bEy3GRcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3GRcr);

		defaultDexpEY.add(b2bEy3GTotal);
		defaultDexpEY.add(b2bEy3GInv);
		defaultDexpEY.add(b2bEy3GCr);
		defaultDexpEY.add(b2bEy3GDr);
		defaultDexpEY.add(b2bEy3GRnv);
		defaultDexpEY.add(b2bEy3GRdr);
		defaultDexpEY.add(b2bEy3GRcr);

		return defaultDexpEY;
	}


	
}
