package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

/**
 * This document represents LineItem Level information for Advance Adjusted
 * Or Received. Actually, line item level information for Advance Adj/Received 
 * is actually the rolled up rate level information. 
 * 
 * @author Sai.Pakanati
 *
 */
public class AdvRecvOrAdvAdjLineItem extends BasicLineItemValues {
	
	protected BigDecimal origRate;
	
	protected BigDecimal origAdvRecvOrAdvAdj;
	
	public BigDecimal getAdvRecvdOrAdvAdj() {
		return this.taxableValue;
	}
	
	public void setAdvRecvdOrAdvAdj(BigDecimal advRecvdOrAdvAdj) {
		this.taxableValue = advRecvdOrAdvAdj;
	}
	
	public BigDecimal getOrigRate() {
		return this.origRate;
	}
	
	public void setOrigRate(BigDecimal origRate) {
		this.origRate = origRate;
	}

	@Override
	public String toString() {
		return "[origRate=" + origRate
				+ ", origAdvRecvOrAdvAdj=" + origAdvRecvOrAdvAdj + ", taxRate="
				+ taxRate + ", igstAmount=" + igstAmount + ", cgstAmount="
				+ cgstAmount + ", sgstAmount=" + sgstAmount
				+ ", cessAmountAdvalorem=" + cessAmountAdvalorem
				+ ", cessAmountSpecific=" + cessAmountSpecific
				+ ", cessRateAdvalorem=" + cessRateAdvalorem
				+ ", cessRateSpecific=" + cessRateSpecific + ", taxableValue="
				+ taxableValue + ", totalAmt=" + totalAmt + "]";
	}
	
}
