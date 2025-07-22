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
import com.ey.advisory.app.docs.dto.simplified.Annexure1SeztEYFinalStructure;
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
@Service("Annexure1SeztStructure")
public class Annexure1SeztStructure {
	
	@Autowired
	@Qualifier("Annexure1DefaultSummaryStructureUtil")
	private Annexure1DefaultSummaryStructureUtil defaultStructureUtil;
	
	@Autowired
	@Qualifier("Annexure1SeztEYFinalStructure")
	private Annexure1SeztEYFinalStructure seztEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;
	
	public JsonElement seztResp(
			List<Annexure1SummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		List<Annexure1SummaryRespDto> defaultEYList = 
				getDefaultExptEYStructure();
		
		List<Annexure1SummaryRespDto> eyList = seztEYFinalStructure
				.getSeztEyList(defaultEYList, eySummaryListFromView);
		
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
	
	private List<Annexure1SummaryRespDto> getDefaultExptEYStructure() {

		List<Annexure1SummaryRespDto> defaultEY = new ArrayList<>();

		Annexure1SummaryRespDto b2cEyINV = new Annexure1SummaryRespDto();
		b2cEyINV.setTableSection("1_3E");
		b2cEyINV.setDocType("INV");
		b2cEyINV = defaultStructureUtil.defaultStructure(b2cEyINV);
		
		Annexure1SummaryRespDto b2cEyDR = new Annexure1SummaryRespDto();
		b2cEyDR.setTableSection("1_3E");
		b2cEyDR.setDocType("DR");
		b2cEyDR = defaultStructureUtil.defaultStructure(b2cEyDR);
		
		Annexure1SummaryRespDto b2cEyCR = new Annexure1SummaryRespDto();
		b2cEyCR.setTableSection("1_3E");
		b2cEyCR.setDocType("CR");
		b2cEyCR = defaultStructureUtil.defaultStructure(b2cEyCR);
		
		Annexure1SummaryRespDto b2cEyRNV = new Annexure1SummaryRespDto();
		b2cEyRNV.setTableSection("1_3E");
		b2cEyRNV.setDocType("RNV");
		b2cEyRNV = defaultStructureUtil.defaultStructure(b2cEyRNV);
		
		Annexure1SummaryRespDto b2cEyRDR = new Annexure1SummaryRespDto();
		b2cEyRDR.setTableSection("1_3E");
		b2cEyRDR.setDocType("RDR");
		b2cEyRDR = defaultStructureUtil.defaultStructure(b2cEyRDR);
		
		Annexure1SummaryRespDto b2cEyRCR = new Annexure1SummaryRespDto();
		b2cEyRCR.setTableSection("1_3E");
		b2cEyRCR.setDocType("RCR");
		b2cEyRCR = defaultStructureUtil.defaultStructure(b2cEyRCR);


		defaultEY.add(b2cEyINV);
		defaultEY.add(b2cEyDR);
		defaultEY.add(b2cEyCR);
		defaultEY.add(b2cEyRNV);
		defaultEY.add(b2cEyRDR);
		defaultEY.add(b2cEyRCR);

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
