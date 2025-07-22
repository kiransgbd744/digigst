package com.ey.advisory.app.dashboard.mergefiles;

public interface APICallDashboardFileNameCreator {

	public String createFileName(String returnType, String gstin,
			String taxPeriod, String existingFileName);
}
