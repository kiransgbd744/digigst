package com.ey.advisory.app.gstr3b;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@Entity
@Table(name = "GSTR3B_ASP_COMPUTE")
public class Gstr3BGstinAspComputeEntity {

	@Id
	@Expose
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GSTR3B_ASP_COMPUTE_ID",  nullable = false)
	private Long computeId;
	
	@Expose
	@Column(name = "TAX_PERIOD",  nullable = false)
	private String taxPeriod;
	
	@Expose
	@Column(name = "GSTIN",   nullable = false)
	private String gstin;
	
	@Expose
	@Column(name = "SECTION_NAME",  nullable = false)
	private String sectionName;
	
	@Expose
	@Column(name = "SUBSECTION_NAME",  nullable = false)
	private String subSectionName;
	
	@Expose
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableVal;
	
	@Expose
	@Column(name = "IGST")
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "SGST")
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "CGST")
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "CESS")
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "POS")
	private String pos;
	
	@Expose
	@Column(name = "INTERSTATE")
	private BigDecimal interState;
	
	@Expose
	@Column(name = "INTRA_STATE")
	private BigDecimal intraState;
	
	@Expose
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;
	
	@Expose
	@Column(name = "UPDATE_DATE")
	private LocalDateTime updateDate;
	
	@Expose
	@Column(name = "CREATE_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "UPDATE_BY")
	private String updatedBy;
	
	@Expose
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Expose
	@Column(name="DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Expose
	@Column(name="BATCH_ID")
	private Long batchId;
	
	//for 3.2 section mapping
		@Expose
		@Column(name = "UI_ROW_NAME")
		private String rowNum;
		

	/**
	 * 
	 */
	public Gstr3BGstinAspComputeEntity() {
		super();
	}

	/**
	 * @param computedId
	 * @param taxPeriod
	 * @param gstn
	 * @param sectionName
	 * @param subSectionName
	 * @param taxableVal
	 * @param igst
	 * @param sgst
	 * @param cgst
	 * @param cess
	 * @param pos
	 * @param interState
	 * @param intraState
	 * @param createDate
	 * @param updateDate
	 * @param createdBy
	 * @param updatedBy
	 * @param isActive
	 */
	public Gstr3BGstinAspComputeEntity(Long computeId, String taxPeriod,
			String gstn, String sectionName, String subSectionName,
			BigDecimal taxableVal, BigDecimal igst, BigDecimal sgst,
			BigDecimal cgst, BigDecimal cess, String pos,
			BigDecimal interState, BigDecimal intraState,
			LocalDateTime createDate, LocalDateTime updateDate,
			String createdBy, String updatedBy, Boolean isActive) {
		super();
		this.computeId = computeId;
		this.taxPeriod = taxPeriod;
		this.gstin = gstn;
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
		this.taxableVal = taxableVal;
		this.igst = igst;
		this.sgst = sgst;
		this.cgst = cgst;
		this.cess = cess;
		this.pos = pos;
		this.interState = interState;
		this.intraState = intraState;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Gstr3BGstinAspUserInputEntity [inputId=" + computeId
				+ ", taxPeriod=" + taxPeriod + ", gstn=" + gstin
				+ ", sectionName=" + sectionName + ", subSectionName="
				+ subSectionName + ", taxableVal=" + taxableVal + ", igst="
				+ igst + ", sgst=" + sgst + ", cgst=" + cgst + ", cess=" + cess
				+ ", pos=" + pos + ", interState=" + interState
				+ ", intraState=" + intraState + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", createdBy=" + createdBy
				+ ", updatedBy=" + updatedBy + ", isActive=" + isActive + "]";
	}
	
	
	

}
