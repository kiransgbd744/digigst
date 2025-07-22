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

import com.ey.advisory.app.docs.dto.Ret1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1RefundSummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.RefundSummaryDto;
import com.ey.advisory.app.services.search.docsummarysearch.Ret1Refund8Structure;
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
@Component("Ret1SumRefund8ReqRespHandler")
@Slf4j
public class Ret1SumRefund8ReqRespHandler {

	@Autowired
	@Qualifier("Ret1SumRefund8SimplDocSummaryService")
	Ret1SumRefund8SimplDocSummaryService service;

	@Autowired
	@Qualifier("Ret1Refund8Structure")
	Ret1Refund8Structure refund8Resp;

	public static final String one = "1";
	public static final String two = "2";
	public static final String three = "3";
	public static final String four = "4";

	public static final String con_8 = "8";
	public static final String con_8_1 = "8(1)";
	public static final String con_8_2 = "8(2)";
	public static final String con_8_3 = "8(3)";
	public static final String con_8_4 = "8(4)";

	public JsonElement handleRe1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		
		SearchResult<Ret1CompleteSummaryDto> summaryResult = service
				.<Ret1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Ret1CompleteSummaryDto.class);

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<? extends Ret1CompleteSummaryDto> list = summaryResult.getResult();

		List<RefundSummaryDto> listadd8 = new ArrayList<>();
		List<RefundSummaryDto> listadd8_1 = new ArrayList<>();
		List<RefundSummaryDto> listadd8_2 = new ArrayList<>();
		List<RefundSummaryDto> listadd8_3 = new ArrayList<>();
		List<RefundSummaryDto> listadd8_4 = new ArrayList<>();

		for (Ret1CompleteSummaryDto dto : list) {

			Ret1BasicSectionSummaryDto refund_8 = dto.getRefund_8();
			Ret1BasicSectionSummaryDto refund_8_1 = dto.getRefund_8_1();
			Ret1BasicSectionSummaryDto refund_8_2 = dto.getRefund_8_2();
			Ret1BasicSectionSummaryDto refund_8_3 = dto.getRefund_8_3();
			Ret1BasicSectionSummaryDto refund_8_4 = dto.getRefund_8_4();

			List<Ret1RefundSummarySectionDto> eySummary8 = refund_8
					.getEySummaryRefund8();
			
			List<Ret1RefundSummarySectionDto> eySummary8_1 = refund_8_1
					.getEySummaryRefund8();
			List<Ret1RefundSummarySectionDto> eySummary8_2 = refund_8_2
					.getEySummaryRefund8();
			List<Ret1RefundSummarySectionDto> eySummary8_3 = refund_8_3
					.getEySummaryRefund8();
			List<Ret1RefundSummarySectionDto> eySummary8_4 = refund_8_4
					.getEySummaryRefund8();
			
			if (eySummary8 != null && !eySummary8.isEmpty()) {
				eySummary8.forEach(summary -> {
					RefundSummaryDto summaryResp = new RefundSummaryDto();
					summaryResp.setTable(summary.getTable());
					summaryResp.setDesc(summary.getDesc());
					summaryResp.setUsrTax(summary.getUsrTax());
					summaryResp.setUsrTotal(summary.getUsrTotal());
					summaryResp.setUsrPenality(summary.getUsrPenality());
					summaryResp.setUsrInterest(summary.getUsrInterest());
					summaryResp.setUsrFee(summary.getUsrFee());
					summaryResp.setUsrOther(summary.getUsrOther());
					summaryResp.setGstnTax(summary.getGstnTax());
					summaryResp.setGstnTotal(summary.getGstnTotal());
					summaryResp.setGstnPenality(summary.getGstnPenality());
					summaryResp.setGstnInterest(summary.getGstnInterest());
					summaryResp.setGstnFee(summary.getGstnFee());
					summaryResp.setGstnOther(summary.getGstnOther());
					summaryResp.setDiffTax(subMethod(summary.getUsrTax(),
							summary.getGstnTax()));
					summaryResp.setDiffTotal(subMethod(summary.getUsrTotal(),
							summary.getGstnTotal()));
					summaryResp
							.setDiffPenality(subMethod(summary.getUsrPenality(),
									summary.getGstnPenality()));
					summaryResp.setDiffOther(subMethod(summary.getUsrOther(),
							summary.getGstnOther()));
					summaryResp
							.setDiffInterest(subMethod(summary.getUsrInterest(),
									summary.getGstnInterest()));
					summaryResp.setDiffFee(subMethod(summary.getUsrFee(),
							summary.getGstnFee()));
					listadd8.add(summaryResp);
				});
			}

			if (eySummary8_1 != null && !eySummary8_1.isEmpty()) {
				eySummary8_1.forEach(summary -> {
					RefundSummaryDto summaryResp = new RefundSummaryDto();
					summaryResp.setTable(summary.getTable());
					summaryResp.setDesc(summary.getDesc());
					summaryResp.setUsrTax(summary.getUsrTax());
					summaryResp.setUsrTotal(summary.getUsrTotal());
					summaryResp.setUsrPenality(summary.getUsrPenality());
					summaryResp.setUsrInterest(summary.getUsrInterest());
					summaryResp.setUsrFee(summary.getUsrFee());
					summaryResp.setUsrOther(summary.getUsrOther());
					summaryResp.setGstnTax(summary.getGstnTax());
					summaryResp.setGstnTotal(summary.getGstnTotal());
					summaryResp.setGstnPenality(summary.getGstnPenality());
					summaryResp.setGstnInterest(summary.getGstnInterest());
					summaryResp.setGstnFee(summary.getGstnFee());
					summaryResp.setGstnOther(summary.getGstnOther());
					summaryResp.setDiffTax(subMethod(summary.getUsrTax(),
							summary.getGstnTax()));
					summaryResp.setDiffTotal(subMethod(summary.getUsrTotal(),
							summary.getGstnTotal()));
					summaryResp
							.setDiffPenality(subMethod(summary.getUsrPenality(),
									summary.getGstnPenality()));
					summaryResp.setDiffOther(subMethod(summary.getUsrOther(),
							summary.getGstnOther()));
					summaryResp
							.setDiffInterest(subMethod(summary.getUsrInterest(),
									summary.getGstnInterest()));
					summaryResp.setDiffFee(subMethod(summary.getUsrFee(),
							summary.getGstnFee()));
					listadd8_1.add(summaryResp);
				});
			}
			if (eySummary8_2 != null && !eySummary8_2.isEmpty()) {
				eySummary8_2.forEach(summary -> {
					RefundSummaryDto summaryResp = new RefundSummaryDto();
					summaryResp.setTable(summary.getTable());
					summaryResp.setDesc(summary.getDesc());
					summaryResp.setUsrTax(summary.getUsrTax());
					summaryResp.setUsrTotal(summary.getUsrTotal());
					summaryResp.setUsrPenality(summary.getUsrPenality());
					summaryResp.setUsrInterest(summary.getUsrInterest());
					summaryResp.setUsrFee(summary.getUsrFee());
					summaryResp.setUsrOther(summary.getUsrOther());
					summaryResp.setGstnTax(summary.getGstnTax());
					summaryResp.setGstnTotal(summary.getGstnTotal());
					summaryResp.setGstnPenality(summary.getGstnPenality());
					summaryResp.setGstnInterest(summary.getGstnInterest());
					summaryResp.setGstnFee(summary.getGstnFee());
					summaryResp.setGstnOther(summary.getGstnOther());
					summaryResp.setDiffTax(subMethod(summary.getUsrTax(),
							summary.getGstnTax()));
					summaryResp.setDiffTotal(subMethod(summary.getUsrTotal(),
							summary.getGstnTotal()));
					summaryResp
							.setDiffPenality(subMethod(summary.getUsrPenality(),
									summary.getGstnPenality()));
					summaryResp.setDiffOther(subMethod(summary.getUsrOther(),
							summary.getGstnOther()));
					summaryResp
							.setDiffInterest(subMethod(summary.getUsrInterest(),
									summary.getGstnInterest()));
					summaryResp.setDiffFee(subMethod(summary.getUsrFee(),
							summary.getGstnFee()));
					listadd8_2.add(summaryResp);
				});
			}
			if (eySummary8_3 != null && !eySummary8_3.isEmpty()) {
				eySummary8_3.forEach(summary -> {
					RefundSummaryDto summaryResp = new RefundSummaryDto();
					summaryResp.setTable(summary.getTable());
					summaryResp.setDesc(summary.getDesc());
					summaryResp.setUsrTax(summary.getUsrTax());
					summaryResp.setUsrTotal(summary.getUsrTotal());
					summaryResp.setUsrPenality(summary.getUsrPenality());
					summaryResp.setUsrInterest(summary.getUsrInterest());
					summaryResp.setUsrFee(summary.getUsrFee());
					summaryResp.setUsrOther(summary.getUsrOther());
					summaryResp.setGstnTax(summary.getGstnTax());
					summaryResp.setGstnTotal(summary.getGstnTotal());
					summaryResp.setGstnPenality(summary.getGstnPenality());
					summaryResp.setGstnInterest(summary.getGstnInterest());
					summaryResp.setGstnFee(summary.getGstnFee());
					summaryResp.setGstnOther(summary.getGstnOther());
					summaryResp.setDiffTax(subMethod(summary.getUsrTax(),
							summary.getGstnTax()));
					summaryResp.setDiffTotal(subMethod(summary.getUsrTotal(),
							summary.getGstnTotal()));
					summaryResp
							.setDiffPenality(subMethod(summary.getUsrPenality(),
									summary.getGstnPenality()));
					summaryResp.setDiffOther(subMethod(summary.getUsrOther(),
							summary.getGstnOther()));
					summaryResp
							.setDiffInterest(subMethod(summary.getUsrInterest(),
									summary.getGstnInterest()));
					summaryResp.setDiffFee(subMethod(summary.getUsrFee(),
							summary.getGstnFee()));
					listadd8_3.add(summaryResp);
				});
			}
			if (eySummary8_4 != null && !eySummary8_4.isEmpty()) {
				eySummary8_4.forEach(summary -> {
					RefundSummaryDto summaryResp = new RefundSummaryDto();
					summaryResp.setTable(summary.getTable());
					summaryResp.setDesc(summary.getDesc());
					summaryResp.setUsrTax(summary.getUsrTax());
					summaryResp.setUsrTotal(summary.getUsrTotal());
					summaryResp.setUsrPenality(summary.getUsrPenality());
					summaryResp.setUsrInterest(summary.getUsrInterest());
					summaryResp.setUsrFee(summary.getUsrFee());
					summaryResp.setUsrOther(summary.getUsrOther());
					summaryResp.setGstnTax(summary.getGstnTax());
					summaryResp.setGstnTotal(summary.getGstnTotal());
					summaryResp.setGstnPenality(summary.getGstnPenality());
					summaryResp.setGstnInterest(summary.getGstnInterest());
					summaryResp.setGstnFee(summary.getGstnFee());
					summaryResp.setGstnOther(summary.getGstnOther());
					summaryResp.setDiffTax(subMethod(summary.getUsrTax(),
							summary.getGstnTax()));
					summaryResp.setDiffTotal(subMethod(summary.getUsrTotal(),
							summary.getGstnTotal()));
					summaryResp
							.setDiffPenality(subMethod(summary.getUsrPenality(),
									summary.getGstnPenality()));
					summaryResp.setDiffOther(subMethod(summary.getUsrOther(),
							summary.getGstnOther()));
					summaryResp
							.setDiffInterest(subMethod(summary.getUsrInterest(),
									summary.getGstnInterest()));
					summaryResp.setDiffFee(subMethod(summary.getUsrFee(),
							summary.getGstnFee()));
					listadd8_4.add(summaryResp);
				});
			}
		}

		List<RefundSummaryDto> refTotal = new ArrayList<>();
		
		List<RefundSummaryDto> ret1Ref8Resp = refund8Resp.ret1Ref8Resp(listadd8, null,
				"cashLedgerRefund", con_8);
		refTotal.addAll(ret1Ref8Resp);
		List<RefundSummaryDto> ret1Ref8_1Resp = refund8Resp.ret1Ref8Resp(listadd8_1, one,
				"igst", con_8_1);
		refTotal.addAll(ret1Ref8_1Resp);
		List<RefundSummaryDto> ret1Ref8_2Resp = refund8Resp.ret1Ref8Resp(listadd8_1, two,
				"cgst", con_8_2);
		refTotal.addAll(ret1Ref8_2Resp);
		List<RefundSummaryDto> ret1Ref8_3Resp = refund8Resp.ret1Ref8Resp(listadd8_1, three,
				"sgst", con_8_3);
		refTotal.addAll(ret1Ref8_3Resp);
		List<RefundSummaryDto> ret1Ref8_4Resp = refund8Resp.ret1Ref8Resp(listadd8_1,four,
				"cess", con_8_4);
		refTotal.addAll(ret1Ref8_4Resp);
		
		JsonElement refund8 = gson.toJsonTree(refTotal);
		
		Map<String, JsonElement> combinedMap = new HashMap<>();

	//	JsonElement ret1Refund = gson.toJsonTree(refTotal);
		combinedMap.put("tab_8", refund8);
		
		JsonElement ret1RefundSummaryRespbody = gson.toJsonTree(combinedMap);

		return ret1RefundSummaryRespbody;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}
}
