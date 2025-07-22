/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2InitiateReconReportRequestStatusService {

	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestStatus(
			String userName, Long entityId);

	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId, Gstr2InitiateReconReqDto reqDto);

	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);

	public List<Gstr2InitiateReconUsernameDto> getgstr2UserNames(Long entityId, String userName, String screenName);

	public List<Gstr2InitiateReconEmailDto> getgstr2EmailIds(Long entityId, String userName);
	
	public List<Gstr2InitiateReconEmailDto> getgstr2EmailIds(Long entityId, String userName,String Username);
	
	public List<Gstr2InitiateReconRequestIdsDto> getUserId(String userName,
			Long entityId, Gstr2InitiateReconReqDto reqDto);

	List<Gstr2InitiateReconRequestIdsDto> getinitiatedByUserEmailIds(
			String userName, Long entityId, Gstr2InitiateReconReqDto reqDto);
}
