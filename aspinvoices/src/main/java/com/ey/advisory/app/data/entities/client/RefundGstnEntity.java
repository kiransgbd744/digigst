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
@Table(name = "RET_GSTN_REFUND_FROM_E_CASHLEDGER")
@Setter
@Getter
@ToString
public class RefundGstnEntity {

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
	@SerializedName("desc")
	@Column(name = "DESCRIPTION")
	protected String desc;

	@Expose
	@SerializedName("tax")
	@Column(name = "TAX")
	protected BigDecimal tax;

	@Expose
	@SerializedName("interest")
	@Column(name = "INTEREST")
	protected BigDecimal interest;

	@Expose
	@SerializedName("penalty")
	@Column(name = "PENALTY")
	protected BigDecimal penalty;

	@Expose
	@SerializedName("fee")
	@Column(name = "FEE")
	protected BigDecimal fee;

	@Expose
	@SerializedName("other")
	@Column(name = "OTHER")
	protected BigDecimal other;

	@Expose
	@SerializedName("total")
	@Column(name = "TOTAL")
	protected BigDecimal total;

	@Expose
	@SerializedName("refundGstnkey")
	@Column(name = "INVKEY_RET_GSTN_REFUND_FROM_E_CASHLEDGER")
	protected String refundGstnkey;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;
	
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;
	
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
	@SerializedName("asEnterTableId")
	@Column(name = "AS_ENTERED_ID")
	protected Long asEnterTableId;
	public RefundGstnEntity add(RefundGstnEntity newObj) {
		this.sNo = newObj.sNo;
		this.sgstin = newObj.sgstin;
		this.retPeriod = newObj.retPeriod;
		this.derivedRetPeriod = newObj.derivedRetPeriod;
		this.desc = newObj.desc;
		this.tax = addBigDecimals(this.tax,
				newObj.tax);
		this.fee = addBigDecimals(this.fee,
				newObj.fee);
		this.other = addBigDecimals(this.other,
				newObj.other);
		this.total = addBigDecimals(this.total, newObj.total);
		this.interest = addBigDecimals(this.interest, newObj.interest);
		this.penalty = addBigDecimals(this.penalty, newObj.penalty);
		this.fileId = newObj.fileId;
		this.createdBy = newObj.createdBy;
		this.createdOn = newObj.createdOn;
		this.modifiedBy = newObj.modifiedBy;
		this.modifiedOn = newObj.modifiedOn;
		this.refundGstnkey = newObj.refundGstnkey;
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