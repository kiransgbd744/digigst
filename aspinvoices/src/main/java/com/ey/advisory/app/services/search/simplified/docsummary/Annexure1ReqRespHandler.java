package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicEcomSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1EcommSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1InwardSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryEcomResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1GstnCalculation;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("Annexure1ReqRespHandler")
public class Annexure1ReqRespHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Annexure1ReqRespHandler.class);

	/*
	 * @Autowired
	 * 
	 * @Qualifier("SimplDocSummarySearchService") private
	 * SimplDocSummarySearchService service;
	 */

	@Autowired
	@Qualifier("SimpleDetailSummarySearchService")
	SimpleDetailSummarySearchService service;

	@Autowired
	@Qualifier("Anx1InwardDetailSummarySearchService")
	private Anx1InwardDetailSummarySearchService inService;

	@Autowired
	@Qualifier("Annexure1B2BRespHandler")
	private Annexure1B2BRespHandler annexure1B2BRespHandler;

	@Autowired
	@Qualifier("Annexure1B2CRespHandler")
	private Annexure1B2CRespHandler annexure1B2CRespHandler;

	@Autowired
	@Qualifier("Annexure1ExptRespHandler")
	private Annexure1ExptRespHandler annexure1ExptRespHandler;

	@Autowired
	@Qualifier("Annexure1ExpwtRespHandler")
	private Annexure1ExpwtRespHandler annexure1ExpwtRespHandler;

	@Autowired
	@Qualifier("Annexure1SeztRespHandler")
	private Annexure1SeztRespHandler annexure1SeztRespHandler;

	@Autowired
	@Qualifier("Annexure1SezwtRespHandler")
	private Annexure1SezwtRespHandler annexure1SezwtRespHandler;

	@Autowired
	@Qualifier("Annexure1DeemExptRespHandler")
	private Annexure1DeemExptRespHandler annexure1DeemExpRespHandler;

	@Autowired
	@Qualifier("annexure1ImpgRespHandler")
	private Annexure1ImpgRespHandler annexure1ImpgRespHandler;

	@Autowired
	@Qualifier("annexure1ImpgSezRespHandler")
	private Annexure1ImpgSezRespHandler annexure1ImpgSezRespHandler;

	@Autowired
	@Qualifier("annexure1ImpsRespHandler")
	private Annexure1ImpsRespHandler annexure1ImpsRespHandler;

	@Autowired
	@Qualifier("annexure1RevRespHandler")
	private Annexure1RevRespHandler annexure1RevRespHandler;

	@Autowired
	@Qualifier("annexure1EcomRespHandler")
	private Annexure1EcomRespHandler annexure1EcomRespHandler;

	@Autowired
	@Qualifier("Anx1DetailEcommSummarySearchService")
	private Anx1DetailEcommSummarySearchService fetchEcomm;

	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;

	@SuppressWarnings("unchecked")
	public JsonElement handleAnnexure1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {
		LOGGER.debug("OUTWARD Summary Execution BEGIN ");
		SearchResult<Annexure1SummaryDto> summaryResult = service
				.<Annexure1SummaryDto>find(annexure1SummaryRequest, null,
						Annexure1SummaryDto.class);
		LOGGER.debug("OUTWARD Summary Execution END ");
		List<? extends Annexure1SummaryDto> list = summaryResult.getResult();
		List<Annexure1SummaryResp1Dto> b2cSummaryRespList = new ArrayList<>();

		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto b2c = an.getB2c();
			List<Annexure1SummarySectionDto> b2cEySummary = b2c.getEySummary();
			if (b2cEySummary != null) {
				b2cEySummary.forEach(b2cSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();
					// EY Resp
					summaryResp.setTableSection(b2cSummary.getTableSection());
					summaryResp.setDocType(b2cSummary.getDocType());
					summaryResp.setEyInvoiceValue(b2cSummary.getInvValue());
					summaryResp.setEyTaxableValue(b2cSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(b2cSummary.getTaxPayble());
					summaryResp.setEyIgst(b2cSummary.getIgst());
					summaryResp.setEyCgst(b2cSummary.getCgst());
					summaryResp.setEySgst(b2cSummary.getSgst());
					summaryResp.setEyCess(b2cSummary.getCess());
					summaryResp.setEyCount(b2cSummary.getRecords());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnInvoiceValue(
							b2cSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							b2cSummary.getGstnTaxableValue());
					summaryResp.setGstnTaxPayble(b2cSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(b2cSummary.getGstnIgst());
					summaryResp.setGstnCgst(b2cSummary.getGstnCgst());
					summaryResp.setGstnSgst(b2cSummary.getGstnSgst());
					summaryResp.setGstnCess(b2cSummary.getGstnCess());
					summaryResp.setGstnCount(b2cSummary.getGstnCount());

					// Memo
					summaryResp.setMemoInvoiceValue(b2cSummary.getInvValue());
					summaryResp
							.setMemoTaxableValue(b2cSummary.getTaxableValue());
					summaryResp.setMemoTaxPayble(b2cSummary.getTaxPayble());
					summaryResp.setMemoIgst(b2cSummary.getIgst());
					summaryResp.setMemoCgst(b2cSummary.getCgst());
					summaryResp.setMemoSgst(b2cSummary.getSgst());
					summaryResp.setMemoCess(b2cSummary.getCess());
					summaryResp.setMemoCount(b2cSummary.getRecords());

					// Diff
					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					b2cSummaryRespList.add(summaryResp);
				});
			}
		}
		JsonElement b2cRespBody = annexure1B2CRespHandler
				.handleB2CResp(b2cSummaryRespList);

		List<Annexure1SummaryResp1Dto> b2bSummaryRespList = new ArrayList<>();

		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto b2b = an.getB2b();
			List<Annexure1SummarySectionDto> b2bEySummary = b2b.getEySummary();
			if (b2bEySummary != null) {
				b2bEySummary.forEach(b2bSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();
					// EY Resp
					summaryResp.setTableSection(b2bSummary.getTableSection());
					summaryResp.setDocType(b2bSummary.getDocType());
					summaryResp.setEyInvoiceValue(b2bSummary.getInvValue());
					summaryResp.setEyTaxableValue(b2bSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(b2bSummary.getTaxPayble());
					summaryResp.setEyIgst(b2bSummary.getIgst());
					summaryResp.setEyCgst(b2bSummary.getCgst());
					summaryResp.setEySgst(b2bSummary.getSgst());
					summaryResp.setEyCess(b2bSummary.getCess());
					summaryResp.setEyCount(b2bSummary.getRecords());
					// GSTN Resp

					summaryResp.setGstnInvoiceValue(
							b2bSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							b2bSummary.getGstnTaxableValue());
					summaryResp.setGstnTaxPayble(b2bSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(b2bSummary.getGstnIgst());
					summaryResp.setGstnCgst(b2bSummary.getGstnCgst());
					summaryResp.setGstnSgst(b2bSummary.getGstnSgst());
					summaryResp.setGstnCess(b2bSummary.getGstnCess());
					summaryResp.setGstnCount(b2bSummary.getGstnCount());
					// Memo
					summaryResp.setMemoInvoiceValue(b2bSummary.getInvValue());
					summaryResp
							.setMemoTaxableValue(b2bSummary.getTaxableValue());
					summaryResp
							.setMemoTaxPayble(b2bSummary.getMemoTaxPayable());
					summaryResp.setMemoIgst(b2bSummary.getMemoIgst());
					summaryResp.setMemoCgst(b2bSummary.getMemoCgst());
					summaryResp.setMemoSgst(b2bSummary.getMemoSgst());
					summaryResp.setMemoCess(b2bSummary.getMemoCess());
					summaryResp.setMemoCount(b2bSummary.getRecords());

					// Diff
					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					b2bSummaryRespList.add(summaryResp);
				});
			}
		}

		JsonElement b2bRespBody = annexure1B2BRespHandler
				.handleB2BResp(b2bSummaryRespList);

		List<Annexure1SummaryResp1Dto> exptSummaryRespList = new ArrayList<>();

		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto expt = an.getExpt();
			List<Annexure1SummarySectionDto> exptEySummary = expt
					.getEySummary();

			if (exptEySummary != null) {
				exptEySummary.forEach(exptSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();

					// EY Resp
					summaryResp.setTableSection(exptSummary.getTableSection());
					summaryResp.setDocType(exptSummary.getDocType());
					summaryResp.setEyInvoiceValue(exptSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(exptSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(exptSummary.getTaxPayble());
					summaryResp.setEyIgst(exptSummary.getIgst());
					summaryResp.setEyCgst(exptSummary.getCgst());
					summaryResp.setEySgst(exptSummary.getSgst());
					summaryResp.setEyCess(exptSummary.getCess());
					summaryResp.setEyCount(exptSummary.getRecords());
					// GSTN Resp
					summaryResp.setGstnInvoiceValue(
							exptSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							exptSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(exptSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(exptSummary.getGstnIgst());
					summaryResp.setGstnCgst(exptSummary.getGstnCgst());
					summaryResp.setGstnSgst(exptSummary.getGstnSgst());
					summaryResp.setGstnCess(exptSummary.getGstnCess());
					summaryResp.setGstnCount(exptSummary.getGstnCount());
					// Memo
					summaryResp.setMemoInvoiceValue(exptSummary.getInvValue());
					summaryResp
							.setMemoTaxableValue(exptSummary.getTaxableValue());
					summaryResp
							.setMemoTaxPayble(exptSummary.getMemoTaxPayable());
					summaryResp.setMemoIgst(exptSummary.getMemoIgst());
					summaryResp.setMemoCgst(exptSummary.getMemoCgst());
					summaryResp.setMemoSgst(exptSummary.getMemoSgst());
					summaryResp.setMemoCess(exptSummary.getMemoCess());
					summaryResp.setMemoCount(exptSummary.getRecords());

					// diff Cal
					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					exptSummaryRespList.add(summaryResp);
				});

			}
		}

		JsonElement exptRespBody = annexure1ExptRespHandler
				.handleExptResp(exptSummaryRespList);

		List<Annexure1SummaryResp1Dto> expwtSummaryRespList = new ArrayList<>();

		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto expt = an.getExpwt();
			List<Annexure1SummarySectionDto> expwtEySummary = expt
					.getEySummary();

			if (expwtEySummary != null) {
				expwtEySummary.forEach(expwtSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();

					// EY Resp
					summaryResp.setTableSection(expwtSummary.getTableSection());
					summaryResp.setDocType(expwtSummary.getDocType());
					summaryResp.setEyInvoiceValue(expwtSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(expwtSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(expwtSummary.getTaxPayble());
					summaryResp.setEyIgst(expwtSummary.getIgst());
					summaryResp.setEyCgst(expwtSummary.getCgst());
					summaryResp.setEySgst(expwtSummary.getSgst());
					summaryResp.setEyCess(expwtSummary.getCess());
					summaryResp.setEyCount(expwtSummary.getRecords());
					// GSTN Resp
					summaryResp.setGstnInvoiceValue(
							expwtSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							expwtSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(expwtSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(expwtSummary.getGstnIgst());
					summaryResp.setGstnCgst(expwtSummary.getGstnCgst());
					summaryResp.setGstnSgst(expwtSummary.getGstnSgst());
					summaryResp.setGstnCess(expwtSummary.getGstnCess());
					summaryResp.setGstnCount(expwtSummary.getGstnCount());

					// Memo
					summaryResp.setMemoInvoiceValue(expwtSummary.getInvValue());
					summaryResp.setMemoTaxableValue(
							expwtSummary.getTaxableValue());
					summaryResp
							.setMemoTaxPayble(expwtSummary.getMemoTaxPayable());
					summaryResp.setMemoIgst(expwtSummary.getMemoIgst());
					summaryResp.setMemoCgst(expwtSummary.getMemoCgst());
					summaryResp.setMemoSgst(expwtSummary.getMemoSgst());
					summaryResp.setMemoCess(expwtSummary.getMemoCess());
					summaryResp.setMemoCount(expwtSummary.getRecords());

					// Diff Cal

					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					expwtSummaryRespList.add(summaryResp);
				});

			}
		}
		JsonElement expwtRespBody = annexure1ExpwtRespHandler
				.handleExpwtResp(expwtSummaryRespList);

		List<Annexure1SummaryResp1Dto> seztSummaryRespList = new ArrayList<>();

		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto sezt = an.getSezt();

			List<Annexure1SummarySectionDto> seztEySummary = sezt
					.getEySummary();

			if (seztEySummary != null) {
				seztEySummary.forEach(seztSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();

					// EY Resp
					summaryResp.setTableSection(seztSummary.getTableSection());
					summaryResp.setDocType(seztSummary.getDocType());
					summaryResp.setEyInvoiceValue(seztSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(seztSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(seztSummary.getTaxPayble());
					summaryResp.setEyIgst(seztSummary.getIgst());
					summaryResp.setEyCgst(seztSummary.getCgst());
					summaryResp.setEySgst(seztSummary.getSgst());
					summaryResp.setEyCess(seztSummary.getCess());
					summaryResp.setEyCount(seztSummary.getRecords());
					// GSTN Resp
					summaryResp.setGstnInvoiceValue(
							seztSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							seztSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(seztSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(seztSummary.getGstnIgst());
					summaryResp.setGstnCgst(seztSummary.getGstnCgst());
					summaryResp.setGstnSgst(seztSummary.getGstnSgst());
					summaryResp.setGstnCess(seztSummary.getGstnCess());
					summaryResp.setGstnCount(seztSummary.getGstnCount());

					// Memo
					summaryResp.setMemoInvoiceValue(seztSummary.getInvValue());
					summaryResp
							.setMemoTaxableValue(seztSummary.getTaxableValue());
					summaryResp
							.setMemoTaxPayble(seztSummary.getMemoTaxPayable());
					summaryResp.setMemoIgst(seztSummary.getMemoIgst());
					summaryResp.setMemoCgst(seztSummary.getMemoCgst());
					summaryResp.setMemoSgst(seztSummary.getMemoSgst());
					summaryResp.setMemoCess(seztSummary.getMemoCess());
					summaryResp.setMemoCount(seztSummary.getRecords());

					// Diff Cal
					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					seztSummaryRespList.add(summaryResp);
				});
			}
		}

		JsonElement seztRespBody = annexure1SeztRespHandler
				.handleSeztResp(seztSummaryRespList);

		List<Annexure1SummaryResp1Dto> sezwtSummaryRespList = new ArrayList<>();
		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto sezwt = an.getSezwt();

			List<Annexure1SummarySectionDto> sezwtEySummary = sezwt
					.getEySummary();

			if (sezwtEySummary != null) {
				sezwtEySummary.forEach(sezwtSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();

					// EY Resp
					summaryResp.setTableSection(sezwtSummary.getTableSection());
					summaryResp.setDocType(sezwtSummary.getDocType());
					summaryResp.setEyInvoiceValue(sezwtSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(sezwtSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(sezwtSummary.getTaxPayble());
					summaryResp.setEyIgst(sezwtSummary.getIgst());
					summaryResp.setEyCgst(sezwtSummary.getCgst());
					summaryResp.setEySgst(sezwtSummary.getSgst());
					summaryResp.setEyCess(sezwtSummary.getCess());
					summaryResp.setEyCount(sezwtSummary.getRecords());
					// GSTN Resp
					summaryResp.setGstnInvoiceValue(
							sezwtSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							sezwtSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(sezwtSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(sezwtSummary.getGstnIgst());
					summaryResp.setGstnCgst(sezwtSummary.getGstnCgst());
					summaryResp.setGstnSgst(sezwtSummary.getGstnSgst());
					summaryResp.setGstnCess(sezwtSummary.getGstnCess());
					summaryResp.setGstnCount(sezwtSummary.getGstnCount());
					// Memo
					summaryResp.setMemoInvoiceValue(sezwtSummary.getInvValue());
					summaryResp.setMemoTaxableValue(
							sezwtSummary.getTaxableValue());
					summaryResp
							.setMemoTaxPayble(sezwtSummary.getMemoTaxPayable());
					summaryResp.setMemoIgst(sezwtSummary.getMemoIgst());
					summaryResp.setMemoCgst(sezwtSummary.getMemoCgst());
					summaryResp.setMemoSgst(sezwtSummary.getMemoSgst());
					summaryResp.setMemoCess(sezwtSummary.getMemoCess());
					summaryResp.setMemoCount(sezwtSummary.getRecords());

					// Diff Cal
					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					sezwtSummaryRespList.add(summaryResp);
				});
			}

		}
		JsonElement sezwtRespBody = annexure1SezwtRespHandler
				.handleSezwtResp(sezwtSummaryRespList);

		List<Annexure1SummaryResp1Dto> deemedSummaryRespList = new ArrayList<>();
		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto deemedExp = an.getDeemedExp();

			List<Annexure1SummarySectionDto> deemedExpEySummary = deemedExp
					.getEySummary();

			if (deemedExpEySummary != null) {
				deemedExpEySummary.forEach(deemedExpSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();

					// EY Resp
					summaryResp.setTableSection(
							deemedExpSummary.getTableSection());
					summaryResp.setDocType(deemedExpSummary.getDocType());
					summaryResp
							.setEyInvoiceValue(deemedExpSummary.getInvValue());
					summaryResp.setEyTaxableValue(
							deemedExpSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(deemedExpSummary.getTaxPayble());
					summaryResp.setEyIgst(deemedExpSummary.getIgst());
					summaryResp.setEyCgst(deemedExpSummary.getCgst());
					summaryResp.setEySgst(deemedExpSummary.getSgst());
					summaryResp.setEyCess(deemedExpSummary.getCess());
					summaryResp.setEyCount(deemedExpSummary.getRecords());
					// GSTN Resp

					summaryResp.setGstnInvoiceValue(
							deemedExpSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							deemedExpSummary.getGstnTaxableValue());
					summaryResp.setGstnTaxPayble(
							deemedExpSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(deemedExpSummary.getGstnIgst());
					summaryResp.setGstnCgst(deemedExpSummary.getGstnCgst());
					summaryResp.setGstnSgst(deemedExpSummary.getGstnSgst());
					summaryResp.setGstnCess(deemedExpSummary.getGstnCess());
					summaryResp.setGstnCount(deemedExpSummary.getGstnCount());

					// Memo
					summaryResp.setMemoInvoiceValue(
							deemedExpSummary.getInvValue());
					summaryResp.setMemoTaxableValue(
							deemedExpSummary.getTaxableValue());
					summaryResp.setMemoTaxPayble(
							deemedExpSummary.getMemoTaxPayable());
					summaryResp.setMemoIgst(deemedExpSummary.getMemoIgst());
					summaryResp.setMemoCgst(deemedExpSummary.getMemoCgst());
					summaryResp.setMemoSgst(deemedExpSummary.getMemoSgst());
					summaryResp.setMemoCess(deemedExpSummary.getMemoCess());
					summaryResp.setMemoCount(deemedExpSummary.getRecords());

					// Diff Cal

					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					deemedSummaryRespList.add(summaryResp);
				});
			}
		}
		JsonElement deemExptRespBody = annexure1DeemExpRespHandler
				.handleDExpResp(deemedSummaryRespList);

		Gson gson = GsonUtil.newSAPGsonInstance();

		Map<String, JsonElement> summaryCombinedMap = new HashMap<>();

		summaryCombinedMap.put("b2c", b2cRespBody);

		summaryCombinedMap.put("b2b", b2bRespBody);

		summaryCombinedMap.put("expt", exptRespBody);

		summaryCombinedMap.put("expwt", expwtRespBody);

		summaryCombinedMap.put("sezt", seztRespBody);

		summaryCombinedMap.put("sezwt", sezwtRespBody);

		summaryCombinedMap.put("deemExpt", deemExptRespBody);

		JsonElement summaryRespbody = gson.toJsonTree(summaryCombinedMap);

		return summaryRespbody;
	}

	// For Inward Data

	@SuppressWarnings("unchecked")
	public JsonElement handleInwardAnnexure1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		LOGGER.debug("INWARD Summary Execution BEGIN ");
		SearchResult<Annexure1SummaryDto> summaryResult = inService
				.<Annexure1SummaryDto>find(annexure1SummaryRequest, null,
						Annexure1SummaryDto.class);
		LOGGER.debug("INWARD Summary Execution END ");
		List<? extends Annexure1SummaryDto> list = summaryResult.getResult();
		List<Annexure1SummaryResp1Dto> impgSummaryRespList = new ArrayList<>();
		List<Annexure1SummaryResp1Dto> impgSezSummaryRespList = new ArrayList<>();
		List<Annexure1SummaryResp1Dto> impsSummaryRespList = new ArrayList<>();
		List<Annexure1SummaryResp1Dto> revChSummaryRespList = new ArrayList<>();

		for (Annexure1SummaryDto an : list) {
			Annexure1BasicSummaryDto impg = an.getImpg();
			List<Annexure1SummarySectionDto> impgEySummary = impg
					.getEySummary();
			if (impgEySummary != null) {
				impgEySummary.forEach(impsSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();
					// EY Resp
					summaryResp.setTableSection(impsSummary.getTableSection());
					summaryResp.setDocType(impsSummary.getDocType());
					summaryResp.setEyInvoiceValue(impsSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(impsSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(impsSummary.getTaxPayble());
					summaryResp.setEyIgst(impsSummary.getIgst());
					summaryResp.setEyCgst(impsSummary.getCgst());
					summaryResp.setEySgst(impsSummary.getSgst());
					summaryResp.setEyCess(impsSummary.getCess());
					summaryResp.setEyCount(impsSummary.getRecords());
					// GSTN Resp
					summaryResp.setGstnInvoiceValue(
							impsSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							impsSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(impsSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(impsSummary.getGstnIgst());
					summaryResp.setGstnCgst(impsSummary.getGstnCgst());
					summaryResp.setGstnSgst(impsSummary.getGstnSgst());
					summaryResp.setGstnCess(impsSummary.getGstnCess());
					summaryResp.setGstnCount(impsSummary.getGstnCount());

					// Memo
					summaryResp.setMemoInvoiceValue(impsSummary.getInvValue());
					summaryResp
							.setMemoTaxableValue(impsSummary.getTaxableValue());
					summaryResp.setMemoTaxPayble(impsSummary.getTaxPayble());
					summaryResp.setMemoIgst(impsSummary.getIgst());
					summaryResp.setMemoCgst(impsSummary.getCgst());
					summaryResp.setMemoSgst(impsSummary.getSgst());
					summaryResp.setMemoCess(impsSummary.getCess());
					summaryResp.setMemoCount(impsSummary.getRecords());

					// Diff Cal

					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					impgSummaryRespList.add(summaryResp);
				});
			}
		}

		for (Annexure1SummaryDto impsz : list) {
			Annexure1BasicSummaryDto impgSez = impsz.getImpgSez();
			List<Annexure1SummarySectionDto> impgSezEySummary = impgSez
					.getEySummary();
			if (impgSezEySummary != null) {
				impgSezEySummary.forEach(impszSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();
					// EY Resp
					summaryResp.setTableSection(impszSummary.getTableSection());
					summaryResp.setDocType(impszSummary.getDocType());
					summaryResp.setEyInvoiceValue(impszSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(impszSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(impszSummary.getTaxPayble());
					summaryResp.setEyIgst(impszSummary.getIgst());
					summaryResp.setEyCgst(impszSummary.getCgst());
					summaryResp.setEySgst(impszSummary.getSgst());
					summaryResp.setEyCess(impszSummary.getCess());
					summaryResp.setEyCount(impszSummary.getRecords());
					// GSTN Resp
					summaryResp.setGstnInvoiceValue(
							impszSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							impszSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(impszSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(impszSummary.getGstnIgst());
					summaryResp.setGstnCgst(impszSummary.getGstnCgst());
					summaryResp.setGstnSgst(impszSummary.getGstnSgst());
					summaryResp.setGstnCess(impszSummary.getGstnCess());
					summaryResp.setGstnCount(impszSummary.getGstnCount());

					// Memo
					summaryResp.setMemoInvoiceValue(impszSummary.getInvValue());
					summaryResp.setMemoTaxableValue(
							impszSummary.getTaxableValue());
					summaryResp.setMemoTaxPayble(impszSummary.getTaxPayble());
					summaryResp.setMemoIgst(impszSummary.getIgst());
					summaryResp.setMemoCgst(impszSummary.getCgst());
					summaryResp.setMemoSgst(impszSummary.getSgst());
					summaryResp.setMemoCess(impszSummary.getCess());
					summaryResp.setMemoCount(impszSummary.getRecords());

					// Diff Cal

					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					impgSezSummaryRespList.add(summaryResp);
				});
			}
		}
		for (Annexure1SummaryDto in : list) {
			Annexure1BasicSummaryDto imps = in.getImps();
			List<Annexure1SummarySectionDto> impsEySummary = imps
					.getEySummary();
			if (impsEySummary != null) {
				impsEySummary.forEach(impssSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();
					// EY Resp
					summaryResp.setTableSection(impssSummary.getTableSection());
					summaryResp.setDocType(impssSummary.getDocType());
					summaryResp.setEyInvoiceValue(impssSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(impssSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(impssSummary.getTaxPayble());
					summaryResp.setEyIgst(impssSummary.getIgst());
					summaryResp.setEyCgst(impssSummary.getCgst());
					summaryResp.setEySgst(impssSummary.getSgst());
					summaryResp.setEyCess(impssSummary.getCess());
					summaryResp.setEyCount(impssSummary.getRecords());
					// GSTN Resp
					summaryResp.setGstnInvoiceValue(
							impssSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							impssSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(impssSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(impssSummary.getGstnIgst());
					summaryResp.setGstnCgst(impssSummary.getGstnCgst());
					summaryResp.setGstnSgst(impssSummary.getGstnSgst());
					summaryResp.setGstnCess(impssSummary.getGstnCess());
					summaryResp.setGstnCount(impssSummary.getGstnCount());
					// Memo
					summaryResp.setMemoInvoiceValue(impssSummary.getInvValue());
					summaryResp.setMemoTaxableValue(
							impssSummary.getTaxableValue());
					summaryResp.setMemoTaxPayble(impssSummary.getTaxPayble());
					summaryResp.setMemoIgst(impssSummary.getIgst());
					summaryResp.setMemoCgst(impssSummary.getCgst());
					summaryResp.setMemoSgst(impssSummary.getSgst());
					summaryResp.setMemoCess(impssSummary.getCess());
					summaryResp.setMemoCount(impssSummary.getRecords());

					// Diff Cal
					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					impsSummaryRespList.add(summaryResp);
				});
			}
		}
		for (Annexure1SummaryDto revCh : list) {
			Annexure1BasicSummaryDto revH = revCh.getRev();
			List<Annexure1SummarySectionDto> revchEySummary = revH
					.getEySummary();
			if (revchEySummary != null) {
				revchEySummary.forEach(revChSummary -> {
					Annexure1SummaryResp1Dto summaryResp = new Annexure1SummaryResp1Dto();
					// EY Resp
					summaryResp.setTableSection(revChSummary.getTableSection());
					summaryResp.setDocType(revChSummary.getDocType());
					summaryResp.setEyInvoiceValue(revChSummary.getInvValue());
					summaryResp
							.setEyTaxableValue(revChSummary.getTaxableValue());
					summaryResp.setEyTaxPayble(revChSummary.getTaxPayble());
					summaryResp.setEyIgst(revChSummary.getIgst());
					summaryResp.setEyCgst(revChSummary.getCgst());
					summaryResp.setEySgst(revChSummary.getSgst());
					summaryResp.setEyCess(revChSummary.getCess());
					summaryResp.setEyCount(revChSummary.getRecords());
					// GSTN Resp
					// Annexure1TypeSummaryDto gstnResp = new
					// Annexure1TypeSummaryDto();
					summaryResp.setGstnInvoiceValue(
							revChSummary.getGstnInvoiceValue());
					summaryResp.setGstnTaxableValue(
							revChSummary.getGstnTaxableValue());
					summaryResp
							.setGstnTaxPayble(revChSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(revChSummary.getGstnIgst());
					summaryResp.setGstnCgst(revChSummary.getGstnCgst());
					summaryResp.setGstnSgst(revChSummary.getGstnSgst());
					summaryResp.setGstnCess(revChSummary.getGstnCess());
					summaryResp.setGstnCount(revChSummary.getGstnCount());

					// Memo
					summaryResp.setMemoInvoiceValue(revChSummary.getInvValue());
					summaryResp.setMemoTaxableValue(
							revChSummary.getTaxableValue());
					summaryResp.setMemoTaxPayble(revChSummary.getTaxPayble());
					summaryResp.setMemoIgst(revChSummary.getIgst());
					summaryResp.setMemoCgst(revChSummary.getCgst());
					summaryResp.setMemoSgst(revChSummary.getSgst());
					summaryResp.setMemoCess(revChSummary.getCess());
					summaryResp.setMemoCount(revChSummary.getRecords());

					// Diff Cal

					BigDecimal diffInv = subMethod(
							summaryResp.getMemoInvoiceValue(),
							summaryResp.getGstnInvoiceValue());
					BigDecimal diffTaxab = subMethod(
							summaryResp.getMemoTaxableValue(),
							summaryResp.getGstnTaxableValue());
					BigDecimal diffTaxp = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());
					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffInvoiceValue(diffInv);
					summaryResp.setDiffTaxableValue(diffTaxab);
					summaryResp.setDiffTaxPayble(diffTaxp);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					revChSummaryRespList.add(summaryResp);
				});
			}
		}

		JsonElement impgRespBody = annexure1ImpgRespHandler
				.handleImpgResp(impgSummaryRespList);

		JsonElement impgSezRespBody = annexure1ImpgSezRespHandler
				.handleImpgSezResp(impgSezSummaryRespList);

		JsonElement impsiRespBody = annexure1ImpsRespHandler
				.handleImpsResp(impsSummaryRespList);

		JsonElement revChRespBody = annexure1RevRespHandler
				.handleRevResp(revChSummaryRespList);

		Gson gson = GsonUtil.newSAPGsonInstance();

		Map<String, JsonElement> summaryCombinedMap = new HashMap<>();

		summaryCombinedMap.put("impg", impgRespBody);
		summaryCombinedMap.put("impgSez", impgSezRespBody);
		summaryCombinedMap.put("imps", impsiRespBody);
		summaryCombinedMap.put("rev", revChRespBody);
		JsonElement summaryRespbody = gson.toJsonTree(summaryCombinedMap);

		return summaryRespbody;
	}

	// Table 4 Ecomm
	public JsonElement handleEcommAnnexure1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		LOGGER.debug("ECOMM Execution BEGIN ..");

		SearchResult<Annexure1SummaryDto> summaryResult = fetchEcomm
				.<Annexure1SummaryDto>find(annexure1SummaryRequest, null,
						Annexure1SummaryDto.class);

		// Gstn Data From Get Summary

		/*Annexure1SummarySectionEcomDto addGstnEcomData = gstnCalculation
				.addGstnEcomData(gstnResult);*/
		List<? extends Annexure1SummaryDto> list = summaryResult.getResult();
		List<Annexure1SummaryEcomResp1Dto> ecomm4 = new ArrayList<>();

		for (Annexure1SummaryDto an : list) {
			Annexure1BasicEcomSummaryDto table4 = an.getTable4();
			List<Annexure1SummarySectionEcomDto> eySummary = table4
					.getEySummary();

			Annexure1SummaryEcomResp1Dto summaryResp = new Annexure1SummaryEcomResp1Dto();
			if (eySummary != null) {
				eySummary.forEach(ecommSummary -> {

					// EY Resp
					summaryResp.setTableSection(ecommSummary.getTableSection());
					// summaryResp.setDocType(impsSummary.getDocType());
					summaryResp.setEySupplyMade(ecommSummary.getSupplyMade());
					summaryResp
							.setEySupplyReturn(ecommSummary.getSupplyReturn());
					summaryResp.setEyNetSupply(ecommSummary.getNetSupply());
					summaryResp.setEyTaxPayble(ecommSummary.getTaxPayble());
					summaryResp.setEyIgst(ecommSummary.getIgst());
					summaryResp.setEyCgst(ecommSummary.getCgst());
					summaryResp.setEySgst(ecommSummary.getSgst());
					summaryResp.setEyCess(ecommSummary.getCess());
					summaryResp.setEyCount(ecommSummary.getRecords());

					// Memo
					summaryResp.setMemoSupplyMade(ecommSummary.getSupplyMade());
					summaryResp.setMemoSupplyReturn(
							ecommSummary.getSupplyReturn());
					summaryResp.setMemoNetSupply(ecommSummary.getNetSupply());
					summaryResp.setMemoTaxPayble(ecommSummary.getTaxPayble());
					summaryResp.setMemoIgst(ecommSummary.getIgst());
					summaryResp.setMemoCgst(ecommSummary.getCgst());
					summaryResp.setMemoSgst(ecommSummary.getSgst());
					summaryResp.setMemoCess(ecommSummary.getCess());
					summaryResp.setMemoCount(ecommSummary.getRecords());

					// GSTN Resp
					summaryResp.setGstnSupplyMade(ecommSummary.getGstnSupplyMade());
					summaryResp.setGstnSupplyReturn(
							ecommSummary.getGstnSupplyReturn());
					summaryResp.setGstnNetSupply(ecommSummary.getGstnNetSupply());
					summaryResp.setGstnTaxPayble(ecommSummary.getGstnTaxPayble());
					summaryResp.setGstnIgst(ecommSummary.getGstnIgst());
					summaryResp.setGstnCgst(ecommSummary.getGstnCgst());
					summaryResp.setGstnSgst(ecommSummary.getGstnSgst());
					summaryResp.setGstnCess(ecommSummary.getGstnCess());
					summaryResp.setGstnCount(ecommSummary.getGstnCount());

					// Diff Cal

					BigDecimal diffSupplyMade = subMethod(
							summaryResp.getMemoSupplyMade(),
							summaryResp.getGstnSupplyMade());
					BigDecimal diffSupplyReturn = subMethod(
							summaryResp.getMemoSupplyReturn(),
							summaryResp.getGstnSupplyReturn());
					BigDecimal diffNetSupply = subMethod(
							summaryResp.getMemoNetSupply(),
							summaryResp.getGstnNetSupply());
					BigDecimal diffTaxpay = subMethod(
							summaryResp.getMemoTaxPayble(),
							summaryResp.getGstnTaxPayble());
					BigDecimal diffIgst = subMethod(summaryResp.getMemoIgst(),
							summaryResp.getGstnIgst());
					BigDecimal diffCgst = subMethod(summaryResp.getMemoCgst(),
							summaryResp.getGstnCgst());
					BigDecimal diffSgst = subMethod(summaryResp.getMemoSgst(),
							summaryResp.getGstnSgst());
					BigDecimal diffCess = subMethod(summaryResp.getMemoCess(),
							summaryResp.getGstnCess());

					Integer memoCount = (summaryResp.getMemoCount() != null)
							? summaryResp.getMemoCount() : 0;
					Integer gstnCount = (summaryResp.getGstnCount() != null)
							? summaryResp.getGstnCount() : 0;

					summaryResp.setDiffSupplyMade(diffSupplyMade);

					summaryResp.setDiffSupplyReturn(diffSupplyReturn);
					summaryResp.setDiffNetSupply(diffNetSupply);
					summaryResp.setDiffTaxPayble(diffTaxpay);
					summaryResp.setDiffIgst(diffIgst);
					summaryResp.setDiffCgst(diffCgst);
					summaryResp.setDiffSgst(diffSgst);
					summaryResp.setDiffCess(diffCess);
					summaryResp.setDiffCount(memoCount - gstnCount);

					ecomm4.add(summaryResp);
				});
			}
		}

		JsonElement ecomRespBody = annexure1EcomRespHandler
				.handleEcomResp(ecomm4);

		Gson gson = GsonUtil.newSAPGsonInstance();

		Map<String, JsonElement> summaryCombinedMap = new HashMap<>();

		summaryCombinedMap.put("table4", ecomRespBody);
		JsonElement summaryRespbody = gson.toJsonTree(summaryCombinedMap);

		return summaryRespbody;
	}

	private BigDecimal subMethod(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.subtract(b1));

	}

}
