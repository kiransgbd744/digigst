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
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenItemRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1HsnSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleHSNSummarySearchService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SummaryScreenHSNReqRespHandler")
public class Gstr1SummaryScreenHSNReqRespHandler {

	public static final String hsnconst = "HSN";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SummaryScreenHSNReqRespHandler.class);
	@Autowired
	@Qualifier("Gstr1SimpleHSNSummarySearchService")
	Gstr1SimpleHSNSummarySearchService service;

	@Autowired
	@Qualifier("Gstr1HsnSummaryRespHandler")
	Gstr1HsnSummaryRespHandler gstr1HsnRespHandler;

	@Autowired
	@Qualifier("Gstr1B2CSCalculation")
	Gstr1B2CSCalculation gstr1B2CSCalculation;

	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenRespDto> handleGstr1HsnReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {

		LOGGER.debug(" Summary Execution For HSN  Section BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug(" Summary Execution For HSN  Section END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		List<Gstr1SummaryScreenRespDto> hsnSummaryRespList = new ArrayList<>();
		List<Gstr1SummarySectionDto> gstnhsnSummary = gstnResult.get(0)
				.getGstnHsn().getGstnSummary();
		List<Gstr1SummarySectionDto> gstnB2bHsnSummary = gstnResult.get(0)
				.getGstnHsnB2b().getGstnSummary();
		List<Gstr1SummarySectionDto> gstnB2cHsnSummary = gstnResult.get(0)
				.getGstnHsnB2c().getGstnSummary();
		
		Gstr1SummarySectionDto hsnGstn = addGstnData(gstnhsnSummary);
		Gstr1SummarySectionDto b2bHsnGstn = addGstnData(gstnB2bHsnSummary);
		Gstr1SummarySectionDto b2cHsnGstn = addGstnData(gstnB2cHsnSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicCDSectionSummaryDto hsn = dto.getHsn();
			List<Gstr1SummaryCDSectionDto> hsnSummary = hsn.getEySummary();
			if (hsnSummary != null) {
				for (Gstr1SummaryCDSectionDto hsnEySummary : hsnSummary) {
					if (hsnEySummary.getTaxDocType()
							.equalsIgnoreCase("HSN_ASP")) {
						Gstr1SummaryScreenRespDto summaryResp = getOverallHsnData(
								hsnEySummary, hsnGstn);
						List<Gstr1SummaryScreenItemRespDto> items = new ArrayList<>();
						for (Gstr1SummaryCDSectionDto summaryDto : hsnSummary) {
							if (summaryDto.getTaxDocType() != null) {
								if (summaryDto.getTaxDocType()
										.equalsIgnoreCase("HSN_ASP_B2B")) {
									items.add(getHsnItemData(summaryDto,
											b2bHsnGstn, "B2B"));
								} else if (summaryDto.getTaxDocType()
										.equalsIgnoreCase("HSN_ASP_B2C")) {
									items.add(getHsnItemData(summaryDto,
											b2cHsnGstn, "B2C"));
								}
							}
						}
						summaryResp.setItems(items);
						hsnSummaryRespList.add(summaryResp);
					} else if (hsnEySummary.getTaxDocType()
							.equalsIgnoreCase("HSN_UI")) {
						Gstr1SummaryScreenRespDto summaryResp = getOverallHsnData(
								hsnEySummary, hsnGstn);
						List<Gstr1SummaryScreenItemRespDto> items = new ArrayList<>();
						for (Gstr1SummaryCDSectionDto summaryDto : hsnSummary) {
							if (summaryDto.getTaxDocType() != null) {
								if (summaryDto.getTaxDocType()
										.equalsIgnoreCase("B2B")) {
									items.add(getHsnItemData(summaryDto,
											b2bHsnGstn, "B2B"));
								} else if (summaryDto.getTaxDocType()
										.equalsIgnoreCase("B2C")) {
									items.add(getHsnItemData(summaryDto,
											b2cHsnGstn, "B2C"));
								}
							}
						}
						summaryResp.setItems(items);
						hsnSummaryRespList.add(summaryResp);
					}

				}
			}
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		/*
		 * List<Gstr1SummaryScreenRespDto> handleHSNResp = gstr1HsnRespHandler
		 * .handleHSNResp(hsnSummaryRespList);
		 */
		return hsnSummaryRespList;
	}

	public Gstr1SummaryScreenItemRespDto getHsnItemData(
			Gstr1SummaryCDSectionDto hsnEySummary,
			Gstr1SummarySectionDto hsnGstn, String taxDocType) {

		Gstr1SummaryScreenItemRespDto item = new Gstr1SummaryScreenItemRespDto();
		item.setTaxDocType(taxDocType);
		item.setAspTaxableValue(hsnEySummary.getTaxableValue());
		item.setAspTaxPayble(hsnEySummary.getTaxPayable());
		item.setAspInvoiceValue(hsnEySummary.getInvValue());
		item.setAspIgst(hsnEySummary.getIgst());
		item.setAspCgst(hsnEySummary.getCgst());
		item.setAspSgst(hsnEySummary.getSgst());
		item.setAspCess(hsnEySummary.getCess());
		item.setAspCount(hsnEySummary.getRecords());

		// Gstn Data
		item.setGstnTaxableValue(hsnGstn.getTaxableValue());
		item.setGstnTaxPayble(hsnGstn.getTaxPayable());
		item.setGstnInvoiceValue(hsnGstn.getInvValue());
		item.setGstnIgst(hsnGstn.getIgst());
		item.setGstnCgst(hsnGstn.getCgst());
		item.setGstnSgst(hsnGstn.getSgst());
		item.setGstnCess(hsnGstn.getCess());
		item.setGstnCount(hsnGstn.getRecords());

		/*
		 * Calculating Difference Asp - Gstn
		 */
		BigDecimal diffTotaltax = subMethod(item.getAspInvoiceValue(),
				item.getGstnInvoiceValue());
		BigDecimal diffTaxable = subMethod(item.getAspTaxableValue(),
				item.getGstnTaxableValue());
		BigDecimal diffTaxPayble = subMethod(item.getAspTaxPayble(),
				item.getGstnTaxPayble());
		BigDecimal diffIgst = subMethod(item.getAspIgst(), item.getGstnIgst());
		BigDecimal diffSgst = subMethod(item.getAspSgst(), item.getGstnSgst());

		BigDecimal diffCgst = subMethod(item.getAspCgst(), item.getGstnCgst());

		BigDecimal diffCess = subMethod(item.getAspCess(), item.getGstnCess());

		Integer aspCount = (item.getAspCount() != null) ? item.getAspCount()
				: 0;
		Integer gstnCount = (item.getGstnCount() != null) ? item.getGstnCount()
				: 0;

		item.setDiffCount(aspCount - gstnCount);
		item.setDiffInvoiceValue(diffTotaltax);
		item.setDiffTaxableValue(diffTaxable);
		item.setDiffTaxPayble(diffTaxPayble);
		item.setDiffIgst(diffIgst);
		item.setDiffCgst(diffCgst);
		item.setDiffSgst(diffSgst);
		item.setDiffCess(diffCess);
		return item;
	}

	public Gstr1SummaryScreenRespDto getOverallHsnData(
			Gstr1SummaryCDSectionDto hsnEySummary,
			Gstr1SummarySectionDto hsnGstn) {
		Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
		summaryResp.setTaxDocType(hsnEySummary.getTaxDocType());
		summaryResp.setAspTaxableValue(hsnEySummary.getTaxableValue());
		summaryResp.setAspTaxPayble(hsnEySummary.getTaxPayable());
		summaryResp.setAspInvoiceValue(hsnEySummary.getInvValue());
		summaryResp.setAspIgst(hsnEySummary.getIgst());
		summaryResp.setAspCgst(hsnEySummary.getCgst());
		summaryResp.setAspSgst(hsnEySummary.getSgst());
		summaryResp.setAspCess(hsnEySummary.getCess());
		summaryResp.setAspCount(hsnEySummary.getRecords());

		// Gstn Data
		summaryResp.setGstnTaxableValue(hsnGstn.getTaxableValue());
		summaryResp.setGstnTaxPayble(hsnGstn.getTaxPayable());
		summaryResp.setGstnInvoiceValue(hsnGstn.getInvValue());
		summaryResp.setGstnIgst(hsnGstn.getIgst());
		summaryResp.setGstnCgst(hsnGstn.getCgst());
		summaryResp.setGstnSgst(hsnGstn.getSgst());
		summaryResp.setGstnCess(hsnGstn.getCess());
		summaryResp.setGstnCount(hsnGstn.getRecords());

		/*
		 * Calculating Difference Asp - Gstn
		 */
		BigDecimal diffTotaltax = subMethod(summaryResp.getAspInvoiceValue(),
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

		return summaryResp;
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
