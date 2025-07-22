package com.ey.advisory.service.gstr1.sales.register;

import java.util.List;

/**
 * @author Shashikant.Shukla
 *
 */
public interface SalesRegisterInitiateReconService {

	public String initiatReconcile(List<String> gstins, Long entityId,
			String fromReturnPeriod, String toReturnPeriod, String criteria);
}