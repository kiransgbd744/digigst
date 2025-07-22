/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
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

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*

@Entity
@Table(name = "ANX_GSTN_TABLE4")
@Setter
@Getter
@ToString
public class OutwardTable4GstnEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("retType")
	@Column(name = "RETURN_TYPE")
	protected String retType;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("ecomGstin")
	@Column(name = "ECOM_GSTIN")
	protected String ecomGstin;

	@Expose
	@SerializedName("valueOfSupMade")
	@Column(name = "ECOM_VAL_SUPMADE")
	protected BigDecimal valueOfSupMade;

	@Expose
	@SerializedName("valueOfSupRet")
	@Column(name = "ECOM_VAL_SUPRET")
	protected BigDecimal valueOfSupRet;

	@Expose
	@SerializedName("netValueOfSup")
	@Column(name = "ECOM_NETVAL_SUP")
	protected BigDecimal netValueOfSup;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

	@Expose
	@SerializedName("table4Gstnkey")
	@Column(name = "TABLE4_GSTN_INVKEY")
	protected String table4Gstnkey;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

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
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSaved;

	@Expose
	@SerializedName("isSent")
	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSent;

	@Expose
	@SerializedName("sentToGSTNDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	protected LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("savedToGSTNDate")
	@Column(name = "SAVED_TO_GSTN_DATE ")
	protected LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("isGstnError")
	@Column(name = "GSTN_ERROR")
	protected boolean isGstnError;

	@Expose
	@SerializedName("isSubmitted")
	@Column(name = "IS_SUBMITTED")
	protected boolean isSubmitted;

	@Expose
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	protected Long gstnBatchId;

	public OutwardTable4GstnEntity add(OutwardTable4GstnEntity newObj) {
		this.retType = newObj.retType;
		this.sgstin = newObj.sgstin;
		this.retPeriod = newObj.retPeriod;
		this.ecomGstin = newObj.ecomGstin;
		this.valueOfSupMade = addBigDecimals(this.valueOfSupMade,
				newObj.valueOfSupMade);
		this.valueOfSupRet = addBigDecimals(this.valueOfSupRet,
				newObj.valueOfSupRet);
		this.netValueOfSup = addBigDecimals(this.netValueOfSup,
				newObj.netValueOfSup);
		this.igstAmt = addBigDecimals(this.igstAmt, newObj.igstAmt);
		this.cgstAmt = addBigDecimals(this.cgstAmt, newObj.cgstAmt);
		this.sgstAmt = addBigDecimals(this.sgstAmt, newObj.sgstAmt);
		this.cessAmt = addBigDecimals(this.cessAmt, newObj.cessAmt);
		this.derivedRetPeriod = newObj.derivedRetPeriod;
		this.fileId = newObj.fileId;
		this.createdBy = newObj.createdBy;
		this.createdOn = newObj.createdOn;
		this.modifiedBy = newObj.modifiedBy;
		this.modifiedOn = newObj.modifiedOn;
		this.table4Gstnkey = newObj.table4Gstnkey;
		this.isDelete = newObj.isDelete;
		return this;

	}

	private BigDecimal addBigDecimals(BigDecimal bd1, BigDecimal bd2) {
		if (bd1 == null && bd2 == null)
			return BigDecimal.ZERO;
		if (bd1 == null)
			return bd2;
		if (bd2 == null)
			return bd1;
		return bd1.add(bd2);
	}

}*/