/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1RSScreenOutwarddownloadDto;
import com.ey.advisory.app.data.views.client.Anx1RSScreendownloadDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.simplified.docsummary.Annexure1SummaryReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Anx1GetGstnDataSearchService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1ReviewSummOutwardScreenDownloadDaoImpl")
public class Anx1ReviewSummOutwardScreenDownloadDaoImpl
		implements Anx1ReviewSummaryScreenDownloadDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummOutwardScreenDownloadDaoImpl.class);

	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;

	@Autowired
	@Qualifier("Annexure1SummaryReqRespHandler")
	Annexure1SummaryReqRespHandler annexure1ReqRespHandler;

	@Override
	public List<Anx1RSScreenOutwarddownloadDto> getReviewSummScreenDownload(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		SearchResult<Annexure1SummaryDto> gstnSummary = gstnService
				.find(annexure1SummaryRequest, null, Annexure1SummaryDto.class);
		List<? extends Annexure1SummaryDto> gstnResult = gstnSummary
				.getResult();

		JsonElement outwardSummaryRespBody = annexure1ReqRespHandler
				.handleAnnexure1ReqAndResp(annexure1SummaryRequest, gstnResult);

		/*
		 * JsonObject requestObject = (new JsonParser())
		 * .parse(outwardSummaryRespBody.toString()).getAsJsonObject();
		 */
		Type listType = new TypeToken<List<Annexure1SummaryResp1Dto>>() {
		}.getType();

		JsonArray json = outwardSummaryRespBody.getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> outwardSummDetailDto = gson
				.fromJson(json, listType);
		List<Anx1RSScreenOutwarddownloadDto> outwardSummDetailDtoList = new ArrayList<>();

		for (Annexure1SummaryResp1Dto resdto : outwardSummDetailDto) {

			Anx1RSScreenOutwarddownloadDto res = new Anx1RSScreenOutwarddownloadDto();

			Map<String, String> outwardSectionsMap = buildOutwardSectionsMap();
			res.setOutwardtableType(outwardSectionsMap.get(resdto.getTableSection()));
			res.setOutwardaspCount(resdto.getEyCount());
			res.setOutwardaspInvoiceValue(resdto.getEyInvoiceValue());
			res.setOutwardaspTaxableValue(resdto.getEyTaxableValue());
			res.setOutwardaspSuppliesReturned(BigDecimal.ZERO);
			res.setOutwardaspNetSupplies(BigDecimal.ZERO);
			res.setOutwardaspTotalTax(resdto.getEyTaxPayble());
			res.setOutwardaspIGST(resdto.getEyIgst());
			res.setOutwardaspCGST(resdto.getEyCgst());
			res.setOutwardaspSGST(resdto.getEySgst());
			res.setOutwardaspCess(resdto.getEyCess());
			res.setOutwardmemoCount(resdto.getMemoCount());
			res.setOutwardmemoInvoiceValue(resdto.getMemoInvoiceValue());
			res.setOutwardmemoTaxableValue(resdto.getMemoTaxableValue());
			res.setOutwardmemoSuppliesReturned(BigDecimal.ZERO);
			res.setOutwardmemoNetSupplies(BigDecimal.ZERO);
			res.setOutwardmemoTotalTax(resdto.getMemoTaxPayble());
			res.setOutwardmemoIGST(resdto.getMemoIgst());
			res.setOutwardmemoCGST(resdto.getMemoCgst());
			res.setOutwardmemoSGST(resdto.getMemoSgst());
			res.setOutwardmemoCess(resdto.getMemoCess());
			res.setOutwardgstnCount(resdto.getGstnCount());
			res.setOutwardgstnInvoiceValue(resdto.getGstnInvoiceValue());
			res.setOutwardgstnTaxableValue(resdto.getGstnTaxableValue());
			res.setOutwardgstnSuppliesReturned(BigDecimal.ZERO);
			res.setOutwardgstnNetSupplies(BigDecimal.ZERO);
			res.setOutwardgstnTotalTax(resdto.getGstnTaxPayble());
			res.setOutwardgstnIGST(resdto.getGstnIgst());
			res.setOutwardgstnCGST(resdto.getGstnCgst());
			res.setOutwardgstnSGST(resdto.getGstnSgst());
			res.setOutwardgstnCess(resdto.getGstnCess());
			res.setOutwarddiffCount(resdto.getDiffCount());
			res.setOutwarddiffInvoiceValue(resdto.getDiffInvoiceValue());
			res.setOutwarddiffTaxableValue(resdto.getDiffTaxableValue());
			res.setOutwarddiffSuppliesReturned(BigDecimal.ZERO);
			res.setOutwarddiffNetSupplies(BigDecimal.ZERO);
			res.setOutwarddiffTotalTax(resdto.getDiffTaxPayble());
			res.setOutwarddiffIGST(resdto.getDiffIgst());
			res.setOutwarddiffCGST(resdto.getDiffCgst());
			res.setOutwarddiffSGST(resdto.getDiffSgst());
			res.setOutwarddiffCess(resdto.getDiffCess());

			outwardSummDetailDtoList.add(res);
		}
		return outwardSummDetailDtoList;
	}

	/**
	 * @return
	 */
	private Map<String, String> buildOutwardSectionsMap() {
		Map<String, String> map = new HashMap<>();
		map.put("3A", "B2C (3A)");
		map.put("3B", "B2B (3B)");
		map.put("3C", "Exports with Tax (3C)");
		map.put("3D", "Exports without Tax (3D)");
		map.put("3E", "SEZ with Tax (3E)");
		map.put("3F", "SEZ without Tax (3F)");
		map.put("3G", "Deemed Exports (3G)");
		return map;
	}

}
