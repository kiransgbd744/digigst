/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnInvoiceSeriesTableDto {

	private String serialNumber;
	private String supplierGSTIN;
	private String returnPeriod;
	private String documentType;
	private String fromserialNumber;
	private String toserialNumber;
	private String totalNumber;
	private String cancelled;
	private String netIssued;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getFromserialNumber() {
		return fromserialNumber;
	}

	public void setFromserialNumber(String fromserialNumber) {
		this.fromserialNumber = fromserialNumber;
	}

	public String getToserialNumber() {
		return toserialNumber;
	}

	public void setToserialNumber(String toserialNumber) {
		this.toserialNumber = toserialNumber;
	}

	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		this.cancelled = cancelled;
	}

	public String getNetIssued() {
		return netIssued;
	}

	public void setNetIssued(String netIssued) {
		this.netIssued = netIssued;
	}

	@Override
	public String toString() {
		return "Gstr1GstnInvoiceSeriesTableDto [serialNumber=" + serialNumber
				+ ", supplierGSTIN=" + supplierGSTIN + ", returnPeriod="
				+ returnPeriod + ", documentType=" + documentType
				+ ", fromserialNumber=" + fromserialNumber + ", toserialNumber="
				+ toserialNumber + ", totalNumber=" + totalNumber
				+ ", cancelled=" + cancelled + ", netIssued=" + netIssued + "]";
	}

}
