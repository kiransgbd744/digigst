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
@Table(name = "GETRET1_PAYMENT_OF_TAX")
@Setter
@Getter
@ToString
public class GetRetPaymentOfTaxEntity {

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

	@Column(name = "TAX_PAYABLE_REVCHARGE")
	private BigDecimal taxPayRevCharge;

	@Column(name = "TAX_PAYABLE_OTHERTHAN_REVCHARGE")
	private BigDecimal taxPayOtherThanRevCharge;

	@Column(name = "TAX_ALREADYPAID_REVCHARGE")
	private BigDecimal taxAlreadyPayRevCharge;

	@Column(name = "TAX_ALREADYPAID_OTHERTHAN_REVCHARGE")
	private BigDecimal taxAlreadyPayOtherRevCharge;

	@Column(name = "ADJ_NEG_LIB_PRETAXPERIOD_RC")
	private BigDecimal adjNegPreTax;

	@Column(name = "ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC")
	private BigDecimal adjNegPreOtherTax;

	@Column(name = "PAID_THROUGH_ITC_IGST")
	private BigDecimal paidItcIgst;

	@Column(name = "PAID_THROUGH_ITC_CGST")
	private BigDecimal paidItcCgst;

	@Column(name = "PAID_THROUGH_ITC_SGST")
	private BigDecimal paidItcSgst;

	@Column(name = "PAID_THROUGH_ITC_CESS")
	private BigDecimal paidItcCess;

	@Column(name = "PAID_IN_CASH_TAX_CESS")
	private BigDecimal paidCashTaxCess;

	@Column(name = "PAID_IN_CASH_INTEREST")
	private BigDecimal paidCashInterest;

	@Column(name = "PAID_IN_CASH_LATEFEE")
	private BigDecimal paidCashLatefee;

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
	protected String returnType;

	@Column(name = "CHKSUM")
	protected String chksum;

}
