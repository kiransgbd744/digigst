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

@Service("B2BAStructure")
public class B2BAStructure {
	

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("B2BAEYFinalStructure")
	private B2BAEYFinalStructure b2baEYFinalStructure;
	
	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;


	public JsonElement b2baResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultb2baEYList = getDefaultB2BAEYStructure();

		List<Gstr1SummaryRespDto> b2baEYList = b2baEYFinalStructure
				.getb2baEyList(defaultb2baEYList, eySummaryListFromView);
		
		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(b2baEYList);
		
		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);
		
		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffStructure(eyTotalCount, gstn);

		//Response Body for Json
		
		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);
		
		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(b2baEYList);
		
		JsonElement gstnRespBody = gson.toJsonTree(gstn);
		
		JsonElement diffRespBody = gson.toJsonTree(diff);
		
		
		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);
		
		Map<String,JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);
		
		JsonElement cdnrRespbody = gson.toJsonTree(combinedMap);
		
		/*JsonObject b2clResp = new JsonObject();
		b2clResp.add("b2cl", b2clRespbody);*/
		
		return cdnrRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultB2BAEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultB2BAEY = new ArrayList<>();

		Gstr1SummaryRespDto b2baEy9A = new Gstr1SummaryRespDto();
		b2baEy9A.setTableSection("9A");
		b2baEy9A = defaultStructureUtil.defaultStructure(b2baEy9A);

		defaultB2BAEY.add(b2baEy9A);
		
		return defaultB2BAEY;

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
			gstnResp.setCgst(gstn.getCgst());
			gstnResp.setSgst(gstn.getSgst());
			gstnResp.setInvValue(gstn.getInvValue());
			gstnResp.setCess(gstn.getCess());
		}
		return gstnResp;
	}

	
	private Gstr1SummaryRespDto eyTotalCount(
			List<Gstr1SummaryRespDto> b2baEYList) {

		BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
		BigDecimal cgst = new BigDecimal(0);
		BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
		Integer records = new Integer(0);
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		//List<Gstr1SummaryRespDto> b2baey = cdnrEYList;
		for (Gstr1SummaryRespDto b2baeyreq : b2baEYList) {
			invValue = invValue.add(b2baeyreq.getInvValue());
			taxable = taxable.add(b2baeyreq.getTaxableValue());
			taxpayble = taxpayble.add(b2baeyreq.getTaxPayble());
			igst = igst.add(b2baeyreq.getIgst());
			cgst = cgst.add(b2baeyreq.getCgst());
			sgst = sgst.add(b2baeyreq.getSgst());
			cess = cess.add(b2baeyreq.getCess());
			records = records + (b2baeyreq.getRecords());

		}

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
