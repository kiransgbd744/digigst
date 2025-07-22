package com.ey.advisory.app.service.reconresponse;

import java.util.List;

public interface ReconUserResponseValidator {
	
	public void validateAndProcessChunk(List<GetReconResponseValidDto> chunk,
			Long fileId);
	
	public Long getErrorCount();

}
