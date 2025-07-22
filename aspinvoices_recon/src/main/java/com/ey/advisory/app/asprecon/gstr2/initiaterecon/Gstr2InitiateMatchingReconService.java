package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2InitiateMatchingReconService {

	public String initiatReconcile(List<String> gstins, Long entityId,
			String toTaxPeriod2A, String fromTaxPeriod2A, String toTaxPeriodPR,
			String fromTaxPeriodPR, String toDocDate, String fromDocDate,
			List<String> addlReportsList, String reconType, Boolean mandatoryReports);
}