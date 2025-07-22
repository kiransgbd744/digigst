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

@Service("B2CSStructure")
public class B2CSStructure {
	
	
	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("B2CSEYFinalStructure")
	private B2CSEYFinalStructure b2csEYFinalStructure;
	
	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement b2csResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultB2csEYList = getDefaultB2CSEYStructure();

		List<Gstr1SummaryRespDto> b2csEYList = b2csEYFinalStructure
				.getB2csEyList(defaultB2csEYList, eySummaryListFromView);
		
		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(b2csEYList);
		
		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);
		
		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffB2CSStructure(eyTotalCount, gstn);

		
		//Response Body for Json
		
		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);
		
		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(b2csEYList);
		
		JsonElement gstnRespBody = gson.toJsonTree(gstn);
		
		JsonElement diffRespBody = gson.toJsonTree(diff);
		
		
		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);
		
		Map<String,JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);
		
		JsonElement b2csRespbody = gson.toJsonTree(combinedMap);
		
		/*JsonObject b2clResp = new JsonObject();
		b2clResp.add("b2cl", b2clRespbody);*/
		
		return b2csRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultB2CSEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultB2CSEY = new ArrayList<>();

		Gstr1SummaryRespDto b2csEy7A1 = new Gstr1SummaryRespDto();
		b2csEy7A1.setTableSection("7A(1)");
		b2csEy7A1 = defaultStructureUtil.defaultB2CSStructure(b2csEy7A1);

		Gstr1SummaryRespDto b2csEy7B1 = new Gstr1SummaryRespDto();
		b2csEy7B1.setTableSection("7B(1)");
		b2csEy7B1 = defaultStructureUtil.defaultB2CSStructure(b2csEy7B1);
		
		Gstr1SummaryRespDto b2csEy7A2 = new Gstr1SummaryRespDto();
		b2csEy7A2.setTableSection("7A(2)");
		b2csEy7A2 = defaultStructureUtil.defaultB2CSStructure(b2csEy7A2);

		Gstr1SummaryRespDto b2csEy7B2 = new Gstr1SummaryRespDto();
		b2csEy7B2.setTableSection("7B(2)");
		b2csEy7B2 = defaultStructureUtil.defaultB2CSStructure(b2csEy7B2);


		defaultB2CSEY.add(b2csEy7A1);
		defaultB2CSEY.add(b2csEy7B1);
		defaultB2CSEY.add(b2csEy7A2);
		defaultB2CSEY.add(b2csEy7B2);

		return defaultB2CSEY;

	}

	private Gstr1SummaryRespDto getGstnStructure(
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		Gstr1SummaryRespDto gstnResp = new Gstr1SummaryRespDto();
		gstnResp.setTableSection("-");
		for (Gstr1B2BGstnRespDto gstn : gstnList) {
		//	gstnResp.setRecords(gstn.getRecords());
			gstnResp.setTaxPayble(gstn.getTaxPayble());
			gstnResp.setTaxableValue(gstn.getTaxableValue());
			gstnResp.setIgst(gstn.getIgst());
			gstnResp.setCgst(gstn.getCgst());
			gstnResp.setSgst(gstn.getSgst());
		//	gstnResp.setInvValue(gstn.getInvValue());
			gstnResp.setCess(gstn.getCess());
		}
		return gstnResp;
	}

	
	private Gstr1SummaryRespDto eyTotalCount(
			List<Gstr1SummaryRespDto> b2csEYList) {

	//	BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
		BigDecimal cgst = new BigDecimal(0);
		BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
	//	Integer records = new Integer(0);
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		List<Gstr1SummaryRespDto> b2cley = b2csEYList;
		for (Gstr1SummaryRespDto b2cleyreq : b2cley) {
		//	invValue = invValue.add(b2cleyreq.getInvValue());
			taxable = taxable.add(b2cleyreq.getTaxableValue());
			taxpayble = taxpayble.add(b2cleyreq.getTaxPayble());
			igst = igst.add(b2cleyreq.getIgst());
			cgst = cgst.add(b2cleyreq.getCgst());
			sgst = sgst.add(b2cleyreq.getSgst());
			cess = cess.add(b2cleyreq.getCess());
		//	records = records + (b2cleyreq.getRecords());

		}

	//	eyTotal.setInvValue(invValue);
		eyTotal.setTaxableValue(taxable);
		eyTotal.setTaxPayble(taxpayble);
		eyTotal.setIgst(igst);
		eyTotal.setCgst(cgst);
		eyTotal.setSgst(sgst);
		eyTotal.setCess(cess);
	//	eyTotal.setRecords(records);

		return eyTotal;

	}

}
