package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr2PRBasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr2PRCompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr2PRSimpleDocSummarySearchService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr2PRSummaryReqRespHandler")
public class Gstr2PRSummaryReqRespHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2PRSummaryReqRespHandler.class);

	@Autowired
	@Qualifier("Gstr2PRSimpleDocSummarySearchService")
	Gstr2PRSimpleDocSummarySearchService searchService;

	@Autowired
	@Qualifier("Gstr2PRB2BStructure")
	Gstr2PRB2BStructure b2bStructure;

	public JsonElement handleGstr1ReqAndResp(
			Gstr2ProcessedRecordsReqDto gstr6SummaryRequest) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		SearchResult<Gstr2PRCompleteSummaryDto> summaryResult = searchService
				.<Gstr2PRCompleteSummaryDto>find(gstr6SummaryRequest, null,
						Gstr2PRCompleteSummaryDto.class);
		List<? extends Gstr2PRCompleteSummaryDto> result = summaryResult
				.getResult();

		// List<Gstr2PRSummarySectionDto> prSummary = new ArrayList<>();
		Map<String, JsonElement> combinedMap = new HashMap<>();

		List<Gstr2PRSummarySectionDto> b2bSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> b2baSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> cdnSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> cdnaSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> isdSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> isdaSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> impSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> impaSummaryList = new ArrayList<>();
		// List<Gstr2PRSummarySectionDto> impgSummaryList = new ArrayList<>();
		// List<Gstr2PRSummarySectionDto> impgaSummaryList = new ArrayList<>();

		// List<Gstr2PRSummarySectionDto> impgsSummaryList = new ArrayList<>();
		// List<Gstr2PRSummarySectionDto> impgsaSummaryList = new ArrayList<>();

		List<Gstr2PRSummarySectionDto> rcurdSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> rcurdaSummaryList = new ArrayList<>();
		List<Gstr2PRSummarySectionDto> rcmadvSummaryList = new ArrayList<>();


		for (Gstr2PRCompleteSummaryDto dto : result) {
			Gstr2PRSummarySectionDto summaryResp = new Gstr2PRSummarySectionDto();

			Gstr2PRBasicSectionSummaryDto b2b = dto.getB2b();
			Gstr2PRBasicSectionSummaryDto b2ba = dto.getB2ba();
			Gstr2PRBasicSectionSummaryDto cdn = dto.getCdn();
			Gstr2PRBasicSectionSummaryDto cdna = dto.getCdna();
			Gstr2PRBasicSectionSummaryDto imp = dto.getImp();
			Gstr2PRBasicSectionSummaryDto impa = dto.getImpa();
			/*
			 * Gstr2PRBasicSectionSummaryDto impgs = dto.getImpgs();
			 * Gstr2PRBasicSectionSummaryDto impgsa = dto.getImpgsa();
			 * Gstr2PRBasicSectionSummaryDto imps = dto.getImps();
			 * Gstr2PRBasicSectionSummaryDto impsa = dto.getImpsa();
			 */
			Gstr2PRBasicSectionSummaryDto isd = dto.getIsd();
			Gstr2PRBasicSectionSummaryDto isda = dto.getIsda();
			Gstr2PRBasicSectionSummaryDto rcurd = dto.getRcurd();
			Gstr2PRBasicSectionSummaryDto rcurda = dto.getRcurda();
			Gstr2PRBasicSectionSummaryDto rcmadv = dto.getRcmadv();

			List<Gstr2PRSummarySectionDto> eySummary = b2b.getEySummary();

			if (eySummary != null) {

				eySummary.forEach(detailEySummary -> {
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						summaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						summaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						summaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						summaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						summaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						summaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						summaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						summaryResp.setCrEligibleTotal(CheckForNegativeValue(
								detailEySummary.getCrEligibleTotal()));
						summaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						summaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						summaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						summaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						b2bSummaryList.add(summaryResp);
					} else if ("INV"
							.equalsIgnoreCase(detailEySummary.getDocType())) {
						summaryResp.setTable(detailEySummary.getTable());
						summaryResp.setCount(detailEySummary.getCount());
						summaryResp.setDocType(detailEySummary.getDocType());
						summaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						summaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						summaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						summaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						summaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						summaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						summaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						summaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						summaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						summaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						summaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						summaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						b2bSummaryList.add(summaryResp);
					}
				});
			}

			Gstr2PRSummarySectionDto b2baSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> b2baEySummary = b2ba.getEySummary();

			if (b2baEySummary != null) {

				b2baEySummary.forEach(detailEySummary -> {
					String docTypeEy = detailEySummary.getDocType();
					if (docTypeEy != null && (docTypeEy.equalsIgnoreCase("CR")
							|| docTypeEy.equalsIgnoreCase("C")
							|| docTypeEy.equalsIgnoreCase("RCR"))) {

						b2baSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						b2baSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						b2baSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						b2baSummaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						b2baSummaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						b2baSummaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						b2baSummaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						b2baSummaryResp
								.setCrEligibleTotal(CheckForNegativeValue(
										detailEySummary.getCrEligibleTotal()));
						b2baSummaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						b2baSummaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						b2baSummaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						b2baSummaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						b2baSummaryList.add(b2baSummaryResp);
					} else if ("RNV"
							.equalsIgnoreCase(detailEySummary.getDocType())) {
						b2baSummaryResp.setTable(detailEySummary.getTable());
						b2baSummaryResp.setCount(detailEySummary.getCount());
						b2baSummaryResp
								.setDocType(detailEySummary.getDocType());
						b2baSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						b2baSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						b2baSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						b2baSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						b2baSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						b2baSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						b2baSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						b2baSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						b2baSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						b2baSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						b2baSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						b2baSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						b2baSummaryList.add(b2baSummaryResp);
					}
				});
			}

			Gstr2PRSummarySectionDto cdnSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> cdnEySummary = cdn.getEySummary();

			if (cdnEySummary != null) {

				cdnEySummary.forEach(detailEySummary -> {
					cdnSummaryResp.setTable(detailEySummary.getTable());
					cdnSummaryResp.setCount(detailEySummary.getCount());
					String docTypeEyDetail = detailEySummary.getDocType();
					cdnSummaryResp.setDocType(detailEySummary.getDocType());
					if (docTypeEyDetail != null && (docTypeEyDetail
							.equalsIgnoreCase("CR")
							|| docTypeEyDetail.equalsIgnoreCase("C")
							|| docTypeEyDetail.equalsIgnoreCase("RCR"))) {
						cdnSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						cdnSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						cdnSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						cdnSummaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						cdnSummaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						cdnSummaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						cdnSummaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						cdnSummaryResp.setCrEligibleTotal(CheckForNegativeValue(
								detailEySummary.getCrEligibleTotal()));
						cdnSummaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						cdnSummaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						cdnSummaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						cdnSummaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						cdnSummaryList.add(cdnSummaryResp);
					} else {
						cdnSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						cdnSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						cdnSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						cdnSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						cdnSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						cdnSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						cdnSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						cdnSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						cdnSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						cdnSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						cdnSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						cdnSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						cdnSummaryList.add(cdnSummaryResp);
					}

				});
			}
			Gstr2PRSummarySectionDto cdnaSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> cdnaEySummary = cdna.getEySummary();

			if (cdnaEySummary != null) {

				cdnaEySummary.forEach(detailEySummary -> {
					cdnaSummaryResp.setTable(detailEySummary.getTable());
					cdnaSummaryResp.setCount(detailEySummary.getCount());
					cdnaSummaryResp.setDocType(detailEySummary.getDocType());
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						cdnaSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						cdnaSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						cdnaSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						cdnaSummaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						cdnaSummaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						cdnaSummaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						cdnaSummaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						cdnaSummaryResp
								.setCrEligibleTotal(CheckForNegativeValue(
										detailEySummary.getCrEligibleTotal()));
						cdnaSummaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						cdnaSummaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						cdnaSummaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						cdnaSummaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						cdnaSummaryList.add(cdnaSummaryResp);
					} else {
						cdnaSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						cdnaSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						cdnaSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						cdnaSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						cdnaSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						cdnaSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						cdnaSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						cdnaSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						cdnaSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						cdnaSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						cdnaSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						cdnaSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						cdnaSummaryList.add(cdnaSummaryResp);
					}
				});
			}
			Gstr2PRSummarySectionDto isdSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> isdEySummary = isd.getEySummary();

			if (isdEySummary != null) {

				isdEySummary.forEach(detailEySummary -> {
					isdSummaryResp.setTable(detailEySummary.getTable());
					isdSummaryResp.setCount(detailEySummary.getCount());
					isdSummaryResp.setDocType(detailEySummary.getDocType());
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						isdSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						isdSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						isdSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						isdSummaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						isdSummaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						isdSummaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						isdSummaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						isdSummaryResp.setCrEligibleTotal(CheckForNegativeValue(
								detailEySummary.getCrEligibleTotal()));
						isdSummaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						isdSummaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						isdSummaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						isdSummaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						isdSummaryList.add(isdSummaryResp);
					} else {
						isdSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						isdSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						isdSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						isdSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						isdSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						isdSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						isdSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						isdSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						isdSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						isdSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						isdSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						isdSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						isdSummaryList.add(isdSummaryResp);
					}
				});
			}
			Gstr2PRSummarySectionDto isdaSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> isdaEySummary = isda.getEySummary();

			if (isdaEySummary != null) {

				isdaEySummary.forEach(detailEySummary -> {
					isdaSummaryResp.setTable(detailEySummary.getTable());
					isdaSummaryResp.setCount(detailEySummary.getCount());
					isdaSummaryResp.setDocType(detailEySummary.getDocType());
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						isdaSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						isdaSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						isdaSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						isdaSummaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						isdaSummaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						isdaSummaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						isdaSummaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						isdaSummaryResp
								.setCrEligibleTotal(CheckForNegativeValue(
										detailEySummary.getCrEligibleTotal()));
						isdaSummaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						isdaSummaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						isdaSummaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						isdaSummaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						isdaSummaryList.add(isdaSummaryResp);
					} else {
						isdaSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						isdaSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						isdaSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						isdaSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						isdaSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						isdaSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						isdaSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						isdaSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						isdaSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						isdaSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						isdaSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						isdaSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						isdaSummaryList.add(isdaSummaryResp);
					}
				});
			}
			Gstr2PRSummarySectionDto impsSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> impsEySummary = imp.getEySummary();

			if (impsEySummary != null) {

				impsEySummary.forEach(detailEySummary -> {
					impsSummaryResp.setTable(detailEySummary.getTable());
					impsSummaryResp.setCount(detailEySummary.getCount());
					impsSummaryResp.setDocType(detailEySummary.getDocType());
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						impsSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						impsSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						impsSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						impsSummaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						impsSummaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						impsSummaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						impsSummaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						impsSummaryResp
								.setCrEligibleTotal(CheckForNegativeValue(
										detailEySummary.getCrEligibleTotal()));
						impsSummaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						impsSummaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						impsSummaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						impsSummaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						impSummaryList.add(impsSummaryResp);
					} else {
						impsSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						impsSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						impsSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						impsSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						impsSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						impsSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						impsSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						impsSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						impsSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						impsSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						impsSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						impsSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						impSummaryList.add(impsSummaryResp);
					}
				});
			}
			Gstr2PRSummarySectionDto impsaSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> impsaEySummary = impa.getEySummary();

			if (impsaEySummary != null) {

				impsaEySummary.forEach(detailEySummary -> {
					impsaSummaryResp.setTable(detailEySummary.getTable());
					impsaSummaryResp.setCount(detailEySummary.getCount());
					impsaSummaryResp.setDocType(detailEySummary.getDocType());
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						impsaSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						impsaSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						impsaSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						impsaSummaryResp
								.setTaxPayableCess(CheckForNegativeValue(
										detailEySummary.getTaxPayableCess()));
						impsaSummaryResp
								.setTaxPayableCgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableCgst()));
						impsaSummaryResp
								.setTaxPayableIgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableIgst()));
						impsaSummaryResp
								.setTaxPayableSgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableSgst()));
						impsaSummaryResp
								.setCrEligibleTotal(CheckForNegativeValue(
										detailEySummary.getCrEligibleTotal()));
						impsaSummaryResp
								.setCrEligibleCess(CheckForNegativeValue(
										detailEySummary.getCrEligibleCess()));
						impsaSummaryResp
								.setCrEligibleCgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleCgst()));
						impsaSummaryResp
								.setCrEligibleIgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleIgst()));
						impsaSummaryResp
								.setCrEligibleSgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleSgst()));
						impaSummaryList.add(impsaSummaryResp);
					} else {
						impsaSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						impsaSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						impsaSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						impsaSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						impsaSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						impsaSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						impsaSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						impsaSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						impsaSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						impsaSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						impsaSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						impsaSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						impaSummaryList.add(impsaSummaryResp);
					}
				});
			}
			/*
			 * Gstr2PRSummarySectionDto impgSummaryResp = new
			 * Gstr2PRSummarySectionDto(); List<Gstr2PRSummarySectionDto>
			 * impgEySummary = impg.getEySummary();
			 * 
			 * if (impgEySummary != null) {
			 * 
			 * impgEySummary.forEach(detailEySummary -> {
			 * impgSummaryResp.setTable(detailEySummary.getTable());
			 * impgSummaryResp.setCount(detailEySummary.getCount());
			 * impgSummaryResp.setDocType(detailEySummary.getDocType());
			 * impgSummaryResp.setInvoiceValue(detailEySummary.getInvoiceValue()
			 * );
			 * impgSummaryResp.setTaxableValue(detailEySummary.getTaxableValue()
			 * );
			 * impgSummaryResp.setTaxPayable(detailEySummary.getTaxPayable());
			 * impgSummaryResp.setTaxPayableCess(detailEySummary.
			 * getTaxPayableCess());
			 * impgSummaryResp.setTaxPayableCgst(detailEySummary.
			 * getTaxPayableCgst());
			 * impgSummaryResp.setTaxPayableIgst(detailEySummary.
			 * getTaxPayableIgst());
			 * impgSummaryResp.setTaxPayableSgst(detailEySummary.
			 * getTaxPayableSgst());
			 * impgSummaryResp.setCrEligibleTotal(detailEySummary.
			 * getCrEligibleTotal());
			 * impgSummaryResp.setCrEligibleCess(detailEySummary.
			 * getCrEligibleCess());
			 * impgSummaryResp.setCrEligibleCgst(detailEySummary.
			 * getCrEligibleCgst());
			 * impgSummaryResp.setCrEligibleIgst(detailEySummary.
			 * getCrEligibleIgst());
			 * impgSummaryResp.setCrEligibleSgst(detailEySummary.
			 * getCrEligibleSgst()); impgSummaryList.add(impgSummaryResp);
			 * 
			 * }); } Gstr2PRSummarySectionDto impgaSummaryResp = new
			 * Gstr2PRSummarySectionDto(); List<Gstr2PRSummarySectionDto>
			 * impgaEySummary = impga.getEySummary();
			 * 
			 * if (impgaEySummary != null) {
			 * 
			 * impgaEySummary.forEach(detailEySummary -> {
			 * impgaSummaryResp.setTable(detailEySummary.getTable());
			 * impgaSummaryResp.setCount(detailEySummary.getCount());
			 * impgaSummaryResp.setDocType(detailEySummary.getDocType());
			 * impgaSummaryResp.setInvoiceValue(detailEySummary.getInvoiceValue(
			 * ));
			 * impgaSummaryResp.setTaxableValue(detailEySummary.getTaxableValue(
			 * ));
			 * impgaSummaryResp.setTaxPayable(detailEySummary.getTaxPayable());
			 * impgaSummaryResp.setTaxPayableCess(detailEySummary.
			 * getTaxPayableCess());
			 * impgaSummaryResp.setTaxPayableCgst(detailEySummary.
			 * getTaxPayableCgst());
			 * impgaSummaryResp.setTaxPayableIgst(detailEySummary.
			 * getTaxPayableIgst());
			 * impgaSummaryResp.setTaxPayableSgst(detailEySummary.
			 * getTaxPayableSgst());
			 * impgaSummaryResp.setCrEligibleTotal(detailEySummary.
			 * getCrEligibleTotal());
			 * impgaSummaryResp.setCrEligibleCess(detailEySummary.
			 * getCrEligibleCess());
			 * impgaSummaryResp.setCrEligibleCgst(detailEySummary.
			 * getCrEligibleCgst());
			 * impgaSummaryResp.setCrEligibleIgst(detailEySummary.
			 * getCrEligibleIgst());
			 * impgaSummaryResp.setCrEligibleSgst(detailEySummary.
			 * getCrEligibleSgst()); impgaSummaryList.add(impgaSummaryResp);
			 * 
			 * }); } Gstr2PRSummarySectionDto impgsSummaryResp = new
			 * Gstr2PRSummarySectionDto(); List<Gstr2PRSummarySectionDto>
			 * impgsEySummary = impgs.getEySummary();
			 * 
			 * if (impgsEySummary != null) {
			 * 
			 * impgsEySummary.forEach(detailEySummary -> {
			 * impgsSummaryResp.setTable(detailEySummary.getTable());
			 * impgsSummaryResp.setCount(detailEySummary.getCount());
			 * impgsSummaryResp.setDocType(detailEySummary.getDocType());
			 * impgsSummaryResp.setInvoiceValue(detailEySummary.getInvoiceValue(
			 * ));
			 * impgsSummaryResp.setTaxableValue(detailEySummary.getTaxableValue(
			 * ));
			 * impgsSummaryResp.setTaxPayable(detailEySummary.getTaxPayable());
			 * impgsSummaryResp.setTaxPayableCess(detailEySummary.
			 * getTaxPayableCess());
			 * impgsSummaryResp.setTaxPayableCgst(detailEySummary.
			 * getTaxPayableCgst());
			 * impgsSummaryResp.setTaxPayableIgst(detailEySummary.
			 * getTaxPayableIgst());
			 * impgsSummaryResp.setTaxPayableSgst(detailEySummary.
			 * getTaxPayableSgst());
			 * impgsSummaryResp.setCrEligibleTotal(detailEySummary.
			 * getCrEligibleTotal());
			 * impgsSummaryResp.setCrEligibleCess(detailEySummary.
			 * getCrEligibleCess());
			 * impgsSummaryResp.setCrEligibleCgst(detailEySummary.
			 * getCrEligibleCgst());
			 * impgsSummaryResp.setCrEligibleIgst(detailEySummary.
			 * getCrEligibleIgst());
			 * impgsSummaryResp.setCrEligibleSgst(detailEySummary.
			 * getCrEligibleSgst()); impgsSummaryList.add(impgsSummaryResp);
			 * 
			 * }); } Gstr2PRSummarySectionDto impgsaSummaryResp = new
			 * Gstr2PRSummarySectionDto(); List<Gstr2PRSummarySectionDto>
			 * impgsaEySummary = impgsa.getEySummary();
			 * 
			 * if (impgsaEySummary != null) {
			 * 
			 * impgsaEySummary.forEach(detailEySummary -> {
			 * impgsaSummaryResp.setTable(detailEySummary.getTable());
			 * impgsaSummaryResp.setCount(detailEySummary.getCount());
			 * impgsaSummaryResp.setDocType(detailEySummary.getDocType());
			 * impgsaSummaryResp.setInvoiceValue(detailEySummary.getInvoiceValue
			 * ());
			 * impgsaSummaryResp.setTaxableValue(detailEySummary.getTaxableValue
			 * ());
			 * impgsaSummaryResp.setTaxPayable(detailEySummary.getTaxPayable());
			 * impgsaSummaryResp.setTaxPayableCess(detailEySummary.
			 * getTaxPayableCess());
			 * impgsaSummaryResp.setTaxPayableCgst(detailEySummary.
			 * getTaxPayableCgst());
			 * impgsaSummaryResp.setTaxPayableIgst(detailEySummary.
			 * getTaxPayableIgst());
			 * impgsaSummaryResp.setTaxPayableSgst(detailEySummary.
			 * getTaxPayableSgst());
			 * impgsaSummaryResp.setCrEligibleTotal(detailEySummary.
			 * getCrEligibleTotal());
			 * impgsaSummaryResp.setCrEligibleCess(detailEySummary.
			 * getCrEligibleCess());
			 * impgsaSummaryResp.setCrEligibleCgst(detailEySummary.
			 * getCrEligibleCgst());
			 * impgsaSummaryResp.setCrEligibleIgst(detailEySummary.
			 * getCrEligibleIgst());
			 * impgsaSummaryResp.setCrEligibleSgst(detailEySummary.
			 * getCrEligibleSgst()); impgsaSummaryList.add(impgsaSummaryResp);
			 * 
			 * }); }
			 */
			Gstr2PRSummarySectionDto rcurdSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> rcurdEySummary = rcurd
					.getEySummary();

			if (rcurdEySummary != null) {

				rcurdEySummary.forEach(detailEySummary -> {
					rcurdSummaryResp.setTable(detailEySummary.getTable());
					rcurdSummaryResp.setCount(detailEySummary.getCount());
					rcurdSummaryResp.setDocType(detailEySummary.getDocType());
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						rcurdSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						rcurdSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						rcurdSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						rcurdSummaryResp
								.setTaxPayableCess(CheckForNegativeValue(
										detailEySummary.getTaxPayableCess()));
						rcurdSummaryResp
								.setTaxPayableCgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableCgst()));
						rcurdSummaryResp
								.setTaxPayableIgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableIgst()));
						rcurdSummaryResp
								.setTaxPayableSgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableSgst()));
						rcurdSummaryResp
								.setCrEligibleTotal(CheckForNegativeValue(
										detailEySummary.getCrEligibleTotal()));
						rcurdSummaryResp
								.setCrEligibleCess(CheckForNegativeValue(
										detailEySummary.getCrEligibleCess()));
						rcurdSummaryResp
								.setCrEligibleCgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleCgst()));
						rcurdSummaryResp
								.setCrEligibleIgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleIgst()));
						rcurdSummaryResp
								.setCrEligibleSgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleSgst()));
						rcurdSummaryList.add(rcurdSummaryResp);
					} else {
						rcurdSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						rcurdSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						rcurdSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						rcurdSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						rcurdSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						rcurdSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						rcurdSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						rcurdSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						rcurdSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						rcurdSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						rcurdSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						rcurdSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						rcurdSummaryList.add(rcurdSummaryResp);
					}
				});
			}
			Gstr2PRSummarySectionDto rcurdaSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> rcurdaEySummary = rcurda
					.getEySummary();

			if (rcurdaEySummary != null) {

				rcurdaEySummary.forEach(detailEySummary -> {
					rcurdaSummaryResp.setTable(detailEySummary.getTable());
					rcurdaSummaryResp.setCount(detailEySummary.getCount());
					rcurdaSummaryResp.setDocType(detailEySummary.getDocType());
					String docType = detailEySummary.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR"))) {
						rcurdaSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						rcurdaSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						rcurdaSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						rcurdaSummaryResp
								.setTaxPayableCess(CheckForNegativeValue(
										detailEySummary.getTaxPayableCess()));
						rcurdaSummaryResp
								.setTaxPayableCgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableCgst()));
						rcurdaSummaryResp
								.setTaxPayableIgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableIgst()));
						rcurdaSummaryResp
								.setTaxPayableSgst(CheckForNegativeValue(
										detailEySummary.getTaxPayableSgst()));
						rcurdaSummaryResp
								.setCrEligibleTotal(CheckForNegativeValue(
										detailEySummary.getCrEligibleTotal()));
						rcurdaSummaryResp
								.setCrEligibleCess(CheckForNegativeValue(
										detailEySummary.getCrEligibleCess()));
						rcurdaSummaryResp
								.setCrEligibleCgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleCgst()));
						rcurdaSummaryResp
								.setCrEligibleIgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleIgst()));
						rcurdaSummaryResp
								.setCrEligibleSgst(CheckForNegativeValue(
										detailEySummary.getCrEligibleSgst()));
						rcurdaSummaryList.add(rcurdaSummaryResp);
					} else {
						rcurdaSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						rcurdaSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						rcurdaSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						rcurdaSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						rcurdaSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						rcurdaSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						rcurdaSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						rcurdaSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						rcurdaSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						rcurdaSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						rcurdaSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						rcurdaSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						rcurdaSummaryList.add(rcurdaSummaryResp);
					}
				});
			}
			

			Gstr2PRSummarySectionDto rcmAdvSummaryResp = new Gstr2PRSummarySectionDto();
			List<Gstr2PRSummarySectionDto> rcmAdvSummary = rcmadv.getEySummary();

			if (rcmAdvSummary != null) {

				rcmAdvSummary.forEach(detailEySummary -> {
					rcmAdvSummaryResp.setTable(detailEySummary.getTable());
					rcmAdvSummaryResp.setCount(detailEySummary.getCount());
					String docTypeEyDetail = detailEySummary.getDocType();
					rcmAdvSummaryResp.setDocType(detailEySummary.getDocType());
					if (docTypeEyDetail != null && (docTypeEyDetail.equalsIgnoreCase("ADJ"))) {
						rcmAdvSummaryResp.setInvoiceValue(CheckForNegativeValue(
								detailEySummary.getInvoiceValue()));
						rcmAdvSummaryResp.setTaxableValue(CheckForNegativeValue(
								detailEySummary.getTaxableValue()));
						rcmAdvSummaryResp.setTaxPayable(CheckForNegativeValue(
								detailEySummary.getTaxPayable()));
						rcmAdvSummaryResp.setTaxPayableCess(CheckForNegativeValue(
								detailEySummary.getTaxPayableCess()));
						rcmAdvSummaryResp.setTaxPayableCgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableCgst()));
						rcmAdvSummaryResp.setTaxPayableIgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableIgst()));
						rcmAdvSummaryResp.setTaxPayableSgst(CheckForNegativeValue(
								detailEySummary.getTaxPayableSgst()));
						rcmAdvSummaryResp.setCrEligibleTotal(CheckForNegativeValue(
								detailEySummary.getCrEligibleTotal()));
						rcmAdvSummaryResp.setCrEligibleCess(CheckForNegativeValue(
								detailEySummary.getCrEligibleCess()));
						rcmAdvSummaryResp.setCrEligibleCgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleCgst()));
						rcmAdvSummaryResp.setCrEligibleIgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleIgst()));
						rcmAdvSummaryResp.setCrEligibleSgst(CheckForNegativeValue(
								detailEySummary.getCrEligibleSgst()));
						rcmadvSummaryList.add(rcmAdvSummaryResp);
					} else {
						rcmAdvSummaryResp.setInvoiceValue(
								detailEySummary.getInvoiceValue());
						rcmAdvSummaryResp.setTaxableValue(
								detailEySummary.getTaxableValue());
						rcmAdvSummaryResp
								.setTaxPayable(detailEySummary.getTaxPayable());
						rcmAdvSummaryResp.setTaxPayableCess(
								detailEySummary.getTaxPayableCess());
						rcmAdvSummaryResp.setTaxPayableCgst(
								detailEySummary.getTaxPayableCgst());
						rcmAdvSummaryResp.setTaxPayableIgst(
								detailEySummary.getTaxPayableIgst());
						rcmAdvSummaryResp.setTaxPayableSgst(
								detailEySummary.getTaxPayableSgst());
						rcmAdvSummaryResp.setCrEligibleTotal(
								detailEySummary.getCrEligibleTotal());
						rcmAdvSummaryResp.setCrEligibleCess(
								detailEySummary.getCrEligibleCess());
						rcmAdvSummaryResp.setCrEligibleCgst(
								detailEySummary.getCrEligibleCgst());
						rcmAdvSummaryResp.setCrEligibleIgst(
								detailEySummary.getCrEligibleIgst());
						rcmAdvSummaryResp.setCrEligibleSgst(
								detailEySummary.getCrEligibleSgst());
						rcmadvSummaryList.add(rcmAdvSummaryResp);
					}

				});
			}

			List<Gstr2PRSummarySectionDto> prB2BResp = b2bStructure
					.prB2BResp(b2bSummaryList, "1-B2B");
			List<Gstr2PRSummarySectionDto> prB2BAResp = b2bStructure
					.prB2BResp(b2baSummaryList, "2-B2BA");
			List<Gstr2PRSummarySectionDto> prCdnResp = b2bStructure
					.prB2BResp(cdnEySummary, "3-CDN");
			List<Gstr2PRSummarySectionDto> prCdnaResp = b2bStructure
					.prB2BResp(cdnaEySummary, "4-CDNA");
			List<Gstr2PRSummarySectionDto> prIsaResp = b2bStructure
					.prB2BResp(isdEySummary, "5-ISD");
			List<Gstr2PRSummarySectionDto> prIsdaResp = b2bStructure
					.prB2BResp(isdaEySummary, "6-ISDA");

			List<Gstr2PRSummarySectionDto> prImpResp = b2bStructure
					.prB2BResp(impsEySummary, "10-IMP");
			List<Gstr2PRSummarySectionDto> prImpaResp = b2bStructure
					.prB2BResp(impsaEySummary, "11-IMPA");
			// List<Gstr2PRSummarySectionDto> prImpgsResp =
			// b2bStructure.prB2BResp(impgsSummaryList,"10-IMPGS");
			// List<Gstr2PRSummarySectionDto> imports = new ArrayList<>();
			// imports.addAll(prImpResp);
			// imports.addAll(prImpaResp);
			// imports.addAll(prImpgsResp);

			/*
			 * List<Gstr2PRSummarySectionDto> prImpgaResp =
			 * b2bStructure.prB2BResp(impgaSummaryList,"11-IMPGA");
			 * List<Gstr2PRSummarySectionDto> prImpsaResp =
			 * b2bStructure.prB2BResp(impsaSummaryList,"11-IMPSA");
			 * List<Gstr2PRSummarySectionDto> prImpgsaResp =
			 * b2bStructure.prB2BResp(impgsaSummaryList,"11-IMPGSA");
			 */
			List<Gstr2PRSummarySectionDto> importsA = new ArrayList<>();
			importsA.addAll(prImpaResp);
			/*
			 * importsA.addAll(prImpsaResp); importsA.addAll(prImpgsaResp);
			 * 
			 */
			List<Gstr2PRSummarySectionDto> prRcurdResp = b2bStructure
					.prB2BResp(rcurdEySummary, "12-RCURD");
			List<Gstr2PRSummarySectionDto> prRcurdaResp = b2bStructure
					.prB2BResp(rcurdaEySummary, "13-RCURDA");
			List<Gstr2PRSummarySectionDto> prRcmadvResp = b2bStructure
					.prB2BResp(rcmAdvSummary, "14-RCMADV");
			List<List<Gstr2PRSummarySectionDto>> gstr2PRSummarySectionDtoList = new ArrayList<>();
			gstr2PRSummarySectionDtoList.add(prB2BResp);
			gstr2PRSummarySectionDtoList.add(prB2BAResp);
			gstr2PRSummarySectionDtoList.add(prCdnResp);
			gstr2PRSummarySectionDtoList.add(prCdnaResp);
			gstr2PRSummarySectionDtoList.add(prIsaResp);
			gstr2PRSummarySectionDtoList.add(prIsdaResp);
			gstr2PRSummarySectionDtoList.add(prImpResp);
			gstr2PRSummarySectionDtoList.add(prImpaResp);
			gstr2PRSummarySectionDtoList.add(prRcurdResp);
			gstr2PRSummarySectionDtoList.add(prRcurdaResp);
			gstr2PRSummarySectionDtoList.add(prRcmadvResp);


			for (List<Gstr2PRSummarySectionDto> gstr2PRSummarySectionDto : gstr2PRSummarySectionDtoList) {
				for (Gstr2PRSummarySectionDto doc : gstr2PRSummarySectionDto) {
					String docType = doc.getDocType();
					if (docType != null && (docType.equalsIgnoreCase("CR")
							|| docType.equalsIgnoreCase("C")
							|| docType.equalsIgnoreCase("RCR")
							|| docType.equalsIgnoreCase("ADJ"))) {
						doc.setCrEligibleCess(
								CheckForNegativeValue(doc.getCrEligibleCess()));
						doc.setCrEligibleCgst(
								CheckForNegativeValue(doc.getCrEligibleCgst()));
						doc.setCrEligibleIgst(
								CheckForNegativeValue(doc.getCrEligibleIgst()));
						doc.setCrEligibleSgst(
								CheckForNegativeValue(doc.getCrEligibleSgst()));
						doc.setCrEligibleTotal(CheckForNegativeValue(
								doc.getCrEligibleTotal()));
						doc.setInvoiceValue(
								CheckForNegativeValue(doc.getInvoiceValue()));
						doc.setTaxableValue(
								CheckForNegativeValue(doc.getTaxableValue()));
						doc.setTaxPayable(
								CheckForNegativeValue(doc.getTaxPayable()));
						doc.setTaxPayableCess(
								CheckForNegativeValue(doc.getTaxPayableCess()));
						doc.setTaxPayableCgst(
								CheckForNegativeValue(doc.getTaxPayableCgst()));
						doc.setTaxPayableIgst(
								CheckForNegativeValue(doc.getTaxPayableIgst()));
						doc.setTaxPayableSgst(
								CheckForNegativeValue(doc.getTaxPayableSgst()));
					}
				}
			}
			JsonElement summaryB2BRespbody = gson.toJsonTree(prB2BResp);
			JsonElement summaryB2BARespbody = gson.toJsonTree(prB2BAResp);

			JsonElement summaryCdnRespbody = gson.toJsonTree(prCdnResp);
			JsonElement summaryCdnaRespbody = gson.toJsonTree(prCdnaResp);

			JsonElement summaryIsaRespbody = gson.toJsonTree(prIsaResp);
			JsonElement summaryIsadRespbody = gson.toJsonTree(prIsdaResp);

			JsonElement importsJson = gson.toJsonTree(prImpResp);
			// JsonElement summaryImpsRespbody = gson.toJsonTree(prImpsResp);
			// JsonElement summaryImpgsRespbody = gson.toJsonTree(prImpgsResp);

			JsonElement importsAJson = gson.toJsonTree(prImpaResp);
			// JsonElement summaryImpgsaRespbody =
			// gson.toJsonTree(prImpgsaResp);
			// JsonElement summaryImpsaRespbody = gson.toJsonTree(prImpsaResp);

			JsonElement summaryRcurdRespbody = gson.toJsonTree(prRcurdResp);
			JsonElement summaryRcurdaRespbody = gson.toJsonTree(prRcurdaResp);
			
			JsonElement summaryRcmadvRespbody = gson.toJsonTree(prRcmadvResp);


			combinedMap.put("b2b", summaryB2BRespbody);
			combinedMap.put("b2ba", summaryB2BARespbody);

			combinedMap.put("cdn", summaryCdnRespbody);
			combinedMap.put("cdna", summaryCdnaRespbody);

			combinedMap.put("isd", summaryIsaRespbody);
			combinedMap.put("isda", summaryIsadRespbody);

			combinedMap.put("imports", importsJson);

			// combinedMap.put("imports", summaryImpgRespbody);

			combinedMap.put("imports-a", importsAJson);
			// combinedMap.put("IMPSA", summaryImpsaRespbody);
			// combinedMap.put("IMPGSA", summaryImpgsaRespbody);

			combinedMap.put("rcurd", summaryRcurdRespbody);
			combinedMap.put("rcurda", summaryRcurdaRespbody);
			combinedMap.put("rcmadv", summaryRcmadvRespbody);


		}
		JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
		return summaryRespbody;
	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}
}
