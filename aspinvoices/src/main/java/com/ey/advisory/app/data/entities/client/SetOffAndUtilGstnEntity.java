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
@Table(name = "RET_GSTN_SETOFF_UTILIZATION")
@Setter
@Getter
@ToString
public class SetOffAndUtilGstnEntity {

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
	@SerializedName("retType")
	@Column(name = "RETURN_TYPE")
	protected String retType;

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
	@SerializedName("taxPayableRevCharge")
	@Column(name = "TAX_PAYABLE_REVCHARGE")
	protected BigDecimal taxPayableRevCharge;

	@Expose
	@SerializedName("taxPayableOthRevCharge")
	@Column(name = "TAX_PAYABLE_OTHERTHAN_REVCHARGE")
	protected BigDecimal taxPayableOthRevCharge;

	@Expose
	@SerializedName("taxAlreadyPaidRevCharge")
	@Column(name = "TAX_ALREADYPAID_REVCHARGE")
	protected BigDecimal taxAlreadyPaidRevCharge;

	@Expose
	@SerializedName("taxAlreadyPaidOthRevCharge")
	@Column(name = "TAX_ALREADYPAID_OTHERTHAN_REVCHARGE")
	protected BigDecimal taxAlreadyPaidOthRevCharge;

	@Expose
	@SerializedName("adjRevCharge")
	@Column(name = "ADJ_NEG_LIB_PRETAXPERIOD_RC")
	protected BigDecimal adjRevCharge;

	@Expose
	@SerializedName("adjOthRevCharge")
	@Column(name = "ADJ_NEG_LIB_PRETAXPERIOD_OTHERTHAN_RC")
	protected BigDecimal adjOthRevCharge;

	@Expose
	@SerializedName("paidThrouhItcIgst")
	@Column(name = "PAID_THROUGH_ITC_IGST")
	protected BigDecimal paidThrouhItcIgst;

	@Expose
	@SerializedName("paidThrouhItcCgst")
	@Column(name = "PAID_THROUGH_ITC_CGST")
	protected BigDecimal paidThrouhItcCgst;

	@Expose
	@SerializedName("paidThrouhItcSgst")
	@Column(name = "PAID_THROUGH_ITC_SGST")
	protected BigDecimal paidThrouhItcSgst;

	@Expose
	@SerializedName("paidThrouhItcCess")
	@Column(name = "PAID_THROUGH_ITC_CESS")
	protected BigDecimal paidThrouhItcCess;

	@Expose
	@SerializedName("paidInCashTaxCess")
	@Column(name = "PAID_IN_CASH_TAX_CESS")
	protected BigDecimal paidInCashTaxCess;

	@Expose
	@SerializedName("paidInCashTaxInterest")
	@Column(name = "PAID_IN_CASH_INTEREST")
	protected BigDecimal paidInCashTaxInterest;

	@Expose
	@SerializedName("paidInCashLateFee")
	@Column(name = "PAID_IN_CASH_LATEFEE")
	protected BigDecimal paidInCashLateFee;

	@Expose
	@SerializedName("setOffGstnKey")
	@Column(name = "INVKEY_RET_GSTN_SETOFF_UTILIZATION")
	protected String setOffGstnKey;

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

	@Expose
	@SerializedName("asEnterTableId")
	@Column(name = "AS_ENTERED_ID")
	protected Long asEnterTableId;

	public SetOffAndUtilGstnEntity add(SetOffAndUtilGstnEntity newObj) {
		this.sNo = newObj.sNo;
		this.retType = newObj.retType;
		this.sgstin = newObj.sgstin;
		this.retPeriod = newObj.retPeriod;
		this.desc = newObj.desc;
		this.taxPayableRevCharge = addBigDecimals(this.taxPayableRevCharge,
				newObj.taxPayableRevCharge);
		this.taxPayableOthRevCharge = addBigDecimals(
				this.taxPayableOthRevCharge, newObj.taxPayableOthRevCharge);
		this.taxAlreadyPaidRevCharge = addBigDecimals(
				this.taxAlreadyPaidRevCharge, newObj.taxAlreadyPaidRevCharge);
		this.taxAlreadyPaidOthRevCharge = addBigDecimals(
				this.taxAlreadyPaidOthRevCharge,
				newObj.taxAlreadyPaidOthRevCharge);
		this.adjOthRevCharge = addBigDecimals(this.adjOthRevCharge,
				newObj.adjOthRevCharge);
		this.adjRevCharge = addBigDecimals(this.adjRevCharge,
				newObj.adjRevCharge);
		this.paidThrouhItcIgst = addBigDecimals(this.paidThrouhItcIgst,
				newObj.paidThrouhItcIgst);
		this.paidThrouhItcCgst = addBigDecimals(this.paidThrouhItcCgst,
				newObj.paidThrouhItcCgst);
		this.paidThrouhItcSgst = addBigDecimals(this.paidThrouhItcSgst,
				newObj.paidThrouhItcSgst);
		this.paidThrouhItcCess = addBigDecimals(this.paidThrouhItcCess,
				newObj.paidThrouhItcCess);
		this.paidInCashTaxCess = addBigDecimals(this.paidInCashTaxCess,
				newObj.paidInCashTaxCess);
		this.paidInCashTaxInterest = addBigDecimals(this.paidInCashTaxInterest,
				newObj.paidInCashTaxInterest);
		this.paidInCashLateFee = addBigDecimals(this.paidInCashLateFee,
				newObj.paidInCashLateFee);
		this.derivedRetPeriod = newObj.derivedRetPeriod;
		this.fileId = newObj.fileId;
		this.createdBy = newObj.createdBy;
		this.createdOn = newObj.createdOn;
		this.modifiedBy = newObj.modifiedBy;
		this.modifiedOn = newObj.modifiedOn;
		this.setOffGstnKey = newObj.setOffGstnKey;
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