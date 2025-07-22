/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface SalesRegisterRequestStatusDao {

	public List<BigInteger> getRequestIds(String userName, Long entityId);

	public List<Gstr1SalesRegisterRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
}


