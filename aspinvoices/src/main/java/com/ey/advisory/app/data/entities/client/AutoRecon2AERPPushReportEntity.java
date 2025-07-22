package com.ey.advisory.app.data.entities.client;

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
 * @author Jithendra.B
 *
 */
@Entity
@Table(name = "TBL_AUTO_2APR_ERP_REPORT")
@Data
public class AutoRecon2AERPPushReportEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ERP_REPORT_ID")
	private Long requestId;

	@Column(name = "RECON_LINK_ID")
	private Long reconLinkID;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long reconReportConfigID;

	@Column(name = "USER_RESPONSE")
	private String userResponse;

	@Column(name = "GSTR_3B_TAX_PERIOD")
	private String gstr3BTaxPeriod;

	@Column(name = "MATCH_REASON")
	private String matchReason;

	@Column(name = "CURRENT_MISMATCH_REASON")
	private String currentMisMatchReason;

	@Column(name = "REPORT_CATEGORY")
	private String reportCategory;

	@Column(name = "CURRENT_REPORT_TYPE")
	private String currentReportType;

	@Column(name = "ERP_REPORT_TYPE")
	private String erpReportType;

	@Column(name = "A2_TAX_PERIOD")
	private String taxPeriod2A;

	@Column(name = "B2_TAX_PERIOD")
	private String taxPeriod2B;

	@Column(name = "PR_TAX_PERIOD")
	private String taxPeriodPR;

	@Column(name = "A2_RECIPIENT_GSTIN")
	private String recipientGstin2A;

	@Column(name = "PR_RECIPIENT_GSTIN")
	private String recipientGstinPR;

	@Column(name = "A2_SUPPLIER_GSTIN")
	private String supplierGstin2A;

	@Column(name = "PR_SUPPLIER_GSTIN")
	private String supplierGstinPR;

	@Column(name = "A2_DOC_TYPE")
	private String DocType2A;

	@Column(name = "PR_DOC_TYPE")
	private String docTypePR;

	@Column(name = "A2_DOC_NUM")
	private String docNum2A;

	@Column(name = "PR_DOC_NUM")
	private String docNumPR;

	@Column(name = "A2_DOC_DATE")
	private LocalDate docDate2A;

	@Column(name = "PR_DOC_DATE")
	private LocalDate docDatePR;

	@Column(name = "A2_POS")
	private String pos2A;

	@Column(name = "PR_POS")
	private String posPR;

	@Column(name = "A2_TAXABLE_VALUE")
	private BigDecimal taxableValue2A;

	@Column(name = "PR_TAXABLE_VALUE")
	private BigDecimal taxableValuePR;

	@Column(name = "A2_IGST")
	private BigDecimal igst2A;

	@Column(name = "PR_IGST")
	private BigDecimal igstPR;

	@Column(name = "A2_CGST")
	private BigDecimal cgst2A;

	@Column(name = "PR_CGST")
	private BigDecimal cgstPR;

	@Column(name = "A2_SGST")
	private BigDecimal sgsst2A;

	@Column(name = "PR_SGST")
	private BigDecimal sgstPR;

	@Column(name = "A2_CESS")
	private BigDecimal cess2A;

	@Column(name = "PR_CESS")
	private BigDecimal cessPR;

	@Column(name = "A2_INVOICE_VALUE")
	private BigDecimal invoiceValue2A;

	@Column(name = "PR_INVOICE_VALUE")
	private BigDecimal invoiceValuePR;

	@Column(name = "B2_ITC_AVAILABLILITY")
	private String itcAvailability2B;

	@Column(name = "B2_REASON_UNAVALIABLILTY")
	private String reasonUnavailability2B;

	@Column(name = "GSRT1_FILING_STATUS")
	private String gstr1FilingStatus;

	@Column(name = "GSRT1_FILING_DATE")
	private LocalDate gstr1FilingDate;

	@Column(name = "GSRT1_FILING_PERIOD")
	private String gstr1FilingPeriod;

	@Column(name = "GSRT3B_FILING_STATUS")
	private String gstr3BFilingStatus;

	@Column(name = "A2_REVERSE_CHARGE_FLAG")
	private String revChrgFlag2A;

	@Column(name = "PR_REVERSE_CHARGE_FLAG")
	private String revChrgFlagPR;

	@Column(name = "PLANTCODE")
	private String plantCode;

	@Column(name = "DIVISION")
	private String division;

	@Column(name = "PURCHASE_ORGANISATION")
	private String purchaseOrg;

	@Column(name = "A2_TABLE_TYPE")
	private String tableType2A;

	@Column(name = "A2_SUPPLY_TYPE")
	private String supplyType2A;

	@Column(name = "PR_SUPPLY_TYPE")
	private String supplyTypePR;

	@Column(name = "ACC_VOUCHER_NUM")
	private String accVoucherNum;

	@Column(name = "ACC_VOUCHER_DATE")
	private LocalDate accVoucherDate;

	@Column(name = "APPROVAL_STATUS")
	private String approvalSatus;

	@Column(name = "IS_DELINK")
	private boolean isDelink;

	@Column(name = "DELINK_REASON")
	private String dlinkReason;

	@Column(name = "A2_CFS")
	private String cfs2A;

	@Column(name = "A2_INVOICE_KEY")
	private String invoiceKey;

	@Column(name = "PR_INVOICE_KEY")
	private String invoiceKeyPR;

	@Column(name = "A2_INV_KEY")
	private String docKey2A;

	@Column(name = "PR_DOC_KEY")
	private String docKeyPR;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "IS_DELETED")
	private boolean isDelete;

	@Column(name = "IS_ERP_PUSH")
	private boolean isERPPush;

	@Column(name = "ERP_PUSH_DATE")
	private LocalDateTime erpPushDate;

}
