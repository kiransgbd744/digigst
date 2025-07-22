/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnnilnonexemptSummarylevelDto {

	private String serialNo;
	private String supplierGSTIN;
	private String returnPeriod;
	private String supplyType;
	private String nilAmt;
	private String exemptedAmt;
	private String nonGstSupAmt;
	private String isFiled;

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
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

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getNilAmt() {
		return nilAmt;
	}

	public void setNilAmt(String nilAmt) {
		this.nilAmt = nilAmt;
	}

	public String getExemptedAmt() {
		return exemptedAmt;
	}

	public void setExemptedAmt(String exemptedAmt) {
		this.exemptedAmt = exemptedAmt;
	}

	public String getNonGstSupAmt() {
		return nonGstSupAmt;
	}

	public void setNonGstSupAmt(String nonGstSupAmt) {
		this.nonGstSupAmt = nonGstSupAmt;
	}

	public String getIsFiled() {
		return isFiled;
	}

	public void setIsFiled(String isFiled) {
		this.isFiled = isFiled;
	}

	@Override
	public String toString() {
		return "Gstr1GstnnilnonexemptSummarylevelDto [serialNo=" + serialNo
				+ ", supplierGSTIN=" + supplierGSTIN + ", returnPeriod="
				+ returnPeriod + ", supplyType=" + supplyType + ", nilAmt="
				+ nilAmt + ", exemptedAmt=" + exemptedAmt + ", nonGstSupAmt="
				+ nonGstSupAmt + ", isFiled=" + isFiled + "]";
	}

}
