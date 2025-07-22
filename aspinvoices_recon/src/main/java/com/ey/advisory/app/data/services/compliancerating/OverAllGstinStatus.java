package com.ey.advisory.app.data.services.compliancerating;

public enum OverAllGstinStatus {
	COMPLIANT("FILED"), NOT_COMPLIANT("NOT FILED"), UNKNOWN("-");

	private String status;

	private OverAllGstinStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}