package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;


public interface Ret1AspVerticalSectionDao {

	public abstract List<Ret1AspVerticalSummaryDto> lateBasicSummarySection(
			Ret1SummaryReqDto req);
	
	public abstract List<Ret1AspVerticalSummaryDto> lateBasicSummarySectionRet1A(
			Ret1SummaryReqDto req);

}
