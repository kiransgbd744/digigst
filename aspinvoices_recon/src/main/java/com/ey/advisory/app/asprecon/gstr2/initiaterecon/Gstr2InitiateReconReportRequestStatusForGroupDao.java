package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;

public interface Gstr2InitiateReconReportRequestStatusForGroupDao {
	
	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
	

	public List<BigInteger> getRequestIds(String userName, List<Long> entityId, 
			Gstr2InitiateReconReqDto reqDto);

}
