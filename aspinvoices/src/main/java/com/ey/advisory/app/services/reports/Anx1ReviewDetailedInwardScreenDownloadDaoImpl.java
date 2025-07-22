/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Anx1RSScreenInwarddetailedDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.simplified.docsummary.Annexure1ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Anx1GetGstnDataSearchService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Anx1ReviewDetailedInwardScreenDownloadDaoImpl")
public class Anx1ReviewDetailedInwardScreenDownloadDaoImpl
		implements Anx1ReviewSummaryInwardScreenDetailedDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewDetailedInwardScreenDownloadDaoImpl.class);

	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;

	@Autowired
	@Qualifier("Annexure1ReqRespHandler")
	Annexure1ReqRespHandler annexure1ReqRespHandler;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Anx1RSScreenInwarddetailedDto> getReviewSummInvScreenDetailDownload(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

	

		JsonElement InwardSummaryRespBody = annexure1ReqRespHandler
				.handleInwardAnnexure1ReqAndResp(annexure1SummaryRequest);

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = (new JsonParser())
				.parse(InwardSummaryRespBody.toString()).getAsJsonObject();

		Type listType = new TypeToken<List<Annexure1SummaryResp1Dto>>() {
		}.getType();

		List<Annexure1SummaryResp1Dto> inwaradDtos = new ArrayList<>();
		JsonArray revJsonArray = requestObject.get("rev").getAsJsonArray();

		List<Annexure1SummaryResp1Dto> revDto = gson.fromJson(revJsonArray,
				listType);

		JsonArray impsJsonArray = requestObject.get("imps").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> impsDto = gson.fromJson(impsJsonArray,
				listType);
		JsonArray impgJsonArray = requestObject.get("impg").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> impgDto = gson.fromJson(impgJsonArray,
				listType);

		JsonArray impgsezJsonArray = requestObject.get("impgSez")
				.getAsJsonArray();
		List<Annexure1SummaryResp1Dto> impgsezDto = gson
				.fromJson(impgsezJsonArray, listType);

		inwaradDtos.addAll(revDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(impsDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(impgDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(impgsezDto);
		inwaradDtos.add(addEmptyObject());

		List<Anx1RSScreenInwarddetailedDto> inwardSummDetailDtoList = new ArrayList<>();
		for (Annexure1SummaryResp1Dto resdto : inwaradDtos) {

			Anx1RSScreenInwarddetailedDto res = new Anx1RSScreenInwarddetailedDto();
			Map<String, String> inwardSectionsMap = buildInwardSectionsMap();
			if (resdto.getDocType() != null) {
				if (resdto.getDocType().equals("total")) {
					res.setInwardtableType(
							inwardSectionsMap.get(resdto.getTableSection()));
				} else {
					res.setInwardtableType(
							inwardSectionsMap.get(resdto.getDocType()));
				}
			} else {
				res.setInwardtableType(resdto.getDocType());
			}
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

	private Annexure1SummaryResp1Dto addEmptyObject() {
		Annexure1SummaryResp1Dto dto = new Annexure1SummaryResp1Dto();
		dto.setTableSection(null);
		dto.setDocType(null);
		dto.setEyCount(null);
		dto.setEyInvoiceValue(null);
		dto.setEyTaxableValue(null);
		dto.setEyTaxPayble(null);
		dto.setEyCess(null);
		dto.setEyCgst(null);
		dto.setEySgst(null);
		dto.setEyIgst(null);
		dto.setMemoCess(null);
		dto.setMemoCgst(null);
		dto.setMemoCount(null);
		dto.setMemoIgst(null);
		dto.setMemoInvoiceValue(null);
		dto.setMemoSgst(null);
		dto.setMemoTaxableValue(null);
		dto.setMemoTaxPayble(null);
		dto.setGstnCess(null);
		dto.setGstnCgst(null);
		dto.setGstnCount(null);
		dto.setGstnIgst(null);
		dto.setGstnInvoiceValue(null);
		dto.setGstnSgst(null);
		dto.setGstnTaxableValue(null);
		dto.setGstnTaxPayble(null);
		dto.setDiffCess(null);
		dto.setDiffCgst(null);
		dto.setDiffCount(null);
		dto.setDiffIgst(null);
		dto.setDiffInvoiceValue(null);
		dto.setDiffSgst(null);
		dto.setDiffTaxableValue(null);
		dto.setDiffTaxPayble(null);
		return dto;
	}

	/**
	 * @return
	 */
	private Map<String, String> buildInwardSectionsMap() {
		Map<String, String> map = new HashMap<>();
		map.put("3H", "Reverse Charge (3H)");
		map.put("3I", "Import of Services (3I)");
		map.put("3J", "Import of Goods (3J)");
		map.put("3K", "Import of Goods from SEZ (3K)");
		map.put("INV", "- Invoice");
		map.put("SLF", "- Self Invoice");
		map.put("DR", "- Debit Note");
		map.put("CR", "- Credit Note");

		return map;
	}

}
