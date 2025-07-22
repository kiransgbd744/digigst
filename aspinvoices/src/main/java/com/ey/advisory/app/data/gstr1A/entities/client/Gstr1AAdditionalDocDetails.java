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
@Table(name = "ADDITIONAL_SUPP_DOCS_1A")
@Data
public class Gstr1AAdditionalDocDetails {
	
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
	@SerializedName("supportingDocURL")
	@Column(name = "SUPP_DOC_URL")
	private String supportingDocURL;

	@Expose
	@SerializedName("supportingDocBase64")
	@Column(name = "SUPP_DOC_BASE64")
	private String supportingDocBase64;

	@Expose
	@SerializedName("addlInfo")
	@Column(name = "ADD_INFO")
	protected String additionalInformation;

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
	private Gstr1AOutwardTransDocument addtitionalDocDetails;
	
	public Gstr1AAdditionalDocDetails(){
		
	}

}
