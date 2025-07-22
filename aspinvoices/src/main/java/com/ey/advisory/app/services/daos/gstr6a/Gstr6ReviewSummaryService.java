package com.ey.advisory.app.services.daos.gstr6a;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr6a.Gstr6DistChannelRevSumRespDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6DistChannelRevSumRespItemDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseItemDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryStringResponseDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Gstr6ReviewSummaryService {

	public List<Gstr6ReviewSummaryResponseDto> getGstr6RevSummary(
			final Annexure1SummaryReqDto reqDto);

	public List<Gstr6DistChannelRevSumRespDto> getGstr6DistChannelRevSum(
			final Annexure1SummaryReqDto reqDto);
	
	public List<Gstr6DistChannelRevSumRespItemDto> convertObjToRespDtos(
			Annexure1SummaryReqDto reqDto);
	
	public List<Gstr6ReviewSummaryResponseItemDto> getGstr6InwardRespItem(
			final Annexure1SummaryReqDto reqDto);
	
	public List<Gstr6ReviewSummaryStringResponseDto> getGstr6SectionsSummary(
			List<Gstr6ReviewSummaryResponseDto> respDtos);
		
}
