/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1SalesRegisterSummaryStatusDao {

	public List<Gstr2ReconSummaryStatusDto> findsalesRegisterGstinStatus(
			Long entityId, String fromReturnPeriod, String toReturnPeriod,
			String criteria);

}
