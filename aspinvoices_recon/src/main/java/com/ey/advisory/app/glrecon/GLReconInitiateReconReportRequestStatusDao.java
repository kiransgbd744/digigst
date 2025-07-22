/**
 * 
 */
package com.ey.advisory.app.glrecon;

import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * @author Sakshi.jain
 *
 */
public interface GLReconInitiateReconReportRequestStatusDao {
	
	public List<BigInteger> getRequestIds(String userName, Long entityId, 
			Gstr2InitiateReconReqDto reqDto);

	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
}


