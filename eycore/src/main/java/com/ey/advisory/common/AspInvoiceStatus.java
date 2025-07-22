/**
 * 
 */
package com.ey.advisory.common;

/**
 * @author Laxmi.Salukuti
 *
 */
public enum AspInvoiceStatus {

	ASP_ERROR(1), ASP_PROCESSED(2);

	int aspInvoiceStatusCode;

	AspInvoiceStatus(int aspInvoiceStatusCode) {
		this.aspInvoiceStatusCode = aspInvoiceStatusCode;
	}

	public int getAspInvoiceStatusCode() {
		return aspInvoiceStatusCode;
	}

}
