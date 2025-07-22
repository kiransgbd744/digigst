/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.GstnApiNilCalculation;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1ATASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1ATSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1TXPDASummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1TXPDSummaryRespHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr1ASummaryScreenAdvReqRespHandler")
public class Gstr1ASummaryScreenAdvReqRespHandler {

	public static final String atconst = "ADV REC";
	public static final String ataconst = "ADV REC-A";
	public static final String txpdconst = "ADV ADJ";
	public static final String txpdaconst = "ADV ADJ-A";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ASummaryScreenAdvReqRespHandler.class);
	@Autowired
	@Qualifier("Gstr1AAdvSimpleDocSummarySearchService")
	Gstr1AAdvSimpleDocSummarySearchService service;

	@Autowired
	@Qualifier("Gstr1ATSummaryRespHandler")
	Gstr1ATSummaryRespHandler gstr1ATRespHandler;

	@Autowired
	@Qualifier("Gstr1ATASummaryRespHandler")
	Gstr1ATASummaryRespHandler gstr1ATARespHandler;

	@Autowired
	@Qualifier("Gstr1TXPDSummaryRespHandler")
	Gstr1TXPDSummaryRespHandler gstr1TXPDRespHandler;

	@Autowired
	@Qualifier("Gstr1TXPDASummaryRespHandler")
	Gstr1TXPDASummaryRespHandler gstr1TXPDARespHandler;

	@Autowired
	@Qualifier("GstnApiNilCalculation")
	GstnApiNilCalculation gstnDataCal;

	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenRespDto> handleGstr1AdvReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {

		LOGGER.debug("OUTWARD Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("Advances Sections Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();
		/*
		 * List<Gstr1SummarySectionDto> gstnAtSummary =
		 * gstnResult.get(0).getAt() .getGstnSummary();
		 */
		List<Gstr1SummarySectionDto> gstnAtSummary = gstnDataCal
				.addAdvRecGstnDocTypes(gstnResult);
		Gstr1SummarySectionDto atGstn = addGstnData(gstnAtSummary);

		List<Gstr1SummaryScreenRespDto> atSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto at = dto.getAt();
			List<Gstr1SummarySectionDto> atSummary = at.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (atSummary != null) {
				atSummary.forEach(atEySummary -> {

					// ASP Data
					summaryResp.setTaxDocType(atEySummary.getTaxDocType());
					summaryResp
							.setAspTaxableValue(atEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(atEySummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(atEySummary.getInvValue());
					summaryResp.setAspIgst(atEySummary.getIgst());
					summaryResp.setAspCgst(atEySummary.getCgst());
					summaryResp.setAspSgst(atEySummary.getSgst());
					summaryResp.setAspCess(atEySummary.getCess());
					summaryResp.setAspCount(atEySummary.getRecords());

				});
			} else {
				summaryResp.setTaxDocType(atconst);
			}
			// Gstn Live Data

			summaryResp.setGstnTaxableValue(atGstn.getTaxableValue());
			summaryResp.setGstnTaxPayble(atGstn.getTaxPayable());
			summaryResp.setGstnInvoiceValue(atGstn.getInvValue());
			summaryResp.setGstnIgst(atGstn.getIgst());
			summaryResp.setGstnCgst(atGstn.getCgst());
			summaryResp.setGstnSgst(atGstn.getSgst());
			summaryResp.setGstnCess(atGstn.getCess());
			summaryResp.setGstnCount(atGstn.getRecords());

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
			atSummaryRespList.add(summaryResp);
		}
		/*
		 * ATA
		 */
		List<Gstr1SummaryScreenRespDto> ataSummaryRespList = new ArrayList<>();

		/*
		 * List<Gstr1SummarySectionDto> gstnAtaSummary =
		 * gstnResult.get(0).getAta() .getGstnSummary();
		 */

		List<Gstr1SummarySectionDto> gstnAtaSummary = gstnDataCal
				.addAdvRecAmGstnDocTypes(gstnResult);
		Gstr1SummarySectionDto ataGstn = addGstnData(gstnAtaSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto ata = dto.getAta();
			List<Gstr1SummarySectionDto> ataSummary = ata.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (ataSummary != null) {
				ataSummary.forEach(ataEySummary -> {

					// ASP Data
					summaryResp.setTaxDocType(ataEySummary.getTaxDocType());
					summaryResp
							.setAspTaxableValue(ataEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(ataEySummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(ataEySummary.getInvValue());
					summaryResp.setAspIgst(ataEySummary.getIgst());
					summaryResp.setAspCgst(ataEySummary.getCgst());
					summaryResp.setAspSgst(ataEySummary.getSgst());
					summaryResp.setAspCess(ataEySummary.getCess());
					summaryResp.setAspCount(ataEySummary.getRecords());

				});
			} else {
				summaryResp.setTaxDocType(ataconst);
			}
			// Gstn Live Data

			summaryResp.setGstnTaxableValue(ataGstn.getTaxableValue());
			summaryResp.setGstnTaxPayble(ataGstn.getTaxPayable());
			summaryResp.setGstnInvoiceValue(ataGstn.getInvValue());
			summaryResp.setGstnIgst(ataGstn.getIgst());
			summaryResp.setGstnCgst(ataGstn.getCgst());
			summaryResp.setGstnSgst(ataGstn.getSgst());
			summaryResp.setGstnCess(ataGstn.getCess());
			summaryResp.setGstnCount(ataGstn.getRecords());

			/*
			 * Calculating Difference Asp - Gstn
			 */
			BigDecimal diffInv = subMethod(summaryResp.getAspInvoiceValue(),
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
			summaryResp.setDiffInvoiceValue(diffInv);
			summaryResp.setDiffTaxableValue(diffTaxable);
			summaryResp.setDiffTaxPayble(diffTaxPayble);
			summaryResp.setDiffIgst(diffIgst);
			summaryResp.setDiffCgst(diffCgst);
			summaryResp.setDiffSgst(diffSgst);
			summaryResp.setDiffCess(diffCess);
			ataSummaryRespList.add(summaryResp);

		}
		/*
		 * TXPD
		 */
		List<Gstr1SummaryScreenRespDto> txpdSummaryRespList = new ArrayList<>();

		/*
		 * List<Gstr1SummarySectionDto> gstnTxpdSummary = gstnResult.get(0)
		 * .getTxpd().getGstnSummary();
		 */
		List<Gstr1SummarySectionDto> gstnTxpdSummary = gstnDataCal
				.addAdvAdjGstnDocTypes(gstnResult);
		Gstr1SummarySectionDto txpdGstn = addGstnData(gstnTxpdSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto txpd = dto.getTxpd();
			List<Gstr1SummarySectionDto> txpdSummary = txpd.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (txpdSummary != null) {
				txpdSummary.forEach(txpdEySummary -> {

					// ASP Data
					summaryResp.setTaxDocType(txpdEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							txpdEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(txpdEySummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(txpdEySummary.getInvValue());
					summaryResp.setAspIgst(txpdEySummary.getIgst());
					summaryResp.setAspCgst(txpdEySummary.getCgst());
					summaryResp.setAspSgst(txpdEySummary.getSgst());
					summaryResp.setAspCess(txpdEySummary.getCess());
					summaryResp.setAspCount(txpdEySummary.getRecords());
				});
			} else {
				summaryResp.setTaxDocType(txpdconst);
			}

			// Gstn Live Data

			summaryResp.setGstnTaxableValue(txpdGstn.getTaxableValue());
			summaryResp.setGstnTaxPayble(txpdGstn.getTaxPayable());
			summaryResp.setGstnInvoiceValue(txpdGstn.getInvValue());
			summaryResp.setGstnIgst(txpdGstn.getIgst());
			summaryResp.setGstnCgst(txpdGstn.getCgst());
			summaryResp.setGstnSgst(txpdGstn.getSgst());
			summaryResp.setGstnCess(txpdGstn.getCess());
			summaryResp.setGstnCount(txpdGstn.getRecords());

			/*
			 * Calculating Difference Asp - Gstn
			 */
			BigDecimal diffInv = subMethod(summaryResp.getAspInvoiceValue(),
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
			summaryResp.setDiffInvoiceValue(diffInv);
			summaryResp.setDiffTaxableValue(diffTaxable);
			summaryResp.setDiffTaxPayble(diffTaxPayble);
			summaryResp.setDiffIgst(diffIgst);
			summaryResp.setDiffCgst(diffCgst);
			summaryResp.setDiffSgst(diffSgst);
			summaryResp.setDiffCess(diffCess);
			txpdSummaryRespList.add(summaryResp);

		}

		// TXPDA
		List<Gstr1SummaryScreenRespDto> txpdaSummaryRespList = new ArrayList<>();

		/*
		 * List<Gstr1SummarySectionDto> gstnTxpdaSummary = gstnResult.get(0)
		 * .getTxpda().getGstnSummary();
		 */
		List<Gstr1SummarySectionDto> gstnTxpdaSummary = gstnDataCal
				.addAdvAdjAmGstnDocTypes(gstnResult);

		Gstr1SummarySectionDto txpdaGstn = addGstnData(gstnTxpdaSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto txpda = dto.getTxpda();
			List<Gstr1SummarySectionDto> txpdaSummary = txpda.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (txpdaSummary != null) {
				txpdaSummary.forEach(txpdaEySummary -> {

					// ASP Data
					summaryResp.setTaxDocType(txpdaEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							txpdaEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(txpdaEySummary.getTaxPayable());
					summaryResp
							.setAspInvoiceValue(txpdaEySummary.getInvValue());
					summaryResp.setAspIgst(txpdaEySummary.getIgst());
					summaryResp.setAspCgst(txpdaEySummary.getCgst());
					summaryResp.setAspSgst(txpdaEySummary.getSgst());
					summaryResp.setAspCess(txpdaEySummary.getCess());
					summaryResp.setAspCount(txpdaEySummary.getRecords());

				});
			} else {
				summaryResp.setTaxDocType(txpdaconst);
			}
			// Gstn Live Data
			summaryResp.setGstnTaxableValue(txpdaGstn.getTaxableValue());
			summaryResp.setGstnTaxPayble(txpdaGstn.getTaxPayable());
			summaryResp.setGstnInvoiceValue(txpdaGstn.getInvValue());
			summaryResp.setGstnIgst(txpdaGstn.getIgst());
			summaryResp.setGstnCgst(txpdaGstn.getCgst());
			summaryResp.setGstnSgst(txpdaGstn.getSgst());
			summaryResp.setGstnCess(txpdaGstn.getCess());
			summaryResp.setGstnCount(txpdaGstn.getRecords());

			/*
			 * Calculating Difference Asp - Gstn
			 */
			BigDecimal diffInv = subMethod(summaryResp.getAspInvoiceValue(),
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
			summaryResp.setDiffInvoiceValue(diffInv);
			summaryResp.setDiffTaxableValue(diffTaxable);
			summaryResp.setDiffTaxPayble(diffTaxPayble);
			summaryResp.setDiffIgst(diffIgst);
			summaryResp.setDiffCgst(diffCgst);
			summaryResp.setDiffSgst(diffSgst);
			summaryResp.setDiffCess(diffCess);
			txpdaSummaryRespList.add(summaryResp);
		}

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<Gstr1SummaryScreenRespDto> handleATResp = gstr1ATRespHandler
				.handleATResp(atSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleATAResp = gstr1ATARespHandler
				.handleATAResp(ataSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleTXPDResp = gstr1TXPDRespHandler
				.handleTXPDResp(txpdSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleTXPDAResp = gstr1TXPDARespHandler
				.handleTXPDAResp(txpdaSummaryRespList);

		List<Gstr1SummaryScreenRespDto> response = new ArrayList<>();
		response.addAll(handleATResp);
		response.addAll(handleTXPDResp);
		response.addAll(handleATAResp);
		response.addAll(handleTXPDAResp);
		return response;

	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

	private Gstr1SummarySectionDto addGstnData(
			List<Gstr1SummarySectionDto> gstnSummary) {

		Gstr1SummarySectionDto sumDto = new Gstr1SummarySectionDto();
		BigDecimal taxableValue = BigDecimal.ZERO;
		BigDecimal invValue = BigDecimal.ZERO;
		BigDecimal taxPayable = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal cess = BigDecimal.ZERO;
		Integer records = 0;
		if (gstnSummary != null) {
			for (Gstr1SummarySectionDto dto : gstnSummary) {

				taxableValue = taxableValue.add((dto.getTaxableValue() == null)
						? BigDecimal.ZERO : dto.getTaxableValue());
				invValue = invValue.add((dto.getInvValue() == null)
						? BigDecimal.ZERO : dto.getInvValue());
				taxPayable = taxPayable.add((dto.getTaxPayable() == null)
						? BigDecimal.ZERO : dto.getTaxPayable());
				igst = igst.add((dto.getIgst() == null) ? BigDecimal.ZERO
						: dto.getIgst());
				sgst = sgst.add((dto.getSgst() == null) ? BigDecimal.ZERO
						: dto.getSgst());
				cgst = cgst.add((dto.getCgst() == null) ? BigDecimal.ZERO
						: dto.getCgst());
				cess = cess.add((dto.getCess() == null) ? BigDecimal.ZERO
						: dto.getCess());
				records = records
						+ (dto.getRecords() == null ? 0 : dto.getRecords());
				sumDto.setRecords(records);
				sumDto.setInvValue(invValue);
				sumDto.setTaxableValue(taxableValue);
				sumDto.setTaxPayable(taxPayable);
				sumDto.setTaxDocType(dto.getTaxDocType());
				sumDto.setIgst(igst);
				sumDto.setSgst(sgst);
				sumDto.setCgst(cgst);
				sumDto.setCess(cess);

			}
		}

		return sumDto;

	}

}
