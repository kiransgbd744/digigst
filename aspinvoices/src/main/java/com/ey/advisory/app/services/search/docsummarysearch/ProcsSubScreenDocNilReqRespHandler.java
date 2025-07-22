/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import com.ey.advisory.app.services.search.simplified.docsummary.ProcsSubmitDocIssueSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.ProcsSubmitNilExtNonSearchService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@Service("ProcsSubScreenDocNilReqRespHandler")
public class ProcsSubScreenDocNilReqRespHandler {

	public static final String docissued = "DOC_ISSUED";
	public static final String nilNoN = "NILEXTNON";
	
	
	@Autowired
	@Qualifier("ProcsSubmitDocIssueSummarySearchService")
	ProcsSubmitDocIssueSummarySearchService service;
	
	@Autowired
	@Qualifier("ProcsSubmitNilExtNonSearchService")
	ProcsSubmitNilExtNonSearchService nilService;
	
	/*@Autowired
	@Qualifier("Gstr1DocSummaryRespHandler")
	Gstr1DocSummaryRespHandler gstr1DocRespHandler;
*/
/*	@Autowired
	@Qualifier("Gstr1NilSummaryRespHandler")
	Gstr1NilSummaryRespHandler gstr1NilRespHandler;
*/	
	@Autowired
	@Qualifier("ProcSubmitNilSummaryStructure")
	ProcSubmitNilSummaryStructure reqRespDefault;
	
	@Autowired
	@Qualifier("ProcsSubmitDocSummaryStructure")
	ProcsSubmitDocSummaryStructure docRespHandler;
	
/*	@Autowired
	@Qualifier("GstnApiNilCalculation")
	GstnApiNilCalculation gstnApiNilCalculation;

*/	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenDocRespDto> handleGstr1DocReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		List<Gstr1SummaryScreenDocRespDto> docSummaryRespList = new ArrayList<>();

		LOGGER.debug("DocIssue  Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("Advances Sections Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicDocSectionSummaryDto doc = dto.getDocIssues();
			List<Gstr1SummaryDocSectionDto> docSummary = doc.getEySummary();
			Gstr1SummaryScreenDocRespDto summaryResp = new Gstr1SummaryScreenDocRespDto();
			if (docSummary != null) {
				docSummary.forEach(docEySummary -> {

					// ASP Data
					summaryResp.setTaxDocType(docEySummary.getTaxDocType());
					summaryResp.setAspTotal(docEySummary.getAspTotal());
					summaryResp.setAspNetIssued(docEySummary.getAspNetIssued());
					summaryResp.setAspCancelled(docEySummary.getAspCancelled());
					summaryResp.setGstnTotal(docEySummary.getGstnTotal());
					summaryResp.setGstnCancelled(docEySummary.getGstnCancelled());
					summaryResp.setGstnNetIssued(docEySummary.getGstnNetIssued());

				});
			}
			
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
		List<Gstr1SummaryScreenDocRespDto> handleDocResp = docRespHandler.gstr1DocResp(docSummaryRespList,docissued);

	/*	List<Gstr1SummaryScreenDocRespDto> response = new ArrayList<>();
		response.addAll(handleDocResp);*/
		return handleDocResp;

	}

	/**
	 * Nil Section Implementation
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Gstr1SummaryScreenNilRespDto> handleGstr1NilReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest) {

		List<Gstr1SummaryScreenNilRespDto> nilSummaryRespList = new ArrayList<>();

		LOGGER.debug(" Nil, Non And Exempted Summary Execution BEGIN ");
		SearchResult<Gstr1CompleteSummaryDto> summaryResult = nilService
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);
		LOGGER.debug("Nil Section Summary Execution END ");

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

	//	List<Gstr1SummaryNilSectionDto> nilSummary = new ArrayList<>();
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicNilSectionSummaryDto doc = dto.getNil();
			List<Gstr1SummaryNilSectionDto> nilSummary = doc.getEySummary();
			
		//	 nilSummary = gstnApiNilCalculation.addNilaspGstnDocTypes(nilSummaryDetail);
			
			Gstr1SummaryScreenNilRespDto summaryResp = 
					new Gstr1SummaryScreenNilRespDto();
			if (nilSummary != null) {
				nilSummary.forEach(nilEySummary -> {

					// ASP Data
					summaryResp.setAspExempted(nilEySummary.getAspExempted());
					summaryResp.setAspNitRated(nilEySummary.getAspNitRated());
					summaryResp.setAspNonGst(nilEySummary.getAspNonGst());
					summaryResp.setTaxDocType(nilEySummary.getTaxDocType());
					summaryResp.setGstnExempted(nilEySummary.getGstnExempted());
					summaryResp.setGstnNitRated(nilEySummary.getGstnNitRated());
					summaryResp.setGstnNonGst(nilEySummary.getGstnNonGst());

				});
			}
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
		}

		/*Gson gson = GsonUtil.newSAPGsonInstance();
		List<Gstr1SummaryScreenNilRespDto> handleNilResp = gstr1NilRespHandler
				.handleNilResp(nilSummaryRespList);*/

		List<Gstr1SummaryScreenNilRespDto> gstr1NilResp = reqRespDefault.gstr1NilResp(nilSummaryRespList, nilNoN);
		
	//	List<Gstr1SummaryScreenNilRespDto> response = new ArrayList<>();
	//	response.addAll(gstr1NilResp);
		return gstr1NilResp;

	}
}
