package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface CreditReversalProcessService {

	public String proceCallForComputeReversal(
			final Annexure1SummaryReqDto reqDto);

	public List<CreditReversalProcessDto> getCredRevrProcess(
			final Annexure1SummaryReqDto reqDto);

	public List<CreditReversalDto> getCredReversal(
			final Annexure1SummaryReqDto reqDto);

	public CreditReveSummaryDto getCredRevSummary(
			final Annexure1SummaryReqDto reqDto);

	public List<CreditTurnOverDto> getCredTurnOverPartA(
			final Annexure1SummaryReqDto reqDto);

	public List<CreditTurnOverDto> getCredTurnOverPartB(
			final Annexure1SummaryReqDto reqDto);
	
	public String pushToGstr3BCredRevRatio(final Annexure1SummaryReqDto reqDto);
}
