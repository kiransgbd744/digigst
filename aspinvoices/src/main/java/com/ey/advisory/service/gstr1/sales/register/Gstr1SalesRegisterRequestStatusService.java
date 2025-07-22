package com.ey.advisory.service.gstr1.sales.register;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1SalesRegisterRequestStatusService {

	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId);

	public List<Gstr1SalesRegisterRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName);
}
