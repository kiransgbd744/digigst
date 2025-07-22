package com.ey.advisory.app.data.services.Gstr1A;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1VerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

public interface Gstr1AAspVerticalSectionDao {

	public abstract List<Ret1AspVerticalSummaryDto> lateBasicSummarySection(
			Annexure1SummaryReqDto req);

	public abstract List<Ret1AspVerticalSummaryDto> gstnBasicSummarySection(
			Annexure1SummaryReqDto req);
	
	public List<Gstr1VerticalSummaryRespDto> verticalBasicSummarySection(
			Annexure1SummaryReqDto req);

}
