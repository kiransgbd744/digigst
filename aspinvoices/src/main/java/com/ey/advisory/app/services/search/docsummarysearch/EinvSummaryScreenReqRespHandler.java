/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicCDSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.EinvSimpleDocSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1B2BASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1B2BSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1B2CLASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1B2CLSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1B2CSASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1B2CSSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1CDNRASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1CDNRSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1CDNURASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1CDNURSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1EXPASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1EXPSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.ProcsSubmitSummaryRespHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@Service("EinvSummaryScreenReqRespHandler")
public class EinvSummaryScreenReqRespHandler {

	@Autowired
	@Qualifier("EinvSimpleDocSummarySearchService")
	EinvSimpleDocSummarySearchService service;

	/*@Autowired
	@Qualifier("Gstr1B2BSummaryRespHandler")
	Gstr1B2BSummaryRespHandler gstr1B2BRespHandler;
	*/
	@Autowired
	@Qualifier("ProcsSubmitSummaryRespHandler")
	ProcsSubmitSummaryRespHandler respHandler;

	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenRespDto> handleGstr1ReqAndResp(
			EinvoiceProcessedReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {
		
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(" Summary Execution For B2B,CDNR,CDNUR");
		}
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(" Summary Execution For B2B,CDNR,CDNUR END ");
		}

		/*
		 * List<? extends Gstr1CompleteSummaryDto> gstnResult =
		 * GstnSummaryResult .getResult();
		 */
		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		List<Gstr1SummaryScreenRespDto> b2bSummaryRespList = new ArrayList<>();
		
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2b = dto.getB2b();
			List<Gstr1SummarySectionDto> b2bSummary = b2b.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (b2bSummary != null) {
				b2bSummary.forEach(b2bEySummary -> {
					summaryResp.setTaxDocType(b2bEySummary.getTaxDocType());
					summaryResp
							.setAspTaxableValue(b2bEySummary.getAspTaxableValue());
					summaryResp.setAspTaxPayble(b2bEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(b2bEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(b2bEySummary.getAspIgst());
					summaryResp.setAspCgst(b2bEySummary.getAspCgst());
					summaryResp.setAspSgst(b2bEySummary.getAspSgst());
					summaryResp.setAspCess(b2bEySummary.getAspCess());
					summaryResp.setAspCount(b2bEySummary.getAspCount());
					
					summaryResp.setGstnTaxableValue(b2bEySummary.getGstnTaxableValue());
					summaryResp.setGstnTaxPayble(b2bEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(b2bEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(b2bEySummary.getGstnIgst());
					summaryResp.setGstnCgst(b2bEySummary.getGstnCgst());
					summaryResp.setGstnSgst(b2bEySummary.getGstnSgst());
					summaryResp.setGstnCess(b2bEySummary.getGstnCess());
					summaryResp.setGstnCount(b2bEySummary.getGstnCount());

				});
			}
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
			b2bSummaryRespList.add(summaryResp);		}
			/*
		 * Exp Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> expSummaryRespList = new ArrayList<>();
			for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto exp = dto.getExp();
			List<Gstr1SummarySectionDto> expSummary = exp.getEySummary();
			Gstr1SummaryScreenRespDto summaryExpResp = new Gstr1SummaryScreenRespDto();
			if (expSummary != null) {
				expSummary.forEach(expEySummary -> {

					summaryExpResp.setTaxDocType(expEySummary.getTaxDocType());
					summaryExpResp
							.setAspTaxableValue(expEySummary.getAspTaxableValue());
					summaryExpResp.setAspTaxPayble(expEySummary.getAspTaxPayble());
					summaryExpResp.setAspInvoiceValue(expEySummary.getAspInvoiceValue());
					summaryExpResp.setAspIgst(expEySummary.getAspIgst());
					summaryExpResp.setAspCgst(expEySummary.getAspCgst());
					summaryExpResp.setAspSgst(expEySummary.getAspSgst());
					summaryExpResp.setAspCess(expEySummary.getAspCess());
					summaryExpResp.setAspCount(expEySummary.getAspCount());
					
					summaryExpResp.setGstnTaxableValue(expEySummary.getGstnTaxableValue());
					summaryExpResp.setGstnTaxPayble(expEySummary.getGstnTaxPayble());
					summaryExpResp.setGstnInvoiceValue(expEySummary.getGstnInvoiceValue());
					summaryExpResp.setGstnIgst(expEySummary.getGstnIgst());
					summaryExpResp.setGstnCgst(expEySummary.getGstnCgst());
					summaryExpResp.setGstnSgst(expEySummary.getGstnSgst());
					summaryExpResp.setGstnCess(expEySummary.getGstnCess());
					summaryExpResp.setGstnCount(expEySummary.getGstnCount());

				});
			}
			
			/*
			 * Calculating Difference Asp - Gstn
			 */

			BigDecimal diffInv = subMethod(summaryExpResp.getAspInvoiceValue(),
					summaryExpResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryExpResp.getAspTaxableValue(),
					summaryExpResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryExpResp.getAspTaxPayble(),
					summaryExpResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryExpResp.getAspIgst(),
					summaryExpResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryExpResp.getAspSgst(),
					summaryExpResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryExpResp.getAspCgst(),
					summaryExpResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryExpResp.getAspCess(),
					summaryExpResp.getGstnCess());

			Integer aspCount = (summaryExpResp.getAspCount() != null)
					? summaryExpResp.getAspCount() : 0;
			Integer gstnCount = (summaryExpResp.getGstnCount() != null)
					? summaryExpResp.getGstnCount() : 0;

			summaryExpResp.setDiffCount(aspCount - gstnCount);
			summaryExpResp.setDiffInvoiceValue(diffInv);
			summaryExpResp.setDiffTaxableValue(diffTaxable);
			summaryExpResp.setDiffTaxPayble(diffTaxPayble);
			summaryExpResp.setDiffIgst(diffIgst);
			summaryExpResp.setDiffCgst(diffCgst);
			summaryExpResp.setDiffSgst(diffSgst);
			summaryExpResp.setDiffCess(diffCess);
			expSummaryRespList.add(summaryExpResp);
		}
			/*
		 * CDNR Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> cdnrSummaryRespList = new ArrayList<>();

	
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnr = dto.getCdnr();
			Gstr1SummaryScreenRespDto summaryCdnrResp = new Gstr1SummaryScreenRespDto();
				List<Gstr1SummarySectionDto> cdnrSummary = cdnr
						.getEySummary();		
			if (cdnrSummary != null) {
				cdnrSummary.forEach(cdnrEySummary -> {
					// ASP Data

					summaryCdnrResp.setTaxDocType(cdnrEySummary.getTaxDocType());
					summaryCdnrResp
							.setAspTaxableValue(cdnrEySummary.getAspTaxableValue());
					summaryCdnrResp.setAspTaxPayble(cdnrEySummary.getAspTaxPayble());
					summaryCdnrResp.setAspInvoiceValue(cdnrEySummary.getAspInvoiceValue());
					summaryCdnrResp.setAspIgst(cdnrEySummary.getAspIgst());
					summaryCdnrResp.setAspCgst(cdnrEySummary.getAspCgst());
					summaryCdnrResp.setAspSgst(cdnrEySummary.getAspSgst());
					summaryCdnrResp.setAspCess(cdnrEySummary.getAspCess());
					summaryCdnrResp.setAspCount(cdnrEySummary.getAspCount());
					
					summaryCdnrResp.setGstnTaxableValue(cdnrEySummary.getGstnTaxableValue());
					summaryCdnrResp.setGstnTaxPayble(cdnrEySummary.getGstnTaxPayble());
					summaryCdnrResp.setGstnInvoiceValue(cdnrEySummary.getGstnInvoiceValue());
					summaryCdnrResp.setGstnIgst(cdnrEySummary.getGstnIgst());
					summaryCdnrResp.setGstnCgst(cdnrEySummary.getGstnCgst());
					summaryCdnrResp.setGstnSgst(cdnrEySummary.getGstnSgst());
					summaryCdnrResp.setGstnCess(cdnrEySummary.getGstnCess());
					summaryCdnrResp.setGstnCount(cdnrEySummary.getGstnCount());


				});
			}
			/*
			 * Calculating Difference Asp - Gstn
			 */
			BigDecimal diffInv = subMethod(summaryCdnrResp.getAspInvoiceValue(),
					summaryCdnrResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryCdnrResp.getAspTaxableValue(),
					summaryCdnrResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryCdnrResp.getAspTaxPayble(),
					summaryCdnrResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryCdnrResp.getAspIgst(),
					summaryCdnrResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryCdnrResp.getAspSgst(),
					summaryCdnrResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryCdnrResp.getAspCgst(),
					summaryCdnrResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryCdnrResp.getAspCess(),
					summaryCdnrResp.getGstnCess());

			Integer aspCount = (summaryCdnrResp.getAspCount() != null)
					? summaryCdnrResp.getAspCount() : 0;
			Integer gstnCount = (summaryCdnrResp.getGstnCount() != null)
					? summaryCdnrResp.getGstnCount() : 0;

			summaryCdnrResp.setDiffCount(aspCount - gstnCount);
			summaryCdnrResp.setDiffInvoiceValue(diffInv);
			summaryCdnrResp.setDiffTaxableValue(diffTaxable);
			summaryCdnrResp.setDiffTaxPayble(diffTaxPayble);
			summaryCdnrResp.setDiffIgst(diffIgst);
			summaryCdnrResp.setDiffCgst(diffCgst);
			summaryCdnrResp.setDiffSgst(diffSgst);
			summaryCdnrResp.setDiffCess(diffCess);
			cdnrSummaryRespList.add(summaryCdnrResp);


		}
			/*
		 * CDNUR Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> cdnurSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnur = dto.getCdnur();
			Gstr1SummaryScreenRespDto summaryCdnurResp = new Gstr1SummaryScreenRespDto();
				List<Gstr1SummarySectionDto> cdnurSummary = cdnur
						.getEySummary();

				if (cdnurSummary != null) {
				cdnurSummary.forEach(cdnurEySummary -> {

					// Asp Data


					summaryCdnurResp.setTaxDocType(cdnurEySummary.getTaxDocType());
					summaryCdnurResp
							.setAspTaxableValue(cdnurEySummary.getAspTaxableValue());
					summaryCdnurResp.setAspTaxPayble(cdnurEySummary.getAspTaxPayble());
					summaryCdnurResp.setAspInvoiceValue(cdnurEySummary.getAspInvoiceValue());
					summaryCdnurResp.setAspIgst(cdnurEySummary.getAspIgst());
					summaryCdnurResp.setAspCgst(cdnurEySummary.getAspCgst());
					summaryCdnurResp.setAspSgst(cdnurEySummary.getAspSgst());
					summaryCdnurResp.setAspCess(cdnurEySummary.getAspCess());
					summaryCdnurResp.setAspCount(cdnurEySummary.getAspCount());
					
					summaryCdnurResp.setGstnTaxableValue(cdnurEySummary.getGstnTaxableValue());
					summaryCdnurResp.setGstnTaxPayble(cdnurEySummary.getGstnTaxPayble());
					summaryCdnurResp.setGstnInvoiceValue(cdnurEySummary.getGstnInvoiceValue());
					summaryCdnurResp.setGstnIgst(cdnurEySummary.getGstnIgst());
					summaryCdnurResp.setGstnCgst(cdnurEySummary.getGstnCgst());
					summaryCdnurResp.setGstnSgst(cdnurEySummary.getGstnSgst());
					summaryCdnurResp.setGstnCess(cdnurEySummary.getGstnCess());
					summaryCdnurResp.setGstnCount(cdnurEySummary.getGstnCount());

				});

			}
				
			/*
			 * Calculating Difference Asp - Gstn
			 */

			BigDecimal diffInv = subMethod(
					summaryCdnurResp.getAspInvoiceValue(),
					summaryCdnurResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryCdnurResp.getAspTaxableValue(),
					summaryCdnurResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryCdnurResp.getAspTaxPayble(),
					summaryCdnurResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryCdnurResp.getAspIgst(),
					summaryCdnurResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryCdnurResp.getAspSgst(),
					summaryCdnurResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryCdnurResp.getAspCgst(),
					summaryCdnurResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryCdnurResp.getAspCess(),
					summaryCdnurResp.getGstnCess());

			Integer aspCount = (summaryCdnurResp.getAspCount() != null)
					? summaryCdnurResp.getAspCount() : 0;
			Integer gstnCount = (summaryCdnurResp.getGstnCount() != null)
					? summaryCdnurResp.getGstnCount() : 0;

			summaryCdnurResp.setDiffCount(aspCount - gstnCount);
			summaryCdnurResp.setDiffInvoiceValue(diffInv);
			summaryCdnurResp.setDiffTaxableValue(diffTaxable);
			summaryCdnurResp.setDiffTaxPayble(diffTaxPayble);
			summaryCdnurResp.setDiffIgst(diffIgst);
			summaryCdnurResp.setDiffCgst(diffCgst);
			summaryCdnurResp.setDiffSgst(diffSgst);
			summaryCdnurResp.setDiffCess(diffCess);
			cdnurSummaryRespList.add(summaryCdnurResp);


		}
		Gson gson = GsonUtil.newSAPGsonInstance();
	//	List<Gstr1SummaryScreenRespDto> handleB2BResp = null;
			//	gstr1B2BRespHandler
			//	.handleB2BResp(b2bSummaryRespList);
		
	
	
		List<Gstr1SummaryScreenRespDto> handleB2BResp = respHandler.pvsSubmitB2BResp(b2bSummaryRespList,"B2B");
		List<Gstr1SummaryScreenRespDto> handleExpResp = respHandler.pvsSubmitB2BResp(expSummaryRespList,"EXPORTS");
		List<Gstr1SummaryScreenRespDto> handleCdnrResp = respHandler.pvsSubmitB2BResp(cdnrSummaryRespList,"CDNR");
		List<Gstr1SummaryScreenRespDto> handlecdrurResp = respHandler.pvsSubmitB2BResp(cdnurSummaryRespList,"CDNUR");
		
		List<Gstr1SummaryScreenRespDto> response = new ArrayList<>();
		response.addAll(handleB2BResp);
		response.addAll(handleExpResp);
		response.addAll(handleCdnrResp);
		response.addAll(handlecdrurResp);
			return response;

	}



	public Gstr1SummarySectionDto addregateCredtiDebit(
			Gstr1SummaryCDSectionDto cdnrDr, Gstr1SummaryCDSectionDto cdnrCr) {

		Gstr1SummarySectionDto crnrFinalValue = new Gstr1SummarySectionDto();

		BigDecimal invValueDr = BigDecimal.ZERO;
		BigDecimal taxableDr = BigDecimal.ZERO;
		BigDecimal taxPayableDr = BigDecimal.ZERO;
		BigDecimal igstDr = BigDecimal.ZERO;
		BigDecimal sgstDr = BigDecimal.ZERO;
		BigDecimal cgstDr = BigDecimal.ZERO;
		BigDecimal cessDr = BigDecimal.ZERO;
		Integer countDr = 0;

		if (cdnrDr == null) {
			invValueDr = BigDecimal.ZERO;
			taxableDr = BigDecimal.ZERO;
			taxPayableDr = BigDecimal.ZERO;
			igstDr = BigDecimal.ZERO;
			sgstDr = BigDecimal.ZERO;
			cgstDr = BigDecimal.ZERO;
			cessDr = BigDecimal.ZERO;
			countDr = 0;
		} else {
			invValueDr = cdnrDr.getInvValue();
			taxableDr = cdnrDr.getTaxableValue();
			taxPayableDr = cdnrDr.getTaxPayable();
			igstDr = cdnrDr.getIgst();
			sgstDr = cdnrDr.getSgst();
			cgstDr = cdnrDr.getCgst();
			cessDr = cdnrDr.getCess();
			countDr = cdnrDr.getRecords();
		}
		BigDecimal invValueCr = BigDecimal.ZERO;
		BigDecimal taxableCr = BigDecimal.ZERO;
		BigDecimal taxPayableCr = BigDecimal.ZERO;
		BigDecimal igstCr = BigDecimal.ZERO;
		BigDecimal sgstCr = BigDecimal.ZERO;
		BigDecimal cgstCr = BigDecimal.ZERO;
		BigDecimal cessCr = BigDecimal.ZERO;
		Integer countCr = 0;
		if (cdnrCr == null) {
			invValueCr = BigDecimal.ZERO;
			taxableCr = BigDecimal.ZERO;
			taxPayableCr = BigDecimal.ZERO;
			igstCr = BigDecimal.ZERO;
			sgstCr = BigDecimal.ZERO;
			cgstCr = BigDecimal.ZERO;
			cessCr = BigDecimal.ZERO;
			countCr = 0;
		} else {
			invValueCr = cdnrCr.getInvValue();
			taxableCr = cdnrCr.getTaxableValue();
			taxPayableCr = cdnrCr.getTaxPayable();
			igstCr = cdnrCr.getIgst();
			sgstCr = cdnrCr.getSgst();
			cgstCr = cdnrCr.getCgst();
			cessCr = cdnrCr.getCess();
			countCr = cdnrCr.getRecords();
		}

		BigDecimal invValue = subMethod(invValueDr, invValueCr);
		BigDecimal taxable = subMethod(taxableDr, taxableCr);
		BigDecimal taxPayable = subMethod(taxPayableDr, taxPayableCr);
		BigDecimal igst = subMethod(igstDr, igstCr);
		BigDecimal sgst = subMethod(sgstDr, sgstCr);
		BigDecimal cgst = subMethod(cgstDr, cgstCr);
		BigDecimal cess = subMethod(cessDr, cessCr);

		crnrFinalValue.setRecords(countDr + countCr);
		// crnrFinalValue.setTaxDocType("CDNR");
		crnrFinalValue.setTaxPayable(taxPayable);
		crnrFinalValue.setTaxableValue(taxable);
		crnrFinalValue.setInvValue(invValue);
		crnrFinalValue.setIgst(igst);
		crnrFinalValue.setSgst(sgst);
		crnrFinalValue.setCgst(cgst);
		crnrFinalValue.setCess(cess);

		return crnrFinalValue;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

	}
