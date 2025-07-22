package com.ey.advisory.app.services.search.simplified.docsummary;

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

@Component("Ret1SumReqRespHandler")
public class Ret1SumReqRespHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1SumReqRespHandler.class);

	@Autowired
	@Qualifier("Ret1SimplDocSummaryService")
	Ret1SimplDocSummaryService service;
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

		List<Ret1SummaryRespDto> a3TotalSummaryRespList = new ArrayList<>();

		List<Ret1SummaryRespDto> a3SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto an : list) {

			Ret1BasicSectionSummaryDto a3Total = an.getA3_tot();
			List<Ret1SummarySectionDto> a3TotEySummary = a3Total.getEySummary();

			if (a3TotEySummary != null) {
				a3TotEySummary.forEach(a3Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(a3Summary.getTable());
					summaryResp.setSupplyType(a3Summary.getSupplyType());
					summaryResp.setAspValue(a3Summary.getTaxableValue());
					summaryResp.setAspIgst(a3Summary.getAspIgst());
					summaryResp.setAspSgst(a3Summary.getAspSgst());
					summaryResp.setAspCgst(a3Summary.getAspCgst());
					summaryResp.setAspCess(a3Summary.getAspCess());
					summaryResp.setUsrValue(a3Summary.getUsrValue());
					summaryResp.setUsrSgst(a3Summary.getUsrSgst());
					summaryResp.setUsrIgst(a3Summary.getUsrIgst());
					summaryResp.setUsrCgst(a3Summary.getUsrCgst());
					summaryResp.setUsrCess(a3Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(a3Summary.getGstnValue());
					summaryResp.setGstnIgst(a3Summary.getGstnIgst());
					summaryResp.setGstnSgst(a3Summary.getGstnSgst());
					summaryResp.setGstnCgst(a3Summary.getGstnCgst());
					summaryResp.setGstnCess(a3Summary.getGstnCess());
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

					a3SummaryRespList.add(summaryResp);
				});
			}
		}

		List<Ret1SummaryRespDto> b2cSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto an : list) {

			Ret1BasicSectionSummaryDto b2c = an.getB2c();
			List<Ret1SummarySectionDto> b2cEySummary = b2c.getEySummary();

			if (b2cEySummary != null) {
				b2cEySummary.forEach(b2cSummary -> {
					Ret1SummaryRespDto summaryRespa1 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa1.setTable(b2cSummary.getTable());
					summaryRespa1.setSection("1");
					summaryRespa1.setSupplyType(b2cSummary.getSupplyType());
					summaryRespa1.setAspValue(b2cSummary.getTaxableValue());
					summaryRespa1.setAspIgst(b2cSummary.getAspIgst());
					summaryRespa1.setAspSgst(b2cSummary.getAspSgst());
					summaryRespa1.setAspCgst(b2cSummary.getAspCgst());
					summaryRespa1.setAspCess(b2cSummary.getAspCess());
					
					summaryRespa1.setUsrValue(b2cSummary.getUsrValue());
					summaryRespa1.setUsrSgst(b2cSummary.getUsrSgst());
					summaryRespa1.setUsrIgst(b2cSummary.getUsrIgst());
					summaryRespa1.setUsrCgst(b2cSummary.getUsrCgst());
					summaryRespa1.setUsrCess(b2cSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa1.setGstnValue(b2cSummary.getGstnValue());
					summaryRespa1.setGstnIgst(b2cSummary.getGstnIgst());
					summaryRespa1.setGstnSgst(b2cSummary.getGstnSgst());
					summaryRespa1.setGstnCgst(b2cSummary.getGstnCgst());
					summaryRespa1.setGstnCess(b2cSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa1.getAspValue(),
							summaryRespa1.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa1.getAspSgst(),
							summaryRespa1.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa1.getAspIgst(),
							summaryRespa1.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa1.getAspCgst(),
							summaryRespa1.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa1.getAspCess(),
							summaryRespa1.getGstnCess());

					summaryRespa1.setDiffValue(diffInv);
					summaryRespa1.setDiffIgst(diffIgst);
					summaryRespa1.setDiffCgst(diffCgst);
					summaryRespa1.setDiffSgst(diffSgst);
					summaryRespa1.setDiffCess(diffCess);

					b2cSummaryRespList.add(summaryRespa1);
				});
			}
		}

		List<Ret1SummaryRespDto> b2bSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto b2b : list) {

			Ret1BasicSectionSummaryDto b2bList = b2b.getB2b();
			List<Ret1SummarySectionDto> b2bEySummary = b2bList.getEySummary();

			if (b2bEySummary != null) {
				b2bEySummary.forEach(b2bSummary -> {
					Ret1SummaryRespDto summaryRespa2 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa2.setTable(b2bSummary.getTable());
					summaryRespa2.setSection("2");
					summaryRespa2.setSupplyType(b2bSummary.getSupplyType());
					summaryRespa2.setAspValue(b2bSummary.getTaxableValue());
					summaryRespa2.setAspIgst(b2bSummary.getAspIgst());
					summaryRespa2.setAspSgst(b2bSummary.getAspSgst());
					summaryRespa2.setAspCgst(b2bSummary.getAspCgst());
					summaryRespa2.setAspCess(b2bSummary.getAspCess());
					
					summaryRespa2.setUsrValue(b2bSummary.getUsrValue());
					summaryRespa2.setUsrSgst(b2bSummary.getUsrSgst());
					summaryRespa2.setUsrIgst(b2bSummary.getUsrIgst());
					summaryRespa2.setUsrCgst(b2bSummary.getUsrCgst());
					summaryRespa2.setUsrCess(b2bSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa2.setGstnValue(b2bSummary.getGstnValue());
					summaryRespa2.setGstnIgst(b2bSummary.getGstnIgst());
					summaryRespa2.setGstnSgst(b2bSummary.getGstnSgst());
					summaryRespa2.setGstnCgst(b2bSummary.getGstnCgst());
					summaryRespa2.setGstnCess(b2bSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa2.getAspValue(),
							summaryRespa2.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa2.getAspSgst(),
							summaryRespa2.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa2.getAspIgst(),
							summaryRespa2.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa2.getAspCgst(),
							summaryRespa2.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa2.getAspCess(),
							summaryRespa2.getGstnCess());

					summaryRespa2.setDiffValue(diffInv);
					summaryRespa2.setDiffIgst(diffIgst);
					summaryRespa2.setDiffCgst(diffCgst);
					summaryRespa2.setDiffSgst(diffSgst);
					summaryRespa2.setDiffCess(diffCess);

					b2bSummaryRespList.add(summaryRespa2);
				});
			}

		}
		List<Ret1SummaryRespDto> expwtSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto expwt : list) {

			Ret1BasicSectionSummaryDto expwtList = expwt.getExpwt();
			List<Ret1SummarySectionDto> expwtEySummary = expwtList
					.getEySummary();

			if (expwtEySummary != null) {
				expwtEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryRespa3 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa3.setTable(expwtSummary.getTable());
					summaryRespa3.setSection("3");
					summaryRespa3.setSupplyType(expwtSummary.getSupplyType());
					summaryRespa3.setAspValue(expwtSummary.getTaxableValue());
					summaryRespa3.setAspIgst(expwtSummary.getAspIgst());
					summaryRespa3.setAspSgst(expwtSummary.getAspSgst());
					summaryRespa3.setAspCgst(expwtSummary.getAspCgst());
					summaryRespa3.setAspCess(expwtSummary.getAspCess());
					
					summaryRespa3.setUsrValue(expwtSummary.getUsrValue());
					summaryRespa3.setUsrSgst(expwtSummary.getUsrSgst());
					summaryRespa3.setUsrIgst(expwtSummary.getUsrIgst());
					summaryRespa3.setUsrCgst(expwtSummary.getUsrCgst());
					summaryRespa3.setUsrCess(expwtSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa3.setGstnValue(expwtSummary.getGstnValue());
					summaryRespa3.setGstnIgst(expwtSummary.getGstnIgst());
					summaryRespa3.setGstnSgst(expwtSummary.getGstnSgst());
					summaryRespa3.setGstnCgst(expwtSummary.getGstnCgst());
					summaryRespa3.setGstnCess(expwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa3.getAspValue(),
							summaryRespa3.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa3.getAspSgst(),
							summaryRespa3.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa3.getAspIgst(),
							summaryRespa3.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa3.getAspCgst(),
							summaryRespa3.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa3.getAspCess(),
							summaryRespa3.getGstnCess());

					summaryRespa3.setDiffValue(diffInv);
					summaryRespa3.setDiffIgst(diffIgst);
					summaryRespa3.setDiffCgst(diffCgst);
					summaryRespa3.setDiffSgst(diffSgst);
					summaryRespa3.setDiffCess(diffCess);

					expwtSummaryRespList.add(summaryRespa3);
				});
			}

		}

		List<Ret1SummaryRespDto> expwotSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto expwot : list) {

			Ret1BasicSectionSummaryDto expwtList = expwot.getExpwot();
			List<Ret1SummarySectionDto> expwtEySummary = expwtList
					.getEySummary();

			if (expwtEySummary != null) {
				expwtEySummary.forEach(expwtSummary -> {
					Ret1SummaryRespDto summaryRespa4 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa4.setTable(expwtSummary.getTable());
					summaryRespa4.setSection("4");
					summaryRespa4.setSupplyType(expwtSummary.getSupplyType());
					summaryRespa4.setAspValue(expwtSummary.getTaxableValue());
					summaryRespa4.setAspIgst(expwtSummary.getAspIgst());
					summaryRespa4.setAspSgst(expwtSummary.getAspSgst());
					summaryRespa4.setAspCgst(expwtSummary.getAspCgst());
					summaryRespa4.setAspCess(expwtSummary.getAspCess());
					
					summaryRespa4.setUsrValue(expwtSummary.getUsrValue());
					summaryRespa4.setUsrSgst(expwtSummary.getUsrSgst());
					summaryRespa4.setUsrIgst(expwtSummary.getUsrIgst());
					summaryRespa4.setUsrCgst(expwtSummary.getUsrCgst());
					summaryRespa4.setUsrCess(expwtSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa4.setGstnValue(expwtSummary.getGstnValue());
					summaryRespa4.setGstnIgst(expwtSummary.getGstnIgst());
					summaryRespa4.setGstnSgst(expwtSummary.getGstnSgst());
					summaryRespa4.setGstnCgst(expwtSummary.getGstnCgst());
					summaryRespa4.setGstnCess(expwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa4.getAspValue(),
							summaryRespa4.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa4.getAspSgst(),
							summaryRespa4.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa4.getAspIgst(),
							summaryRespa4.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa4.getAspCgst(),
							summaryRespa4.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa4.getAspCess(),
							summaryRespa4.getGstnCess());

					summaryRespa4.setDiffValue(diffInv);
					summaryRespa4.setDiffIgst(diffIgst);
					summaryRespa4.setDiffCgst(diffCgst);
					summaryRespa4.setDiffSgst(diffSgst);
					summaryRespa4.setDiffCess(diffCess);

					expwotSummaryRespList.add(summaryRespa4);
				});
			}

		}
		List<Ret1SummaryRespDto> sezwtSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto sezwt : list) {

			Ret1BasicSectionSummaryDto sezwttList = sezwt.getSezwt();
			List<Ret1SummarySectionDto> sezwtEySummary = sezwttList
					.getEySummary();

			if (sezwtEySummary != null) {
				sezwtEySummary.forEach(sezwtSummary -> {
					Ret1SummaryRespDto summaryRespa5 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa5.setTable(sezwtSummary.getTable());
					summaryRespa5.setSection("5");
					summaryRespa5.setSupplyType(sezwtSummary.getSupplyType());
					summaryRespa5.setAspValue(sezwtSummary.getTaxableValue());
					summaryRespa5.setAspIgst(sezwtSummary.getAspIgst());
					summaryRespa5.setAspSgst(sezwtSummary.getAspSgst());
					summaryRespa5.setAspCgst(sezwtSummary.getAspCgst());
					summaryRespa5.setAspCess(sezwtSummary.getAspCess());
					
					summaryRespa5.setUsrValue(sezwtSummary.getUsrValue());
					summaryRespa5.setUsrSgst(sezwtSummary.getUsrSgst());
					summaryRespa5.setUsrIgst(sezwtSummary.getUsrIgst());
					summaryRespa5.setUsrCgst(sezwtSummary.getUsrCgst());
					summaryRespa5.setUsrCess(sezwtSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa5.setGstnValue(sezwtSummary.getGstnValue());
					summaryRespa5.setGstnIgst(sezwtSummary.getGstnIgst());
					summaryRespa5.setGstnSgst(sezwtSummary.getGstnSgst());
					summaryRespa5.setGstnCgst(sezwtSummary.getGstnCgst());
					summaryRespa5.setGstnCess(sezwtSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa5.getAspValue(),
							summaryRespa5.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa5.getAspSgst(),
							summaryRespa5.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa5.getAspIgst(),
							summaryRespa5.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa5.getAspCgst(),
							summaryRespa5.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa5.getAspCess(),
							summaryRespa5.getGstnCess());

					summaryRespa5.setDiffValue(diffInv);
					summaryRespa5.setDiffIgst(diffIgst);
					summaryRespa5.setDiffCgst(diffCgst);
					summaryRespa5.setDiffSgst(diffSgst);
					summaryRespa5.setDiffCess(diffCess);

					sezwtSummaryRespList.add(summaryRespa5);
				});
			}

		}
		List<Ret1SummaryRespDto> sezwotSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto sezwot : list) {

			Ret1BasicSectionSummaryDto sezwottList = sezwot.getSezwot();
			List<Ret1SummarySectionDto> sezwotEySummary = sezwottList
					.getEySummary();

			if (sezwotEySummary != null) {
				sezwotEySummary.forEach(sezwotSummary -> {
					Ret1SummaryRespDto summaryRespa6 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa6.setTable(sezwotSummary.getTable());
					summaryRespa6.setSection("6");
					summaryRespa6.setSupplyType(sezwotSummary.getSupplyType());
					summaryRespa6.setAspValue(sezwotSummary.getTaxableValue());
					summaryRespa6.setAspIgst(sezwotSummary.getAspIgst());
					summaryRespa6.setAspSgst(sezwotSummary.getAspSgst());
					summaryRespa6.setAspCgst(sezwotSummary.getAspCgst());
					summaryRespa6.setAspCess(sezwotSummary.getAspCess());
					
					summaryRespa6.setUsrValue(sezwotSummary.getUsrValue());
					summaryRespa6.setUsrSgst(sezwotSummary.getUsrSgst());
					summaryRespa6.setUsrIgst(sezwotSummary.getUsrIgst());
					summaryRespa6.setUsrCgst(sezwotSummary.getUsrCgst());
					summaryRespa6.setUsrCess(sezwotSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa6.setGstnValue(sezwotSummary.getGstnValue());
					summaryRespa6.setGstnIgst(sezwotSummary.getGstnIgst());
					summaryRespa6.setGstnSgst(sezwotSummary.getGstnSgst());
					summaryRespa6.setGstnCgst(sezwotSummary.getGstnCgst());
					summaryRespa6.setGstnCess(sezwotSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa6.getAspValue(),
							summaryRespa6.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa6.getAspSgst(),
							summaryRespa6.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa6.getAspIgst(),
							summaryRespa6.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa6.getAspCgst(),
							summaryRespa6.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa6.getAspCess(),
							summaryRespa6.getGstnCess());

					summaryRespa6.setDiffValue(diffInv);
					summaryRespa6.setDiffIgst(diffIgst);
					summaryRespa6.setDiffCgst(diffCgst);
					summaryRespa6.setDiffSgst(diffSgst);
					summaryRespa6.setDiffCess(diffCess);

					sezwotSummaryRespList.add(summaryRespa6);
				});
			}

		}

		List<Ret1SummaryRespDto> deSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto de : list) {

			Ret1BasicSectionSummaryDto deList = de.getDeemedExp();
			List<Ret1SummarySectionDto> deEySummary = deList.getEySummary();

			if (deEySummary != null) {
				deEySummary.forEach(deSummary -> {
					Ret1SummaryRespDto summaryRespa7 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa7.setTable(deSummary.getTable());
					summaryRespa7.setSection("7");
					summaryRespa7.setSupplyType(deSummary.getSupplyType());
					summaryRespa7.setAspValue(deSummary.getTaxableValue());
					summaryRespa7.setAspIgst(deSummary.getAspIgst());
					summaryRespa7.setAspSgst(deSummary.getAspSgst());
					summaryRespa7.setAspCgst(deSummary.getAspCgst());
					summaryRespa7.setAspCess(deSummary.getAspCess());
					
					summaryRespa7.setUsrValue(deSummary.getUsrValue());
					summaryRespa7.setUsrSgst(deSummary.getUsrSgst());
					summaryRespa7.setUsrIgst(deSummary.getUsrIgst());
					summaryRespa7.setUsrCgst(deSummary.getUsrCgst());
					summaryRespa7.setUsrCess(deSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa7.setGstnValue(deSummary.getGstnValue());
					summaryRespa7.setGstnIgst(deSummary.getGstnIgst());
					summaryRespa7.setGstnSgst(deSummary.getGstnSgst());
					summaryRespa7.setGstnCgst(deSummary.getGstnCgst());
					summaryRespa7.setGstnCess(deSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa7.getAspValue(),
							summaryRespa7.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa7.getAspSgst(),
							summaryRespa7.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa7.getAspIgst(),
							summaryRespa7.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa7.getAspCgst(),
							summaryRespa7.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa7.getAspCess(),
							summaryRespa7.getGstnCess());

					summaryRespa7.setDiffValue(diffInv);
					summaryRespa7.setDiffIgst(diffIgst);
					summaryRespa7.setDiffCgst(diffCgst);
					summaryRespa7.setDiffSgst(diffSgst);
					summaryRespa7.setDiffCess(diffCess);

					deSummaryRespList.add(summaryRespa7);
				});
			}

		}
		List<Ret1SummaryRespDto> pPSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto pp : list) {

			Ret1BasicSectionSummaryDto ppList = pp.getpPeriod();
			List<Ret1SummarySectionDto> ppEySummary = ppList.getEySummary();

			if (ppEySummary != null) {
				ppEySummary.forEach(pPSummary -> {
					Ret1SummaryRespDto summaryRespa8 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa8.setTable(pPSummary.getTable());
					summaryRespa8.setSection("8");
					summaryRespa8.setSupplyType(pPSummary.getSupplyType());
					summaryRespa8.setAspValue(pPSummary.getTaxableValue());
					summaryRespa8.setAspIgst(pPSummary.getAspIgst());
					summaryRespa8.setAspSgst(pPSummary.getAspSgst());
					summaryRespa8.setAspCgst(pPSummary.getAspCgst());
					summaryRespa8.setAspCess(pPSummary.getAspCess());
					
					summaryRespa8.setUsrValue(pPSummary.getUsrValue());
					summaryRespa8.setUsrSgst(pPSummary.getUsrSgst());
					summaryRespa8.setUsrIgst(pPSummary.getUsrIgst());
					summaryRespa8.setUsrCgst(pPSummary.getUsrCgst());
					summaryRespa8.setUsrCess(pPSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa8.setGstnValue(pPSummary.getGstnValue());
					summaryRespa8.setGstnIgst(pPSummary.getGstnIgst());
					summaryRespa8.setGstnSgst(pPSummary.getGstnSgst());
					summaryRespa8.setGstnCgst(pPSummary.getGstnCgst());
					summaryRespa8.setGstnCess(pPSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa8.getAspValue(),
							summaryRespa8.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa8.getAspSgst(),
							summaryRespa8.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa8.getAspIgst(),
							summaryRespa8.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa8.getAspCgst(),
							summaryRespa8.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa8.getAspCess(),
							summaryRespa8.getGstnCess());

					summaryRespa8.setDiffValue(diffInv);
					summaryRespa8.setDiffIgst(diffIgst);
					summaryRespa8.setDiffCgst(diffCgst);
					summaryRespa8.setDiffSgst(diffSgst);
					summaryRespa8.setDiffCess(diffCess);

					pPSummaryRespList.add(summaryRespa8);
				});
			}

		}

		List<Ret1SummaryRespDto> b3SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto b3 : list) {

			Ret1BasicSectionSummaryDto b3List = b3.getB3_Tot();
			List<Ret1SummarySectionDto> b3EySummary = b3List.getEySummary();

			if (b3EySummary != null) {
				b3EySummary.forEach(b3Summary -> {
					Ret1SummaryRespDto summaryRespa9 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespa9.setTable(b3Summary.getTable());
					summaryRespa9.setSupplyType(b3Summary.getSupplyType());
					summaryRespa9.setAspValue(b3Summary.getTaxableValue());
					summaryRespa9.setAspIgst(b3Summary.getAspIgst());
					summaryRespa9.setAspSgst(b3Summary.getAspSgst());
					summaryRespa9.setAspCgst(b3Summary.getAspCgst());
					summaryRespa9.setAspCess(b3Summary.getAspCess());
					
					summaryRespa9.setUsrValue(b3Summary.getUsrValue());
					summaryRespa9.setUsrSgst(b3Summary.getUsrSgst());
					summaryRespa9.setUsrIgst(b3Summary.getUsrIgst());
					summaryRespa9.setUsrCgst(b3Summary.getUsrCgst());
					summaryRespa9.setUsrCess(b3Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespa9.setGstnValue(b3Summary.getGstnValue());
					summaryRespa9.setGstnIgst(b3Summary.getGstnIgst());
					summaryRespa9.setGstnSgst(b3Summary.getGstnSgst());
					summaryRespa9.setGstnCgst(b3Summary.getGstnCgst());
					summaryRespa9.setGstnCess(b3Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespa9.getAspValue(),
							summaryRespa9.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespa9.getAspSgst(),
							summaryRespa9.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespa9.getAspIgst(),
							summaryRespa9.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespa9.getAspCgst(),
							summaryRespa9.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespa9.getAspCess(),
							summaryRespa9.getGstnCess());

					summaryRespa9.setDiffValue(diffInv);
					summaryRespa9.setDiffIgst(diffIgst);
					summaryRespa9.setDiffCgst(diffCgst);
					summaryRespa9.setDiffSgst(diffSgst);
					summaryRespa9.setDiffCess(diffCess);

					b3SummaryRespList.add(summaryRespa9);
				});
			}

		}

		List<Ret1SummaryRespDto> revSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rev : list) {

			Ret1BasicSectionSummaryDto revList = rev.getRev();
			List<Ret1SummarySectionDto> revEySummary = revList.getEySummary();

			if (revEySummary != null) {
				revEySummary.forEach(revSummary -> {
					Ret1SummaryRespDto summaryRespb1 = new Ret1SummaryRespDto();
					// EY Resp
					if("rcInward".equalsIgnoreCase(revSummary.getSupplyType())){
					summaryRespb1.setTable(revSummary.getTable());
					summaryRespb1.setSection("1");
					summaryRespb1.setSupplyType(revSummary.getSupplyType());
					summaryRespb1.setAspValue(revSummary.getTaxableValue());
					summaryRespb1.setAspIgst(revSummary.getAspIgst());
					summaryRespb1.setAspSgst(revSummary.getAspSgst());
					summaryRespb1.setAspCgst(revSummary.getAspCgst());
					summaryRespb1.setAspCess(revSummary.getAspCess());
					
					summaryRespb1.setUsrValue(revSummary.getUsrValue());
					summaryRespb1.setUsrSgst(revSummary.getUsrSgst());
					summaryRespb1.setUsrIgst(revSummary.getUsrIgst());
					summaryRespb1.setUsrCgst(revSummary.getUsrCgst());
					summaryRespb1.setUsrCess(revSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespb1.setGstnValue(revSummary.getGstnValue());
					summaryRespb1.setGstnIgst(revSummary.getGstnIgst());
					summaryRespb1.setGstnSgst(revSummary.getGstnSgst());
					summaryRespb1.setGstnCgst(revSummary.getGstnCgst());
					summaryRespb1.setGstnCess(revSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespb1.getAspValue(),
							summaryRespb1.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespb1.getAspSgst(),
							summaryRespb1.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespb1.getAspIgst(),
							summaryRespb1.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespb1.getAspCgst(),
							summaryRespb1.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespb1.getAspCess(),
							summaryRespb1.getGstnCess());

					summaryRespb1.setDiffValue(diffInv);
					summaryRespb1.setDiffIgst(diffIgst);
					summaryRespb1.setDiffCgst(diffCgst);
					summaryRespb1.setDiffSgst(diffSgst);
					summaryRespb1.setDiffCess(diffCess);

					revSummaryRespList.add(summaryRespb1);
					}
				});
			}

		}
		List<Ret1SummaryRespDto> impsSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto imps : list) {

			Ret1BasicSectionSummaryDto impsList = imps.getImps();
			List<Ret1SummarySectionDto> impsEySummary = impsList.getEySummary();

			if (impsEySummary != null) {
				impsEySummary.forEach(impsSummary -> {
					Ret1SummaryRespDto summaryRespb2 = new Ret1SummaryRespDto();
					// EY Resp
					if("imps".equalsIgnoreCase(impsSummary.getSupplyType())){
					summaryRespb2.setTable(impsSummary.getTable());
					summaryRespb2.setSection("2");
					summaryRespb2.setSupplyType(impsSummary.getSupplyType());
					summaryRespb2.setAspValue(impsSummary.getTaxableValue());
					summaryRespb2.setAspIgst(impsSummary.getAspIgst());
					summaryRespb2.setAspSgst(impsSummary.getAspSgst());
					summaryRespb2.setAspCgst(impsSummary.getAspCgst());
					summaryRespb2.setAspCess(impsSummary.getAspCess());
					
					summaryRespb2.setUsrValue(impsSummary.getUsrValue());
					summaryRespb2.setUsrSgst(impsSummary.getUsrSgst());
					summaryRespb2.setUsrIgst(impsSummary.getUsrIgst());
					summaryRespb2.setUsrCgst(impsSummary.getUsrCgst());
					summaryRespb2.setUsrCess(impsSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespb2.setGstnValue(impsSummary.getGstnValue());
					summaryRespb2.setGstnIgst(impsSummary.getGstnIgst());
					summaryRespb2.setGstnSgst(impsSummary.getGstnSgst());
					summaryRespb2.setGstnCgst(impsSummary.getGstnCgst());
					summaryRespb2.setGstnCess(impsSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespb2.getAspValue(),
							summaryRespb2.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespb2.getAspSgst(),
							summaryRespb2.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespb2.getAspIgst(),
							summaryRespb2.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespb2.getAspCgst(),
							summaryRespb2.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespb2.getAspCess(),
							summaryRespb2.getGstnCess());

					summaryRespb2.setDiffValue(diffInv);
					summaryRespb2.setDiffIgst(diffIgst);
					summaryRespb2.setDiffCgst(diffCgst);
					summaryRespb2.setDiffSgst(diffSgst);
					summaryRespb2.setDiffCess(diffCess);

					impsSummaryRespList.add(summaryRespb2);
					}
				});
			}

		}

		List<Ret1SummaryRespDto> c3SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto c3 : list) {

			Ret1BasicSectionSummaryDto c3List = c3.getC3_Tot();
			List<Ret1SummarySectionDto> c3EySummary = c3List.getEySummary();

			if (c3EySummary != null) {
				c3EySummary.forEach(c3Summary -> {
					Ret1SummaryRespDto summaryRespct = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespct.setTable(c3Summary.getTable());
					summaryRespct.setSupplyType(c3Summary.getSupplyType());
					summaryRespct.setAspValue(c3Summary.getTaxableValue());
					summaryRespct.setAspIgst(c3Summary.getAspIgst());
					summaryRespct.setAspSgst(c3Summary.getAspSgst());
					summaryRespct.setAspCgst(c3Summary.getAspCgst());
					summaryRespct.setAspCess(c3Summary.getAspCess());
					
					summaryRespct.setUsrValue(c3Summary.getUsrValue());
					summaryRespct.setUsrSgst(c3Summary.getUsrSgst());
					summaryRespct.setUsrIgst(c3Summary.getUsrIgst());
					summaryRespct.setUsrCgst(c3Summary.getUsrCgst());
					summaryRespct.setUsrCess(c3Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespct.setGstnValue(c3Summary.getGstnValue());
					summaryRespct.setGstnIgst(c3Summary.getGstnIgst());
					summaryRespct.setGstnSgst(c3Summary.getGstnSgst());
					summaryRespct.setGstnCgst(c3Summary.getGstnCgst());
					summaryRespct.setGstnCess(c3Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespct.getAspValue(),
							summaryRespct.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespct.getAspSgst(),
							summaryRespct.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespct.getAspIgst(),
							summaryRespct.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespct.getAspCgst(),
							summaryRespct.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespct.getAspCess(),
							summaryRespct.getGstnCess());

					summaryRespct.setDiffValue(diffInv);
					summaryRespct.setDiffIgst(diffIgst);
					summaryRespct.setDiffCgst(diffCgst);
					summaryRespct.setDiffSgst(diffSgst);
					summaryRespct.setDiffCess(diffCess);

					c3SummaryRespList.add(summaryRespct);
				});
			}

		}

		List<Ret1SummaryRespDto> drSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto dr : list) {

			Ret1BasicSectionSummaryDto drList = dr.getDr();
			List<Ret1SummarySectionDto> drEySummary = drList.getEySummary();

			if (drEySummary != null) {
				drEySummary.forEach(drSummary -> {
					Ret1SummaryRespDto summaryRespc1 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespc1.setTable(drSummary.getTable());
					summaryRespc1.setSection("1");
					summaryRespc1.setSupplyType(drSummary.getSupplyType());
					summaryRespc1.setAspValue(drSummary.getTaxableValue());
					summaryRespc1.setAspIgst(drSummary.getAspIgst());
					summaryRespc1.setAspSgst(drSummary.getAspSgst());
					summaryRespc1.setAspCgst(drSummary.getAspCgst());
					summaryRespc1.setAspCess(drSummary.getAspCess());
					
					summaryRespc1.setUsrValue(drSummary.getUsrValue());
					summaryRespc1.setUsrSgst(drSummary.getUsrSgst());
					summaryRespc1.setUsrIgst(drSummary.getUsrIgst());
					summaryRespc1.setUsrCgst(drSummary.getUsrCgst());
					summaryRespc1.setUsrCess(drSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespc1.setGstnValue(drSummary.getGstnValue());
					summaryRespc1.setGstnIgst(drSummary.getGstnIgst());
					summaryRespc1.setGstnSgst(drSummary.getGstnSgst());
					summaryRespc1.setGstnCgst(drSummary.getGstnCgst());
					summaryRespc1.setGstnCess(drSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespc1.getAspValue(),
							summaryRespc1.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespc1.getAspSgst(),
							summaryRespc1.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespc1.getAspIgst(),
							summaryRespc1.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespc1.getAspCgst(),
							summaryRespc1.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespc1.getAspCess(),
							summaryRespc1.getGstnCess());

					summaryRespc1.setDiffValue(diffInv);
					summaryRespc1.setDiffIgst(diffIgst);
					summaryRespc1.setDiffCgst(diffCgst);
					summaryRespc1.setDiffSgst(diffSgst);
					summaryRespc1.setDiffCess(diffCess);

					drSummaryRespList.add(summaryRespc1);
				});
			}

		}

		List<Ret1SummaryRespDto> crSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto cr : list) {

			Ret1BasicSectionSummaryDto crList = cr.getCr();
			List<Ret1SummarySectionDto> crEySummary = crList.getEySummary();

			if (crEySummary != null) {
				crEySummary.forEach(crSummary -> {
					Ret1SummaryRespDto summaryRespc2 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespc2.setTable(crSummary.getTable());
					summaryRespc2.setSection("2");
					summaryRespc2.setSupplyType(crSummary.getSupplyType());
					summaryRespc2.setAspValue(crSummary.getTaxableValue());
					summaryRespc2.setAspIgst(crSummary.getAspIgst());
					summaryRespc2.setAspSgst(crSummary.getAspSgst());
					summaryRespc2.setAspCgst(crSummary.getAspCgst());
					summaryRespc2.setAspCess(crSummary.getAspCess());
					
					summaryRespc2.setUsrValue(crSummary.getUsrValue());
					summaryRespc2.setUsrSgst(crSummary.getUsrSgst());
					summaryRespc2.setUsrIgst(crSummary.getUsrIgst());
					summaryRespc2.setUsrCgst(crSummary.getUsrCgst());
					summaryRespc2.setUsrCess(crSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespc2.setGstnValue(crSummary.getGstnValue());
					summaryRespc2.setGstnIgst(crSummary.getGstnIgst());
					summaryRespc2.setGstnSgst(crSummary.getGstnSgst());
					summaryRespc2.setGstnCgst(crSummary.getGstnCgst());
					summaryRespc2.setGstnCess(crSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespc2.getAspValue(),
							summaryRespc2.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespc2.getAspSgst(),
							summaryRespc2.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespc2.getAspIgst(),
							summaryRespc2.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespc2.getAspCgst(),
							summaryRespc2.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespc2.getAspCess(),
							summaryRespc2.getGstnCess());

					summaryRespc2.setDiffValue(diffInv);
					summaryRespc2.setDiffIgst(diffIgst);
					summaryRespc2.setDiffCgst(diffCgst);
					summaryRespc2.setDiffSgst(diffSgst);
					summaryRespc2.setDiffCess(diffCess);

					crSummaryRespList.add(summaryRespc2);
				});
			}

		}

		List<Ret1SummaryRespDto> advRejSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto advRej : list) {

			Ret1BasicSectionSummaryDto advRejList = advRej.getAdvRec();
			List<Ret1SummarySectionDto> advRejEySummary = advRejList
					.getEySummary();

			if (advRejEySummary != null) {
				advRejEySummary.forEach(advRejSummary -> {
					Ret1SummaryRespDto summaryRespc3 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespc3.setTable(advRejSummary.getTable());
					summaryRespc3.setSection("3");
					summaryRespc3.setSupplyType(advRejSummary.getSupplyType());
					summaryRespc3.setAspValue(advRejSummary.getTaxableValue());
					summaryRespc3.setAspIgst(advRejSummary.getAspIgst());
					summaryRespc3.setAspSgst(advRejSummary.getAspSgst());
					summaryRespc3.setAspCgst(advRejSummary.getAspCgst());
					summaryRespc3.setAspCess(advRejSummary.getAspCess());
					
					summaryRespc3.setUsrValue(advRejSummary.getUsrValue());
					summaryRespc3.setUsrSgst(advRejSummary.getUsrSgst());
					summaryRespc3.setUsrIgst(advRejSummary.getUsrIgst());
					summaryRespc3.setUsrCgst(advRejSummary.getUsrCgst());
					summaryRespc3.setUsrCess(advRejSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespc3.setGstnValue(advRejSummary.getGstnValue());
					summaryRespc3.setGstnIgst(advRejSummary.getGstnIgst());
					summaryRespc3.setGstnSgst(advRejSummary.getGstnSgst());
					summaryRespc3.setGstnCgst(advRejSummary.getGstnCgst());
					summaryRespc3.setGstnCess(advRejSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespc3.getAspValue(),
							summaryRespc3.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespc3.getAspSgst(),
							summaryRespc3.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespc3.getAspIgst(),
							summaryRespc3.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespc3.getAspCgst(),
							summaryRespc3.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespc3.getAspCess(),
							summaryRespc3.getGstnCess());

					summaryRespc3.setDiffValue(diffInv);
					summaryRespc3.setDiffIgst(diffIgst);
					summaryRespc3.setDiffCgst(diffCgst);
					summaryRespc3.setDiffSgst(diffSgst);
					summaryRespc3.setDiffCess(diffCess);

					advRejSummaryRespList.add(summaryRespc3);
				});
			}

		}

		List<Ret1SummaryRespDto> advAdjSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto advAdj : list) {

			Ret1BasicSectionSummaryDto advAdjList = advAdj.getAdvAdj();
			List<Ret1SummarySectionDto> advAdjEySummary = advAdjList
					.getEySummary();

			if (advAdjEySummary != null) {
				advAdjEySummary.forEach(advAdjSummary -> {
					Ret1SummaryRespDto summaryRespc4 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespc4.setTable(advAdjSummary.getTable());
					summaryRespc4.setSection("4");
					summaryRespc4.setSupplyType(advAdjSummary.getSupplyType());
					summaryRespc4.setAspValue(advAdjSummary.getTaxableValue());
					summaryRespc4.setAspIgst(advAdjSummary.getAspIgst());
					summaryRespc4.setAspSgst(advAdjSummary.getAspSgst());
					summaryRespc4.setAspCgst(advAdjSummary.getAspCgst());
					summaryRespc4.setAspCess(advAdjSummary.getAspCess());
					
					summaryRespc4.setUsrValue(advAdjSummary.getUsrValue());
					summaryRespc4.setUsrSgst(advAdjSummary.getUsrSgst());
					summaryRespc4.setUsrIgst(advAdjSummary.getUsrIgst());
					summaryRespc4.setUsrCgst(advAdjSummary.getUsrCgst());
					summaryRespc4.setUsrCess(advAdjSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespc4.setGstnValue(advAdjSummary.getGstnValue());
					summaryRespc4.setGstnIgst(advAdjSummary.getGstnIgst());
					summaryRespc4.setGstnSgst(advAdjSummary.getGstnSgst());
					summaryRespc4.setGstnCgst(advAdjSummary.getGstnCgst());
					summaryRespc4.setGstnCess(advAdjSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespc4.getAspValue(),
							summaryRespc4.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespc4.getAspSgst(),
							summaryRespc4.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespc4.getAspIgst(),
							summaryRespc4.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespc4.getAspCgst(),
							summaryRespc4.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespc4.getAspCess(),
							summaryRespc4.getGstnCess());

					summaryRespc4.setDiffValue(diffInv);
					summaryRespc4.setDiffIgst(diffIgst);
					summaryRespc4.setDiffCgst(diffCgst);
					summaryRespc4.setDiffSgst(diffSgst);
					summaryRespc4.setDiffCess(diffCess);

					advAdjSummaryRespList.add(summaryRespc4);
				});
			}

		}

		List<Ret1SummaryRespDto> otherRedSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto otherRed : list) {

			Ret1BasicSectionSummaryDto otherList = otherRed.getOtherRed();
			List<Ret1SummarySectionDto> otherListEySummary = otherList
					.getEySummary();

			if (otherListEySummary != null) {
				otherListEySummary.forEach(otherRedSummary -> {
					Ret1SummaryRespDto summaryRespc5 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespc5.setTable(otherRedSummary.getTable());
					summaryRespc5.setSection("5");
					summaryRespc5
							.setSupplyType(otherRedSummary.getSupplyType());
					summaryRespc5
							.setAspValue(otherRedSummary.getTaxableValue());
					summaryRespc5.setAspIgst(otherRedSummary.getAspIgst());
					summaryRespc5.setAspSgst(otherRedSummary.getAspSgst());
					summaryRespc5.setAspCgst(otherRedSummary.getAspCgst());
					summaryRespc5.setAspCess(otherRedSummary.getAspCess());
					
					summaryRespc5.setUsrValue(otherRedSummary.getUsrValue());
					summaryRespc5.setUsrSgst(otherRedSummary.getUsrSgst());
					summaryRespc5.setUsrIgst(otherRedSummary.getUsrIgst());
					summaryRespc5.setUsrCgst(otherRedSummary.getUsrCgst());
					summaryRespc5.setUsrCess(otherRedSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespc5.setGstnValue(otherRedSummary.getGstnValue());
					summaryRespc5.setGstnIgst(otherRedSummary.getGstnIgst());
					summaryRespc5.setGstnSgst(otherRedSummary.getGstnSgst());
					summaryRespc5.setGstnCgst(otherRedSummary.getGstnCgst());
					summaryRespc5.setGstnCess(otherRedSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespc5.getAspValue(),
							summaryRespc5.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespc5.getAspSgst(),
							summaryRespc5.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespc5.getAspIgst(),
							summaryRespc5.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespc5.getAspCgst(),
							summaryRespc5.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespc5.getAspCess(),
							summaryRespc5.getGstnCess());

					summaryRespc5.setDiffValue(diffInv);
					summaryRespc5.setDiffIgst(diffIgst);
					summaryRespc5.setDiffCgst(diffCgst);
					summaryRespc5.setDiffSgst(diffSgst);
					summaryRespc5.setDiffCess(diffCess);

					otherRedSummaryRespList.add(summaryRespc5);
				});
			}

		}

		List<Ret1SummaryRespDto> d3ExemptSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto d3 : list) {

			Ret1BasicSectionSummaryDto d3List = d3.getD_3D();
			List<Ret1SummarySectionDto> d3ListEySummary = d3List.getEySummary();

			if (d3ListEySummary != null) {
				d3ListEySummary.forEach(d3Summary -> {
					Ret1SummaryRespDto summaryRespdt = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespdt.setTable(d3Summary.getTable());
					summaryRespdt.setSupplyType(d3Summary.getSupplyType());
					summaryRespdt.setAspValue(d3Summary.getTaxableValue());
					summaryRespdt.setAspIgst(d3Summary.getAspIgst());
					summaryRespdt.setAspSgst(d3Summary.getAspSgst());
					summaryRespdt.setAspCgst(d3Summary.getAspCgst());
					summaryRespdt.setAspCess(d3Summary.getAspCess());
					
					summaryRespdt.setUsrValue(d3Summary.getUsrValue());
					summaryRespdt.setUsrSgst(d3Summary.getUsrSgst());
					summaryRespdt.setUsrIgst(d3Summary.getUsrIgst());
					summaryRespdt.setUsrCgst(d3Summary.getUsrCgst());
					summaryRespdt.setUsrCess(d3Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespdt.setGstnValue(d3Summary.getGstnValue());
					summaryRespdt.setGstnIgst(d3Summary.getGstnIgst());
					summaryRespdt.setGstnSgst(d3Summary.getGstnSgst());
					summaryRespdt.setGstnCgst(d3Summary.getGstnCgst());
					summaryRespdt.setGstnCess(d3Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespdt.getAspValue(),
							summaryRespdt.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespdt.getAspSgst(),
							summaryRespdt.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespdt.getAspIgst(),
							summaryRespdt.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespdt.getAspCgst(),
							summaryRespdt.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespdt.getAspCess(),
							summaryRespdt.getGstnCess());

					summaryRespdt.setDiffValue(diffInv);
					summaryRespdt.setDiffIgst(diffIgst);
					summaryRespdt.setDiffCgst(diffCgst);
					summaryRespdt.setDiffSgst(diffSgst);
					summaryRespdt.setDiffCess(diffCess);
					d3ExemptSummaryRespList.add(summaryRespdt);
				});
			}

		}

		List<Ret1SummaryRespDto> nilExemptSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto nil : list) {

			Ret1BasicSectionSummaryDto nilList = nil.getD_3D1();
			List<Ret1SummarySectionDto> nilListEySummary = nilList
					.getEySummary();

			if (nilListEySummary != null) {
				nilListEySummary.forEach(nilSummary -> {
					Ret1SummaryRespDto summaryRespd1 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespd1.setTable(nilSummary.getTable());
					summaryRespd1.setSection("1");
					summaryRespd1.setSupplyType(nilSummary.getSupplyType());
					summaryRespd1.setAspValue(nilSummary.getTaxableValue());
					summaryRespd1.setAspIgst(nilSummary.getAspIgst());
					summaryRespd1.setAspSgst(nilSummary.getAspSgst());
					summaryRespd1.setAspCgst(nilSummary.getAspCgst());
					summaryRespd1.setAspCess(nilSummary.getAspCess());
					
					summaryRespd1.setUsrValue(nilSummary.getUsrValue());
					summaryRespd1.setUsrSgst(nilSummary.getUsrSgst());
					summaryRespd1.setUsrIgst(nilSummary.getUsrIgst());
					summaryRespd1.setUsrCgst(nilSummary.getUsrCgst());
					summaryRespd1.setUsrCess(nilSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespd1.setGstnValue(nilSummary.getGstnValue());
					summaryRespd1.setGstnIgst(nilSummary.getGstnIgst());
					summaryRespd1.setGstnSgst(nilSummary.getGstnSgst());
					summaryRespd1.setGstnCgst(nilSummary.getGstnCgst());
					summaryRespd1.setGstnCess(nilSummary.getGstnCess());

					// Diff
					BigDecimal diffInv = subMethod(summaryRespd1.getAspValue(),
							summaryRespd1.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespd1.getAspSgst(),
							summaryRespd1.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespd1.getAspIgst(),
							summaryRespd1.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespd1.getAspCgst(),
							summaryRespd1.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespd1.getAspCess(),
							summaryRespd1.getGstnCess());

					summaryRespd1.setDiffValue(diffInv);
					summaryRespd1.setDiffIgst(diffIgst);
					summaryRespd1.setDiffCgst(diffCgst);
					summaryRespd1.setDiffSgst(diffSgst);
					summaryRespd1.setDiffCess(diffCess);

					nilExemptSummaryRespList.add(summaryRespd1);
				});
			}

		}

		List<Ret1SummaryRespDto> nonSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto non : list) {

			Ret1BasicSectionSummaryDto nonList = non.getD_3D2();
			List<Ret1SummarySectionDto> nonListEySummary = nonList
					.getEySummary();

			if (nonListEySummary != null) {
				nonListEySummary.forEach(nonSummary -> {
					Ret1SummaryRespDto summaryRespd2 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespd2.setTable(nonSummary.getTable());
					summaryRespd2.setSection("2");
					summaryRespd2.setSupplyType(nonSummary.getSupplyType());
					summaryRespd2.setAspValue(nonSummary.getTaxableValue());
					summaryRespd2.setAspIgst(nonSummary.getAspIgst());
					summaryRespd2.setAspSgst(nonSummary.getAspSgst());
					summaryRespd2.setAspCgst(nonSummary.getAspCgst());
					summaryRespd2.setAspCess(nonSummary.getAspCess());
					
					summaryRespd2.setUsrValue(nonSummary.getUsrValue());
					summaryRespd2.setUsrSgst(nonSummary.getUsrSgst());
					summaryRespd2.setUsrIgst(nonSummary.getUsrIgst());
					summaryRespd2.setUsrCgst(nonSummary.getUsrCgst());
					summaryRespd2.setUsrCess(nonSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespd2.setGstnValue(nonSummary.getGstnValue());
					summaryRespd2.setGstnIgst(nonSummary.getGstnIgst());
					summaryRespd2.setGstnSgst(nonSummary.getGstnSgst());
					summaryRespd2.setGstnCgst(nonSummary.getGstnCgst());
					summaryRespd2.setGstnCess(nonSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespd2.getAspValue(),
							summaryRespd2.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespd2.getAspSgst(),
							summaryRespd2.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespd2.getAspIgst(),
							summaryRespd2.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespd2.getAspCgst(),
							summaryRespd2.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespd2.getAspCess(),
							summaryRespd2.getGstnCess());

					summaryRespd2.setDiffValue(diffInv);
					summaryRespd2.setDiffIgst(diffIgst);
					summaryRespd2.setDiffCgst(diffCgst);
					summaryRespd2.setDiffSgst(diffSgst);
					summaryRespd2.setDiffCess(diffCess);

					nonSummaryRespList.add(summaryRespd2);
				});
			}

		}

		List<Ret1SummaryRespDto> osRevSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto osRev : list) {

			Ret1BasicSectionSummaryDto osRevList = osRev.getD_3D3();
			List<Ret1SummarySectionDto> osRevListEySummary = osRevList
					.getEySummary();

			if (osRevListEySummary != null) {
				osRevListEySummary.forEach(osRevSummary -> {
					Ret1SummaryRespDto summaryRespd3 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespd3.setTable(osRevSummary.getTable());
					summaryRespd3.setSection("3");
					summaryRespd3.setSupplyType(osRevSummary.getSupplyType());
					summaryRespd3.setAspValue(osRevSummary.getTaxableValue());
					summaryRespd3.setAspIgst(osRevSummary.getAspIgst());
					summaryRespd3.setAspSgst(osRevSummary.getAspSgst());
					summaryRespd3.setAspCgst(osRevSummary.getAspCgst());
					summaryRespd3.setAspCess(osRevSummary.getAspCess());
					
					summaryRespd3.setUsrValue(osRevSummary.getUsrValue());
					summaryRespd3.setUsrSgst(osRevSummary.getUsrSgst());
					summaryRespd3.setUsrIgst(osRevSummary.getUsrIgst());
					summaryRespd3.setUsrCgst(osRevSummary.getUsrCgst());
					summaryRespd3.setUsrCess(osRevSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespd3.setGstnValue(osRevSummary.getGstnValue());
					summaryRespd3.setGstnIgst(osRevSummary.getGstnIgst());
					summaryRespd3.setGstnSgst(osRevSummary.getGstnSgst());
					summaryRespd3.setGstnCgst(osRevSummary.getGstnCgst());
					summaryRespd3.setGstnCess(osRevSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespd3.getAspValue(),
							summaryRespd3.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespd3.getAspSgst(),
							summaryRespd3.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespd3.getAspIgst(),
							summaryRespd3.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespd3.getAspCgst(),
							summaryRespd3.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespd3.getAspCess(),
							summaryRespd3.getGstnCess());

					summaryRespd3.setDiffValue(diffInv);
					summaryRespd3.setDiffIgst(diffIgst);
					summaryRespd3.setDiffCgst(diffCgst);
					summaryRespd3.setDiffSgst(diffSgst);
					summaryRespd3.setDiffCess(diffCess);

					osRevSummaryRespList.add(summaryRespd3);
				});
			}

		}

		List<Ret1SummaryRespDto> sezDtaSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto sezDta : list) {

			Ret1BasicSectionSummaryDto sezDtaList = sezDta.getD_3D4();
			List<Ret1SummarySectionDto> sezDtaListEySummary = sezDtaList
					.getEySummary();

			if (sezDtaListEySummary != null) {
				sezDtaListEySummary.forEach(sezDtaSummary -> {
					Ret1SummaryRespDto summaryRespd4 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespd4.setTable(sezDtaSummary.getTable());
					summaryRespd4.setSection("4");
					summaryRespd4.setSupplyType(sezDtaSummary.getSupplyType());
					summaryRespd4.setAspValue(sezDtaSummary.getTaxableValue());
					summaryRespd4.setAspIgst(sezDtaSummary.getAspIgst());
					summaryRespd4.setAspSgst(sezDtaSummary.getAspSgst());
					summaryRespd4.setAspCgst(sezDtaSummary.getAspCgst());
					summaryRespd4.setAspCess(sezDtaSummary.getAspCess());
					
					summaryRespd4.setUsrValue(sezDtaSummary.getUsrValue());
					summaryRespd4.setUsrSgst(sezDtaSummary.getUsrSgst());
					summaryRespd4.setUsrIgst(sezDtaSummary.getUsrIgst());
					summaryRespd4.setUsrCgst(sezDtaSummary.getUsrCgst());
					summaryRespd4.setUsrCess(sezDtaSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespd4.setGstnValue(sezDtaSummary.getGstnValue());
					summaryRespd4.setGstnIgst(sezDtaSummary.getGstnIgst());
					summaryRespd4.setGstnSgst(sezDtaSummary.getGstnSgst());
					summaryRespd4.setGstnCgst(sezDtaSummary.getGstnCgst());
					summaryRespd4.setGstnCess(sezDtaSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespd4.getAspValue(),
							summaryRespd4.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespd4.getAspSgst(),
							summaryRespd4.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespd4.getAspIgst(),
							summaryRespd4.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespd4.getAspCgst(),
							summaryRespd4.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespd4.getAspCess(),
							summaryRespd4.getGstnCess());

					summaryRespd4.setDiffValue(diffInv);
					summaryRespd4.setDiffIgst(diffIgst);
					summaryRespd4.setDiffCgst(diffCgst);
					summaryRespd4.setDiffSgst(diffSgst);
					summaryRespd4.setDiffCess(diffCess);

					sezDtaSummaryRespList.add(summaryRespd4);
				});
			}

		}

		List<Ret1SummaryRespDto> totalSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto total : list) {

			Ret1BasicSectionSummaryDto totalList = total.getE_3E();
			List<Ret1SummarySectionDto> totalValueListEySummary = totalList
					.getEySummary();

			if (totalValueListEySummary != null) {
				totalValueListEySummary.forEach(totalValueSummary -> {
					Ret1SummaryRespDto summaryRespE = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespE.setTable(totalValueSummary.getTable());
					summaryRespE
							.setSupplyType(totalValueSummary.getSupplyType());
					summaryRespE
							.setAspValue(totalValueSummary.getTaxableValue());
					summaryRespE.setAspIgst(totalValueSummary.getAspIgst());
					summaryRespE.setAspSgst(totalValueSummary.getAspSgst());
					summaryRespE.setAspCgst(totalValueSummary.getAspCgst());
					summaryRespE.setAspCess(totalValueSummary.getAspCess());
					
					summaryRespE.setUsrValue(totalValueSummary.getUsrValue());
					summaryRespE.setUsrSgst(totalValueSummary.getUsrValue());
					summaryRespE.setUsrIgst(totalValueSummary.getUsrIgst());
					summaryRespE.setUsrCgst(totalValueSummary.getUsrCgst());
					summaryRespE.setUsrCess(totalValueSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespE.setGstnValue(totalValueSummary.getGstnValue());
					summaryRespE.setGstnIgst(totalValueSummary.getGstnIgst());
					summaryRespE.setGstnSgst(totalValueSummary.getGstnSgst());
					summaryRespE.setGstnCgst(totalValueSummary.getGstnCgst());
					summaryRespE.setGstnCess(totalValueSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespE.getAspValue(),
							summaryRespE.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespE.getAspSgst(),
							summaryRespE.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespE.getAspIgst(),
							summaryRespE.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespE.getAspCgst(),
							summaryRespE.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespE.getAspCess(),
							summaryRespE.getGstnCess());

					summaryRespE.setDiffValue(diffInv);
					summaryRespE.setDiffIgst(diffIgst);
					summaryRespE.setDiffCgst(diffCgst);
					summaryRespE.setDiffSgst(diffSgst);
					summaryRespE.setDiffCess(diffCess);

					totalSummaryRespList.add(summaryRespE);
				});
			}

		}

		List<Ret1SummaryRespDto> a4SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto a4 : list) {

			Ret1BasicSectionSummaryDto a4List = a4.getA_4A();
			List<Ret1SummarySectionDto> a4ListEySummary = a4List.getEySummary();

			if (a4ListEySummary != null) {
				a4ListEySummary.forEach(a4Summary -> {
					Ret1SummaryRespDto summaryResp4 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4.setTable(a4Summary.getTable());
					summaryResp4.setSupplyType(a4Summary.getSupplyType());
					summaryResp4.setAspValue(a4Summary.getTaxableValue());
					summaryResp4.setAspIgst(a4Summary.getAspIgst());
					summaryResp4.setAspSgst(a4Summary.getAspSgst());
					summaryResp4.setAspCgst(a4Summary.getAspCgst());
					summaryResp4.setAspCess(a4Summary.getAspCess());
					
					summaryResp4.setUsrValue(a4Summary.getUsrValue());
					summaryResp4.setUsrSgst(a4Summary.getUsrSgst());
					summaryResp4.setUsrIgst(a4Summary.getUsrIgst());
					summaryResp4.setUsrCgst(a4Summary.getUsrCgst());
					summaryResp4.setUsrCess(a4Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4.setGstnValue(a4Summary.getGstnValue());
					summaryResp4.setGstnIgst(a4Summary.getGstnIgst());
					summaryResp4.setGstnSgst(a4Summary.getGstnSgst());
					summaryResp4.setGstnCgst(a4Summary.getGstnCgst());
					summaryResp4.setGstnCess(a4Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4.getAspValue(),
							summaryResp4.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4.getAspSgst(),
							summaryResp4.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4.getAspIgst(),
							summaryResp4.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4.getAspCgst(),
							summaryResp4.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4.getAspCess(),
							summaryResp4.getGstnCess());

					summaryResp4.setDiffValue(diffInv);
					summaryResp4.setDiffIgst(diffIgst);
					summaryResp4.setDiffCgst(diffCgst);
					summaryResp4.setDiffSgst(diffSgst);
					summaryResp4.setDiffCess(diffCess);

					a4SummaryRespList.add(summaryResp4);
				});
			}

		}

		List<Ret1SummaryRespDto> itcRejSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcRej : list) {

			Ret1BasicSectionSummaryDto itcRejList = itcRej.getA_4A1();
			List<Ret1SummarySectionDto> itcRejListEySummary = itcRejList
					.getEySummary();

			if (itcRejListEySummary != null) {
				itcRejListEySummary.forEach(itcRejSummary -> {
					Ret1SummaryRespDto summaryResp4a1 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a1.setTable(itcRejSummary.getTable());
					summaryResp4a1.setSection("1");
					summaryResp4a1.setSupplyType(itcRejSummary.getSupplyType());
					summaryResp4a1.setAspValue(itcRejSummary.getTaxableValue());
					summaryResp4a1.setAspIgst(itcRejSummary.getAspIgst());
					summaryResp4a1.setAspSgst(itcRejSummary.getAspSgst());
					summaryResp4a1.setAspCgst(itcRejSummary.getAspCgst());
					summaryResp4a1.setAspCess(itcRejSummary.getAspCess());
					
					summaryResp4a1.setUsrValue(itcRejSummary.getUsrValue());
					summaryResp4a1.setUsrSgst(itcRejSummary.getUsrSgst());
					summaryResp4a1.setUsrIgst(itcRejSummary.getUsrIgst());
					summaryResp4a1.setUsrCgst(itcRejSummary.getUsrCgst());
					summaryResp4a1.setUsrCess(itcRejSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a1.setGstnValue(itcRejSummary.getGstnValue());
					summaryResp4a1.setGstnIgst(itcRejSummary.getGstnIgst());
					summaryResp4a1.setGstnSgst(itcRejSummary.getGstnSgst());
					summaryResp4a1.setGstnCgst(itcRejSummary.getGstnCgst());
					summaryResp4a1.setGstnCess(itcRejSummary.getGstnCess());					
					// Diff
				
					BigDecimal diffInv = subMethod(summaryResp4a1.getAspValue(),
							summaryResp4a1.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a1.getAspSgst(),
							summaryResp4a1.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a1.getAspIgst(),
							summaryResp4a1.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a1.getAspCgst(),
							summaryResp4a1.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a1.getAspCess(),
							summaryResp4a1.getGstnCess());

					summaryResp4a1.setDiffValue(diffInv);
					summaryResp4a1.setDiffIgst(diffIgst);
					summaryResp4a1.setDiffCgst(diffCgst);
					summaryResp4a1.setDiffSgst(diffSgst);
					summaryResp4a1.setDiffCess(diffCess);

					itcRejSummaryRespList.add(summaryResp4a1);
				});
			}

		}
		List<Ret1SummaryRespDto> itcPenSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcPen : list) {

			Ret1BasicSectionSummaryDto itcPenList = itcPen.getA_4A2();
			List<Ret1SummarySectionDto> itcPenListEySummary = itcPenList
					.getEySummary();

			if (itcPenListEySummary != null) {
				itcPenListEySummary.forEach(itcPenSummary -> {
					Ret1SummaryRespDto summaryResp4a2 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a2.setTable(itcPenSummary.getTable());
					summaryResp4a2.setSection("2");
					summaryResp4a2.setSupplyType(itcPenSummary.getSupplyType());
					summaryResp4a2.setAspValue(itcPenSummary.getTaxableValue());
					summaryResp4a2.setAspIgst(itcPenSummary.getAspIgst());
					summaryResp4a2.setAspSgst(itcPenSummary.getAspSgst());
					summaryResp4a2.setAspCgst(itcPenSummary.getAspCgst());
					summaryResp4a2.setAspCess(itcPenSummary.getAspCess());
					
					summaryResp4a2.setUsrValue(itcPenSummary.getUsrValue());
					summaryResp4a2.setUsrSgst(itcPenSummary.getUsrSgst());
					summaryResp4a2.setUsrIgst(itcPenSummary.getUsrIgst());
					summaryResp4a2.setUsrCgst(itcPenSummary.getUsrCgst());
					summaryResp4a2.setUsrCess(itcPenSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();

					summaryResp4a2.setGstnValue(itcPenSummary.getGstnValue());
					summaryResp4a2.setGstnIgst(itcPenSummary.getGstnIgst());
					summaryResp4a2.setGstnSgst(itcPenSummary.getGstnSgst());
					summaryResp4a2.setGstnCgst(itcPenSummary.getGstnCgst());
					summaryResp4a2.setGstnCess(itcPenSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a2.getAspValue(),
							summaryResp4a2.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a2.getAspSgst(),
							summaryResp4a2.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a2.getAspIgst(),
							summaryResp4a2.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a2.getAspCgst(),
							summaryResp4a2.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a2.getAspCess(),
							summaryResp4a2.getGstnCess());

					summaryResp4a2.setDiffValue(diffInv);
					summaryResp4a2.setDiffIgst(diffIgst);
					summaryResp4a2.setDiffCgst(diffCgst);
					summaryResp4a2.setDiffSgst(diffSgst);
					summaryResp4a2.setDiffCess(diffCess);

					itcPenSummaryRespList.add(summaryResp4a2);
				});
			}

		}

		List<Ret1SummaryRespDto> itcAccSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcAcc : list) {

			Ret1BasicSectionSummaryDto itcAccList = itcAcc.getA_4A3();
			List<Ret1SummarySectionDto> itcAccListEySummary = itcAccList
					.getEySummary();

			if (itcAccListEySummary != null) {
				itcAccListEySummary.forEach(itcAccSummary -> {
					Ret1SummaryRespDto summaryResp4a3 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a3.setTable(itcAccSummary.getTable());
					summaryResp4a3.setSection("3");
					summaryResp4a3.setSupplyType(itcAccSummary.getSupplyType());
					summaryResp4a3.setAspValue(itcAccSummary.getTaxableValue());
					summaryResp4a3.setAspIgst(itcAccSummary.getAspIgst());
					summaryResp4a3.setAspSgst(itcAccSummary.getAspSgst());
					summaryResp4a3.setAspCgst(itcAccSummary.getAspCgst());
					summaryResp4a3.setAspCess(itcAccSummary.getAspCess());
					
					summaryResp4a3.setUsrValue(itcAccSummary.getUsrValue());
					summaryResp4a3.setUsrSgst(itcAccSummary.getUsrSgst());
					summaryResp4a3.setUsrIgst(itcAccSummary.getUsrIgst());
					summaryResp4a3.setUsrCgst(itcAccSummary.getUsrCgst());
					summaryResp4a3.setUsrCess(itcAccSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();

					summaryResp4a3.setGstnValue(itcAccSummary.getGstnValue());
					summaryResp4a3.setGstnIgst(itcAccSummary.getGstnIgst());
					summaryResp4a3.setGstnSgst(itcAccSummary.getGstnSgst());
					summaryResp4a3.setGstnCgst(itcAccSummary.getGstnCgst());
					summaryResp4a3.setGstnCess(itcAccSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a3.getAspValue(),
							summaryResp4a3.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a3.getAspSgst(),
							summaryResp4a3.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a3.getAspIgst(),
							summaryResp4a3.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a3.getAspCgst(),
							summaryResp4a3.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a3.getAspCess(),
							summaryResp4a3.getGstnCess());

					summaryResp4a3.setDiffValue(diffInv);
					summaryResp4a3.setDiffIgst(diffIgst);
					summaryResp4a3.setDiffCgst(diffCgst);
					summaryResp4a3.setDiffSgst(diffSgst);
					summaryResp4a3.setDiffCess(diffCess);

					itcAccSummaryRespList.add(summaryResp4a3);
				});
			}

		}

		List<Ret1SummaryRespDto> itcEligibleSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcElig : list) {

			Ret1BasicSectionSummaryDto itcEligList = itcElig.getA_4A4();
			List<Ret1SummarySectionDto> itcEligListEySummary = itcEligList
					.getEySummary();

			if (itcEligListEySummary != null) {
				itcEligListEySummary.forEach(itcEligSummary -> {
					Ret1SummaryRespDto summaryResp4a4 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a4.setTable(itcEligSummary.getTable());
					summaryResp4a4.setSection("4");
					summaryResp4a4
							.setSupplyType(itcEligSummary.getSupplyType());
					summaryResp4a4
							.setAspValue(itcEligSummary.getTaxableValue());
					summaryResp4a4.setAspIgst(itcEligSummary.getAspIgst());
					summaryResp4a4.setAspSgst(itcEligSummary.getAspSgst());
					summaryResp4a4.setAspCgst(itcEligSummary.getAspCgst());
					summaryResp4a4.setAspCess(itcEligSummary.getAspCess());
					
					summaryResp4a4.setUsrValue(itcEligSummary.getUsrValue());
					summaryResp4a4.setUsrSgst(itcEligSummary.getUsrSgst());
					summaryResp4a4.setUsrIgst(itcEligSummary.getUsrIgst());
					summaryResp4a4.setUsrCgst(itcEligSummary.getUsrCgst());
					summaryResp4a4.setUsrCess(itcEligSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();

					summaryResp4a4.setGstnValue(itcEligSummary.getGstnValue());
					summaryResp4a4.setGstnIgst(itcEligSummary.getGstnIgst());
					summaryResp4a4.setGstnSgst(itcEligSummary.getGstnSgst());
					summaryResp4a4.setGstnCgst(itcEligSummary.getGstnCgst());
					summaryResp4a4.setGstnCess(itcEligSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a4.getAspValue(),
							summaryResp4a4.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a4.getAspSgst(),
							summaryResp4a4.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a4.getAspIgst(),
							summaryResp4a4.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a4.getAspCgst(),
							summaryResp4a4.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a4.getAspCess(),
							summaryResp4a4.getGstnCess());

					summaryResp4a4.setDiffValue(diffInv);
					summaryResp4a4.setDiffIgst(diffIgst);
					summaryResp4a4.setDiffCgst(diffCgst);
					summaryResp4a4.setDiffSgst(diffSgst);
					summaryResp4a4.setDiffCess(diffCess);

					itcEligibleSummaryRespList.add(summaryResp4a4);
				});
			}

		}

		List<Ret1SummaryRespDto> itcInwRevSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcInwRev : list) {

			Ret1BasicSectionSummaryDto itcRevList = itcInwRev.getA_4A5();
			List<Ret1SummarySectionDto> itcInREvListEySummary = itcRevList
					.getEySummary();

			if (itcInREvListEySummary != null) {
				itcInREvListEySummary.forEach(itcInREvSummary -> {
					Ret1SummaryRespDto summaryResp4a5 = new Ret1SummaryRespDto();
					// EY Resp
					if("rcInward".equalsIgnoreCase(itcInREvSummary.getSupplyType())){
					summaryResp4a5.setTable(itcInREvSummary.getTable());
					summaryResp4a5.setSection("5");
					summaryResp4a5
							.setSupplyType(itcInREvSummary.getSupplyType());
					summaryResp4a5
							.setAspValue(itcInREvSummary.getTaxableValue());
					summaryResp4a5.setAspIgst(itcInREvSummary.getAspIgst());
					summaryResp4a5.setAspSgst(itcInREvSummary.getAspSgst());
					summaryResp4a5.setAspCgst(itcInREvSummary.getAspCgst());
					summaryResp4a5.setAspCess(itcInREvSummary.getAspCess());
					
					summaryResp4a5.setUsrValue(itcInREvSummary.getUsrValue());
					summaryResp4a5.setUsrSgst(itcInREvSummary.getUsrSgst());
					summaryResp4a5.setUsrIgst(itcInREvSummary.getUsrIgst());
					summaryResp4a5.setUsrCgst(itcInREvSummary.getUsrCgst());
					summaryResp4a5.setUsrCess(itcInREvSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a5.setGstnValue(itcInREvSummary.getGstnValue());
					summaryResp4a5.setGstnIgst(itcInREvSummary.getGstnIgst());
					summaryResp4a5.setGstnSgst(itcInREvSummary.getGstnSgst());
					summaryResp4a5.setGstnCgst(itcInREvSummary.getGstnCgst());
					summaryResp4a5.setGstnCess(itcInREvSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a5.getAspValue(),
							summaryResp4a5.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a5.getAspSgst(),
							summaryResp4a5.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a5.getAspIgst(),
							summaryResp4a5.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a5.getAspCgst(),
							summaryResp4a5.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a5.getAspCess(),
							summaryResp4a5.getGstnCess());

					summaryResp4a5.setDiffValue(diffInv);
					summaryResp4a5.setDiffIgst(diffIgst);
					summaryResp4a5.setDiffCgst(diffCgst);
					summaryResp4a5.setDiffSgst(diffSgst);
					summaryResp4a5.setDiffCess(diffCess);

					itcInwRevSummaryRespList.add(summaryResp4a5);
					}
				});
			}

		}

		List<Ret1SummaryRespDto> itcImpsSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcImps : list) {

			Ret1BasicSectionSummaryDto itcImpsList = itcImps.getA_4A6();
			List<Ret1SummarySectionDto> itcImpsListEySummary = itcImpsList
					.getEySummary();

			if (itcImpsListEySummary != null) {
				itcImpsListEySummary.forEach(itcImpsSummary -> {
					Ret1SummaryRespDto summaryResp4a6 = new Ret1SummaryRespDto();
					// EY Resp
					if("imps".equalsIgnoreCase(itcImpsSummary.getSupplyType())){
					summaryResp4a6.setTable(itcImpsSummary.getTable());
					summaryResp4a6.setSection("6");
					summaryResp4a6
							.setSupplyType(itcImpsSummary.getSupplyType());
					summaryResp4a6
							.setAspValue(itcImpsSummary.getTaxableValue());
					summaryResp4a6.setAspIgst(itcImpsSummary.getAspIgst());
					summaryResp4a6.setAspSgst(itcImpsSummary.getAspSgst());
					summaryResp4a6.setAspCgst(itcImpsSummary.getAspCgst());
					summaryResp4a6.setAspCess(itcImpsSummary.getAspCess());
					
					summaryResp4a6.setUsrValue(itcImpsSummary.getUsrValue());
					summaryResp4a6.setUsrSgst(itcImpsSummary.getUsrSgst());
					summaryResp4a6.setUsrIgst(itcImpsSummary.getUsrIgst());
					summaryResp4a6.setUsrCgst(itcImpsSummary.getUsrCgst());
					summaryResp4a6.setUsrCess(itcImpsSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a6.setGstnValue(itcImpsSummary.getGstnValue());
					summaryResp4a6.setGstnIgst(itcImpsSummary.getGstnIgst());
					summaryResp4a6.setGstnSgst(itcImpsSummary.getGstnSgst());
					summaryResp4a6.setGstnCgst(itcImpsSummary.getGstnCgst());
					summaryResp4a6.setGstnCess(itcImpsSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a6.getAspValue(),
							summaryResp4a6.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a6.getAspSgst(),
							summaryResp4a6.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a6.getAspIgst(),
							summaryResp4a6.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a6.getAspCgst(),
							summaryResp4a6.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a6.getAspCess(),
							summaryResp4a6.getGstnCess());

					summaryResp4a6.setDiffValue(diffInv);
					summaryResp4a6.setDiffIgst(diffIgst);
					summaryResp4a6.setDiffCgst(diffCgst);
					summaryResp4a6.setDiffSgst(diffSgst);
					summaryResp4a6.setDiffCess(diffCess);

					itcImpsSummaryRespList.add(summaryResp4a6);
					}
				});
			}

		}

		List<Ret1SummaryRespDto> itcImpgSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcImpg : list) {

			Ret1BasicSectionSummaryDto itcImpgList = itcImpg.getA_4A7();
			List<Ret1SummarySectionDto> itcImpgListEySummary = itcImpgList
					.getEySummary();

			if (itcImpgListEySummary != null) {
				itcImpgListEySummary.forEach(itcImpgSummary -> {
					Ret1SummaryRespDto summaryResp4a7 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a7.setTable(itcImpgSummary.getTable());
					summaryResp4a7.setSection("7");
					summaryResp4a7
							.setSupplyType(itcImpgSummary.getSupplyType());
					summaryResp4a7
							.setAspValue(itcImpgSummary.getTaxableValue());
					summaryResp4a7.setAspIgst(itcImpgSummary.getAspIgst());
					summaryResp4a7.setAspSgst(itcImpgSummary.getAspSgst());
					summaryResp4a7.setAspCgst(itcImpgSummary.getAspCgst());
					summaryResp4a7.setAspCess(itcImpgSummary.getAspCess());
					
					summaryResp4a7.setUsrValue(itcImpgSummary.getUsrValue());
					summaryResp4a7.setUsrSgst(itcImpgSummary.getUsrSgst());
					summaryResp4a7.setUsrIgst(itcImpgSummary.getUsrIgst());
					summaryResp4a7.setUsrCgst(itcImpgSummary.getUsrCgst());
					summaryResp4a7.setUsrCess(itcImpgSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a7.setGstnValue(itcImpgSummary.getGstnValue());
					summaryResp4a7.setGstnIgst(itcImpgSummary.getGstnIgst());
					summaryResp4a7.setGstnSgst(itcImpgSummary.getGstnSgst());
					summaryResp4a7.setGstnCgst(itcImpgSummary.getGstnCgst());
					summaryResp4a7.setGstnCess(itcImpgSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a7.getAspValue(),
							summaryResp4a7.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a7.getAspSgst(),
							summaryResp4a7.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a7.getAspIgst(),
							summaryResp4a7.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a7.getAspCgst(),
							summaryResp4a7.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a7.getAspCess(),
							summaryResp4a7.getGstnCess());

					summaryResp4a7.setDiffValue(diffInv);
					summaryResp4a7.setDiffIgst(diffIgst);
					summaryResp4a7.setDiffCgst(diffCgst);
					summaryResp4a7.setDiffSgst(diffSgst);
					summaryResp4a7.setDiffCess(diffCess);

					itcImpgSummaryRespList.add(summaryResp4a7);
				});
			}

		}
		List<Ret1SummaryRespDto> itcImpgSezSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcImpgSez : list) {

			Ret1BasicSectionSummaryDto itcImpgSezList = itcImpgSez.getA_4A8();
			List<Ret1SummarySectionDto> itcImpgSezListEySummary = itcImpgSezList
					.getEySummary();

			if (itcImpgSezListEySummary != null) {
				itcImpgSezListEySummary.forEach(itcImpgSezSummary -> {
					Ret1SummaryRespDto summaryResp4a8 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a8.setTable(itcImpgSezSummary.getTable());
					summaryResp4a8.setSection("8");
					summaryResp4a8
							.setSupplyType(itcImpgSezSummary.getSupplyType());
					summaryResp4a8
							.setAspValue(itcImpgSezSummary.getTaxableValue());
					summaryResp4a8.setAspIgst(itcImpgSezSummary.getAspIgst());
					summaryResp4a8.setAspSgst(itcImpgSezSummary.getAspSgst());
					summaryResp4a8.setAspCgst(itcImpgSezSummary.getAspCgst());
					summaryResp4a8.setAspCess(itcImpgSezSummary.getAspCess());
					
					summaryResp4a8.setUsrValue(itcImpgSezSummary.getUsrValue());
					summaryResp4a8.setUsrSgst(itcImpgSezSummary.getUsrSgst());
					summaryResp4a8.setUsrIgst(itcImpgSezSummary.getUsrIgst());
					summaryResp4a8.setUsrCgst(itcImpgSezSummary.getUsrCgst());
					summaryResp4a8.setUsrCess(itcImpgSezSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a8.setGstnValue(itcImpgSezSummary.getGstnValue());
					summaryResp4a8.setGstnIgst(itcImpgSezSummary.getGstnIgst());
					summaryResp4a8.setGstnSgst(itcImpgSezSummary.getGstnSgst());
					summaryResp4a8.setGstnCgst(itcImpgSezSummary.getGstnCgst());
					summaryResp4a8.setGstnCess(itcImpgSezSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a8.getAspValue(),
							summaryResp4a8.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a8.getAspSgst(),
							summaryResp4a8.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a8.getAspIgst(),
							summaryResp4a8.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a8.getAspCgst(),
							summaryResp4a8.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a8.getAspCess(),
							summaryResp4a8.getGstnCess());

					summaryResp4a8.setDiffValue(diffInv);
					summaryResp4a8.setDiffIgst(diffIgst);
					summaryResp4a8.setDiffCgst(diffCgst);
					summaryResp4a8.setDiffSgst(diffSgst);
					summaryResp4a8.setDiffCess(diffCess);

					itcImpgSezSummaryRespList.add(summaryResp4a8);
				});
			}

		}

		List<Ret1SummaryRespDto> itcIsdSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcIsd : list) {

			Ret1BasicSectionSummaryDto itcIsdList = itcIsd.getA_4A9();
			List<Ret1SummarySectionDto> itcIsdListEySummary = itcIsdList
					.getEySummary();

			if (itcIsdListEySummary != null) {
				itcIsdListEySummary.forEach(itcIsdSummary -> {
					Ret1SummaryRespDto summaryResp4a9 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a9.setTable(itcIsdSummary.getTable());
					summaryResp4a9.setSection("9");
					summaryResp4a9.setSupplyType(itcIsdSummary.getSupplyType());
					summaryResp4a9.setAspValue(itcIsdSummary.getTaxableValue());
					summaryResp4a9.setAspIgst(itcIsdSummary.getAspIgst());
					summaryResp4a9.setAspSgst(itcIsdSummary.getAspSgst());
					summaryResp4a9.setAspCgst(itcIsdSummary.getAspCgst());
					summaryResp4a9.setAspCess(itcIsdSummary.getAspCess());
					
					summaryResp4a9.setUsrValue(itcIsdSummary.getUsrValue());
					summaryResp4a9.setUsrSgst(itcIsdSummary.getUsrSgst());
					summaryResp4a9.setUsrIgst(itcIsdSummary.getUsrIgst());
					summaryResp4a9.setUsrCgst(itcIsdSummary.getUsrCgst());
					summaryResp4a9.setUsrCess(itcIsdSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a9.setGstnValue(itcIsdSummary.getGstnValue());
					summaryResp4a9.setGstnIgst(itcIsdSummary.getGstnIgst());
					summaryResp4a9.setGstnSgst(itcIsdSummary.getGstnSgst());
					summaryResp4a9.setGstnCgst(itcIsdSummary.getGstnCgst());
					summaryResp4a9.setGstnCess(itcIsdSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4a9.getAspValue(),
							summaryResp4a9.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4a9.getAspSgst(),
							summaryResp4a9.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4a9.getAspIgst(),
							summaryResp4a9.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4a9.getAspCgst(),
							summaryResp4a9.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4a9.getAspCess(),
							summaryResp4a9.getGstnCess());

					summaryResp4a9.setDiffValue(diffInv);
					summaryResp4a9.setDiffIgst(diffIgst);
					summaryResp4a9.setDiffCgst(diffCgst);
					summaryResp4a9.setDiffSgst(diffSgst);
					summaryResp4a9.setDiffCess(diffCess);

					itcIsdSummaryRespList.add(summaryResp4a9);
				});
			}

		}

		List<Ret1SummaryRespDto> itcProSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcPro : list) {

			Ret1BasicSectionSummaryDto itcProList = itcPro.getA_4A10();
			List<Ret1SummarySectionDto> itcProListEySummary = itcProList
					.getEySummary();

			if (itcProListEySummary != null) {
				itcProListEySummary.forEach(itcProSummary -> {
					Ret1SummaryRespDto summaryResp4a10 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a10.setTable(itcProSummary.getTable());
					summaryResp4a10.setSection("10");
					summaryResp4a10
							.setSupplyType(itcProSummary.getSupplyType());
					summaryResp4a10
							.setAspValue(itcProSummary.getTaxableValue());
					summaryResp4a10.setAspIgst(itcProSummary.getAspIgst());
					summaryResp4a10.setAspSgst(itcProSummary.getAspSgst());
					summaryResp4a10.setAspCgst(itcProSummary.getAspCgst());
					summaryResp4a10.setAspCess(itcProSummary.getAspCess());
					
					summaryResp4a10.setUsrValue(itcProSummary.getUsrValue());
					summaryResp4a10.setUsrSgst(itcProSummary.getUsrSgst());
					summaryResp4a10.setUsrIgst(itcProSummary.getUsrIgst());
					summaryResp4a10.setUsrCgst(itcProSummary.getUsrCgst());
					summaryResp4a10.setUsrCess(itcProSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a10.setGstnValue(itcProSummary.getGstnValue());
					summaryResp4a10.setGstnIgst(itcProSummary.getGstnIgst());
					summaryResp4a10.setGstnSgst(itcProSummary.getGstnSgst());
					summaryResp4a10.setGstnCgst(itcProSummary.getGstnCgst());
					summaryResp4a10.setGstnCess(itcProSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(
							summaryResp4a10.getAspValue(),
							summaryResp4a10.getGstnValue());
					BigDecimal diffSgst = subMethod(
							summaryResp4a10.getAspSgst(),
							summaryResp4a10.getGstnSgst());
					BigDecimal diffIgst = subMethod(
							summaryResp4a10.getAspIgst(),
							summaryResp4a10.getGstnIgst());
					BigDecimal diffCgst = subMethod(
							summaryResp4a10.getAspCgst(),
							summaryResp4a10.getGstnCgst());
					BigDecimal diffCess = subMethod(
							summaryResp4a10.getAspCess(),
							summaryResp4a10.getGstnCess());

					summaryResp4a10.setDiffValue(diffInv);
					summaryResp4a10.setDiffIgst(diffIgst);
					summaryResp4a10.setDiffCgst(diffCgst);
					summaryResp4a10.setDiffSgst(diffSgst);
					summaryResp4a10.setDiffCess(diffCess);

					itcProSummaryRespList.add(summaryResp4a10);
				});
			}

		}

		List<Ret1SummaryRespDto> itcAdjSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto itcAdj : list) {

			Ret1BasicSectionSummaryDto itcAdjList = itcAdj.getA_4A11();
			List<Ret1SummarySectionDto> itcAdjListEySummary = itcAdjList
					.getEySummary();

			if (itcAdjListEySummary != null) {
				itcAdjListEySummary.forEach(itcAdjSummary -> {
					Ret1SummaryRespDto summaryResp4a11 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4a11.setTable(itcAdjSummary.getTable());
					summaryResp4a11.setSection("11");
					summaryResp4a11
							.setSupplyType(itcAdjSummary.getSupplyType());
					summaryResp4a11
							.setAspValue(itcAdjSummary.getTaxableValue());
					summaryResp4a11.setAspIgst(itcAdjSummary.getAspIgst());
					summaryResp4a11.setAspSgst(itcAdjSummary.getAspSgst());
					summaryResp4a11.setAspCgst(itcAdjSummary.getAspCgst());
					summaryResp4a11.setAspCess(itcAdjSummary.getAspCess());
					
					summaryResp4a11.setUsrValue(itcAdjSummary.getUsrValue());
					summaryResp4a11.setUsrSgst(itcAdjSummary.getUsrSgst());
					summaryResp4a11.setUsrIgst(itcAdjSummary.getUsrIgst());
					summaryResp4a11.setUsrCgst(itcAdjSummary.getUsrCgst());
					summaryResp4a11.setUsrCess(itcAdjSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4a11.setGstnValue(itcAdjSummary.getGstnValue());
					summaryResp4a11.setGstnIgst(itcAdjSummary.getGstnIgst());
					summaryResp4a11.setGstnSgst(itcAdjSummary.getGstnSgst());
					summaryResp4a11.setGstnCgst(itcAdjSummary.getGstnCgst());
					summaryResp4a11.setGstnCess(itcAdjSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(
							summaryResp4a11.getAspValue(),
							summaryResp4a11.getGstnValue());
					BigDecimal diffSgst = subMethod(
							summaryResp4a11.getAspSgst(),
							summaryResp4a11.getGstnSgst());
					BigDecimal diffIgst = subMethod(
							summaryResp4a11.getAspIgst(),
							summaryResp4a11.getGstnIgst());
					BigDecimal diffCgst = subMethod(
							summaryResp4a11.getAspCgst(),
							summaryResp4a11.getGstnCgst());
					BigDecimal diffCess = subMethod(
							summaryResp4a11.getAspCess(),
							summaryResp4a11.getGstnCess());

					summaryResp4a11.setDiffValue(diffInv);
					summaryResp4a11.setDiffIgst(diffIgst);
					summaryResp4a11.setDiffCgst(diffCgst);
					summaryResp4a11.setDiffSgst(diffSgst);
					summaryResp4a11.setDiffCess(diffCess);

					itcAdjSummaryRespList.add(summaryResp4a11);
				});
			}

		}

		List<Ret1SummaryRespDto> b4SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto b4 : list) {

			Ret1BasicSectionSummaryDto b4List = b4.getB_4B();
			List<Ret1SummarySectionDto> b4ListEySummary = b4List.getEySummary();

			if (b4ListEySummary != null) {
				b4ListEySummary.forEach(b4Summary -> {
					Ret1SummaryRespDto summaryRespb4 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespb4.setTable(b4Summary.getTable());
					summaryRespb4.setSupplyType(b4Summary.getSupplyType());
					summaryRespb4.setAspValue(b4Summary.getTaxableValue());
					summaryRespb4.setAspIgst(b4Summary.getAspIgst());
					summaryRespb4.setAspSgst(b4Summary.getAspSgst());
					summaryRespb4.setAspCgst(b4Summary.getAspCgst());
					summaryRespb4.setAspCess(b4Summary.getAspCess());
					
					summaryRespb4.setUsrValue(b4Summary.getUsrValue());
					summaryRespb4.setUsrSgst(b4Summary.getUsrSgst());
					summaryRespb4.setUsrIgst(b4Summary.getUsrIgst());
					summaryRespb4.setUsrCgst(b4Summary.getUsrCgst());
					summaryRespb4.setUsrCess(b4Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespb4.setGstnValue(b4Summary.getGstnValue());
					summaryRespb4.setGstnIgst(b4Summary.getGstnIgst());
					summaryRespb4.setGstnSgst(b4Summary.getGstnSgst());
					summaryRespb4.setGstnCgst(b4Summary.getGstnCgst());
					summaryRespb4.setGstnCess(b4Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespb4.getAspValue(),
							summaryRespb4.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespb4.getAspSgst(),
							summaryRespb4.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespb4.getAspIgst(),
							summaryRespb4.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespb4.getAspCgst(),
							summaryRespb4.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespb4.getAspCess(),
							summaryRespb4.getGstnCess());

					summaryRespb4.setDiffValue(diffInv);
					summaryRespb4.setDiffIgst(diffIgst);
					summaryRespb4.setDiffCgst(diffCgst);
					summaryRespb4.setDiffSgst(diffSgst);
					summaryRespb4.setDiffCess(diffCess);

					b4SummaryRespList.add(summaryRespb4);
				});
			}

		}
		List<Ret1SummaryRespDto> rej4b1SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4b1 : list) {

			Ret1BasicSectionSummaryDto itcRejList = rej4b1.getB_4B1();
			List<Ret1SummarySectionDto> itcRejListEySummary = itcRejList
					.getEySummary();

			if (itcRejListEySummary != null) {
				itcRejListEySummary.forEach(itcRej4b1Summary -> {
					Ret1SummaryRespDto summaryRespb41 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespb41.setTable(itcRej4b1Summary.getTable());
					summaryRespb41.setSection("1");
					summaryRespb41
							.setSupplyType(itcRej4b1Summary.getSupplyType());
					summaryRespb41
							.setAspValue(itcRej4b1Summary.getTaxableValue());
					summaryRespb41.setAspIgst(itcRej4b1Summary.getAspIgst());
					summaryRespb41.setAspSgst(itcRej4b1Summary.getAspSgst());
					summaryRespb41.setAspCgst(itcRej4b1Summary.getAspCgst());
					summaryRespb41.setAspCess(itcRej4b1Summary.getAspCess());
					
					summaryRespb41.setUsrValue(itcRej4b1Summary.getUsrValue());
					summaryRespb41.setUsrSgst(itcRej4b1Summary.getUsrSgst());
					summaryRespb41.setUsrIgst(itcRej4b1Summary.getUsrIgst());
					summaryRespb41.setUsrCgst(itcRej4b1Summary.getUsrCgst());
					summaryRespb41.setUsrCess(itcRej4b1Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespb41.setGstnValue(itcRej4b1Summary.getGstnValue());
					summaryRespb41.setGstnIgst(itcRej4b1Summary.getGstnIgst());
					summaryRespb41.setGstnSgst(itcRej4b1Summary.getGstnSgst());
					summaryRespb41.setGstnCgst(itcRej4b1Summary.getGstnCgst());
					summaryRespb41.setGstnCess(itcRej4b1Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespb41.getAspValue(),
							summaryRespb41.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespb41.getAspSgst(),
							summaryRespb41.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespb41.getAspIgst(),
							summaryRespb41.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespb41.getAspCgst(),
							summaryRespb41.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespb41.getAspCess(),
							summaryRespb41.getGstnCess());

					summaryRespb41.setDiffValue(diffInv);
					summaryRespb41.setDiffIgst(diffIgst);
					summaryRespb41.setDiffCgst(diffCgst);
					summaryRespb41.setDiffSgst(diffSgst);
					summaryRespb41.setDiffCess(diffCess);

					rej4b1SummaryRespList.add(summaryRespb41);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4b2SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4b2 : list) {

			Ret1BasicSectionSummaryDto rej4b2List = rej4b2.getB_4B2();
			List<Ret1SummarySectionDto> rej4b2ListEySummary = rej4b2List
					.getEySummary();

			if (rej4b2ListEySummary != null) {
				rej4b2ListEySummary.forEach(itcRej4b2Summary -> {
					Ret1SummaryRespDto summaryRespb42 = new Ret1SummaryRespDto();
					// EY Resp
					summaryRespb42.setTable(itcRej4b2Summary.getTable());
					summaryRespb42.setSection("2");
					summaryRespb42
							.setSupplyType(itcRej4b2Summary.getSupplyType());
					summaryRespb42
							.setAspValue(itcRej4b2Summary.getTaxableValue());
					summaryRespb42.setAspIgst(itcRej4b2Summary.getAspIgst());
					summaryRespb42.setAspSgst(itcRej4b2Summary.getAspSgst());
					summaryRespb42.setAspCgst(itcRej4b2Summary.getAspCgst());
					summaryRespb42.setAspCess(itcRej4b2Summary.getAspCess());
					
					summaryRespb42.setUsrValue(itcRej4b2Summary.getUsrValue());
					summaryRespb42.setUsrSgst(itcRej4b2Summary.getUsrSgst());
					summaryRespb42.setUsrIgst(itcRej4b2Summary.getUsrIgst());
					summaryRespb42.setUsrCgst(itcRej4b2Summary.getUsrCgst());
					summaryRespb42.setUsrCess(itcRej4b2Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryRespb42.setGstnValue(itcRej4b2Summary.getGstnValue());
					summaryRespb42.setGstnIgst(itcRej4b2Summary.getGstnIgst());
					summaryRespb42.setGstnSgst(itcRej4b2Summary.getGstnSgst());
					summaryRespb42.setGstnCgst(itcRej4b2Summary.getGstnCgst());
					summaryRespb42.setGstnCess(itcRej4b2Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryRespb42.getAspValue(),
							summaryRespb42.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryRespb42.getAspSgst(),
							summaryRespb42.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryRespb42.getAspIgst(),
							summaryRespb42.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryRespb42.getAspCgst(),
							summaryRespb42.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryRespb42.getAspCess(),
							summaryRespb42.getGstnCess());

					summaryRespb42.setDiffValue(diffInv);
					summaryRespb42.setDiffIgst(diffIgst);
					summaryRespb42.setDiffCgst(diffCgst);
					summaryRespb42.setDiffSgst(diffSgst);
					summaryRespb42.setDiffCess(diffCess);

					rej4b2SummaryRespList.add(summaryRespb42);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4b3SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4b3 : list) {

			Ret1BasicSectionSummaryDto rej4b3List = rej4b3.getB_4B3();
			List<Ret1SummarySectionDto> rej4b3ListEySummary = rej4b3List
					.getEySummary();

			if (rej4b3ListEySummary != null) {
				rej4b3ListEySummary.forEach(itcRej4b3Summary -> {
					Ret1SummaryRespDto summaryResp4b3 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4b3.setTable(itcRej4b3Summary.getTable());
					summaryResp4b3.setSection("3");
					summaryResp4b3
							.setSupplyType(itcRej4b3Summary.getSupplyType());
					summaryResp4b3
							.setAspValue(itcRej4b3Summary.getTaxableValue());
					summaryResp4b3.setAspIgst(itcRej4b3Summary.getAspIgst());
					summaryResp4b3.setAspSgst(itcRej4b3Summary.getAspSgst());
					summaryResp4b3.setAspCgst(itcRej4b3Summary.getAspCgst());
					summaryResp4b3.setAspCess(itcRej4b3Summary.getAspCess());
					
					summaryResp4b3.setUsrValue(itcRej4b3Summary.getUsrValue());
					summaryResp4b3.setUsrSgst(itcRej4b3Summary.getUsrSgst());
					summaryResp4b3.setUsrIgst(itcRej4b3Summary.getUsrIgst());
					summaryResp4b3.setUsrCgst(itcRej4b3Summary.getUsrCgst());
					summaryResp4b3.setUsrCess(itcRej4b3Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4b3.setGstnValue(itcRej4b3Summary.getGstnValue());
					summaryResp4b3.setGstnIgst(itcRej4b3Summary.getGstnIgst());
					summaryResp4b3.setGstnSgst(itcRej4b3Summary.getGstnSgst());
					summaryResp4b3.setGstnCgst(itcRej4b3Summary.getGstnCgst());
					summaryResp4b3.setGstnCess(itcRej4b3Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4b3.getAspValue(),
							summaryResp4b3.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4b3.getAspSgst(),
							summaryResp4b3.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4b3.getAspIgst(),
							summaryResp4b3.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4b3.getAspCgst(),
							summaryResp4b3.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4b3.getAspCess(),
							summaryResp4b3.getGstnCess());

					summaryResp4b3.setDiffValue(diffInv);
					summaryResp4b3.setDiffIgst(diffIgst);
					summaryResp4b3.setDiffCgst(diffCgst);
					summaryResp4b3.setDiffSgst(diffSgst);
					summaryResp4b3.setDiffCess(diffCess);

					rej4b3SummaryRespList.add(summaryResp4b3);
				});
			}

		}
		List<Ret1SummaryRespDto> rej4b4SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4b4 : list) {

			Ret1BasicSectionSummaryDto rej4b4List = rej4b4.getB_4B4();
			List<Ret1SummarySectionDto> rej4b4ListEySummary = rej4b4List
					.getEySummary();

			if (rej4b4ListEySummary != null) {
				rej4b4ListEySummary.forEach(itcRej4b4Summary -> {
					Ret1SummaryRespDto summaryResp4b4 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4b4.setTable(itcRej4b4Summary.getTable());
					summaryResp4b4.setSection("4");
					summaryResp4b4
							.setSupplyType(itcRej4b4Summary.getSupplyType());
					summaryResp4b4
							.setAspValue(itcRej4b4Summary.getTaxableValue());
					summaryResp4b4.setAspIgst(itcRej4b4Summary.getAspIgst());
					summaryResp4b4.setAspSgst(itcRej4b4Summary.getAspSgst());
					summaryResp4b4.setAspCgst(itcRej4b4Summary.getAspCgst());
					summaryResp4b4.setAspCess(itcRej4b4Summary.getAspCess());
					
					summaryResp4b4.setUsrValue(itcRej4b4Summary.getUsrValue());
					summaryResp4b4.setUsrSgst(itcRej4b4Summary.getUsrSgst());
					summaryResp4b4.setUsrIgst(itcRej4b4Summary.getUsrIgst());
					summaryResp4b4.setUsrCgst(itcRej4b4Summary.getUsrCgst());
					summaryResp4b4.setUsrCess(itcRej4b4Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4b4.setGstnValue(itcRej4b4Summary.getGstnValue());
					summaryResp4b4.setGstnIgst(itcRej4b4Summary.getGstnIgst());
					summaryResp4b4.setGstnSgst(itcRej4b4Summary.getGstnSgst());
					summaryResp4b4.setGstnCgst(itcRej4b4Summary.getGstnCgst());
					summaryResp4b4.setGstnCess(itcRej4b4Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4b4.getAspValue(),
							summaryResp4b4.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4b4.getAspSgst(),
							summaryResp4b4.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4b4.getAspIgst(),
							summaryResp4b4.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4b4.getAspCgst(),
							summaryResp4b4.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4b4.getAspCess(),
							summaryResp4b4.getGstnCess());

					summaryResp4b4.setDiffValue(diffInv);
					summaryResp4b4.setDiffIgst(diffIgst);
					summaryResp4b4.setDiffCgst(diffCgst);
					summaryResp4b4.setDiffSgst(diffSgst);
					summaryResp4b4.setDiffCess(diffCess);

					rej4b4SummaryRespList.add(summaryResp4b4);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4b5SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4b5 : list) {

			Ret1BasicSectionSummaryDto rej4b5List = rej4b5.getB_4B5();
			List<Ret1SummarySectionDto> rej4b5ListEySummary = rej4b5List
					.getEySummary();

			if (rej4b5ListEySummary != null) {
				rej4b5ListEySummary.forEach(itcRej4b5Summary -> {
					Ret1SummaryRespDto summaryResp4b5 = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4b5.setTable(itcRej4b5Summary.getTable());
					summaryResp4b5.setSection("5");
					summaryResp4b5
							.setSupplyType(itcRej4b5Summary.getSupplyType());
					summaryResp4b5
							.setAspValue(itcRej4b5Summary.getTaxableValue());
					summaryResp4b5.setAspIgst(itcRej4b5Summary.getAspIgst());
					summaryResp4b5.setAspSgst(itcRej4b5Summary.getAspSgst());
					summaryResp4b5.setAspCgst(itcRej4b5Summary.getAspCgst());
					summaryResp4b5.setAspCess(itcRej4b5Summary.getAspCess());
					
					summaryResp4b5.setUsrValue(itcRej4b5Summary.getUsrValue());
					summaryResp4b5.setUsrSgst(itcRej4b5Summary.getUsrSgst());
					summaryResp4b5.setUsrIgst(itcRej4b5Summary.getUsrIgst());
					summaryResp4b5.setUsrCgst(itcRej4b5Summary.getUsrCgst());
					summaryResp4b5.setUsrCess(itcRej4b5Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4b5.setGstnValue(itcRej4b5Summary.getGstnValue());
					summaryResp4b5.setGstnIgst(itcRej4b5Summary.getGstnIgst());
					summaryResp4b5.setGstnSgst(itcRej4b5Summary.getGstnSgst());
					summaryResp4b5.setGstnCgst(itcRej4b5Summary.getGstnCgst());
					summaryResp4b5.setGstnCess(itcRej4b5Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4b5.getAspValue(),
							summaryResp4b5.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4b5.getAspSgst(),
							summaryResp4b5.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4b5.getAspIgst(),
							summaryResp4b5.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4b5.getAspCgst(),
							summaryResp4b5.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4b5.getAspCess(),
							summaryResp4b5.getGstnCess());

					summaryResp4b5.setDiffValue(diffInv);
					summaryResp4b5.setDiffIgst(diffIgst);
					summaryResp4b5.setDiffCgst(diffCgst);
					summaryResp4b5.setDiffSgst(diffSgst);
					summaryResp4b5.setDiffCess(diffCess);

					rej4b5SummaryRespList.add(summaryResp4b5);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4CSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4c : list) {

			Ret1BasicSectionSummaryDto rej4cList = rej4c.getC_4C();
			List<Ret1SummarySectionDto> rej4CListEySummary = rej4cList
					.getEySummary();

			if (rej4CListEySummary != null) {
				rej4CListEySummary.forEach(itcRej4cSummary -> {
					Ret1SummaryRespDto summaryResp4c = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4c.setTable(itcRej4cSummary.getTable());
					summaryResp4c
							.setSupplyType(itcRej4cSummary.getSupplyType());
					summaryResp4c
							.setAspValue(itcRej4cSummary.getTaxableValue());
					summaryResp4c.setAspIgst(itcRej4cSummary.getAspIgst());
					summaryResp4c.setAspSgst(itcRej4cSummary.getAspSgst());
					summaryResp4c.setAspCgst(itcRej4cSummary.getAspCgst());
					summaryResp4c.setAspCess(itcRej4cSummary.getAspCess());
					
					summaryResp4c.setUsrValue(itcRej4cSummary.getUsrValue());
					summaryResp4c.setUsrSgst(itcRej4cSummary.getUsrSgst());
					summaryResp4c.setUsrIgst(itcRej4cSummary.getUsrIgst());
					summaryResp4c.setUsrCgst(itcRej4cSummary.getUsrCgst());
					summaryResp4c.setUsrCess(itcRej4cSummary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4c.setGstnValue(itcRej4cSummary.getGstnValue());
					summaryResp4c.setGstnIgst(itcRej4cSummary.getGstnIgst());
					summaryResp4c.setGstnSgst(itcRej4cSummary.getGstnSgst());
					summaryResp4c.setGstnCgst(itcRej4cSummary.getGstnCgst());
					summaryResp4c.setGstnCess(itcRej4cSummary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4c.getAspValue(),
							summaryResp4c.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4c.getAspSgst(),
							summaryResp4c.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4c.getAspIgst(),
							summaryResp4c.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4c.getAspCgst(),
							summaryResp4c.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4c.getAspCess(),
							summaryResp4c.getGstnCess());

					summaryResp4c.setDiffValue(diffInv);
					summaryResp4c.setDiffIgst(diffIgst);
					summaryResp4c.setDiffCgst(diffCgst);
					summaryResp4c.setDiffSgst(diffSgst);
					summaryResp4c.setDiffCess(diffCess);

					rej4CSummaryRespList.add(summaryResp4c);
				});
			}

		}

		List<Ret1SummaryRespDto> d4FirstSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto d4 : list) {

			Ret1BasicSectionSummaryDto d4List = d4.getD_4D();
			List<Ret1SummarySectionDto> d4ListEySummary = d4List.getEySummary();

			if (d4ListEySummary != null) {
				d4ListEySummary.forEach(d4Summary -> {
					Ret1SummaryRespDto summaryResp4d = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp4d.setTable(d4Summary.getTable());
					summaryResp4d.setSupplyType(d4Summary.getSupplyType());
					summaryResp4d.setAspValue(d4Summary.getTaxableValue());
					summaryResp4d.setAspIgst(d4Summary.getAspIgst());
					summaryResp4d.setAspSgst(d4Summary.getAspSgst());
					summaryResp4d.setAspCgst(d4Summary.getAspCgst());
					summaryResp4d.setAspCess(d4Summary.getAspCess());
					
					summaryResp4d.setUsrValue(d4Summary.getUsrValue());
					summaryResp4d.setUsrSgst(d4Summary.getUsrSgst());
					summaryResp4d.setUsrIgst(d4Summary.getUsrIgst());
					summaryResp4d.setUsrCgst(d4Summary.getUsrCgst());
					summaryResp4d.setUsrCess(d4Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp4d.setGstnValue(d4Summary.getGstnValue());
					summaryResp4d.setGstnIgst(d4Summary.getGstnIgst());
					summaryResp4d.setGstnSgst(d4Summary.getGstnSgst());
					summaryResp4d.setGstnCgst(d4Summary.getGstnCgst());
					summaryResp4d.setGstnCess(d4Summary.getGstnCess());
					// Diff
					BigDecimal diffInv = subMethod(summaryResp4d.getAspValue(),
							summaryResp4d.getGstnValue());
					BigDecimal diffSgst = subMethod(summaryResp4d.getAspSgst(),
							summaryResp4d.getGstnSgst());
					BigDecimal diffIgst = subMethod(summaryResp4d.getAspIgst(),
							summaryResp4d.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp4d.getAspCgst(),
							summaryResp4d.getGstnCgst());
					BigDecimal diffCess = subMethod(summaryResp4d.getAspCess(),
							summaryResp4d.getGstnCess());

					summaryResp4d.setDiffValue(diffInv);
					summaryResp4d.setDiffIgst(diffIgst);
					summaryResp4d.setDiffCgst(diffCgst);
					summaryResp4d.setDiffSgst(diffSgst);
					summaryResp4d.setDiffCess(diffCess);

					d4FirstSummaryRespList.add(summaryResp4d);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4DFirstSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4D : list) {

			Ret1BasicSectionSummaryDto rej4D1List = rej4D.getD_4D1();
			List<Ret1SummarySectionDto> rej4D1ListEySummary = rej4D1List
					.getEySummary();

			if (rej4D1ListEySummary != null) {
				rej4D1ListEySummary.forEach(itcRej4D1Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(itcRej4D1Summary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(itcRej4D1Summary.getSupplyType());
					summaryResp.setAspValue(itcRej4D1Summary.getTaxableValue());
					summaryResp.setAspIgst(itcRej4D1Summary.getAspIgst());
					summaryResp.setAspSgst(itcRej4D1Summary.getAspSgst());
					summaryResp.setAspCgst(itcRej4D1Summary.getAspCgst());
					summaryResp.setAspCess(itcRej4D1Summary.getAspCess());
					
					summaryResp.setUsrValue(itcRej4D1Summary.getUsrValue());
					summaryResp.setUsrSgst(itcRej4D1Summary.getUsrSgst());
					summaryResp.setUsrIgst(itcRej4D1Summary.getUsrIgst());
					summaryResp.setUsrCgst(itcRej4D1Summary.getUsrCgst());
					summaryResp.setUsrCess(itcRej4D1Summary.getUsrCess());

					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(itcRej4D1Summary.getGstnValue());
					summaryResp.setGstnIgst(itcRej4D1Summary.getGstnIgst());
					summaryResp.setGstnSgst(itcRej4D1Summary.getGstnSgst());
					summaryResp.setGstnCgst(itcRej4D1Summary.getGstnCgst());
					summaryResp.setGstnCess(itcRej4D1Summary.getGstnCess());
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

					rej4DFirstSummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4DSecondSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4D1 : list) {

			Ret1BasicSectionSummaryDto rej4D2List = rej4D1.getD_4D2();
			List<Ret1SummarySectionDto> rej4D2ListEySummary = rej4D2List
					.getEySummary();

			if (rej4D2ListEySummary != null) {
				rej4D2ListEySummary.forEach(itcRej4D2Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(itcRej4D2Summary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(itcRej4D2Summary.getSupplyType());
					summaryResp.setAspValue(itcRej4D2Summary.getTaxableValue());
					summaryResp.setAspIgst(itcRej4D2Summary.getAspIgst());
					summaryResp.setAspSgst(itcRej4D2Summary.getAspSgst());
					summaryResp.setAspCgst(itcRej4D2Summary.getAspCgst());
					summaryResp.setAspCess(itcRej4D2Summary.getAspCess());
					
					summaryResp.setUsrValue(itcRej4D2Summary.getUsrValue());
					summaryResp.setUsrSgst(itcRej4D2Summary.getUsrSgst());
					summaryResp.setUsrIgst(itcRej4D2Summary.getUsrIgst());
					summaryResp.setUsrCgst(itcRej4D2Summary.getUsrCgst());
					summaryResp.setUsrCess(itcRej4D2Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(itcRej4D2Summary.getGstnValue());
					summaryResp.setGstnIgst(itcRej4D2Summary.getGstnIgst());
					summaryResp.setGstnSgst(itcRej4D2Summary.getGstnSgst());
					summaryResp.setGstnCgst(itcRej4D2Summary.getGstnCgst());
					summaryResp.setGstnCess(itcRej4D2Summary.getGstnCess());
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

					rej4DSecondSummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> e4SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto e4 : list) {

			Ret1BasicSectionSummaryDto e4List = e4.getE_4E();
			List<Ret1SummarySectionDto> e4ListEySummary = e4List.getEySummary();

			if (e4ListEySummary != null) {
				e4ListEySummary.forEach(e4Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(e4Summary.getTable());
					summaryResp.setSupplyType(e4Summary.getSupplyType());
					summaryResp.setAspValue(e4Summary.getTaxableValue());
					summaryResp.setAspIgst(e4Summary.getAspIgst());
					summaryResp.setAspSgst(e4Summary.getAspSgst());
					summaryResp.setAspCgst(e4Summary.getAspCgst());
					summaryResp.setAspCess(e4Summary.getAspCess());
					
					summaryResp.setUsrValue(e4Summary.getUsrValue());
					summaryResp.setUsrSgst(e4Summary.getUsrSgst());
					summaryResp.setUsrIgst(e4Summary.getUsrIgst());
					summaryResp.setUsrCgst(e4Summary.getUsrCgst());
					summaryResp.setUsrCess(e4Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(e4Summary.getGstnValue());
					summaryResp.setGstnIgst(e4Summary.getGstnIgst());
					summaryResp.setGstnSgst(e4Summary.getGstnSgst());
					summaryResp.setGstnCgst(e4Summary.getGstnCgst());
					summaryResp.setGstnCess(e4Summary.getGstnCess());
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

					e4SummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4E1SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4E1 : list) {

			Ret1BasicSectionSummaryDto rej4E1List = rej4E1.getE_4E1();
			List<Ret1SummarySectionDto> rej4E1ListEySummary = rej4E1List
					.getEySummary();

			if (rej4E1ListEySummary != null) {
				rej4E1ListEySummary.forEach(itcRej4E1Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(itcRej4E1Summary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(itcRej4E1Summary.getSupplyType());
					summaryResp.setAspValue(itcRej4E1Summary.getTaxableValue());
					summaryResp.setAspIgst(itcRej4E1Summary.getAspIgst());
					summaryResp.setAspSgst(itcRej4E1Summary.getAspSgst());
					summaryResp.setAspCgst(itcRej4E1Summary.getAspCgst());
					summaryResp.setAspCess(itcRej4E1Summary.getAspCess());
					
					summaryResp.setUsrValue(itcRej4E1Summary.getUsrValue());
					summaryResp.setUsrSgst(itcRej4E1Summary.getUsrSgst());
					summaryResp.setUsrIgst(itcRej4E1Summary.getUsrIgst());
					summaryResp.setUsrCgst(itcRej4E1Summary.getUsrCgst());
					summaryResp.setUsrCess(itcRej4E1Summary.getUsrCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(itcRej4E1Summary.getGstnValue());
					summaryResp.setGstnIgst(itcRej4E1Summary.getGstnIgst());
					summaryResp.setGstnSgst(itcRej4E1Summary.getGstnSgst());
					summaryResp.setGstnCgst(itcRej4E1Summary.getGstnCgst());
					summaryResp.setGstnCess(itcRej4E1Summary.getGstnCess());

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

					rej4E1SummaryRespList.add(summaryResp);
				});
			}

		}
		List<Ret1SummaryRespDto> rej4E2SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4E2 : list) {

			Ret1BasicSectionSummaryDto rej4E2List = rej4E2.getE_4E2();
			List<Ret1SummarySectionDto> rej4E2ListEySummary = rej4E2List
					.getEySummary();

			if (rej4E2ListEySummary != null) {
				rej4E2ListEySummary.forEach(itcRej4E2Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(itcRej4E2Summary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(itcRej4E2Summary.getSupplyType());
					summaryResp.setAspValue(itcRej4E2Summary.getTaxableValue());
					summaryResp.setAspIgst(itcRej4E2Summary.getAspIgst());
					summaryResp.setAspSgst(itcRej4E2Summary.getAspSgst());
					summaryResp.setAspCgst(itcRej4E2Summary.getAspCgst());
					summaryResp.setAspCess(itcRej4E2Summary.getAspCess());
					

					summaryResp.setGstnValue(itcRej4E2Summary.getGstnValue());
					summaryResp.setGstnIgst(itcRej4E2Summary.getGstnIgst());
					summaryResp.setGstnSgst(itcRej4E2Summary.getGstnSgst());
					summaryResp.setGstnCgst(itcRej4E2Summary.getGstnCgst());
					summaryResp.setGstnCess(itcRej4E2Summary.getGstnCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(itcRej4E2Summary.getGstnValue());
					summaryResp.setGstnIgst(itcRej4E2Summary.getGstnIgst());
					summaryResp.setGstnSgst(itcRej4E2Summary.getGstnSgst());
					summaryResp.setGstnCgst(itcRej4E2Summary.getGstnCgst());
					summaryResp.setGstnCess(itcRej4E2Summary.getGstnCess());

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

					rej4E2SummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> rej4E3SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto rej4E3 : list) {

			Ret1BasicSectionSummaryDto rej4E3List = rej4E3.getE_4E3();
			List<Ret1SummarySectionDto> rej4E3ListEySummary = rej4E3List
					.getEySummary();

			if (rej4E3ListEySummary != null) {
				rej4E3ListEySummary.forEach(itcRej4E3Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(itcRej4E3Summary.getTable());
					summaryResp.setSection("3");
					summaryResp.setSupplyType(itcRej4E3Summary.getSupplyType());
					summaryResp.setAspValue(itcRej4E3Summary.getTaxableValue());
					summaryResp.setAspIgst(itcRej4E3Summary.getAspIgst());
					summaryResp.setAspSgst(itcRej4E3Summary.getAspSgst());
					summaryResp.setAspCgst(itcRej4E3Summary.getAspCgst());
					summaryResp.setAspCess(itcRej4E3Summary.getAspCess());
					

					summaryResp.setGstnValue(itcRej4E3Summary.getGstnValue());
					summaryResp.setGstnIgst(itcRej4E3Summary.getGstnIgst());
					summaryResp.setGstnSgst(itcRej4E3Summary.getGstnSgst());
					summaryResp.setGstnCgst(itcRej4E3Summary.getGstnCgst());
					summaryResp.setGstnCess(itcRej4E3Summary.getGstnCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(itcRej4E3Summary.getGstnValue());
					summaryResp.setGstnIgst(itcRej4E3Summary.getGstnIgst());
					summaryResp.setGstnSgst(itcRej4E3Summary.getGstnSgst());
					summaryResp.setGstnCgst(itcRej4E3Summary.getGstnCgst());
					summaryResp.setGstnCess(itcRej4E3Summary.getGstnCess());
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

					rej4E3SummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> t5SummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto t5 : list) {

			Ret1BasicSectionSummaryDto t5List = t5.getTdtc_5();
			List<Ret1SummarySectionDto> t5ListEySummary = t5List.getEySummary();

			if (t5ListEySummary != null) {
				t5ListEySummary.forEach(t5Summary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(t5Summary.getTable());
					summaryResp.setSupplyType(t5Summary.getSupplyType());
					summaryResp.setAspValue(t5Summary.getTaxableValue());
					summaryResp.setAspIgst(t5Summary.getAspIgst());
					summaryResp.setAspSgst(t5Summary.getAspSgst());
					summaryResp.setAspCgst(t5Summary.getAspCgst());
					summaryResp.setAspCess(t5Summary.getAspCess());

					summaryResp.setGstnValue(t5Summary.getGstnValue());
					summaryResp.setGstnIgst(t5Summary.getGstnIgst());
					summaryResp.setGstnSgst(t5Summary.getGstnSgst());
					summaryResp.setGstnCgst(t5Summary.getGstnCgst());
					summaryResp.setGstnCess(t5Summary.getGstnCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(t5Summary.getGstnValue());
					summaryResp.setGstnIgst(t5Summary.getGstnIgst());
					summaryResp.setGstnSgst(t5Summary.getGstnSgst());
					summaryResp.setGstnCgst(t5Summary.getGstnCgst());
					summaryResp.setGstnCess(t5Summary.getGstnCess());
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

					t5SummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> rejTdsSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto tds : list) {

			Ret1BasicSectionSummaryDto rejtdsList = tds.getTdtc_51();
			List<Ret1SummarySectionDto> rejtdsListEySummary = rejtdsList
					.getEySummary();

			if (rejtdsListEySummary != null) {
				rejtdsListEySummary.forEach(tdsSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(tdsSummary.getTable());
					summaryResp.setSection("1");
					summaryResp.setSupplyType(tdsSummary.getSupplyType());
					summaryResp.setAspValue(tdsSummary.getTaxableValue());
					summaryResp.setAspIgst(tdsSummary.getAspIgst());
					summaryResp.setAspSgst(tdsSummary.getAspSgst());
					summaryResp.setAspCgst(tdsSummary.getAspCgst());
					summaryResp.setAspCess(tdsSummary.getAspCess());
					

					summaryResp.setGstnValue(tdsSummary.getGstnValue());
					summaryResp.setGstnIgst(tdsSummary.getGstnIgst());
					summaryResp.setGstnSgst(tdsSummary.getGstnSgst());
					summaryResp.setGstnCgst(tdsSummary.getGstnCgst());
					summaryResp.setGstnCess(tdsSummary.getGstnCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(tdsSummary.getGstnValue());
					summaryResp.setGstnIgst(tdsSummary.getGstnIgst());
					summaryResp.setGstnSgst(tdsSummary.getGstnSgst());
					summaryResp.setGstnCgst(tdsSummary.getGstnCgst());
					summaryResp.setGstnCess(tdsSummary.getGstnCess());
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

					rejTdsSummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> rejTcsSummaryRespList = new ArrayList<>();
		for (Ret1CompleteSummaryDto tcs : list) {

			Ret1BasicSectionSummaryDto rejtcsList = tcs.getTdtc_52();
			List<Ret1SummarySectionDto> rejTcsListEySummary = rejtcsList
					.getEySummary();

			if (rejTcsListEySummary != null) {
				rejTcsListEySummary.forEach(tcsSummary -> {
					Ret1SummaryRespDto summaryResp = new Ret1SummaryRespDto();
					// EY Resp
					summaryResp.setTable(tcsSummary.getTable());
					summaryResp.setSection("2");
					summaryResp.setSupplyType(tcsSummary.getSupplyType());
					summaryResp.setAspValue(tcsSummary.getTaxableValue());
					summaryResp.setAspIgst(tcsSummary.getAspIgst());
					summaryResp.setAspSgst(tcsSummary.getAspSgst());
					summaryResp.setAspCgst(tcsSummary.getAspCgst());
					summaryResp.setAspCess(tcsSummary.getAspCess());
					

					summaryResp.setGstnValue(tcsSummary.getGstnValue());
					summaryResp.setGstnIgst(tcsSummary.getGstnIgst());
					summaryResp.setGstnSgst(tcsSummary.getGstnSgst());
					summaryResp.setGstnCgst(tcsSummary.getGstnCgst());
					summaryResp.setGstnCess(tcsSummary.getGstnCess());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnValue(tcsSummary.getGstnValue());
					summaryResp.setGstnIgst(tcsSummary.getGstnIgst());
					summaryResp.setGstnSgst(tcsSummary.getGstnSgst());
					summaryResp.setGstnCgst(tcsSummary.getGstnCgst());
					summaryResp.setGstnCess(tcsSummary.getGstnCess());
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

					rejTcsSummaryRespList.add(summaryResp);
				});
			}

		}

		List<Ret1SummaryRespDto> a3List = new ArrayList<>();

		List<Ret1SummaryRespDto> a3 = ret1B2CRespHandler.ret1B2CResp(
				a3SummaryRespList, null, "A3", "outwardSupplies");
		a3List.addAll(a3);
		List<Ret1SummaryRespDto> b2cRespBody = ret1B2CRespHandler
				.ret1B2CResp(b2cSummaryRespList, "1", "3A", "b2c");
		a3List.addAll(b2cRespBody);
		List<Ret1SummaryRespDto> b2bRespBody = ret1B2CRespHandler
				.ret1B2CResp(b2bSummaryRespList, "2", "3B", "b2b");
		a3List.addAll(b2bRespBody);
		List<Ret1SummaryRespDto> expwtRespBody = ret1B2CRespHandler
				.ret1B2CResp(expwtSummaryRespList, "3", "3C", "expwt");
		a3List.addAll(expwtRespBody);
		List<Ret1SummaryRespDto> expwotRespBody = ret1B2CRespHandler
				.ret1B2CResp(expwotSummaryRespList, "4", "3D", "expwot");
		a3List.addAll(expwotRespBody);
		List<Ret1SummaryRespDto> sezwtRespBody = ret1B2CRespHandler
				.ret1B2CResp(sezwtSummaryRespList, "5", "3E", "sezwt");
		a3List.addAll(sezwtRespBody);
		List<Ret1SummaryRespDto> sezwotRespBody = ret1B2CRespHandler
				.ret1B2CResp(sezwotSummaryRespList, "6", "3F", "sezwot");
		a3List.addAll(sezwotRespBody);
		List<Ret1SummaryRespDto> deRespBody = ret1B2CRespHandler
				.ret1B2CResp(deSummaryRespList, "7", "3G", "de");
		a3List.addAll(deRespBody);
		List<Ret1SummaryRespDto> priorRespBody = ret1B2CRespHandler
				.ret1B2CResp(pPSummaryRespList, "8", "3A8", "priorPeriod");
		a3List.addAll(priorRespBody);

		List<Ret1SummaryRespDto> b3Total = new ArrayList<>();

		List<Ret1SummaryRespDto> b3TotRespBody = ret1B2CRespHandler
				.ret1B2CResp(b3SummaryRespList, null, "B3", "inwardSupplies");
		b3Total.addAll(b3TotRespBody);
		List<Ret1SummaryRespDto> revRespBody = ret1B2CRespHandler
				.ret1B2CResp(revSummaryRespList, "1", "3H", "rcInward");
		b3Total.addAll(revRespBody);
		List<Ret1SummaryRespDto> impsRespBody = ret1B2CRespHandler
				.ret1B2CResp(impsSummaryRespList, "2", "3I", "imps");
		b3Total.addAll(impsRespBody);

		List<Ret1SummaryRespDto> c3Total = new ArrayList<>();
		List<Ret1SummaryRespDto> c3RespBody = ret1B2CRespHandler
				.ret1B2CResp(c3SummaryRespList, null, "C3", "drCrNotes");
		c3Total.addAll(c3RespBody);
		List<Ret1SummaryRespDto> drRespBody = ret1B2CRespHandler
				.ret1B2CResp(drSummaryRespList, "1", "3C1", "dr");
		c3Total.addAll(drRespBody);
		List<Ret1SummaryRespDto> crRespBody = ret1B2CRespHandler
				.ret1B2CResp(crSummaryRespList, "2", "3C2", "cr");
		c3Total.addAll(crRespBody);
		List<Ret1SummaryRespDto> advRejRespBody = ret1B2CRespHandler
				.ret1B2CResp(advRejSummaryRespList, "3", "3C3", "advRec");
		c3Total.addAll(advRejRespBody);
		List<Ret1SummaryRespDto> advAdjRespBody = ret1B2CRespHandler
				.ret1B2CResp(advAdjSummaryRespList, "4", "3C4", "advAdj");
		c3Total.addAll(advAdjRespBody);
		List<Ret1SummaryRespDto> otherRedRespBody = ret1B2CRespHandler
				.ret1B2CResp(otherRedSummaryRespList, "5", "3C5",
						"otherReductions");
		c3Total.addAll(otherRedRespBody);

		List<Ret1SummaryRespDto> d3Total = new ArrayList<>();
		List<Ret1SummaryRespDto> d3RespBody = ret1B2CRespHandler.ret1B2CResp(
				d3ExemptSummaryRespList, null, "D3", "suppliesNoLiability");
		d3Total.addAll(d3RespBody);
		List<Ret1SummaryRespDto> nilExemRespBody = ret1B2CRespHandler
				.ret1B2CResp(nilExemptSummaryRespList, "1", "3D1", "exemptNil");
		d3Total.addAll(nilExemRespBody);
		List<Ret1SummaryRespDto> nonRespBody = ret1B2CRespHandler
				.ret1B2CResp(nonSummaryRespList, "2", "3D2", "nonGst");
		d3Total.addAll(nonRespBody);
		List<Ret1SummaryRespDto> osRevRespBody = ret1B2CRespHandler
				.ret1B2CResp(osRevSummaryRespList, "3", "3D3", "rcOutward");
		d3Total.addAll(osRevRespBody);
		List<Ret1SummaryRespDto> sezToDtaRespBody = ret1B2CRespHandler
				.ret1B2CResp(sezDtaSummaryRespList, "4", "3D4", "sezDta");
		d3Total.addAll(sezToDtaRespBody);
		// 3E
		List<Ret1SummaryRespDto> e3Total = new ArrayList<>();
		List<Ret1SummaryRespDto> totalValRespBody = ret1B2CRespHandler
				.ret1B2CResp(totalSummaryRespList, null, "E3",
						"valueNLiability");
		e3Total.addAll(totalValRespBody);
		// 4A

		List<Ret1SummaryRespDto> a4Total = new ArrayList<>();
		List<Ret1SummaryRespDto> a4RespBody = ret1B2CRespHandler
				.ret1B2CResp(a4SummaryRespList, null, "A4", "itcNOtherClaims");
		a4Total.addAll(a4RespBody);
		List<Ret1SummaryRespDto> itcRejRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcRejSummaryRespList, "1", "4A1", "rejectDoc");
		a4Total.addAll(itcRejRespBody);
		List<Ret1SummaryRespDto> itcPenRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcPenSummaryRespList, "2", "4A2", "pendingDoc");
		a4Total.addAll(itcPenRespBody);
		List<Ret1SummaryRespDto> itcAccRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcAccSummaryRespList, "3", "4A3", "acceptDoc");
		a4Total.addAll(itcAccRespBody);
		List<Ret1SummaryRespDto> itcEligRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcEligibleSummaryRespList, "4", "4A4", "ecnapr");
		a4Total.addAll(itcEligRespBody);
		List<Ret1SummaryRespDto> itcInwRevRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcInwRevSummaryRespList, "5", "3H", "rcInward");
		a4Total.addAll(itcInwRevRespBody);
		List<Ret1SummaryRespDto> itcImpsRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcImpsSummaryRespList, "6", "3I", "imps");
		a4Total.addAll(itcImpsRespBody);
		List<Ret1SummaryRespDto> itcImpgRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcImpgSummaryRespList, "7", "3J", "impg");
		a4Total.addAll(itcImpgRespBody);
		List<Ret1SummaryRespDto> itcImpgSezRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcImpgSezSummaryRespList, "8", "3K", "impgSez");
		a4Total.addAll(itcImpgSezRespBody);
		List<Ret1SummaryRespDto> itcIsdRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcIsdSummaryRespList, "9", "ISD", "isdCredit");
		a4Total.addAll(itcIsdRespBody);
		List<Ret1SummaryRespDto> itcProRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcProSummaryRespList, "10", "4A10",
						"itcProvisional");
		a4Total.addAll(itcProRespBody);
		List<Ret1SummaryRespDto> itcAdjRespBody = ret1B2CRespHandler
				.ret1B2CResp(itcAdjSummaryRespList, "11", "4A11",
						"adjustments");
		a4Total.addAll(itcAdjRespBody);
		// 4B

		List<Ret1SummaryRespDto> b4Total = new ArrayList<>();
		List<Ret1SummaryRespDto> b4TRespBody = ret1B2CRespHandler
				.ret1B2CResp(b4SummaryRespList, null, "B4", "creditReversal");
		b4Total.addAll(b4TRespBody);
		List<Ret1SummaryRespDto> rev4b1RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4b1SummaryRespList, "1", "4B1",
						"acceptRejectDoc");
		b4Total.addAll(rev4b1RespBody);
		List<Ret1SummaryRespDto> rev4b2RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4b2SummaryRespList, "2", "4B2",
						"ineligibleCredit");
		b4Total.addAll(rev4b2RespBody);
		List<Ret1SummaryRespDto> rev4b3RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4b3SummaryRespList, "3", "4B3",
						"itcProvisionalReverse");
		b4Total.addAll(rev4b3RespBody);
		List<Ret1SummaryRespDto> rev4b4RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4b4SummaryRespList, "4", "4B4", "itcReversal");
		b4Total.addAll(rev4b4RespBody);
		List<Ret1SummaryRespDto> rev4b5RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4b5SummaryRespList, "5", "4B5",
						"otherReversal");
		b4Total.addAll(rev4b5RespBody);
		// 4C

		// List<Ret1SummaryRespDto> c4Total = new ArrayList<>();
		List<Ret1SummaryRespDto> c4Total = ret1B2CRespHandler.ret1B2CResp(
				rej4CSummaryRespList, null, "C4", "creditReversal");

		// 4D

		List<Ret1SummaryRespDto> d4Total = new ArrayList<>();
		List<Ret1SummaryRespDto> d4TRespBody = ret1B2CRespHandler
				.ret1B2CResp(d4FirstSummaryRespList, null, "D4", "itcDeclared");
		d4Total.addAll(d4TRespBody);
		List<Ret1SummaryRespDto> firstRespBody = ret1B2CRespHandler.ret1B2CResp(
				rej4DFirstSummaryRespList, "1", "4D1", "firstMonth");
		d4Total.addAll(firstRespBody);
		List<Ret1SummaryRespDto> secondRespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4DSecondSummaryRespList, "2", "4D2",
						"secondMonth");
		d4Total.addAll(secondRespBody);
		// 4E

		List<Ret1SummaryRespDto> e4Total = new ArrayList<>();
		List<Ret1SummaryRespDto> e4TRespBody = ret1B2CRespHandler
				.ret1B2CResp(e4SummaryRespList, null, "E4", "netItcAvailable");
		e4Total.addAll(e4TRespBody);
		List<Ret1SummaryRespDto> cap4E1RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4E1SummaryRespList, "1", "4E1", "itcCapital");
		e4Total.addAll(cap4E1RespBody);
		List<Ret1SummaryRespDto> cap4E2RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4E2SummaryRespList, "2", "4E2", "itcService");
		e4Total.addAll(cap4E2RespBody);
		List<Ret1SummaryRespDto> cap4E3RespBody = ret1B2CRespHandler
				.ret1B2CResp(rej4E3SummaryRespList, "3", "4E3",
						"itcInputGoods");
		e4Total.addAll(cap4E3RespBody);
		// Section 5

		List<Ret1SummaryRespDto> t5Total = new ArrayList<>();
		List<Ret1SummaryRespDto> t5tRespBody = ret1B2CRespHandler
				.ret1B2CResp(t5SummaryRespList, null, "5", "tdsTcsCredit");
		t5Total.addAll(t5tRespBody);
		List<Ret1SummaryRespDto> tdsRespBody = ret1B2CRespHandler
				.ret1B2CResp(rejTdsSummaryRespList, "1", "TDS", "tds");
		t5Total.addAll(tdsRespBody);
		List<Ret1SummaryRespDto> tcsRespBody = ret1B2CRespHandler
				.ret1B2CResp(rejTcsSummaryRespList, "2", "TCS", "tcs");
		t5Total.addAll(tcsRespBody);

		JsonElement a3Respbody = gson.toJsonTree(a3List);
		JsonElement b3Respbody = gson.toJsonTree(b3Total);
		JsonElement c3Respbody = gson.toJsonTree(c3Total);
		JsonElement d3Respbody = gson.toJsonTree(d3Total);
		JsonElement e3Respbody = gson.toJsonTree(e3Total);
		JsonElement a4tRespBody = gson.toJsonTree(a4Total);
		JsonElement b4RespBody = gson.toJsonTree(b4Total);
		JsonElement c4RespBody = gson.toJsonTree(c4Total);
		JsonElement d4RespBody = gson.toJsonTree(d4Total);
		JsonElement e4RespBody = gson.toJsonTree(e4Total);
		JsonElement t5RespBody = gson.toJsonTree(t5Total);

		combinedMap.put("A3", a3Respbody);
		/*
		 * combinedMap.put("3A", b2cRespBody); combinedMap.put("3B",
		 * b2bRespBody); combinedMap.put("3C", expwtRespBody);
		 * combinedMap.put("3D", expwotRespBody); combinedMap.put("3E",
		 * sezwtRespBody); combinedMap.put("3F", sezwotRespBody);
		 * combinedMap.put("3G", deRespBody); combinedMap.put("3A8",
		 * priorRespBody);
		 */
		combinedMap.put("B3", b3Respbody);
		/*
		 * combinedMap.put("3H", revRespBody); combinedMap.put("3I",
		 * impsRespBody);
		 */

		combinedMap.put("C3", c3Respbody);
		/*
		 * combinedMap.put("3C1", drRespBody); combinedMap.put("3C2",
		 * crRespBody); combinedMap.put("3C3", advRejRespBody);
		 * combinedMap.put("3C4", advAdjRespBody); combinedMap.put("3C5",
		 * otherRedRespBody);
		 */

		combinedMap.put("D3", d3Respbody);
		/*
		 * combinedMap.put("3D1", nilExemRespBody); combinedMap.put("3D2",
		 * nonRespBody); combinedMap.put("3D3", osRevRespBody);
		 * combinedMap.put("3D4", sezToDtaRespBody);
		 */

		combinedMap.put("E3", e3Respbody);

		combinedMap.put("A4", a4tRespBody);
		/*
		 * combinedMap.put("4A1", itcRejRespBody); combinedMap.put("4A2",
		 * itcPenRespBody); combinedMap.put("4A3", itcAccRespBody);
		 * combinedMap.put("4A4", itcEligRespBody); combinedMap.put("3H",
		 * itcInwRevRespBody); combinedMap.put("3I", itcImpsRespBody);
		 * combinedMap.put("3J", itcImpgRespBody); combinedMap.put("3K",
		 * itcImpgSezRespBody); combinedMap.put("ISD", itcIsdRespBody);
		 * combinedMap.put("4A10", itcProRespBody); combinedMap.put("4A11",
		 * itcAdjRespBody);
		 */

		combinedMap.put("B4", b4RespBody);
		/*
		 * combinedMap.put("4B1", rev4b1RespBody); combinedMap.put("4B2",
		 * rev4b2RespBody); combinedMap.put("4B3", rev4b3RespBody);
		 * combinedMap.put("4B4", rev4b4RespBody); combinedMap.put("4B5",
		 * rev4b5RespBody);
		 */

		combinedMap.put("C4", c4RespBody);

		combinedMap.put("D4", d4RespBody);
		/*
		 * combinedMap.put("4D1", firstRespBody); combinedMap.put("4D2",
		 * secondRespBody);
		 */

		combinedMap.put("E4", e4RespBody);
		/*
		 * combinedMap.put("4E1", cap4E1RespBody); combinedMap.put("4E2",
		 * cap4E2RespBody); combinedMap.put("4E3", cap4E3RespBody);
		 */

		combinedMap.put("5", t5RespBody);
		/*
		 * combinedMap.put("5(1)", tdsRespBody); combinedMap.put("5(2)",
		 * tcsRespBody);
		 */

		JsonElement ret13ASummaryRespbody = gson.toJsonTree(combinedMap);

		/*
		 * Map<String, JsonElement> combinedMap2 = new HashMap<>();
		 * 
		 * combinedMap2.put("aspValues", ret13ASummaryRespbody);
		 */

		return ret13ASummaryRespbody;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

}
