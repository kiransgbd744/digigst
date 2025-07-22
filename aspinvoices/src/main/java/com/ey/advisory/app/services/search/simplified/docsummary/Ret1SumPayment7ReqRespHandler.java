package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
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
import com.ey.advisory.app.docs.dto.Ret1PaymentSummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.app.services.search.docsummarysearch.Ret1Payment7Structure;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Component("Ret1SumPayment7ReqRespHandler")
@Slf4j
public class Ret1SumPayment7ReqRespHandler {

	@Autowired
	@Qualifier("Ret1SumPayment7SimplDocSummaryService")
	Ret1SumPayment7SimplDocSummaryService service;
	@Autowired
	@Qualifier("Ret1Payment7Structure")
	private Ret1Payment7Structure ret1Payment7Structure;
	public static final String one = "1";
	public static final String two = "2";
	public static final String three = "3";
	public static final String four = "4";

	public static final String con_7 = "7";
	public static final String con_7_1 = "7(1)";
	public static final String con_7_2 = "7(2)";
	public static final String con_7_3 = "7(3)";
	public static final String con_7_4 = "7(4)";

	public JsonElement handleRe1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		LOGGER.debug("Section 8 Service Execution Started..");
		SearchResult<Ret1CompleteSummaryDto> summaryResult = service
				.<Ret1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Ret1CompleteSummaryDto.class);
		LOGGER.debug("Section 8 Service Execution Ended..");
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<? extends Ret1CompleteSummaryDto> list = summaryResult.getResult();

		List<TaxPaymentSummaryDto> listadd7 = new ArrayList<>();
		List<TaxPaymentSummaryDto> listadd7_1 = new ArrayList<>();
		List<TaxPaymentSummaryDto> listadd7_2 = new ArrayList<>();
		List<TaxPaymentSummaryDto> listadd7_3 = new ArrayList<>();
		List<TaxPaymentSummaryDto> listadd7_4 = new ArrayList<>();
		for (Ret1CompleteSummaryDto dto : list) {
			
			Ret1BasicSectionSummaryDto payment_7 = dto.getPayment_7();
			Ret1BasicSectionSummaryDto payment_7_1 = dto.getPayment_7_1();
			Ret1BasicSectionSummaryDto payment_7_2 = dto.getPayment7_2();
			Ret1BasicSectionSummaryDto payment_7_3 = dto.getPayment7_3();
			Ret1BasicSectionSummaryDto payment_7_4 = dto.getPayment7_4();
			
			List<Ret1PaymentSummarySectionDto> eySummaryforint7list = payment_7
					.getEySummaryPayment7();
			
			List<Ret1PaymentSummarySectionDto> eySummaryforint6_1list = payment_7_1
					.getEySummaryPayment7();
			List<Ret1PaymentSummarySectionDto> eySummaryforint6_2list = payment_7_2
					.getEySummaryPayment7();
			List<Ret1PaymentSummarySectionDto> eySummaryforint6_3list = payment_7_3
					.getEySummaryPayment7();
			List<Ret1PaymentSummarySectionDto> eySummaryforint6_4list = payment_7_4
					.getEySummaryPayment7();
			if (eySummaryforint7list != null
					&& !eySummaryforint7list.isEmpty()) {
				eySummaryforint7list.forEach(Summary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					summaryResp.setTable(Summary.getTable());
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrPayable(Summary.getAsptaxPayableRc());
					summaryResp
							.setUsrOtherPayable(Summary.getAsptaxPayableOtherRc());
					summaryResp.setUsrPaid(Summary.getAsptaxAlreadyPaidRc());
					summaryResp.setUsrOtherPaid(Summary.getAsptaxAlreadyPaidotherRc());
					summaryResp.setUsrLiability(Summary.getAspadjOflibRc());
					summaryResp.setUsrOtherLiability(
							Summary.getAspadjOflibOtherRc());
					summaryResp.setUsrItcPaidIgst(Summary.getAsppaidThroughIgst());
					summaryResp.setUsrItcPaidCgst(Summary.getAsppaidThroughCgst());
					summaryResp.setUsrItcPaidSgst(Summary.getAsppaidThroughSgst());
					summaryResp.setUsrItcPaidCess(Summary.getAsppaidThroughCess());
					summaryResp.setUsrCashPaidInterest(
							Summary.getAsppaidIncash_interest());
					summaryResp.setUsrCashPaidTax(Summary.getAsppaidIncash_tax());
					summaryResp.setUsrCashPaidLateFee(
							Summary.getAsppaidIncash_latefee());
					summaryResp.setGstnPayable(Summary.getGstntaxPayable_rc());
					summaryResp
							.setGstnOtherPayable(Summary.getGstntaxPayable_Other_rc());
					summaryResp.setGstnPaid(Summary.getGstnalready_paid_rc());
					summaryResp.setGstnOtherPaid(Summary.getGstnalready_paid_Other_rc());
					summaryResp.setGstnLiability(Summary.getGstnadjOflibRc());
					summaryResp.setGstnOtherLiability(
							Summary.getGstnadjOflibOtherRc());
					summaryResp
							.setGstnItcPaidIgst(Summary.getGstnpaidThroughIgst());
					summaryResp
							.setGstnItcPaidCgst(Summary.getGstnpaidThroughCgst());
					summaryResp
							.setGstnItcPaidSgst(Summary.getGstnpaidThroughSgst());
					summaryResp
							.setGstnItcPaidCess(Summary.getGstnpaidThroughCess());
					summaryResp.setGstnCashPaidInterest(
							Summary.getGstnpaidIncash_interest());
					summaryResp.setGstnCashPaidTax(Summary.getGstnpaidIncash_tax());
					summaryResp.setGstnCashPaidLateFee(
							Summary.getGstnpaidIncash_latefee());

					summaryResp.setDiffPayable(
							subMethod(summaryResp.getUsrPayable(),
									summaryResp.getGstnPayable()));
					summaryResp.setDiffOtherPayable(
							subMethod(summaryResp.getUsrOtherPayable(),
									summaryResp.getGstnOtherPayable()));
					summaryResp.setDiffPaid(subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid()));
					summaryResp.setDiffOtherPaid(
							subMethod(summaryResp.getUsrOtherPaid(),
									summaryResp.getGstnOtherPaid()));
					summaryResp.setDiffLiability(
							subMethod(summaryResp.getUsrLiability(),
									summaryResp.getGstnLiability()));
					summaryResp.setDiffOtherLiability(
							subMethod(summaryResp.getUsrOtherLiability(),
									summaryResp.getGstnOtherLiability()));
					summaryResp.setDiffItcPaidIgst(
							subMethod(summaryResp.getUsrItcPaidIgst(),
									summaryResp.getGstnItcPaidIgst()));
					summaryResp.setDiffItcPaidCgst(
							subMethod(summaryResp.getUsrItcPaidCgst(),
									summaryResp.getGstnItcPaidCgst()));
					summaryResp.setDiffItcPaidSgst(
							subMethod(summaryResp.getUsrItcPaidSgst(),
									summaryResp.getGstnItcPaidSgst()));
					summaryResp.setDiffItcPaidCess(
							subMethod(summaryResp.getUsrItcPaidCess(),
									summaryResp.getGstnItcPaidCess()));
					summaryResp.setDiffCashPaidInterest(
							subMethod(summaryResp.getUsrCashPaidInterest(),
									summaryResp.getGstnCashPaidInterest()));
					summaryResp.setDiffCashPaidTax(
							subMethod(summaryResp.getUsrCashPaidTax(),
									summaryResp.getGstnCashPaidTax()));
					summaryResp.setDiffCashPaidLateFee(
							subMethod(summaryResp.getUsrCashPaidLateFee(),
									summaryResp.getGstnCashPaidLateFee()));
					listadd7.add(summaryResp);
				});
			}
			
			
			if (eySummaryforint6_1list != null
					&& !eySummaryforint6_1list.isEmpty()) {
				eySummaryforint6_1list.forEach(Summary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection(one);
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrPayable(Summary.getAsptaxPayableRc());
					summaryResp
							.setUsrOtherPayable(Summary.getAsptaxPayableOtherRc());
					summaryResp.setUsrPaid(Summary.getAsptaxAlreadyPaidRc());
					summaryResp.setUsrOtherPaid(Summary.getAsptaxAlreadyPaidotherRc());
					summaryResp.setUsrLiability(Summary.getAspadjOflibRc());
					summaryResp.setUsrOtherLiability(
							Summary.getAspadjOflibOtherRc());
					summaryResp.setUsrItcPaidIgst(Summary.getAsppaidThroughIgst());
					summaryResp.setUsrItcPaidCgst(Summary.getAsppaidThroughCgst());
					summaryResp.setUsrItcPaidSgst(Summary.getAsppaidThroughSgst());
					summaryResp.setUsrItcPaidCess(Summary.getAsppaidThroughCess());
					summaryResp.setUsrCashPaidInterest(
							Summary.getAsppaidIncash_interest());
					summaryResp.setUsrCashPaidTax(Summary.getAsppaidIncash_tax());
					summaryResp.setUsrCashPaidLateFee(
							Summary.getAsppaidIncash_latefee());
					summaryResp.setGstnPayable(Summary.getGstntaxPayable_rc());
					summaryResp
							.setGstnOtherPayable(Summary.getGstntaxPayable_Other_rc());
					summaryResp.setGstnPaid(Summary.getGstnalready_paid_rc());
					summaryResp.setGstnOtherPaid(Summary.getGstnalready_paid_Other_rc());
					summaryResp.setGstnLiability(Summary.getGstnadjOflibRc());
					summaryResp.setGstnOtherLiability(
							Summary.getGstnadjOflibOtherRc());
					summaryResp
							.setGstnItcPaidIgst(Summary.getGstnpaidThroughIgst());
					summaryResp
							.setGstnItcPaidCgst(Summary.getGstnpaidThroughCgst());
					summaryResp
							.setGstnItcPaidSgst(Summary.getGstnpaidThroughSgst());
					summaryResp
							.setGstnItcPaidCess(Summary.getGstnpaidThroughCess());
					summaryResp.setGstnCashPaidInterest(
							Summary.getGstnpaidIncash_interest());
					summaryResp.setGstnCashPaidTax(Summary.getGstnpaidIncash_tax());
					summaryResp.setGstnCashPaidLateFee(
							Summary.getGstnpaidIncash_latefee());

					summaryResp.setDiffPayable(
							subMethod(summaryResp.getUsrPayable(),
									summaryResp.getGstnPayable()));
					summaryResp.setDiffOtherPayable(
							subMethod(summaryResp.getUsrOtherPayable(),
									summaryResp.getGstnOtherPayable()));
					summaryResp.setDiffPaid(subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid()));
					summaryResp.setDiffOtherPaid(
							subMethod(summaryResp.getUsrOtherPaid(),
									summaryResp.getGstnOtherPaid()));
					summaryResp.setDiffLiability(
							subMethod(summaryResp.getUsrLiability(),
									summaryResp.getGstnLiability()));
					summaryResp.setDiffOtherLiability(
							subMethod(summaryResp.getUsrOtherLiability(),
									summaryResp.getGstnOtherLiability()));
					summaryResp.setDiffItcPaidIgst(
							subMethod(summaryResp.getUsrItcPaidIgst(),
									summaryResp.getGstnItcPaidIgst()));
					summaryResp.setDiffItcPaidCgst(
							subMethod(summaryResp.getUsrItcPaidCgst(),
									summaryResp.getGstnItcPaidCgst()));
					summaryResp.setDiffItcPaidSgst(
							subMethod(summaryResp.getUsrItcPaidSgst(),
									summaryResp.getGstnItcPaidSgst()));
					summaryResp.setDiffItcPaidCess(
							subMethod(summaryResp.getUsrItcPaidCess(),
									summaryResp.getGstnItcPaidCess()));
					summaryResp.setDiffCashPaidInterest(
							subMethod(summaryResp.getUsrCashPaidInterest(),
									summaryResp.getGstnCashPaidInterest()));
					summaryResp.setDiffCashPaidTax(
							subMethod(summaryResp.getUsrCashPaidTax(),
									summaryResp.getGstnCashPaidTax()));
					summaryResp.setDiffCashPaidLateFee(
							subMethod(summaryResp.getUsrCashPaidLateFee(),
									summaryResp.getGstnCashPaidLateFee()));
					listadd7.add(summaryResp);
				});
			}
			
			if (eySummaryforint6_2list != null
					&& !eySummaryforint6_2list.isEmpty()) {
				eySummaryforint6_2list.forEach(Summary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection(two);
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrPayable(Summary.getAsptaxPayableRc());
					summaryResp
							.setUsrOtherPayable(Summary.getAsptaxPayableOtherRc());
					summaryResp.setUsrPaid(Summary.getAsptaxAlreadyPaidRc());
					summaryResp.setUsrOtherPaid(Summary.getAsptaxAlreadyPaidotherRc());
					summaryResp.setUsrLiability(Summary.getAspadjOflibRc());
					summaryResp.setUsrOtherLiability(
							Summary.getAspadjOflibOtherRc());
					summaryResp.setUsrItcPaidIgst(Summary.getAsppaidThroughIgst());
					summaryResp.setUsrItcPaidCgst(Summary.getAsppaidThroughCgst());
					summaryResp.setUsrItcPaidSgst(Summary.getAsppaidThroughSgst());
					summaryResp.setUsrItcPaidCess(Summary.getAsppaidThroughCess());
					summaryResp.setUsrCashPaidInterest(
							Summary.getAsppaidIncash_interest());
					summaryResp.setUsrCashPaidTax(Summary.getAsppaidIncash_tax());
					summaryResp.setUsrCashPaidLateFee(
							Summary.getAsppaidIncash_latefee());
					summaryResp.setGstnPayable(Summary.getGstntaxPayable_rc());
					summaryResp
							.setGstnOtherPayable(Summary.getGstntaxPayable_Other_rc());
					summaryResp.setGstnPaid(Summary.getGstnalready_paid_rc());
					summaryResp.setGstnOtherPaid(Summary.getGstnalready_paid_Other_rc());
					summaryResp.setGstnLiability(Summary.getGstnadjOflibRc());
					summaryResp.setGstnOtherLiability(
							Summary.getGstnadjOflibOtherRc());
					summaryResp
							.setGstnItcPaidIgst(Summary.getGstnpaidThroughIgst());
					summaryResp
							.setGstnItcPaidCgst(Summary.getGstnpaidThroughCgst());
					summaryResp
							.setGstnItcPaidSgst(Summary.getGstnpaidThroughSgst());
					summaryResp
							.setGstnItcPaidCess(Summary.getGstnpaidThroughCess());
					summaryResp.setGstnCashPaidInterest(
							Summary.getGstnpaidIncash_interest());
					summaryResp.setGstnCashPaidTax(Summary.getGstnpaidIncash_tax());
					summaryResp.setGstnCashPaidLateFee(
							Summary.getGstnpaidIncash_latefee());

					summaryResp.setDiffPayable(
							subMethod(summaryResp.getUsrPayable(),
									summaryResp.getGstnPayable()));
					summaryResp.setDiffOtherPayable(
							subMethod(summaryResp.getUsrOtherPayable(),
									summaryResp.getGstnOtherPayable()));
					summaryResp.setDiffPaid(subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid()));
					summaryResp.setDiffOtherPaid(
							subMethod(summaryResp.getUsrOtherPaid(),
									summaryResp.getGstnOtherPaid()));
					summaryResp.setDiffLiability(
							subMethod(summaryResp.getUsrLiability(),
									summaryResp.getGstnLiability()));
					summaryResp.setDiffOtherLiability(
							subMethod(summaryResp.getUsrOtherLiability(),
									summaryResp.getGstnOtherLiability()));
					summaryResp.setDiffItcPaidIgst(
							subMethod(summaryResp.getUsrItcPaidIgst(),
									summaryResp.getGstnItcPaidIgst()));
					summaryResp.setDiffItcPaidCgst(
							subMethod(summaryResp.getUsrItcPaidCgst(),
									summaryResp.getGstnItcPaidCgst()));
					summaryResp.setDiffItcPaidSgst(
							subMethod(summaryResp.getUsrItcPaidSgst(),
									summaryResp.getGstnItcPaidSgst()));
					summaryResp.setDiffItcPaidCess(
							subMethod(summaryResp.getUsrItcPaidCess(),
									summaryResp.getGstnItcPaidCess()));
					summaryResp.setDiffCashPaidInterest(
							subMethod(summaryResp.getUsrCashPaidInterest(),
									summaryResp.getGstnCashPaidInterest()));
					summaryResp.setDiffCashPaidTax(
							subMethod(summaryResp.getUsrCashPaidTax(),
									summaryResp.getGstnCashPaidTax()));
					summaryResp.setDiffCashPaidLateFee(
							subMethod(summaryResp.getUsrCashPaidLateFee(),
									summaryResp.getGstnCashPaidLateFee()));
					listadd7_2.add(summaryResp);
				});
			}
			if (eySummaryforint6_3list != null
					&& !eySummaryforint6_3list.isEmpty()) {
				eySummaryforint6_3list.forEach(Summary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection(three);
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrPayable(Summary.getAsptaxPayableRc());
					summaryResp
							.setUsrOtherPayable(Summary.getAsptaxPayableOtherRc());
					summaryResp.setUsrPaid(Summary.getAsptaxAlreadyPaidRc());
					summaryResp.setUsrOtherPaid(Summary.getAsptaxAlreadyPaidotherRc());
					summaryResp.setUsrLiability(Summary.getAspadjOflibRc());
					summaryResp.setUsrOtherLiability(
							Summary.getAspadjOflibOtherRc());
					summaryResp.setUsrItcPaidIgst(Summary.getAsppaidThroughIgst());
					summaryResp.setUsrItcPaidCgst(Summary.getAsppaidThroughCgst());
					summaryResp.setUsrItcPaidSgst(Summary.getAsppaidThroughSgst());
					summaryResp.setUsrItcPaidCess(Summary.getAsppaidThroughCess());
					summaryResp.setUsrCashPaidInterest(
							Summary.getAsppaidIncash_interest());
					summaryResp.setUsrCashPaidTax(Summary.getAsppaidIncash_tax());
					summaryResp.setUsrCashPaidLateFee(
							Summary.getAsppaidIncash_latefee());
					summaryResp.setGstnPayable(Summary.getGstntaxPayable_rc());
					summaryResp
							.setGstnOtherPayable(Summary.getGstntaxPayable_Other_rc());
					summaryResp.setGstnPaid(Summary.getGstnalready_paid_rc());
					summaryResp.setGstnOtherPaid(Summary.getGstnalready_paid_Other_rc());
					summaryResp.setGstnLiability(Summary.getGstnadjOflibRc());
					summaryResp.setGstnOtherLiability(
							Summary.getGstnadjOflibOtherRc());
					summaryResp
							.setGstnItcPaidIgst(Summary.getGstnpaidThroughIgst());
					summaryResp
							.setGstnItcPaidCgst(Summary.getGstnpaidThroughCgst());
					summaryResp
							.setGstnItcPaidSgst(Summary.getGstnpaidThroughSgst());
					summaryResp
							.setGstnItcPaidCess(Summary.getGstnpaidThroughCess());
					summaryResp.setGstnCashPaidInterest(
							Summary.getGstnpaidIncash_interest());
					summaryResp.setGstnCashPaidTax(Summary.getGstnpaidIncash_tax());
					summaryResp.setGstnCashPaidLateFee(
							Summary.getGstnpaidIncash_latefee());
					summaryResp.setDiffPayable(
							subMethod(summaryResp.getUsrPayable(),
									summaryResp.getGstnPayable()));
					summaryResp.setDiffOtherPayable(
							subMethod(summaryResp.getUsrOtherPayable(),
									summaryResp.getGstnOtherPayable()));
					summaryResp.setDiffPaid(subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid()));
					summaryResp.setDiffOtherPaid(
							subMethod(summaryResp.getUsrOtherPaid(),
									summaryResp.getGstnOtherPaid()));
					summaryResp.setDiffLiability(
							subMethod(summaryResp.getUsrLiability(),
									summaryResp.getGstnLiability()));
					summaryResp.setDiffOtherLiability(
							subMethod(summaryResp.getUsrOtherLiability(),
									summaryResp.getGstnOtherLiability()));
					summaryResp.setDiffItcPaidIgst(
							subMethod(summaryResp.getUsrItcPaidIgst(),
									summaryResp.getGstnItcPaidIgst()));
					summaryResp.setDiffItcPaidCgst(
							subMethod(summaryResp.getUsrItcPaidCgst(),
									summaryResp.getGstnItcPaidCgst()));
					summaryResp.setDiffItcPaidSgst(
							subMethod(summaryResp.getUsrItcPaidSgst(),
									summaryResp.getGstnItcPaidSgst()));
					summaryResp.setDiffItcPaidCess(
							subMethod(summaryResp.getUsrItcPaidCess(),
									summaryResp.getGstnItcPaidCess()));
					summaryResp.setDiffCashPaidInterest(
							subMethod(summaryResp.getUsrCashPaidInterest(),
									summaryResp.getGstnCashPaidInterest()));
					summaryResp.setDiffCashPaidTax(
							subMethod(summaryResp.getUsrCashPaidTax(),
									summaryResp.getGstnCashPaidTax()));
					summaryResp.setDiffCashPaidLateFee(
							subMethod(summaryResp.getUsrCashPaidLateFee(),
									summaryResp.getGstnCashPaidLateFee()));
					listadd7_3.add(summaryResp);
				});
			}
			if (eySummaryforint6_4list != null
					&& !eySummaryforint6_4list.isEmpty()) {
				eySummaryforint6_4list.forEach(Summary -> {
					TaxPaymentSummaryDto summaryResp = new TaxPaymentSummaryDto();
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection(four);
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrPayable(Summary.getAsptaxPayableRc());
					summaryResp
							.setUsrOtherPayable(Summary.getAsptaxPayableOtherRc());
					summaryResp.setUsrPaid(Summary.getAsptaxAlreadyPaidRc());
					summaryResp.setUsrOtherPaid(Summary.getAsptaxAlreadyPaidotherRc());
					summaryResp.setUsrLiability(Summary.getAspadjOflibRc());
					summaryResp.setUsrOtherLiability(
							Summary.getAspadjOflibOtherRc());
					summaryResp.setUsrItcPaidIgst(Summary.getAsppaidThroughIgst());
					summaryResp.setUsrItcPaidCgst(Summary.getAsppaidThroughCgst());
					summaryResp.setUsrItcPaidSgst(Summary.getAsppaidThroughSgst());
					summaryResp.setUsrItcPaidCess(Summary.getAsppaidThroughCess());
					summaryResp.setUsrCashPaidInterest(
							Summary.getAsppaidIncash_interest());
					summaryResp.setUsrCashPaidTax(Summary.getAsppaidIncash_tax());
					summaryResp.setUsrCashPaidLateFee(
							Summary.getAsppaidIncash_latefee());
					summaryResp.setGstnPayable(Summary.getGstntaxPayable_rc());
					summaryResp
							.setGstnOtherPayable(Summary.getGstntaxPayable_Other_rc());
					summaryResp.setGstnPaid(Summary.getGstnalready_paid_rc());
					summaryResp.setGstnOtherPaid(Summary.getGstnalready_paid_Other_rc());
					summaryResp.setGstnLiability(Summary.getGstnadjOflibRc());
					summaryResp.setGstnOtherLiability(
							Summary.getGstnadjOflibOtherRc());
					summaryResp
							.setGstnItcPaidIgst(Summary.getGstnpaidThroughIgst());
					summaryResp
							.setGstnItcPaidCgst(Summary.getGstnpaidThroughCgst());
					summaryResp
							.setGstnItcPaidSgst(Summary.getGstnpaidThroughSgst());
					summaryResp
							.setGstnItcPaidCess(Summary.getGstnpaidThroughCess());
					summaryResp.setGstnCashPaidInterest(
							Summary.getGstnpaidIncash_interest());
					summaryResp.setGstnCashPaidTax(Summary.getGstnpaidIncash_tax());
					summaryResp.setGstnCashPaidLateFee(
							Summary.getGstnpaidIncash_latefee());

					summaryResp.setDiffPayable(
							subMethod(summaryResp.getUsrPayable(),
									summaryResp.getGstnPayable()));
					summaryResp.setDiffOtherPayable(
							subMethod(summaryResp.getUsrOtherPayable(),
									summaryResp.getGstnOtherPayable()));
					summaryResp.setDiffPaid(subMethod(summaryResp.getUsrPaid(),
							summaryResp.getGstnPaid()));
					summaryResp.setDiffOtherPaid(
							subMethod(summaryResp.getUsrOtherPaid(),
									summaryResp.getGstnOtherPaid()));
					summaryResp.setDiffLiability(
							subMethod(summaryResp.getUsrLiability(),
									summaryResp.getGstnLiability()));
					summaryResp.setDiffOtherLiability(
							subMethod(summaryResp.getUsrOtherLiability(),
									summaryResp.getGstnOtherLiability()));
					summaryResp.setDiffItcPaidIgst(
							subMethod(summaryResp.getUsrItcPaidIgst(),
									summaryResp.getGstnItcPaidIgst()));
					summaryResp.setDiffItcPaidCgst(
							subMethod(summaryResp.getUsrItcPaidCgst(),
									summaryResp.getGstnItcPaidCgst()));
					summaryResp.setDiffItcPaidSgst(
							subMethod(summaryResp.getUsrItcPaidSgst(),
									summaryResp.getGstnItcPaidSgst()));
					summaryResp.setDiffItcPaidCess(
							subMethod(summaryResp.getUsrItcPaidCess(),
									summaryResp.getGstnItcPaidCess()));
					summaryResp.setDiffCashPaidInterest(
							subMethod(summaryResp.getUsrCashPaidInterest(),
									summaryResp.getGstnCashPaidInterest()));
					summaryResp.setDiffCashPaidTax(
							subMethod(summaryResp.getUsrCashPaidTax(),
									summaryResp.getGstnCashPaidTax()));
					summaryResp.setDiffCashPaidLateFee(
							subMethod(summaryResp.getUsrCashPaidLateFee(),
									summaryResp.getGstnCashPaidLateFee()));
					listadd7_4.add(summaryResp);
				});
			}
		}
		
		List<TaxPaymentSummaryDto> paymentTotal = new ArrayList<>();
		
		
		List<TaxPaymentSummaryDto> int7RespBody = ret1Payment7Structure.ret1int7Resp(
				listadd7, null, "paymentTax",con_7);
		paymentTotal.addAll(int7RespBody);
		List<TaxPaymentSummaryDto> int7_1RespBody = ret1Payment7Structure.ret1int7Resp(
					listadd7_1, one, "igst",con_7_1);
		paymentTotal.addAll(int7_1RespBody);
		List<TaxPaymentSummaryDto> int7_2RespBody = ret1Payment7Structure.ret1int7Resp(
					listadd7_1, two, "cgst",con_7_2);
		paymentTotal.addAll(int7_2RespBody);
		List<TaxPaymentSummaryDto> int7_3RespBody = ret1Payment7Structure.ret1int7Resp(
					listadd7_1, three, "sgst",con_7_3);
		paymentTotal.addAll(int7_3RespBody);
		List<TaxPaymentSummaryDto> int7_4RespBody = ret1Payment7Structure.ret1int7Resp(
					listadd7_1, four, "cess",con_7_4);
		paymentTotal.addAll(int7_4RespBody);

		Map<String, JsonElement> combinedMap = new HashMap<>();

		JsonElement ret17SummaryRespbody = gson.toJsonTree(paymentTotal);
			combinedMap.put("tab_7", ret17SummaryRespbody);
		/*	combinedMap.put(con_7_1, int7_1RespBody);
			combinedMap.put(con_7_2, int7_2RespBody);
			combinedMap.put(con_7_3, int7_3RespBody);
			combinedMap.put(con_7_4, int7_4RespBody);*/

			JsonElement ret13ASummaryRespbody = gson.toJsonTree(combinedMap);
		
			return ret13ASummaryRespbody;
		
		
	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

}
