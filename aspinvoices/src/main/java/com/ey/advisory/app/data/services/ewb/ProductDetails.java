package com.ey.advisory.app.data.services.ewb;

import java.math.BigDecimal;

public class ProductDetails {

	
	String hsn;
	String descripition;
	String quantity;
	BigDecimal taxableValue;
	String taxRate;
	
	
	public String getDescripition() {
		return descripition;
	}
	public void setDescripition(String descripition) {
		this.descripition = descripition;
	}
	public String getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}
	public String getHsn() {
		return hsn;
	}
	public void setHsn(String hsn) {
		this.hsn = hsn;
	}
	public String getDescription() {
		return descripition;
	}
	public void setDescription(String description) {
		this.descripition = description;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}
	
	
}


