/*package com.ey.advisory.app.data.entities.client;

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

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*

@Entity
@Table(name = "RET_GSTN_INTEREST_LATEFEE")
@Setter
@Getter
@ToString
public class InterestAndLateFeeGstnEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("sNo")
	@Column(name = "SR_NO")
	protected Integer sNo;

	@Expose
	@SerializedName("returnType")
	@Column(name = "RETURN_TYPE")
	protected String returnType;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("returnTable")
	@Column(name = "RETURN_TABLE")
	protected String returnTable;

	@Expose
	@SerializedName("interestIgstAmt")
	@Column(name = "INTEREST_IGST_AMT")
	protected BigDecimal interestIgstAmt;

	@Expose
	@SerializedName("interestCgstAmt")
	@Column(name = "INTEREST_CGST_AMT")
	protected BigDecimal interestCgstAmt;

	@Expose
	@SerializedName("interestSgstAmt")
	@Column(name = "INTEREST_SGST_AMT")
	protected BigDecimal interestSgstAmt;

	@Expose
	@SerializedName("interestCessAmt")
	@Column(name = "INTEREST_CESS_AMT")
	protected BigDecimal interestCessAmt;

	@Expose
	@SerializedName("lateCgstAmt")
	@Column(name = "LATEFEE_CGST_AMT")
	protected BigDecimal lateCgstAmt;

	@Expose
	@SerializedName("lateSgstAmt")
	@Column(name = "LATEFEE_SGST_AMT")
	protected BigDecimal lateSgstAmt;

	@Expose
	@SerializedName("interestGstnKey")
	@Column(name = "INVKEY_RET_GSTN_INTEREST_LATEFEE")
	protected String interestGstnKey;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

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

	public InterestAndLateFeeGstnEntity add(
			InterestAndLateFeeGstnEntity newObj) {
		this.sNo = newObj.sNo;
		this.returnType = newObj.returnType;
		this.sgstin = newObj.sgstin;
		this.retPeriod = newObj.retPeriod;
		this.returnTable = newObj.returnTable;
		this.interestIgstAmt = addBigDecimals(this.interestIgstAmt,
				newObj.interestIgstAmt);
		this.interestCgstAmt = addBigDecimals(this.interestCgstAmt,
				newObj.interestCgstAmt);
		this.interestSgstAmt = addBigDecimals(this.interestSgstAmt,
				newObj.interestSgstAmt);
		this.interestCessAmt = addBigDecimals(this.interestCessAmt,
				newObj.interestCessAmt);
		this.lateCgstAmt = addBigDecimals(this.lateCgstAmt, newObj.lateCgstAmt);
		this.lateSgstAmt = addBigDecimals(this.lateSgstAmt, newObj.lateSgstAmt);
		this.derivedRetPeriod = newObj.derivedRetPeriod;
		this.fileId = newObj.fileId;
		this.createdBy = newObj.createdBy;
		this.createdOn = newObj.createdOn;
		this.modifiedBy = newObj.modifiedBy;
		this.modifiedOn = newObj.modifiedOn;
		this.interestGstnKey = newObj.interestGstnKey;
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