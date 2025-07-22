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

@Service("CDNRAStructure")
public class CDNRAStructure {

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("CDNRAEYFinalStructure")
	private CDNRAEYFinalStructure cdnraEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement cdnraResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultcdnraEYList = getDefaultCDNRAEYStructure();

		List<Gstr1SummaryRespDto> cdnraEYList = cdnraEYFinalStructure
				.getcdnraEyList(defaultcdnraEYList, eySummaryListFromView);

		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(cdnraEYList);

		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);

		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffStructure(eyTotalCount, gstn);

		// Response Body for Json

		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(cdnraEYList);

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

	private List<Gstr1SummaryRespDto> getDefaultCDNRAEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultCDNRAEY = new ArrayList<>();

		Gstr1SummaryRespDto cdnraEy9B = new Gstr1SummaryRespDto();
		cdnraEy9B.setTableSection("9B");
		/*cdnraEy9B.setTableSection("9C");*/
		cdnraEy9B = defaultStructureUtil.defaultStructure(cdnraEy9B);

		defaultCDNRAEY.add(cdnraEy9B);

		return defaultCDNRAEY;

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
			List<Gstr1SummaryRespDto> cdnraEYList) {

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
		for (Gstr1SummaryRespDto cdnraeyreq : cdnraEYList) {
			invValue = invValue.add(cdnraeyreq.getInvValue());
			taxable = taxable.add(cdnraeyreq.getTaxableValue());
			taxpayble = taxpayble.add(cdnraeyreq.getTaxPayble());
			igst = igst.add(cdnraeyreq.getIgst());
			cgst = cgst.add(cdnraeyreq.getCgst());
			sgst = sgst.add(cdnraeyreq.getSgst());
			cess = cess.add(cdnraeyreq.getCess());
			records = records + (cdnraeyreq.getRecords());

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
