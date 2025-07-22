package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Ret1PaymentTaxDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1RefundDetailSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Ret1PaymentTaxDetailSectionDao {

	
	public abstract List<Ret1PaymentTaxDetailSummaryDto> loadPaymentSummarySection(
			Annexure1SummaryReqDto req);
	
	public abstract List<Ret1RefundDetailSummaryDto> loadRefundSummarySection(
			Annexure1SummaryReqDto req);
	
	public abstract List<Ret1PaymentTaxDetailSummaryDto> loadPaymentSummarySectionrRet1A(
			Annexure1SummaryReqDto req);
}
