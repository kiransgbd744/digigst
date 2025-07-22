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

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR9_GET_SMRY")
public class Gstr9GetSummaryEntity {

	public Gstr9GetSummaryEntity(String gstin, String fy, String retPeriod,
			Integer derivedRetPeriod) {
		super();
		this.gstin = gstin;
		this.retPeriod = retPeriod;
		this.fy = fy;
		this.derivedRetPeriod = derivedRetPeriod;
	}

	public Gstr9GetSummaryEntity() {
		super();
	}

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

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "SUBSECTION")
	private String subSection;

	@Column(name = "SUBSECTION_NAME")
	private String subSectionName;

	@Column(name = "TXVAL")
	private BigDecimal txVal = BigDecimal.ZERO;

	@Column(name = "IAMT")
	private BigDecimal iamt = BigDecimal.ZERO;

	@Column(name = "CAMT")
	private BigDecimal camt = BigDecimal.ZERO;

	@Column(name = "SAMT")
	private BigDecimal samt = BigDecimal.ZERO;

	@Column(name = "CSAMT")
	private BigDecimal csamt = BigDecimal.ZERO;

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

	@Column(name = "HSN_SC")
	private String hsnSc;

	@Column(name = "UQC")
	private String uqc;

	@Column(name = "QTY")
	private BigDecimal qty = BigDecimal.ZERO;

	@Column(name = "RT")
	private BigDecimal rt = BigDecimal.ZERO;

	@Column(name = "ISCONCESSTIONAL")
	private String isConcesstional;

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
	private BigDecimal tot = BigDecimal.ZERO;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

}
