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
import com.ey.advisory.app.docs.dto.simplified.Annexure1B2BEYFinalStructure;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryDifference;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("Annexure1B2BStructure")
public class Annexure1B2BStructure {

	@Autowired
	@Qualifier("Annexure1DefaultSummaryStructureUtil")
	private Annexure1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Annexure1B2BEYFinalStructure")
	private Annexure1B2BEYFinalStructure b2bEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement b2bResp(
			List<Annexure1SummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryRespDto> defaultB2bEYList = 
												getDefaultB2BEYStructure();

		List<Annexure1SummaryRespDto> b2bEYList = b2bEYFinalStructure
				.getB2BEyList(defaultB2bEYList, eySummaryListFromView);

		Annexure1SummaryRespDto eyTotalCount = eyTotalCount(b2bEYList);

		//Annexure1SummaryRespDto gstn = getGstnStructure(gstnList);
		Annexure1SummaryRespDto gstn = new Annexure1SummaryRespDto();

		/*Annexure1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffStructure(eyTotalCount, gstn);*/
		Annexure1SummaryRespDto diff = new Annexure1SummaryRespDto();
		
		// Response Body for Json

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(b2bEYList);

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

	private List<Annexure1SummaryRespDto> getDefaultB2BEYStructure() {

		List<Annexure1SummaryRespDto> defaultB2BEY = new ArrayList<>();

		Annexure1SummaryRespDto b2bEyINV = new Annexure1SummaryRespDto();
		b2bEyINV.setTableSection("1_3B");
		b2bEyINV.setDocType("INV");
		b2bEyINV = defaultStructureUtil.defaultStructure(b2bEyINV);
		
		Annexure1SummaryRespDto b2bEyDR = new Annexure1SummaryRespDto();
		b2bEyDR.setTableSection("1_3B");
		b2bEyDR.setDocType("DR");
		b2bEyDR = defaultStructureUtil.defaultStructure(b2bEyDR);
		
		Annexure1SummaryRespDto b2bEyCR = new Annexure1SummaryRespDto();
		b2bEyCR.setTableSection("1_3B");
		b2bEyCR.setDocType("CR");
		b2bEyCR = defaultStructureUtil.defaultStructure(b2bEyCR);
		
		Annexure1SummaryRespDto b2bEyRNV = new Annexure1SummaryRespDto();
		b2bEyRNV.setTableSection("1_3B");
		b2bEyRNV.setDocType("RNV");
		b2bEyRNV = defaultStructureUtil.defaultStructure(b2bEyRNV);
		
		Annexure1SummaryRespDto b2bEyRDR = new Annexure1SummaryRespDto();
		b2bEyRDR.setTableSection("1_3B");
		b2bEyRDR.setDocType("RDR");
		b2bEyRDR = defaultStructureUtil.defaultStructure(b2bEyRDR);
		
		Annexure1SummaryRespDto b2bEyRCR = new Annexure1SummaryRespDto();
		b2bEyRCR.setTableSection("1_3B");
		b2bEyRCR.setDocType("RCR");
		b2bEyRCR = defaultStructureUtil.defaultStructure(b2bEyRCR);


		defaultB2BEY.add(b2bEyINV);
		defaultB2BEY.add(b2bEyDR);
		defaultB2BEY.add(b2bEyCR);
		defaultB2BEY.add(b2bEyRNV);
		defaultB2BEY.add(b2bEyRDR);
		defaultB2BEY.add(b2bEyRCR);

		return defaultB2BEY;
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
