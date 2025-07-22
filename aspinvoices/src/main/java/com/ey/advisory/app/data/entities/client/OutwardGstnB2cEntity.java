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
@Table(name = "ANX_GSTN_B2C")
@Setter
@Getter
@ToString
public class OutwardGstnB2cEntity {

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
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SEC7_OF_IGST_FLAG")
	protected String sec7OfIgstFlag;

	@Expose
	@SerializedName("autoPopulateToRefund")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopulateToRefund;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("quentity")
	@Column(name = "QUANTITY")
	protected BigDecimal quentity;

	@Expose
	@SerializedName("taxRate")
	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;

	@Expose
	@SerializedName("stateCessRate")
	@Column(name = "STATE_CESS_RATE")
	protected BigDecimal stateCessRate;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableValue;

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
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected BigDecimal totalValue;

	@Expose
	@SerializedName("stateCessAmt")
	@Column(name = "STATE_CESS_AMT")
	protected BigDecimal stateCessAmt;

	@Expose
	@SerializedName("ecomValueSuppMade")
	@Column(name = "ECOM_VAL_SUPMADE")
	protected BigDecimal ecomValueSuppMade;

	@Expose
	@SerializedName("ecomValSuppRet")
	@Column(name = "ECOM_VAL_SUPRET")
	protected BigDecimal ecomValSuppRet;

	@Expose
	@SerializedName("ecomNetValSupp")
	@Column(name = "ECOM_NETVAL_SUP")
	protected BigDecimal ecomNetValSupp;

	@Expose
	@SerializedName("tcsAmt")
	@Column(name = "TCS_AMT")
	protected BigDecimal tcsAmt;
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
	@SerializedName("b2cGstnKey")
	@Column(name = "B2C_GSTN_INVKEY")
	protected String b2cGstnKey;

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
	
	public OutwardGstnB2cEntity add(OutwardGstnB2cEntity newObj) {
		this.retType = newObj.retType;
		this.sgstin = newObj.sgstin;
		this.retPeriod = newObj.retPeriod;
		this.diffPercent = newObj.diffPercent;
		this.sec7OfIgstFlag = newObj.sec7OfIgstFlag;
		this.autoPopulateToRefund = newObj.autoPopulateToRefund;
		this.pos = newObj.pos;
		this.quentity = addBigDecimals(this.quentity, newObj.quentity);
		this.taxRate = newObj.taxRate;
		this.stateCessRate = newObj.stateCessRate;
		this.taxableValue = addBigDecimals(this.taxableValue,
				newObj.taxableValue);
		this.ecomValueSuppMade = addBigDecimals(this.ecomValueSuppMade,
				newObj.ecomValueSuppMade);
		this.ecomValSuppRet = addBigDecimals(this.ecomValSuppRet,
				newObj.ecomValSuppRet);
		this.ecomNetValSupp = addBigDecimals(this.ecomNetValSupp,
				newObj.ecomNetValSupp);
		this.igstAmt = addBigDecimals(this.igstAmt, newObj.igstAmt);
		this.cgstAmt = addBigDecimals(this.cgstAmt, newObj.cgstAmt);
		this.sgstAmt = addBigDecimals(this.sgstAmt, newObj.sgstAmt);
		this.cessAmt = addBigDecimals(this.cessAmt, newObj.cessAmt);
		this.tcsAmt = addBigDecimals(this.tcsAmt, newObj.tcsAmt);
		this.stateCessAmt = addBigDecimals(this.stateCessAmt,
				newObj.stateCessAmt);
		this.totalValue = addBigDecimals(this.totalValue, newObj.totalValue);
		this.fileId = newObj.fileId;
		this.b2cGstnKey = newObj.b2cGstnKey;

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