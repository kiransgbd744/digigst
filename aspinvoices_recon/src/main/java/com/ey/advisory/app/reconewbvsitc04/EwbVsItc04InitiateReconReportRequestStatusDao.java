/**
 * 
 */
package com.ey.advisory.app.reconewbvsitc04;

import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * @author Ravindra V S
 *
 */
public interface EwbVsItc04InitiateReconReportRequestStatusDao {

	public List<BigInteger> getRequestIds(String userName, Long entityId);

	public List<EwbVsItc04InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
}


