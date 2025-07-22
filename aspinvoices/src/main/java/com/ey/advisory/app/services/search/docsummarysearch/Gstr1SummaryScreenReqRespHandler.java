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

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
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
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SummaryScreenReqRespHandler")
public class Gstr1SummaryScreenReqRespHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SummaryScreenReqRespHandler.class);
	@Autowired
	@Qualifier("Gstr1SimpleDocSummarySearchService")
	Gstr1SimpleDocSummarySearchService service;

	@Autowired
	@Qualifier("Gstr1B2BSummaryRespHandler")
	Gstr1B2BSummaryRespHandler gstr1B2BRespHandler;

	@Autowired
	@Qualifier("Gstr1B2BASummaryRespHandler")
	Gstr1B2BASummaryRespHandler gstr1B2BARespHandler;

	@Autowired
	@Qualifier("Gstr1B2CLSummaryRespHandler")
	Gstr1B2CLSummaryRespHandler gstr1B2CLRespHandler;

	@Autowired
	@Qualifier("Gstr1B2CLASummaryRespHandler")
	Gstr1B2CLASummaryRespHandler gstr1B2CLARespHandler;

	@Autowired
	@Qualifier("Gstr1EXPSummaryRespHandler")
	Gstr1EXPSummaryRespHandler gstr1EXPRespHandler;

	@Autowired
	@Qualifier("Gstr1EXPASummaryRespHandler")
	Gstr1EXPASummaryRespHandler gstr1EXPARespHandler;

	@Autowired
	@Qualifier("Gstr1CDNRSummaryRespHandler")
	Gstr1CDNRSummaryRespHandler gstr1CDNRRespHandler;

	@Autowired
	@Qualifier("Gstr1CDNRASummaryRespHandler")
	Gstr1CDNRASummaryRespHandler gstr1CDNRARespHandler;

	@Autowired
	@Qualifier("Gstr1CDNURSummaryRespHandler")
	Gstr1CDNURSummaryRespHandler gstr1CDNURRespHandler;

	@Autowired
	@Qualifier("Gstr1CDNURASummaryRespHandler")
	Gstr1CDNURASummaryRespHandler gstr1CDNURARespHandler;

	@Autowired
	@Qualifier("Gstr1B2CSSummaryRespHandler")
	Gstr1B2CSSummaryRespHandler gstr1B2CSRespHandler;

	@Autowired
	@Qualifier("Gstr1B2CSASummaryRespHandler")
	Gstr1B2CSASummaryRespHandler gstr1B2CSARespHandler;

	@Autowired
	@Qualifier("CalculatingDrCrTotal")
	CalculatingDrCrTotal calculatingDrCrTotal;

	@Autowired
	@Qualifier("Gstr1B2CSCalculation")
	Gstr1B2CSCalculation gstr1B2CSCalculation;

	@Autowired
	@Qualifier("GstnApiNilCalculation")
	GstnApiNilCalculation gstnCalculation;

	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenRespDto> handleGstr1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {
		
		LOGGER.debug(" Summary Execution For B2B,B2BA,B2CL,B2CLA,B2CS,"
				+ "B2CSA, CDNR,CDNRA,CDNUR AND CDNURA  Sections BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug(" Summary Execution For B2B,B2BA,B2CL,B2CLA,B2CS,"
				+ "B2CSA, CDNR,CDNRA,CDNUR AND CDNURA  Sections END ");

		/*
		 * List<? extends Gstr1CompleteSummaryDto> gstnResult =
		 * GstnSummaryResult .getResult();
		 */
		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		List<Gstr1SummaryScreenRespDto> b2bSummaryRespList = new ArrayList<>();
		List<Gstr1SummarySectionDto> gstnB2BSummary = gstnResult.get(0).getB2b()
				.getGstnSummary();
		Gstr1SummarySectionDto b2bGstn = addGstnData(gstnB2BSummary);
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2b = dto.getB2b();
			List<Gstr1SummarySectionDto> b2bSummary = b2b.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (b2bSummary != null) {
				b2bSummary.forEach(b2bEySummary -> {
					summaryResp.setTaxDocType(b2bEySummary.getTaxDocType());
					summaryResp
							.setAspTaxableValue(b2bEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(b2bEySummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(b2bEySummary.getInvValue());
					summaryResp.setAspIgst(b2bEySummary.getIgst());
					summaryResp.setAspCgst(b2bEySummary.getCgst());
					summaryResp.setAspSgst(b2bEySummary.getSgst());
					summaryResp.setAspCess(b2bEySummary.getCess());
					summaryResp.setAspCount(b2bEySummary.getRecords());
				});
			}else {
				summaryResp.setTaxDocType("B2B");
			}
			// Gstn Data
			summaryResp.setGstnTaxableValue(b2bGstn.getTaxableValue());
			summaryResp.setGstnTaxPayble(b2bGstn.getTaxPayable());
			summaryResp.setGstnInvoiceValue(b2bGstn.getInvValue());
			summaryResp.setGstnIgst(b2bGstn.getIgst());
			summaryResp.setGstnCgst(b2bGstn.getCgst());
			summaryResp.setGstnSgst(b2bGstn.getSgst());
			summaryResp.setGstnCess(b2bGstn.getCess());
			summaryResp.setGstnCount(b2bGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnB2BASummary = gstnResult.get(0)
				.getB2ba().getGstnSummary();
		Gstr1SummarySectionDto b2baGstn = addGstnData(gstnB2BASummary);
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2ba = dto.getB2ba();
			List<Gstr1SummarySectionDto> b2baSummary = b2ba.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (b2baSummary != null) {
				b2baSummary.forEach(b2baEySummary -> {

					// ASP Data
					summaryResp.setTaxDocType(b2baEySummary.getTaxDocType());
					summaryResp.setAspTaxableValue(
							b2baEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(b2baEySummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(b2baEySummary.getInvValue());
					summaryResp.setAspIgst(b2baEySummary.getIgst());
					summaryResp.setAspCgst(b2baEySummary.getCgst());
					summaryResp.setAspSgst(b2baEySummary.getSgst());
					summaryResp.setAspCess(b2baEySummary.getCess());
					summaryResp.setAspCount(b2baEySummary.getRecords());

				});
			}else {
				summaryResp.setTaxDocType("B2BA");
			}
			// Gstn Live Data
			summaryResp.setGstnTaxableValue(b2baGstn.getTaxableValue());
			summaryResp.setGstnTaxPayble(b2baGstn.getTaxPayable());
			summaryResp.setGstnInvoiceValue(b2baGstn.getInvValue());
			summaryResp.setGstnIgst(b2baGstn.getIgst());
			summaryResp.setGstnCgst(b2baGstn.getCgst());
			summaryResp.setGstnSgst(b2baGstn.getSgst());
			summaryResp.setGstnCess(b2baGstn.getCess());
			summaryResp.setGstnCount(b2baGstn.getRecords());

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
			b2baSummaryRespList.add(summaryResp);

		}

		/*
		 * B2CL Section Stub Data
		 */
		List<Gstr1SummaryScreenRespDto> b2clSummaryRespList = new ArrayList<>();
		List<Gstr1SummarySectionDto> gstnB2CLSummary = gstnResult.get(0)
				.getB2cl().getGstnSummary();
		Gstr1SummarySectionDto b2clGstn = addGstnData(gstnB2CLSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2cl = dto.getB2cl();
			List<Gstr1SummarySectionDto> b2clSummary = b2cl.getEySummary();
			Gstr1SummaryScreenRespDto summaryB2clResp = new Gstr1SummaryScreenRespDto();
			if (b2clSummary != null) {
				b2clSummary.forEach(b2clEySummary -> {

					// ASP Data
					summaryB2clResp
							.setTaxDocType(b2clEySummary.getTaxDocType());
					summaryB2clResp.setAspTaxableValue(
							b2clEySummary.getTaxableValue());
					summaryB2clResp
							.setAspTaxPayble(b2clEySummary.getTaxPayable());
					summaryB2clResp
							.setAspInvoiceValue(b2clEySummary.getInvValue());
					summaryB2clResp.setAspIgst(b2clEySummary.getIgst());
					summaryB2clResp.setAspCgst(b2clEySummary.getCgst());
					summaryB2clResp.setAspSgst(b2clEySummary.getSgst());
					summaryB2clResp.setAspCess(b2clEySummary.getCess());
					summaryB2clResp.setAspCount(b2clEySummary.getRecords());

				});
			}else {
				summaryB2clResp.setTaxDocType("B2CL");
			}
			// Gstn Live Data
			summaryB2clResp.setGstnTaxableValue(b2clGstn.getTaxableValue());
			summaryB2clResp.setGstnTaxPayble(b2clGstn.getTaxPayable());
			summaryB2clResp.setGstnInvoiceValue(b2clGstn.getInvValue());
			summaryB2clResp.setGstnIgst(b2clGstn.getIgst());
			summaryB2clResp.setGstnCgst(b2clGstn.getCgst());
			summaryB2clResp.setGstnSgst(b2clGstn.getSgst());
			summaryB2clResp.setGstnCess(b2clGstn.getCess());
			summaryB2clResp.setGstnCount(b2clGstn.getRecords());

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
		List<Gstr1SummarySectionDto> gstnB2CLaSummary = gstnResult.get(0)
				.getB2cla().getGstnSummary();
		Gstr1SummarySectionDto b2claGstn = addGstnData(gstnB2CLaSummary);

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
							b2claEySummary.getTaxableValue());
					summaryB2claResp
							.setAspTaxPayble(b2claEySummary.getTaxPayable());
					summaryB2claResp
							.setAspInvoiceValue(b2claEySummary.getInvValue());
					summaryB2claResp.setAspIgst(b2claEySummary.getIgst());
					summaryB2claResp.setAspCgst(b2claEySummary.getCgst());
					summaryB2claResp.setAspSgst(b2claEySummary.getSgst());

					summaryB2claResp.setAspCess(b2claEySummary.getCess());
					summaryB2claResp.setAspCount(b2claEySummary.getRecords());

				});
			}else {
				summaryB2claResp.setTaxDocType("B2CLA");
			}
			// Gstn Stub Data
			summaryB2claResp.setGstnTaxableValue(b2claGstn.getTaxableValue());
			summaryB2claResp.setGstnTaxPayble(b2claGstn.getTaxPayable());
			summaryB2claResp.setGstnInvoiceValue(b2claGstn.getInvValue());
			summaryB2claResp.setGstnIgst(b2claGstn.getIgst());
			summaryB2claResp.setGstnCgst(b2claGstn.getCgst());
			summaryB2claResp.setGstnSgst(b2claGstn.getSgst());
			summaryB2claResp.setGstnCess(b2claGstn.getCess());
			summaryB2claResp.setGstnCount(b2claGstn.getRecords());

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
		List<Gstr1SummarySectionDto> gstnEXPSummary = gstnResult.get(0).getExp()
				.getGstnSummary();
		Gstr1SummarySectionDto expGstn = addGstnData(gstnEXPSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto exp = dto.getExp();
			List<Gstr1SummarySectionDto> expSummary = exp.getEySummary();
			Gstr1SummaryScreenRespDto summaryExpResp = new Gstr1SummaryScreenRespDto();
			if (expSummary != null) {
				expSummary.forEach(expEySummary -> {

					// Asp data
					summaryExpResp.setTaxDocType(expEySummary.getTaxDocType());
					summaryExpResp
							.setAspTaxableValue(expEySummary.getTaxableValue());
					summaryExpResp
							.setAspTaxPayble(expEySummary.getTaxPayable());
					summaryExpResp
							.setAspInvoiceValue(expEySummary.getInvValue());
					summaryExpResp.setAspIgst(expEySummary.getIgst());
					summaryExpResp.setAspCgst(expEySummary.getCgst());
					summaryExpResp.setAspSgst(expEySummary.getSgst());
					summaryExpResp.setAspCess(expEySummary.getCess());
					summaryExpResp.setAspCount(expEySummary.getRecords());

				});
			}else {
				summaryExpResp.setTaxDocType("EXPORTS");
			}
			// Gstn Live Data
			summaryExpResp.setGstnTaxableValue(expGstn.getTaxableValue());
			summaryExpResp.setGstnTaxPayble(expGstn.getTaxPayable());
			summaryExpResp.setGstnInvoiceValue(expGstn.getInvValue());
			summaryExpResp.setGstnIgst(expGstn.getIgst());
			summaryExpResp.setGstnCgst(expGstn.getCgst());
			summaryExpResp.setGstnSgst(expGstn.getSgst());
			summaryExpResp.setGstnCess(expGstn.getCess());
			summaryExpResp.setGstnCount(expGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnExpaSummary = gstnResult.get(0)
				.getExpa().getGstnSummary();
		Gstr1SummarySectionDto expaGstn = addGstnData(gstnExpaSummary);

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
							expaEySummary.getTaxableValue());
					summaryExpaResp
							.setAspTaxPayble(expaEySummary.getTaxPayable());
					summaryExpaResp
							.setAspInvoiceValue(expaEySummary.getInvValue());
					summaryExpaResp.setAspIgst(expaEySummary.getIgst());
					summaryExpaResp.setAspCgst(expaEySummary.getCgst());
					summaryExpaResp.setAspSgst(expaEySummary.getSgst());
					summaryExpaResp.setAspCess(expaEySummary.getCess());
					summaryExpaResp.setAspCount(expaEySummary.getRecords());

				});
			}else {
				summaryExpaResp.setTaxDocType("EXPORTS-A");
			}
			// Gstn Live Data
			summaryExpaResp.setGstnTaxableValue(expaGstn.getTaxableValue());
			summaryExpaResp.setGstnTaxPayble(expaGstn.getTaxPayable());
			summaryExpaResp.setGstnInvoiceValue(expaGstn.getInvValue());
			summaryExpaResp.setGstnIgst(expaGstn.getIgst());
			summaryExpaResp.setGstnCgst(expaGstn.getCgst());
			summaryExpaResp.setGstnSgst(expaGstn.getSgst());
			summaryExpaResp.setGstnCess(expaGstn.getCess());
			summaryExpaResp.setGstnCount(expaGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnCDNRSummary = gstnCalculation
				.addCDNRGstnDocTypes(gstnResult);
	//	Gstr1SummarySectionDto cdnrGstn = gstnCDNRSummary.get(0);
		Gstr1SummarySectionDto cdnrGstn = addCDGstnData(gstnCDNRSummary);
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnr = dto.getCdnr();
			//List<Gstr1SummarySectionDto> cdnrSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnrResp = new Gstr1SummaryScreenRespDto();
				List<Gstr1SummarySectionDto> cdnrSummary = cdnr
						.getEySummary();		
			if (cdnrSummary != null) {
				cdnrSummary.forEach(cdnrEySummary -> {
					// ASP Data

					summaryCdnrResp
							.setTaxDocType(cdnrEySummary.getTaxDocType());
					summaryCdnrResp.setAspTaxableValue(
							cdnrEySummary.getTaxableValue());
					summaryCdnrResp
							.setAspTaxPayble(cdnrEySummary.getTaxPayable());
					summaryCdnrResp
							.setAspInvoiceValue(cdnrEySummary.getInvValue());
					summaryCdnrResp.setAspIgst(cdnrEySummary.getIgst());
					summaryCdnrResp.setAspCgst(cdnrEySummary.getCgst());
					summaryCdnrResp.setAspSgst(cdnrEySummary.getSgst());
					summaryCdnrResp.setAspCess(cdnrEySummary.getCess());
					summaryCdnrResp.setAspCount(cdnrEySummary.getRecords());

				});
			}else {
				summaryCdnrResp.setTaxDocType("CDNR");
			}
			// Gstn Live Data
			summaryCdnrResp.setGstnTaxableValue(cdnrGstn.getTaxableValue());
			summaryCdnrResp.setGstnTaxPayble(cdnrGstn.getTaxPayable());
			summaryCdnrResp.setGstnInvoiceValue(cdnrGstn.getInvValue());
			summaryCdnrResp.setGstnIgst(cdnrGstn.getIgst());
			summaryCdnrResp.setGstnCgst(cdnrGstn.getCgst());
			summaryCdnrResp.setGstnSgst(cdnrGstn.getSgst());
			summaryCdnrResp.setGstnCess(cdnrGstn.getCess());
			summaryCdnrResp.setGstnCount(cdnrGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnCDNRASummary = gstnCalculation
				.addCDNRAGstnDocTypes(gstnResult);
	//	Gstr1SummarySectionDto cdnraGstn = gstnCDNRASummary.get(0);
		Gstr1SummarySectionDto cdnraGstn = addCDGstnData(gstnCDNRASummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnra = dto.getCdnra();
		//	List<Gstr1SummarySectionDto> cdnraSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnraResp = new Gstr1SummaryScreenRespDto();

				List<Gstr1SummarySectionDto> cdnraSummary = cdnra
						.getEySummary();
			if (cdnraSummary != null) {
				cdnraSummary.forEach(cdnraEySummary -> {

					// ASP Data

					summaryCdnraResp
							.setTaxDocType(cdnraEySummary.getTaxDocType());
					summaryCdnraResp.setAspTaxableValue(
							cdnraEySummary.getTaxableValue());
					summaryCdnraResp
							.setAspTaxPayble(cdnraEySummary.getTaxPayable());
					summaryCdnraResp
							.setAspInvoiceValue(cdnraEySummary.getInvValue());
					summaryCdnraResp.setAspIgst(cdnraEySummary.getIgst());
					summaryCdnraResp.setAspCgst(cdnraEySummary.getCgst());
					summaryCdnraResp.setAspSgst(cdnraEySummary.getSgst());
					summaryCdnraResp.setAspCess(cdnraEySummary.getCess());
					summaryCdnraResp.setAspCount(cdnraEySummary.getRecords());

				});
			}else {
				summaryCdnraResp.setTaxDocType("CDNRA");
			}
			// Gstn Live Data
			summaryCdnraResp.setGstnTaxableValue(cdnraGstn.getTaxableValue());
			summaryCdnraResp.setGstnTaxPayble(cdnraGstn.getTaxPayable());
			summaryCdnraResp.setGstnInvoiceValue(cdnraGstn.getInvValue());
			summaryCdnraResp.setGstnIgst(cdnraGstn.getIgst());
			summaryCdnraResp.setGstnCgst(cdnraGstn.getCgst());
			summaryCdnraResp.setGstnSgst(cdnraGstn.getSgst());
			summaryCdnraResp.setGstnCess(cdnraGstn.getCess());
			summaryCdnraResp.setGstnCount(cdnraGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnCDNURSummary = gstnCalculation
				.addCDNURGstnDocTypes(gstnResult);
	//	Gstr1SummarySectionDto cdnurGstn = gstnCDNURSummary.get(0);
		Gstr1SummarySectionDto cdnurGstn = addCDGstnData(gstnCDNURSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto cdnur = dto.getCdnur();
		//	List<Gstr1SummarySectionDto> cdnurSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnurResp = new Gstr1SummaryScreenRespDto();
		//	if (cdnur != null) {
				List<Gstr1SummarySectionDto> cdnurSummary = cdnur
						.getEySummary();

				if (cdnurSummary != null) {
				cdnurSummary.forEach(cdnurEySummary -> {

					// Asp Data

					summaryCdnurResp
							.setTaxDocType(cdnurEySummary.getTaxDocType());
					summaryCdnurResp.setAspTaxableValue(
							cdnurEySummary.getTaxableValue());
					summaryCdnurResp
							.setAspTaxPayble(cdnurEySummary.getTaxPayable());
					summaryCdnurResp
							.setAspInvoiceValue(cdnurEySummary.getInvValue());
					summaryCdnurResp.setAspIgst(cdnurEySummary.getIgst());
					summaryCdnurResp.setAspCgst(cdnurEySummary.getCgst());
					summaryCdnurResp.setAspSgst(cdnurEySummary.getSgst());
					summaryCdnurResp.setAspCess(cdnurEySummary.getCess());
					summaryCdnurResp.setAspCount(cdnurEySummary.getRecords());

				});

			}else {
				summaryCdnurResp.setTaxDocType("CDNUR");
			}
			// Gstn Live Data
			summaryCdnurResp.setGstnTaxableValue(cdnurGstn.getTaxableValue());
			summaryCdnurResp.setGstnTaxPayble(cdnurGstn.getTaxPayable());
			summaryCdnurResp.setGstnInvoiceValue(cdnurGstn.getInvValue());
			summaryCdnurResp.setGstnIgst(cdnurGstn.getIgst());
			summaryCdnurResp.setGstnCgst(cdnurGstn.getCgst());
			summaryCdnurResp.setGstnSgst(cdnurGstn.getSgst());
			summaryCdnurResp.setGstnCess(cdnurGstn.getCess());
			summaryCdnurResp.setGstnCount(cdnurGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnCDNURASummary = gstnCalculation
				.addCDNURAGstnDocTypes(gstnResult);
	//	Gstr1SummarySectionDto cdnuraGstn = gstnCDNURASummary.get(0);
		Gstr1SummarySectionDto cdnuraGstn = addCDGstnData(gstnCDNURASummary);

		for (Gstr1CompleteSummaryDto dto : list) {

			Gstr1BasicSectionSummaryDto cdnura = dto.getCdnura();
		//	List<Gstr1SummarySectionDto> cdnuraSummary = new ArrayList<>();
			Gstr1SummaryScreenRespDto summaryCdnuraResp = new Gstr1SummaryScreenRespDto();
			
				List<Gstr1SummarySectionDto> cdnuraSummary = cdnura
						.getEySummary();

			if (cdnuraSummary != null) {
				cdnuraSummary.forEach(cdnuraEySummary -> {

					// ASP Data

					summaryCdnuraResp
							.setTaxDocType(cdnuraEySummary.getTaxDocType());
					summaryCdnuraResp.setAspTaxableValue(
							cdnuraEySummary.getTaxableValue());
					summaryCdnuraResp
							.setAspTaxPayble(cdnuraEySummary.getTaxPayable());
					summaryCdnuraResp
							.setAspInvoiceValue(cdnuraEySummary.getInvValue());
					summaryCdnuraResp.setAspIgst(cdnuraEySummary.getIgst());
					summaryCdnuraResp.setAspCgst(cdnuraEySummary.getCgst());
					summaryCdnuraResp.setAspSgst(cdnuraEySummary.getSgst());
					summaryCdnuraResp.setAspCess(cdnuraEySummary.getCess());
					summaryCdnuraResp.setAspCount(cdnuraEySummary.getRecords());

				});
			}else {
				summaryCdnuraResp.setTaxDocType("CDNURA");
			}
			// Gstn Live Data
			summaryCdnuraResp.setGstnTaxableValue(cdnuraGstn.getTaxableValue());
			summaryCdnuraResp.setGstnTaxPayble(cdnuraGstn.getTaxPayable());
			summaryCdnuraResp.setGstnInvoiceValue(cdnuraGstn.getInvValue());
			summaryCdnuraResp.setGstnIgst(cdnuraGstn.getIgst());
			summaryCdnuraResp.setGstnCgst(cdnuraGstn.getCgst());
			summaryCdnuraResp.setGstnSgst(cdnuraGstn.getSgst());
			summaryCdnuraResp.setGstnCess(cdnuraGstn.getCess());
			summaryCdnuraResp.setGstnCount(cdnuraGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnB2CSSummary = gstnCalculation
				.addB2CSGstnDocTypes(gstnResult);
		/*
		 * Gstr1SummarySectionDto b2csGstn =
		 * gstr1B2CSCalculation.addB2csGstnData(gstnB2CSSummary);
		 */
	//	Gstr1SummarySectionDto b2csGstn = gstnB2CSSummary.get(0);
		Gstr1SummarySectionDto b2csGstn = addCDGstnData(gstnB2CSSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2cs = dto.getB2cs();

			List<Gstr1SummarySectionDto> b2csSummary = b2cs.getEySummary();
		//	List<Gstr1SummarySectionDto> b2csSummary = new ArrayList<>();
			/*Gstr1SummarySectionDto b2csSummarylist = gstr1B2CSCalculation
					.addB2csGstnData(invCrDrSummary);
			b2csSummary.add(b2csSummarylist);*/
			Gstr1SummaryScreenRespDto summaryB2csResp = new Gstr1SummaryScreenRespDto();
			if (b2csSummary != null) {
				b2csSummary.forEach(b2csEySummary -> {

					summaryB2csResp
							.setTaxDocType(b2csEySummary.getTaxDocType());
					summaryB2csResp.setAspTaxableValue(
							b2csEySummary.getTaxableValue());
					summaryB2csResp
							.setAspTaxPayble(b2csEySummary.getTaxPayable());
					summaryB2csResp
							.setAspInvoiceValue(b2csEySummary.getInvValue());
					summaryB2csResp.setAspIgst(b2csEySummary.getIgst());
					summaryB2csResp.setAspCgst(b2csEySummary.getCgst());
					summaryB2csResp.setAspSgst(b2csEySummary.getSgst());
					summaryB2csResp.setAspCess(b2csEySummary.getCess());
					summaryB2csResp.setAspCount(b2csEySummary.getRecords());

				});
			}else {
				summaryB2csResp.setTaxDocType("B2CS");
			}
			// Gstn Data
			summaryB2csResp.setGstnTaxableValue(b2csGstn.getTaxableValue());
			summaryB2csResp.setGstnTaxPayble(b2csGstn.getTaxPayable());
			summaryB2csResp.setGstnInvoiceValue(b2csGstn.getInvValue());
			summaryB2csResp.setGstnIgst(b2csGstn.getIgst());
			summaryB2csResp.setGstnCgst(b2csGstn.getCgst());
			summaryB2csResp.setGstnSgst(b2csGstn.getSgst());
			summaryB2csResp.setGstnCess(b2csGstn.getCess());
			summaryB2csResp.setGstnCount(b2csGstn.getRecords());

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

		List<Gstr1SummarySectionDto> gstnB2CSASummary = gstnCalculation
				.addB2CSAGstnDocTypes(gstnResult);
		/*
		 * Gstr1SummarySectionDto b2csaGstn =
		 * gstr1B2CSCalculation.addB2csaGstnData(gstnB2CSASummary);
		 */

		/*
		 * Gstr1SummarySectionDto b2csaGstn =
		 * gstr1B2CSCalculation.addB2csaGstnData(gstnB2CSASummary);
		 */
	//	Gstr1SummarySectionDto b2csaGstn = gstnB2CSASummary.get(0);
		Gstr1SummarySectionDto b2csaGstn = addCDGstnData(gstnB2CSASummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto b2csa = dto.getB2csa();
			List<Gstr1SummarySectionDto> b2csaSummary = b2csa
					.getEySummary();
		/*	List<Gstr1SummarySectionDto> b2csaSummary = new ArrayList<>();
			Gstr1SummarySectionDto addB2csaAspData = gstr1B2CSCalculation
					.addB2csaGstnData(b2csaSummaryList);
			b2csaSummary.add(addB2csaAspData);*/
			Gstr1SummaryScreenRespDto summaryB2csaResp = new Gstr1SummaryScreenRespDto();
			if (b2csaSummary != null) {
				b2csaSummary.forEach(b2csaEySummary -> {

					summaryB2csaResp
							.setTaxDocType(b2csaEySummary.getTaxDocType());
					summaryB2csaResp.setAspTaxableValue(
							b2csaEySummary.getTaxableValue());
					summaryB2csaResp
							.setAspTaxPayble(b2csaEySummary.getTaxPayable());
					summaryB2csaResp
							.setAspInvoiceValue(b2csaEySummary.getInvValue());
					summaryB2csaResp.setAspIgst(b2csaEySummary.getIgst());
					summaryB2csaResp.setAspCgst(b2csaEySummary.getCgst());
					summaryB2csaResp.setAspSgst(b2csaEySummary.getSgst());
					summaryB2csaResp.setAspCess(b2csaEySummary.getCess());
					summaryB2csaResp.setAspCount(b2csaEySummary.getRecords());
				});
			}else {
				summaryB2csaResp.setTaxDocType("B2CSA");
			}
			// Gstn Stub Data
			summaryB2csaResp.setGstnTaxableValue(b2csaGstn.getTaxableValue());
			summaryB2csaResp.setGstnTaxPayble(b2csaGstn.getTaxPayable());
			summaryB2csaResp.setGstnInvoiceValue(b2csaGstn.getInvValue());
			summaryB2csaResp.setGstnIgst(b2csaGstn.getIgst());
			summaryB2csaResp.setGstnCgst(b2csaGstn.getCgst());
			summaryB2csaResp.setGstnSgst(b2csaGstn.getSgst());
			summaryB2csaResp.setGstnCess(b2csaGstn.getCess());
			summaryB2csaResp.setGstnCount(b2csaGstn.getRecords());

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
		List<Gstr1SummaryScreenRespDto> handleB2BResp = gstr1B2BRespHandler
				.handleB2BResp(b2bSummaryRespList);
		List<Gstr1SummaryScreenRespDto> handleB2BAResp = gstr1B2BARespHandler
				.handleB2BAResp(b2baSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleB2CLResp = gstr1B2CLRespHandler
				.handleB2CLResp(b2clSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleB2CLAResp = gstr1B2CLARespHandler                                        
				.handleB2CLAResp(b2claSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleEXPResp = gstr1EXPRespHandler
				.handleEXPResp(expSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleEXPAResp = gstr1EXPARespHandler
				.handleEXPAResp(expaSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleCDNRResp = gstr1CDNRRespHandler
				.handleCDNRResp(cdnrSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleCDNRAResp = gstr1CDNRARespHandler
				.handleCDNRAResp(cdnraSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleCDNURResp = gstr1CDNURRespHandler
				.handleCDNURResp(cdnurSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleCDNURAResp = gstr1CDNURARespHandler
				.handleCDNURAResp(cdnuraSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleB2CSResp = gstr1B2CSRespHandler
				.handleB2CSResp(b2csSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleB2CSAResp = gstr1B2CSARespHandler
				.handleB2CSAResp(b2csaSummaryRespList);

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

	private Gstr1SummarySectionDto addCDGstnData(
			List<Gstr1SummarySectionDto> gstnSummary) {

		/*
		 * Gstr1SummaryCDSectionDto cdnrDr = gstnSummary.stream() .filter(x ->
		 * "DR".equals(x.getDocType())).findAny() .orElse(null);
		 * 
		 * Gstr1SummaryCDSectionDto cdnrCr = gstnSummary.stream() .filter(x ->
		 * "CR".equals(x.getDocType())).findAny() .orElse(null);
		 * 
		 * Gstr1SummaryCDSectionDto cdnrRfv = gstnSummary.stream() .filter(x ->
		 * "RFV".equals(x.getDocType())).findAny() .orElse(null);
		 * Gstr1SummarySectionDto cdnrSummaryFinal =
		 * calculatingDrCrTotal.addregateCDNRCredtiDebit(cdnrDr, cdnrCr,
		 * cdnrRfv);
		 */

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
