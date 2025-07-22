package com.ey.advisory.app.reconewbvsitc04;

import java.util.List;



/**
 * @author Ravindra V S
 *
 */
public interface EwbVsItc04InitiateReconService {

	public String initiatReconcile(List<String> gstins, Long entityId,
			String fromTaxPeriod, String toTaxPeriod, String fy, String criteria, List<String> addReport);
}