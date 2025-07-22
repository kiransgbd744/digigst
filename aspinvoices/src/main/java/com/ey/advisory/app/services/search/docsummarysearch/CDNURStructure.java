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

@Service("CDNURStructure")
public class CDNURStructure {
	

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("CDNUREYFinalStructure")
	private CDNUREYFinalStructure cdnurEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement cdnurResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultcdnurEYList = getDefaultCDNUREYStructure();

		List<Gstr1SummaryRespDto> cdnurEYList = cdnurEYFinalStructure
				.getcdnurEyList(defaultcdnurEYList, eySummaryListFromView);
				

		
		
		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(cdnurEYList);

		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);

		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffStructure(eyTotalCount, gstn);

		// Response Body for Json

		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(cdnurEYList);

		JsonElement gstnRespBody = gson.toJsonTree(gstn);

		JsonElement diffRespBody = gson.toJsonTree(diff);

		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);

		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);

		JsonElement cdnurRespbody = gson.toJsonTree(combinedMap);

		/*
		 * JsonObject b2clResp = new JsonObject(); b2clResp.add("b2cl",
		 * b2clRespbody);
		 */

		return cdnurRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultCDNUREYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultCDNUREY = new ArrayList<>();

		Gstr1SummaryRespDto cdnurEy9B = new Gstr1SummaryRespDto();
		cdnurEy9B.setTableSection("9B");
		cdnurEy9B = defaultStructureUtil.defaultStructure(cdnurEy9B);

		defaultCDNUREY.add(cdnurEy9B);

		return defaultCDNUREY;

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
			List<Gstr1SummaryRespDto> cdnurEYList) {

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
		for (Gstr1SummaryRespDto cdnureyreq : cdnurEYList) {
			invValue = invValue.add(cdnureyreq.getInvValue());
			taxable = taxable.add(cdnureyreq.getTaxableValue());
			taxpayble = taxpayble.add(cdnureyreq.getTaxPayble());
			igst = igst.add(cdnureyreq.getIgst());
			cgst = cgst.add(cdnureyreq.getCgst());
			sgst = sgst.add(cdnureyreq.getSgst());
			cess = cess.add(cdnureyreq.getCess());
			records = records + (cdnureyreq.getRecords());

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
