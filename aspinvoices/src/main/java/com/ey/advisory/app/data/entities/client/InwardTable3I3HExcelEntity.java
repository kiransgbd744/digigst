package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.javatuples.Pair;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
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
@Table(name = "ANX_AS_ENTERED_3H_3I")
@Setter
@Getter
@ToString
public class InwardTable3I3HExcelEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("recipientGstin")
	@Column(name = "CUST_GSTIN")
	protected String recipientGstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("returnType")
	@Column(name = "RETURN_TYPE")
	protected String returnType;

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
	@SerializedName("supplierGSTINorpan")
	@Column(name = "SUPPLIER_GSTIN_PAN")
	protected String supplierGSTINorpan;

	@Expose
	@SerializedName("supplierName")
	@Column(name = "SUPPLIER_NAME")
	protected String supplierName;

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
	protected String taxableValue;

	@Expose
	@SerializedName("integratedTaxAmount")
	@Column(name = "IGST_AMT")
	protected String integratedTaxAmount;

	@Expose
	@SerializedName("centralTaxAmount")
	@Column(name = "CGST_AMT")
	protected String centralTaxAmount;

	@Expose
	@SerializedName("stateUTTaxAmount")
	@Column(name = "SGST_AMT")
	protected String stateUTTaxAmount;

	@Expose
	@SerializedName("cessAmount")
	@Column(name = "CESS_AMT")
	protected String cessAmount;

	@Expose
	@SerializedName("eligibilityIndicator")
	@Column(name = "ELGBL_INDICATOR")
	protected String eligibilityIndicator;

	@Expose
	@SerializedName("availableIGST")
	@Column(name = "AVAIL_IGST")
	protected String availableIGST;

	@Expose
	@SerializedName("availableCGST")
	@Column(name = "AVAIL_CGST")
	protected String availableCGST;

	@Expose
	@SerializedName("availableSGST")
	@Column(name = "AVAIL_SGST")
	protected String availableSGST;

	@Expose
	@SerializedName("availableCess")
	@Column(name = "AVAIL_CESS")
	protected String availableCess;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected String totalValue;

	@Expose
	@SerializedName("rate")
	@Column(name = "TAX_RATE")
	protected String rate;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTER")
	protected String profitCentre;

	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT")
	protected String plant;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("purchaseOrganisation")
	@Column(name = "PURCHAGE_ORG")
	protected String purchaseOrganisation;

	@Expose
	@SerializedName("userDefined1")
	@Column(name = "USERDEFINED1")
	protected String userDefined1;

	@Expose
	@SerializedName("userDefined2")
	@Column(name = "USERDEFINED2")
	protected String userDefined2;

	@Expose
	@SerializedName("userDefined3")
	@Column(name = "USERDEFINED3")
	protected String userDefined3;

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
	@SerializedName("table3h3iInvKey")
	@Column(name = "INVKEY_3H_3I")
	protected String table3h3iInvKey;

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
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derivedRetPeriod;

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
	@Transient
	private Long entityId;

	@Transient
	private Long groupId;

	@Transient
	private Map<Long, List<Pair<String, String>>> entityAtValMap;

	@Transient
	private Map<EntityAtConfigKey, Map<Long, String>> entityAtConfMap;

	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;
	
	@Transient
	private String formReturnType;
	
	@Transient
	private boolean isCgstInMasterCust;
}