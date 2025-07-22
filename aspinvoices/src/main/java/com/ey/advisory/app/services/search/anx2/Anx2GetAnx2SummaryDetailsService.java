package com.ey.advisory.app.services.search.anx2;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetAnx2SummaryDetailsReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetFinalSummaryDetailsResDto;

public interface Anx2GetAnx2SummaryDetailsService {

	Anx2GetFinalSummaryDetailsResDto loadSummaryDetails(
			Anx2GetAnx2SummaryDetailsReqDto anx2GetAnx2SummaryDetailsRequest);

}
