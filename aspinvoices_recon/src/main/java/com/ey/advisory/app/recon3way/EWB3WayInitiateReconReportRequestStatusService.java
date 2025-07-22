package com.ey.advisory.app.recon3way;

import java.util.List;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconEmailDto;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconUsernameDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.gstr2.initiaterecon.EWB3WayInitiateReconReportRequestStatusDto;

/**
 * @author Sakshi.jain
 *
 */
public interface EWB3WayInitiateReconReportRequestStatusService {

	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId);

	public List<EWB3WayInitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);

	public List<Gstr2InitiateReconUsernameDto> getEWB3WayUserNames(Long entityId, String userName);

	public List<Gstr2InitiateReconEmailDto> getgstr2EmailIds(Long entityId, String userName);
}
