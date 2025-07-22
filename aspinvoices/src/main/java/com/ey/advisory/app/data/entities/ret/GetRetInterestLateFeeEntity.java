package com.ey.advisory.app.data.entities.ret;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "GETRET1_INTEREST_LATEFEE")
@Setter
@Getter
@ToString
public class GetRetInterestLateFeeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "GET_TABLE_SECTION")
	private String tableSection;

	@Column(name = "GET_DESCRIPTION")
	private String getDescription;

	@Column(name = "INTEREST_IGST_AMT")
	private BigDecimal interestIgstAmt;

	@Column(name = "INTEREST_CGST_AMT")
	private BigDecimal interestCgstAmt;

	@Column(name = "INTEREST_SGST_AMT")
	private BigDecimal interestSgstAmt;

	@Column(name = "INTEREST_CESS_AMT")
	private BigDecimal interestCessAmt;

	@Column(name = "LATEFEE_CGST_AMT")
	private BigDecimal latefeeCgstAmt;

	@Column(name = "LATEFEE_SGST_AMT")
	private BigDecimal latefeeSgstAmt;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "GET_RETURN_TYPE")
	protected String getReturnType;

	@Column(name = "CHKSUM")
	protected String chksum;

}
