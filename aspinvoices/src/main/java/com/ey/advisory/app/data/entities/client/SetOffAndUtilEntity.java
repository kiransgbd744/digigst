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
@Table(name = "RET_PROCESSED_SETOFF_UTILIZATION")
@Setter
@Getter
@ToString
public class SetOffAndUtilEntity {

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
	@SerializedName("userDef1")
	@Column(name = "USERDEFINED1")
	protected String userDef1;

	@Expose
	@SerializedName("userDef2")
	@Column(name = "USERDEFINED2")
	protected String userDef2;

	@Expose
	@SerializedName("userDef3")
	@Column(name = "USERDEFINED3")
	protected String userDef3;

	@Expose
	@SerializedName("setOffInvKey")
	@Column(name = "INVKEY_RET_SETOFF_UTILIZATION")
	protected String setOffInvKey;

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

	/*@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;*/
	
	@Expose
	@SerializedName("asEnterTableId")
	@Column(name = "AS_ENTERED_ID")
	protected Long asEnterTableId;
	
	
}