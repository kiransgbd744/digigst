/**
 * 
 */
package com.ey.advisory.common;

/**
 * @author Laxmi.Salukuti
 *
 */
public enum EinvEwbJobStatus {

	EWB_ERP_AUTO(1), EWB_ERP_MANUAL(2), EWB_CLOUD_AUTO(3), EWB_CLOUD_MANUAL(
			4), EINV_ERP_AUTO(5), EINV_ERP_MANUAL(
					6), EINV_CLOUD_AUTO(7), EINV_CLOUD_MANUAL(8);

	int einvEwbJobStatusCode;

	EinvEwbJobStatus(int einvEwbJobStatusCode) {
		this.einvEwbJobStatusCode = einvEwbJobStatusCode;
	}

	public int getEinvEwbJobStatusCode() {
		return einvEwbJobStatusCode;
	}

}
