package com.ey.advisory.app.data.entities.client;

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

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "ANX_PROCESSED_B2C")
@Setter
@Getter
@ToString
public class OutwardB2cEntity {

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
	@SerializedName("hsnSac")
	@Column(name = "HSNORSAC")
	protected String hsnSac;

	@Expose
	@SerializedName("uom")
	@Column(name = "UOM")
	protected String uom;

	@Expose
	@SerializedName("quentity")
	@Column(name = "QUANTITY")
	protected BigDecimal quentity;

	@Expose
	@SerializedName("rate")
	@Column(name = "TAX_RATE")
	protected BigDecimal rate;

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
	@SerializedName("stateApplyCess")
	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyCess;

	@Expose
	@SerializedName("stateCessRate")
	@Column(name = "STATE_CESS_RATE")
	protected BigDecimal stateCessRate;

	@Expose
	@SerializedName("stateCessAmt")
	@Column(name = "STATE_CESS_AMT")
	protected BigDecimal stateCessAmt;

	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;

	@Expose
	@SerializedName("ecomGstin")
	@Column(name = "ECOM_GSTIN")
	protected String ecomGstin;

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
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTER")
	protected String profitCentre;

	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT")
	protected String plant;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("salesOrganisation")
	@Column(name = "SALES_ORG")
	protected String salesOrganisation;

	@Expose
	@SerializedName("distributionChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;

	@Expose
	@SerializedName("userAccess1")
	@Column(name = "USER_ACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("userAccess2")
	@Column(name = "USER_ACCESS2")
	protected String userAccess2;

	@Expose
	@SerializedName("userAccess3")
	@Column(name = "USER_ACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("userAccess4")
	@Column(name = "USER_ACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("userAccess5")
	@Column(name = "USER_ACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("userAccess6")
	@Column(name = "USER_ACCESS6")
	protected String userAccess6;

	@Expose
	@SerializedName("userDef1")
	@Column(name = "USER_DEFINED1")
	protected String userDef1;

	@Expose
	@SerializedName("userDef2")
	@Column(name = "USER_DEFINED2")
	protected String userDef2;

	@Expose
	@SerializedName("userDef3")
	@Column(name = "USER_DEFINED3")
	protected String userDef3;

	@Expose
	@SerializedName("b2cInvKey")
	@Column(name = "B2C_INVKEY")
	protected String b2cInvKey;

	@Expose
	@SerializedName("b2cGstnKey")
	@Column(name = "B2C_GSTN_INVKEY")
	protected String b2cGstnKey;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

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
	@SerializedName("asEnterTableId")
	@Column(name = "AS_ENTERED_ID")
	protected Long asEnterTableId;
	
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
	@Column(name = "SAVED_TO_GSTN_DATE")
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

	public OutwardB2cEntity add(OutwardB2cEntity newObj) {

		this.retType = newObj.retType;
		this.sgstin = newObj.sgstin;
		this.retPeriod = newObj.retPeriod;
		this.diffPercent = newObj.diffPercent;
		this.sec7OfIgstFlag = newObj.sec7OfIgstFlag;
		this.autoPopulateToRefund = newObj.autoPopulateToRefund;
		this.pos = newObj.pos;
		this.hsnSac = newObj.hsnSac;
		this.uom = newObj.uom;
		this.quentity = addBigDecimals(this.quentity, newObj.quentity);
		this.rate = newObj.rate;
		this.stateApplyCess = newObj.stateApplyCess;
		this.stateCessRate = newObj.stateCessRate;
		this.taxableValue = addBigDecimals(this.taxableValue,
				newObj.taxableValue);
		this.tcsFlag = newObj.tcsFlag;
		this.ecomGstin = newObj.ecomGstin;
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
		this.profitCentre = newObj.profitCentre;
		this.plant = newObj.plant;
		this.division = newObj.division;
		this.location = newObj.location;
		this.salesOrganisation = newObj.salesOrganisation;
		this.distributionChannel = newObj.distributionChannel;
		this.userAccess1 = newObj.userAccess1;
		this.userAccess2 = newObj.userAccess2;
		this.userAccess3 = newObj.userAccess3;
		this.userAccess4 = newObj.userAccess4;
		this.userAccess5 = newObj.userAccess5;
		this.userAccess6 = newObj.userAccess6;
		this.userDef1 = newObj.userDef1;
		this.userDef2 = newObj.userDef2;
		this.userDef3 = newObj.userDef3;
		this.fileId = newObj.fileId;
		this.b2cInvKey = newObj.b2cInvKey;

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
}