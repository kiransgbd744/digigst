package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.ProcessSubmitDocSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.ProcsSubmitSummaryRespHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr1AProcsSubmitScreenReqRespHandler")
public class Gstr1AProcsSubmitScreenReqRespHandler {

	@Autowired
	@Qualifier("Gstr1AProcessSubmitDocSummarySearchService")
	Gstr1AProcessSubmitDocSummarySearchService searchService;

	@Autowired
	@Qualifier("ProcsSubmitSummaryRespHandler")
	ProcsSubmitSummaryRespHandler respHandler;

	public static final String atconst = "ADV REC";
	public static final String ataconst = "ADV REC-A";
	public static final String txpdconst = "ADV ADJ";
	public static final String txpdaconst = "ADV ADJ-A";

	public Pair<List<Gstr1SummaryScreenRespDto>, List<Gstr1SummaryScreenRespDto>> handleGstr1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		LOGGER.debug(" Summary Execution For B2B,B2BA,B2CL,B2CLA,B2CS,"
				+ "B2CSA, CDNR,CDNRA,CDNUR AND CDNURA  Sections BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = searchService
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug(" Summary Execution For B2B,B2BA,B2CL,B2CLA,B2CS,"
				+ "B2CSA, CDNR,CDNRA,CDNUR AND CDNURA  Sections END ");

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
					summaryResp.setAspTaxableValue(
							b2bEySummary.getAspTaxableValue());
					summaryResp.setAspTaxPayble(b2bEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(
							b2bEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(b2bEySummary.getAspIgst());
					summaryResp.setAspCgst(b2bEySummary.getAspCgst());
					summaryResp.setAspSgst(b2bEySummary.getAspSgst());
					summaryResp.setAspCess(b2bEySummary.getAspCess());
					summaryResp.setAspCount(b2bEySummary.getAspCount());

					summaryResp.setGstnTaxableValue(
							b2bEySummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(b2bEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(
							b2bEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(b2bEySummary.getGstnIgst());
					summaryResp.setGstnCgst(b2bEySummary.getGstnCgst());
					summaryResp.setGstnSgst(b2bEySummary.getGstnSgst());
					summaryResp.setGstnCess(b2bEySummary.getGstnCess());
					summaryResp.setGstnCount(b2bEySummary.getGstnCount());

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
			b2bSummaryRespList.add(summaryResp);
		}
		/**
		 * B2BA
		 */

		List<Gstr1SummaryScreenRespDto> b2baSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2ba = dto.getB2ba();
			List<Gstr1SummarySectionDto> b2baSummary = b2ba.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (b2baSummary != null) {
				b2baSummary.forEach(b2baEySummary -> {

					summaryResp.setTaxDocType(b2baEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							b2baEySummary.getAspTaxableValue());
					summaryResp
							.setAspTaxPayble(b2baEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(
							b2baEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(b2baEySummary.getAspIgst());
					summaryResp.setAspCgst(b2baEySummary.getAspCgst());
					summaryResp.setAspSgst(b2baEySummary.getAspSgst());
					summaryResp.setAspCess(b2baEySummary.getAspCess());
					summaryResp.setAspCount(b2baEySummary.getAspCount());

					summaryResp.setGstnTaxableValue(
							b2baEySummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(b2baEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(
							b2baEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(b2baEySummary.getGstnIgst());
					summaryResp.setGstnCgst(b2baEySummary.getGstnCgst());
					summaryResp.setGstnSgst(b2baEySummary.getGstnSgst());
					summaryResp.setGstnCess(b2baEySummary.getGstnCess());
					summaryResp.setGstnCount(b2baEySummary.getGstnCount());

				});
			}

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
			b2baSummaryRespList.add(summaryResp);

		}

		/*
		 * B2CL Section Stub Data
		 */
		List<Gstr1SummaryScreenRespDto> b2clSummaryRespList = new ArrayList<>();
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2cl = dto.getB2cl();
			List<Gstr1SummarySectionDto> b2clSummary = b2cl.getEySummary();
			Gstr1SummaryScreenRespDto summaryB2clResp = new Gstr1SummaryScreenRespDto();
			if (b2clSummary != null) {
				b2clSummary.forEach(b2clEySummary -> {

					summaryB2clResp
							.setTaxDocType(b2clEySummary.getTaxDocType());
					summaryB2clResp.setAspTaxableValue(
							b2clEySummary.getAspTaxableValue());
					summaryB2clResp
							.setAspTaxPayble(b2clEySummary.getAspTaxPayble());
					summaryB2clResp.setAspInvoiceValue(
							b2clEySummary.getAspInvoiceValue());
					summaryB2clResp.setAspIgst(b2clEySummary.getAspIgst());
					summaryB2clResp.setAspCgst(b2clEySummary.getAspCgst());
					summaryB2clResp.setAspSgst(b2clEySummary.getAspSgst());
					summaryB2clResp.setAspCess(b2clEySummary.getAspCess());
					summaryB2clResp.setAspCount(b2clEySummary.getAspCount());

					summaryB2clResp.setGstnTaxableValue(
							b2clEySummary.getGstnTaxableValue());
					summaryB2clResp
							.setGstnTaxPayble(b2clEySummary.getGstnTaxPayble());
					summaryB2clResp.setGstnInvoiceValue(
							b2clEySummary.getGstnInvoiceValue());
					summaryB2clResp.setGstnIgst(b2clEySummary.getGstnIgst());
					summaryB2clResp.setGstnCgst(b2clEySummary.getGstnCgst());
					summaryB2clResp.setGstnSgst(b2clEySummary.getGstnSgst());
					summaryB2clResp.setGstnCess(b2clEySummary.getGstnCess());
					summaryB2clResp.setGstnCount(b2clEySummary.getGstnCount());

				});
			}
			/*
			 * Calculating Difference Asp - Gstn
			 */

			BigDecimal diffInv = subMethod(summaryB2clResp.getAspInvoiceValue(),
					summaryB2clResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryB2clResp.getAspTaxableValue(),
					summaryB2clResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryB2clResp.getAspTaxPayble(),
					summaryB2clResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryB2clResp.getAspIgst(),
					summaryB2clResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryB2clResp.getAspSgst(),
					summaryB2clResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryB2clResp.getAspCgst(),
					summaryB2clResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryB2clResp.getAspCess(),
					summaryB2clResp.getGstnCess());

			Integer aspCount = (summaryB2clResp.getAspCount() != null)
					? summaryB2clResp.getAspCount() : 0;
			Integer gstnCount = (summaryB2clResp.getGstnCount() != null)
					? summaryB2clResp.getGstnCount() : 0;

			summaryB2clResp.setDiffCount(aspCount - gstnCount);
			summaryB2clResp.setDiffInvoiceValue(diffInv);
			summaryB2clResp.setDiffTaxableValue(diffTaxable);
			summaryB2clResp.setDiffTaxPayble(diffTaxPayble);
			summaryB2clResp.setDiffIgst(diffIgst);
			summaryB2clResp.setDiffCgst(diffCgst);
			summaryB2clResp.setDiffSgst(diffSgst);
			summaryB2clResp.setDiffCess(diffCess);
			b2clSummaryRespList.add(summaryB2clResp);

		}
		/*
		 * B2CLa Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> b2claSummaryRespList = new ArrayList<>();
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2cla = dto.getB2cla();
			List<Gstr1SummarySectionDto> b2claSummary = b2cla.getEySummary();
			Gstr1SummaryScreenRespDto summaryB2claResp = new Gstr1SummaryScreenRespDto();
			if (b2claSummary != null) {
				b2claSummary.forEach(b2claEySummary -> {
					// ASP Data

					summaryB2claResp
							.setTaxDocType(b2claEySummary.getTaxDocType());
					summaryB2claResp.setAspTaxableValue(
							b2claEySummary.getAspTaxableValue());
					summaryB2claResp
							.setAspTaxPayble(b2claEySummary.getAspTaxPayble());
					summaryB2claResp.setAspInvoiceValue(
							b2claEySummary.getAspInvoiceValue());
					summaryB2claResp.setAspIgst(b2claEySummary.getAspIgst());
					summaryB2claResp.setAspCgst(b2claEySummary.getAspCgst());
					summaryB2claResp.setAspSgst(b2claEySummary.getAspSgst());
					summaryB2claResp.setAspCess(b2claEySummary.getAspCess());
					summaryB2claResp.setAspCount(b2claEySummary.getAspCount());

					summaryB2claResp.setGstnTaxableValue(
							b2claEySummary.getGstnTaxableValue());
					summaryB2claResp.setGstnTaxPayble(
							b2claEySummary.getGstnTaxPayble());
					summaryB2claResp.setGstnInvoiceValue(
							b2claEySummary.getGstnInvoiceValue());
					summaryB2claResp.setGstnIgst(b2claEySummary.getGstnIgst());
					summaryB2claResp.setGstnCgst(b2claEySummary.getGstnCgst());
					summaryB2claResp.setGstnSgst(b2claEySummary.getGstnSgst());
					summaryB2claResp.setGstnCess(b2claEySummary.getGstnCess());
					summaryB2claResp
							.setGstnCount(b2claEySummary.getGstnCount());

				});
			}

			BigDecimal diffInv = subMethod(
					summaryB2claResp.getAspInvoiceValue(),
					summaryB2claResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryB2claResp.getAspTaxableValue(),
					summaryB2claResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryB2claResp.getAspTaxPayble(),
					summaryB2claResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryB2claResp.getAspIgst(),
					summaryB2claResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryB2claResp.getAspSgst(),
					summaryB2claResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryB2claResp.getAspCgst(),
					summaryB2claResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryB2claResp.getAspCess(),
					summaryB2claResp.getGstnCess());

			Integer aspCount = (summaryB2claResp.getAspCount() != null)
					? summaryB2claResp.getAspCount() : 0;
			Integer gstnCount = (summaryB2claResp.getGstnCount() != null)
					? summaryB2claResp.getGstnCount() : 0;

			summaryB2claResp.setDiffCount(aspCount - gstnCount);
			summaryB2claResp.setDiffInvoiceValue(diffInv);
			summaryB2claResp.setDiffTaxableValue(diffTaxable);
			summaryB2claResp.setDiffTaxPayble(diffTaxPayble);
			summaryB2claResp.setDiffIgst(diffIgst);
			summaryB2claResp.setDiffCgst(diffCgst);
			summaryB2claResp.setDiffSgst(diffSgst);
			summaryB2claResp.setDiffCess(diffCess);
			b2claSummaryRespList.add(summaryB2claResp);

		}
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
					summaryExpResp.setAspTaxableValue(
							expEySummary.getAspTaxableValue());
					summaryExpResp
							.setAspTaxPayble(expEySummary.getAspTaxPayble());
					summaryExpResp.setAspInvoiceValue(
							expEySummary.getAspInvoiceValue());
					summaryExpResp.setAspIgst(expEySummary.getAspIgst());
					summaryExpResp.setAspCgst(expEySummary.getAspCgst());
					summaryExpResp.setAspSgst(expEySummary.getAspSgst());
					summaryExpResp.setAspCess(expEySummary.getAspCess());
					summaryExpResp.setAspCount(expEySummary.getAspCount());

					summaryExpResp.setGstnTaxableValue(
							expEySummary.getGstnTaxableValue());
					summaryExpResp
							.setGstnTaxPayble(expEySummary.getGstnTaxPayble());
					summaryExpResp.setGstnInvoiceValue(
							expEySummary.getGstnInvoiceValue());
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
		 * Expa Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> expaSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto expa = dto.getExpa();
			List<Gstr1SummarySectionDto> expaSummary = expa.getEySummary();
			Gstr1SummaryScreenRespDto summaryExpaResp = new Gstr1SummaryScreenRespDto();
			if (expaSummary != null) {
				expaSummary.forEach(expaEySummary -> {
					// Asp Data
					summaryExpaResp
							.setTaxDocType(expaEySummary.getTaxDocType());
					summaryExpaResp.setAspTaxableValue(
							expaEySummary.getAspTaxableValue());
					summaryExpaResp
							.setAspTaxPayble(expaEySummary.getAspTaxPayble());
					summaryExpaResp.setAspInvoiceValue(
							expaEySummary.getAspInvoiceValue());
					summaryExpaResp.setAspIgst(expaEySummary.getAspIgst());
					summaryExpaResp.setAspCgst(expaEySummary.getAspCgst());
					summaryExpaResp.setAspSgst(expaEySummary.getAspSgst());
					summaryExpaResp.setAspCess(expaEySummary.getAspCess());
					summaryExpaResp.setAspCount(expaEySummary.getAspCount());

					summaryExpaResp.setGstnTaxableValue(
							expaEySummary.getGstnTaxableValue());
					summaryExpaResp
							.setGstnTaxPayble(expaEySummary.getGstnTaxPayble());
					summaryExpaResp.setGstnInvoiceValue(
							expaEySummary.getGstnInvoiceValue());
					summaryExpaResp.setGstnIgst(expaEySummary.getGstnIgst());
					summaryExpaResp.setGstnCgst(expaEySummary.getGstnCgst());
					summaryExpaResp.setGstnSgst(expaEySummary.getGstnSgst());
					summaryExpaResp.setGstnCess(expaEySummary.getGstnCess());
					summaryExpaResp.setGstnCount(expaEySummary.getGstnCount());

				});
			}
			/*
			 * Calculating Difference Asp - Gstn
			 */

			BigDecimal diffInv = subMethod(summaryExpaResp.getAspInvoiceValue(),
					summaryExpaResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryExpaResp.getAspTaxableValue(),
					summaryExpaResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryExpaResp.getAspTaxPayble(),
					summaryExpaResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryExpaResp.getAspIgst(),
					summaryExpaResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryExpaResp.getAspSgst(),
					summaryExpaResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryExpaResp.getAspCgst(),
					summaryExpaResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryExpaResp.getAspCess(),
					summaryExpaResp.getGstnCess());

			Integer aspCount = (summaryExpaResp.getAspCount() != null)
					? summaryExpaResp.getAspCount() : 0;
			Integer gstnCount = (summaryExpaResp.getGstnCount() != null)
					? summaryExpaResp.getGstnCount() : 0;

			summaryExpaResp.setDiffCount(aspCount - gstnCount);
			summaryExpaResp.setDiffInvoiceValue(diffInv);
			summaryExpaResp.setDiffTaxableValue(diffTaxable);
			summaryExpaResp.setDiffTaxPayble(diffTaxPayble);
			summaryExpaResp.setDiffIgst(diffIgst);
			summaryExpaResp.setDiffCgst(diffCgst);
			summaryExpaResp.setDiffSgst(diffSgst);
			summaryExpaResp.setDiffCess(diffCess);
			expaSummaryRespList.add(summaryExpaResp);

		}
		/*
		 * CDNR Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> cdnrSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnr = dto.getCdnr();
			// List<Gstr1SummarySectionDto> cdnrSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnrResp = new Gstr1SummaryScreenRespDto();
			List<Gstr1SummarySectionDto> cdnrSummary = cdnr.getEySummary();
			if (cdnrSummary != null) {
				cdnrSummary.forEach(cdnrEySummary -> {
					// ASP Data

					summaryCdnrResp
							.setTaxDocType(cdnrEySummary.getTaxDocType());
					summaryCdnrResp.setAspTaxableValue(
							cdnrEySummary.getAspTaxableValue());
					summaryCdnrResp
							.setAspTaxPayble(cdnrEySummary.getAspTaxPayble());
					summaryCdnrResp.setAspInvoiceValue(
							cdnrEySummary.getAspInvoiceValue());
					summaryCdnrResp.setAspIgst(cdnrEySummary.getAspIgst());
					summaryCdnrResp.setAspCgst(cdnrEySummary.getAspCgst());
					summaryCdnrResp.setAspSgst(cdnrEySummary.getAspSgst());
					summaryCdnrResp.setAspCess(cdnrEySummary.getAspCess());
					summaryCdnrResp.setAspCount(cdnrEySummary.getAspCount());

					summaryCdnrResp.setGstnTaxableValue(
							cdnrEySummary.getGstnTaxableValue());
					summaryCdnrResp
							.setGstnTaxPayble(cdnrEySummary.getGstnTaxPayble());
					summaryCdnrResp.setGstnInvoiceValue(
							cdnrEySummary.getGstnInvoiceValue());
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
		 * CDNRA Section
		 */
		List<Gstr1SummaryScreenRespDto> cdnraSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnra = dto.getCdnra();
			// List<Gstr1SummarySectionDto> cdnraSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnraResp = new Gstr1SummaryScreenRespDto();

			List<Gstr1SummarySectionDto> cdnraSummary = cdnra.getEySummary();
			if (cdnraSummary != null) {
				cdnraSummary.forEach(cdnraEySummary -> {

					summaryCdnraResp
							.setTaxDocType(cdnraEySummary.getTaxDocType());
					summaryCdnraResp.setAspTaxableValue(
							cdnraEySummary.getAspTaxableValue());
					summaryCdnraResp
							.setAspTaxPayble(cdnraEySummary.getAspTaxPayble());
					summaryCdnraResp.setAspInvoiceValue(
							cdnraEySummary.getAspInvoiceValue());
					summaryCdnraResp.setAspIgst(cdnraEySummary.getAspIgst());
					summaryCdnraResp.setAspCgst(cdnraEySummary.getAspCgst());
					summaryCdnraResp.setAspSgst(cdnraEySummary.getAspSgst());
					summaryCdnraResp.setAspCess(cdnraEySummary.getAspCess());
					summaryCdnraResp.setAspCount(cdnraEySummary.getAspCount());

					summaryCdnraResp.setGstnTaxableValue(
							cdnraEySummary.getGstnTaxableValue());
					summaryCdnraResp.setGstnTaxPayble(
							cdnraEySummary.getGstnTaxPayble());
					summaryCdnraResp.setGstnInvoiceValue(
							cdnraEySummary.getGstnInvoiceValue());
					summaryCdnraResp.setGstnIgst(cdnraEySummary.getGstnIgst());
					summaryCdnraResp.setGstnCgst(cdnraEySummary.getGstnCgst());
					summaryCdnraResp.setGstnSgst(cdnraEySummary.getGstnSgst());
					summaryCdnraResp.setGstnCess(cdnraEySummary.getGstnCess());
					summaryCdnraResp
							.setGstnCount(cdnraEySummary.getGstnCount());

				});
			}
			BigDecimal diffInv = subMethod(
					summaryCdnraResp.getAspInvoiceValue(),
					summaryCdnraResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryCdnraResp.getAspTaxableValue(),
					summaryCdnraResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryCdnraResp.getAspTaxPayble(),
					summaryCdnraResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryCdnraResp.getAspIgst(),
					summaryCdnraResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryCdnraResp.getAspSgst(),
					summaryCdnraResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryCdnraResp.getAspCgst(),
					summaryCdnraResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryCdnraResp.getAspCess(),
					summaryCdnraResp.getGstnCess());

			Integer aspCount = (summaryCdnraResp.getAspCount() != null)
					? summaryCdnraResp.getAspCount() : 0;
			Integer gstnCount = (summaryCdnraResp.getGstnCount() != null)
					? summaryCdnraResp.getGstnCount() : 0;

			summaryCdnraResp.setDiffCount(aspCount - gstnCount);
			summaryCdnraResp.setDiffInvoiceValue(diffInv);
			summaryCdnraResp.setDiffTaxableValue(diffTaxable);
			summaryCdnraResp.setDiffTaxPayble(diffTaxPayble);
			summaryCdnraResp.setDiffIgst(diffIgst);
			summaryCdnraResp.setDiffCgst(diffCgst);
			summaryCdnraResp.setDiffSgst(diffSgst);
			summaryCdnraResp.setDiffCess(diffCess);
			cdnraSummaryRespList.add(summaryCdnraResp);

		}
		/*
		 * CDNUR Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> cdnurSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnur = dto.getCdnur();
			// List<Gstr1SummarySectionDto> cdnurSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnurResp = new Gstr1SummaryScreenRespDto();
			// if (cdnur != null) {
			List<Gstr1SummarySectionDto> cdnurSummary = cdnur.getEySummary();

			if (cdnurSummary != null) {
				cdnurSummary.forEach(cdnurEySummary -> {

					// Asp Data

					summaryCdnurResp
							.setTaxDocType(cdnurEySummary.getTaxDocType());
					summaryCdnurResp.setAspTaxableValue(
							cdnurEySummary.getAspTaxableValue());
					summaryCdnurResp
							.setAspTaxPayble(cdnurEySummary.getAspTaxPayble());
					summaryCdnurResp.setAspInvoiceValue(
							cdnurEySummary.getAspInvoiceValue());
					summaryCdnurResp.setAspIgst(cdnurEySummary.getAspIgst());
					summaryCdnurResp.setAspCgst(cdnurEySummary.getAspCgst());
					summaryCdnurResp.setAspSgst(cdnurEySummary.getAspSgst());
					summaryCdnurResp.setAspCess(cdnurEySummary.getAspCess());
					summaryCdnurResp.setAspCount(cdnurEySummary.getAspCount());

					summaryCdnurResp.setGstnTaxableValue(
							cdnurEySummary.getGstnTaxableValue());
					summaryCdnurResp.setGstnTaxPayble(
							cdnurEySummary.getGstnTaxPayble());
					summaryCdnurResp.setGstnInvoiceValue(
							cdnurEySummary.getGstnInvoiceValue());
					summaryCdnurResp.setGstnIgst(cdnurEySummary.getGstnIgst());
					summaryCdnurResp.setGstnCgst(cdnurEySummary.getGstnCgst());
					summaryCdnurResp.setGstnSgst(cdnurEySummary.getGstnSgst());
					summaryCdnurResp.setGstnCess(cdnurEySummary.getGstnCess());
					summaryCdnurResp
							.setGstnCount(cdnurEySummary.getGstnCount());

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
		/**
		 * CDNURA Section Stub
		 */
		List<Gstr1SummaryScreenRespDto> cdnuraSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {

			Gstr1BasicSectionSummaryDto cdnura = dto.getCdnura();
			// List<Gstr1SummarySectionDto> cdnuraSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnuraResp = new Gstr1SummaryScreenRespDto();

			List<Gstr1SummarySectionDto> cdnuraSummary = cdnura.getEySummary();

			if (cdnuraSummary != null) {
				cdnuraSummary.forEach(cdnuraEySummary -> {

					summaryCdnuraResp
							.setTaxDocType(cdnuraEySummary.getTaxDocType());
					summaryCdnuraResp.setAspTaxableValue(
							cdnuraEySummary.getAspTaxableValue());
					summaryCdnuraResp
							.setAspTaxPayble(cdnuraEySummary.getAspTaxPayble());
					summaryCdnuraResp.setAspInvoiceValue(
							cdnuraEySummary.getAspInvoiceValue());
					summaryCdnuraResp.setAspIgst(cdnuraEySummary.getAspIgst());
					summaryCdnuraResp.setAspCgst(cdnuraEySummary.getAspCgst());
					summaryCdnuraResp.setAspSgst(cdnuraEySummary.getAspSgst());
					summaryCdnuraResp.setAspCess(cdnuraEySummary.getAspCess());
					summaryCdnuraResp
							.setAspCount(cdnuraEySummary.getAspCount());

					summaryCdnuraResp.setGstnTaxableValue(
							cdnuraEySummary.getGstnTaxableValue());
					summaryCdnuraResp.setGstnTaxPayble(
							cdnuraEySummary.getGstnTaxPayble());
					summaryCdnuraResp.setGstnInvoiceValue(
							cdnuraEySummary.getGstnInvoiceValue());
					summaryCdnuraResp
							.setGstnIgst(cdnuraEySummary.getGstnIgst());
					summaryCdnuraResp
							.setGstnCgst(cdnuraEySummary.getGstnCgst());
					summaryCdnuraResp
							.setGstnSgst(cdnuraEySummary.getGstnSgst());
					summaryCdnuraResp
							.setGstnCess(cdnuraEySummary.getGstnCess());
					summaryCdnuraResp
							.setGstnCount(cdnuraEySummary.getGstnCount());

				});
			}

			/*
			 * Calculating Difference Asp - Gstn
			 */

			BigDecimal diffInv = subMethod(
					summaryCdnuraResp.getAspInvoiceValue(),
					summaryCdnuraResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryCdnuraResp.getAspTaxableValue(),
					summaryCdnuraResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryCdnuraResp.getAspTaxPayble(),
					summaryCdnuraResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryCdnuraResp.getAspIgst(),
					summaryCdnuraResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryCdnuraResp.getAspSgst(),
					summaryCdnuraResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryCdnuraResp.getAspCgst(),
					summaryCdnuraResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryCdnuraResp.getAspCess(),
					summaryCdnuraResp.getGstnCess());

			Integer aspCount = (summaryCdnuraResp.getAspCount() != null)
					? summaryCdnuraResp.getAspCount() : 0;
			Integer gstnCount = (summaryCdnuraResp.getGstnCount() != null)
					? summaryCdnuraResp.getGstnCount() : 0;

			summaryCdnuraResp.setDiffCount(aspCount - gstnCount);
			summaryCdnuraResp.setDiffInvoiceValue(diffInv);
			summaryCdnuraResp.setDiffTaxableValue(diffTaxable);
			summaryCdnuraResp.setDiffTaxPayble(diffTaxPayble);
			summaryCdnuraResp.setDiffIgst(diffIgst);
			summaryCdnuraResp.setDiffCgst(diffCgst);
			summaryCdnuraResp.setDiffSgst(diffSgst);
			summaryCdnuraResp.setDiffCess(diffCess);
			cdnuraSummaryRespList.add(summaryCdnuraResp);

		}
		/**
		 * B2CS Section Stub Data
		 */
		List<Gstr1SummaryScreenRespDto> b2csSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2cs = dto.getB2cs();

			List<Gstr1SummarySectionDto> b2csSummary = b2cs.getEySummary();
			// List<Gstr1SummarySectionDto> b2csSummary = new ArrayList<>();
			/*
			 * Gstr1SummarySectionDto b2csSummarylist = gstr1B2CSCalculation
			 * .addB2csGstnData(invCrDrSummary);
			 * b2csSummary.add(b2csSummarylist);
			 */
			Gstr1SummaryScreenRespDto summaryB2csResp = new Gstr1SummaryScreenRespDto();
			if (b2csSummary != null) {
				b2csSummary.forEach(b2csEySummary -> {

					summaryB2csResp
							.setTaxDocType(b2csEySummary.getTaxDocType());
					summaryB2csResp.setAspTaxableValue(
							b2csEySummary.getAspTaxableValue());
					summaryB2csResp
							.setAspTaxPayble(b2csEySummary.getAspTaxPayble());
					summaryB2csResp.setAspInvoiceValue(
							b2csEySummary.getAspInvoiceValue());
					summaryB2csResp.setAspIgst(b2csEySummary.getAspIgst());
					summaryB2csResp.setAspCgst(b2csEySummary.getAspCgst());
					summaryB2csResp.setAspSgst(b2csEySummary.getAspSgst());
					summaryB2csResp.setAspCess(b2csEySummary.getAspCess());
					summaryB2csResp.setAspCount(b2csEySummary.getAspCount());

					summaryB2csResp.setGstnTaxableValue(
							b2csEySummary.getGstnTaxableValue());
					summaryB2csResp
							.setGstnTaxPayble(b2csEySummary.getGstnTaxPayble());
					summaryB2csResp.setGstnInvoiceValue(
							b2csEySummary.getGstnInvoiceValue());
					summaryB2csResp.setGstnIgst(b2csEySummary.getGstnIgst());
					summaryB2csResp.setGstnCgst(b2csEySummary.getGstnCgst());
					summaryB2csResp.setGstnSgst(b2csEySummary.getGstnSgst());
					summaryB2csResp.setGstnCess(b2csEySummary.getGstnCess());
					summaryB2csResp.setGstnCount(b2csEySummary.getGstnCount());

				});
			}

			/*
			 * Calculating Difference Asp - Gstn
			 */

			BigDecimal diffInv = subMethod(summaryB2csResp.getAspInvoiceValue(),
					summaryB2csResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryB2csResp.getAspTaxableValue(),
					summaryB2csResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryB2csResp.getAspTaxPayble(),
					summaryB2csResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryB2csResp.getAspIgst(),
					summaryB2csResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryB2csResp.getAspSgst(),
					summaryB2csResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryB2csResp.getAspCgst(),
					summaryB2csResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryB2csResp.getAspCess(),
					summaryB2csResp.getGstnCess());

			Integer aspCount = (summaryB2csResp.getAspCount() != null)
					? summaryB2csResp.getAspCount() : 0;
			Integer gstnCount = (summaryB2csResp.getGstnCount() != null)
					? summaryB2csResp.getGstnCount() : 0;

			summaryB2csResp.setDiffCount(aspCount - gstnCount);
			summaryB2csResp.setDiffInvoiceValue(diffInv);
			summaryB2csResp.setDiffTaxableValue(diffTaxable);
			summaryB2csResp.setDiffTaxPayble(diffTaxPayble);
			summaryB2csResp.setDiffIgst(diffIgst);
			summaryB2csResp.setDiffCgst(diffCgst);
			summaryB2csResp.setDiffSgst(diffSgst);
			summaryB2csResp.setDiffCess(diffCess);
			b2csSummaryRespList.add(summaryB2csResp);
		}

		/**
		 * B2CSA Section Stub Data
		 */
		List<Gstr1SummaryScreenRespDto> b2csaSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2csa = dto.getB2csa();
			List<Gstr1SummarySectionDto> b2csaSummary = b2csa.getEySummary();
			/*
			 * List<Gstr1SummarySectionDto> b2csaSummary = new ArrayList<>();
			 * Gstr1SummarySectionDto addB2csaAspData = gstr1B2CSCalculation
			 * .addB2csaGstnData(b2csaSummaryList);
			 * b2csaSummary.add(addB2csaAspData);
			 */
			Gstr1SummaryScreenRespDto summaryB2csaResp = new Gstr1SummaryScreenRespDto();
			if (b2csaSummary != null) {
				b2csaSummary.forEach(b2csaEySummary -> {

					summaryB2csaResp
							.setTaxDocType(b2csaEySummary.getTaxDocType());
					summaryB2csaResp.setAspTaxableValue(
							b2csaEySummary.getAspTaxableValue());
					summaryB2csaResp
							.setAspTaxPayble(b2csaEySummary.getAspTaxPayble());
					summaryB2csaResp.setAspInvoiceValue(
							b2csaEySummary.getAspInvoiceValue());
					summaryB2csaResp.setAspIgst(b2csaEySummary.getAspIgst());
					summaryB2csaResp.setAspCgst(b2csaEySummary.getAspCgst());
					summaryB2csaResp.setAspSgst(b2csaEySummary.getAspSgst());
					summaryB2csaResp.setAspCess(b2csaEySummary.getAspCess());
					summaryB2csaResp.setAspCount(b2csaEySummary.getAspCount());

					summaryB2csaResp.setGstnTaxableValue(
							b2csaEySummary.getGstnTaxableValue());
					summaryB2csaResp.setGstnTaxPayble(
							b2csaEySummary.getGstnTaxPayble());
					summaryB2csaResp.setGstnInvoiceValue(
							b2csaEySummary.getGstnInvoiceValue());
					summaryB2csaResp.setGstnIgst(b2csaEySummary.getGstnIgst());
					summaryB2csaResp.setGstnCgst(b2csaEySummary.getGstnCgst());
					summaryB2csaResp.setGstnSgst(b2csaEySummary.getGstnSgst());
					summaryB2csaResp.setGstnCess(b2csaEySummary.getGstnCess());
					summaryB2csaResp
							.setGstnCount(b2csaEySummary.getGstnCount());

				});
			}

			/*
			 * Calculating Difference Asp - Gstn
			 */
			BigDecimal diffInv = subMethod(
					summaryB2csaResp.getAspInvoiceValue(),
					summaryB2csaResp.getGstnInvoiceValue());
			BigDecimal diffTaxable = subMethod(
					summaryB2csaResp.getAspTaxableValue(),
					summaryB2csaResp.getGstnTaxableValue());
			BigDecimal diffTaxPayble = subMethod(
					summaryB2csaResp.getAspTaxPayble(),
					summaryB2csaResp.getGstnTaxPayble());
			BigDecimal diffIgst = subMethod(summaryB2csaResp.getAspIgst(),
					summaryB2csaResp.getGstnIgst());
			BigDecimal diffSgst = subMethod(summaryB2csaResp.getAspSgst(),
					summaryB2csaResp.getGstnSgst());

			BigDecimal diffCgst = subMethod(summaryB2csaResp.getAspCgst(),
					summaryB2csaResp.getGstnCgst());

			BigDecimal diffCess = subMethod(summaryB2csaResp.getAspCess(),
					summaryB2csaResp.getGstnCess());

			Integer aspCount = (summaryB2csaResp.getAspCount() != null)
					? summaryB2csaResp.getAspCount() : 0;
			Integer gstnCount = (summaryB2csaResp.getGstnCount() != null)
					? summaryB2csaResp.getGstnCount() : 0;

			summaryB2csaResp.setDiffCount(aspCount - gstnCount);
			summaryB2csaResp.setDiffInvoiceValue(diffInv);
			summaryB2csaResp.setDiffTaxableValue(diffTaxable);
			summaryB2csaResp.setDiffTaxPayble(diffTaxPayble);
			summaryB2csaResp.setDiffIgst(diffIgst);
			summaryB2csaResp.setDiffCgst(diffCgst);
			summaryB2csaResp.setDiffSgst(diffSgst);
			summaryB2csaResp.setDiffCess(diffCess);
			b2csaSummaryRespList.add(summaryB2csaResp);
		}

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<Gstr1SummaryScreenRespDto> handleB2BResp = respHandler
				.pvsSubmitB2BResp(b2bSummaryRespList, "B2B");
		List<Gstr1SummaryScreenRespDto> handleB2BAResp = respHandler
				.pvsSubmitB2BResp(b2baSummaryRespList, "B2BA");
		List<Gstr1SummaryScreenRespDto> handleB2CLResp = respHandler
				.pvsSubmitB2BResp(b2clSummaryRespList, "B2CL");
		List<Gstr1SummaryScreenRespDto> handleB2CLAResp = respHandler
				.pvsSubmitB2BResp(b2claSummaryRespList, "B2CLA");
		List<Gstr1SummaryScreenRespDto> handleEXPResp = respHandler
				.pvsSubmitB2BResp(expSummaryRespList, "EXPORTS");
		List<Gstr1SummaryScreenRespDto> handleEXPAResp = respHandler
				.pvsSubmitB2BResp(expaSummaryRespList, "EXPORTS-A");
		List<Gstr1SummaryScreenRespDto> handleCDNRResp = respHandler
				.pvsSubmitB2BResp(cdnrSummaryRespList, "CDNR");
		List<Gstr1SummaryScreenRespDto> handleCDNRAResp = respHandler
				.pvsSubmitB2BResp(cdnraSummaryRespList, "CDNRA");
		List<Gstr1SummaryScreenRespDto> handleCDNURResp = respHandler
				.pvsSubmitB2BResp(cdnurSummaryRespList, "CDNUR");
		List<Gstr1SummaryScreenRespDto> handleCDNURAResp = respHandler
				.pvsSubmitB2BResp(cdnuraSummaryRespList, "CDNURA");

		List<Gstr1SummaryScreenRespDto> handleB2CSResp = respHandler
				.pvsSubmitB2BResp(b2csSummaryRespList, "B2CS");
		List<Gstr1SummaryScreenRespDto> handleB2CSAResp = respHandler
				.pvsSubmitB2BResp(b2csaSummaryRespList, "B2CSA");
		List<Gstr1SummaryScreenRespDto> responseAdvc = getAdvancedData(list);

		List<Gstr1SummaryScreenRespDto> response = new ArrayList<>();
		response.addAll(handleB2BResp);
		response.addAll(handleB2BAResp);
		response.addAll(handleB2CLResp);
		response.addAll(handleB2CLAResp);
		response.addAll(handleEXPResp);
		response.addAll(handleEXPAResp);
		response.addAll(handleCDNRResp);
		response.addAll(handleCDNRAResp);
		response.addAll(handleCDNURResp);
		response.addAll(handleCDNURAResp);
		response.addAll(handleB2CSResp);
		response.addAll(handleB2CSAResp);

		return new Pair<List<Gstr1SummaryScreenRespDto>, List<Gstr1SummaryScreenRespDto>>(
				response, responseAdvc);

	}

	/*
	 * public List<Gstr1SummaryScreenRespDto> handleAdvReqAndResp(
	 * Annexure1SummaryReqDto annexure1SummaryRequest) {
	 * 
	 * 
	 * 
	 * LOGGER.debug(" Summary Execution For B2B,B2BA,B2CL,B2CLA,B2CS," +
	 * "B2CSA, CDNR,CDNRA,CDNUR AND CDNURA  Sections BEGIN ");
	 * SearchResult<Gstr1CompleteSummaryDto> summaryResult = searchService
	 * .<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
	 * Gstr1CompleteSummaryDto.class);
	 * LOGGER.debug(" Summary Execution For B2B,B2BA,B2CL,B2CLA,B2CS," +
	 * "B2CSA, CDNR,CDNRA,CDNUR AND CDNURA  Sections END ");
	 * 
	 * 
	 * 
	 * List<? extends Gstr1CompleteSummaryDto> list = summaryResult
	 * .getResult();
	 * 
	 * 
	 * List<Gstr1SummaryScreenRespDto> atSummaryRespList = new ArrayList<>();
	 * 
	 * for (Gstr1CompleteSummaryDto dto : list) { Gstr1BasicSectionSummaryDto at
	 * = dto.getAt(); List<Gstr1SummarySectionDto> atSummary =
	 * at.getEySummary(); Gstr1SummaryScreenRespDto summaryResp = new
	 * Gstr1SummaryScreenRespDto(); if (atSummary != null) {
	 * atSummary.forEach(atEySummary -> {
	 * 
	 * summaryResp.setTaxDocType(atEySummary.getTaxDocType()); summaryResp
	 * .setAspTaxableValue(atEySummary.getAspTaxableValue());
	 * summaryResp.setAspTaxPayble(atEySummary.getAspTaxPayble());
	 * summaryResp.setAspInvoiceValue(atEySummary.getAspInvoiceValue());
	 * summaryResp.setAspIgst(atEySummary.getAspIgst());
	 * summaryResp.setAspCgst(atEySummary.getAspCgst());
	 * summaryResp.setAspSgst(atEySummary.getAspSgst());
	 * summaryResp.setAspCess(atEySummary.getAspCess());
	 * summaryResp.setAspCount(atEySummary.getAspCount());
	 * 
	 * summaryResp.setGstnTaxableValue(atEySummary.getGstnTaxableValue());
	 * summaryResp.setGstnTaxPayble(atEySummary.getGstnTaxPayble());
	 * summaryResp.setGstnInvoiceValue(atEySummary.getGstnInvoiceValue());
	 * summaryResp.setGstnIgst(atEySummary.getGstnIgst());
	 * summaryResp.setGstnCgst(atEySummary.getGstnCgst());
	 * summaryResp.setGstnSgst(atEySummary.getGstnSgst());
	 * summaryResp.setGstnCess(atEySummary.getGstnCess());
	 * summaryResp.setGstnCount(atEySummary.getGstnCount());
	 * 
	 * }); }
	 * 
	 * Calculating Difference Asp - Gstn
	 * 
	 * BigDecimal diffTotaltax = subMethod( summaryResp.getAspInvoiceValue(),
	 * summaryResp.getGstnInvoiceValue()); BigDecimal diffTaxable =
	 * subMethod(summaryResp.getAspTaxableValue(),
	 * summaryResp.getGstnTaxableValue()); BigDecimal diffTaxPayble =
	 * subMethod(summaryResp.getAspTaxPayble(), summaryResp.getGstnTaxPayble());
	 * BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
	 * summaryResp.getGstnIgst()); BigDecimal diffSgst =
	 * subMethod(summaryResp.getAspSgst(), summaryResp.getGstnSgst());
	 * 
	 * BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
	 * summaryResp.getGstnCgst());
	 * 
	 * BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
	 * summaryResp.getGstnCess());
	 * 
	 * Integer aspCount = (summaryResp.getAspCount() != null) ?
	 * summaryResp.getAspCount() : 0; Integer gstnCount =
	 * (summaryResp.getGstnCount() != null) ? summaryResp.getGstnCount() : 0;
	 * 
	 * summaryResp.setDiffCount(aspCount - gstnCount);
	 * summaryResp.setDiffInvoiceValue(diffTotaltax);
	 * summaryResp.setDiffTaxableValue(diffTaxable);
	 * summaryResp.setDiffTaxPayble(diffTaxPayble);
	 * summaryResp.setDiffIgst(diffIgst); summaryResp.setDiffCgst(diffCgst);
	 * summaryResp.setDiffSgst(diffSgst); summaryResp.setDiffCess(diffCess);
	 * atSummaryRespList.add(summaryResp); }
	 * 
	 * ATA
	 * 
	 * List<Gstr1SummaryScreenRespDto> ataSummaryRespList = new ArrayList<>();
	 * 
	 * for (Gstr1CompleteSummaryDto dto : list) { Gstr1BasicSectionSummaryDto
	 * ata = dto.getAta(); List<Gstr1SummarySectionDto> ataSummary =
	 * ata.getEySummary(); Gstr1SummaryScreenRespDto summaryResp = new
	 * Gstr1SummaryScreenRespDto(); if (ataSummary != null) {
	 * ataSummary.forEach(ataEySummary -> {
	 * 
	 * summaryResp.setTaxDocType(ataEySummary.getTaxDocType()); summaryResp
	 * .setAspTaxableValue(ataEySummary.getAspTaxableValue());
	 * summaryResp.setAspTaxPayble(ataEySummary.getAspTaxPayble());
	 * summaryResp.setAspInvoiceValue(ataEySummary.getAspInvoiceValue());
	 * summaryResp.setAspIgst(ataEySummary.getAspIgst());
	 * summaryResp.setAspCgst(ataEySummary.getAspCgst());
	 * summaryResp.setAspSgst(ataEySummary.getAspSgst());
	 * summaryResp.setAspCess(ataEySummary.getAspCess());
	 * summaryResp.setAspCount(ataEySummary.getAspCount());
	 * 
	 * summaryResp.setGstnTaxableValue(ataEySummary.getGstnTaxableValue());
	 * summaryResp.setGstnTaxPayble(ataEySummary.getGstnTaxPayble());
	 * summaryResp.setGstnInvoiceValue(ataEySummary.getGstnInvoiceValue());
	 * summaryResp.setGstnIgst(ataEySummary.getGstnIgst());
	 * summaryResp.setGstnCgst(ataEySummary.getGstnCgst());
	 * summaryResp.setGstnSgst(ataEySummary.getGstnSgst());
	 * summaryResp.setGstnCess(ataEySummary.getGstnCess());
	 * summaryResp.setGstnCount(ataEySummary.getGstnCount());
	 * 
	 * }); }
	 * 
	 * Calculating Difference Asp - Gstn
	 * 
	 * BigDecimal diffInv = subMethod(summaryResp.getAspInvoiceValue(),
	 * summaryResp.getGstnInvoiceValue()); BigDecimal diffTaxable =
	 * subMethod(summaryResp.getAspTaxableValue(),
	 * summaryResp.getGstnTaxableValue()); BigDecimal diffTaxPayble =
	 * subMethod(summaryResp.getAspTaxPayble(), summaryResp.getGstnTaxPayble());
	 * BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
	 * summaryResp.getGstnIgst()); BigDecimal diffSgst =
	 * subMethod(summaryResp.getAspSgst(), summaryResp.getGstnSgst());
	 * 
	 * BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
	 * summaryResp.getGstnCgst());
	 * 
	 * BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
	 * summaryResp.getGstnCess());
	 * 
	 * Integer aspCount = (summaryResp.getAspCount() != null) ?
	 * summaryResp.getAspCount() : 0; Integer gstnCount =
	 * (summaryResp.getGstnCount() != null) ? summaryResp.getGstnCount() : 0;
	 * 
	 * summaryResp.setDiffCount(aspCount - gstnCount);
	 * summaryResp.setDiffInvoiceValue(diffInv);
	 * summaryResp.setDiffTaxableValue(diffTaxable);
	 * summaryResp.setDiffTaxPayble(diffTaxPayble);
	 * summaryResp.setDiffIgst(diffIgst); summaryResp.setDiffCgst(diffCgst);
	 * summaryResp.setDiffSgst(diffSgst); summaryResp.setDiffCess(diffCess);
	 * ataSummaryRespList.add(summaryResp);
	 * 
	 * }
	 * 
	 * TXPD
	 * 
	 * List<Gstr1SummaryScreenRespDto> txpdSummaryRespList = new ArrayList<>();
	 * 
	 * for (Gstr1CompleteSummaryDto dto : list) { Gstr1BasicSectionSummaryDto
	 * txpd = dto.getTxpd(); List<Gstr1SummarySectionDto> txpdSummary =
	 * txpd.getEySummary(); Gstr1SummaryScreenRespDto summaryResp = new
	 * Gstr1SummaryScreenRespDto(); if (txpdSummary != null) {
	 * txpdSummary.forEach(txpdEySummary -> {
	 * 
	 * summaryResp.setTaxDocType(txpdEySummary.getTaxDocType()); summaryResp
	 * .setAspTaxableValue(txpdEySummary.getAspTaxableValue());
	 * summaryResp.setAspTaxPayble(txpdEySummary.getAspTaxPayble());
	 * summaryResp.setAspInvoiceValue(txpdEySummary.getAspInvoiceValue());
	 * summaryResp.setAspIgst(txpdEySummary.getAspIgst());
	 * summaryResp.setAspCgst(txpdEySummary.getAspCgst());
	 * summaryResp.setAspSgst(txpdEySummary.getAspSgst());
	 * summaryResp.setAspCess(txpdEySummary.getAspCess());
	 * summaryResp.setAspCount(txpdEySummary.getAspCount());
	 * 
	 * summaryResp.setGstnTaxableValue(txpdEySummary.getGstnTaxableValue());
	 * summaryResp.setGstnTaxPayble(txpdEySummary.getGstnTaxPayble());
	 * summaryResp.setGstnInvoiceValue(txpdEySummary.getGstnInvoiceValue());
	 * summaryResp.setGstnIgst(txpdEySummary.getGstnIgst());
	 * summaryResp.setGstnCgst(txpdEySummary.getGstnCgst());
	 * summaryResp.setGstnSgst(txpdEySummary.getGstnSgst());
	 * summaryResp.setGstnCess(txpdEySummary.getGstnCess());
	 * summaryResp.setGstnCount(txpdEySummary.getGstnCount());
	 * 
	 * 
	 * }); }
	 * 
	 * 
	 * Calculating Difference Asp - Gstn
	 * 
	 * BigDecimal diffInv = subMethod(summaryResp.getAspInvoiceValue(),
	 * summaryResp.getGstnInvoiceValue()); BigDecimal diffTaxable =
	 * subMethod(summaryResp.getAspTaxableValue(),
	 * summaryResp.getGstnTaxableValue()); BigDecimal diffTaxPayble =
	 * subMethod(summaryResp.getAspTaxPayble(), summaryResp.getGstnTaxPayble());
	 * BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
	 * summaryResp.getGstnIgst()); BigDecimal diffSgst =
	 * subMethod(summaryResp.getAspSgst(), summaryResp.getGstnSgst());
	 * 
	 * BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
	 * summaryResp.getGstnCgst());
	 * 
	 * BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
	 * summaryResp.getGstnCess());
	 * 
	 * Integer aspCount = (summaryResp.getAspCount() != null) ?
	 * summaryResp.getAspCount() : 0; Integer gstnCount =
	 * (summaryResp.getGstnCount() != null) ? summaryResp.getGstnCount() : 0;
	 * 
	 * summaryResp.setDiffCount(aspCount - gstnCount);
	 * summaryResp.setDiffInvoiceValue(diffInv);
	 * summaryResp.setDiffTaxableValue(diffTaxable);
	 * summaryResp.setDiffTaxPayble(diffTaxPayble);
	 * summaryResp.setDiffIgst(diffIgst); summaryResp.setDiffCgst(diffCgst);
	 * summaryResp.setDiffSgst(diffSgst); summaryResp.setDiffCess(diffCess);
	 * txpdSummaryRespList.add(summaryResp);
	 * 
	 * }
	 * 
	 * // TXPDA List<Gstr1SummaryScreenRespDto> txpdaSummaryRespList = new
	 * ArrayList<>();
	 * 
	 * 
	 * for (Gstr1CompleteSummaryDto dto : list) { Gstr1BasicSectionSummaryDto
	 * txpda = dto.getTxpda(); List<Gstr1SummarySectionDto> txpdaSummary =
	 * txpda.getEySummary(); Gstr1SummaryScreenRespDto summaryResp = new
	 * Gstr1SummaryScreenRespDto(); if (txpdaSummary != null) {
	 * txpdaSummary.forEach(txpdaEySummary -> {
	 * 
	 * summaryResp.setTaxDocType(txpdaEySummary.getTaxDocType()); summaryResp
	 * .setAspTaxableValue(txpdaEySummary.getAspTaxableValue());
	 * summaryResp.setAspTaxPayble(txpdaEySummary.getAspTaxPayble());
	 * summaryResp.setAspInvoiceValue(txpdaEySummary.getAspInvoiceValue());
	 * summaryResp.setAspIgst(txpdaEySummary.getAspIgst());
	 * summaryResp.setAspCgst(txpdaEySummary.getAspCgst());
	 * summaryResp.setAspSgst(txpdaEySummary.getAspSgst());
	 * summaryResp.setAspCess(txpdaEySummary.getAspCess());
	 * summaryResp.setAspCount(txpdaEySummary.getAspCount());
	 * 
	 * summaryResp.setGstnTaxableValue(txpdaEySummary.getGstnTaxableValue());
	 * summaryResp.setGstnTaxPayble(txpdaEySummary.getGstnTaxPayble());
	 * summaryResp.setGstnInvoiceValue(txpdaEySummary.getGstnInvoiceValue());
	 * summaryResp.setGstnIgst(txpdaEySummary.getGstnIgst());
	 * summaryResp.setGstnCgst(txpdaEySummary.getGstnCgst());
	 * summaryResp.setGstnSgst(txpdaEySummary.getGstnSgst());
	 * summaryResp.setGstnCess(txpdaEySummary.getGstnCess());
	 * summaryResp.setGstnCount(txpdaEySummary.getGstnCount());
	 * 
	 * 
	 * }); }
	 * 
	 * 
	 * Calculating Difference Asp - Gstn
	 * 
	 * BigDecimal diffInv = subMethod(summaryResp.getAspInvoiceValue(),
	 * summaryResp.getGstnInvoiceValue()); BigDecimal diffTaxable =
	 * subMethod(summaryResp.getAspTaxableValue(),
	 * summaryResp.getGstnTaxableValue()); BigDecimal diffTaxPayble =
	 * subMethod(summaryResp.getAspTaxPayble(), summaryResp.getGstnTaxPayble());
	 * BigDecimal diffIgst = subMethod(summaryResp.getAspIgst(),
	 * summaryResp.getGstnIgst()); BigDecimal diffSgst =
	 * subMethod(summaryResp.getAspSgst(), summaryResp.getGstnSgst());
	 * 
	 * BigDecimal diffCgst = subMethod(summaryResp.getAspCgst(),
	 * summaryResp.getGstnCgst());
	 * 
	 * BigDecimal diffCess = subMethod(summaryResp.getAspCess(),
	 * summaryResp.getGstnCess());
	 * 
	 * Integer aspCount = (summaryResp.getAspCount() != null) ?
	 * summaryResp.getAspCount() : 0; Integer gstnCount =
	 * (summaryResp.getGstnCount() != null) ? summaryResp.getGstnCount() : 0;
	 * 
	 * summaryResp.setDiffCount(aspCount - gstnCount);
	 * summaryResp.setDiffInvoiceValue(diffInv);
	 * summaryResp.setDiffTaxableValue(diffTaxable);
	 * summaryResp.setDiffTaxPayble(diffTaxPayble);
	 * summaryResp.setDiffIgst(diffIgst); summaryResp.setDiffCgst(diffCgst);
	 * summaryResp.setDiffSgst(diffSgst); summaryResp.setDiffCess(diffCess);
	 * txpdaSummaryRespList.add(summaryResp); }
	 * 
	 * Gson gson = GsonUtil.newSAPGsonInstance();
	 * 
	 * List<Gstr1SummaryScreenRespDto> handleATResp =
	 * respHandler.pvsSubmitB2BResp(atSummaryRespList,atconst);
	 * List<Gstr1SummaryScreenRespDto> handleATAResp =
	 * respHandler.pvsSubmitB2BResp(ataSummaryRespList,ataconst);
	 * List<Gstr1SummaryScreenRespDto> handleTXPDResp =
	 * respHandler.pvsSubmitB2BResp(txpdSummaryRespList,txpdconst);
	 * List<Gstr1SummaryScreenRespDto> handleTXPDAResp =
	 * respHandler.pvsSubmitB2BResp(txpdaSummaryRespList,txpdaconst);
	 * 
	 * 
	 * 
	 * List<Gstr1SummaryScreenRespDto> response = new ArrayList<>();
	 * response.addAll(handleATResp); response.addAll(handleTXPDResp);
	 * response.addAll(handleATAResp); response.addAll(handleTXPDAResp); return
	 * response; }
	 */

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

	private List<Gstr1SummaryScreenRespDto> getAdvancedData(
			List<? extends Gstr1CompleteSummaryDto> list) {

		List<Gstr1SummaryScreenRespDto> atSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto at = dto.getAt();
			List<Gstr1SummarySectionDto> atSummary = at.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (atSummary != null) {
				atSummary.forEach(atEySummary -> {

					summaryResp.setTaxDocType(atEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							atEySummary.getAspTaxableValue());
					summaryResp.setAspTaxPayble(atEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(
							atEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(atEySummary.getAspIgst());
					summaryResp.setAspCgst(atEySummary.getAspCgst());
					summaryResp.setAspSgst(atEySummary.getAspSgst());
					summaryResp.setAspCess(atEySummary.getAspCess());
					summaryResp.setAspCount(atEySummary.getAspCount());

					summaryResp.setGstnTaxableValue(
							atEySummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(atEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(
							atEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(atEySummary.getGstnIgst());
					summaryResp.setGstnCgst(atEySummary.getGstnCgst());
					summaryResp.setGstnSgst(atEySummary.getGstnSgst());
					summaryResp.setGstnCess(atEySummary.getGstnCess());
					summaryResp.setGstnCount(atEySummary.getGstnCount());

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
			atSummaryRespList.add(summaryResp);
		}
		/*
		 * ATA
		 */
		List<Gstr1SummaryScreenRespDto> ataSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto ata = dto.getAta();
			List<Gstr1SummarySectionDto> ataSummary = ata.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (ataSummary != null) {
				ataSummary.forEach(ataEySummary -> {

					summaryResp.setTaxDocType(ataEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							ataEySummary.getAspTaxableValue());
					summaryResp.setAspTaxPayble(ataEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(
							ataEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(ataEySummary.getAspIgst());
					summaryResp.setAspCgst(ataEySummary.getAspCgst());
					summaryResp.setAspSgst(ataEySummary.getAspSgst());
					summaryResp.setAspCess(ataEySummary.getAspCess());
					summaryResp.setAspCount(ataEySummary.getAspCount());

					summaryResp.setGstnTaxableValue(
							ataEySummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(ataEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(
							ataEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(ataEySummary.getGstnIgst());
					summaryResp.setGstnCgst(ataEySummary.getGstnCgst());
					summaryResp.setGstnSgst(ataEySummary.getGstnSgst());
					summaryResp.setGstnCess(ataEySummary.getGstnCess());
					summaryResp.setGstnCount(ataEySummary.getGstnCount());

				});
			}
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

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto txpd = dto.getTxpd();
			List<Gstr1SummarySectionDto> txpdSummary = txpd.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (txpdSummary != null) {
				txpdSummary.forEach(txpdEySummary -> {

					summaryResp.setTaxDocType(txpdEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							txpdEySummary.getAspTaxableValue());
					summaryResp
							.setAspTaxPayble(txpdEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(
							txpdEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(txpdEySummary.getAspIgst());
					summaryResp.setAspCgst(txpdEySummary.getAspCgst());
					summaryResp.setAspSgst(txpdEySummary.getAspSgst());
					summaryResp.setAspCess(txpdEySummary.getAspCess());
					summaryResp.setAspCount(txpdEySummary.getAspCount());

					summaryResp.setGstnTaxableValue(
							txpdEySummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(txpdEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(
							txpdEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(txpdEySummary.getGstnIgst());
					summaryResp.setGstnCgst(txpdEySummary.getGstnCgst());
					summaryResp.setGstnSgst(txpdEySummary.getGstnSgst());
					summaryResp.setGstnCess(txpdEySummary.getGstnCess());
					summaryResp.setGstnCount(txpdEySummary.getGstnCount());

				});
			}

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

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto txpda = dto.getTxpda();
			List<Gstr1SummarySectionDto> txpdaSummary = txpda.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (txpdaSummary != null) {
				txpdaSummary.forEach(txpdaEySummary -> {

					summaryResp.setTaxDocType(txpdaEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							txpdaEySummary.getAspTaxableValue());
					summaryResp
							.setAspTaxPayble(txpdaEySummary.getAspTaxPayble());
					summaryResp.setAspInvoiceValue(
							txpdaEySummary.getAspInvoiceValue());
					summaryResp.setAspIgst(txpdaEySummary.getAspIgst());
					summaryResp.setAspCgst(txpdaEySummary.getAspCgst());
					summaryResp.setAspSgst(txpdaEySummary.getAspSgst());
					summaryResp.setAspCess(txpdaEySummary.getAspCess());
					summaryResp.setAspCount(txpdaEySummary.getAspCount());

					summaryResp.setGstnTaxableValue(
							txpdaEySummary.getGstnTaxableValue());
					summaryResp.setGstnTaxPayble(
							txpdaEySummary.getGstnTaxPayble());
					summaryResp.setGstnInvoiceValue(
							txpdaEySummary.getGstnInvoiceValue());
					summaryResp.setGstnIgst(txpdaEySummary.getGstnIgst());
					summaryResp.setGstnCgst(txpdaEySummary.getGstnCgst());
					summaryResp.setGstnSgst(txpdaEySummary.getGstnSgst());
					summaryResp.setGstnCess(txpdaEySummary.getGstnCess());
					summaryResp.setGstnCount(txpdaEySummary.getGstnCount());

				});
			}

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

		List<Gstr1SummaryScreenRespDto> handleATResp = respHandler
				.pvsSubmitB2BResp(atSummaryRespList, atconst);
		List<Gstr1SummaryScreenRespDto> handleATAResp = respHandler
				.pvsSubmitB2BResp(ataSummaryRespList, ataconst);
		List<Gstr1SummaryScreenRespDto> handleTXPDResp = respHandler
				.pvsSubmitB2BResp(txpdSummaryRespList, txpdconst);
		List<Gstr1SummaryScreenRespDto> handleTXPDAResp = respHandler
				.pvsSubmitB2BResp(txpdaSummaryRespList, txpdaconst);

		List<Gstr1SummaryScreenRespDto> response = new ArrayList<>();
		response.addAll(handleATResp);
		response.addAll(handleTXPDResp);
		response.addAll(handleATAResp);
		response.addAll(handleTXPDAResp);
		return response;

	}

}