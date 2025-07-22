package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1VerticalSummaryRespDto;

public interface Ret1VerticalSectionDao {

	public abstract List<Ret1VerticalSummaryRespDto> lateBasicSummarySection(
			Ret1SummaryReqDto req);
	public abstract List<Ret1VerticalSummaryRespDto> lateBasicSummarySectionRet1A(
			Ret1SummaryReqDto req);
}
