/**
 * 
 */
package com.ey.advisory.app.glrecon;

import java.util.List;

import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

/**
 * @author Sakshi.jain
 *
 */
public interface GLReconInitiateReconReportRequestStatusService {

	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId, Gstr2InitiateReconReqDto reqDto);

	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);

	}
