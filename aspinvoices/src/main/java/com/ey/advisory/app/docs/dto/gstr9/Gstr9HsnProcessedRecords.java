package com.ey.advisory.app.docs.dto.gstr9;

/**
 * 
 * @author Anand3.M
 *
 */

public class Gstr9HsnProcessedRecords {
	private String gstin;
	private String fy;
	private String tableNumber;
	private String hsn;
	private String description;
	private String rateofTax;
	private String uqc;
	private String totalQuantity;
	private String taxableValue;
	private String concessionalRateFlag;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getFy() {
		return fy;
	}

	public void setFy(String fy) {
		this.fy = fy;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
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

	public String getRateofTax() {
		return rateofTax;
	}

	public void setRateofTax(String rateofTax) {
		this.rateofTax = rateofTax;
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

	public String getConcessionalRateFlag() {
		return concessionalRateFlag;
	}

	public void setConcessionalRateFlag(String concessionalRateFlag) {
		this.concessionalRateFlag = concessionalRateFlag;
	}

	public String getIgst() {
		return igst;
	}

	public void setIgst(String igst) {
		this.igst = igst;
	}

	public String getCgst() {
		return cgst;
	}

	public void setCgst(String cgst) {
		this.cgst = cgst;
	}

	public String getSgst() {
		return sgst;
	}

	public void setSgst(String sgst) {
		this.sgst = sgst;
	}

	public String getCess() {
		return cess;
	}

	public void setCess(String cess) {
		this.cess = cess;
	}

	@Override
	public String toString() {
		return "Gstr9HsnProcessedRecords [gstin=" + gstin + ", fy=" + fy
				+ ", tableNumber=" + tableNumber + ", hsn=" + hsn
				+ ", description=" + description + ", rateofTax=" + rateofTax
				+ ", uqc=" + uqc + ", totalQuantity=" + totalQuantity
				+ ", taxableValue=" + taxableValue + ", concessionalRateFlag="
				+ concessionalRateFlag + ", igst=" + igst + ", cgst=" + cgst
				+ ", sgst=" + sgst + ", cess=" + cess + "]";
	}

}
