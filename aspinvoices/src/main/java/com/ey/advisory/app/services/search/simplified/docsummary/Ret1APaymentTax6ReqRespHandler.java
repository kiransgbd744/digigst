package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Ret1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.TaxSectionPaymentSummaryDto;
import com.ey.advisory.app.services.search.docsummarysearch.Ret1APaymentTax6Structure;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Ret1APaymentTax6ReqRespHandler")
@Slf4j
public class Ret1APaymentTax6ReqRespHandler {

	@Autowired
	@Qualifier("Ret1APayment6SimplDocSummaryService")
	Ret1APayment6SimplDocSummaryService service;

	@Autowired
	@Qualifier("Ret1APaymentTax6Structure")
	Ret1APaymentTax6Structure paymentTax;

	public static final String one = "1";
	public static final String two = "2";
	public static final String three = "3";
	public static final String four = "4";

	public static final String con_6 = "6";
	public static final String con_6_1 = "6(1)";
	public static final String con_6_2 = "6(2)";
	public static final String con_6_3 = "6(3)";
	public static final String con_6_4 = "6(4)";

	public JsonElement handleRe1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		SearchResult<Ret1CompleteSummaryDto> summaryResult = service
				.<Ret1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Ret1CompleteSummaryDto.class);

		List<? extends Ret1CompleteSummaryDto> result = summaryResult
				.getResult();

		List<TaxPaymentSummaryDto> a6 = new ArrayList<>();

		for (Ret1CompleteSummaryDto an : result) {

			Ret1BasicSectionSummaryDto ret1A6 = an.getRet1APay6();
			List<TaxSectionPaymentSummaryDto> eySummaryTax6 = ret1A6
					.getEySummaryPaymentTax6();
			if (eySummaryTax6 != null) {
				eySummaryTax6.forEach(taxSummary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					// EY Resp

					summaryResp.setTable(taxSummary.getTable());
					summaryResp.setDesc(taxSummary.getDesc());
					summaryResp.setUsrCashPaidInterest(
							taxSummary.getUsrCashPaidInterest());
					summaryResp.setUsrCashPaidLateFee(
							taxSummary.getUsrCashPaidLateFee());
					summaryResp
							.setUsrCashPaidTax(taxSummary.getUsrCashPaidTax());
					summaryResp
							.setUsrItcPaidCess(taxSummary.getUsrItcPaidCess());
					summaryResp
							.setUsrItcPaidCgst(taxSummary.getUsrItcPaidCgst());
					summaryResp
							.setUsrItcPaidIgst(taxSummary.getUsrItcPaidIgst());
					summaryResp
							.setUsrItcPaidSgst(taxSummary.getUsrItcPaidSgst());
					summaryResp.setUsrLiability(taxSummary.getUsrLiability());
					summaryResp.setUsrOtherLiability(
							taxSummary.getUsrOtherLiability());
					summaryResp.setUsrOtherPaid(taxSummary.getUsrOtherPaid());
					summaryResp.setUsrOtherPayable(
							taxSummary.getUsrOtherPayable());
					summaryResp.setUsrPaid(taxSummary.getUsrPaid());
					summaryResp.setUsrPayable(taxSummary.getUsrPayable());

					summaryResp.setGstnCashPaidInterest(
							taxSummary.getGstnCashPaidInterest());
					summaryResp.setGstnCashPaidLateFee(
							taxSummary.getGstnCashPaidLateFee());
					summaryResp.setGstnCashPaidTax(
							taxSummary.getGstnCashPaidTax());
					summaryResp.setGstnItcPaidCess(
							taxSummary.getGstnItcPaidCess());
					summaryResp.setGstnItcPaidCgst(
							taxSummary.getGstnItcPaidCgst());
					summaryResp.setGstnItcPaidIgst(
							taxSummary.getGstnItcPaidIgst());
					summaryResp.setGstnItcPaidSgst(
							taxSummary.getGstnItcPaidSgst());
					summaryResp.setGstnLiability(taxSummary.getGstnLiability());
					summaryResp.setGstnOtherLiability(
							taxSummary.getGstnOtherLiability());
					summaryResp.setGstnOtherPaid(taxSummary.getGstnOtherPaid());
					summaryResp.setGstnOtherPayable(
							taxSummary.getGstnOtherPayable());
					summaryResp.setGstnPaid(taxSummary.getGstnPaid());
					summaryResp.setGstnPayable(taxSummary.getGstnPayable());

					// Diff
					BigDecimal diffcashPaid = subMethod(
							summaryResp.getUsrCashPaidInterest(),
							summaryResp.getGstnCashPaidInterest());
					BigDecimal diffCashPaidLateFee = subMethod(
							summaryResp.getUsrCashPaidLateFee(),
							summaryResp.getGstnCashPaidLateFee());
					BigDecimal diffCashPaidTad = subMethod(
							summaryResp.getUsrCashPaidTax(),
							summaryResp.getGstnCashPaidTax());
					BigDecimal diffCess = subMethod(
							summaryResp.getUsrItcPaidCess(),
							summaryResp.getGstnItcPaidCess());
					BigDecimal diffCgst = subMethod(
							summaryResp.getDiffItcPaidCgst(),
							summaryResp.getGstnItcPaidCgst());
					BigDecimal diffIgst = subMethod(
							summaryResp.getUsrItcPaidIgst(),
							summaryResp.getGstnItcPaidIgst());
					BigDecimal diffSgst = subMethod(
							summaryResp.getUsrItcPaidSgst(),
							summaryResp.getGstnItcPaidSgst());

					BigDecimal diffLialibility = subMethod(
							summaryResp.getUsrLiability(),
							summaryResp.getGstnLiability());

					BigDecimal diffOthLialibility = subMethod(
							summaryResp.getUsrOtherLiability(),
							summaryResp.getGstnOtherLiability());
					BigDecimal diffOtherPaid = subMethod(
							summaryResp.getUsrOtherPaid(),
							summaryResp.getGstnOtherPaid());
					BigDecimal diffOthPayable = subMethod(
							summaryResp.getUsrOtherPayable(),
							summaryResp.getGstnOtherPayable());
					BigDecimal diffPaid = subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid());
					BigDecimal diffPayable = subMethod(
							summaryResp.getUsrPayable(),
							summaryResp.getGstnPayable());

					summaryResp.setDiffCashPaidInterest(diffcashPaid);
					summaryResp.setDiffCashPaidLateFee(diffCashPaidLateFee);
					summaryResp.setDiffCashPaidTax(diffCashPaidTad);
					summaryResp.setDiffItcPaidCess(diffCess);
					summaryResp.setDiffItcPaidCgst(diffCgst);
					summaryResp.setDiffItcPaidIgst(diffIgst);
					summaryResp.setDiffItcPaidSgst(diffSgst);
					summaryResp.setDiffLiability(diffLialibility);
					summaryResp.setDiffOtherLiability(diffOthLialibility);
					summaryResp.setDiffOtherPaid(diffOtherPaid);
					summaryResp.setDiffOtherPayable(diffOthPayable);
					summaryResp.setDiffPaid(diffPaid);
					summaryResp.setDiffPayable(diffPayable);

					a6.add(summaryResp);
				});
			}
		}

		List<TaxPaymentSummaryDto> a61 = new ArrayList<>();

		for (Ret1CompleteSummaryDto an61 : result) {

			Ret1BasicSectionSummaryDto ret1A61 = an61.getRet1APay6_1();
			List<TaxSectionPaymentSummaryDto> eySummaryTax61 = ret1A61
					.getEySummaryPaymentTax6();
			if (eySummaryTax61 != null) {
				eySummaryTax61.forEach(taxSummary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					// EY Resp

					summaryResp.setTable(taxSummary.getTable());
					summaryResp.setDesc(taxSummary.getDesc());
					summaryResp.setUsrCashPaidInterest(
							taxSummary.getUsrCashPaidInterest());
					summaryResp.setUsrCashPaidLateFee(
							taxSummary.getUsrCashPaidLateFee());
					summaryResp
							.setUsrCashPaidTax(taxSummary.getUsrCashPaidTax());
					summaryResp
							.setUsrItcPaidCess(taxSummary.getUsrItcPaidCess());
					summaryResp
							.setUsrItcPaidCgst(taxSummary.getUsrItcPaidCgst());
					summaryResp
							.setUsrItcPaidIgst(taxSummary.getUsrItcPaidIgst());
					summaryResp
							.setUsrItcPaidSgst(taxSummary.getUsrItcPaidSgst());
					summaryResp.setUsrLiability(taxSummary.getUsrLiability());
					summaryResp.setUsrOtherLiability(
							taxSummary.getUsrOtherLiability());
					summaryResp.setUsrOtherPaid(taxSummary.getUsrOtherPaid());
					summaryResp.setUsrOtherPayable(
							taxSummary.getUsrOtherPayable());
					summaryResp.setUsrPaid(taxSummary.getUsrPaid());
					summaryResp.setUsrPayable(taxSummary.getUsrPayable());

					summaryResp.setGstnCashPaidInterest(
							taxSummary.getGstnCashPaidInterest());
					summaryResp.setGstnCashPaidLateFee(
							taxSummary.getGstnCashPaidLateFee());
					summaryResp.setGstnCashPaidTax(
							taxSummary.getGstnCashPaidTax());
					summaryResp.setGstnItcPaidCess(
							taxSummary.getGstnItcPaidCess());
					summaryResp.setGstnItcPaidCgst(
							taxSummary.getGstnItcPaidCgst());
					summaryResp.setGstnItcPaidIgst(
							taxSummary.getGstnItcPaidIgst());
					summaryResp.setGstnItcPaidSgst(
							taxSummary.getGstnItcPaidSgst());
					summaryResp.setGstnLiability(taxSummary.getGstnLiability());
					summaryResp.setGstnOtherLiability(
							taxSummary.getGstnOtherLiability());
					summaryResp.setGstnOtherPaid(taxSummary.getGstnOtherPaid());
					summaryResp.setGstnOtherPayable(
							taxSummary.getGstnOtherPayable());
					summaryResp.setGstnPaid(taxSummary.getGstnPaid());
					summaryResp.setGstnPayable(taxSummary.getGstnPayable());

					// Diff
					BigDecimal diffcashPaid = subMethod(
							summaryResp.getUsrCashPaidInterest(),
							summaryResp.getGstnCashPaidInterest());
					BigDecimal diffCashPaidLateFee = subMethod(
							summaryResp.getUsrCashPaidLateFee(),
							summaryResp.getGstnCashPaidLateFee());
					BigDecimal diffCashPaidTad = subMethod(
							summaryResp.getUsrCashPaidTax(),
							summaryResp.getGstnCashPaidTax());
					BigDecimal diffCess = subMethod(
							summaryResp.getUsrItcPaidCess(),
							summaryResp.getGstnItcPaidCess());
					BigDecimal diffCgst = subMethod(
							summaryResp.getDiffItcPaidCgst(),
							summaryResp.getGstnItcPaidCgst());
					BigDecimal diffIgst = subMethod(
							summaryResp.getUsrItcPaidIgst(),
							summaryResp.getGstnItcPaidIgst());
					BigDecimal diffSgst = subMethod(
							summaryResp.getUsrItcPaidSgst(),
							summaryResp.getGstnItcPaidSgst());

					BigDecimal diffLialibility = subMethod(
							summaryResp.getUsrLiability(),
							summaryResp.getGstnLiability());

					BigDecimal diffOthLialibility = subMethod(
							summaryResp.getUsrOtherLiability(),
							summaryResp.getGstnOtherLiability());
					BigDecimal diffOtherPaid = subMethod(
							summaryResp.getUsrOtherPaid(),
							summaryResp.getGstnOtherPaid());
					BigDecimal diffOthPayable = subMethod(
							summaryResp.getUsrOtherPayable(),
							summaryResp.getGstnOtherPayable());
					BigDecimal diffPaid = subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid());
					BigDecimal diffPayable = subMethod(
							summaryResp.getUsrPayable(),
							summaryResp.getGstnPayable());

					summaryResp.setDiffCashPaidInterest(diffcashPaid);
					summaryResp.setDiffCashPaidLateFee(diffCashPaidLateFee);
					summaryResp.setDiffCashPaidTax(diffCashPaidTad);
					summaryResp.setDiffItcPaidCess(diffCess);
					summaryResp.setDiffItcPaidCgst(diffCgst);
					summaryResp.setDiffItcPaidIgst(diffIgst);
					summaryResp.setDiffItcPaidSgst(diffSgst);
					summaryResp.setDiffLiability(diffLialibility);
					summaryResp.setDiffOtherLiability(diffOthLialibility);
					summaryResp.setDiffOtherPaid(diffOtherPaid);
					summaryResp.setDiffOtherPayable(diffOthPayable);
					summaryResp.setDiffPaid(diffPaid);
					summaryResp.setDiffPayable(diffPayable);

					a61.add(summaryResp);
				});
			}
		}
		List<TaxPaymentSummaryDto> a62 = new ArrayList<>();

		for (Ret1CompleteSummaryDto an62 : result) {

			Ret1BasicSectionSummaryDto ret1A62 = an62.getRet1APay6_2();
			List<TaxSectionPaymentSummaryDto> eySummaryTax62 = ret1A62
					.getEySummaryPaymentTax6();
			if (eySummaryTax62 != null) {
				eySummaryTax62.forEach(taxSummary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					// EY Resp

					summaryResp.setTable(taxSummary.getTable());
					summaryResp.setDesc(taxSummary.getDesc());
					summaryResp.setUsrCashPaidInterest(
							taxSummary.getUsrCashPaidInterest());
					summaryResp.setUsrCashPaidLateFee(
							taxSummary.getUsrCashPaidLateFee());
					summaryResp
							.setUsrCashPaidTax(taxSummary.getUsrCashPaidTax());
					summaryResp
							.setUsrItcPaidCess(taxSummary.getUsrItcPaidCess());
					summaryResp
							.setUsrItcPaidCgst(taxSummary.getUsrItcPaidCgst());
					summaryResp
							.setUsrItcPaidIgst(taxSummary.getUsrItcPaidIgst());
					summaryResp
							.setUsrItcPaidSgst(taxSummary.getUsrItcPaidSgst());
					summaryResp.setUsrLiability(taxSummary.getUsrLiability());
					summaryResp.setUsrOtherLiability(
							taxSummary.getUsrOtherLiability());
					summaryResp.setUsrOtherPaid(taxSummary.getUsrOtherPaid());
					summaryResp.setUsrOtherPayable(
							taxSummary.getUsrOtherPayable());
					summaryResp.setUsrPaid(taxSummary.getUsrPaid());
					summaryResp.setUsrPayable(taxSummary.getUsrPayable());

					summaryResp.setGstnCashPaidInterest(
							taxSummary.getGstnCashPaidInterest());
					summaryResp.setGstnCashPaidLateFee(
							taxSummary.getGstnCashPaidLateFee());
					summaryResp.setGstnCashPaidTax(
							taxSummary.getGstnCashPaidTax());
					summaryResp.setGstnItcPaidCess(
							taxSummary.getGstnItcPaidCess());
					summaryResp.setGstnItcPaidCgst(
							taxSummary.getGstnItcPaidCgst());
					summaryResp.setGstnItcPaidIgst(
							taxSummary.getGstnItcPaidIgst());
					summaryResp.setGstnItcPaidSgst(
							taxSummary.getGstnItcPaidSgst());
					summaryResp.setGstnLiability(taxSummary.getGstnLiability());
					summaryResp.setGstnOtherLiability(
							taxSummary.getGstnOtherLiability());
					summaryResp.setGstnOtherPaid(taxSummary.getGstnOtherPaid());
					summaryResp.setGstnOtherPayable(
							taxSummary.getGstnOtherPayable());
					summaryResp.setGstnPaid(taxSummary.getGstnPaid());
					summaryResp.setGstnPayable(taxSummary.getGstnPayable());

					// Diff
					BigDecimal diffcashPaid = subMethod(
							summaryResp.getUsrCashPaidInterest(),
							summaryResp.getGstnCashPaidInterest());
					BigDecimal diffCashPaidLateFee = subMethod(
							summaryResp.getUsrCashPaidLateFee(),
							summaryResp.getGstnCashPaidLateFee());
					BigDecimal diffCashPaidTad = subMethod(
							summaryResp.getUsrCashPaidTax(),
							summaryResp.getGstnCashPaidTax());
					BigDecimal diffCess = subMethod(
							summaryResp.getUsrItcPaidCess(),
							summaryResp.getGstnItcPaidCess());
					BigDecimal diffCgst = subMethod(
							summaryResp.getDiffItcPaidCgst(),
							summaryResp.getGstnItcPaidCgst());
					BigDecimal diffIgst = subMethod(
							summaryResp.getUsrItcPaidIgst(),
							summaryResp.getGstnItcPaidIgst());
					BigDecimal diffSgst = subMethod(
							summaryResp.getUsrItcPaidSgst(),
							summaryResp.getGstnItcPaidSgst());

					BigDecimal diffLialibility = subMethod(
							summaryResp.getUsrLiability(),
							summaryResp.getGstnLiability());

					BigDecimal diffOthLialibility = subMethod(
							summaryResp.getUsrOtherLiability(),
							summaryResp.getGstnOtherLiability());
					BigDecimal diffOtherPaid = subMethod(
							summaryResp.getUsrOtherPaid(),
							summaryResp.getGstnOtherPaid());
					BigDecimal diffOthPayable = subMethod(
							summaryResp.getUsrOtherPayable(),
							summaryResp.getGstnOtherPayable());
					BigDecimal diffPaid = subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid());
					BigDecimal diffPayable = subMethod(
							summaryResp.getUsrPayable(),
							summaryResp.getGstnPayable());

					summaryResp.setDiffCashPaidInterest(diffcashPaid);
					summaryResp.setDiffCashPaidLateFee(diffCashPaidLateFee);
					summaryResp.setDiffCashPaidTax(diffCashPaidTad);
					summaryResp.setDiffItcPaidCess(diffCess);
					summaryResp.setDiffItcPaidCgst(diffCgst);
					summaryResp.setDiffItcPaidIgst(diffIgst);
					summaryResp.setDiffItcPaidSgst(diffSgst);
					summaryResp.setDiffLiability(diffLialibility);
					summaryResp.setDiffOtherLiability(diffOthLialibility);
					summaryResp.setDiffOtherPaid(diffOtherPaid);
					summaryResp.setDiffOtherPayable(diffOthPayable);
					summaryResp.setDiffPaid(diffPaid);
					summaryResp.setDiffPayable(diffPayable);

					a62.add(summaryResp);
				});
			}
		}
		List<TaxPaymentSummaryDto> a63 = new ArrayList<>();

		for (Ret1CompleteSummaryDto an63 : result) {

			Ret1BasicSectionSummaryDto ret1A63 = an63.getRet1APay6_3();
			List<TaxSectionPaymentSummaryDto> eySummaryTax63 = ret1A63
					.getEySummaryPaymentTax6();
			if (eySummaryTax63 != null) {
				eySummaryTax63.forEach(taxSummary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					// EY Resp

					summaryResp.setTable(taxSummary.getTable());
					summaryResp.setDesc(taxSummary.getDesc());
					summaryResp.setUsrCashPaidInterest(
							taxSummary.getUsrCashPaidInterest());
					summaryResp.setUsrCashPaidLateFee(
							taxSummary.getUsrCashPaidLateFee());
					summaryResp
							.setUsrCashPaidTax(taxSummary.getUsrCashPaidTax());
					summaryResp
							.setUsrItcPaidCess(taxSummary.getUsrItcPaidCess());
					summaryResp
							.setUsrItcPaidCgst(taxSummary.getUsrItcPaidCgst());
					summaryResp
							.setUsrItcPaidIgst(taxSummary.getUsrItcPaidIgst());
					summaryResp
							.setUsrItcPaidSgst(taxSummary.getUsrItcPaidSgst());
					summaryResp.setUsrLiability(taxSummary.getUsrLiability());
					summaryResp.setUsrOtherLiability(
							taxSummary.getUsrOtherLiability());
					summaryResp.setUsrOtherPaid(taxSummary.getUsrOtherPaid());
					summaryResp.setUsrOtherPayable(
							taxSummary.getUsrOtherPayable());
					summaryResp.setUsrPaid(taxSummary.getUsrPaid());
					summaryResp.setUsrPayable(taxSummary.getUsrPayable());

					summaryResp.setGstnCashPaidInterest(
							taxSummary.getGstnCashPaidInterest());
					summaryResp.setGstnCashPaidLateFee(
							taxSummary.getGstnCashPaidLateFee());
					summaryResp.setGstnCashPaidTax(
							taxSummary.getGstnCashPaidTax());
					summaryResp.setGstnItcPaidCess(
							taxSummary.getGstnItcPaidCess());
					summaryResp.setGstnItcPaidCgst(
							taxSummary.getGstnItcPaidCgst());
					summaryResp.setGstnItcPaidIgst(
							taxSummary.getGstnItcPaidIgst());
					summaryResp.setGstnItcPaidSgst(
							taxSummary.getGstnItcPaidSgst());
					summaryResp.setGstnLiability(taxSummary.getGstnLiability());
					summaryResp.setGstnOtherLiability(
							taxSummary.getGstnOtherLiability());
					summaryResp.setGstnOtherPaid(taxSummary.getGstnOtherPaid());
					summaryResp.setGstnOtherPayable(
							taxSummary.getGstnOtherPayable());
					summaryResp.setGstnPaid(taxSummary.getGstnPaid());
					summaryResp.setGstnPayable(taxSummary.getGstnPayable());

					// Diff
					BigDecimal diffcashPaid = subMethod(
							summaryResp.getUsrCashPaidInterest(),
							summaryResp.getGstnCashPaidInterest());
					BigDecimal diffCashPaidLateFee = subMethod(
							summaryResp.getUsrCashPaidLateFee(),
							summaryResp.getGstnCashPaidLateFee());
					BigDecimal diffCashPaidTad = subMethod(
							summaryResp.getUsrCashPaidTax(),
							summaryResp.getGstnCashPaidTax());
					BigDecimal diffCess = subMethod(
							summaryResp.getUsrItcPaidCess(),
							summaryResp.getGstnItcPaidCess());
					BigDecimal diffCgst = subMethod(
							summaryResp.getDiffItcPaidCgst(),
							summaryResp.getGstnItcPaidCgst());
					BigDecimal diffIgst = subMethod(
							summaryResp.getUsrItcPaidIgst(),
							summaryResp.getGstnItcPaidIgst());
					BigDecimal diffSgst = subMethod(
							summaryResp.getUsrItcPaidSgst(),
							summaryResp.getGstnItcPaidSgst());

					BigDecimal diffLialibility = subMethod(
							summaryResp.getUsrLiability(),
							summaryResp.getGstnLiability());

					BigDecimal diffOthLialibility = subMethod(
							summaryResp.getUsrOtherLiability(),
							summaryResp.getGstnOtherLiability());
					BigDecimal diffOtherPaid = subMethod(
							summaryResp.getUsrOtherPaid(),
							summaryResp.getGstnOtherPaid());
					BigDecimal diffOthPayable = subMethod(
							summaryResp.getUsrOtherPayable(),
							summaryResp.getGstnOtherPayable());
					BigDecimal diffPaid = subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid());
					BigDecimal diffPayable = subMethod(
							summaryResp.getUsrPayable(),
							summaryResp.getGstnPayable());

					summaryResp.setDiffCashPaidInterest(diffcashPaid);
					summaryResp.setDiffCashPaidLateFee(diffCashPaidLateFee);
					summaryResp.setDiffCashPaidTax(diffCashPaidTad);
					summaryResp.setDiffItcPaidCess(diffCess);
					summaryResp.setDiffItcPaidCgst(diffCgst);
					summaryResp.setDiffItcPaidIgst(diffIgst);
					summaryResp.setDiffItcPaidSgst(diffSgst);
					summaryResp.setDiffLiability(diffLialibility);
					summaryResp.setDiffOtherLiability(diffOthLialibility);
					summaryResp.setDiffOtherPaid(diffOtherPaid);
					summaryResp.setDiffOtherPayable(diffOthPayable);
					summaryResp.setDiffPaid(diffPaid);
					summaryResp.setDiffPayable(diffPayable);

					a63.add(summaryResp);
				});
			}
		}
		List<TaxPaymentSummaryDto> a64 = new ArrayList<>();

		for (Ret1CompleteSummaryDto an64 : result) {

			Ret1BasicSectionSummaryDto ret1A64 = an64.getRet1APay6_4();
			List<TaxSectionPaymentSummaryDto> eySummaryTax64 = ret1A64
					.getEySummaryPaymentTax6();
			if (eySummaryTax64 != null) {
				eySummaryTax64.forEach(taxSummary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					// EY Resp

					summaryResp.setTable(taxSummary.getTable());
					summaryResp.setDesc(taxSummary.getDesc());
					summaryResp.setUsrCashPaidInterest(
							taxSummary.getUsrCashPaidInterest());
					summaryResp.setUsrCashPaidLateFee(
							taxSummary.getUsrCashPaidLateFee());
					summaryResp
							.setUsrCashPaidTax(taxSummary.getUsrCashPaidTax());
					summaryResp
							.setUsrItcPaidCess(taxSummary.getUsrItcPaidCess());
					summaryResp
							.setUsrItcPaidCgst(taxSummary.getUsrItcPaidCgst());
					summaryResp
							.setUsrItcPaidIgst(taxSummary.getUsrItcPaidIgst());
					summaryResp
							.setUsrItcPaidSgst(taxSummary.getUsrItcPaidSgst());
					summaryResp.setUsrLiability(taxSummary.getUsrLiability());
					summaryResp.setUsrOtherLiability(
							taxSummary.getUsrOtherLiability());
					summaryResp.setUsrOtherPaid(taxSummary.getUsrOtherPaid());
					summaryResp.setUsrOtherPayable(
							taxSummary.getUsrOtherPayable());
					summaryResp.setUsrPaid(taxSummary.getUsrPaid());
					summaryResp.setUsrPayable(taxSummary.getUsrPayable());

					summaryResp.setGstnCashPaidInterest(
							taxSummary.getGstnCashPaidInterest());
					summaryResp.setGstnCashPaidLateFee(
							taxSummary.getGstnCashPaidLateFee());
					summaryResp.setGstnCashPaidTax(
							taxSummary.getGstnCashPaidTax());
					summaryResp.setGstnItcPaidCess(
							taxSummary.getGstnItcPaidCess());
					summaryResp.setGstnItcPaidCgst(
							taxSummary.getGstnItcPaidCgst());
					summaryResp.setGstnItcPaidIgst(
							taxSummary.getGstnItcPaidIgst());
					summaryResp.setGstnItcPaidSgst(
							taxSummary.getGstnItcPaidSgst());
					summaryResp.setGstnLiability(taxSummary.getGstnLiability());
					summaryResp.setGstnOtherLiability(
							taxSummary.getGstnOtherLiability());
					summaryResp.setGstnOtherPaid(taxSummary.getGstnOtherPaid());
					summaryResp.setGstnOtherPayable(
							taxSummary.getGstnOtherPayable());
					summaryResp.setGstnPaid(taxSummary.getGstnPaid());
					summaryResp.setGstnPayable(taxSummary.getGstnPayable());

					// Diff
					BigDecimal diffcashPaid = subMethod(
							summaryResp.getUsrCashPaidInterest(),
							summaryResp.getGstnCashPaidInterest());
					BigDecimal diffCashPaidLateFee = subMethod(
							summaryResp.getUsrCashPaidLateFee(),
							summaryResp.getGstnCashPaidLateFee());
					BigDecimal diffCashPaidTad = subMethod(
							summaryResp.getUsrCashPaidTax(),
							summaryResp.getGstnCashPaidTax());
					BigDecimal diffCess = subMethod(
							summaryResp.getUsrItcPaidCess(),
							summaryResp.getGstnItcPaidCess());
					BigDecimal diffCgst = subMethod(
							summaryResp.getDiffItcPaidCgst(),
							summaryResp.getGstnItcPaidCgst());
					BigDecimal diffIgst = subMethod(
							summaryResp.getUsrItcPaidIgst(),
							summaryResp.getGstnItcPaidIgst());
					BigDecimal diffSgst = subMethod(
							summaryResp.getUsrItcPaidSgst(),
							summaryResp.getGstnItcPaidSgst());

					BigDecimal diffLialibility = subMethod(
							summaryResp.getUsrLiability(),
							summaryResp.getGstnLiability());

					BigDecimal diffOthLialibility = subMethod(
							summaryResp.getUsrOtherLiability(),
							summaryResp.getGstnOtherLiability());
					BigDecimal diffOtherPaid = subMethod(
							summaryResp.getUsrOtherPaid(),
							summaryResp.getGstnOtherPaid());
					BigDecimal diffOthPayable = subMethod(
							summaryResp.getUsrOtherPayable(),
							summaryResp.getGstnOtherPayable());
					BigDecimal diffPaid = subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid());
					BigDecimal diffPayable = subMethod(
							summaryResp.getUsrPayable(),
							summaryResp.getGstnPayable());

					summaryResp.setDiffCashPaidInterest(diffcashPaid);
					summaryResp.setDiffCashPaidLateFee(diffCashPaidLateFee);
					summaryResp.setDiffCashPaidTax(diffCashPaidTad);
					summaryResp.setDiffItcPaidCess(diffCess);
					summaryResp.setDiffItcPaidCgst(diffCgst);
					summaryResp.setDiffItcPaidIgst(diffIgst);
					summaryResp.setDiffItcPaidSgst(diffSgst);
					summaryResp.setDiffLiability(diffLialibility);
					summaryResp.setDiffOtherLiability(diffOthLialibility);
					summaryResp.setDiffOtherPaid(diffOtherPaid);
					summaryResp.setDiffOtherPayable(diffOthPayable);
					summaryResp.setDiffPaid(diffPaid);
					summaryResp.setDiffPayable(diffPayable);

					a64.add(summaryResp);
				});
			}

		}
		List<TaxPaymentSummaryDto> paymentTaxTotal = new ArrayList<>();

		List<TaxPaymentSummaryDto> pay6RespBody = paymentTax.ret1A6Resp(a6,
				null, "a_paymentTax", con_6);
		List<TaxPaymentSummaryDto> pay61RespBody = paymentTax.ret1A6Resp(a61,
				one, "a_igst", con_6_1);
		List<TaxPaymentSummaryDto> pay62RespBody = paymentTax.ret1A6Resp(a62,
				two, "a_cgst", con_6_2);
		List<TaxPaymentSummaryDto> pay63RespBody = paymentTax.ret1A6Resp(a63,
				three, "a_sgst", con_6_3);
		List<TaxPaymentSummaryDto> pay64RespBody = paymentTax.ret1A6Resp(a64,
				four, "a_cess", con_6_4);

		paymentTaxTotal.addAll(pay6RespBody);
		paymentTaxTotal.addAll(pay61RespBody);
		paymentTaxTotal.addAll(pay62RespBody);
		paymentTaxTotal.addAll(pay63RespBody);
		paymentTaxTotal.addAll(pay64RespBody);
		
		JsonElement ret16Respbody = gson.toJsonTree(paymentTaxTotal);
		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("tab_6", ret16Respbody);

		JsonElement ret16SummaryRespbody = gson.toJsonTree(combinedMap);

		return ret16SummaryRespbody;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

}
