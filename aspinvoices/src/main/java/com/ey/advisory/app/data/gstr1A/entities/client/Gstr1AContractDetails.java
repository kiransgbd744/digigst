/**
 * 
 */
package com.ey.advisory.app.data.gstr1A.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */
@Entity
@Table(name = "CONTRACT_DTL_1A")
@Data
public class Gstr1AContractDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("suppGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;
	
	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected LocalDate docDate;

	@Expose
	@SerializedName("receiptAdviceRef")
	@Column(name = "RECEIPT_ADVICE_REFERENCE")
	protected String receiptAdviceReference;

	@Expose
	@SerializedName("receiptAdviceDate")
	@Column(name = "RECEIPT_ADVICE_DATE")
	protected LocalDate receiptAdviceDate;

	@Expose
	@SerializedName("tenderRef")
	@Column(name = "TENDER_REFERENCE")
	protected String tenderReference;

	@Expose
	@SerializedName("contractRef")
	@Column(name = "CONTRACT_REFERENCE")
	protected String contractReference;

	@Expose
	@SerializedName("externalRef")
	@Column(name = "EXTERNAL_REFERENCE")
	protected String externalReference;

	@Expose
	@SerializedName("projectRef")
	@Column(name = "PROJECT_REFERENCE")
	protected String projectReference;

	@Expose
	@SerializedName("custPoRefNo")
	@Column(name = "CUST_PO_REF_NUM")
	protected String customerPOReferenceNumber;

	@Expose
	@SerializedName("custPoRefDate")
	@Column(name = "CUST_PO_REF_DATE")
	protected LocalDate customerPOReferenceDate;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("isDeleted")
	@Column(name = "IS_DELETE")
	protected boolean isDeleted;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime updatedDate;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Gstr1AOutwardTransDocument contractDetails;

	public Gstr1AContractDetails() {

	}

}
