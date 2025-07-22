/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnHsnSummaryTableDto {

	private String serialNumber;
	private String supplierGSTIN;
	private String returnPeriod;
	private String hsn;
	private String description;
	private String uqc;
	private String totalQuantity;
	private String taxableValue;
	private String igstAmount;
	private String cgstAmount;
	private String sGSTUTGSTAmount;
	private String cessAmount;
	private String totalValue;
	private String isFiled;
	private String taxRate;

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

	public String getHsn() {
		return hsn;
	}

	public void setHsn(String hsn) {
		this.hsn = hsn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUqc() {
		return uqc;
	}

	public void setUqc(String uqc) {
		this.uqc = uqc;
	}

	public String getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(String totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(String igstAmount) {
		this.igstAmount = igstAmount;
	}

	public String getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(String cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public String getsGSTUTGSTAmount() {
		return sGSTUTGSTAmount;
	}

	public void setsGSTUTGSTAmount(String sGSTUTGSTAmount) {
		this.sGSTUTGSTAmount = sGSTUTGSTAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(String cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

	public String getIsFiled() {
		return isFiled;
	}

	public void setIsFiled(String isFiled) {
		this.isFiled = isFiled;
	}

	
	
	
	/**
	 * @return the taxRate
	 */
	public String getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gstr1GstnHsnSummaryTableDto [serialNumber=" + serialNumber
				+ ", supplierGSTIN=" + supplierGSTIN + ", returnPeriod="
				+ returnPeriod + ", hsn=" + hsn + ", description=" + description
				+ ", uqc=" + uqc + ", totalQuantity=" + totalQuantity
				+ ", taxableValue=" + taxableValue + ", igstAmount="
				+ igstAmount + ", cgstAmount=" + cgstAmount
				+ ", sGSTUTGSTAmount=" + sGSTUTGSTAmount + ", cessAmount="
				+ cessAmount + ", totalValue=" + totalValue + ", isFiled="
				+ isFiled + ", taxRate=" + taxRate + "]";
	}

}
