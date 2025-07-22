package com.ey.advisory.app.service.ims;

import java.util.List;

public interface ImsRequestStatusService {

	public String saveTosttagingTable(ImsActioResponseReqDto reqDto,
			Long fileId, String username,Long BatchId);
	
	public List<ImsRequestStatusRespDto> requestStatusData(Long entityId);

}
