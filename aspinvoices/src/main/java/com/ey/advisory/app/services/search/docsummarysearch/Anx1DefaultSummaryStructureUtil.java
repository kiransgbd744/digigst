package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryEcomResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.RefundSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;

@Service("Anx1DefaultStructureUtil")
public class Anx1DefaultSummaryStructureUtil {

	public Annexure1SummaryResp1Dto anx1DefaultStructure(
			Annexure1SummaryResp1Dto respDto) {

		respDto.setEyInvoiceValue(new BigDecimal("0"));
		respDto.setEyTaxableValue(new BigDecimal("0"));
		respDto.setEyTaxPayble(new BigDecimal("0"));
		respDto.setEyIgst(new BigDecimal("0"));
		respDto.setEyCgst(new BigDecimal("0"));
		respDto.setEySgst(new BigDecimal("0"));
		respDto.setEyCess(new BigDecimal("0"));
		respDto.setEyCount(0);
		// GSTN Resp
		// Annexure1TypeSummaryDto gstnResp = new Annexure1TypeSummaryDto();
		respDto.setGstnInvoiceValue(new BigDecimal("0"));
		respDto.setGstnTaxableValue(new BigDecimal("0"));
		respDto.setGstnTaxPayble(new BigDecimal("0"));
		respDto.setGstnIgst(new BigDecimal("0"));
		respDto.setGstnCgst(new BigDecimal("0"));
		respDto.setGstnSgst(new BigDecimal("0"));
		respDto.setGstnCess(new BigDecimal("0"));
		respDto.setGstnCount(0);
		// Diff
		// Annexure1TypeSummaryDto diffResp = new Annexure1TypeSummaryDto();
		respDto.setDiffInvoiceValue(new BigDecimal("0"));
		respDto.setDiffTaxableValue(new BigDecimal("0"));
		respDto.setDiffTaxPayble(new BigDecimal("0"));
		respDto.setDiffIgst(new BigDecimal("0"));
		respDto.setDiffCgst(new BigDecimal("0"));
		respDto.setDiffSgst(new BigDecimal("0"));
		respDto.setDiffSgst(new BigDecimal("0"));
		respDto.setDiffCount(0);

		// Mom
		respDto.setMemoInvoiceValue(new BigDecimal("0"));
		respDto.setMemoTaxableValue(new BigDecimal("0"));
		respDto.setMemoTaxPayble(new BigDecimal("0"));
		respDto.setMemoIgst(new BigDecimal("0"));
		respDto.setMemoCgst(new BigDecimal("0"));
		respDto.setMemoSgst(new BigDecimal("0"));
		respDto.setMemoSgst(new BigDecimal("0"));
		respDto.setMemoCount(0);

		return respDto;

	}
	public Annexure1SummaryEcomResp1Dto anx1EcomDefaultStructure(
			Annexure1SummaryEcomResp1Dto respDto) {

		// Ey Resp
		respDto.setEySupplyMade(new BigDecimal("0"));
		respDto.setEySupplyReturn(new BigDecimal("0"));
		respDto.setEyNetSupply(new BigDecimal("0"));
		respDto.setEyTaxPayble(new BigDecimal("0"));
		respDto.setEyIgst(new BigDecimal("0"));
		respDto.setEyCgst(new BigDecimal("0"));
		respDto.setEySgst(new BigDecimal("0"));
		respDto.setEyCess(new BigDecimal("0"));
		respDto.setEyCount(0);
		// GSTN Resp
		// Annexure1TypeSummaryDto gstnResp = new Annexure1TypeSummaryDto();
		respDto.setGstnSupplyMade(new BigDecimal("0"));
		respDto.setGstnSupplyReturn(new BigDecimal("0"));
		respDto.setGstnNetSupply(new BigDecimal("0"));
		respDto.setGstnTaxPayble(new BigDecimal("0"));
		respDto.setGstnIgst(new BigDecimal("0"));
		respDto.setGstnCgst(new BigDecimal("0"));
		respDto.setGstnSgst(new BigDecimal("0"));
		respDto.setGstnCess(new BigDecimal("0"));
		respDto.setGstnCount(0);
		// Diff
		// Annexure1TypeSummaryDto diffResp = new Annexure1TypeSummaryDto();
		respDto.setDiffSupplyMade(new BigDecimal("0"));
		respDto.setDiffSupplyReturn(new BigDecimal("0"));
		respDto.setDiffNetSupply(new BigDecimal("0"));
		respDto.setDiffTaxPayble(new BigDecimal("0"));
		respDto.setDiffIgst(new BigDecimal("0"));
		respDto.setDiffCgst(new BigDecimal("0"));
		respDto.setDiffSgst(new BigDecimal("0"));
		respDto.setDiffSgst(new BigDecimal("0"));
		respDto.setDiffCount(0);

		// Mom
		respDto.setMemoSupplyMade(new BigDecimal("0"));
		respDto.setMemoSupplyReturn(new BigDecimal("0"));
		respDto.setMemoNetSupply(new BigDecimal("0"));
		respDto.setMemoTaxPayble(new BigDecimal("0"));
		respDto.setMemoIgst(new BigDecimal("0"));
		respDto.setMemoCgst(new BigDecimal("0"));
		respDto.setMemoSgst(new BigDecimal("0"));
		respDto.setMemoSgst(new BigDecimal("0"));
		respDto.setMemoCount(0);

		return respDto;

}
	
	// Ret1 Common Method 
	public Ret1SummaryRespDto ret1DefaultStructure(
			Ret1SummaryRespDto respDto) {

		// Ey Resp
		respDto.setAspValue(new BigDecimal("0"));
		respDto.setAspIgst(new BigDecimal("0"));
		respDto.setAspSgst(new BigDecimal("0"));
		respDto.setAspCgst(new BigDecimal("0"));
		respDto.setAspCess(new BigDecimal("0"));
		// user
		respDto.setUsrValue(new BigDecimal("0"));
		respDto.setUsrIgst(new BigDecimal("0"));
		respDto.setUsrSgst(new BigDecimal("0"));
		respDto.setUsrCgst(new BigDecimal("0"));
		respDto.setUsrCess(new BigDecimal("0"));
		// GSTN Resp
		respDto.setGstnValue(new BigDecimal("0"));
		respDto.setGstnIgst(new BigDecimal("0"));
		respDto.setGstnSgst(new BigDecimal("0"));
		respDto.setGstnCgst(new BigDecimal("0"));
		respDto.setGstnCess(new BigDecimal("0"));		
		// Diff
		respDto.setDiffValue(new BigDecimal("0"));
		respDto.setDiffIgst(new BigDecimal("0"));
		respDto.setDiffSgst(new BigDecimal("0"));
		respDto.setDiffCgst(new BigDecimal("0"));
		respDto.setDiffCess(new BigDecimal("0"));
		
		return respDto;

}
	// Ret1 Common Method 
	public Ret1LateFeeSummaryDto ret1Int6DefaultStructure(
			Ret1LateFeeSummaryDto respDto) {
		
		respDto.setUsrIgst(BigDecimal.ZERO);
		respDto.setUsrCgst(BigDecimal.ZERO);
		respDto.setUsrSgst(BigDecimal.ZERO);
		respDto.setUsrCess(BigDecimal.ZERO);
		respDto.setUsrLateFeeCgst(BigDecimal.ZERO);
		respDto.setUsrLateFeeSgst(BigDecimal.ZERO);
		respDto.setGstnIgst(BigDecimal.ZERO);
		respDto.setGstnCgst(BigDecimal.ZERO);
		respDto.setGstnSgst(BigDecimal.ZERO);
		respDto.setGstnCess(BigDecimal.ZERO);
		respDto.setGstnLateFeeCgst(BigDecimal.ZERO);
		respDto.setGstnLateFeeSgst(BigDecimal.ZERO);
		respDto.setDiffIgst(BigDecimal.ZERO);
		respDto.setDiffCgst(BigDecimal.ZERO);
		respDto.setDiffSgst(BigDecimal.ZERO);
		respDto.setDiffCess(BigDecimal.ZERO);
		respDto.setDiffLateFeeCgst(BigDecimal.ZERO);
		respDto.setDiffLateFeeSgst(BigDecimal.ZERO);
		return respDto;

}
	public TaxPaymentSummaryDto ret1Payment7DefaultStructure(
			TaxPaymentSummaryDto summaryResp) {

		summaryResp.setUsrPayable(BigDecimal.ZERO);
		summaryResp.setUsrOtherPayable(BigDecimal.ZERO);
		summaryResp.setUsrPaid(BigDecimal.ZERO);
		summaryResp.setUsrOtherPaid(BigDecimal.ZERO);
		summaryResp.setUsrLiability(BigDecimal.ZERO);
		summaryResp.setUsrOtherLiability(BigDecimal.ZERO);
		summaryResp.setUsrItcPaidIgst(BigDecimal.ZERO);
		summaryResp.setUsrItcPaidCgst(BigDecimal.ZERO);
		summaryResp.setUsrItcPaidSgst(BigDecimal.ZERO);
		summaryResp.setUsrItcPaidCess(BigDecimal.ZERO);
		summaryResp.setUsrCashPaidInterest(BigDecimal.ZERO);
		summaryResp.setUsrCashPaidLateFee(BigDecimal.ZERO);
		summaryResp.setGstnPayable(BigDecimal.ZERO);
		summaryResp.setGstnOtherPayable(BigDecimal.ZERO);
		summaryResp.setGstnPaid(BigDecimal.ZERO);
		summaryResp.setGstnOtherPaid(BigDecimal.ZERO);
		summaryResp.setGstnLiability(BigDecimal.ZERO);
		summaryResp.setGstnOtherLiability(BigDecimal.ZERO);
		summaryResp.setGstnItcPaidIgst(BigDecimal.ZERO);
		summaryResp.setGstnItcPaidCgst(BigDecimal.ZERO);
		summaryResp.setGstnItcPaidSgst(BigDecimal.ZERO);
		summaryResp.setGstnItcPaidCess(BigDecimal.ZERO);
		summaryResp.setGstnCashPaidInterest(BigDecimal.ZERO);
		summaryResp.setGstnCashPaidLateFee(BigDecimal.ZERO);
		summaryResp.setDiffPayable(BigDecimal.ZERO);
		summaryResp.setDiffOtherPayable(BigDecimal.ZERO);
		summaryResp.setDiffPaid(BigDecimal.ZERO);
		summaryResp.setDiffOtherPaid(BigDecimal.ZERO);
		summaryResp.setDiffLiability(BigDecimal.ZERO);
		summaryResp.setDiffOtherLiability(BigDecimal.ZERO);
		summaryResp.setDiffItcPaidIgst(BigDecimal.ZERO);
		summaryResp.setDiffItcPaidCgst(BigDecimal.ZERO);
		summaryResp.setDiffItcPaidSgst(BigDecimal.ZERO);
		summaryResp.setDiffItcPaidCess(BigDecimal.ZERO);
		summaryResp.setDiffCashPaidInterest(BigDecimal.ZERO);
		summaryResp.setDiffCashPaidLateFee(BigDecimal.ZERO);
		return summaryResp;

}
	
	public RefundSummaryDto ret1Refund8DefaultStructure(
			RefundSummaryDto summaryResp) {

		summaryResp.setUsrTax(BigDecimal.ZERO);
		summaryResp.setUsrTotal(BigDecimal.ZERO);
		summaryResp.setUsrPenality(BigDecimal.ZERO);
		summaryResp.setUsrInterest(BigDecimal.ZERO);
		summaryResp.setUsrFee(BigDecimal.ZERO);
		summaryResp.setUsrOther(BigDecimal.ZERO);
		summaryResp.setGstnTax(BigDecimal.ZERO);
		summaryResp.setGstnTotal(BigDecimal.ZERO);
		summaryResp.setGstnPenality(BigDecimal.ZERO);
		summaryResp.setGstnInterest(BigDecimal.ZERO);
		summaryResp.setGstnOther(BigDecimal.ZERO);
		summaryResp.setGstnFee(BigDecimal.ZERO);
		summaryResp.setDiffTax(BigDecimal.ZERO);
		summaryResp.setDiffTotal(BigDecimal.ZERO);
		summaryResp.setDiffPenality(BigDecimal.ZERO);
		summaryResp.setDiffOther(BigDecimal.ZERO);
		summaryResp.setDiffInterest(BigDecimal.ZERO);
		summaryResp.setDiffFee(BigDecimal.ZERO);
		return summaryResp;

}
	
}
