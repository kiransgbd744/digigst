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

import com.ey.advisory.app.data.views.client.Anx1RSScreenDetailOutwarddownloadDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.simplified.docsummary.Annexure1ReqRespHandler;
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
 * @author Sujith.Nanga
 *
 * 
 */
@Component("Anx1ReviewDetailedOutwardScreenDownloadDaoImpl")
public class Anx1ReviewDetailedOutwardScreenDownloadDaoImpl
		implements Anx1ReviewSummaryScreenDetailedDownloadDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ReviewDetailedOutwardScreenDownloadDaoImpl.class);

	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;

	@Autowired
	@Qualifier("Annexure1ReqRespHandler")
	Annexure1ReqRespHandler annexure1ReqRespHandler;

	@Override
	public List<Anx1RSScreenDetailOutwarddownloadDto> getReviewDetailScreenDownload(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		
		JsonElement outwardSummaryRespBody = annexure1ReqRespHandler
				.handleAnnexure1ReqAndResp(annexure1SummaryRequest);
		// outwardSummaryRespBody.get("b2c")
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject requestObject = (new JsonParser())
				.parse(outwardSummaryRespBody.toString()).getAsJsonObject();

		Type listType = new TypeToken<List<Annexure1SummaryResp1Dto>>() {
		}.getType();

		List<Annexure1SummaryResp1Dto> inwaradDtos = new ArrayList<>();
		JsonArray asJsonArray = requestObject.get("b2c").getAsJsonArray();

		List<Annexure1SummaryResp1Dto> b2cDto = gson.fromJson(asJsonArray,
				listType);

		JsonArray b2bJsonArray = requestObject.get("b2b").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> b2bDto = gson.fromJson(b2bJsonArray,
				listType);
		JsonArray cJsonArray = requestObject.get("expt").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> cDto = gson.fromJson(cJsonArray,
				listType);
		JsonArray dJsonArray = requestObject.get("expwt").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> dDto = gson.fromJson(dJsonArray,
				listType);

		JsonArray eJsonArray = requestObject.get("sezt").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> eDto = gson.fromJson(eJsonArray,
				listType);

		JsonArray fJsonArray = requestObject.get("sezwt").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> fDto = gson.fromJson(fJsonArray,
				listType);

		JsonArray gJsonArray = requestObject.get("deemExpt").getAsJsonArray();
		List<Annexure1SummaryResp1Dto> gDto = gson.fromJson(gJsonArray,
				listType);

		inwaradDtos.addAll(b2cDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(b2bDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(cDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(dDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(eDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(fDto);
		inwaradDtos.add(addEmptyObject());
		inwaradDtos.addAll(gDto);
		inwaradDtos.add(addEmptyObject());

		List<Anx1RSScreenDetailOutwarddownloadDto> outwardSummDetailDtoList = new ArrayList<>();

		for (Annexure1SummaryResp1Dto resdto : inwaradDtos) {

			Anx1RSScreenDetailOutwarddownloadDto res = new Anx1RSScreenDetailOutwarddownloadDto();

			Map<String, String> outwardSectionsMap = buildOutwardSectionsMap();
			if (resdto.getDocType() != null) {
				if (resdto.getDocType().equals("total")) {
					res.setOutwardtableType(
							outwardSectionsMap.get(resdto.getTableSection()));
				} else {
					res.setOutwardtableType(
							outwardSectionsMap.get(resdto.getDocType()));
				}
			} else {
				res.setOutwardtableType(resdto.getDocType());
			}

			res.setOutwardaspCount(resdto.getEyCount());
			res.setOutwardaspInvoiceValue(resdto.getEyInvoiceValue());
			res.setOutwardaspTaxableValue(resdto.getEyTaxableValue());
			res.setOutwardaspSuppliesReturned(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
			res.setOutwardaspNetSupplies(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
			res.setOutwardaspTotalTax(resdto.getEyTaxPayble());
			res.setOutwardaspIGST(resdto.getEyIgst());
			res.setOutwardaspCGST(resdto.getEyCgst());
			res.setOutwardaspSGST(resdto.getEySgst());
			res.setOutwardaspCess(resdto.getEyCess());
			res.setOutwardmemoCount(resdto.getMemoCount());
			res.setOutwardmemoInvoiceValue(resdto.getMemoInvoiceValue());
			res.setOutwardmemoTaxableValue(resdto.getMemoTaxableValue());
			res.setOutwardmemoSuppliesReturned(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
			res.setOutwardmemoNetSupplies(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
			res.setOutwardmemoTotalTax(resdto.getMemoTaxPayble());
			res.setOutwardmemoIGST(resdto.getMemoIgst());
			res.setOutwardmemoCGST(resdto.getMemoCgst());
			res.setOutwardmemoSGST(resdto.getMemoSgst());
			res.setOutwardmemoCess(resdto.getMemoCess());
			res.setOutwardgstnCount(resdto.getGstnCount());
			res.setOutwardgstnInvoiceValue(resdto.getGstnInvoiceValue());
			res.setOutwardgstnTaxableValue(resdto.getGstnTaxableValue());
			res.setOutwardgstnSuppliesReturned(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
			res.setOutwardgstnNetSupplies(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
			res.setOutwardgstnTotalTax(resdto.getGstnTaxPayble());
			res.setOutwardgstnIGST(resdto.getGstnIgst());
			res.setOutwardgstnCGST(resdto.getGstnCgst());
			res.setOutwardgstnSGST(resdto.getGstnSgst());
			res.setOutwardgstnCess(resdto.getGstnCess());
			res.setOutwarddiffCount(resdto.getDiffCount());
			res.setOutwarddiffInvoiceValue(resdto.getDiffInvoiceValue());
			res.setOutwarddiffTaxableValue(resdto.getDiffTaxableValue());
			res.setOutwarddiffSuppliesReturned(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
			res.setOutwarddiffNetSupplies(
					resdto.getDocType() == null ? null : BigDecimal.ZERO);
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
	private Map<String, String> buildOutwardSectionsMap() {
		Map<String, String> map = new HashMap<>();
		map.put("3A", "B2C (3A)");
		map.put("3B", "B2B (3B)");
		map.put("INV", "- Invoice");
		map.put("DR", "- Debit Note");
		map.put("CR", "- Credit Note");
		map.put("RNV", "- Invoice-Amendments");
		map.put("RDR", "- Debit Note-Amendments");
		map.put("RCR", "- Credit Note-Amendments");
		map.put("3C", "Exports with Tax (3C)");
		map.put("3D", "Exports without Tax (3D)");
		map.put("3E", "SEZ with Tax (3E)");
		map.put("3F", "SEZ without Tax (3F)");
		map.put("3G", "Deemed Exports (3G)");

		return map;
	}

}
