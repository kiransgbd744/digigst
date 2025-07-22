package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;

public class Anx2ReconResultsSummaryResDto {

	private String Action;

	private String taxPeriod;

	private String myGSTIN;
	
	private String vendorGSTIN;

	private String vendorTradeNmae;

	private int count;

	private BigDecimal elTaxableValue = BigDecimal.ZERO;

	private BigDecimal elIGST = BigDecimal.ZERO;

	private BigDecimal elCGST = BigDecimal.ZERO;

	private BigDecimal elSGST = BigDecimal.ZERO;

	private BigDecimal elCess = BigDecimal.ZERO;

	private BigDecimal avTaxableValue = BigDecimal.ZERO;

	private BigDecimal avIGST = BigDecimal.ZERO;

	private BigDecimal avCGST = BigDecimal.ZERO;

	private BigDecimal avSGST = BigDecimal.ZERO;

	private BigDecimal avCess = BigDecimal.ZERO;

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public String getMyGSTIN() {
		return myGSTIN;
	}

	public void setMyGSTIN(String myGSTIN) {
		this.myGSTIN = myGSTIN;
	}

	public String getVendorTradeNmae() {
		return vendorTradeNmae;
	}

	public void setVendorTradeNmae(String vendorTradeNmae) {
		this.vendorTradeNmae = vendorTradeNmae;
	}

		public BigDecimal getElTaxableValue() {
		return elTaxableValue;
	}

	public void setElTaxableValue(BigDecimal elTaxableValue) {
		this.elTaxableValue = elTaxableValue;
	}

	public BigDecimal getElIGST() {
		return elIGST;
	}

	public void setElIGST(BigDecimal elIGST) {
		this.elIGST = elIGST;
	}

	public BigDecimal getElCGST() {
		return elCGST;
	}

	public void setElCGST(BigDecimal elCGST) {
		this.elCGST = elCGST;
	}

	public BigDecimal getElSGST() {
		return elSGST;
	}

	public void setElSGST(BigDecimal elSGST) {
		this.elSGST = elSGST;
	}

	public BigDecimal getElCess() {
		return elCess;
	}

	public void setElCess(BigDecimal elCess) {
		this.elCess = elCess;
	}

	public BigDecimal getAvTaxableValue() {
		return avTaxableValue;
	}

	public void setAvTaxableValue(BigDecimal avTaxableValue) {
		this.avTaxableValue = avTaxableValue;
	}

	public BigDecimal getAvIGST() {
		return avIGST;
	}

	public void setAvIGST(BigDecimal avIGST) {
		this.avIGST = avIGST;
	}

	public BigDecimal getAvCGST() {
		return avCGST;
	}

	public void setAvCGST(BigDecimal avCGST) {
		this.avCGST = avCGST;
	}

	public BigDecimal getAvSGST() {
		return avSGST;
	}

	public void setAvSGST(BigDecimal avSGST) {
		this.avSGST = avSGST;
	}

	public BigDecimal getAvCess() {
		return avCess;
	}

	public void setAvCess(BigDecimal avCess) {
		this.avCess = avCess;
	}

	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	
	public String getVendorGSTIN() {
		return vendorGSTIN;
	}

	public void setVendorGSTIN(String vendorGSTIN) {
		this.vendorGSTIN = vendorGSTIN;
	}

	@Override
	public String toString() {
		return "Anx2ReconResultsSummaryResDto [Action=" + Action
				+ ", taxPeriod=" + taxPeriod + ", myGSTIN=" + myGSTIN
				+ ", vendorGSTIN=" + vendorGSTIN + ", "
				+ "vendorTradeNmae=" + vendorTradeNmae + ", count="
				+ count + ", elTaxableValue=" + elTaxableValue + ", elIGST="
				+ elIGST + ", elCGST=" + elCGST + ", elSGST=" + elSGST
				+ ", elCess=" + elCess 
				+ ", avTaxableValue=" + avTaxableValue + ", avIGST=" + avIGST
				+ ", avCGST=" + avCGST + " , avSGST=" + avSGST + ", avCess="
				+ avCess + "]";
	}

}
