package com.ey.advisory.app.data.entities.drc;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "TBL_DRC_GETCOMPLIST_DETAILS")
public class TblDrcGetRetCompListDetails {

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

	@Expose
	@SerializedName("refId")
	@Column(name = "REF_ID")
	private String refId;

	@Expose
	@SerializedName("formType")
	@Column(name = "FORM_TYPE")
	private String formType;

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

	@Lob
	@Column(name = "RETCOMPLIST_GET_PAYLOAD")
	private String getPayload;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

}
