package com.ey.advisory.app.data.services.noncomplaintvendor;

public enum OverAllPanStatus {
	COMPLIANT("COMPLIANT"), NOT_COMPLIANT("NOT_COMPLIANT"), PARTIALLY_COMPLIANT(
			"PARTIALLY_COMPLIANT"), UNKNOWN("-");

	private String status;

	private OverAllPanStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
