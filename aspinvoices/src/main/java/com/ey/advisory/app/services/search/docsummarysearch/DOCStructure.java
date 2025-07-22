package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("DOCStructure")
public class DOCStructure {

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("DOCEYFinalStructure")
	private DOCEYFinalStructure docEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement docResp(
			List<Gstr1DocIssuedSummarySectionDto> eySummaryListFromView,
			List<Gstr1DocIssuedSummarySectionDto> gstnList) {
		
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1DocIssuedSummarySectionDto> defaultcdnurEYList = getDefaultDOCEYStructure();

		List<Gstr1DocIssuedSummarySectionDto> docEYList = docEYFinalStructure
				.getDocEyList(defaultcdnurEYList, eySummaryListFromView);
				

		
		
		Gstr1DocIssuedSummarySectionDto eyTotalCount = eyTotalCount(eySummaryListFromView);

		Gstr1DocIssuedSummarySectionDto gstn = getGstnStructure(gstnList);

		Gstr1DocIssuedSummarySectionDto diff = gstr1SummaryDifference
				.getDiffDOCStructure(eyTotalCount, gstn);

		// Response Body for Json

		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(docEYList);

		JsonElement gstnRespBody = gson.toJsonTree(gstn);

		JsonElement diffRespBody = gson.toJsonTree(diff);

		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);

		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);

		JsonElement docRespbody = gson.toJsonTree(combinedMap);

		/*
		 * JsonObject b2clResp = new JsonObject(); b2clResp.add("b2cl",
		 * b2clRespbody);
		 */

		return docRespbody;

	}

	private List<Gstr1DocIssuedSummarySectionDto> getDefaultDOCEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1DocIssuedSummarySectionDto> defaultDOCEY = new ArrayList<>();

		Gstr1DocIssuedSummarySectionDto docEy13 = new Gstr1DocIssuedSummarySectionDto();
		docEy13.setTableSection("13");
		docEy13 = defaultStructureUtil.defaultDOCStructure(docEy13);

		defaultDOCEY.add(docEy13);

		return defaultDOCEY;

	}

	private Gstr1DocIssuedSummarySectionDto getGstnStructure(
			List<Gstr1DocIssuedSummarySectionDto> gstnList) {
		Gstr1DocIssuedSummarySectionDto gstnResp = new Gstr1DocIssuedSummarySectionDto();
	//	gstnResp.setTableSection("-");
		for (Gstr1DocIssuedSummarySectionDto gstn : gstnList) {
			gstnResp.setRecords(gstn.getRecords());
			gstnResp.setTotalIssued(gstn.getTotalIssued());
			gstnResp.setNetIssued(gstn.getNetIssued());
			gstnResp.setCancelled(gstn.getCancelled());
		}
		return gstnResp;
	}

	private Gstr1DocIssuedSummarySectionDto eyTotalCount(
			List<Gstr1DocIssuedSummarySectionDto> docEYList) {

		Integer records = new Integer(0);
		Integer totalIssued = new Integer(0);
		Integer netIssued = new Integer(0);
		Integer cancelled = new Integer(0);
		
		Gstr1DocIssuedSummarySectionDto eyTotal = new Gstr1DocIssuedSummarySectionDto();

		// List<Gstr1SummaryRespDto> b2baey = cdnrEYList;
		for (Gstr1DocIssuedSummarySectionDto doceyreq : docEYList) {
			records = records + doceyreq.getRecords();
			totalIssued = totalIssued + doceyreq.getTotalIssued();
			netIssued = netIssued + doceyreq.getNetIssued();
			cancelled = cancelled + doceyreq.getCancelled();
			
		}

		eyTotal.setRecords(records);
		eyTotal.setTotalIssued(totalIssued);
		eyTotal.setNetIssued(netIssued);
		eyTotal.setCancelled(cancelled);
		return eyTotal;

	}

}
