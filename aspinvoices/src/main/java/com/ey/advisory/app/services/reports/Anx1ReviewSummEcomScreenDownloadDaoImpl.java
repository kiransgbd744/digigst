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

import com.ey.advisory.app.data.views.client.Anx1RSScreenEcomdownloadDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryEcomResp1Dto;
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
@Component("Anx1ReviewSummEcomScreenDownloadDaoImpl")
public class Anx1ReviewSummEcomScreenDownloadDaoImpl
		implements Anx1ReviewSummaryScreenEcomDownloadDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewSummEcomScreenDownloadDaoImpl.class);

	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;

	@Autowired
	@Qualifier("Annexure1SummaryReqRespHandler")
	Annexure1SummaryReqRespHandler annexure1ReqRespHandler;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx1RSScreenEcomdownloadDto> getReviewSummScreenEcomDownload(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		SearchResult<Annexure1SummaryDto> gstnSummary = gstnService
				.find(annexure1SummaryRequest, null, Annexure1SummaryDto.class);
		List<? extends Annexure1SummaryDto> gstnResult = gstnSummary
				.getResult();

		JsonElement ecomSummaryRespBody = annexure1ReqRespHandler
				.handleEcommAnnexure1ReqAndResp(annexure1SummaryRequest,
						gstnResult);

		Type listType = new TypeToken<List<Annexure1SummaryEcomResp1Dto>>() {
		}.getType();

		JsonArray json = ecomSummaryRespBody.getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryEcomResp1Dto> ecomSummDetailDto = gson
				.fromJson(json, listType);
		List<Anx1RSScreenEcomdownloadDto> ecomSummDetailDtoList = new ArrayList<>();
		for (Annexure1SummaryEcomResp1Dto resdto : ecomSummDetailDto) {

			Anx1RSScreenEcomdownloadDto res = new Anx1RSScreenEcomdownloadDto();

			Map<String, String> ecomSectionsMap = buildEcomSectionsMap();

			res.setEcomtableType("Supplies through Ecom");
			res.setEcomaspNetSupplies(resdto.getEyNetSupply());
			res.setEcomaspTotalTax(resdto.getEyTaxPayble());
			res.setEcomaspIGST(resdto.getEyIgst());
			res.setEcomaspCGST(resdto.getEyCgst());
			res.setEcomaspSGST(resdto.getEySgst());
			res.setEcomaspCess(resdto.getEyCess());
			res.setEcommemoNetSupplies(resdto.getMemoNetSupply());
			res.setEcommemoTotalTax(resdto.getMemoTaxPayble());
			res.setEcommemoIGST(resdto.getMemoIgst());
			res.setEcommemoCGST(resdto.getMemoCgst());
			res.setEcommemoSGST(resdto.getMemoSgst());
			res.setEcommemoCess(resdto.getMemoCess());
			res.setEcomdiffNetSupplies(resdto.getDiffNetSupply());
			res.setEcomdiffTotalTax(resdto.getDiffTaxPayble());
			res.setEcomdiffIGST(resdto.getDiffIgst());
			res.setEcomdiffCGST(resdto.getDiffCgst());
			res.setEcomdiffSGST(resdto.getDiffSgst());
			res.setEcomdiffCess(resdto.getDiffCess());

			ecomSummDetailDtoList.add(res);
		}
		return ecomSummDetailDtoList;
	}

	private Map<String, String> buildEcomSectionsMap() {
		Map<String, String> map = new HashMap<>();
		map.put("Table4", "Supplies through Ecom(Table4)");
		return map;
	}

}
