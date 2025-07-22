package com.ey.advisory.app.anx1.recipientsummary;
import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
public class RecipientResponseDetailsDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    

    @Expose
    private String level;

    @Expose
    private String cPan;

    @Expose
    private String cName;

    @Expose
    private String cgstin;

    @Expose
    private String tableType;
    
    @Expose
    private String docType;

    @Expose
    private Integer notSavedPercent = 0;

    @Expose
    private Integer notSavedcount = 0;

    @Expose
    private BigDecimal notSavedTaxableVal = BigDecimal.ZERO;

    @Expose
    private BigDecimal notSavedTaxAmt = BigDecimal.ZERO;

    @Expose
    private Integer savedPercent = 0;

    @Expose
    private Integer savedCount = 0;

    @Expose
    private BigDecimal savedTaxableVal = BigDecimal.ZERO;

    @Expose
    private BigDecimal savedTaxAmt = BigDecimal.ZERO;

    @Expose
    private Integer acceptedPercent = 0;

    @Expose
    private Integer acceptedCount = 0;

    @Expose
    private BigDecimal acceptedTaxableVal = BigDecimal.ZERO;

    @Expose
    private BigDecimal acceptedTaxAmt = BigDecimal.ZERO;

    @Expose
    private Integer rejectedPercent = 0;

    @Expose
    private Integer rejectedCount = 0;

    @Expose
    private BigDecimal rejectedTaxableVal = BigDecimal.ZERO;

    @Expose
    private BigDecimal rejectedTaxAmt = BigDecimal.ZERO;

    @Expose
    private Integer pendingPercent = 0;

    @Expose
    private Integer pendingCount = 0;

    @Expose
    private BigDecimal pendingTaxableVal = BigDecimal.ZERO;

    @Expose
    private BigDecimal pendingTaxAmt = BigDecimal.ZERO;

    @Expose
    private Integer noActionPercent = 0;

    @Expose
    private Integer noActionCount = 0;

    @Expose
    private BigDecimal noActionTaxableVal = BigDecimal.ZERO;

    @Expose
    private BigDecimal noActionTaxAmt = BigDecimal.ZERO;

	/**
	 * @param tableType
	 * @param notSavedPercent
	 * @param notSavedcount
	 * @param notSavedTaxableVal
	 * @param notSavedTaxAmt
	 * @param savedCount
	 * @param savedTaxableVal
	 * @param savedTaxAmt
	 * @param acceptedPercent
	 * @param acceptedCount
	 * @param acceptedTaxableVal
	 * @param acceptedTaxAmt
	 * @param rejectedCount
	 * @param rejectedTaxableVal
	 * @param rejectedTaxAmt
	 * @param pendingCount
	 * @param pendingTaxableVal
	 * @param pendingTaxAmt
	 * @param noActionCount
	 * @param noActionTaxableVal
	 * @param noActionTaxAmt
	 */
	public RecipientResponseDetailsDto(String tableType,
			 Integer notSavedcount,
			BigDecimal notSavedTaxableVal, BigDecimal notSavedTaxAmt,
			Integer savedCount, BigDecimal savedTaxableVal,
			BigDecimal savedTaxAmt, Integer acceptedPercent,
			Integer acceptedCount, BigDecimal acceptedTaxableVal,
			BigDecimal acceptedTaxAmt, Integer rejectedCount,
			BigDecimal rejectedTaxableVal, BigDecimal rejectedTaxAmt,
			Integer pendingCount, BigDecimal pendingTaxableVal,
			BigDecimal pendingTaxAmt, Integer noActionCount,
			BigDecimal noActionTaxableVal, BigDecimal noActionTaxAmt) {
		super();
		this.tableType = tableType;
		this.notSavedcount = notSavedcount;
		this.notSavedTaxableVal = notSavedTaxableVal;
		this.notSavedTaxAmt = notSavedTaxAmt;
		this.savedCount = savedCount;
		this.savedTaxableVal = savedTaxableVal;
		this.savedTaxAmt = savedTaxAmt;
		this.acceptedPercent = acceptedPercent;
		this.acceptedCount = acceptedCount;
		this.acceptedTaxableVal = acceptedTaxableVal;
		this.acceptedTaxAmt = acceptedTaxAmt;
		this.rejectedCount = rejectedCount;
		this.rejectedTaxableVal = rejectedTaxableVal;
		this.rejectedTaxAmt = rejectedTaxAmt;
		this.pendingCount = pendingCount;
		this.pendingTaxableVal = pendingTaxableVal;
		this.pendingTaxAmt = pendingTaxAmt;
		this.noActionCount = noActionCount;
		this.noActionTaxableVal = noActionTaxableVal;
		this.noActionTaxAmt = noActionTaxAmt;
	}
	
	
    
	public RecipientResponseDetailsDto() {
		
	}

	public RecipientResponseDetailsDto(String level) {
		this.level = level;
	}
	
	public String getKey() {
		if ("L1".equals(level)) return cPan;
		if ("L2".equals(level)) return cPan + "#" + cgstin;
		return cPan + "#" + cgstin + "#" + tableType + "#" + docType;
	}



	public RecipientResponseDetailsDto(String level, String cPan,String cName,
			String cgstin,
			String tableType, Integer notSavedcount,
			BigDecimal notSavedTaxableVal, BigDecimal notSavedTaxAmt,
		    Integer savedCount,
			BigDecimal savedTaxableVal, BigDecimal savedTaxAmt,
			Integer acceptedCount,
			BigDecimal acceptedTaxableVal, BigDecimal acceptedTaxAmt,
			Integer rejectedCount,
			BigDecimal rejectedTaxableVal, BigDecimal rejectedTaxAmt,
		   Integer pendingCount,
			BigDecimal pendingTaxableVal, BigDecimal pendingTaxAmt,
		  Integer noActionCount,
			BigDecimal noActionTaxableVal, BigDecimal noActionTaxAmt) {
		this.level = level;
		this.cPan = cPan;
		this.cName = cName;
		this.cgstin = cgstin;
		this.tableType = tableType;
		this.notSavedcount = notSavedcount;
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
		this.noActionCount = noActionCount;
		this.noActionTaxableVal = noActionTaxableVal;
		this.noActionTaxAmt = noActionTaxAmt;
		calculateAndSetPercents();
		
	}
	
	public void calculateAndSetPercents() {
		
		int totalCount = this.acceptedCount + this.rejectedCount + 
				this.pendingCount + this.noActionCount;
		int nsTotalCount = this.savedCount + this.notSavedcount;
		
		this.acceptedPercent = totalCount != 0 ? 
				this.acceptedCount * 100 / totalCount : 0;
		this.rejectedPercent = totalCount != 0 ? 
				this.rejectedCount * 100 / totalCount : 0;
		this.pendingPercent = totalCount != 0 ? 
				this.pendingCount * 100 / totalCount : 0;
		this.noActionPercent= totalCount != 0 ? 
				this.noActionCount * 100 / totalCount : 0;
		this.savedPercent = nsTotalCount != 0 ? 
				this.savedCount * 100 / nsTotalCount : 0;
		this.notSavedPercent = nsTotalCount != 0 ? 
				this.notSavedcount * 100 / nsTotalCount : 0;
	}



	@Override
	public String toString() {
		return "RecipientResponseDetailsDto [level=" + level + ", cPan=" + cPan
				+ ", cName=" + cName + ", cgstin=" + cgstin + ", tableType="
				+ tableType + ", notSavedPercent=" + notSavedPercent
				+ ", notSavedcount=" + notSavedcount + ", notSavedTaxableVal="
				+ notSavedTaxableVal + ", notSavedTaxAmt=" + notSavedTaxAmt
				+ ", savedPercent=" + savedPercent + ", savedCount="
				+ savedCount + ", savedTaxableVal=" + savedTaxableVal
				+ ", savedTaxAmt=" + savedTaxAmt + ", acceptedPercent="
				+ acceptedPercent + ", acceptedCount=" + acceptedCount
				+ ", acceptedTaxableVal=" + acceptedTaxableVal
				+ ", acceptedTaxAmt=" + acceptedTaxAmt + ", rejectedPercent="
				+ rejectedPercent + ", rejectedCount=" + rejectedCount
				+ ", rejectedTaxableVal=" + rejectedTaxableVal
				+ ", rejectedTaxAmt=" + rejectedTaxAmt + ", pendingPercent="
				+ pendingPercent + ", pendingCount=" + pendingCount
				+ ", pendingTaxableVal=" + pendingTaxableVal
				+ ", pendingTaxAmt=" + pendingTaxAmt + ", noActionPercent="
				+ noActionPercent + ", noActionCount=" + noActionCount
				+ ", noActionTaxableVal=" + noActionTaxableVal
				+ ", noActionTaxAmt=" + noActionTaxAmt + "]";
	}
    

}
