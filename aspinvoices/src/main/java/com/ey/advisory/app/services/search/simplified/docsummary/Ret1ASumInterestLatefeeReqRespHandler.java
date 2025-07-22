package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Ret1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Ret1LateFeeSummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.services.search.docsummarysearch.Ret1IntAndLateFeeStructure;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
/**
 * 
 * @author Mahesh.Golla
 *
 */


@Service("Ret1ASumInterestLatefeeReqRespHandler")
public class Ret1ASumInterestLatefeeReqRespHandler {
	
	
	@Autowired
	@Qualifier("Ret1SumInteAndLateFeeImplDocSummaryService")
	private Ret1SumInteAndLateFeeImplDocSummaryService service;
	
	@Autowired
	@Qualifier("Ret1IntAndLateFeeStructure")
	private Ret1IntAndLateFeeStructure ret1IntAndLateFeeStructure;
	
	public static final String one = "1";
	public static final String two = "2";
	public static final String three = "3";
	public static final String four = "4";
	
	public static final String con_5_1 = "5(1)";
	public static final String con_5_2 = "5(2)";
	public static final String con_5_3 = "5(3)";
	public static final String con_5_4 = "5(4)";
	public static final String con_5 = "5";
	

	public JsonElement handleRe1ReqAndResp(
			Annexure1SummaryReqDto ret1AreviewSum) {

		SearchResult<Ret1CompleteSummaryDto> summaryResult = service
				.<Ret1CompleteSummaryDto>find(ret1AreviewSum, null,
						Ret1CompleteSummaryDto.class);
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<? extends Ret1CompleteSummaryDto> list = summaryResult.getResult();
		
		
		List<Ret1LateFeeSummaryDto> listadd5_1 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd5_2 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd5_3 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd5_4 = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> listadd5 = new ArrayList<>();
		
		
		for (Ret1CompleteSummaryDto dto : list) {
			Ret1BasicSectionSummaryDto int_5_1 = dto.getInt_6_1();
			Ret1BasicSectionSummaryDto int_5_2 = dto.getInt_6_2();
			Ret1BasicSectionSummaryDto int_5_3 = dto.getInt_6_3();
			Ret1BasicSectionSummaryDto int_5_4 = dto.getInt_6_4();
			Ret1BasicSectionSummaryDto int_5 = dto.getInt_6();
			
			
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_1list = 
					int_5_1.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_2list = 
					int_5_2.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_3list = 
					int_5_3.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6_4list = 
					int_5_4.getEySummaryforint6();
			List<Ret1LateFeeSummarySectionDto> eySummaryforint5list = 
					int_5.getEySummaryforint6();
			
			
			if (eySummaryforint5list != null && 
					!eySummaryforint5list.isEmpty()) {
				eySummaryforint5list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp =
							new Ret1LateFeeSummaryDto();
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
					summaryResp.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());
					
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
						listadd5.add(summaryResp);
				});
			}
			
			if (eySummaryforint6_1list != null && 
					!eySummaryforint6_1list.isEmpty()) {
				eySummaryforint6_1list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp =
							new Ret1LateFeeSummaryDto();
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
					summaryResp.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());
					
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
						listadd5_1.add(summaryResp);
				});
			}
			if (eySummaryforint6_2list != null && 
					!eySummaryforint6_2list.isEmpty()) {
				eySummaryforint6_2list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp =
							new Ret1LateFeeSummaryDto();
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
					summaryResp.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());
					
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
						listadd5_2.add(summaryResp);
				});
			}
			if (eySummaryforint6_3list != null && 
					!eySummaryforint6_3list.isEmpty()) {
				eySummaryforint6_3list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp = 
							new Ret1LateFeeSummaryDto();
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
					summaryResp.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());
					
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
						listadd5_3.add(summaryResp);
				});
			}
			if (eySummaryforint6_4list != null &&
					!eySummaryforint6_4list.isEmpty()) {
				eySummaryforint6_4list.forEach(Summary -> {
					Ret1LateFeeSummaryDto summaryResp =
							new Ret1LateFeeSummaryDto();
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
					summaryResp.setGstnLateFeeCgst(Summary.getGstnlatefeeCgst());
					summaryResp.setGstnLateFeeSgst(Summary.getGstnlatefeeSgst());
					
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
						listadd5_4.add(summaryResp);
				});
			}
		}
		
		List<Ret1LateFeeSummaryDto> lateFeeList = new ArrayList<>();
		List<Ret1LateFeeSummaryDto> int5RespBody = 
				ret1IntAndLateFeeStructure.ret1int6Resp(listadd5,
				null, "a_interestNLateFee",con_5);
		lateFeeList.addAll(int5RespBody);
		List<Ret1LateFeeSummaryDto> int5_1RespBody = 
					ret1IntAndLateFeeStructure.ret1int6Resp(listadd5_1,
					one, "a_interestNFeeLateFiling",con_5_1);
		lateFeeList.addAll(int5_1RespBody);
		List<Ret1LateFeeSummaryDto> int5_2RespBody = 
					ret1IntAndLateFeeStructure.ret1int6Resp(listadd5_2,
					two, "a_interestItcReversal", con_5_2);
		lateFeeList.addAll(int5_2RespBody);
		List<Ret1LateFeeSummaryDto> int5_3RespBody =
					ret1IntAndLateFeeStructure.ret1int6Resp(listadd5_3,
					three, "a_lateReportingRCM", con_5_3);
		lateFeeList.addAll(int5_3RespBody);
		List<Ret1LateFeeSummaryDto> int5_4RespBody = 
					ret1IntAndLateFeeStructure.ret1int6Resp(listadd5_4,
					four, "a_otherInterest", con_5_4);
		lateFeeList.addAll(int5_4RespBody);
		Map<String, JsonElement> combinedMap = new HashMap<>();
		JsonElement ret1LateFeeRespbody = gson.toJsonTree(lateFeeList);
		
		combinedMap.put("tab_5", ret1LateFeeRespbody);
		/*combinedMap.put(con_5_2, int5_2RespBody);
		combinedMap.put(con_5_3, int5_3RespBody);
		combinedMap.put(con_5_4, int5_4RespBody);
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
