package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "GSTR3B_ITC_AS_PROCESSED")
@Setter
@Getter
@ToString
public class Gstr3bEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("serialNo")
	@Column(name = "SERIAL_NO")
	protected Integer serialNo;

	@Expose
	@SerializedName("description")
	@Column(name = "DESCRIPTION")
	protected String description;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("tableSection")
	@Column(name = "TABLE_SECTION")
	protected String tableSection;

	@Expose
	@SerializedName("tableDescription")
	@Column(name = "TABLE_DESCRIPTION")
	protected String tableDescription;

	@Expose
	@SerializedName("igstAmnt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmnt;

	@Expose
	@SerializedName("cgstAmnt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmnt;

	@Expose
	@SerializedName("sgstAmnt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmnt;

	@Expose
	@SerializedName("cessAmnt")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmnt;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;
	
	@Expose
	@SerializedName("asEnteredId")
	@Column(name = "AS_ENTERED_ID")
	protected Long asEnteredId;
	
	@Expose
	@SerializedName("invKey")
	@Column(name = "DOC_KEY")
	protected String invKey;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

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

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;
	
	@Expose
	@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginType;
}