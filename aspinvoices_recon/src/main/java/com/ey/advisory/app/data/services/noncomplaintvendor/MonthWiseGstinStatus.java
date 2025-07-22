package com.ey.advisory.app.data.services.noncomplaintvendor;

public enum MonthWiseGstinStatus {
	FILED("Filed"), NOT_FILED("Not Filed"), UNKNOWN("-");

	private String status;

	private MonthWiseGstinStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
