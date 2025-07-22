package com.ey.advisory.app.data.entities.drc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "TBL_DRC_DETAILS")
public class TblDrcDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "TAXPERIOD")
	private String taxPeriod;


	/*@Expose
	@SerializedName("derivedTaxPeriod")
	@Column(name = "DERIVED_TAXPERIOD")
	private String derivedTaxPeriod;
*/

	@Expose
	@SerializedName("refId")
	@Column(name = "REF_ID")
	private String refId;

	@Expose
	@SerializedName("returnFilingFreq")
	@Column(name = "RETURN_FILING_FREQ")
	private String returnFilingFreq;

	@Column(name = "GSTR1_IGST_AMT")
	private BigDecimal gstr1IgstAmt;

	@Column(name = "GSTR1_SGST_AMT")
	private BigDecimal gstr1SgstAmt;

	@Column(name = "GSTR1_CGST_AMT")
	private BigDecimal gstr1CgstAmt;

	@Column(name = "GSTR1_CESS_AMT")
	private BigDecimal gstr1CessAmt;

	@Column(name = "GSTR1_TTL_AMT")
	private BigDecimal gstr1TtlAmt;

	@Column(name = "GSTR3B_IGST_AMT")
	private BigDecimal gstr3BIgstAmt;

	@Column(name = "GSTR3B_SGST_AMT")
	private BigDecimal gstr3BSgstAmt;

	@Column(name = "GSTR3B_CGST_AMT")
	private BigDecimal gstr3BCgstAmt;

	@Column(name = "GSTR3B_CESS_AMT")
	private BigDecimal gstr3BCessAmt;

	@Column(name = "GSTR3B_TTL_AMT")
	private BigDecimal gstr3BTtlAmt;

	@Column(name = "IGST_DIFF_AMT")
	private BigDecimal igstDiffAmt;

	@Column(name = "CGST_DIFF_AMT")
	private BigDecimal cgstDiffAmt;

	@Column(name = "SGST_DIFF_AMT")
	private BigDecimal sgstDiffAmt;

	@Column(name = "CESS_DIFF_AMT")
	private BigDecimal cessDiffAmt;

	@Column(name = "TTL_DIFF_AMT")
	private BigDecimal ttlDiffAmt;

	@Expose
	@SerializedName("errorCode")
	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Expose
	@SerializedName("errorDesc")
	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Expose
	@SerializedName("getStatus")
	@Column(name = "GET_STATUS")
	private String getStatus;

	@Expose
	@SerializedName("taxPayerStatus")
	@Column(name = "TAXPAYER_STATUS")
	private String taxPayerStatus;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	   
	@OneToMany(mappedBy = "drcDetailsId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected List<TblDrcPaymentDetails> paymentDetails = new ArrayList<>();

	@OneToMany(mappedBy = "drcDetailsId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected List<TblDrcReasonDetails> reasonDetails = new ArrayList<>();

}

