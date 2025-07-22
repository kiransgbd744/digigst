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
public class Gstr3BSummaryDto implements Serializable {

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
	private String pos;

	@Expose
	private BigDecimal interState = BigDecimal.ZERO;

	@Expose
	private BigDecimal intraState = BigDecimal.ZERO;

	public Gstr3BSummaryDto() {
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
	 * @param taxPeriod
	 */
	public Gstr3BSummaryDto(String gstin, String sectionName,
			String subSectionName, BigDecimal taxableVal, BigDecimal igst,
			BigDecimal cgst, BigDecimal sgst, BigDecimal cess, String pos,
			BigDecimal interState, BigDecimal intraState, String taxPeriod) {
		super();
		this.gstin = gstin;
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
		this.taxPeriod = taxPeriod;
	}

	@Override
	public String toString() {
		return "Gstr3BSummaryDto [gstin=" + gstin + ", taxPeriod=" + taxPeriod
				+ ", sectionName=" + sectionName + ", subSectionName="
				+ subSectionName + ", taxableVal=" + taxableVal + ", igst="
				+ igst + ", cgst=" + cgst + ", sgst=" + sgst + ", cess=" + cess
				+ ", pos=" + pos + ", interState=" + interState
				+ ", intraState=" + intraState + "]";
	}

	

}
