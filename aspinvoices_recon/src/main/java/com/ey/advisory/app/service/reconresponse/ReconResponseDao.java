package com.ey.advisory.app.service.reconresponse;

import java.util.List;

public interface ReconResponseDao {

	List<ErrorResponseDto> getErrorRecords(Long fileId);
	
}
