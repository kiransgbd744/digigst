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

@Service("B2BStructure")
public class B2BStructure {

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("B2BEYFinalStructure")
	private B2BEYFinalStructure b2bEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement b2bResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultB2bEYList = getDefaultB2BEYStructure();

		List<Gstr1SummaryRespDto> b2bEYList = b2bEYFinalStructure
				.getB2BEyList(defaultB2bEYList, eySummaryListFromView);

		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(b2bEYList);

		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);

		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffStructure(eyTotalCount, gstn);

		// Response Body for Json

		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);

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

	private List<Gstr1SummaryRespDto> getDefaultB2BEYStructure() {

		List<Gstr1SummaryRespDto> defaultB2BEY = new ArrayList<>();

		Gstr1SummaryRespDto b2bEy4A = new Gstr1SummaryRespDto();
		b2bEy4A.setTableSection("4A");
		b2bEy4A = defaultStructureUtil.defaultStructure(b2bEy4A);

		Gstr1SummaryRespDto b2bEy4B = new Gstr1SummaryRespDto();
		b2bEy4B.setTableSection("4B");
		b2bEy4B = defaultStructureUtil.defaultStructure(b2bEy4B);

		Gstr1SummaryRespDto b2bEy4C = new Gstr1SummaryRespDto();
		b2bEy4C.setTableSection("4C");
		b2bEy4C = defaultStructureUtil.defaultStructure(b2bEy4C);

		Gstr1SummaryRespDto b2bEy6B = new Gstr1SummaryRespDto();
		b2bEy6B.setTableSection("6B");
		b2bEy6B = defaultStructureUtil.defaultStructure(b2bEy6B);

		Gstr1SummaryRespDto b2bEy6C = new Gstr1SummaryRespDto();
		b2bEy6C.setTableSection("6C");
		b2bEy6C = defaultStructureUtil.defaultStructure(b2bEy6C);

		defaultB2BEY.add(b2bEy4A);
		defaultB2BEY.add(b2bEy4B);
		defaultB2BEY.add(b2bEy4C);
		defaultB2BEY.add(b2bEy6B);
		defaultB2BEY.add(b2bEy6C);

		return defaultB2BEY;
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
			List<Gstr1SummaryRespDto> b2bEYList) {

		BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
		BigDecimal cgst = new BigDecimal(0);
		BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
		Integer records = new Integer(0);
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		List<Gstr1SummaryRespDto> b2bey = b2bEYList;
		for (Gstr1SummaryRespDto b2beyreq : b2bey) {
			invValue = invValue.add(b2beyreq.getInvValue());
			taxable = taxable.add(b2beyreq.getTaxableValue());
			taxpayble = taxpayble.add(b2beyreq.getTaxPayble());
			igst = igst.add(b2beyreq.getIgst());
			cgst = cgst.add(b2beyreq.getCgst());
			sgst = sgst.add(b2beyreq.getSgst());
			cess = cess.add(b2beyreq.getCess());
			records = records + (b2beyreq.getRecords());

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
