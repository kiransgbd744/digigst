package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import com.ey.advisory.common.Document;

/**
 * This class represents B2CS data gathered from the customer. GSTN requires us 
 * to send B2CS Data for each tax rate rolled up at a POS and ETIN level. 
 * Instead of getting the rolled-up data at the POS & ETIN level, we drill 
 * down a little bit further to gather data at the POS, ETIN and HSN/SAC code
 * level, for each rate. 
 * 
 * @author Sai.Pakanati
 *
 */
public class B2CSRolledUpDoc extends Document {
	
	private String sgstin;
	
	private String origTaxPeriod;
	
	/**
	 * Inter State or Intra State
	 */
	private String transType;
	
	private String pos;
	
	private String origPos;
	
	private String hsnSac;
	
	/**
	 * UOM associated with HSN/SAC code.
	 */
	private String uom;
	
	private String origUom;
	
	private String origHsnSac;
	
	private String etin;
	
	private String origEtin;
	
	private BigDecimal rate;
	
	private BigDecimal origRate;

	private BigDecimal qty;
	
	private BigDecimal origQty;
	
	private BigDecimal taxableValue;
	
	private BigDecimal origTaxableValue;
	
	private BigDecimal ecomSupplyValue;
	
	private BigDecimal origEcomSupplyValue;
	
	private BigDecimal igstAmount;
	
	private BigDecimal cgstAmount;
	
	private BigDecimal sgstAmount;
	
	private BigDecimal cessAmount;
	
	private BigDecimal totalValue;

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getOrigTaxPeriod() {
		return origTaxPeriod;
	}

	public void setOrigTaxPeriod(String origTaxPeriod) {
		this.origTaxPeriod = origTaxPeriod;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getOrigPos() {
		return origPos;
	}

	public void setOrigPos(String origPos) {
		this.origPos = origPos;
	}

	public String getHsnSac() {
		return hsnSac;
	}

	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getOrigUom() {
		return origUom;
	}

	public void setOrigUom(String origUom) {
		this.origUom = origUom;
	}

	public String getOrigHsnSac() {
		return origHsnSac;
	}

	public void setOrigHsnSac(String origHsnSac) {
		this.origHsnSac = origHsnSac;
	}

	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public String getOrigEtin() {
		return origEtin;
	}

	public void setOrigEtin(String origEtin) {
		this.origEtin = origEtin;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getOrigRate() {
		return origRate;
	}

	public void setOrigRate(BigDecimal origRate) {
		this.origRate = origRate;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public BigDecimal getOrigQty() {
		return origQty;
	}

	public void setOrigQty(BigDecimal origQty) {
		this.origQty = origQty;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getOrigTaxableValue() {
		return origTaxableValue;
	}

	public void setOrigTaxableValue(BigDecimal origTaxableValue) {
		this.origTaxableValue = origTaxableValue;
	}

	public BigDecimal getEcomSupplyValue() {
		return ecomSupplyValue;
	}

	public void setEcomSupplyValue(BigDecimal ecomSupplyValue) {
		this.ecomSupplyValue = ecomSupplyValue;
	}

	public BigDecimal getOrigEcomSupplyValue() {
		return origEcomSupplyValue;
	}

	public void setOrigEcomSupplyValue(BigDecimal origEcomSupplyValue) {
		this.origEcomSupplyValue = origEcomSupplyValue;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	
}
