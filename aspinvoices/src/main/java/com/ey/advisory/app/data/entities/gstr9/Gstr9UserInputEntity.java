package com.ey.advisory.app.data.entities.gstr9;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;


/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "TBL_GSTR9_USER_INPUT")
public class Gstr9UserInputEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "RET_PERIOD")
	private String retPeriod;
	
	@Column(name = "FY")
	private String fy;
	
	@Column(name = "DERIVED_RETPERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "SECTION")
	private String section;
	
	@Column(name = "SUBSECTION")
	private String subSection;
	
	@Column(name = "NATUREOFSUPPLIES")
	private String natureOfSupplies;
	
	@Column(name = "TAXABLEVALUE")
	private BigDecimal txVal = BigDecimal.ZERO;
	
	@Column(name = "IGST")
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Column(name = "CGST")
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Column(name = "SGST")
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Column(name = "CESS")
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Column(name = "MISC")
	private String misc;
	
	@Column(name = "ITC_TYP")
	private String itcTyp;
	
	@Column(name = "DESC")
	private String desc;
	
	@Column(name = "TXPAID_CASH")
	private BigDecimal txpaidCash = BigDecimal.ZERO;
	
	@Column(name = "TAX_PAID_ITC_IAMT")
	private BigDecimal taxPaidItcIamt = BigDecimal.ZERO;
	
	@Column(name = "TAX_PAID_ITC_CAMT")
	private BigDecimal taxPaidItcCamt = BigDecimal.ZERO;
	
	@Column(name = "TAX_PAID_ITC_SAMT")
	private BigDecimal taxPaidItcSamt = BigDecimal.ZERO;
	
	@Column(name = "TAX_PAID_ITC_CSAMT")
	private BigDecimal taxPaidItcCSamt = BigDecimal.ZERO;
	
	@Column(name = "TXPYBLE")
	private BigDecimal txPyble = BigDecimal.ZERO;
	
	@Column(name = "TXPAID")
	private BigDecimal txPaid = BigDecimal.ZERO;
	
	@Column(name = "INTR")
	private BigDecimal intr = BigDecimal.ZERO;
	
	@Column(name = "FEE")
	private BigDecimal fee = BigDecimal.ZERO;
	
	@Column(name = "PEN")
	private BigDecimal pen = BigDecimal.ZERO;
	
	@Column(name = "LIAB_ID")
	private Integer liabId;
	
	@Column(name = "DEBIT_ID")
	private String debitId;
	
	@Column(name = "TRANS_CODE")
	private Integer transCode;
	
	@Column(name = "TRANS_DATE")
	private LocalDate transDate;
	
	@Column(name = "OTH")
	private BigDecimal oth = BigDecimal.ZERO;
	
	@Column(name = "TOT")
	private BigDecimal tot;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;
	
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("asEnterTableId")
	@Column(name = "AS_ENTERED_ID")
	protected Long asEnterTableId;
	
	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "UPDATED_SOURCE")
	private String updatedSource;

}
