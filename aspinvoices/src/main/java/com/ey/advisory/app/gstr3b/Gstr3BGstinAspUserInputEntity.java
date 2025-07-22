package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.StoredProcedureParameter;
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
@Table(name = "GSTR3B_ASP_USER")
@NamedStoredProcedureQuery(name = "Gstr3bSummaryReport", procedureName = "USP_GSTR_3B_SUMMARY_REPORT", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_GSTIN", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_TAX_PERIOD", type = String.class) })
public class Gstr3BGstinAspUserInputEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_ASP_USER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "GSTR3B_ASP_INPUT_ID", nullable = false)
	private Long inputId;

	@Expose
	@Column(name = "TAX_PERIOD", nullable = false)
	private String taxPeriod;

	@Expose
	@Column(name = "GSTIN", nullable = false)
	private String gstin;

	@Expose
	@Column(name = "SECTION_NAME", nullable = false)
	private String sectionName;

	@Expose
	@Column(name = "SUBSECTION_NAME", nullable = false)
	private String subSectionName;

	@Expose
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableVal = BigDecimal.ZERO;

	@Expose
	@Column(name = "IGST")
	private BigDecimal igst  = BigDecimal.ZERO;

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
	private BigDecimal interState = BigDecimal.ZERO;

	@Expose
	@Column(name = "INTRA_STATE")
	private BigDecimal intraState = BigDecimal.ZERO;

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
	@Column(name = "IS_ITC_ACTIVE")
	private Boolean isITCActive;

	// for 3.2 section mapping
	@Expose
	@Column(name = "UI_ROW_NAME")
	private String rowName;

	@Expose
	@Column(name = "DOC_KEY")
	private String docKey;

	@Expose
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@Column(name = "SOURCE")
	private String source;

	@Expose
	@Column(name = "USER_RETPERIOD")
	private String userRetPeriod;

	@Expose
	@Column(name = "All_Other_ITC_Flag")
	private Boolean radioFlag = false;

	
	/**
	 * 
	 */
	public Gstr3BGstinAspUserInputEntity() {
		super();
	}

	/**
	 * @param inputId
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
	public Gstr3BGstinAspUserInputEntity(Long inputId, String taxPeriod,
			String gstn, String sectionName, String subSectionName,
			BigDecimal taxableVal, BigDecimal igst, BigDecimal sgst,
			BigDecimal cgst, BigDecimal cess, String pos, BigDecimal interState,
			BigDecimal intraState, LocalDateTime createDate,
			LocalDateTime updateDate, String createdBy, String updatedBy,
			Boolean isActive,Boolean radioFlag) {
		super();
		this.inputId = inputId;
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
		this.radioFlag=radioFlag;
	}

	@Override
	public String toString() {
		return "Gstr3BGstinAspUserInputEntity [inputId=" + inputId
				+ ", taxPeriod=" + taxPeriod + ", gstn=" + gstin
				+ ", sectionName=" + sectionName + ", subSectionName="
				+ subSectionName + ", taxableVal=" + taxableVal + ", igst="
				+ igst + ", sgst=" + sgst + ", cgst=" + cgst + ", cess=" + cess
				+ ", pos=" + pos + ", interState=" + interState
				+ ", intraState=" + intraState + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", createdBy=" + createdBy
				+ ", updatedBy=" + updatedBy + ", isActive=" + isActive + ", radioFlag=" + radioFlag +"]";
	}

	public Gstr3BGstinAspUserInputEntity merge(
			Gstr3BGstinAspUserInputEntity that) {

		BigDecimal zeroVal = BigDecimal.ZERO;

		BigDecimal igst = this.igst == null ? zeroVal.add(that.igst)
				: this.igst.add(that.igst);
		BigDecimal taxableVal = this.taxableVal == null
				? zeroVal.add(that.taxableVal)
				: this.taxableVal.add(that.taxableVal);

		return new Gstr3BGstinAspUserInputEntity(null, null, null, null, null,
				taxableVal, igst, null, null, null, null, null, null, null,
				null, null, null, null,false);

	}

}
