/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author vishal.verma
 * 
 *
 */

@Data
@Entity
@Table(name = "TBL_180_DAYS_REVERSAL_STG")
public class Reversal180DaysStageEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_180_DAYS_REVERSAL_STG_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	 	
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "ACTION_TYPE")
	private String actionType;

	@Column(name = "CUST_GSTIN")
	private String customerGSTIN;

	@Column(name = "SUPP_GSTIN")
	private String supplierGSTIN;

	@Column(name = "SUPP_NAME")
	private String supplierName;

	@Column(name = "SUPP_CODE")
	private String supplierCode;

	@Column(name = "DOC_TYPE")
	private String documentType;

	@Column(name = "DOC_NUM")
	private String documentNumber;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "DOC_DATE")
	private String documentDate;

	@Column(name = "INV_VAL")
	private String invoiceValue;

	@Column(name = "STATUTORY_DEDUCTIONS_APPL")
	private String statutoryDeductionsApplicable;

	@Column(name = "STATUTORY_DEDUCTION_AMT")
	private String statutoryDeductionAmount;

	@Column(name = "ANY_OTHER_DEDUCTION_AMT")
	private String anyOtherDeductionAmount;

	@Column(name = "REMARKS_FOR_DEDUCTIONS")
	private String remarksforDeductions;

	@Column(name = "DUE_DATE_OF_PAYMENT")
	private String dueDateofPayment;

	@Column(name = "PAYMENT_REF_NUM")
	private String paymentReferenceNumber;

	@Column(name = "PAYMENT_REF_DATE")
	private String paymentReferenceDate;

	@Column(name = "PAYMENT_DESC")
	private String paymentDescription;

	@Column(name = "PAYMENT_STATUS")
	private String paymentStatus;

	@Column(name = "PAID_AMT_TO_SUPP")
	private String paidAmounttoSupplier;

	@Column(name = "CURRENCY_CODE")
	private String currencyCode;

	@Column(name = "EXCHANGE_RATE")
	private String exchangeRate;

	@Column(name = "UNPAID_AMT_TO_SUPPLIER")
	private String unpaidAmounttoSupplier;

	@Column(name = "POSTING_DATE")
	private String postingDate;

	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Column(name = "PROFIT_CENTRE")
	private String profitCentre;

	@Column(name = "DIVISION")
	private String division;

	@Column(name = "USER_DEFINED_FIELD_1")
	private String userDefinedField1;

	@Column(name = "USER_DEFINED_FIELD_2")
	private String userDefinedField2;

	@Column(name = "USER_DEFINED_FIELD_3")
	private String userDefinedField3;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String updatedBy;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;
	
	@Column(name = "SOURCE")
	private String source;
	
	@Column(name = "PAYLOAD_ID")
	private String payLoadId;

}
