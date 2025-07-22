package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
/**
 * 
 * @author Balakrishna.S
 *
 */

import com.ey.advisory.app.docs.dto.Ret1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Ret1B2CStructure;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Component("Ret1ASumReqRespHandler")
@Slf4j
public class Ret1ASumReqRespHandler {

	@Autowired
	@Qualifier("Ret1ASimplDocSummaryService")
	Ret1ASimplDocSummaryService service;

	@Autowired
	@Qualifier("Ret1B2CStructure")
	Ret1B2CStructure ret1B2CRespHandler;

	public JsonElement handleRe1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		SearchResult<Ret1CompleteSummaryDto> summaryResult = service
				.<Ret1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Ret1CompleteSummaryDto.class);
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<? extends Ret1CompleteSummaryDto> list = summaryResult.getResult();

		Map<String, JsonElement> combinedMap = new HashMap<>();
		List<Ret1SummaryRespDto> b2caSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto an : list) {

			Ret1BasicSectionSummaryDto b2ca = an.getB2c_a();
			List<Ret1SummarySectionDto> b2caEySummary = b2ca.getEySummary();

			if (b2caEySummary != null) {
				b2caEySummary.forEach(b2cSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(b2cSummary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(b2cSummary.getSupplyType());
					summaryResp.setAspValue(b2cSummary.getTaxableValue());
					summaryResp.setAspIgst(b2cSummary.getAspIgst());
					summaryResp.setAspSgst(b2cSummary.getAspSgst());
					summaryResp.setAspCgst(b2cSummary.getAspCgst());
					summaryResp.setAspCess(b2cSummary.getAspCess());

					summaryResp.setUsrValue(b2cSummary.getUsrValue());
					summaryResp.setUsrSgst(b2cSummary.getUsrSgst());
					summaryResp.setUsrIgst(b2cSummary.getUsrIgst());
					summaryResp.setUsrCgst(b2cSummary.getUsrCgst());
					summaryResp.setUsrCess(b2cSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(b2cSummary.getGstnValue());
					summaryResp.setGstnIgst(b2cSummary.getGstnIgst());
					summaryResp.setGstnSgst(b2cSummary.getGstnSgst());
					summaryResp.setGstnCgst(b2cSummary.getGstnCgst());
					summaryResp.setGstnCess(b2cSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					b2caSummaryRespList.add(summaryResp);
				});
			}
		}

		List<Ret1SummaryRespDto> expwtaSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto expwta : list) {

			Ret1BasicSectionSummaryDto expwatList = expwta.getExpwt_a();
			List<Ret1SummarySectionDto> expwtaEySummary = expwatList
					.getEySummary();

			if (expwtaEySummary != null) {
				expwtaEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					expwtaSummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> expwotaSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto expwota : list) {

			Ret1BasicSectionSummaryDto expwotaList = expwota.getExpwot_a();
			List<Ret1SummarySectionDto> expwotEySummary = expwotaList
					.getEySummary();

			if (expwotEySummary != null) {
				expwotEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("3");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					expwotaSummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> priorperidaListaSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto expwota : list) {

			Ret1BasicSectionSummaryDto priorperidaList = expwota
					.getPriorPeriod_a();
			List<Ret1SummarySectionDto> priorperiodaEySummary = priorperidaList
					.getEySummary();

			if (priorperiodaEySummary != null) {
				priorperiodaEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("4");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					priorperidaListaSummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> outwardSupplyaRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto expwota : list) {

			Ret1BasicSectionSummaryDto outwardtotal = expwota.getA3Total();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = outwardtotal
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("3");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					outwardSupplyaRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> inwardSupplyaRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto inward : list) {

			Ret1BasicSectionSummaryDto inwardtotal = inward
					.getA_inwardsupplies();
			List<Ret1SummarySectionDto> inwardtotalEySummary = inwardtotal
					.getEySummary();

			if (inwardtotalEySummary != null) {
				inwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("3");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					inwardSupplyaRespList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> reverseChargeRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto reverse : list) {

			Ret1BasicSectionSummaryDto recersechargetotal = reverse
					.getInwatd_3h();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = recersechargetotal
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					reverseChargeRespList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> importsRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto expwota : list) {

			Ret1BasicSectionSummaryDto i_3itotal = expwota.getInwatd_3i();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = i_3itotal
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					importsRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> crdraList = new ArrayList<>();
		for (Ret1CompleteSummaryDto crdra : list) {

			Ret1BasicSectionSummaryDto crdr_3i = crdra.getInwatd_3i();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = crdr_3i
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("2");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					importsRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> othersList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getOthers_a();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					othersList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> libilityList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa
					.getA_suppliesNoLiability();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("1");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					libilityList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> nilList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getA_nil();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					nilList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> nonogstList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getA_nonGst();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					nonogstList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> setAoutward_supplies = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getA_outwardsupplies();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("3");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					setAoutward_supplies.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> nolibrary = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getA_sezTodta();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("4");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					nolibrary.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> nllibrary = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getNLlibility_a();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("4");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					nllibrary.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> itcnoList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getA_itcNOtherClaims();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("4");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					itcnoList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> sec3hList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getSec4_3h();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					sec3hList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> sec3iList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getSec4_3i();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					sec3iList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> sec3jlist = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getSec4_3j();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("3");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					sec3jlist.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> sec3klist = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getSec4_3k();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("4");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					sec3klist.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> sec4a5list = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getSec4_4a5();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("5");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					sec4a5list.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> reverselList = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getReverser_a();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("5");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					reverselList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> eilist = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getEi_a();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					eilist.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> itclist = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getItc_a();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					itclist.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> c4sitclist = new ArrayList<>();
		for (Ret1CompleteSummaryDto othersa : list) {

			Ret1BasicSectionSummaryDto others = othersa.getC_4C();
			List<Ret1SummarySectionDto> ouwardtotalEySummary = others
					.getEySummary();

			if (ouwardtotalEySummary != null) {
				ouwardtotalEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(expwtSummary.getTable());
					// summaryResp.setSection("2");
					summaryResp.setSupplyType(expwtSummary.getSupplyType());
					summaryResp.setAspValue(expwtSummary.getTaxableValue());
					summaryResp.setAspIgst(expwtSummary.getAspIgst());
					summaryResp.setAspSgst(expwtSummary.getAspSgst());
					summaryResp.setAspCgst(expwtSummary.getAspCgst());
					summaryResp.setAspCess(expwtSummary.getAspCess());

					summaryResp.setUsrValue(expwtSummary.getUsrValue());
					summaryResp.setUsrSgst(expwtSummary.getUsrSgst());
					summaryResp.setUsrIgst(expwtSummary.getUsrIgst());
					summaryResp.setUsrCgst(expwtSummary.getUsrCgst());
					summaryResp.setUsrCess(expwtSummary.getUsrCess());

					summaryResp.setGstnValue(expwtSummary.getGstnValue());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryResp.getAspValue(),
							summaryResp.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
							summaryResp.getGstnCess());

					summaryResp.setDiffValue(diffInv);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);

					c4sitclist.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> a3List = new ArrayList<>();
		List<Ret1SummaryRespDto> b3List = new ArrayList<>();
		List<Ret1SummaryRespDto> c3List = new ArrayList<>();
		List<Ret1SummaryRespDto> d3List = new ArrayList<>();
		List<Ret1SummaryRespDto> e3List = new ArrayList<>();
		List<Ret1SummaryRespDto> a4List = new ArrayList<>();
		List<Ret1SummaryRespDto> b4List = new ArrayList<>();
		List<Ret1SummaryRespDto> c4cList = new ArrayList<>();
		List<Ret1SummaryRespDto> a3 = ret1B2CRespHandler.ret1B2CResp(
				outwardSupplyaRespList, null, "A3", "a_outwardSupplies");

		List<Ret1SummaryRespDto> a31 = ret1B2CRespHandler
				.ret1B2CResp(b2caSummaryRespList, "1", "3A", "a_b2c");
		List<Ret1SummaryRespDto> a32 = ret1B2CRespHandler
				.ret1B2CResp(expwtaSummaryRespList, "2", "3C", "a_expwt");
		List<Ret1SummaryRespDto> a33 = ret1B2CRespHandler
				.ret1B2CResp(expwotaSummaryRespList, "3", "3D", "a_expwot");
		List<Ret1SummaryRespDto> a34 = ret1B2CRespHandler.ret1B2CResp(
				priorperidaListaSummaryRespList, "4", "3A4", "a_priorPeriod");
		a3List.addAll(a3);
		a3List.addAll(a31);
		a3List.addAll(a32);
		a3List.addAll(a33);
		a3List.addAll(a34);

		List<Ret1SummaryRespDto> b3 = ret1B2CRespHandler.ret1B2CResp(
				outwardSupplyaRespList, null, "B3", "a_inwardSupplies");

		List<Ret1SummaryRespDto> b31 = ret1B2CRespHandler
				.ret1B2CResp(reverseChargeRespList, "1", "3H", "a_rcInward");

		List<Ret1SummaryRespDto> b32 = ret1B2CRespHandler
				.ret1B2CResp(importsRespList, "2", "3I", "a_imps");
		b3List.addAll(b3);
		b3List.addAll(b31);
		b3List.addAll(b32);
		List<Ret1SummaryRespDto> c3 = ret1B2CRespHandler.ret1B2CResp(crdraList,
				null, "C3", "a_drCrNotes");
		List<Ret1SummaryRespDto> c31 = ret1B2CRespHandler
				.ret1B2CResp(othersList, "1", "3C1", "a_otherReductions");
		c3List.addAll(c3);
		c3List.addAll(c31);

		List<Ret1SummaryRespDto> d3 = ret1B2CRespHandler
				.ret1B2CResp(libilityList, null, "D3", "a_suppliesNoLiability");
		List<Ret1SummaryRespDto> d31 = ret1B2CRespHandler.ret1B2CResp(nilList,
				"1", "3D1", "a_exemptNil");

		List<Ret1SummaryRespDto> d32 = ret1B2CRespHandler
				.ret1B2CResp(nonogstList, "2", "3D2", "a_nonGst");
		List<Ret1SummaryRespDto> d33 = ret1B2CRespHandler
				.ret1B2CResp(setAoutward_supplies, "3", "3D3", "a_rcOutward");
		List<Ret1SummaryRespDto> d34 = ret1B2CRespHandler.ret1B2CResp(nolibrary,
				"4", "3D4", "a_sezDta");
		d3List.addAll(d3);
		d3List.addAll(d31);
		d3List.addAll(d32);
		d3List.addAll(d33);
		d3List.addAll(d34);

		List<Ret1SummaryRespDto> e3 = ret1B2CRespHandler.ret1B2CResp(nllibrary,
				null, "E3", "a_valueNLiability");
		e3List.addAll(e3);

		List<Ret1SummaryRespDto> a4 = ret1B2CRespHandler.ret1B2CResp(itcnoList,
				null, "A4", "a_itcNOtherClaims");
		List<Ret1SummaryRespDto> a41 = ret1B2CRespHandler.ret1B2CResp(sec3hList,
				"1", "3H", "a_rcInward");
		List<Ret1SummaryRespDto> a42 = ret1B2CRespHandler.ret1B2CResp(sec3iList,
				"2", "3I", "a_imps");
		List<Ret1SummaryRespDto> a43 = ret1B2CRespHandler.ret1B2CResp(sec3jlist,
				"3", "3J", "a_impg");
		List<Ret1SummaryRespDto> a44 = ret1B2CRespHandler.ret1B2CResp(sec3klist,
				"4", "3K", "a_impgSez");
		List<Ret1SummaryRespDto> a45 = ret1B2CRespHandler
				.ret1B2CResp(sec4a5list, "5", "4A5", "a_adjustments");

		a4List.addAll(a4);
		a4List.addAll(a41);
		a4List.addAll(a42);
		a4List.addAll(a43);
		a4List.addAll(a44);
		a4List.addAll(a45);

		List<Ret1SummaryRespDto> b4 = ret1B2CRespHandler
				.ret1B2CResp(reverselList, null, "B4", "a_creditReversal");

		List<Ret1SummaryRespDto> b41 = ret1B2CRespHandler.ret1B2CResp(eilist,
				"1", "4B1", "a_ineligibleCredit");

		List<Ret1SummaryRespDto> b42 = ret1B2CRespHandler.ret1B2CResp(itclist,
				"2", "4B2", "a_itcReversal");
		b4List.addAll(b4);
		b4List.addAll(b41);
		b4List.addAll(b42);

		List<Ret1SummaryRespDto> c4c = ret1B2CRespHandler
				.ret1B2CResp(c4sitclist, null, "C4", "a_itcAvailable");
		c4cList.addAll(c4c);

		JsonElement a3Respbody = gson.toJsonTree(a3List);
		JsonElement b3Respbody = gson.toJsonTree(b3List);
		JsonElement c3Respbody = gson.toJsonTree(c3List);
		JsonElement d3Respbody = gson.toJsonTree(d3List);
		JsonElement e3Respbody = gson.toJsonTree(e3List);
		JsonElement a4tRespBody = gson.toJsonTree(a4List);
		JsonElement b4tRespBody = gson.toJsonTree(b4List);
		JsonElement c4cRespbody = gson.toJsonTree(c4cList);

		combinedMap.put("A3", a3Respbody);

		combinedMap.put("B3", b3Respbody);

		combinedMap.put("C3", c3Respbody);

		combinedMap.put("D3", d3Respbody);

		combinedMap.put("E3", e3Respbody);

		combinedMap.put("A4", a4tRespBody);

		combinedMap.put("B4", b4tRespBody);
		combinedMap.put("C4", c4cRespbody);
		JsonElement ret13ASummaryRespbody = gson.toJsonTree(combinedMap);

		return ret13ASummaryRespbody;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

}
