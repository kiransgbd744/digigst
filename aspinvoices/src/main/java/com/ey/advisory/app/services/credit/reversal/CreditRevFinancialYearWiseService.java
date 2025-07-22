package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface CreditRevFinancialYearWiseService {

	public String proceCallForFinancialYear(Annexure1SummaryReqDto reqDto);

	public FinancialYearFinalRespDto getFinYearForProceSummary(
			final Annexure1SummaryReqDto reqDto);

	public List<CreditReversalForFinancialYearDto> getCredReviewReversalSummary(
			final Annexure1SummaryReqDto reqDto);

	public List<CreditTurnOverFinancialItemDto> getFinancialYearCredTurnOverPart1(
			final Annexure1SummaryReqDto reqDto);
	
	public List<CreditTurnOverFinancialProcess2Dto> getFinancialYearCredTurnOverPart2(
			final Annexure1SummaryReqDto reqDto);
}
