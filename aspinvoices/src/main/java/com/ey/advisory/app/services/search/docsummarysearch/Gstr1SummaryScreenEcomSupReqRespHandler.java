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

import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleEcomSupSearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1tbl14and15SummaryRespHandler;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;

/**
 * @author Siva.Reddy
 *
 */
@Service("Gstr1SummaryScreenEcomSupReqRespHandler")
public class Gstr1SummaryScreenEcomSupReqRespHandler {

	@Autowired
	@Qualifier("Gstr1SimpleEcomSupSearchService")
	Gstr1SimpleEcomSupSearchService service;

	@Autowired
	@Qualifier("Gstr1tbl14and15SummaryRespHandler")
	Gstr1tbl14and15SummaryRespHandler gstr1tbl14and15SummaryRespHandler;

	public List<Gstr1SummaryScreenRespDto> handleGstr1ReqAndResp(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {

		SearchResult<Gstr1CompleteSummaryDto> summaryResult = service
				.<Gstr1CompleteSummaryDto>find(annexure1SummaryRequest, null,
						Gstr1CompleteSummaryDto.class);

		List<? extends Gstr1CompleteSummaryDto> list = summaryResult
				.getResult();

		List<Gstr1SummaryScreenRespDto> tbl14RespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl14ofOneRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl14ofTwoRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl14AmdRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl14ofOneAmdRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl14ofTwoAmdRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15RespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofOneRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofTwoRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofThreeRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofFourRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15AmdRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofOneAAmdRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofOneBAmdRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15AmdIIRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofTwoAAmdRespList = new ArrayList<>();
		List<Gstr1SummaryScreenRespDto> tbl15ofTwoBAmdRespList = new ArrayList<>();

/*		Gstr1SummaryScreenRespDto tbl14summaryResp = new Gstr1SummaryScreenRespDto();
		tbl14summaryResp.setTaxDocType(GSTConstants.GSTR1_14);
		tbl14RespList.add(tbl14summaryResp);
*/		
		setTable14And15Response(gstnResult, list, tbl14RespList,
				gstnResult.get(0).getTbl14Sec().getGstnSummary(),
				GSTConstants.GSTR1_14);

		setTable14And15Response(gstnResult, list, tbl14ofOneRespList,
				gstnResult.get(0).getTbl14ofOne().getGstnSummary(),
				GSTConstants.GSTR1_14I);

		setTable14And15Response(gstnResult, list, tbl14ofTwoRespList,
				gstnResult.get(0).getTbl14ofTwo().getGstnSummary(),
				GSTConstants.GSTR1_14II);

		/*Gstr1SummaryScreenRespDto tbl14Amd = new Gstr1SummaryScreenRespDto();
		tbl14Amd.setTaxDocType(GSTConstants.GSTR1_14A);
		tbl14AmdRespList.add(tbl14Amd);*/
		
		setTable14And15Response(gstnResult, list, tbl14AmdRespList,
				gstnResult.get(0).getTbl14AmdSec().getGstnSummary(),
				GSTConstants.GSTR1_14A);

		setTable14And15Response(gstnResult, list, tbl14ofOneAmdRespList,
				gstnResult.get(0).getTbl14AmdOne().getGstnSummary(),
				GSTConstants.GSTR1_14AI);

		setTable14And15Response(gstnResult, list, tbl14ofTwoAmdRespList,
				gstnResult.get(0).getTbl14AmdTwo().getGstnSummary(),
				GSTConstants.GSTR1_14AII);

		setTable14And15Response(gstnResult, list, tbl15RespList,
				gstnResult.get(0).getTbl15Sec().getGstnSummary(),
				GSTConstants.GSTR1_15);

		setTable14And15Response(gstnResult, list, tbl15ofOneRespList, null,
				GSTConstants.GSTR1_15I);

		setTable14And15Response(gstnResult, list, tbl15ofTwoRespList, null,
				GSTConstants.GSTR1_15II);

		setTable14And15Response(gstnResult, list, tbl15ofThreeRespList, null,
				GSTConstants.GSTR1_15III);

		setTable14And15Response(gstnResult, list, tbl15ofFourRespList, null,
				GSTConstants.GSTR1_15IV);

		setTable14And15Response(gstnResult, list, tbl15AmdRespList,
				gstnResult.get(0).getTbl15AmdOneSec().getGstnSummary(),
				GSTConstants.GSTR1_15AI);

		setTable14And15Response(gstnResult, list, tbl15ofOneAAmdRespList, null,
				GSTConstants.GSTR1_15AIA);
		setTable14And15Response(gstnResult, list, tbl15ofOneBAmdRespList, null,
				GSTConstants.GSTR1_15AIB);

		setTable14And15Response(gstnResult, list, tbl15AmdIIRespList,
				gstnResult.get(0).getTbl15AmdTwoSec().getGstnSummary(),
				GSTConstants.GSTR1_15AII);

		setTable14And15Response(gstnResult, list, tbl15ofTwoAAmdRespList, null,
				GSTConstants.GSTR1_15AIIA);
		setTable14And15Response(gstnResult, list, tbl15ofTwoBAmdRespList, null,
				GSTConstants.GSTR1_15AIIB);
		
		List<Gstr1SummaryScreenRespDto> handleTbl14Resp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl14RespList, GSTConstants.GSTR1_14);

		List<Gstr1SummaryScreenRespDto> handleTbl14ofOneResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl14ofOneRespList,
						GSTConstants.GSTR1_14I);

		List<Gstr1SummaryScreenRespDto> handleTbl14ofTwoResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl14ofTwoRespList,
						GSTConstants.GSTR1_14II);
		
		List<Gstr1SummaryScreenRespDto> handleTbl14AmdSecResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl14AmdRespList, GSTConstants.GSTR1_14A);

		List<Gstr1SummaryScreenRespDto> handleTbl14AmdOneResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl14ofOneAmdRespList,
						GSTConstants.GSTR1_14AI);

		List<Gstr1SummaryScreenRespDto> handleTbl14AmdTwoResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl14ofTwoAmdRespList,
						GSTConstants.GSTR1_14AII);

		List<Gstr1SummaryScreenRespDto> handleTbl15Resp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15RespList, GSTConstants.GSTR1_15);

		List<Gstr1SummaryScreenRespDto> handleTbl15ofOneResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofOneRespList,
						GSTConstants.GSTR1_15I);

		List<Gstr1SummaryScreenRespDto> handleTbl15ofTwoResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofTwoRespList,
						GSTConstants.GSTR1_15II);

		List<Gstr1SummaryScreenRespDto> handleTbl15ofThreeResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofThreeRespList,
						GSTConstants.GSTR1_15III);

		List<Gstr1SummaryScreenRespDto> handleTbl15ofFourResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofFourRespList,
						GSTConstants.GSTR1_15IV);

		List<Gstr1SummaryScreenRespDto> handleTbl15AmdSecResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15AmdRespList,
						GSTConstants.GSTR1_15AI);

		List<Gstr1SummaryScreenRespDto> handleTbl15AmdofOneResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofOneAAmdRespList,
						GSTConstants.GSTR1_15AIA);

		List<Gstr1SummaryScreenRespDto> handleTbl15AmdofTwoResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofOneBAmdRespList,
						GSTConstants.GSTR1_15AIB);

		List<Gstr1SummaryScreenRespDto> handleTbl15AmdTwoSecResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15AmdIIRespList,
						GSTConstants.GSTR1_15AII);

		List<Gstr1SummaryScreenRespDto> handleTbl15TAmdofThreeResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofTwoAAmdRespList,
						GSTConstants.GSTR1_15AIIA);

		List<Gstr1SummaryScreenRespDto> handleTbl15AmdofFourResp = gstr1tbl14and15SummaryRespHandler
				.handletbl14and15Resp(tbl15ofTwoBAmdRespList,
						GSTConstants.GSTR1_15AIIB);

		List<Gstr1SummaryScreenRespDto> response = new ArrayList<>();

		response.addAll(handleTbl14Resp);
		response.addAll(handleTbl14ofOneResp);
		response.addAll(handleTbl14ofTwoResp);
		response.addAll(handleTbl14AmdSecResp);
		response.addAll(handleTbl14AmdOneResp);
		response.addAll(handleTbl14AmdTwoResp);
		response.addAll(handleTbl15Resp);
		response.addAll(handleTbl15ofOneResp);
		response.addAll(handleTbl15ofTwoResp);
		response.addAll(handleTbl15ofThreeResp);
		response.addAll(handleTbl15ofFourResp);
		response.addAll(handleTbl15AmdSecResp);
		response.addAll(handleTbl15AmdofOneResp);
		response.addAll(handleTbl15AmdofTwoResp);
		response.addAll(handleTbl15AmdTwoSecResp);
		response.addAll(handleTbl15TAmdofThreeResp);
		response.addAll(handleTbl15AmdofFourResp);
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

	private void setTable14And15Response(
			List<? extends Gstr1CompleteSummaryDto> gstnResult,
			List<? extends Gstr1CompleteSummaryDto> list,
			List<Gstr1SummaryScreenRespDto> respList,
			List<Gstr1SummarySectionDto> gstnSummary, String taxDocType) {
		for (Gstr1CompleteSummaryDto dto : list) {
			Gstr1BasicSectionSummaryDto sectionSummary = getSummaryDto(
					taxDocType, dto);
			List<Gstr1SummarySectionDto> tbltotalSummary = sectionSummary
					.getEySummary();
			Gstr1SummaryScreenRespDto summaryResp = new Gstr1SummaryScreenRespDto();
			if (tbltotalSummary != null) {
				tbltotalSummary.forEach(tblSummary -> {
					summaryResp.setTaxDocType(tblSummary.getTaxDocType());
					summaryResp
							.setAspTaxableValue(tblSummary.getTaxableValue());
					summaryResp.setAspTaxPayble(tblSummary.getTaxPayable());
					summaryResp.setAspInvoiceValue(tblSummary.getInvValue());
					summaryResp.setAspIgst(tblSummary.getIgst());
					summaryResp.setAspCgst(tblSummary.getCgst());
					summaryResp.setAspSgst(tblSummary.getSgst());
					summaryResp.setAspCess(tblSummary.getCess());
					summaryResp.setAspCount(tblSummary.getRecords());
				});
			} else {
				summaryResp.setTaxDocType(taxDocType);
			}

			if (gstnSummary != null) {
				Gstr1SummarySectionDto summarySection = addGstnData(
						gstnSummary);
				summaryResp
						.setGstnTaxableValue(summarySection.getTaxableValue());
				summaryResp.setGstnTaxPayble(summarySection.getTaxPayable());
				summaryResp.setGstnInvoiceValue(summarySection.getInvValue());
				summaryResp.setGstnIgst(summarySection.getIgst());
				summaryResp.setGstnCgst(summarySection.getCgst());
				summaryResp.setGstnSgst(summarySection.getSgst());
				summaryResp.setGstnCess(summarySection.getCess());
				summaryResp.setGstnCount(summarySection.getRecords());
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

			respList.add(summaryResp);
		}

	}

	private Gstr1BasicSectionSummaryDto getSummaryDto(String taxDocType,
			Gstr1CompleteSummaryDto dto) {

		switch (taxDocType) {
		case GSTConstants.GSTR1_14:
			return dto.getTbl14Sec();
		case GSTConstants.GSTR1_14I:
			return dto.getTbl14ofOne();
		case GSTConstants.GSTR1_14II:
			return dto.getTbl14ofTwo();
		case GSTConstants.GSTR1_14A:
			return dto.getTbl14AmdSec();
		case GSTConstants.GSTR1_14AI:
			return dto.getTbl14AmdOne();
		case GSTConstants.GSTR1_14AII:
			return dto.getTbl14AmdTwo();
		case GSTConstants.GSTR1_15:
			return dto.getTbl15Sec();
		case GSTConstants.GSTR1_15I:
			return dto.getTbl15ofOne();
		case GSTConstants.GSTR1_15II:
			return dto.getTbl15ofTwo();
		case GSTConstants.GSTR1_15III:
			return dto.getTbl15ofThree();
		case GSTConstants.GSTR1_15IV:
			return dto.getTbl15dofFour();
		case GSTConstants.GSTR1_15AI:
			return dto.getTbl15AmdOneSec();
		case GSTConstants.GSTR1_15AIA:
			return dto.getTbl15AmdOne();
		case GSTConstants.GSTR1_15AIB:
			return dto.getTbl15AmdTwo();
		case GSTConstants.GSTR1_15AII:
			return dto.getTbl15AmdTwoSec();
		case GSTConstants.GSTR1_15AIIA:
			return dto.getTbl15AmdThree();
		case GSTConstants.GSTR1_15AIIB:
			return dto.getTbl15AmdFour();

		default:
			return new Gstr1BasicSectionSummaryDto();
		}
	}

}