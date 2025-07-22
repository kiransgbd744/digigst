package com.ey.advisory.app.anx2.initiaterecon;

import java.util.List;

public interface ReconResponseDataStatusService {

	public List<ReconResponseDataStatusDto> getReconResponseDataStatus(
			ReconResponseDataStatusReqDto req);

}
