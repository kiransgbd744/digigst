package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("EXPAMDStructure")
public class EXPAMDStructure {

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("EXPAEYFinalStructure")
	private EXPAEYFinalStructure expaEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement expaResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		 List<Gstr1SummaryRespDto> defaultexpEYList = getDefaultEXPEYStructure();

		
		 List<Gstr1SummaryRespDto> expEYList = expaEYFinalStructure
		 .getexpaEyList(defaultexpEYList, eySummaryListFromView);
		  
		  
	
		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(expEYList);

		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);

		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffB2CLStructure(eyTotalCount, gstn);

		// Response Body for Json

		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(expEYList);

		JsonElement gstnRespBody = gson.toJsonTree(gstn);

		JsonElement diffRespBody = gson.toJsonTree(diff);

		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);

		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);

		JsonElement expRespbody = gson.toJsonTree(combinedMap);

		/*
		 * JsonObject b2clResp = new JsonObject(); b2clResp.add("b2cl",
		 * b2clRespbody);
		 */

		return expRespbody;

	}

	
	 private List<Gstr1SummaryRespDto> getDefaultEXPEYStructure() { // TODO
	// Auto-generated method stub 
	 List<Gstr1SummaryRespDto> defaultEXPEY = new ArrayList<>();
	
	  Gstr1SummaryRespDto expAEy9A = new Gstr1SummaryRespDto();
	  expAEy9A.setTableSection("9A"); 
	  expAEy9A = defaultStructureUtil.defaultStructure(expAEy9A);
	 
	  defaultEXPEY.add(expAEy9A);
	  
	 return defaultEXPEY;
	  
	  }
	 
	private Gstr1SummaryRespDto getGstnStructure(
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		Gstr1SummaryRespDto gstnResp = new Gstr1SummaryRespDto();
		gstnResp.setTableSection("-");
		for (Gstr1B2BGstnRespDto gstn : gstnList) {
			gstnResp.setRecords(gstn.getRecords());
			gstnResp.setTaxPayble(gstn.getTaxPayble());
			gstnResp.setTaxableValue(gstn.getTaxableValue());
			gstnResp.setIgst(gstn.getIgst());
		//	gstnResp.setCgst(gstn.getCgst());
		//	gstnResp.setSgst(gstn.getSgst());
			gstnResp.setInvValue(gstn.getInvValue());
			gstnResp.setCess(gstn.getCess());
		}
		return gstnResp;
	}

	private Gstr1SummaryRespDto eyTotalCount(
			List<Gstr1SummaryRespDto> expEYList) {

		BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
	//	BigDecimal cgst = new BigDecimal(0);
		//BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
		Integer records = new Integer(0);
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		// List<Gstr1SummaryRespDto> b2baey = cdnrEYList;
		for (Gstr1SummaryRespDto expeyreq : expEYList) {
			invValue = invValue.add(expeyreq.getInvValue());
			taxable = taxable.add(expeyreq.getTaxableValue());
			taxpayble = taxpayble.add(expeyreq.getTaxPayble());
			igst = igst.add(expeyreq.getIgst());
		//	cgst = cgst.add(expeyreq.getCgst());
		//	sgst = sgst.add(expeyreq.getSgst());
			cess = cess.add(expeyreq.getCess());
			records = records + (expeyreq.getRecords());

		}

		eyTotal.setInvValue(invValue);
		eyTotal.setTaxableValue(taxable);
		eyTotal.setTaxPayble(taxpayble);
		eyTotal.setIgst(igst);
	//	eyTotal.setCgst(cgst);
	//	eyTotal.setSgst(sgst);
		eyTotal.setCess(cess);
		eyTotal.setRecords(records);

		return eyTotal;

	}

}
