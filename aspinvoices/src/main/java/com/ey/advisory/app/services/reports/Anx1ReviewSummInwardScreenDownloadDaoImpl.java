/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1RSScreenInwarddownloadDto;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1ReviewSummInwardScreenDownloadDaoImpl")
public class Anx1ReviewSummInwardScreenDownloadDaoImpl
		implements Anx1ReviewSummaryInwardScreenDownloadDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummInwardScreenDownloadDaoImpl.class);

	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;

	@Autowired
	@Qualifier("Annexure1SummaryReqRespHandler")
	Annexure1SummaryReqRespHandler annexure1ReqRespHandler;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx1RSScreenInwarddownloadDto> getReviewSummInvScreenDownload(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		SearchResult<Annexure1SummaryDto> gstnSummary = gstnService
				.find(annexure1SummaryRequest, null, Annexure1SummaryDto.class);
		List<? extends Annexure1SummaryDto> gstnResult = gstnSummary
				.getResult();

		JsonElement InwardSummaryRespBody = annexure1ReqRespHandler
				.handleInwardAnnexure1ReqAndResp(annexure1SummaryRequest,
						gstnResult);

		Type listType = new TypeToken<List<Annexure1SummaryResp1Dto>>() {
		}.getType();

		JsonArray json = InwardSummaryRespBody.getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> inwardSummDetailDto = gson.fromJson(json,
				listType);
		List<Anx1RSScreenInwarddownloadDto> inwardSummDetailDtoList = new ArrayList<>();
		for (Annexure1SummaryResp1Dto resdto : inwardSummDetailDto) {

			Anx1RSScreenInwarddownloadDto res = new Anx1RSScreenInwarddownloadDto();
			Map<String, String> inwardSectionMap = buildInwardSectionsMap();
			res.setInwardtableType(inwardSectionMap.get(resdto.getTableSection()));
			res.setInwardaspCount(resdto.getEyCount());
			res.setInwardaspInvoiceValue(resdto.getEyInvoiceValue());
			res.setInwardaspTaxableValue(resdto.getEyTaxableValue());
			res.setInwardaspTotalTax(resdto.getEyTaxPayble());
			res.setInwardaspIGST(resdto.getEyIgst());
			res.setInwardaspCGST(resdto.getEyCgst());
			res.setInwardaspSGST(resdto.getEySgst());
			res.setInwardaspCess(resdto.getEyCess());
			res.setInwardmemoCount(resdto.getMemoCount());
			res.setInwardmemoInvoiceValue(resdto.getMemoInvoiceValue());
			res.setInwardmemoTaxableValue(resdto.getMemoTaxableValue());
			res.setInwardmemoTotalTax(resdto.getMemoTaxPayble());
			res.setInwardmemoIGST(resdto.getMemoIgst());
			res.setInwardmemoCGST(resdto.getMemoCgst());
			res.setInwardmemoSGST(resdto.getMemoSgst());
			res.setInwardmemoCess(resdto.getMemoCess());
			res.setInwardgstnCount(resdto.getGstnCount());
			res.setInwardgstnInvoiceValue(resdto.getGstnInvoiceValue());
			res.setInwardgstnTaxableValue(resdto.getGstnTaxableValue());
			res.setInwardgstnTotalTax(resdto.getGstnTaxPayble());
			res.setInwardgstnIGST(resdto.getGstnIgst());
			res.setInwardgstnCGST(resdto.getGstnCgst());
			res.setInwardgstnSGST(resdto.getGstnSgst());
			res.setInwardgstnCess(resdto.getGstnCess());
			res.setInwarddiffCount(resdto.getDiffCount());
			res.setInwarddiffInvoiceValue(resdto.getDiffInvoiceValue());
			res.setInwarddiffTaxableValue(resdto.getDiffTaxableValue());
			res.setInwarddiffTotalTax(resdto.getDiffTaxPayble());
			res.setInwarddiffIGST(resdto.getDiffIgst());
			res.setInwarddiffCGST(resdto.getDiffCgst());
			res.setInwarddiffSGST(resdto.getDiffSgst());
			res.setInwarddiffCess(resdto.getDiffCess());

			inwardSummDetailDtoList.add(res);
		}
		return inwardSummDetailDtoList;
	}

	private Map<String, String> buildInwardSectionsMap() {
		Map<String, String> map = new HashMap<>();
		map.put("3H", "Reverse Charge (3H)");
		map.put("3I", "Import of Services (3I)");
		map.put("3J", "Import of Goods(3J)");
		map.put("3K", "Import of Goods from SEZ(3K)");
		return map;
	}
	
	

}
