package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

/**
 * This class represents all the item level/rolled up document information
 * that can be amended.
 * 
 * @author Sai.pakanati
 *
 */
public class BasicAmendmentLineItemValues extends BasicLineItemValues {
	
	private BigDecimal origTaxRate;
	
	private BigDecimal origTaxableValue;
	
	private BigDecimal origQty;
	
	public BasicAmendmentLineItemValues() {}
	
	/**
	 * Copy constructor.
	 * 
	 * @param values
	 */
	public BasicAmendmentLineItemValues(BasicAmendmentLineItemValues values) {
		super(values); // Invoke the copy constructor of the base class.
		this.origTaxRate = values.taxRate;
		this.origTaxableValue = values.taxableValue;
		this.origQty = values.origQty;
	}

	public BigDecimal getOrigTaxRate() {
		return origTaxRate;
	}

	public void setOrigTaxRate(BigDecimal origTaxRate) {
		this.origTaxRate = origTaxRate;
	}

	public BigDecimal getOrigTaxableValue() {
		return origTaxableValue;
	}

	public void setOrigTaxableValue(BigDecimal origTaxableValue) {
		this.origTaxableValue = origTaxableValue;
	}

	public BigDecimal getOrigQty() {
		return origQty;
	}

	public void setOrigQty(BigDecimal origQty) {
		this.origQty = origQty;
	}

	@Override
	public String toString() {
		return "origTaxRate=" + origTaxRate
				+ ", origTaxableValue=" + origTaxableValue + ", origQty="
				+ origQty;
	}
	
}
