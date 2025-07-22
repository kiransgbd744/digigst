package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("NILStructure")
public class NILStructure {
	

	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("NILEYFinalStructure")
	private NILEYFinalStructure nilEYFinalStructure;

	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;

	public JsonElement nilResp(
			List<Gstr1NilRatedSummarySectionDto> eySummaryListFromView,
			List<Gstr1NilRatedSummarySectionDto> gstnList) {
		
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1NilRatedSummarySectionDto> defaultNilEYList = getDefaultNILEYStructure();

		List<Gstr1NilRatedSummarySectionDto> nilEYList = nilEYFinalStructure
				.getNilEyList(defaultNilEYList, eySummaryListFromView);
					
		Gstr1NilRatedSummarySectionDto eyTotalCount = eyTotalCount(eySummaryListFromView);

		Gstr1NilRatedSummarySectionDto gstn = getGstnStructure(gstnList);

		Gstr1NilRatedSummarySectionDto diff = gstr1SummaryDifference
				.getDiffNILStructure(eyTotalCount, gstn);

		// Response Body for Json

		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);

		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(nilEYList);

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

	private List<Gstr1NilRatedSummarySectionDto> getDefaultNILEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1NilRatedSummarySectionDto> defaultNILEY = new ArrayList<>();

		Gstr1NilRatedSummarySectionDto nilEy8A = new Gstr1NilRatedSummarySectionDto();
		nilEy8A.setTableSection("8A");
		nilEy8A = defaultStructureUtil.defaultNILStructure(nilEy8A);
		
		Gstr1NilRatedSummarySectionDto nilEy8B = new Gstr1NilRatedSummarySectionDto();
		nilEy8B.setTableSection("8B");
		nilEy8B = defaultStructureUtil.defaultNILStructure(nilEy8B);
		
		Gstr1NilRatedSummarySectionDto nilEy8C = new Gstr1NilRatedSummarySectionDto();
		nilEy8C.setTableSection("8C");
		nilEy8C = defaultStructureUtil.defaultNILStructure(nilEy8C);

		Gstr1NilRatedSummarySectionDto nilEy8D = new Gstr1NilRatedSummarySectionDto();
		nilEy8D.setTableSection("8D");
		nilEy8D = defaultStructureUtil.defaultNILStructure(nilEy8D);

		defaultNILEY.add(nilEy8A);
		defaultNILEY.add(nilEy8B);
		defaultNILEY.add(nilEy8C);
		defaultNILEY.add(nilEy8D);
		
		return defaultNILEY;

	}

	private Gstr1NilRatedSummarySectionDto getGstnStructure(
			List<Gstr1NilRatedSummarySectionDto> gstnList) {
		Gstr1NilRatedSummarySectionDto gstnResp = new Gstr1NilRatedSummarySectionDto();
	//	gstnResp.setTableSection("-");
		for (Gstr1NilRatedSummarySectionDto gstn : gstnList) {
			gstnResp.setRecordCount(gstn.getRecordCount());
			gstnResp.setTotalExempted(gstn.getTotalExempted());
			gstnResp.setTotalNilRated(gstn.getTotalNilRated());
			gstnResp.setTotalNonGST(gstn.getTotalNonGST());
			
		}
		return gstnResp;
	}

	private Gstr1NilRatedSummarySectionDto eyTotalCount(
			List<Gstr1NilRatedSummarySectionDto> nilEYList) {

		Integer recordCount=new Integer(0);
		BigDecimal totalExempted = new BigDecimal(0.0);
		BigDecimal totalNilRated =new BigDecimal(0.0);
		BigDecimal totalNonGST = new BigDecimal(0.0);
		
		
		Gstr1NilRatedSummarySectionDto eyTotal = new Gstr1NilRatedSummarySectionDto();

		// List<Gstr1SummaryRespDto> b2baey = cdnrEYList;
		for (Gstr1NilRatedSummarySectionDto nileyreq : nilEYList) {
			recordCount = recordCount + nileyreq.getRecordCount();
			totalExempted = totalExempted.add(nileyreq.getTotalExempted());
			totalNilRated = totalNilRated.add(nileyreq.getTotalNilRated());
			totalNonGST = totalNonGST.add(nileyreq.getTotalNonGST());
			
		}

		eyTotal.setRecordCount(recordCount);
		eyTotal.setTotalExempted(totalExempted);
		eyTotal.setTotalNilRated(totalNilRated);
		eyTotal.setTotalNonGST(totalNonGST);
		return eyTotal;

	}


}
