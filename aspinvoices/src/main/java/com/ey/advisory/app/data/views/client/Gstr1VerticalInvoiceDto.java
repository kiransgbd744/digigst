/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Gstr1VerticalInvoiceDto {

	private String supplierGSTIN;
	private String returnPeriod;
	private String serialNo;
	private String natureOfDocument;
	private String from;
	private String to;
	private String totalNumber;
	private String cancelled;
	private String netNumber;
	private String aspErrorCode;
	private String aspErrorDescription;
	private String aspInformationID;
	private String aspInformationDescription;
	

	public String getAspInformationID() {
		return aspInformationID;
	}

	public void setAspInformationID(String aspInformationID) {
		this.aspInformationID = aspInformationID;
	}

	public String getAspInformationDescription() {
		return aspInformationDescription;
	}

	public void setAspInformationDescription(String aspInformationDescription) {
		this.aspInformationDescription = aspInformationDescription;
	}

	/**
	 * @return the supplierGSTIN
	 */
	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	/**
	 * @param supplierGSTIN
	 *            the supplierGSTIN to set
	 */
	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}

	/**
	 * @param returnPeriod
	 *            the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	/**
	 * @return the serialNo
	 */
	public String getSerialNo() {
		return serialNo;
	}

	/**
	 * @param serialNo
	 *            the serialNo to set
	 */
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	/**
	 * @return the natureOfDocument
	 */
	public String getNatureOfDocument() {
		return natureOfDocument;
	}

	/**
	 * @param natureOfDocument
	 *            the natureOfDocument to set
	 */
	public void setNatureOfDocument(String natureOfDocument) {
		this.natureOfDocument = natureOfDocument;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the totalNumber
	 */
	public String getTotalNumber() {
		return totalNumber;
	}

	/**
	 * @param totalNumber
	 *            the totalNumber to set
	 */
	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	/**
	 * @return the cancelled
	 */
	public String getCancelled() {
		return cancelled;
	}

	/**
	 * @param cancelled
	 *            the cancelled to set
	 */
	public void setCancelled(String cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * @return the netNumber
	 */
	public String getNetNumber() {
		return netNumber;
	}

	/**
	 * @param netNumber
	 *            the netNumber to set
	 */
	public void setNetNumber(String netNumber) {
		this.netNumber = netNumber;
	}

	/**
	 * @return the aspErrorCode
	 */
	public String getAspErrorCode() {
		return aspErrorCode;
	}

	/**
	 * @param aspErrorCode
	 *            the aspErrorCode to set
	 */
	public void setAspErrorCode(String aspErrorCode) {
		this.aspErrorCode = aspErrorCode;
	}

	/**
	 * @return the aspErrorDescription
	 */
	public String getAspErrorDescription() {
		return aspErrorDescription;
	}

	/**
	 * @param aspErrorDescription
	 *            the aspErrorDescription to set
	 */
	public void setAspErrorDescription(String aspErrorDescription) {
		this.aspErrorDescription = aspErrorDescription;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gstr1VerticalInvoiceDto [supplierGSTIN=" + supplierGSTIN
				+ ", returnPeriod=" + returnPeriod + ", serialNo=" + serialNo
				+ ", natureOfDocument=" + natureOfDocument + ", from=" + from
				+ ", to=" + to + ", totalNumber=" + totalNumber + ", cancelled="
				+ cancelled + ", netNumber=" + netNumber + ", aspErrorCode="
				+ aspErrorCode + ", aspErrorDescription=" + aspErrorDescription
				+ ", aspInformationID=" + aspInformationID
				+ ", aspInformationDescription=" + aspInformationDescription
				+ "]";
	}

	
	}


