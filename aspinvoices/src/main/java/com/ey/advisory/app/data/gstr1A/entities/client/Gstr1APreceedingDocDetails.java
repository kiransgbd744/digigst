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
@Table(name = "PRECEEDING_DOC_DTL_1A")
@Data
public class Gstr1APreceedingDocDetails {

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
	@SerializedName("preceedingInvNo")
	@Column(name = "PRE_INV_NUM")
	private String preceedingInvoiceNumber;

	@Expose
	@SerializedName("preceedingInvDate")
	@Column(name = "PRE_INV_DATE")
	private LocalDate preceedingInvoiceDate;

	@Expose
	@SerializedName("invRef")
	@Column(name = "INV_REFERENCE")
	private String invoiceReference;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

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
	private Gstr1AOutwardTransDocument preDocDetails;

	public Gstr1APreceedingDocDetails() {

	}

}
