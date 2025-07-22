/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnNILErrorDto {

	private String supplyType;
	private String exemptedOutwardAmount;
	private String nilratedOutwardSupplyAmount;
	private String nongstOutwardSupplyAmount;
	private String errorCode;
	private String gstnErrorMessage;
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	private String refId;

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getExemptedOutwardAmount() {
		return exemptedOutwardAmount;
	}

	public void setExemptedOutwardAmount(String exemptedOutwardAmount) {
		this.exemptedOutwardAmount = exemptedOutwardAmount;
	}

	public String getNilratedOutwardSupplyAmount() {
		return nilratedOutwardSupplyAmount;
	}

	public void setNilratedOutwardSupplyAmount(
			String nilratedOutwardSupplyAmount) {
		this.nilratedOutwardSupplyAmount = nilratedOutwardSupplyAmount;
	}

	public String getNongstOutwardSupplyAmount() {
		return nongstOutwardSupplyAmount;
	}

	public void setNongstOutwardSupplyAmount(String nongstOutwardSupplyAmount) {
		this.nongstOutwardSupplyAmount = nongstOutwardSupplyAmount;
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
		return "Gstr1GstnNILErrorDto [supplyType=" + supplyType + ", exemptedOutwardAmount=" + exemptedOutwardAmount
				+ ", nilratedOutwardSupplyAmount=" + nilratedOutwardSupplyAmount + ", nongstOutwardSupplyAmount="
				+ nongstOutwardSupplyAmount + ", errorCode=" + errorCode + ", gstnErrorMessage=" + gstnErrorMessage
				+ ", refId=" + refId + "]";
	}

}
