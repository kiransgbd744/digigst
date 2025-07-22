/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicCDSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.ProcesSubmitHSNSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.ProcsSubmitSummaryRespHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr1AProcsSubmitScreenHSNReqRespHandler")
public class Gstr1AProcsSubmitScreenHSNReqRespHandler {

	public static final String hsnconst = "HSN";

	@Autowired
	@Qualifier("Gstr1AProcesSubmitHSNSummarySearchService")
	Gstr1AProcesSubmitHSNSummarySearchService service;

	@Autowired
	@Qualifier("ProcsSubmitSummaryRespHandler")
	ProcsSubmitSummaryRespHandler respHandler;

	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenRespDto> handleGstr1HsnReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		LOGGER.debug(" Summary Execution For HSN  Section BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug(" Summary Execution For HSN  Section END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		List<Gstr1SummaryScreenRespDto> hsnSummaryRespList = new ArrayList<>();
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicCDSectionSummaryDto hsn = dto.getHsn();

			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			List<Gstr1SummaryCDSectionDto> hsnSummary = hsn.getEySummary();

			if (hsnSummary != null) {
				hsnSummary.forEach(hsnEySummary -> {

					summaryResp.setTaxDocType(hsnEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							hsnEySummary.getAspTaxableValue());
					summaryResp.setAspTaxPayble(hsnEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(
							hsnEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(hsnEySummary.getAspIgst());
					summaryResp.setAspCgst(hsnEySummary.getAspCgst());
					summaryResp.setAspSgst(hsnEySummary.getAspSgst());
					summaryResp.setAspCess(hsnEySummary.getAspCess());
					summaryResp.setAspCount(hsnEySummary.getAspCount());

					summaryResp.setGstnTaxableValue(
							hsnEySummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(hsnEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(
							hsnEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(hsnEySummary.getGstnIgst());
					summaryResp.setGstnCgst(hsnEySummary.getGstnCgst());
					summaryResp.setGstnSgst(hsnEySummary.getGstnSgst());
					summaryResp.setGstnCess(hsnEySummary.getGstnCess());
					summaryResp.setGstnCount(hsnEySummary.getGstnCount());

				});
			}
			/*
			 * Calculating Difference Asp - Gstn
			 */
			BigDecimal diffTotaltax = subMethod(
					summaryResp.getAspInvoiceValue(),
					summaryResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(summaryResp.getAspTaxableValue(),
					summaryResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(summaryResp.getAspTaxPayble(),
					summaryResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
					summaryResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryResp.getAspSgst(),
					summaryResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
					summaryResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
					summaryResp.getGstnCess());

			Integer aspCount = (summaryResp.getAspCount() != null)
					? summaryResp.getAspCount() : 0;
			Integer gstnCount = (summaryResp.getGstnCount() != null)
					? summaryResp.getGstnCount() : 0;

			summaryResp.setDiffCount(aspCount - gstnCount);
			summaryResp.setDiffInvoiceValue(diffTotaltax);
			summaryResp.setDiffTaxableValue(diffTaxable);
			summaryResp.setDiffTaxPayble(diffTaxPayble);
			summaryResp.setDiffIgst(diffIgst);
			summaryResp.setDiffCgst(diffCgst);
			summaryResp.setDiffSgst(diffSgst);
			summaryResp.setDiffCess(diffCess);
			hsnSummaryRespList.add(summaryResp);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<Gstr1SummaryScreenRespDto> handleHSNResp = respHandler
				.pvsSubmitB2BResp(hsnSummaryRespList, "HSN");

		return handleHSNResp;
	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}
}
