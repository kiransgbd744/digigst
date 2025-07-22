package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

public interface Gstr2InitiateReconReportRequestStatusForGroupService {
	
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			List<Long> entityId, Gstr2InitiateReconReqDto reqDto);
	
	public List<Gstr2InitiateReconUsernameDto> getgstr2UserNames(List<Long> entityId, String userName);

	public List<Gstr2InitiateReconEmailDto> getgstr2EmailIds(List<Long> entityId, String userName);
	
	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);

}
