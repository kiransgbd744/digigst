/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2InitiateReconReportRequestStatusDao {
	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestStatus(
			String userName, Long entityId);

	public List<BigInteger> getRequestIds(String userName, Long entityId, 
			Gstr2InitiateReconReqDto reqDto);

	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
	
	public List<String> getUserIds(String userName, Long entityId, 
			Gstr2InitiateReconReqDto reqDto);
	
	public List<String> getUserEmailIds(String userName, Long entityId, 
			Gstr2InitiateReconReqDto reqDto);
}


