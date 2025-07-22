package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
 * @author Umesha M
 *
 */
@MappedSuperclass
@Getter
@Setter
@ToString
public class BasicLineItemErrorValues {

	@Column(name = "TAX_RATE")
	protected String taxRate;
	
	@Expose
	@SerializedName("igstRate")
	@Column(name = "IGST_RATE")
	protected String igstRate;
		
	@Expose
	@SerializedName("cgstRate")
	@Column(name = "CGST_RATE")
	protected String cgstRate;
	
	@Expose
	@SerializedName("sgstRate")
	@Column(name = "SGST_RATE")
	protected String sgstRate;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected String igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected String cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected String sgstAmount;

	@Expose
	@SerializedName("cessAmtAdvalorem")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected String cessAmountAdvalorem;

	@Expose
	@SerializedName("cessAmtSpecfic")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected String cessAmountSpecific;

	@Expose
	@SerializedName("cessRateAdvalorem")
	@Column(name = "CESS_RATE_ADVALOREM")
	protected String cessRateAdvalorem;

	@Expose
	@SerializedName("cessRateSpecific")
	@Column(name = "CESS_RATE_SPECIFIC")
	protected String cessRateSpecific;
	
	@Expose
	@SerializedName("stateCessRate")
	@Column(name = "STATECESS_RATE")
	protected String stateCessRate;
	
	@Expose
	@SerializedName("stateCessAmt")
	@Column(name = "STATECESS_AMT")
	protected String stateCessAmount;
	
	@Expose
	@SerializedName("otherValues")
	@Column(name = "OTHER_VALUES")
	protected String otherValues;
		
	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	protected String taxableValue;	
	
	/**
	 * This field can be used to represent amounts like invoice amount,
	 * credit note amount, advance received amount, advance adjusted 
	 * amount etc.
	 */
	@Transient
	protected BigDecimal totalAmt = BigDecimal.ZERO;
		
	public BasicLineItemErrorValues() {}
	
	/**
	 * Copy constructor to clone an object already available
	 * 
	 * @param sourceObj
	 */
	public BasicLineItemErrorValues(BasicLineItemErrorValues sourceObj) {
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
	
}
