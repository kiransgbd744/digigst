package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx1.SavestatusReqDto;
import com.ey.advisory.app.docs.dto.simplified.DifferenceStatusRetSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.RefundSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("DifferenceStatusCommonRetService")
public class DifferenceStatusCommonRetService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DifferenceStatusCommonRetService.class);

	@Autowired
	@Qualifier("Ret1ReqRespHandler")
	Ret1ReqRespHandler ret1ReqRespHandler;

	@Autowired
	@Qualifier("Ret1SumReqRespHandler")
	Ret1SumReqRespHandler retSum1ReqRespHandler;

	@Autowired
	@Qualifier("Ret1SumLatefee6ReqRespHandler")
	Ret1SumLatefee6ReqRespHandler lateFess;

	@Autowired
	@Qualifier("Ret1SumPayment7ReqRespHandler")
	Ret1SumPayment7ReqRespHandler taxPayment;

	@Autowired
	@Qualifier("Ret1SumRefund8ReqRespHandler")
	Ret1SumRefund8ReqRespHandler refund;

	@Autowired
	@Qualifier("Annexure1ReqRespHandler")
	Annexure1ReqRespHandler annexure1ReqRespHandler;

	@Autowired
	@Qualifier("Ret1aReqRespHandler")
	Ret1aReqRespHandler ret1aReqRespHandler;

	public Map<? extends String, ? extends DifferenceStatusRetSummaryRespDto> getDifferenceForRet1(
			SavestatusReqDto criteria, Gson gson) {

		Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap = new HashMap<>();
		Annexure1SummaryReqDto annexure1SummaryRequest = new Annexure1SummaryReqDto();
		annexure1SummaryRequest.setTaxPeriod(criteria.getTaxPeriod());
		annexure1SummaryRequest.getEntityId()
				.add(Long.parseLong(criteria.getEntityId()));

		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put("GSTIN", Arrays.asList(criteria.getGstin()));
		annexure1SummaryRequest.setDataSecAttrs(dataSecAttrs);

		JsonElement handleRet1ReqAndResp = retSum1ReqRespHandler
				.handleRe1ReqAndResp(annexure1SummaryRequest);
		convertRet1SummaryToDiffSummary(
				handleRet1ReqAndResp, diffRespDtosMap, Arrays.asList("A3", "B3",
						"C3", "D3", "E3", "A4", "B4", "C4", "D4", "E4", "5"),
				gson);

		JsonElement handleRet1ReqLateFee = lateFess
				.handleRe1ReqAndResp(annexure1SummaryRequest);
		convertRet1SummaryToLateFeeDiffSummary(handleRet1ReqLateFee,
				diffRespDtosMap, Arrays.asList("tab_6"), gson);

		JsonElement handleRet1ReqTaxPayment = taxPayment
				.handleRe1ReqAndResp(annexure1SummaryRequest);
		convertRet1SummaryToTaxPaymentDiffSummary(handleRet1ReqTaxPayment,
				diffRespDtosMap, Arrays.asList("tab_7"), gson);

		JsonElement handleRe1ReqRefund = refund
				.handleRe1ReqAndResp(annexure1SummaryRequest);
		convertRet1SummaryToRefundDiffSummary(handleRe1ReqRefund,
				diffRespDtosMap, Arrays.asList("tab_8"), gson);

		return diffRespDtosMap;

	}

	private void convertRet1SummaryToRefundDiffSummary(
			JsonElement handleRe1ReqRefund,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap,
			List<String> asList, Gson gson) {
		java.lang.reflect.Type type = new TypeToken<Map<String, List<RefundSummaryDto>>>() {
		}.getType();
		Map<String, List<RefundSummaryDto>> summaryList = gson
				.fromJson(handleRe1ReqRefund, type);
		asList.forEach(section -> {
			List<RefundSummaryDto> sectionDataList = summaryList.get(section);
			convertRet1RefundToMapBySection(sectionDataList.get(0), section,
					diffRespDtosMap);
		});

	}

	private void convertRet1RefundToMapBySection(
			RefundSummaryDto refundSummaryDto, String section,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap) {
		DifferenceStatusRetSummaryRespDto respDto = new DifferenceStatusRetSummaryRespDto();
		respDto.setSection("0");
		respDto.setValue(BigDecimal.ZERO);
		respDto.setIgst(BigDecimal.ZERO);
		respDto.setCgst(BigDecimal.ZERO);
		respDto.setSgst(BigDecimal.ZERO);
		respDto.setCess(BigDecimal.ZERO);

		diffRespDtosMap.put(section, respDto);

	}

	@SuppressWarnings("serial")
	private void convertRet1SummaryToTaxPaymentDiffSummary(
			JsonElement handleRet1ReqTaxPayment,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap,
			List<String> asList, Gson gson) {
		java.lang.reflect.Type type = new TypeToken<Map<String, List<TaxPaymentSummaryDto>>>() {
		}.getType();
		Map<String, List<TaxPaymentSummaryDto>> summaryList = gson
				.fromJson(handleRet1ReqTaxPayment, type);
		asList.forEach(section -> {
			List<TaxPaymentSummaryDto> sectionDataList = summaryList
					.get(section);
			convertRet1TaxPaymenttoMapBySection(sectionDataList.get(0), section,
					diffRespDtosMap);
		});

	}

	private void convertRet1TaxPaymenttoMapBySection(
			TaxPaymentSummaryDto taxPaymentSummaryDto, String section,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap) {
		DifferenceStatusRetSummaryRespDto respDto = new DifferenceStatusRetSummaryRespDto();
		respDto.setSection(taxPaymentSummaryDto.getTable());
		respDto.setValue(taxPaymentSummaryDto.getDiffItcPaidIgst());
		respDto.setIgst(taxPaymentSummaryDto.getDiffItcPaidIgst());
		respDto.setCgst(taxPaymentSummaryDto.getDiffItcPaidCgst());
		respDto.setSgst(taxPaymentSummaryDto.getDiffItcPaidSgst());
		respDto.setCess(taxPaymentSummaryDto.getDiffItcPaidCess());

		diffRespDtosMap.put(section, respDto);

	}

	private void convertRet1SummaryToLateFeeDiffSummary(
			JsonElement handleRet1ReqLateFee,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap,
			List<String> asList, Gson gson) {
		java.lang.reflect.Type type = new TypeToken<Map<String, List<Ret1LateFeeSummaryDto>>>() {
		}.getType();
		Map<String, List<Ret1LateFeeSummaryDto>> summaryList = gson
				.fromJson(handleRet1ReqLateFee, type);
		asList.forEach(section -> {
			List<Ret1LateFeeSummaryDto> sectionDataList = summaryList
					.get(section);
			convertRet1LateFeetoMapBySection(sectionDataList.get(0), section,
					diffRespDtosMap);
		});

	}

	private void convertRet1LateFeetoMapBySection(
			Ret1LateFeeSummaryDto lateFeeSummaryDto, String section,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap) {
		DifferenceStatusRetSummaryRespDto respDto = new DifferenceStatusRetSummaryRespDto();
		respDto.setSection(lateFeeSummaryDto.getTable());
		respDto.setSection(lateFeeSummaryDto.getTable());
		respDto.setValue(BigDecimal.ZERO);
		respDto.setIgst(lateFeeSummaryDto.getDiffIgst());
		respDto.setCgst(lateFeeSummaryDto.getDiffCgst());
		respDto.setSgst(lateFeeSummaryDto.getDiffSgst());
		respDto.setCess(lateFeeSummaryDto.getDiffCess());

		diffRespDtosMap.put(section, respDto);

	}

	private void convertRet1SummaryToDiffSummary(
			JsonElement handleRet1ReqAndResp,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap,
			List<String> asList, Gson gson) {

		java.lang.reflect.Type type = new TypeToken<Map<String, List<Ret1SummaryRespDto>>>() {
		}.getType();
		Map<String, List<Ret1SummaryRespDto>> summaryList = gson
				.fromJson(handleRet1ReqAndResp, type);
		asList.forEach(section -> {
			List<Ret1SummaryRespDto> sectionDataList = summaryList.get(section);
			convertRet1AndAddtoMapBySection(sectionDataList.get(0), section,
					diffRespDtosMap);
		});

	}

	private void convertRet1AndAddtoMapBySection(
			Ret1SummaryRespDto ret1SummaryRespDto, String section,
			Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap) {

		DifferenceStatusRetSummaryRespDto respDto = new DifferenceStatusRetSummaryRespDto();
		respDto.setSection(ret1SummaryRespDto.getTable());
		respDto.setSupplyType(ret1SummaryRespDto.getSupplyType());
		respDto.setValue(ret1SummaryRespDto.getDiffValue());
		respDto.setIgst(ret1SummaryRespDto.getDiffIgst());
		respDto.setCgst(ret1SummaryRespDto.getDiffCgst());
		respDto.setSgst(ret1SummaryRespDto.getDiffSgst());
		respDto.setCess(ret1SummaryRespDto.getDiffCess());

		diffRespDtosMap.put(section, respDto);

	}

	public Map<? extends String, ? extends DifferenceStatusRetSummaryRespDto> getDifferenceForRet1a(
			SavestatusReqDto criteria, Gson gson) {
		Map<String, DifferenceStatusRetSummaryRespDto> diffRespDtosMap = new HashMap<>();
		Annexure1SummaryReqDto annexure1SummaryRequest = new Annexure1SummaryReqDto();
		annexure1SummaryRequest.setTaxPeriod(criteria.getTaxPeriod());
		annexure1SummaryRequest.getEntityId()
				.add(Long.parseLong(criteria.getEntityId()));

		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put("GSTIN", Arrays.asList(criteria.getGstin()));
		annexure1SummaryRequest.setDataSecAttrs(dataSecAttrs);

		Map<String, JsonElement> handleRet1ReqAndResp = ret1aReqRespHandler
				.handleRet1aReqAndResp(annexure1SummaryRequest);

		JsonElement summaryAspVRespbody = handleRet1ReqAndResp.get("aspValues");
		JsonElement summaryLateFeeRespbody = handleRet1ReqAndResp
				.get("lateFee");
		JsonElement taxSummaryRespbody = handleRet1ReqAndResp.get("taxPayment");

		convertRet1SummaryToDiffSummary(summaryAspVRespbody, diffRespDtosMap,
				Arrays.asList("3A", "3B", "3C", "3D", "3E", "4A", "4B", "4C"),
				gson);

		convertRet1SummaryToLateFeeDiffSummary(summaryLateFeeRespbody,
				diffRespDtosMap, Arrays.asList("5"), gson);

		convertRet1SummaryToTaxPaymentDiffSummary(taxSummaryRespbody,
				diffRespDtosMap, Arrays.asList("6"), gson);

		return diffRespDtosMap;
	}

}
