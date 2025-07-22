package com.ey.advisory.app.anx1.counterparty;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CounterPartyDetailsDto implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Expose
	private String type;
	
	@Expose
	private Integer totalCount = 0;
	
	@Expose
	private Integer notSavedCount = 0;
	
	@Expose
	private BigDecimal notSavedTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal notSavedTaxAmt = BigDecimal.ZERO;
	
	@SerializedName("savedTaxableVal")
	@Expose
	private BigDecimal savedTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal acceptedTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal acceptedTaxAmt = BigDecimal.ZERO;
	
	@Expose
	private Integer savedCount = 0;
	
	@Expose
	private Integer acceptedCount = 0;
	
	@Expose
	private Integer acceptedPercent = 0;
	
	@Expose
	private Integer rejectedCount= 0;
	
	@Expose
	private BigDecimal savedTaxAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal rejectedTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal rejectedTaxAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal pendingTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal unlockedTaxAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal unlockedTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal pendingTaxAmt = BigDecimal.ZERO;
	
	@Expose
	private Integer rejectedPercent = 0;
	
	@Expose
	private Integer pendingCount = 0;
	
	@Expose
	private Integer unlockedPercent = 0;
	
	@Expose
	private Integer pendingPercent = 0;
	
	@Expose
	private Integer noActionPercent = 0;
	
	@Expose
	private Integer noActionCount = 0;
	
	@Expose
	private Integer unlockedCount = 0;
	
	@Expose
	private BigDecimal noActionTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal noActionTaxAmt = BigDecimal.ZERO;

		
	/**
	 * This method should be called only after all the total counts are 
	 * populated. This method will calculate the individual percentage values
	 * and set them to the data members.
	 */
	public void calculateAndSetPercents() {
		
		int totalCount = this.acceptedCount + this.rejectedCount + 
				this.pendingCount + this.noActionCount + this.unlockedCount;
						
		this.totalCount = totalCount;
		
		this.acceptedPercent = totalCount != 0 ? 
				this.acceptedCount * 100 / totalCount : 0;
		this.rejectedPercent = totalCount != 0 ? 
				this.rejectedCount * 100 / totalCount : 0;
		this.pendingPercent = totalCount != 0 ? 
				this.pendingCount * 100 / totalCount : 0;
		this.unlockedPercent = totalCount != 0 ? 
				this.unlockedCount * 100 / totalCount : 0;
		this.noActionPercent = totalCount != 0 ? 
				this.noActionPercent * 100 / totalCount : 0;
	}

	public CounterPartyDetailsDto(String type, Integer notSavedCount,
			BigDecimal notSavedTaxableVal, BigDecimal notSavedTaxAmt,
			Integer savedCount, BigDecimal savedTaxableVal,
			BigDecimal savedTaxAmt, Integer acceptedCount,
			BigDecimal acceptedTaxableVal, BigDecimal acceptedTaxAmt,
			Integer rejectedCount, BigDecimal rejectedTaxableVal,
			BigDecimal rejectedTaxAmt, Integer pendingCount,
			BigDecimal pendingTaxableVal, BigDecimal pendingTaxAmt,
			Integer unlockedCount, BigDecimal unlockedTaxableVal,
			BigDecimal unlockedTaxAmt, Integer noActionCount,
			BigDecimal noActionTaxableVal, BigDecimal noActionTaxAmt) {
		super();
		this.type = type;
		this.notSavedCount = notSavedCount;
		this.notSavedTaxableVal = notSavedTaxableVal;
		this.notSavedTaxAmt = notSavedTaxAmt;
		this.savedCount = savedCount;
		this.savedTaxableVal = savedTaxableVal;
		this.savedTaxAmt = savedTaxAmt;
		this.acceptedCount = acceptedCount;
		this.acceptedTaxableVal = acceptedTaxableVal;
		this.acceptedTaxAmt = acceptedTaxAmt;
		this.rejectedCount = rejectedCount;
		this.rejectedTaxableVal = rejectedTaxableVal;
		this.rejectedTaxAmt = rejectedTaxAmt;
		this.pendingCount = pendingCount;
		this.pendingTaxableVal = pendingTaxableVal;
		this.pendingTaxAmt = pendingTaxAmt;
		this.unlockedCount = unlockedCount;
		this.unlockedTaxableVal = unlockedTaxableVal;
		this.unlockedTaxAmt = unlockedTaxAmt;
		this.noActionCount = noActionCount;
		this.noActionTaxableVal = noActionTaxableVal;
		this.noActionTaxAmt = noActionTaxAmt;
		
		// After setting all the other counts and amounts, call the 
		// calculateAndSetPercents method to update the total count and the
		// percentages of different sections.
		calculateAndSetPercents();
	}
	
	public CounterPartyDetailsDto(String type) {
		this.type = type;
	}

	public CounterPartyDetailsDto() {
		super();
	}
	
		
}

