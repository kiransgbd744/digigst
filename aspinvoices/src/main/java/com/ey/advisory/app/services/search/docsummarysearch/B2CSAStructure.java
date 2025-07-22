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

@Service("B2CSAStructure")
public class B2CSAStructure {



	@Autowired
	@Qualifier("DefaultStructureUtil")
	private DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("B2CSAEYFinalStructure")
	private B2CSAEYFinalStructure b2csaEYFinalStructure;
	
	@Autowired
	@Qualifier("Gstr1SummaryDifference")
	private Gstr1SummaryDifference gstr1SummaryDifference;


	public JsonElement b2csaResp(
			List<Gstr1BasicSummarySectionDto> eySummaryListFromView,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr1SummaryRespDto> defaultb2csaEYList = getDefaultB2CSAEYStructure();

		List<Gstr1SummaryRespDto> b2csaEYList = b2csaEYFinalStructure
				.getb2csaEyList(defaultb2csaEYList, eySummaryListFromView);
		
		Gstr1SummaryRespDto eyTotalCount = eyTotalCount(b2csaEYList);
		
		Gstr1SummaryRespDto gstn = getGstnStructure(gstnList);
		
		Gstr1SummaryRespDto diff = gstr1SummaryDifference
				.getDiffB2CSStructure(eyTotalCount, gstn);

		//Response Body for Json
		
		JsonElement eyTotalCountRespBody = gson.toJsonTree(eyTotalCount);
		
		JsonElement eyTypeDetailsRespBody = gson.toJsonTree(b2csaEYList);
		
		JsonElement gstnRespBody = gson.toJsonTree(gstn);
		
		JsonElement diffRespBody = gson.toJsonTree(diff);
		
		
		JsonElement eyRespBody = gson.toJsonTree(eyTypeDetailsRespBody);
		
		Map<String,JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("ey", eyRespBody);
		combinedMap.put("gstn", gstnRespBody);
		combinedMap.put("diff", diffRespBody);
		
		JsonElement b2csaRespbody = gson.toJsonTree(combinedMap);
		
		/*JsonObject b2clResp = new JsonObject();
		b2clResp.add("b2cl", b2clRespbody);*/
		
		return b2csaRespbody;

	}

	private List<Gstr1SummaryRespDto> getDefaultB2CSAEYStructure() {
		// TODO Auto-generated method stub
		List<Gstr1SummaryRespDto> defaultB2CSAEY = new ArrayList<>();

		Gstr1SummaryRespDto b2csaEy10A = new Gstr1SummaryRespDto();
		b2csaEy10A.setTableSection("10A");
		b2csaEy10A = defaultStructureUtil.defaultB2CSStructure(b2csaEy10A);
		
		Gstr1SummaryRespDto b2csaEy10B = new Gstr1SummaryRespDto();
		b2csaEy10B.setTableSection("10B");
		b2csaEy10B = defaultStructureUtil.defaultB2CSStructure(b2csaEy10B);

		Gstr1SummaryRespDto b2csaEy10A1 = new Gstr1SummaryRespDto();
		b2csaEy10A1.setTableSection("10A(1)");
		b2csaEy10A1 = defaultStructureUtil.defaultB2CSStructure(b2csaEy10A1);
		
		Gstr1SummaryRespDto b2csaEy10B1 = new Gstr1SummaryRespDto();
		b2csaEy10B1.setTableSection("10B(1)");
		b2csaEy10B1 = defaultStructureUtil.defaultB2CSStructure(b2csaEy10B1);


		defaultB2CSAEY.add(b2csaEy10A);
		defaultB2CSAEY.add(b2csaEy10B);
		defaultB2CSAEY.add(b2csaEy10A1);
		defaultB2CSAEY.add(b2csaEy10B1);
		
		return defaultB2CSAEY;

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
			List<Gstr1SummaryRespDto> b2csaEYList) {

	//	BigDecimal invValue = new BigDecimal(0);
		BigDecimal taxable = new BigDecimal(0);
		BigDecimal taxpayble = new BigDecimal(0);
		BigDecimal igst = new BigDecimal(0);
		BigDecimal cgst = new BigDecimal(0);
		BigDecimal sgst = new BigDecimal(0);
		BigDecimal cess = new BigDecimal(0);
	//	Integer records = new Integer(0);
		Gstr1SummaryRespDto eyTotal = new Gstr1SummaryRespDto();

		List<Gstr1SummaryRespDto> b2csaey = b2csaEYList;
		for (Gstr1SummaryRespDto b2csaeyreq : b2csaey) {
		//	invValue = invValue.add(b2csaeyreq.getInvValue());
			taxable = taxable.add(b2csaeyreq.getTaxableValue());
			taxpayble = taxpayble.add(b2csaeyreq.getTaxPayble());
			igst = igst.add(b2csaeyreq.getIgst());
			cgst = cgst.add(b2csaeyreq.getCgst());
			sgst = sgst.add(b2csaeyreq.getSgst());
			cess = cess.add(b2csaeyreq.getCess());
		//	records = records + (b2csaeyreq.getRecords());

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
