/**
 * 
 */
package com.ey.advisory.gstr2.initiaterecon;

import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * @author vishal.verma
 *
 */
public interface EWB3WayInitiateReconReportRequestStatusDao {

	public List<BigInteger> getRequestIds(String userName, Long entityId);

	public List<EWB3WayInitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
}


