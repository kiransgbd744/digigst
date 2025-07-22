package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "TBL_LINK_2A_PR")
public class Gstr2Link2APREntity {
	
	@Id
	@Column(name = "RECON_LINK_ID")
	private Long reconLinkId;
	
	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long reconReportConfigId;
	
	@Column(name = "USER_RESPONSE")
	private String userResponse;
	
	@Column(name = "SUGGESTED_RESPONSE")
	private String suggResp;
	
	@Column(name = "CURRENT_REPORT_TYPE")
	private String currentReportType;
	
	@Column(name = "CURRENT_MISMATCH_REASON")
	private String currentMismachReason;
	
	@Column(name = "MATCHING_SCORE")
	private String machingScore;
	
	@Column(name = "A2_SUPPLIER_GSTIN")
	private String a2SuppGstin;
	
	@Column(name = "PR_SUPPLIER_GSTIN")
	private String prSuppGstin;
	
	@Column(name = "A2_DOC_NUM")
	private String a2DocNum;

	@Column(name = "PR_DOC_NUM")
	private String prDocNum;

	@Column(name = "A2_RECIPIENT_GSTIN")
	private String a2RecpGstin;
	
	@Column(name = "PR_RECIPIENT_GSTIN")
	private String prRecpGstin;

	@Column(name = "A2_DOC_TYPE")
	private String a2DocType;

	@Column(name = "PR_DOC_TYPE")
	private String prDocType;

	@Column(name = "A2_DOC_DATE")
	private LocalDate a2DocDate;

	@Column(name = "PR_DOC_DATE")
	private LocalDate prDocDate;

	@Column(name = "A2_TAXABLE_VALUE")
	private BigDecimal a2TaxableVal;

	@Column(name = "PR_TAXABLE_VALUE")
	private BigDecimal prTaxableVal;

	@Column(name = "A2_CGST")
	private BigDecimal a2Cgst;

	@Column(name = "PR_CGST")
	private BigDecimal prCgst;

	@Column(name = "A2_SGST")
	private BigDecimal a2Sgst;

	@Column(name = "PR_SGST")
	private BigDecimal prSgst;

	@Column(name = "A2_IGST")
	private BigDecimal a2Igst;

	@Column(name = "PR_IGST")
	private BigDecimal prIgst;

	@Column(name = "A2_CESS")
	private BigDecimal a2Cess;

	@Column(name = "PR_CESS")
	private BigDecimal prCess;
	
	@Column(name = "A2_POS")
	private String a2Pos;

	@Column(name = "PR_POS")
	private String prPos;

	@Column(name = "A2_TAX_PERIOD")
	private String a2TaxPeriod;

	@Column(name = "PR_TAX_PERIOD")
	private String prTaxPeriod;

	@Column(name = "A2_RET_PERIOD")
	private Integer a2RetPeriod;

	@Column(name = "PR_RET_PERIOD")
	private Integer prRetPeriod;

	@Column(name = "BUCKET_TYPE")
	private String bucketType;

	@Column(name = "A2_INVOICE_KEY")
	private String a2InvoiceKey;

	@Column(name = "PR_INVOICE_KEY")
	private String prInvoiceKey;

	@Column(name = "A2_ID")
	private Long a2Id;

	@Column(name = "PR_ID")
	private Long prId;

	@Column(name = "A2_TABLE")
	private String a2Table;

	@Column(name = "PR_TABLE")
	private String prTable;

	@Column(name = "PRE_A2_REPORT_TYPE")
	private String preA2ReportType;

	@Column(name = "PRE_PR_REPORT_TYPE")
	private String prePrReportType;

	@Column(name = "PRE_A2_USER_RESPONSE")
	private String preA2UserResponse;

	@Column(name = "PRE_PR_USER_RESPONSE")
	private String prePrUserResponse;

	@Column(name = "IS_INFORMATION_REPORT")
	private Integer isInformationReport;

	@Column(name = "REPORT_TYPE_ID")
	private Integer reportTypeId;

	@Column(name = "IS_FORCED_MATCH")
	private Boolean isForceMatch;

	@Column(name = "IS_LINK_TO_FORCE_MATCH")
	private Boolean isLinkToForceMatch;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "IS_DELETED")
	private Boolean isDelete;

	@Column(name = "FORCE_MATCH")
	private String forceMatch;

	@Column(name = "GSTR_3B_TAX_PERIOD")
	private String gstr3BTaxPeriod;

	@Column(name = "A2_CFS")
	private String a2CFS;

}
