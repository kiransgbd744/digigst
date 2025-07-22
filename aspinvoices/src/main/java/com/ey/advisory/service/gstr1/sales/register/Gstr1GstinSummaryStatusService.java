package com.ey.advisory.service.gstr1.sales.register;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;
/**
 * 
 * @author Shashikant.Shukla
 *
 */

public interface Gstr1GstinSummaryStatusService {

	public List<Gstr2ReconSummaryStatusDto> getSalesRegisterGstinSummaryStatus(
			Long entityId, String fromReturnPeriod, 
			String toReturnPeriod, String criteria);
}
