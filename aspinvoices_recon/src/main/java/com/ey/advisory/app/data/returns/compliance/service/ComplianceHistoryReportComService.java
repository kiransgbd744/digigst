/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

/**
 * @author Sujith.Nanga
 *
 */
public interface ComplianceHistoryReportComService {

	void createEntryCompClientGstin(Long requestId, String Gstin,
			String returnType);

	Long createEntryComplainceComReq(Long gstinList, String financialYear,
			Long entityId);

}
