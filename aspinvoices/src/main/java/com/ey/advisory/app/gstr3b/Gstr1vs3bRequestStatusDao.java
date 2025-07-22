package com.ey.advisory.app.gstr3b;

import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

/**
 * 
 * @author Sakshi.jain
 *
 */
public interface Gstr1vs3bRequestStatusDao {
	
	public List<BigInteger> getRequestIds(String userName, Long entityId);
	
	public List<Gstr1Vs3BRequestStatusDto> getRequestIdSummaryData(
			Gstr2InitiateReconReqDto reqDto, String userName);
}