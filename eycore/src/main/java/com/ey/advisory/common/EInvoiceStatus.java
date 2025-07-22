package com.ey.advisory.common;

/**
 * @author Laxmi.Salukuti
 *
 */

public enum EInvoiceStatus {

	NOT_OPTED(1), NOT_APPLICABLE(2), NOT_APPLICABLE_M(3), PENDING(
			4), IRN_IN_PROGRESS(5), ASP_ERROR(6), ASP_PROCESSED(7), IRN_ERROR(
					8), IRN_CANCELLED(9), IRN_GENERATED(
							10), IRN_GENERATED_IN_ERP(
									11), IRN_NOT_GENERATED_IN_ERP(
											12), ERROR_CANCELLATION(
													13), DUPLICATE_IRN(14);
	int eInvoiceStatusCode;

	EInvoiceStatus(int eInvoiceStatusCode) {
		this.eInvoiceStatusCode = eInvoiceStatusCode;
	}

	public int geteInvoiceStatusCode() {
		return eInvoiceStatusCode;
	}

}
