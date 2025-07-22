package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
public class Gstr3BAspUserInputSaveDto implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private String sectionName;
	
	@Expose
	private String subSectionName;
	
	@Expose
	private BigDecimal taxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	private String pos = "";
	
	@Expose
	private BigDecimal interState = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal intraState = BigDecimal.ZERO;
	
	@Expose
	private LocalDateTime createDate;
	
	@Expose
	private LocalDateTime updateDate;
	
	@Expose
	private String createdBy;
	
	@Expose
	private String updatedBy;
	
	@Expose
	private Boolean isActive = true;

	/**
	 * @param gstin
	 * @param taxPeriod
	 * @param sectionName
	 * @param subSectionName
	 * @param userInputTaxableVal
	 * @param userInputIgst
	 * @param userInputCgst
	 * @param userInputSgst
	 * @param userInputCess
	 * @param pos
	 * @param interState
	 * @param intraState
	 * @param createDate
	 * @param updateDate
	 * @param createdBy
	 * @param updatedBy
	 * @param isActive
	 */
	public Gstr3BAspUserInputSaveDto(String gstin, String taxPeriod,
			String sectionName, String subSectionName,
			BigDecimal userInputTaxableVal, BigDecimal userInputIgst,
			BigDecimal userInputCgst, BigDecimal userInputSgst,
			BigDecimal userInputCess, String pos, BigDecimal interState,
			BigDecimal intraState, LocalDateTime createDate,
			LocalDateTime updateDate, String createdBy, String updatedBy,
			Boolean isActive) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
		this.taxableVal = userInputTaxableVal;
		this.igst = userInputIgst;
		this.cgst = userInputCgst;
		this.sgst = userInputSgst;
		this.cess = userInputCess;
		this.pos = pos;
		this.interState = interState;
		this.intraState = intraState;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.isActive = isActive;
	}

	/**
	 * 
	 */
	public Gstr3BAspUserInputSaveDto() {
		super();
	
	}

	@Override
	public String toString() {
		return "Gstr3BAspUserInputSaveDto [gstin=" + gstin + ", taxPeriod="
				+ taxPeriod + ", sectionName=" + sectionName
				+ ", subSectionName=" + subSectionName
				+ ", userInputTaxableVal=" + taxableVal
				+ ", userInputIgst=" + igst + ", userInputCgst="
				+ cgst + ", userInputSgst=" + sgst
				+ ", userInputCess=" + cess + ", pos=" + pos
				+ ", interState=" + interState + ", intraState=" + intraState
				+ ", createDate=" + createDate + ", updateDate=" + updateDate
				+ ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
				+ ", isActive=" + isActive + "]";
	}
	
	

}
