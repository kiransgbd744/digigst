package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryRespDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("B2CLAStructure")
public class B2CLAStructure {
	

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("B2CLAEYFinalStructure")
	private B2CLAEYFinalStructure b2claEYFinalStructure;
	
	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;


	public JsonElement b2claResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultb2claEYList = getDefaultB2CLAEYStructure();

		List<Gstr1SummaryRespDto> b2claEYList = b2claEYFinalStructure
				.getB2claEyList(defaultb2claEYList, eySummaryListFromView);
		
		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(b2claEYList);
		
		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);
		
		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffB2CLStructure(eyTotalCount, gstn);

		//Response Body for Json
		
		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);
		
		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(b2claEYList);
		
		JsonElement gstnRespBody = gson.toJsonTree(gstn);
		
		JsonElement diffRespBody = gson.toJsonTree(diff);
		
		
		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);
		
		Map<String,JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);
		
		JsonElement b2claRespbody = gson.toJsonTree(combinedMap);
		
		/*JsonObject b2clResp = new JsonObject();
		b2clResp.add("b2cl", b2clRespbody);*/
		
		return b2claRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultB2CLAEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultB2CLAEY = new ArrayList<>();

		Gstr1SummaryRespDto b2claEy9A = new Gstr1SummaryRespDto();
		b2claEy9A.setTableSection("9A");
		b2claEy9A = defaultStructureUtil.defaultB2CLStructure(b2claEy9A);

		defaultB2CLAEY.add(b2claEy9A);
		
		return defaultB2CLAEY;

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
			List<Gstr1SummaryRespDto> b2claEYList) {

		BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
	//	BigDecimal cgst = new BigDecimal(0);
	//	BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
		Integer records = new Integer(0);
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		List<Gstr1SummaryRespDto> b2claey = b2claEYList;
		for (Gstr1SummaryRespDto b2claeyreq : b2claey) {
			invValue = invValue.add(b2claeyreq.getInvValue());
			taxable = taxable.add(b2claeyreq.getTaxableValue());
			taxpayble = taxpayble.add(b2claeyreq.getTaxPayble());
			igst = igst.add(b2claeyreq.getIgst());
		//	cgst = cgst.add(b2claeyreq.getCgst());
		//	sgst = sgst.add(b2claeyreq.getSgst());
			cess = cess.add(b2claeyreq.getCess());
			records = records + (b2claeyreq.getRecords());

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
