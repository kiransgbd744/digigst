package com.ey.advisory.app.gstr3b;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

/**
 * 
 * @author Sakshi.jain
 *
 */
public interface Gstr1vs3bRequestStatusService {

	public List<Gstr1Vs3BRequestStatusDto> getRequestIdSummary(
			Gstr2InitiateReconReqDto reqDto, String userName);
	
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId);
}