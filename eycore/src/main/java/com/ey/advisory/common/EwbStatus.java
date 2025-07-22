/**
 * 
 */
package com.ey.advisory.common;

/**
 * @author Laxmi.Salukuti
 *
 */
public enum EwbStatus {

	NOT_APPLICABLE(1), ASP_ERROR(2), ASP_PROCESSED(3), IRN_IN_PROGRESS(
			4), IRN_ERROR(5), IRN_GENERATED(6), IRN_CANCELLED(7),
	CANCELLATION_FAILED(8), EWB_ACTIVE(5),PARTA_GENERATED(4),GENERATION_ERROR(3),
	CANCELLED(6);

	int ewbStatusCode;

	EwbStatus(int ewbStatusCode) {
		this.ewbStatusCode = ewbStatusCode;
	}

	public int getEwbStatusCode() {
		return ewbStatusCode;
	}
}
