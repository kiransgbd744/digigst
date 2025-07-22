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
import com.ey.advisory.app.docs.dto.Ret1LateFeeSummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.services.search.docsummarysearch.Ret1Int6Structure;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Component("Ret1SumLatefee6ReqRespHandler")
@Slf4j
public class Ret1SumLatefee6ReqRespHandler {

	@Autowired
	@Qualifier("Ret1SumLateFee6SimplDocSummaryService")
	Ret1SumLateFee6SimplDocSummaryService service;
	@Autowired
	@Qualifier("Ret1Int6Structure")
	Ret1Int6Structure ret1Int6Structure;
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

		SearchResult<Ret1CompleteSummaryDto> summaryResult = service
				.<Ret1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Ret1CompleteSummaryDto.class);
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<? extends Ret1CompleteSummaryDto> list = summaryResult.getResult();
		List<Ret1LateFeeSummaryDto> listadd6 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd6_1 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd6_2 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd6_3 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd6_4 = new ArrayList<>();
		for (Ret1CompleteSummaryDto dto : list) {
			Ret1BasicSectionSummaryDto int_6 = dto.getInt_6();
			Ret1BasicSectionSummaryDto int_6_1 = dto.getInt_6_1();
			Ret1BasicSectionSummaryDto int_6_2 = dto.getInt_6_2();
			Ret1BasicSectionSummaryDto int_6_3 = dto.getInt_6_3();
			Ret1BasicSectionSummaryDto int_6_4 = dto.getInt_6_4();
			
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6list = int_6
					.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_1list = int_6_1
					.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_2list = int_6_2
					.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_3list = int_6_3
					.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_4list = int_6_4
					.getEySummaryforint6();
			
			if (eySummaryforint6list != null
					&& !eySummaryforint6list.isEmpty()) {
				eySummaryforint6list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp = new Ret1LateFeeSummaryDto();
					// EY Resp
					summaryResp.setTable(Summary.getTable());
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrIgst(Summary.getAspIgst());
					summaryResp.setUsrCgst(Summary.getAspCgst());
					summaryResp.setUsrSgst(Summary.getAspSgst());
					summaryResp.setUsrCess(Summary.getAspCess());
					summaryResp.setUsrLateFeeCgst(Summary.getAsplatefeeCgst());
					summaryResp.setUsrLateFeeSgst(Summary.getAsplatefeeSgst());
					summaryResp.setGstnIgst(Summary.getGstnIgst());
					summaryResp.setGstnCgst(Summary.getGstnCgst());
					summaryResp.setGstnSgst(Summary.getGstnSgst());
					summaryResp.setGstnCess(Summary.getGstnCess());
					summaryResp
							.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp
							.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());

					summaryResp.setDiffIgst(subMethod(Summary.getAspIgst(),
							Summary.getGstnIgst()));
					summaryResp.setDiffCgst(subMethod(Summary.getAspCgst(),
							Summary.getGstnCgst()));
					summaryResp.setDiffSgst(subMethod(Summary.getAspSgst(),
							Summary.getGstnSgst()));
					summaryResp.setDiffCess(subMethod(Summary.getAspCess(),
							Summary.getGstnCess()));
					summaryResp.setDiffLateFeeCgst(
							subMethod(Summary.getAsplatefeeCgst(),
									Summary.getGstnlatefeeCgst()));
					summaryResp.setDiffLateFeeSgst(
							subMethod(Summary.getAsplatefeeSgst(),
									Summary.getAsplatefeeSgst()));

					listadd6.add(summaryResp);
				});
			}
			
			if (eySummaryforint6_1list != null
					&& !eySummaryforint6_1list.isEmpty()) {
				eySummaryforint6_1list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp = new Ret1LateFeeSummaryDto();
					// EY Resp
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection("1");
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrIgst(Summary.getAspIgst());
					summaryResp.setUsrCgst(Summary.getAspCgst());
					summaryResp.setUsrSgst(Summary.getAspSgst());
					summaryResp.setUsrCess(Summary.getAspCess());
					summaryResp.setUsrLateFeeCgst(Summary.getAsplatefeeCgst());
					summaryResp.setUsrLateFeeSgst(Summary.getAsplatefeeSgst());
					summaryResp.setGstnIgst(Summary.getGstnIgst());
					summaryResp.setGstnCgst(Summary.getGstnCgst());
					summaryResp.setGstnSgst(Summary.getGstnSgst());
					summaryResp.setGstnCess(Summary.getGstnCess());
					summaryResp
							.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp
							.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());

					summaryResp.setDiffIgst(subMethod(Summary.getAspIgst(),
							Summary.getGstnIgst()));
					summaryResp.setDiffCgst(subMethod(Summary.getAspCgst(),
							Summary.getGstnCgst()));
					summaryResp.setDiffSgst(subMethod(Summary.getAspSgst(),
							Summary.getGstnSgst()));
					summaryResp.setDiffCess(subMethod(Summary.getAspCess(),
							Summary.getGstnCess()));
					summaryResp.setDiffLateFeeCgst(
							subMethod(Summary.getAsplatefeeCgst(),
									Summary.getGstnlatefeeCgst()));
					summaryResp.setDiffLateFeeSgst(
							subMethod(Summary.getAsplatefeeSgst(),
									Summary.getAsplatefeeSgst()));

					listadd6_1.add(summaryResp);
				});
			}
			if (eySummaryforint6_2list != null
					&& !eySummaryforint6_2list.isEmpty()) {
				eySummaryforint6_2list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp = new Ret1LateFeeSummaryDto();
					// EY Resp
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection("2");
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrIgst(Summary.getAspIgst());
					summaryResp.setUsrCgst(Summary.getAspCgst());
					summaryResp.setUsrSgst(Summary.getAspSgst());
					summaryResp.setUsrCess(Summary.getAspCess());
					summaryResp.setUsrLateFeeCgst(Summary.getAsplatefeeCgst());
					summaryResp.setUsrLateFeeSgst(Summary.getAsplatefeeSgst());
					summaryResp.setGstnIgst(Summary.getGstnIgst());
					summaryResp.setGstnCgst(Summary.getGstnCgst());
					summaryResp.setGstnSgst(Summary.getGstnSgst());
					summaryResp.setGstnCess(Summary.getGstnCess());
					summaryResp
							.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp
							.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());

					summaryResp.setDiffIgst(subMethod(Summary.getAspIgst(),
							Summary.getGstnIgst()));
					summaryResp.setDiffCgst(subMethod(Summary.getAspCgst(),
							Summary.getGstnCgst()));
					summaryResp.setDiffSgst(subMethod(Summary.getAspSgst(),
							Summary.getGstnSgst()));
					summaryResp.setDiffCess(subMethod(Summary.getAspCess(),
							Summary.getGstnCess()));
					summaryResp.setDiffLateFeeCgst(
							subMethod(Summary.getAsplatefeeCgst(),
									Summary.getGstnlatefeeCgst()));
					summaryResp.setDiffLateFeeSgst(
							subMethod(Summary.getAsplatefeeSgst(),
									Summary.getAsplatefeeSgst()));

					listadd6_2.add(summaryResp);
				});
			}
			if (eySummaryforint6_3list != null
					&& !eySummaryforint6_3list.isEmpty()) {
				eySummaryforint6_3list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp = new Ret1LateFeeSummaryDto();
					// EY Resp
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection("3");
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrIgst(Summary.getAspIgst());
					summaryResp.setUsrCgst(Summary.getAspCgst());
					summaryResp.setUsrSgst(Summary.getAspSgst());
					summaryResp.setUsrCess(Summary.getAspCess());
					summaryResp.setUsrLateFeeCgst(Summary.getAsplatefeeCgst());
					summaryResp.setUsrLateFeeSgst(Summary.getAsplatefeeSgst());
					summaryResp.setGstnIgst(Summary.getGstnIgst());
					summaryResp.setGstnCgst(Summary.getGstnCgst());
					summaryResp.setGstnSgst(Summary.getGstnSgst());
					summaryResp.setGstnCess(Summary.getGstnCess());
					summaryResp
							.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp
							.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());

					summaryResp.setDiffIgst(subMethod(Summary.getAspIgst(),
							Summary.getGstnIgst()));
					summaryResp.setDiffCgst(subMethod(Summary.getAspCgst(),
							Summary.getGstnCgst()));
					summaryResp.setDiffSgst(subMethod(Summary.getAspSgst(),
							Summary.getGstnSgst()));
					summaryResp.setDiffCess(subMethod(Summary.getAspCess(),
							Summary.getGstnCess()));
					summaryResp.setDiffLateFeeCgst(
							subMethod(Summary.getAsplatefeeCgst(),
									Summary.getGstnlatefeeCgst()));
					summaryResp.setDiffLateFeeSgst(
							subMethod(Summary.getAsplatefeeSgst(),
									Summary.getAsplatefeeSgst()));

					listadd6_4.add(summaryResp);
				});
			}
			if (eySummaryforint6_4list != null
					&& !eySummaryforint6_4list.isEmpty()) {
				eySummaryforint6_4list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp = new Ret1LateFeeSummaryDto();
					// EY Resp
					summaryResp.setTable(Summary.getTable());
					summaryResp.setSection("4");
					summaryResp.setDesc(Summary.getSupplyType());
					summaryResp.setUsrIgst(Summary.getAspIgst());
					summaryResp.setUsrCgst(Summary.getAspCgst());
					summaryResp.setUsrSgst(Summary.getAspSgst());
					summaryResp.setUsrCess(Summary.getAspCess());
					summaryResp.setUsrLateFeeCgst(Summary.getAsplatefeeCgst());
					summaryResp.setUsrLateFeeSgst(Summary.getAsplatefeeSgst());
					summaryResp.setGstnIgst(Summary.getGstnIgst());
					summaryResp.setGstnCgst(Summary.getGstnCgst());
					summaryResp.setGstnSgst(Summary.getGstnSgst());
					summaryResp.setGstnCess(Summary.getGstnCess());
					summaryResp
							.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp
							.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());

					summaryResp.setDiffIgst(subMethod(Summary.getAspIgst(),
							Summary.getGstnIgst()));
					summaryResp.setDiffCgst(subMethod(Summary.getAspCgst(),
							Summary.getGstnCgst()));
					summaryResp.setDiffSgst(subMethod(Summary.getAspSgst(),
							Summary.getGstnSgst()));
					summaryResp.setDiffCess(subMethod(Summary.getAspCess(),
							Summary.getGstnCess()));
					summaryResp.setDiffLateFeeCgst(
							subMethod(Summary.getAsplatefeeCgst(),
									Summary.getGstnlatefeeCgst()));
					summaryResp.setDiffLateFeeSgst(
							subMethod(Summary.getAsplatefeeSgst(),
									Summary.getAsplatefeeSgst()));

					listadd6_4.add(summaryResp);
				});
			}
		}
		
		List<Ret1LateFeeSummaryDto> lateFeeTotal = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> int6RespBody = ret1Int6Structure.ret1int6Resp(listadd6,
				null, "interestNLateFee", con_6);
		lateFeeTotal.addAll(int6RespBody);
		List<Ret1LateFeeSummaryDto> int6_1RespBody = ret1Int6Structure.ret1int6Resp(listadd6_1,
				one, "interestNFeeLateFiling", con_6_1);
		lateFeeTotal.addAll(int6_1RespBody);
		List<Ret1LateFeeSummaryDto> int6_2RespBody = ret1Int6Structure.ret1int6Resp(listadd6_2,
				two, "interestItcReversal", con_6_2);
		lateFeeTotal.addAll(int6_2RespBody);
		List<Ret1LateFeeSummaryDto> int6_3RespBody = ret1Int6Structure.ret1int6Resp(listadd6_3,
				three, "lateReportingRCM", con_6_3);
		lateFeeTotal.addAll(int6_3RespBody);
		List<Ret1LateFeeSummaryDto> int6_4RespBody = ret1Int6Structure.ret1int6Resp(listadd6_4,
				four, "otherInterest", con_6_4);
		lateFeeTotal.addAll(int6_4RespBody);
		Map<String, JsonElement> combinedMap = new HashMap<>();
		
		JsonElement ret16SummaryRespbody = gson.toJsonTree(lateFeeTotal);
		combinedMap.put("tab_6", ret16SummaryRespbody);
	/*	combinedMap.put(con_6_1, int6_1RespBody);
		combinedMap.put(con_6_2, int6_2RespBody);
		combinedMap.put(con_6_3, int6_3RespBody);
		combinedMap.put(con_6_4, int6_4RespBody);
*/
		JsonElement ret13ASummaryRespbody = gson.toJsonTree(combinedMap);

		return ret13ASummaryRespbody;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

}
