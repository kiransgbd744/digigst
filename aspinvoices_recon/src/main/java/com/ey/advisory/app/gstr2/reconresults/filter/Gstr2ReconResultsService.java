package com.ey.advisory.app.gstr2.reconresults.filter;

import java.util.List;

public interface Gstr2ReconResultsService {

	public String reconResponseUpload(Gstr2ReconResultsReqDto reqDto,
			Long fileId, String username,Long BatchId, String identifier);
	
	public List<Gstr2ReconResultsRequestStatusRespDto> requestStatusData(Long entityId, String identifier);

}
