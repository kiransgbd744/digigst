package com.ey.advisory.app.recon3way;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface EWB3WayInitiateReconService {

	public String initiatReconcile(List<String> gstins, Long entityId,
			String fromReturnPeriod, String toReturnPeriod, String criteria,
			String gstr1Type, String eInvType, String gewbType,List<String> addReport);
}