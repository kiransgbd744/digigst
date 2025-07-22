package com.ey.advisory.app.data.entities.drc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "TBL_DRC_CHALLAN_DETAILS")
public class TblDrcChallanDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAXPERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_TAXPERIOD")
	private String derivedTaxPeriod;

	@Column(name = "REF_ID")
	private String refId;

	@SerializedName("drcArnNo")
	@Expose
	@Column(name = "DRC_ARN_NO")
	private String drcArnNo;

	@SerializedName("challanNo")
	@Expose
	@Column(name = "CHALLAN_NO")
	private String challanNo;
	
	@Column(name = "CHALLAN_DATE")
	private LocalDate challanDate;

	@Transient
	@SerializedName("challanDateStr")
	@Expose
	private String challanDateStr;

	@SerializedName("igst")
	@Expose
	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@SerializedName("cgst")
	@Expose
	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@SerializedName("sgst")
	@Expose
	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@SerializedName("cess")
	@Expose
	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

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

	@Column(name = "Source")
	private String source;
}
