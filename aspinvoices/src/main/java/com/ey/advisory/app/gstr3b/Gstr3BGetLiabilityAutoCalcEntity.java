package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 *
 */
@Setter
@Getter
@Entity
@ToString
@Table(name = "GSTR3B_AUTO_CALC")
public class Gstr3BGetLiabilityAutoCalcEntity {
	
	@Id
	@Expose
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_AUTO_CALC_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "GSTR3B_AUTO_CALC_ID",  nullable = false)
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
	private BigDecimal taxableVal = BigDecimal.ZERO;;
	
	@Expose
	@Column(name = "IGST")
	private BigDecimal igst = BigDecimal.ZERO;;
	
	@Expose
	@Column(name = "SGST")
	private BigDecimal sgst = BigDecimal.ZERO;;
	
	@Expose
	@Column(name = "CGST")
	private BigDecimal cgst = BigDecimal.ZERO;;
	
	@Expose
	@Column(name = "CESS")
	private BigDecimal cess = BigDecimal.ZERO;;
	
	@Expose
	@Column(name = "POS")
	private String pos;
	
	@Expose
	@Column(name = "INTERSTATE")
	private BigDecimal interState = BigDecimal.ZERO;;
	
	@Expose
	@Column(name = "INTRA_STATE")
	private BigDecimal intraState = BigDecimal.ZERO;;
	
	@Expose
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;
	
	@Expose
	@Column(name = "CREATE_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "UPDATE_DATE")
	private LocalDateTime updateDate;
	
	@Expose
	@Column(name = "UPDATE_BY")
	private String updatedBy;
	
	@Expose
	@Column(name = "BATCH_ID")
	private Long batcId;
	
	@Expose
	@Column(name = "DERIVED_RET_PERIOD",  nullable = false)
	private Integer derivedRetPeriod;
	
	@Expose
	@Column(name = "INVOICE_VALUE")
	private String invoiceValue;

	@Expose
	@Column(name = "CALC_3B_RET_PERIOD")
	private String calc3BRetPeriod;
	
	@Expose
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	

}
