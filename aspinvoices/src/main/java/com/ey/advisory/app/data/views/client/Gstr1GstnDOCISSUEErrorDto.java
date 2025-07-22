/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnDOCISSUEErrorDto {

	private String serialNumber;
	private String fromserialNumber;
	private String toserialNumber;
	private String totalNumber;
	private String cancelled;
	private String netIssued;
	private String errorCode;
	private String gstnErrorMessage;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getGstnErrorMessage() {
		return gstnErrorMessage;
	}

	public void setGstnErrorMessage(String gstnErrorMessage) {
		this.gstnErrorMessage = gstnErrorMessage;
	}

	@Override
	public String toString() {
		return "Gstr1GstnDOCISSUEErrorDto [serialNumber=" + serialNumber
				+ ", fromserialNumber=" + fromserialNumber + ", toserialNumber="
				+ toserialNumber + ", totalNumber=" + totalNumber
				+ ", cancelled=" + cancelled + ", netIssued=" + netIssued
				+ ", errorCode=" + errorCode + ", gstnErrorMessage="
				+ gstnErrorMessage + "]";
	}

}
