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

import com.ey.advisory.app.docs.dto.Gstr1BasicDocSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicNilSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1DocSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1NilSummaryRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocIssueSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleNilSummarySearchService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SummaryScreenDocReqRespHandler")
public class Gstr1SummaryScreenDocReqRespHandler {

	public static final String docissued = "DOC ISSUED";
	public static final String nilNoN = "NILEXTNON";
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SummaryScreenDocReqRespHandler.class);

	@Autowired
	@Qualifier("Gstr1SimpleDocIssueSummarySearchService")
	Gstr1SimpleDocIssueSummarySearchService service;

	@Autowired
	@Qualifier("Gstr1SimpleNilSummarySearchService")
	Gstr1SimpleNilSummarySearchService nilService;
	
	@Autowired
	@Qualifier("Gstr1DocSummaryRespHandler")
	Gstr1DocSummaryRespHandler gstr1DocRespHandler;

	@Autowired
	@Qualifier("Gstr1NilSummaryRespHandler")
	Gstr1NilSummaryRespHandler gstr1NilRespHandler;
	
	@Autowired
	@Qualifier("GstnApiNilCalculation")
	GstnApiNilCalculation gstnApiNilCalculation;

	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenDocRespDto> handleGstr1DocReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {

		List<Gstr1SummaryScreenDocRespDto> docSummaryRespList = new ArrayList<>();

		LOGGER.debug("DocIssue  Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("Advances Sections Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		List<Gstr1SummaryDocSectionDto> gstnDocSummary = gstnResult.get(0)
				.getDocIssues().getGstnSummary();
		Gstr1SummaryDocSectionDto docGstn = addGstnData(gstnDocSummary);

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicDocSectionSummaryDto doc = dto.getDocIssues();
			List<Gstr1SummaryDocSectionDto> docSummary = doc.getEySummary();
			Gstr1SummaryScreenDocRespDto summaryResp = new Gstr1SummaryScreenDocRespDto();
			if (docSummary != null) {
				docSummary.forEach(docEySummary -> {

					// ASP Data
					summaryResp.setTotal(docEySummary.getTotal());
					summaryResp.setTaxDocType(docEySummary.getTaxDocType());
					summaryResp.setAspTotal(docEySummary.getTotal());
					summaryResp.setAspNetIssued(docEySummary.getNetIssued());
					summaryResp.setAspCancelled(docEySummary.getDocCancelled());

				});
			}else{
				summaryResp.setTaxDocType(docissued);
			}
			// Gstn Live Data

			summaryResp.setGstnCancelled(docGstn.getDocCancelled());
			summaryResp.setGstnNetIssued(docGstn.getNetIssued());
			summaryResp.setGstnTotal(docGstn.getTotal());

			/*
			 * Calculating Difference Asp - Gstn
			 */

			Integer aspCancelled = (summaryResp.getAspCancelled() != null)
					? summaryResp.getAspCancelled() : 0;
			Integer gstnCancelled = (summaryResp.getGstnCancelled() != null)
					? summaryResp.getGstnCancelled() : 0;

			Integer gstnTotal = (summaryResp.getGstnTotal() != null)
					? summaryResp.getGstnTotal() : 0;
			Integer aspTotal = (summaryResp.getAspTotal() != null)
					? summaryResp.getAspTotal() : 0;

			Integer aspNet = (summaryResp.getAspNetIssued() != null)
					? summaryResp.getAspNetIssued() : 0;
			Integer gstnNet = (summaryResp.getGstnNetIssued() != null)
					? summaryResp.getGstnNetIssued() : 0;

			summaryResp.setDiffCancelled(aspCancelled - gstnCancelled);

			summaryResp.setDiffNetIssued(aspNet - gstnNet);
			summaryResp.setDiffTotal(aspTotal - gstnTotal);

			docSummaryRespList.add(summaryResp);
		}

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<Gstr1SummaryScreenDocRespDto> handleDocResp = gstr1DocRespHandler
				.handleDocResp(docSummaryRespList);

		List<Gstr1SummaryScreenDocRespDto> response = new ArrayList<>();
		response.addAll(handleDocResp);
		return response;

	}

	/**
	 * Nil Section Implementation
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenNilRespDto> handleGstr1NilReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {

		List<Gstr1SummaryScreenNilRespDto> nilSummaryRespList = new ArrayList<>();

		LOGGER.debug(" Nil, Non And Exempted Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = nilService
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("Nil Section Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		
		List<Gstr1SummaryNilSectionDto> gstnDocSummary = gstnApiNilCalculation
				.addNilGstnDocTypes(gstnResult);
		
		Gstr1SummaryNilSectionDto nilGstn = addNilGstnData(gstnDocSummary);
		
	//	List<Gstr1SummaryNilSectionDto> nilSummary = new ArrayList<>();
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicNilSectionSummaryDto doc = dto.getNil();
			List<Gstr1SummaryNilSectionDto> nilSummary = doc.getEySummary();
			
		//	 nilSummary = gstnApiNilCalculation.addNilaspGstnDocTypes(nilSummaryDetail);
			
		
			if (nilSummary != null) {
				nilSummary.forEach(nilEySummary -> {

					Gstr1SummaryScreenNilRespDto summaryResp = 
							new Gstr1SummaryScreenNilRespDto();
					// ASP Data
					summaryResp.setTotal(nilEySummary.getTotal());
					summaryResp.setAspExempted(nilEySummary.getAspExempted());
					summaryResp.setAspNitRated(nilEySummary.getAspNitRated());
					summaryResp.setAspNonGst(nilEySummary.getAspNonGst());
					summaryResp.setTaxDocType(nilEySummary.getTaxDocType());

			
			// Gstn Live Data

			summaryResp.setGstnExempted(nilGstn.getAspExempted());
			summaryResp.setGstnNitRated(nilGstn.getAspNitRated());
			summaryResp.setGstnNonGst(nilGstn.getAspNonGst());

			/*
			 * Calculating Difference Asp - Gstn
			 */

			BigDecimal aspExempted = (summaryResp.getAspExempted() != null)
					? summaryResp.getAspExempted() : BigDecimal.ZERO;
					BigDecimal gstnExempted = (summaryResp.getGstnExempted() != null)
					? summaryResp.getGstnExempted() : BigDecimal.ZERO;

					BigDecimal aspNitRated = (summaryResp.getAspNitRated() != null)
					? summaryResp.getAspNitRated() : BigDecimal.ZERO;
					BigDecimal gstnNitRated = (summaryResp.getGstnNitRated() != null)
					? summaryResp.getGstnNitRated() : BigDecimal.ZERO;

					BigDecimal aspNonGst = (summaryResp.getAspNonGst() != null)
					? summaryResp.getAspNonGst() : BigDecimal.ZERO;
					BigDecimal gstnNonGstn = (summaryResp.getGstnNonGst() != null)
					? summaryResp.getGstnNonGst() : BigDecimal.ZERO;

			summaryResp.setDiffExempted(aspExempted.subtract(gstnExempted));
			summaryResp.setDiffNitRated(aspNitRated.subtract(gstnNitRated));
			summaryResp.setDiffNonGst(aspNonGst.subtract(gstnNonGstn));

			nilSummaryRespList.add(summaryResp);
				});
			}
		}

		Gson gson = GsonUtil.newSAPGsonInstance();
	/*	List<Gstr1SummaryScreenNilRespDto> handleNilResp = gstr1NilRespHandler
				.handleNilResp(nilSummaryRespList);*/

		List<Gstr1SummaryScreenNilRespDto> response = new ArrayList<>();
		response.addAll(nilSummaryRespList);
		return response;

	}

	private Gstr1SummaryDocSectionDto addGstnData(
			List<Gstr1SummaryDocSectionDto> gstnSummary) {
		Gstr1SummaryDocSectionDto sumDto = new Gstr1SummaryDocSectionDto();

		Integer total = 0;
		Integer netIssued = 0;
		Integer cancelled = 0;

		if (gstnSummary != null) {
			for (Gstr1SummaryDocSectionDto dto : gstnSummary) {

				total = total + (dto.getTotal() == null ? 0 : dto.getTotal());
				netIssued = netIssued
						+ (dto.getNetIssued() == null ? 0 : dto.getNetIssued());
				cancelled = cancelled + (dto.getDocCancelled() == null ? 0
						: dto.getDocCancelled());

			}
		}

		sumDto.setDocCancelled(cancelled);
		sumDto.setNetIssued(netIssued);
		sumDto.setTotal(total);
		return sumDto;

	}

	private Gstr1SummaryNilSectionDto addNilGstnData(
			List<Gstr1SummaryNilSectionDto> gstnSummary) {
		Gstr1SummaryNilSectionDto sumDto = new Gstr1SummaryNilSectionDto();

		BigDecimal totalAspExempted = BigDecimal.ZERO;
		BigDecimal nitRated = BigDecimal.ZERO;
		BigDecimal nonGstn = BigDecimal.ZERO;

		if (gstnSummary != null) {
			for (Gstr1SummaryNilSectionDto dto : gstnSummary) {

				totalAspExempted = totalAspExempted
						.add(dto.getAspExempted() == null ? BigDecimal.ZERO
								: dto.getAspExempted());
				nitRated = nitRated.add(dto.getAspNitRated() == null
						? BigDecimal.ZERO : dto.getAspNitRated());
				nonGstn = nonGstn.add(dto.getAspNonGst() == null
						? BigDecimal.ZERO : dto.getAspNonGst());

			}
		}

		sumDto.setAspExempted(totalAspExempted);
		sumDto.setAspNitRated(nitRated);
		sumDto.setAspNonGst(nonGstn);
		return sumDto;

	}

}
