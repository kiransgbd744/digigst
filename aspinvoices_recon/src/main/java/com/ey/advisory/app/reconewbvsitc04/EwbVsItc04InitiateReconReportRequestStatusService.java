package com.ey.advisory.app.reconewbvsitc04;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

/**
 * @author Ravindra V S
 *
 */
public interface EwbVsItc04InitiateReconReportRequestStatusService {

	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId);

	public List<EwbVsItc04InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
}
