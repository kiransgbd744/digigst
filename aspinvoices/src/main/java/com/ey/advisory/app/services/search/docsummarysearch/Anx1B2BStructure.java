package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("anx1B2BStructure")
public class Anx1B2BStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1B2BEYFinalStructure")
	private Anx1B2BEYFinalStructure anx1B2BEYFinalStructure;
	
	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;


	public JsonElement anx1B2bResp(
			List<Annexure1SummaryResp1Dto> b2bEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultB2bEYList = 
				getDefaultB2BTotalEYStructure();

		List<Annexure1SummaryResp1Dto> b2bEYList = anx1B2BEYFinalStructure
				.getB2BEyList(defaultB2bEYList, b2bEySummary);

		/*Annexure1SummaryResp1Dto total = b2bEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);
		
*/
	/*	Annexure1SummaryResp1Dto inv = b2bEYList.stream()                        
                .filter(x -> "INV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto cr = b2bEYList.stream()                        
                .filter(x -> "CR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto dr = b2bEYList.stream()                        
                .filter(x -> "DR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto rdr = b2bEYList.stream()                        
                .filter(x -> "RDR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null); 
		
		Annexure1SummaryResp1Dto rnv = b2bEYList.stream()                        
                .filter(x -> "RNV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto rcr = b2bEYList.stream()                        
                .filter(x -> "RCR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummarySectionDto addGstnB2bData = gstnCalculation
				.addGstnB2BData(gstnResult);
		
		Annexure1SummaryResp1Dto b2bTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnB2bData);
*/		
		
		  List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	   //     retList.add(total);
	        retList.addAll(b2bEYList);
			
		
		JsonElement b2bRespbody = gson.toJsonTree(retList);

		return b2bRespbody;

	}
	// This method is used For Calculating Total In Review Summary Section Wise
	public Annexure1SummaryResp1Dto anx1SumB2bResp(
			List<Annexure1SummaryResp1Dto> b2bEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultB2bEYList = 
				getDefaultB2BEYStructure();

		List<Annexure1SummaryResp1Dto> b2bEYList = anx1B2BEYFinalStructure
				.getB2BEyList(defaultB2bEYList, b2bEySummary);


		Annexure1SummaryResp1Dto inv = b2bEYList.stream()                        
                .filter(x -> "INV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto cr = b2bEYList.stream()                        
                .filter(x -> "CR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto dr = b2bEYList.stream()                        
                .filter(x -> "DR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);  
		
		Annexure1SummaryResp1Dto rdr = b2bEYList.stream()                        
                .filter(x -> "RDR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null); 
		
		Annexure1SummaryResp1Dto rnv = b2bEYList.stream()                        
                .filter(x -> "RNV".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummaryResp1Dto rcr = b2bEYList.stream()                        
                .filter(x -> "RCR".equals(x.getDocType()))        
                .findAny()                                     
                .orElse(null);
		
		Annexure1SummarySectionDto addGstnB2bData = gstnCalculation
				.addGstnB2BData(gstnResult);
		
		Annexure1SummaryResp1Dto b2bTotal = summaryTotalCalculation
				.totalCalculation(inv, dr, cr, rnv, rdr, rcr,addGstnB2bData);

		return b2bTotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultB2BEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultB2BEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3BInv = new Annexure1SummaryResp1Dto();
		b2bEy3BInv.setTableSection("3B");
		b2bEy3BInv.setDocType("INV");
		b2bEy3BInv.setIndex(1);
		b2bEy3BInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3BInv);

		Annexure1SummaryResp1Dto b2bEy3BCr = new Annexure1SummaryResp1Dto();
		b2bEy3BCr.setTableSection("3B");
		b2bEy3BCr.setDocType("CR");
		b2bEy3BCr.setIndex(3);
		b2bEy3BCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BCr);

		Annexure1SummaryResp1Dto b2bEy3BDr = new Annexure1SummaryResp1Dto();
		b2bEy3BDr.setTableSection("3B");
		b2bEy3BDr.setDocType("DR");
		b2bEy3BDr.setIndex(2);
		b2bEy3BDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BDr);

		Annexure1SummaryResp1Dto b2bEy3BRnv = new Annexure1SummaryResp1Dto();
		b2bEy3BRnv.setTableSection("3B");
		b2bEy3BRnv.setDocType("RNV");
		b2bEy3BRnv.setIndex(4);
		b2bEy3BRnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3BRnv);

		Annexure1SummaryResp1Dto b2bEy3BRdr = new Annexure1SummaryResp1Dto();
		b2bEy3BRdr.setTableSection("3B");
		b2bEy3BRdr.setDocType("RDR");
		b2bEy3BRdr.setIndex(5);
		b2bEy3BRdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BRdr);

		Annexure1SummaryResp1Dto b2bEy3BRcr = new Annexure1SummaryResp1Dto();
		b2bEy3BRcr.setTableSection("3B");
		b2bEy3BRcr.setDocType("RCR");
		b2bEy3BRcr.setIndex(6);
		b2bEy3BRcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BRcr);

		defaultB2BEY.add(b2bEy3BInv);
		defaultB2BEY.add(b2bEy3BCr);
		defaultB2BEY.add(b2bEy3BDr);
		defaultB2BEY.add(b2bEy3BRnv);
		defaultB2BEY.add(b2bEy3BRdr);
		defaultB2BEY.add(b2bEy3BRcr);

		return defaultB2BEY;
	}
	
	private List<Annexure1SummaryResp1Dto> getDefaultB2BTotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultB2BEY = new ArrayList<>();

		Annexure1SummaryResp1Dto b2bEy3BTotal = new Annexure1SummaryResp1Dto();
		b2bEy3BTotal.setTableSection("3B");
		b2bEy3BTotal.setDocType("total");
		b2bEy3BTotal.setIndex(0);
		b2bEy3BTotal = defaultStructureUtil.anx1DefaultStructure(b2bEy3BTotal);
		
		
		Annexure1SummaryResp1Dto b2bEy3BInv = new Annexure1SummaryResp1Dto();
		b2bEy3BInv.setTableSection("3B");
		b2bEy3BInv.setDocType("INV");
		b2bEy3BInv.setIndex(1);
		b2bEy3BInv = defaultStructureUtil.anx1DefaultStructure(b2bEy3BInv);

		Annexure1SummaryResp1Dto b2bEy3BCr = new Annexure1SummaryResp1Dto();
		b2bEy3BCr.setTableSection("3B");
		b2bEy3BCr.setDocType("CR");
		b2bEy3BCr.setIndex(3);
		b2bEy3BCr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BCr);

		Annexure1SummaryResp1Dto b2bEy3BDr = new Annexure1SummaryResp1Dto();
		b2bEy3BDr.setTableSection("3B");
		b2bEy3BDr.setDocType("DR");
		b2bEy3BDr.setIndex(2);
		b2bEy3BDr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BDr);

		Annexure1SummaryResp1Dto b2bEy3BRnv = new Annexure1SummaryResp1Dto();
		b2bEy3BRnv.setTableSection("3B");
		b2bEy3BRnv.setDocType("RNV");
		b2bEy3BRnv.setIndex(4);
		b2bEy3BRnv = defaultStructureUtil.anx1DefaultStructure(b2bEy3BRnv);

		Annexure1SummaryResp1Dto b2bEy3BRdr = new Annexure1SummaryResp1Dto();
		b2bEy3BRdr.setTableSection("3B");
		b2bEy3BRdr.setDocType("RDR");
		b2bEy3BRdr.setIndex(5);
		b2bEy3BRdr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BRdr);

		Annexure1SummaryResp1Dto b2bEy3BRcr = new Annexure1SummaryResp1Dto();
		b2bEy3BRcr.setTableSection("3B");
		b2bEy3BRcr.setDocType("RCR");
		b2bEy3BRcr.setIndex(6);
		b2bEy3BRcr = defaultStructureUtil.anx1DefaultStructure(b2bEy3BRcr);

		defaultB2BEY.add(b2bEy3BTotal);
		defaultB2BEY.add(b2bEy3BInv);
		defaultB2BEY.add(b2bEy3BCr);
		defaultB2BEY.add(b2bEy3BDr);
		defaultB2BEY.add(b2bEy3BRnv);
		defaultB2BEY.add(b2bEy3BRdr);
		defaultB2BEY.add(b2bEy3BRcr);

		return defaultB2BEY;
	}

}
