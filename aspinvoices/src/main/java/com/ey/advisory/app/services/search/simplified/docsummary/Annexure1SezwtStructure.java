package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SezwtEYFinalStructure;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryDifference;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("Annexure1SezwtStructure")
public class Annexure1SezwtStructure {
	
	@Autowired
	@Qualifier("Annexure1DefaultSummaryStructureUtil")
	private Annexure1DefaultSummaryStructureUtil defaultStructureUtil;
	
	@Autowired
	@Qualifier("Annexure1SezwtEYFinalStructure")
	private Annexure1SezwtEYFinalStructure sezwtEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;
	
	public JsonElement sezwtResp(
			List<Annexure1SummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		List<Annexure1SummaryRespDto> defaultEYList = 
				getDefaultSezwtEYStructure();
		
		List<Annexure1SummaryRespDto> eyList = sezwtEYFinalStructure
				.getSezwtEyList(defaultEYList, eySummaryListFromView);
		
		Annexure1SummaryRespDto eyTotalCount = eyTotalCount(eyList);

		//Annexure1SummaryRespDto gstn = getGstnStructure(gstnList);
		Annexure1SummaryRespDto gstn = new Annexure1SummaryRespDto();

		/*Annexure1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffStructure(eyTotalCount, gstn);*/
		Annexure1SummaryRespDto diff = new Annexure1SummaryRespDto();
		
		// Response Body for Json

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(eyList);

		JsonElement gstnRespBody = gson.toJsonTree(gstn);

		JsonElement diffRespBody = gson.toJsonTree(diff);

		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);

		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);

		JsonElement b2bRespbody = gson.toJsonTree(combinedMap);

		return b2bRespbody;
	}
	
	private List<Annexure1SummaryRespDto> getDefaultSezwtEYStructure() {

		List<Annexure1SummaryRespDto> defaultEY = new ArrayList<>();

		Annexure1SummaryRespDto eyINV = new Annexure1SummaryRespDto();
		eyINV.setTableSection("1_3F");
		eyINV.setDocType("INV");
		eyINV = defaultStructureUtil.defaultStructure(eyINV);
		
		Annexure1SummaryRespDto eyDR = new Annexure1SummaryRespDto();
		eyDR.setTableSection("1_3F");
		eyDR.setDocType("DR");
		eyDR = defaultStructureUtil.defaultStructure(eyDR);
		
		Annexure1SummaryRespDto eyCR = new Annexure1SummaryRespDto();
		eyCR.setTableSection("1_3F");
		eyCR.setDocType("CR");
		eyCR = defaultStructureUtil.defaultStructure(eyCR);
		
		Annexure1SummaryRespDto eyRNV = new Annexure1SummaryRespDto();
		eyRNV.setTableSection("1_3F");
		eyRNV.setDocType("RNV");
		eyRNV = defaultStructureUtil.defaultStructure(eyRNV);
		
		Annexure1SummaryRespDto eyRDR = new Annexure1SummaryRespDto();
		eyRDR.setTableSection("1_3F");
		eyRDR.setDocType("RDR");
		eyRDR = defaultStructureUtil.defaultStructure(eyRDR);
		
		Annexure1SummaryRespDto eyRCR = new Annexure1SummaryRespDto();
		eyRCR.setTableSection("1_3F");
		eyRCR.setDocType("RCR");
		eyRCR = defaultStructureUtil.defaultStructure(eyRCR);


		defaultEY.add(eyINV);
		defaultEY.add(eyDR);
		defaultEY.add(eyCR);
		defaultEY.add(eyRNV);
		defaultEY.add(eyRDR);
		defaultEY.add(eyRCR);

		return defaultEY;
	}
	
	private Annexure1SummaryRespDto getGstnStructure(
			List<? extends Annexure1SummaryRespDto> gstnList) {
		Annexure1SummaryRespDto gstnResp = new Annexure1SummaryRespDto();
		gstnResp.setTableSection("-");
		for (Annexure1SummaryRespDto gstn : gstnList) {
			gstnResp.setRecords(gstn.getRecords());
			gstnResp.setTaxPayble(gstn.getTaxPayble());
			gstnResp.setTaxableValue(gstn.getTaxableValue());
			gstnResp.setIgst(gstn.getIgst());
			gstnResp.setCgst(gstn.getCgst());
			gstnResp.setSgst(gstn.getSgst());
			gstnResp.setInvValue(gstn.getInvValue());
			gstnResp.setCess(gstn.getCess());
		}
		return gstnResp;
	}

	private Annexure1SummaryRespDto eyTotalCount(
			List<Annexure1SummaryRespDto> b2bEYList) {

		BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
		BigDecimal cgst = new BigDecimal(0);
		BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
		Integer records = new Integer(0);
		Annexure1SummaryRespDto eyTotal = new Annexure1SummaryRespDto();

		List<Annexure1SummaryRespDto> b2bey = b2bEYList;
		/*for (Annexure1SummaryRespDto b2beyreq : b2bey) {
			invValue = invValue.add(b2beyreq.getInvValue());
			taxable = taxable.add(b2beyreq.getTaxableValue());
			taxpayble = taxpayble.add(b2beyreq.getTaxPayble());
			igst = igst.add(b2beyreq.getIgst());
			cgst = cgst.add(b2beyreq.getCgst());
			sgst = sgst.add(b2beyreq.getSgst());
			cess = cess.add(b2beyreq.getCess());
			records = records + (b2beyreq.getRecords());

		}*/

		eyTotal.setInvValue(invValue);
		eyTotal.setTaxableValue(taxable);
		eyTotal.setTaxPayble(taxpayble);
		eyTotal.setIgst(igst);
		eyTotal.setCgst(cgst);
		eyTotal.setSgst(sgst);
		eyTotal.setCess(cess);
		eyTotal.setRecords(records);

		return eyTotal;

	}

}
