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

@Service("HSNStructure")
public class HSNStructure {
	

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("HSNEYFinalStructure")
	private HSNEYFinalStructure hsnEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement hsnResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaulthsnEYList = getDefaultHSNEYStructure();

		List<Gstr1SummaryRespDto> hsnEYList = hsnEYFinalStructure
				.gethsnEyList(defaulthsnEYList, eySummaryListFromView);

		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(hsnEYList);

		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);

		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffStructure(eyTotalCount, gstn);

		// Response Body for Json

		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(hsnEYList);

		JsonElement gstnRespBody = gson.toJsonTree(gstn);

		JsonElement diffRespBody = gson.toJsonTree(diff);

		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);

		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);

		JsonElement cdnraRespbody = gson.toJsonTree(combinedMap);

		/*
		 * JsonObject b2clResp = new JsonObject(); b2clResp.add("b2cl",
		 * b2clRespbody);
		 */

		return cdnraRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultHSNEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultHSNEY = new ArrayList<>();

		Gstr1SummaryRespDto hsnEy12 = new Gstr1SummaryRespDto();
		hsnEy12.setTableSection("12");
		hsnEy12 = defaultStructureUtil.defaultStructure(hsnEy12);

		defaultHSNEY.add(hsnEy12);

		return defaultHSNEY;

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
			List<Gstr1SummaryRespDto> hsnEYList) {

		BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
		BigDecimal cgst = new BigDecimal(0);
		BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
		Integer records = new Integer(0);
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		// List<Gstr1SummaryRespDto> b2baey = cdnrEYList;
		for (Gstr1SummaryRespDto hsneyreq : hsnEYList) {
			invValue = invValue.add(hsneyreq.getInvValue());
			taxable = taxable.add(hsneyreq.getTaxableValue());
			taxpayble = taxpayble.add(hsneyreq.getTaxPayble());
			igst = igst.add(hsneyreq.getIgst());
			cgst = cgst.add(hsneyreq.getCgst());
			sgst = sgst.add(hsneyreq.getSgst());
			cess = cess.add(hsneyreq.getCess());
			records = records + (hsneyreq.getRecords());

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
