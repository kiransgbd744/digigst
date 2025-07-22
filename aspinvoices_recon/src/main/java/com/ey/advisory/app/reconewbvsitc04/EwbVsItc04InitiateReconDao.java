package com.ey.advisory.app.reconewbvsitc04;

import java.util.List;

public interface EwbVsItc04InitiateReconDao {

	public String createReconcileData(List<String> gstins, Long entityId,
			String fromTaxPeriod, String toTaxPeriod, String fy,
			String criteria, List<String> addReport);

}
