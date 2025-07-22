package com.ey.advisory.app.data.daos.client.anx2;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetAnx2SummaryDetailsReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetFinalSummaryDetailsResDto;

public interface Anx2GetAnx2SummaryDetailsDao {

Anx2GetFinalSummaryDetailsResDto loadSummaryDetails(
		Anx2GetAnx2SummaryDetailsReqDto anx2GetAnx2SummaryDetailsRequest);

}
