package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "CROSS_ITC_AS_ENTERED")
@Setter
@Getter
@ToString
public class CrossItcAsEnteredEntity {


	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "CROSS_ITC_AS_ENTERED_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("isdGstin")
	@Column(name = "ISD_GSTIN")
	protected String isdGstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxPeriod;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derivedRetPeriod;

	@Expose
	@SerializedName("igstUsedAsIgst")
	@Column(name = "IGST_USED_AS_IGST")
	protected String igstUsedAsIgst;

	@Expose
	@SerializedName("sgstUsedAsIgst")
	@Column(name = "SGST_USED_AS_IGST")
	protected String sgstUsedAsIgst;

	@Expose
	@SerializedName("cgstUsedAsIgst")
	@Column(name = "CGST_USED_AS_IGST")
	protected String cgstUsedAsIgst;

	@Expose
	@SerializedName("sgstUsedAsSgst")
	@Column(name = "SGST_USED_AS_SGST")
	protected String sgstUsedAsSgst;

	@Expose
	@SerializedName("igstUsedAsSgst")
	@Column(name = "IGST_USED_AS_SGST")
	protected String igstUsedAsSgst;

	@Expose
	@SerializedName("cgstUsedAsCgst")
	@Column(name = "CGST_USED_AS_CGST")
	protected String cgstUsedAsCgst;

	@Expose
	@SerializedName("igstUsedAsCgst")
	@Column(name = "IGST_USED_AS_CGST")
	protected String igstUsedAsCgst;

	@Expose
	@SerializedName("cessUsedAsCess")
	@Column(name = "CESS_USED_AS_CESS")
	protected String cessUsedAsCess;

	@Expose
	@SerializedName("crossItcDocKey")
	@Column(name = "DOC_KEY")
	protected String crossItcDocKey;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

}
