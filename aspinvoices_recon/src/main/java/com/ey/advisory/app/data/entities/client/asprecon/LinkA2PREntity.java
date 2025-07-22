package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Vishal.Verma
 *
 */

@Getter
@Setter
@Entity

@NamedStoredProcedureQuery(name = "FETCH_RECON_RESP_SUMMARY", 
procedureName = "FETCH_RECON_RESP_SUMMARY", parameters = {
		@StoredProcedureParameter(mode = ParameterMode.IN, 
				name = "VAR_GSTIN", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, 
				name = "VAR_TAX_PERIOD", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, 
				name = "VAR_TABLE_TYPE", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN,
				name = "VAR_DOC_TYPE", type = String.class),
		@StoredProcedureParameter(mode = ParameterMode.IN, 
				name = "VAR_TYPE", type = String.class) })

@Table(name = "LINK_A2_PR")
public class LinkA2PREntity {

	@Id
	@Column(name = "RECON_LINK_ID")
	private Long reconLinkId;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long reconReportConfigId;

	@Column(name = "USER_RESPONSE")
	private String userResponse;

	@Column(name = "SUGGESTED_RESPONSE")
	private String suggestResponse;

	@Column(name = "CURRENT_REPORT_TYPE")
	private String currentReportType;

	@Column(name = "CURRENT_MISMATCH_REASON")
	private String currentMisReason;

	@Column(name = "FORCED_MATCH_RESPONSE")
	private String forceMatchResponse;

	@Column(name = "ADDITION_A2_PR_RESPONSE_TAX_PERIOD")
	private String addA2PRRespTaxPeriod;

	@Column(name = "PREVIOUS_REPORT_TYPE")
	private String prevReportType;

	@Column(name = "PREVIOUS_RESPONSE")
	private String prevResponse;

	@Column(name = "A2_GSTN_SAVED_RESPONSE")
	private String a2GstnSaveResponse;

	@Column(name = "MATCHING_SCORE")
	private String matchingScore;

	@Column(name = "A2_SUPPLIER_GSTIN")
	private String a2SupGstn;

	@Column(name = "PR_SUPPLIER_GSTIN")
	private String prSupGstn;

	@Column(name = "A2_DOC_NUM")
	private String a2DocNum;

	@Column(name = "PR_DOC_NUM")
	private String prDocNum;

	@Column(name = "A2_RECIPIENT_GSTIN")
	private String a2RecGstn;

	@Column(name = "PR_RECIPIENT_GSTIN")
	private String prRecGstn;

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

	@Column(name = "A2_INVOICE_KEY")
	private String a2InvoiceKey;

	@Column(name = "PR_INVOICE_KEY")
	private String prInvoiceKey;

	@Column(name = "BUCKET_TYPE")
	private String bucType;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "IS_DELETED")
	private Boolean isDelated;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

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
	private String preA2UserResp;

	@Column(name = "PRE_PR_USER_RESPONSE")
	private String prePrUserResp;

	@Column(name = "A2_SAVED_RESPONSE_REPORT_TYPE")
	private String a2SaveRespReptType;

	@Column(name = "A2_DIGIGST_SAVED_RESPONSE")
	private String a2DigSaveResp;

	@Column(name = "ROLLOVER_CURR_TAX_PERIOD")
	private String rollCurrTaxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Column(name ="IS_FORCED_MATCH")
	private Boolean forcedMatchFalg;

}
