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
@Table(name = "ITC04_PROCESSED")
@Setter
@Getter
@ToString
public class Itc04ProcessedEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("tableNumber")
	@Column(name = "TABLE_NUMBER")
	protected String tableNumber;
	
	@Expose
	@SerializedName("actionType")
	@Column(name = "ACTION_TYPE")
	protected String actionType;

	@Expose
	@SerializedName("fy")
	@Column(name = "FY")
	protected String fy;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;
	
	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derivedRetPeriod;

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String supplierGstin;

	@Expose
	@SerializedName("deliveryChallanaNumber")
	@Column(name = "DELIVERY_CHALLANA_NUMBER")
	protected String deliveryChallanaNumber;
	
	@Expose
	@SerializedName("deliveryChallanaDate")
	@Column(name = "DELIVERY_CHALLANA_DATE")
	protected LocalDate deliveryChallanaDate;
	
	@Expose
	@SerializedName("jwDeliveryChallanaNumber")
	@Column(name = "JW_DELIVERY_CHALLANA_NUMBER")
	protected String jwDeliveryChallanaNumber;
	
	@Expose
	@SerializedName("jwDeliveryChallanaDate")
	@Column(name = "JW_DELIVERY_CHALLANA_DATE")
	protected LocalDate jwDeliveryChallanaDate;
	
	@Expose
	@SerializedName("goodsReceivingDate")
	@Column(name = "GOODS_RECEIVING_DATE")
	protected LocalDate goodsReceivingDate;
	
	@Expose
	@SerializedName("invNumber")
	@Column(name = "INVOICE_NUMBER")
	protected String invNumber;
	
	@Expose
	@SerializedName("invDate")
	@Column(name = "INVOICE_DATE")
	protected LocalDate invDate;

	@Expose
	@SerializedName("jobWorkerGstin")
	@Column(name = "JOB_WORKER_GSTIN")
	protected String jobWorkerGstin;

	@Expose
	@SerializedName("jobWorkerStateCode")
	@Column(name = "JOB_WORKER_STATECODE")
	protected String jobWorkerStateCode;

	@Expose
	@SerializedName("jobWorkerId")
	@Column(name = "JOB_WORKER_ID")
	protected String jobWorkerId;

	@Expose
	@SerializedName("jobWorkerName")
	@Column(name = "JOB_WORKER_NAME")
	protected String jobWorkerName;

	@Expose
	@SerializedName("typesOfGoods")
	@Column(name = "TYPES_OF_GOODS")
	protected String typesOfGoods;

	@Expose
	@SerializedName("itemSerialNumber")
	@Column(name = "ITEM_SERIAL_NUMBER")
	protected Integer itemSerialNumber;

	@Expose
	@SerializedName("productDescription")
	@Column(name = "PRODUCT_DESCRIPTION")
	protected String productDescription;
	
	@Expose
	@SerializedName("productCode")
	@Column(name = "PRODUCT_CODE")
	protected String productCode;
	
	@Expose
	@SerializedName("natureOfJw")
	@Column(name = "NATURE_OF_JW")
	protected String natureOfJw;

	@Expose
	@SerializedName("hsn")
	@Column(name = "ITM_HSNSAC")
	protected String hsn;

	@Expose
	@SerializedName("uqc")
	@Column(name = "ITM_UQC")
	protected String uqc;

	@Expose
	@SerializedName("qnt")
	@Column(name = "ITM_QTY")
	protected BigDecimal qnt;
	
	@Expose
	@SerializedName("lossesUqc")
	@Column(name = "LOSSES_UQC")
	protected String lossesUqc;

	@Expose
	@SerializedName("lossesQnt")
	@Column(name = "LOSSES_QTY")
	protected BigDecimal lossesQnt;
	
	@Expose
	@SerializedName("itemAssessableAmt")
	@Column(name = "ITEM_ASSESSABLE_AMOUNT")
	protected BigDecimal itemAssessableAmt;
	
	@Expose
	@SerializedName("igstRate")
	@Column(name = "IGST_RATE")
	protected BigDecimal igstRate;
		
	@Expose
	@SerializedName("cgstRate")
	@Column(name = "CGST_RATE")
	protected BigDecimal cgstRate;
	
	@Expose
	@SerializedName("sgstRate")
	@Column(name = "SGST_RATE")
	protected BigDecimal sgstRate;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmount;
	
	@Expose
	@SerializedName("cessAdvaloremRate")
	@Column(name = "CESS_RATE_ADVALOREM")
	protected BigDecimal cessAdvaloremRate;

	@Expose
	@SerializedName("cessAdvaloremAmount")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected BigDecimal cessAdvaloremAmount;

	@Expose
	@SerializedName("cessSpecificAmount")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected BigDecimal cessSpecificAmount;

	@Expose
	@SerializedName("cessSpecificRate")
	@Column(name = "CESS_RATE_SPECIFIC")
	protected BigDecimal cessSpecificRate;
	
	@Expose
	@SerializedName("stateCessAdvaloremRate")
	@Column(name = "STATE_CESS_RATE_ADVALOREM")
	protected BigDecimal stateCessAdvaloremRate;

	@Expose
	@SerializedName("stateCessAdvaloremAmount")
	@Column(name = "STATE_CESS_AMT_ADVALOREM")
	protected BigDecimal stateCessAdvaloremAmount;

	@Expose
	@SerializedName("stateCessSpecificAmount")
	@Column(name = "STATE_CESS_AMT_SPECIFIC")
	protected BigDecimal stateCessSpecificAmount;

	@Expose
	@SerializedName("stateCessSpecificRate")
	@Column(name = "STATE_CESS_RATE_SPECIFIC")
	protected BigDecimal stateCessSpecificRate;
	
	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableValue;	
	
	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	protected LocalDate postingDate;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("sourceFileName")
	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;

	@Expose
	@SerializedName("sourceIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;
	
	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;
	
	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("profitCentre1")
	@Column(name = "PROFIT_CENTRE1")
	protected String profitCentre1;
	
	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;
	
	@Expose
	@SerializedName("accountVoucherNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accountVoucherDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected LocalDate accountingVoucherDate;
	
	@Expose
	@SerializedName("userDefinedField1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("userDefinedField2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("userDefinedField3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("userDefinedField4")
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;
	@Expose
	@SerializedName("userDefinedField5")
	@Column(name = "USERDEFINED_FIELD5")
	protected String userdefinedfield5;

	@Expose
	@SerializedName("userDefinedField6")
	@Column(name = "USERDEFINED_FIELD6")
	protected String userdefinedfield6;

	@Expose
	@SerializedName("userDefinedField7")
	@Column(name = "USERDEFINED_FIELD7")
	protected String userdefinedfield7;

	@Expose
	@SerializedName("userDefinedField8")
	@Column(name = "USERDEFINED_FIELD8")
	protected String userDefinedField8;

	@Expose
	@SerializedName("userDefinedField9")
	@Column(name = "USERDEFINED_FIELD9")
	protected String userdefinedfield9;

	@Expose
	@SerializedName("userDefinedField10")
	@Column(name = "USERDEFINED_FIELD10")
	protected String userdefinedfield10;
	
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
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;
}