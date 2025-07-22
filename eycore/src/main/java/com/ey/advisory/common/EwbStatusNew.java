/**
 * 
 */
package com.ey.advisory.common;

/**
 * @author Laxmi.Salukuti
 *
 */
public enum EwbStatusNew {

	NOT_APPLICABLE(1), PENDING(2),GENERATION_ERROR(3),PART_A_GENERATED(4),
	EWB_ACTIVE(5),CANCELLED(6),DISCARDED(7),REJECTED(8),EXPIRED(9),PUSHED_TO_NIC(10),
	ASP_ERROR(11),NOT_OPTED(12),ALREADY_GENERATED_BY_USER(13),PENDING_ERROR(14),
	EWAY_BILL_GENERATED_ERP(23),EWAY_BILL_NOT_GENERATED_ERP(24);

	int ewbNewStatusCode;

	EwbStatusNew(int ewbNewStatusCode) {
		this.ewbNewStatusCode = ewbNewStatusCode;
	}

	public int getEwbNewStatusCode() {
		return ewbNewStatusCode;
	}

}
