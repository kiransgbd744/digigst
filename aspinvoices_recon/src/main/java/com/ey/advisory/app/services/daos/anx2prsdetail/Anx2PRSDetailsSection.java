package com.ey.advisory.app.services.daos.anx2prsdetail;

import java.util.List;

public interface Anx2PRSDetailsSection {

	public Anx2PRSDetailHeaderDto getAnx2PRSummaryDetail(
			List<Anx2PRSDetailResponseDto> entityResponse,List<String> docType);

}
