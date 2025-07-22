package com.ey.advisory.app.data.entities.gstr9;

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
@Table(name = "GSTR9_HSN_AS_ENTERED")
@Setter
@Getter
@ToString
public class Gstr9HsnAsEnteredEntity {

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
	@SerializedName("hsn")
	@Column(name = "HSN")
	protected String hsn;

	@Expose
	@SerializedName("desc")
	@Column(name = "DESCRIPTION")
	protected String desc;

	@Expose
	@SerializedName("rateOfTax")
	@Column(name = "RATE_OF_TAX")
	protected String rateOfTax;

	@Expose
	@SerializedName("uqc")
	@Column(name = "UQC")
	protected String uqc;

	@Expose
	@SerializedName("totalQnt")
	@Column(name = "TOTAL_QNT")
	protected String totalQnt;

	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	protected String taxableVal;

	@Expose
	@SerializedName("conRateFlag")
	@Column(name = "CON_RATE_FLAG")
	protected String conRateFlag;

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
