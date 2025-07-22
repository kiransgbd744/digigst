package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "GSTR9_OUTWARD_INWARD_AS_ENTERED")
@Setter
@Getter
@ToString
public class Gstr9OutwardInwardAsEnteredEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("fy")
	@Column(name = "FY")
	protected String fy;

	@Expose
	@SerializedName("tableNumber")
	@Column(name = "TABLE_NUMBER")
	protected String tableNumber;

	@Expose
	@SerializedName("natureOfSupp")
	@Column(name = "NATURE_OF_SUPPLIES")
	protected String natureOfSupp;

	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	protected String taxableVal;

	@Expose
	@SerializedName("igst")
	@Column(name = "IGST")
	protected String igst;

	@Expose
	@SerializedName("cgst")
	@Column(name = "CGST")
	protected String cgst;

	@Expose
	@SerializedName("sgst")
	@Column(name = "SGST")
	protected String sgst;

	@Expose
	@SerializedName("cess")
	@Column(name = "CESS")
	protected String cess;

	@Expose
	@SerializedName("interest")
	@Column(name = "INTEREST")
	protected String interest;

	@Expose
	@SerializedName("lateFee")
	@Column(name = "LATE_FEE")
	protected String lateFee;

	@Expose
	@SerializedName("penalty")
	@Column(name = "PENALTY")
	protected String penalty;

	@Expose
	@SerializedName("other")
	@Column(name = "OTHER")
	protected String other;

	@Expose
	@SerializedName("gst9DocKey")
	@Column(name = "DOC_KEY")
	protected String gst9DocKey;

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
