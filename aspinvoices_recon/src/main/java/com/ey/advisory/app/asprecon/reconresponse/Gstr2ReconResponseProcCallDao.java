package com.ey.advisory.app.asprecon.reconresponse;

import java.util.List;

public interface Gstr2ReconResponseProcCallDao {

	public List<Gstr2ReconResponseButtonReqDto> validateReconResponse(Long batchId, String reconType,
			Long fileId);
}
