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
@Table(name = "ANX_GSTN_3H_3I")
@Setter
@Getter
@ToString
public class InwardTable3H3IGstnDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("returnType")
	@Column(name = "RETURN_TYPE")
	protected String returnType;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String custGstin;

	@Expose
	@SerializedName("supplierGSTINorpan")
	@Column(name = "SUPPLIER_GSTIN_PAN")
	protected String supplierGSTINorpan;

	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("sec70fIGSTFLAG")
	@Column(name = "SEC7_OF_IGST_FLAG")
	protected String sec70fIGSTFLAG;

	@Expose
	@SerializedName("autoPopulateToRefund")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopulateToRefund;

	@Expose
	@SerializedName("transactionFlag")
	@Column(name = "TRAN_FLAG")
	protected String transactionFlag;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("hsn")
	@Column(name = "HSNORSAC")
	private String hsn;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableValue;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected BigDecimal totalValue;

	@Expose
	@SerializedName("integratedTaxAmount")
	@Column(name = "IGST_AMT")
	protected BigDecimal integratedTaxAmount;

	@Expose
	@SerializedName("centralTaxAmount")
	@Column(name = "CGST_AMT")
	protected BigDecimal centralTaxAmount;

	@Expose
	@SerializedName("stateUTTaxAmount")
	@Column(name = "SGST_AMT")
	protected BigDecimal stateUTTaxAmount;

	@Expose
	@SerializedName("cessAmount")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmount;;

	@Expose
	@SerializedName("availableIGST")
	@Column(name = "AVAIL_IGST")
	protected BigDecimal availableIGST;

	@Expose
	@SerializedName("availableCGST")
	@Column(name = "AVAIL_CGST")
	protected BigDecimal availableCGST;

	@Expose
	@SerializedName("availableSGST")
	@Column(name = "AVAIL_SGST")
	protected BigDecimal availableSGST;

	@Expose
	@SerializedName("availableCess")
	@Column(name = "AVAIL_CESS")
	protected BigDecimal availableCess;

	@Expose
	@SerializedName("taxRate")
	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;

	// @Expose
	// @SerializedName("table3h3iInvKey")
	// @Column(name = "3H_3I_INVKEY")
	// protected String table3h3iInvKey;

	@Expose
	@SerializedName("table3h3iGstnKey")
	@Column(name = "INVKEY_GSTN_3H_3I")
	protected String table3h3iGstnKey;

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

	*//**
	 * @param newObj
	 * @return
	 *//*
	public InwardTable3H3IGstnDetailsEntity add(
			InwardTable3H3IGstnDetailsEntity newObj) {
		this.custGstin = newObj.custGstin;
		this.returnPeriod = newObj.returnPeriod;
		this.returnType = newObj.returnType;
		this.diffPercent = newObj.diffPercent;
		this.sec70fIGSTFLAG = newObj.sec70fIGSTFLAG;
		this.autoPopulateToRefund = newObj.autoPopulateToRefund;
		this.sec70fIGSTFLAG = newObj.sec70fIGSTFLAG;
		this.autoPopulateToRefund = newObj.autoPopulateToRefund;
		this.transactionFlag = newObj.transactionFlag;
		this.supplierGSTINorpan = newObj.supplierGSTINorpan;
		this.pos = newObj.pos;
		this.hsn = newObj.hsn;
		this.taxableValue = addBigDecimals(this.taxableValue,
				newObj.taxableValue);
		this.integratedTaxAmount = addBigDecimals(this.integratedTaxAmount,
				newObj.integratedTaxAmount);
		this.centralTaxAmount = addBigDecimals(this.centralTaxAmount,
				newObj.centralTaxAmount);
		this.stateUTTaxAmount = addBigDecimals(this.stateUTTaxAmount,
				newObj.stateUTTaxAmount);
		this.cessAmount = addBigDecimals(this.cessAmount, newObj.cessAmount);
		this.availableIGST = addBigDecimals(this.availableIGST,
				newObj.availableIGST);
		this.availableCGST = addBigDecimals(this.availableCGST,
				newObj.availableCGST);
		this.availableSGST = addBigDecimals(this.availableSGST,
				newObj.availableSGST);
		this.availableCess = addBigDecimals(this.availableCess,
				newObj.availableCess);
		this.totalValue = addBigDecimals(this.totalValue, newObj.totalValue);

		this.taxRate = newObj.taxRate;
		this.fileId = newObj.fileId;
		this.table3h3iGstnKey = newObj.table3h3iGstnKey;
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