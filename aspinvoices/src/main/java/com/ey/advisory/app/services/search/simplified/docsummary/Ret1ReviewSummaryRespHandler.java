package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Component("Ret1ReviewSummaryRespHandler")
public class Ret1ReviewSummaryRespHandler {

	public JsonElement handleRet1ReqAndRespWithDetailData(
			Annexure1SummaryReqDto ret1SummaryRequest,
			List<? extends Ret1SummaryRespDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Ret1SummaryRespDto dto = new Ret1SummaryRespDto();
		List<Ret1SummaryRespDto> summaryCombinedList = new ArrayList<>();
		summaryCombinedList.add(dto);
		JsonElement summaryRespbody = gson.toJsonTree(summaryCombinedList);
		return summaryRespbody;

	}

	public JsonElement handleRet1ReqAndRespWithLateFeeData(
			Annexure1SummaryReqDto ret1SummaryRequest,
			List<? extends Ret1LateFeeSummaryDto> gstnResult) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		Ret1LateFeeSummaryDto dto = new Ret1LateFeeSummaryDto();
		List<Ret1LateFeeSummaryDto> summaryCombinedList = new ArrayList<>();
		summaryCombinedList.add(dto);
		JsonElement summaryRespbody = gson.toJsonTree(summaryCombinedList);
		return summaryRespbody;
	}

	public JsonElement handleRet1ReqAndRespWithTaxPaymentData(
			Annexure1SummaryReqDto ret1SummaryRequest) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		TaxPaymentSummaryDto dto = new TaxPaymentSummaryDto();
		List<TaxPaymentSummaryDto> summaryCombinedList = new ArrayList<>();
		summaryCombinedList.add(dto);
		JsonElement summaryRespbody = gson.toJsonTree(summaryCombinedList);
		return summaryRespbody;
	}

}
