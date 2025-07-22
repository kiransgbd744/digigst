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
 * 
 * @author vishal.verma
 *
 */
@Setter
@Getter
@Entity
@Table(name = "GSTR3B_GSTN")
public class Gstr3bGstnSaveToAspEntity {
	
	@Id
	@Expose
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GSTR3B_GSTN_ID",  nullable = false)
	private Long id;
	
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
	private BigDecimal igst;
	
	@Expose
	@Column(name = "SGST")
	private BigDecimal sgst;
	
	@Expose
	@Column(name = "CGST")
	private BigDecimal cgst;
	
	@Expose
	@Column(name = "CESS")
	private BigDecimal cess;
	
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
	@Column(name = "CREATE_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Expose
	@Column(name = "DERIVED_TAX_PERIOD",  nullable = false)
	private Integer derivedTaxPeriod;

	@Override
	public String toString() {
		return "Gstr3bGstnSaveEntity [id=" + id + ", taxPeriod=" + taxPeriod
				+ ", gstin=" + gstin + ", sectionName=" + sectionName
				+ ", subSectionName=" + subSectionName + ", taxableVal="
				+ taxableVal + ", igst=" + igst + ", sgst=" + sgst + ", cgst="
				+ cgst + ", cess=" + cess + ", pos=" + pos + ", interState="
				+ interState + ", intraState=" + intraState + ", createDate="
				+ createDate + ", createdBy=" + createdBy + ", isActive="
				+ isActive + "]";
	}
	
	

	
}
