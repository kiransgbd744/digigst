package com.ey.advisory.app.data.entities.gstr9;

import java.math.BigDecimal;
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
@Table(name = "TBL_GSTR9_AUTO_CALC")
public class Gstr9AutoCalculateEntity {

	public Gstr9AutoCalculateEntity(String gstin, String fy, String retPeriod,
			Integer derivedRetPeriod) {
		super();
		this.gstin = gstin;
		this.retPeriod = retPeriod;
		this.fy = fy;
		this.derivedRetPeriod = derivedRetPeriod;
	}

	public Gstr9AutoCalculateEntity() {
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

	@Column(name = "TXPYBLE")
	private BigDecimal txPyble = BigDecimal.ZERO;

	@Column(name = "TXPAID_CASH")
	private BigDecimal txPaidCash = BigDecimal.ZERO;

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
	
	@Column(name = "TAX_PAID_ITC_IAMT")
	private BigDecimal taxPaidItcIamt = BigDecimal.ZERO;

	@Column(name = "TAX_PAID_ITC_CAMT")
	private BigDecimal taxPaidItcCamt = BigDecimal.ZERO;

	@Column(name = "TAX_PAID_ITC_SAMT")
	private BigDecimal taxPaidItcSamt = BigDecimal.ZERO;

	@Column(name = "TAX_PAID_ITC_CSAMT")
	private BigDecimal taxPaidItcCSamt = BigDecimal.ZERO;


}
