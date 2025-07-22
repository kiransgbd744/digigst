package com.ey.advisory.app.data.entities.gstr6;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Shashikant Shukla
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR6_DIGI_COMPUTE")
public class Gstr6DigiComputeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	protected Long id;

	@Column(name = "ID_CNT_ASP", nullable = false)
	protected Long aspId;

	@Column(name = "INVOICE_VALUEASP")
	private BigDecimal invoiceValueAsp;

	@Column(name = "TAXABLE_VALUEASP")
	private BigDecimal taxableValueAsp;

	@Column(name = "TOTAL_TAXASP")
	private BigDecimal totalTaxAsp;

	@Column(name = "IGST_AMOUNTASP")
	private BigDecimal igstAmountAsp;

	@Column(name = "CGST_AMOUNTASP")
	private BigDecimal cgstAmountAsp;

	@Column(name = "SGST_AMOUNTASP")
	private BigDecimal sgstAmountAsp;

	@Column(name = "CESS_AMOUNTASP")
	private BigDecimal cessAmountAsp;

	@Expose
	@Column(name = "ELIG_INDICATOR")
	private String eligibiltyIndicatorAsp;

	@Expose
	@Column(name = "TAX_DOCUMENT_TYPE")
	private String taxDocumentType;

	@Expose
	@Column(name = "CUST_GSTIN_ASP")
	private String custGstinAsp;

	@Expose
	@Column(name = "TAX_PERIOD_ASP")
	private String taxPeriodAsp;

	@Expose
	@Column(name = "RECORD_COUNT_GST")
	private Long recordCountGst;

	@Column(name = "INVOICE_VALUE_GST")
	private BigDecimal invoiceValueGst;

	@Column(name = "TAXABLE_VALUE_GST")
	private BigDecimal taxableValueGst;

	@Column(name = "TOTAL_TAX_GST")
	private BigDecimal totalTaxGst;

	@Column(name = "IGST_AMOUNT_GST")
	private BigDecimal igstAmountGst;

	@Column(name = "CGST_AMOUNT_GST")
	private BigDecimal cgstAmountGst;

	@Column(name = "SGST_AMOUNT_GST")
	private BigDecimal sgstAmountGst;

	@Column(name = "CESS_AMOUNT_GST")
	private BigDecimal cessAmountGst;

	@Expose
	@Column(name = "ELIG_IND_GSTIN")
	private String eligibiltyIndicatorGstin;

	@Expose
	@Column(name = "TAX_DOC_TYPE_GSTIN")
	protected String taxDocTypeGstin;

	@Expose
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@Column(name = "ONB_ANSWER")
	private String onbANSWER;
		
}
