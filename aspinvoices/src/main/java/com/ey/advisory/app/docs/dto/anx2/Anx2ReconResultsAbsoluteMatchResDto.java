package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;

/**
 * 
 * @author Anand3.M
 *
 */

public class Anx2ReconResultsAbsoluteMatchResDto {

	private Long id;

	private String myGSTIN;

	private String vendorGSTIN;

	private String taxPeriod;

	private String twoAInvoiceKey;

	private BigDecimal anx2taxableValue = BigDecimal.ZERO;

	private BigDecimal anx2igst = BigDecimal.ZERO;

	private BigDecimal anx2cgst = BigDecimal.ZERO;

	private BigDecimal anx2sgst = BigDecimal.ZERO;

	private BigDecimal anx2cess = BigDecimal.ZERO;

	private String prInvoiceKey;

	private BigDecimal prtaxableValue = BigDecimal.ZERO;

	private BigDecimal prigst = BigDecimal.ZERO;

	private BigDecimal prcgst = BigDecimal.ZERO;

	private BigDecimal prsgst = BigDecimal.ZERO;

	private BigDecimal prcess = BigDecimal.ZERO;

	public Anx2ReconResultsAbsoluteMatchResDto() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMyGSTIN() {
		return myGSTIN;
	}

	public void setMyGSTIN(String myGSTIN) {
		this.myGSTIN = myGSTIN;
	}

	public String getVendorGSTIN() {
		return vendorGSTIN;
	}

	public void setVendorGSTIN(String vendorGSTIN) {
		this.vendorGSTIN = vendorGSTIN;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public BigDecimal getAnx2taxableValue() {
		return anx2taxableValue;
	}

	public void setAnx2taxableValue(BigDecimal anx2taxableValue) {
		this.anx2taxableValue = anx2taxableValue;
	}

	public BigDecimal getAnx2igst() {
		return anx2igst;
	}

	public void setAnx2igst(BigDecimal anx2igst) {
		this.anx2igst = anx2igst;
	}

	public BigDecimal getAnx2cgst() {
		return anx2cgst;
	}

	public void setAnx2cgst(BigDecimal anx2cgst) {
		this.anx2cgst = anx2cgst;
	}

	public BigDecimal getAnx2sgst() {
		return anx2sgst;
	}

	public void setAnx2sgst(BigDecimal anx2sgst) {
		this.anx2sgst = anx2sgst;
	}

	public BigDecimal getAnx2cess() {
		return anx2cess;
	}

	public void setAnx2cess(BigDecimal anx2cess) {
		this.anx2cess = anx2cess;
	}

	public BigDecimal getPrtaxableValue() {
		return prtaxableValue;
	}

	public void setPrtaxableValue(BigDecimal prtaxableValue) {
		this.prtaxableValue = prtaxableValue;
	}

	public BigDecimal getPrigst() {
		return prigst;
	}

	public void setPrigst(BigDecimal prigst) {
		this.prigst = prigst;
	}

	public BigDecimal getPrcgst() {
		return prcgst;
	}

	public void setPrcgst(BigDecimal prcgst) {
		this.prcgst = prcgst;
	}

	public BigDecimal getPrsgst() {
		return prsgst;
	}

	public void setPrsgst(BigDecimal prsgst) {
		this.prsgst = prsgst;
	}

	public BigDecimal getPrcess() {
		return prcess;
	}

	public void setPrcess(BigDecimal prcess) {
		this.prcess = prcess;
	}

	public String getTwoAInvoiceKey() {
		return twoAInvoiceKey;
	}

	public void setTwoAInvoiceKey(String twoAInvoiceKey) {
		this.twoAInvoiceKey = twoAInvoiceKey;
	}

	public String getPrInvoiceKey() {
		return prInvoiceKey;
	}

	public void setPrInvoiceKey(String prInvoiceKey) {
		this.prInvoiceKey = prInvoiceKey;
	}

	@Override
	public String toString() {
		return "Anx2ReconResultsAbsoluteMatchResDto [id=" + id + ", myGSTIN="
				+ myGSTIN + ", vendorGSTIN=" + vendorGSTIN + ", taxPeriod="
				+ taxPeriod + ", twoAInvoiceKey=" + twoAInvoiceKey
				+ ", anx2taxableValue=" + anx2taxableValue + ", anx2igst="
				+ anx2igst + ", anx2cgst=" + anx2cgst + ", anx2sgst=" + anx2sgst
				+ ", anx2cess=" + anx2cess + ", prInvoiceKey=" + prInvoiceKey
				+ ", prtaxableValue=" + prtaxableValue + ", prigst=" + prigst
				+ ", prcgst=" + prcgst + ", prsgst=" + prsgst + ", prcess="
				+ prcess + "]";
	}

}
