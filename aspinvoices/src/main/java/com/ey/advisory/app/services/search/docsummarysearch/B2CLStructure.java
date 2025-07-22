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
import com.google.gson.JsonObject;

@Service("B2CLStructure")
public class B2CLStructure {

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("B2CLEYFinalStructure")
	private B2CLEYFinalStructure b2clEYFinalStructure;
	
	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement b2clResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultB2clEYList = getDefaultB2CLEYStructure();

		List<Gstr1SummaryRespDto> b2clEYList = b2clEYFinalStructure
				.getB2clEyList(defaultB2clEYList, eySummaryListFromView);
		
		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(b2clEYList);
		
		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);
		
		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffB2CLStructure(eyTotalCount, gstn);
	/*	Gstr1SummaryRespDto diff=getDiffStructure(eyTotalCount, gstn);*/
		//Response Body for Json
		
		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);
		
		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(b2clEYList);
		
		JsonElement gstnRespBody = gson.toJsonTree(gstn);
		
		JsonElement diffRespBody = gson.toJsonTree(diff);
		
		
		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);
		
		Map<String,JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);
		
		JsonElement b2clRespbody = gson.toJsonTree(combinedMap);
		
		/*JsonObject b2clResp = new JsonObject();
		b2clResp.add("b2cl", b2clRespbody);*/
		
		return b2clRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultB2CLEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultB2CLEY = new ArrayList<>();

		Gstr1SummaryRespDto b2clEy5A = new Gstr1SummaryRespDto();
		b2clEy5A.setTableSection("5A");
		b2clEy5A = defaultStructureUtil.defaultB2CLStructure(b2clEy5A);

		Gstr1SummaryRespDto b2clEy5B = new Gstr1SummaryRespDto();
		b2clEy5B.setTableSection("5B");
		b2clEy5B = defaultStructureUtil.defaultB2CLStructure(b2clEy5B);

		defaultB2CLEY.add(b2clEy5A);
		defaultB2CLEY.add(b2clEy5B);

		return defaultB2CLEY;

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
			List<Gstr1SummaryRespDto> b2clEYList) {
		
		BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
		//BigDecimal cgst = new BigDecimal(0);
	//	BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
		Integer records = new Integer(0);
		String tableSection= b2clEYList.get(0).getTableSection();
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		List<Gstr1SummaryRespDto> b2cley = b2clEYList;
		for (Gstr1SummaryRespDto b2cleyreq : b2cley) {
			invValue = invValue.add(b2cleyreq.getInvValue());
			taxable = taxable.add(b2cleyreq.getTaxableValue());
			taxpayble = taxpayble.add(b2cleyreq.getTaxPayble());
			igst = igst.add(b2cleyreq.getIgst());
		//	cgst = cgst.add(b2cleyreq.getCgst());
		//	sgst = sgst.add(b2cleyreq.getSgst());
			cess = cess.add(b2cleyreq.getCess());
			records = records + (b2cleyreq.getRecords());

		}
		eyTotal.setTableSection(tableSection);
		eyTotal.setInvValue(invValue);
		eyTotal.setTaxableValue(taxable);
		eyTotal.setTaxPayble(taxpayble);
		eyTotal.setIgst(igst);
		//eyTotal.setCgst(cgst);
		//eyTotal.setSgst(sgst);
		eyTotal.setCess(cess);
		eyTotal.setRecords(records);

		return eyTotal;

	}
	private Gstr1SummaryRespDto getDiffStructure(Gstr1SummaryRespDto eyTotal,
			Gstr1SummaryRespDto gstn) {

		Gstr1SummaryRespDto diff = new Gstr1SummaryRespDto();

		diff.setTableSection("-");

		//Subtraction logic : diff = eyTotal - gstn 
		BigDecimal invValue = eyTotal.getInvValue()
				.subtract(gstn.getInvValue());
		BigDecimal taxable = eyTotal.getTaxableValue()
				.subtract(gstn.getTaxableValue());
		BigDecimal taxpayble = eyTotal.getTaxPayble()
				.subtract(gstn.getTaxPayble());
		BigDecimal igst = eyTotal.getIgst().subtract(gstn.getIgst());
	//	BigDecimal cgst = eyTotal.getCgst().subtract(gstn.getCgst());
	//	BigDecimal sgst = eyTotal.getSgst().subtract(gstn.getSgst());
		BigDecimal cess = eyTotal.getCess().subtract(gstn.getCess());
		Integer records = eyTotal.getRecords() - (gstn.getRecords());

		diff.setInvValue(invValue);
		diff.setTaxableValue(taxable);
		diff.setTaxPayble(taxpayble);
		diff.setIgst(igst);
	//	diff.setCgst(cgst);
	//	diff.setSgst(sgst);
		diff.setCess(cess);
		diff.setRecords(records);

		return diff;
	}


}
