package com.ey.advisory.service.days.revarsal180;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import lombok.Data;

/**
 * @author Jithendra.B
 *
 */

@Data
@Entity
@Table(name = "TBL_180_DAYS_REV_RESP_PSD")
@NamedStoredProcedureQuery(name = "rev180RespRptDwnld", procedureName = "USP_180_DAYS_REV_RESP_RPT", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_FILE_ID", type = Long.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REPORT_TYPE", type = String.class) })

public class Reversal180DaysResponsePSDEntity {

	@Id
@SequenceGenerator(name = "sequence", sequenceName = "TBL_180_DAYS_REV_RESP_PSD_SEQ", allocationSize = 100)
@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE) 		
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "REVERSAL_TAX_PERIOD")
	private String userResponseReversal;

	@Column(name = "RECLAIM_TAX_PERIOD")
	private String userResponseReclaim;

	@Column(name = "ACTION_TYPE")
	private String actionType;

	@Column(name = "CUST_GSTIN")
	private String custGstin;

	@Column(name = "SUPP_GSTIN")
	private String supplierGstin;

	@Column(name = "SUPP_NAME")
	private String supplierName;

	@Column(name = "SUPP_CODE")
	private String supplierCode;

	@Column(name = "DOC_TYPE")
	private String documentType;

	@Column(name = "DOC_NUM")
	private String documentNum;

	@Column(name = "DOC_DATE")
	private LocalDate documentDate;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "INV_VALUE")
	private BigDecimal invoiceValue;

	@Column(name = "STATUTORY_DEDUCTIONS_APPLICABLE")
	private String statDeductionApplicable;

	@Column(name = "STATUTORY_DEDUCTION_AMT")
	private BigDecimal statDeductionAmt;

	@Column(name = "ANYOTHER_DEDUCTION_AMT")
	private BigDecimal anyOtherDeductionAmt;

	@Column(name = "REMARKS_FOR_DEDUCTIONS")
	private String remarksForDeductions;

	@Column(name = "PAYMENT_DUE_DATE")
	private LocalDate paymentDueDate;

	@Column(name = "PAYMENT_REFERENCE_STATUS")
	private String paymentRefStatus;

	@Column(name = "PAYMENT_REFERENCE_NUMBER")
	private String paymentRefNumber;

	@Column(name = "PAYMENT_REFERENCE_DATE")
	private LocalDate paymentRefDate;

	@Column(name = "PAYMENT_DESCRIPTION")
	private String paymentDesc;

	@Column(name = "PAYMENT_STATUS")
	private String paymentStatus;

	@Column(name = "PAID_AMT_TO_SUPPLIER")
	private BigDecimal paidAmtToSupplier;

	@Column(name = "CURRENCY_CODE")
	private String currencyCode;

	@Column(name = "EXCHANGE_RATE")
	private String exchangeRate;

	@Column(name = "UNPAID_AMT_TO_SUPPLIER")
	private BigDecimal unpaidAmtToSupplier;

	@Column(name = "POSTING_DATE")
	private LocalDate postingDate;

	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Column(name = "PROFIT_CENTRE")
	private String profitCentre;

	@Column(name = "DIVISION")
	private String division;

	@Column(name = "USERDEFINEDFIELD1")
	private String userDefinedField1;

	@Column(name = "USERDEFINEDFIELD2")
	private String userDefinedField2;

	@Column(name = "USERDEFINEDFIELD3")
	private String userDefinedField3;

	@Column(name = "DOC_DATE_180_DAYS")
	private LocalDate docDate180Days;

	@Column(name = "RETURN_PERIOD_PR")
	private String retPeriodPR;

	@Column(name = "RETURN_PERIOD_RECON_RESPONSE")
	private String retPeriodReconResp;

	@Column(name = "IGST_TAX_PAID_PR")
	private BigDecimal igstTaxPaidPR;

	@Column(name = "CGST_TAX_PAID_PR")
	private BigDecimal cgstTaxPaidPR;

	@Column(name = "SGST_TAX_PAID_PR")
	private BigDecimal sgstTaxPaidPR;

	@Column(name = "CESS_TAX_PAID_PR")
	private BigDecimal cessTaxPaidPR;

	@Column(name = "AVAILABLE_IGST_PR")
	private BigDecimal availableIgstPR;

	@Column(name = "AVAILABLE_CGST_PR")
	private BigDecimal availableCgstPR;

	@Column(name = "AVAILABLE_SGST_PR")
	private BigDecimal availableSgstPR;

	@Column(name = "AVAILABLE_CESS_PR")
	private BigDecimal availableCessPR;

	@Column(name = "ITCREV_RECLAIM_STATUS_DIGI")
	private String itcRevReclaimStatusDigi;

	@Column(name = "ITCREV_RET_PRD_DIGI_INDICATIVE")
	private String itcRevRetPrdDigiIndicative;

	@Column(name = "REV_IGST_DIGI_INDICATIVE")
	private BigDecimal revIgstDigiIndicative;

	@Column(name = "REV_CGST_DIGI_INDICATIVE")
	private BigDecimal revCgstDigiIndicative;

	@Column(name = "REV_SGST_DIGI_INDICATIVE")
	private BigDecimal revSgstDigiIndicative;

	@Column(name = "REV_CESS_DIGI_INDICATIVE")
	private BigDecimal revCessDigiIndicative;

	@Column(name = "RECLAIM_RET_PRD_DIGI_INDICATIVE")
	private String reclaimRetPrdDigiIndicative;

	@Column(name = "RECLAIM_IGST_INDICATIVE")
	private BigDecimal reclaimIgstIndicative;

	@Column(name = "RECLAIM_CGST_INDICATIVE")
	private BigDecimal reclaimCgstIndicative;

	@Column(name = "RECLAIM_SGST_INDICATIVE")
	private BigDecimal reclaimSgstIndicative;

	@Column(name = "RECLAIM_CESS_INDICATIVE")
	private BigDecimal reclaimCessIndicative;

	@Column(name = "ITC_REV_COMPUTE_DATE_TIME")
	private LocalDateTime itcRevComputeDateTime;

	@Column(name = "COMPUTE_ID")
	private Long computeID;

	@Column(name = "RECON_DTTM")
	private LocalDateTime reconDateTime;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long reconReportConfigId;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDesc;

	@Column(name = "IS_PROCESSED")
	private boolean isPsd;

}
