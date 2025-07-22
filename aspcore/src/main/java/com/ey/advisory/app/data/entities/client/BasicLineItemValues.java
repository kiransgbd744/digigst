package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * This class represents the basic line item level values (or facts) that can
 * be rolled up to different levels. This can be used to represent a single
 * document line item data or the data of several documents rolled up by one or
 * more dimensions.
 * 
 * The total amount can be used to represent amounts like invoice amount,
 * credit note amount, advance received amount, advance adjusted amount etc.
 * 
 * Rates are the only values that remain the same when rolled up. Other than
 * rates, all values will get summed up.
 * 
 * Not marking this class as abstract, as an instance of this class can be used 
 * to represent the rolled up information.
 * 
 * @author Sai.Pakanati
 *
 */
@MappedSuperclass
public class BasicLineItemValues {

	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;
	
	@Expose
	@SerializedName("igstRt")
	@Column(name = "IGST_RATE")
	protected BigDecimal igstRate;
		
	@Expose
	@SerializedName("cgstRt")
	@Column(name = "CGST_RATE")
	protected BigDecimal cgstRate;
	
	@Expose
	@SerializedName("sgstRt")
	@Column(name = "SGST_RATE")
	protected BigDecimal sgstRate;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmount;

	@Expose
	@SerializedName("cessAmtAdvalorem")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected BigDecimal cessAmountAdvalorem;

	@Expose
	@SerializedName("cessAmtSpecfic")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected BigDecimal cessAmountSpecific;

	@Expose
	@SerializedName("cessRtAdvalorem")
	@Column(name = "CESS_RATE_ADVALOREM")
	protected BigDecimal cessRateAdvalorem;

	@Expose
	@SerializedName("cessRtSpecific")
	@Column(name = "CESS_RATE_SPECIFIC")
	protected BigDecimal cessRateSpecific;
	
	@Expose
	@SerializedName("stateCessRt")
	@Column(name = "STATECESS_RATE")
	protected BigDecimal stateCessRate;
	
	@Expose
	@SerializedName("stateCessAmt")
	@Column(name = "STATECESS_AMT")
	protected BigDecimal stateCessAmount;
	
	@Expose
	@SerializedName("otherValues")
	@Column(name = "OTHER_VALUES")
	protected BigDecimal otherValues;
		
	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableValue;	
	
	
	
	/**
	 * This field can be used to represent amounts like invoice amount,
	 * credit note amount, advance received amount, advance adjusted 
	 * amount etc.
	 */
	@Transient
	protected BigDecimal totalAmt;
		
	public BasicLineItemValues() {}
	
	/**
	 * Copy constructor to clone an object already available
	 * 
	 * @param sourceObj
	 */
	public BasicLineItemValues(BasicLineItemValues sourceObj) {
		this.taxRate = sourceObj.taxRate;
		this.igstRate = sourceObj.igstRate;
		this.sgstRate = sourceObj.sgstRate;
		this.cgstRate = sourceObj.cgstRate;
		this.igstAmount = sourceObj.igstAmount;
		this.cgstAmount= sourceObj.cgstAmount;
		this.sgstAmount =  sourceObj.sgstAmount;
		this.cessAmountAdvalorem = sourceObj.cessAmountAdvalorem;
		this.cessAmountSpecific = sourceObj.cessAmountSpecific;
		this.cessRateAdvalorem = sourceObj.cessRateAdvalorem;
		this.cessRateSpecific = sourceObj.cessRateSpecific;
		this.taxableValue = sourceObj.taxableValue;
		this.totalAmt = sourceObj.totalAmt;		
	}

	/**
	 * @return the taxRate
	 */
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	/**
	 * @return the igstAmount
	 */
	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	/**
	 * @param igstAmount the igstAmount to set
	 */
	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	/**
	 * @return the cgstAmount
	 */
	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	/**
	 * @param cgstAmount the cgstAmount to set
	 */
	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	/**
	 * @return the sgstAmount
	 */
	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	/**
	 * @param sgstAmount the sgstAmount to set
	 */
	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	/**
	 * @return the cessAmountAdvalorem
	 */
	public BigDecimal getCessAmountAdvalorem() {
		return cessAmountAdvalorem;
	}

	/**
	 * @param cessAmountAdvalorem the cessAmountAdvalorem to set
	 */
	public void setCessAmountAdvalorem(BigDecimal cessAmountAdvalorem) {
		this.cessAmountAdvalorem = cessAmountAdvalorem;
	}

	/**
	 * @return the cessAmountSpecific
	 */
	public BigDecimal getCessAmountSpecific() {
		return cessAmountSpecific;
	}

	/**
	 * @param cessAmountSpecific the cessAmountSpecific to set
	 */
	public void setCessAmountSpecific(BigDecimal cessAmountSpecific) {
		this.cessAmountSpecific = cessAmountSpecific;
	}

	/**
	 * @return the cessRateAdvalorem
	 */
	public BigDecimal getCessRateAdvalorem() {
		return cessRateAdvalorem;
	}

	/**
	 * @param cessRateAdvalorem the cessRateAdvalorem to set
	 */
	public void setCessRateAdvalorem(BigDecimal cessRateAdvalorem) {
		this.cessRateAdvalorem = cessRateAdvalorem;
	}

	/**
	 * @return the cessRateSpecific
	 */
	public BigDecimal getCessRateSpecific() {
		return cessRateSpecific;
	}

	/**
	 * @param cessRateSpecific the cessRateSpecific to set
	 */
	public void setCessRateSpecific(BigDecimal cessRateSpecific) {
		this.cessRateSpecific = cessRateSpecific;
	}

	/**
	 * @return the cessRate
	 */
	public BigDecimal getCessRate() {
		// TODO: Need to clarify with business team.
		return cessRateAdvalorem.add(cessRateSpecific);
	}

	/**
	 * @return the cessAmount
	 */
	public BigDecimal getCessAmount() {
		if(cessAmountAdvalorem == null){
			cessAmountAdvalorem = BigDecimal.ZERO;
		}
		if(cessAmountSpecific == null){
			cessAmountSpecific = BigDecimal.ZERO;
		}
		return cessAmountAdvalorem.add(cessAmountSpecific);
	}

	/**
	 * @return the taxableValue
	 */
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	/**
	 * @param taxableValue the taxableValue to set
	 */
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	/**
	 * @return the totalAmt
	 */
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	/**
	 * @param totalAmt the totalAmt to set
	 */
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	/**
	 * @return the igstRate
	 */
	public BigDecimal getIgstRate() {
		return igstRate;
	}

	/**
	 * @param igstRate the igstRate to set
	 */
	public void setIgstRate(BigDecimal igstRate) {
		this.igstRate = igstRate;
	}

	/**
	 * @return the cgstRate
	 */
	public BigDecimal getCgstRate() {
		return cgstRate;
	}

	/**
	 * @param cgstRate the cgstRate to set
	 */
	public void setCgstRate(BigDecimal cgstRate) {
		this.cgstRate = cgstRate;
	}

	/**
	 * @return the sgstRate
	 */
	public BigDecimal getSgstRate() {
		return sgstRate;
	}

	/**
	 * @param sgstRate the sgstRate to set
	 */
	public void setSgstRate(BigDecimal sgstRate) {
		this.sgstRate = sgstRate;
	}

	/**
	 * @return the stateCessRate
	 */
	public BigDecimal getStateCessRate() {
		return stateCessRate;
	}

	/**
	 * @param stateCessRate the stateCessRate to set
	 */
	public void setStateCessRate(BigDecimal stateCessRate) {
		this.stateCessRate = stateCessRate;
	}

	/**
	 * @return the stateCessAmount
	 */
	public BigDecimal getStateCessAmount() {
		return stateCessAmount;
	}

	/**
	 * @param stateCessAmount the stateCessAmount to set
	 */
	public void setStateCessAmount(BigDecimal stateCessAmount) {
		this.stateCessAmount = stateCessAmount;
	}

	/**
	 * @return the otherValues
	 */
	public BigDecimal getOtherValues() {
		return otherValues;
	}

	/**
	 * @param otherValues the otherValues to set
	 */
	public void setOtherValues(BigDecimal otherValues) {
		this.otherValues = otherValues;
	}

	@Override
	public String toString() {
		return String.format(
				"BasicLineItemValues [taxRate=%s, igstRate=%s, cgstRate=%s, "
				+ "sgstRate=%s, igstAmount=%s, cgstAmount=%s, sgstAmount=%s, "
				+ "cessAmountAdvalorem=%s, cessAmountSpecific=%s, "
				+ "cessRateAdvalorem=%s, cessRateSpecific=%s, "
				+ "stateCessRate=%s, stateCessAmount=%s, otherValues=%s, "
				+ "taxableValue=%s, totalAmt=%s]",
				taxRate, igstRate, cgstRate, sgstRate, igstAmount, cgstAmount,
				sgstAmount, cessAmountAdvalorem, cessAmountSpecific,
				cessRateAdvalorem, cessRateSpecific, stateCessRate,
				stateCessAmount, otherValues, taxableValue, totalAmt);
	}
	
}
