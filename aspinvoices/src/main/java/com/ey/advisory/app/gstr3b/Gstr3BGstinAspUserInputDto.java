/**
 * 
 */
package com.ey.advisory.app.gstr3b;

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
public class Gstr3BGstinAspUserInputDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String sectionName;

	@Expose
	private String subSectionName;

	@Expose
	private String returnPeriod;

	@Expose
	private String userRetPeriod;

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
	private String pos;

	@Expose
	private BigDecimal interState = BigDecimal.ZERO;

	@Expose
	private BigDecimal intraState = BigDecimal.ZERO;

	@Expose
	private String rowName;
	
	@Expose
	private Boolean radioFlag = false;

	public Gstr3BGstinAspUserInputDto() {
		super();
	}

	/**
	 * @param gstin
	 * @param sectionName
	 * @param subSectionName
	 * @param taxableVal
	 * @param igst
	 * @param cgst
	 * @param sgst
	 * @param cess
	 * @param pos
	 * @param interState
	 * @param intraState
	 */
	public Gstr3BGstinAspUserInputDto(String sectionName, String subSectionName,
			BigDecimal taxableVal, BigDecimal igst, BigDecimal cgst,
			BigDecimal sgst, BigDecimal cess, String pos, BigDecimal interState,
			BigDecimal intraState) {
		super();
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
		this.taxableVal = taxableVal;
		this.igst = igst;
		this.cgst = cgst;
		this.sgst = sgst;
		this.cess = cess;
		this.pos = pos;
		this.interState = interState;
		this.intraState = intraState;
	}

	@Override
	public String toString() {
		return "Gstr3BGstinAspUserInputDto [sectionName=" + sectionName
				+ ", subSectionName=" + subSectionName + ", returnPeriod="
				+ returnPeriod + ", userRetPeriod=" + userRetPeriod
				+ ", taxableVal=" + taxableVal + ", igst=" + igst + ", cgst="
				+ cgst + ", sgst=" + sgst + ", cess=" + cess + ", pos=" + pos
				+ ", interState=" + interState + ", intraState=" + intraState
				+ ", rowName=" + rowName + ", radioFlag=" + radioFlag + "]";
	}


}
