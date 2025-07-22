package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.RefundSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.TaxPaymentSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Ret1ReviewSummaryScreenDownloadDao {
	List<Ret1SummaryRespDto> getRet1ReviewSummScreenDownload(
			Annexure1SummaryReqDto request);

	List<Ret1LateFeeSummaryDto> getRet1ReviewSummaryInterestAndLateFeeDao(
			Annexure1SummaryReqDto request);

	List<TaxPaymentSummaryDto> getReviewSummTaxPaymentDownload(
			Annexure1SummaryReqDto request);

	List<RefundSummaryDto> getReviewSummRefundDownload(
			Annexure1SummaryReqDto request);
}
