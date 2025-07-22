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
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SezSimpleDocSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SeztSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SezwtSummaryRespHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SummaryScreenSezReqRespHandler")
public class Gstr1SummaryScreenSezReqRespHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SummaryScreenSezReqRespHandler.class);
	@Autowired
	@Qualifier("Gstr1SezSimpleDocSummarySearchService")
	Gstr1SezSimpleDocSummarySearchService service;

	@Autowired
	@Qualifier("Gstr1SeztSummaryRespHandler")
	Gstr1SeztSummaryRespHandler gstr1SeztRespHandler;

	@Autowired
	@Qualifier("Gstr1SezwtSummaryRespHandler")
	Gstr1SezwtSummaryRespHandler gstr1SezwtRespHandler;

	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenRespDto> handleGstr1SezReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		LOGGER.debug("SEZ Sections's Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("SEZ Sections's Sections Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();
		List<Gstr1SummaryScreenRespDto> seztSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto sezt = dto.getSezt();
			List<Gstr1SummarySectionDto> seztSummary = sezt.getEySummary();
			if (seztSummary != null) {
				seztSummary.forEach(seztEySummary -> {
					Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
					summaryResp.setTaxDocType(seztEySummary.getTaxDocType());
					summaryResp
							.setAspTaxableValue(seztEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(seztEySummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(seztEySummary.getInvValue());
					summaryResp.setAspIgst(seztEySummary.getIgst());
					summaryResp.setAspCgst(seztEySummary.getCgst());
					summaryResp.setAspSgst(seztEySummary.getSgst());
					summaryResp.setAspCess(seztEySummary.getCess());
					summaryResp.setAspCount(seztEySummary.getRecords());
					seztSummaryRespList.add(summaryResp);
					
				});
			}
		}
		/*
		 * ATA
		 */
		List<Gstr1SummaryScreenRespDto> sezwtSummaryRespList = new ArrayList<>();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto sezwt = dto.getSezwt();
			List<Gstr1SummarySectionDto> sezwtSummary = sezwt.getEySummary();
			if (sezwtSummary != null) {
				sezwtSummary.forEach(sezwtEySummary -> {
					Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
					summaryResp.setTaxDocType(sezwtEySummary.getTaxDocType());
					summaryResp
							.setAspTaxableValue(sezwtEySummary.getTaxableValue());
					summaryResp.setAspTaxPayble(sezwtEySummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(sezwtEySummary.getInvValue());
					summaryResp.setAspIgst(sezwtEySummary.getIgst());
					summaryResp.setAspCgst(sezwtEySummary.getCgst());
					summaryResp.setAspSgst(sezwtEySummary.getSgst());
					summaryResp.setAspCess(sezwtEySummary.getCess());
					summaryResp.setAspCount(sezwtEySummary.getRecords());
					sezwtSummaryRespList.add(summaryResp);
				});
			}
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<Gstr1SummaryScreenRespDto> handleSeztResp = gstr1SeztRespHandler
				.handleSeztResp(seztSummaryRespList);

		List<Gstr1SummaryScreenRespDto> handleSezwtResp = gstr1SezwtRespHandler
				.handleSezwtResp(sezwtSummaryRespList);
		
		List<Gstr1SummaryScreenRespDto> sezTotal = new ArrayList<>();
		sezTotal.addAll(handleSeztResp);
		sezTotal.addAll(handleSezwtResp);
		Gstr1SummaryScreenRespDto addSezTotal = addSezTotal(sezTotal);

		List<Gstr1SummaryScreenRespDto> response = new ArrayList<>();
		response.add(addSezTotal);
		response.addAll(handleSeztResp);
		response.addAll(handleSezwtResp);
		return response;

	}

	public Gstr1SummaryScreenRespDto addSezTotal(List<Gstr1SummaryScreenRespDto> sezt){
		
		Gstr1SummaryScreenRespDto seztotal = new Gstr1SummaryScreenRespDto();
		
		BigDecimal invValue = BigDecimal.ZERO;
		BigDecimal taxable = BigDecimal.ZERO;
		BigDecimal taxPayable = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal cess = BigDecimal.ZERO;
		Integer count = 0;
		for(Gstr1SummaryScreenRespDto dto1 : sezt){
			
			invValue = invValue.add(dto1.getAspInvoiceValue());
			taxable = taxable.add(dto1.getAspTaxableValue());
			taxPayable = taxPayable.add(dto1.getAspTaxPayble());
			igst = igst.add(dto1.getAspIgst());
			sgst = sgst.add(dto1.getAspCgst());
			cgst = cgst.add(dto1.getAspSgst());
			cess = cess.add(dto1.getAspCess());
			count = count+dto1.getAspCount();
		}
		seztotal.setAspInvoiceValue(invValue);
		seztotal.setAspTaxableValue(taxable);
		seztotal.setAspTaxPayble(taxPayable);
		seztotal.setAspIgst(igst);
		seztotal.setAspCgst(cgst);
		seztotal.setAspSgst(sgst);
		seztotal.setAspCess(cess);
		seztotal.setAspCount(count);
		seztotal.setTaxDocType("Total");
		return seztotal;

		
	}
	
	
}