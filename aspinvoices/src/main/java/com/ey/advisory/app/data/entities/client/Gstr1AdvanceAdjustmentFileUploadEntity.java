package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "GSTR1_PROCESSED_ADV_ADJUSTMENT")
@Setter
@Getter
@ToString
public class Gstr1AdvanceAdjustmentFileUploadEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_PROCESSED_ADV_ADJUSTMENT_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("asEnterId")
	@Column(name = "AS_ENTERED_ID")
	private Long asEnterId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("transactionType")
	@Column(name = "TRAN_TYPE")
	private String transactionType;

	@Expose
	@SerializedName("month")
	@Column(name = "MONTH")
	private String month;

	@Expose
	@SerializedName("orgPos")
	@Column(name = "ORG_POS")
	private String orgPOS;

	@Expose
	@SerializedName("orgRate")
	@Column(name = "ORG_RATE")
	private BigDecimal orgRate ;

	@Expose
	@SerializedName("orgGrossAdvanceAdjusted")
	@Column(name = "ORG_GROSS_ADV_ADJUSTED")
	private BigDecimal orgGrossAdvanceAdjusted ;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	private String newPOS;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	private BigDecimal newRate ;

	@Expose
	@SerializedName("newGrossAdvanceAdjusted")
	@Column(name = "NEW_GROSS_ADV_ADJUSTED")
	private BigDecimal newGrossAdvanceAdjusted ;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	private BigDecimal integratedTaxAmount ;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	private BigDecimal centralTaxAmount ;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	private BigDecimal stateUTTaxAmount ;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	private BigDecimal cessAmount ;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT_CODE")
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
	@Column(name = "SALES_ORGANIZATION")
	protected String salesOrganisation;

	@Expose
	@SerializedName("distributionChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;

	@Expose
	@SerializedName("userAccess1")
	@Column(name = "USERACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("userAccess2")
	@Column(name = "USERACCESS2")
	protected String userAccess2;

	@Expose
	@SerializedName("userAccess3")
	@Column(name = "USERACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("userAccess4")
	@Column(name = "USERACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("userAccess5")
	@Column(name = "USERACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("userAccess6")
	@Column(name = "USERACCESS6")
	protected String userAccess6;

	@Expose
	@SerializedName("userDef1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userDef1;

	@Expose
	@SerializedName("userDef2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userDef2;

	@Expose
	@SerializedName("userDef3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userDef3;

	@Expose
	@SerializedName("txpdInvKey")
	@Column(name = "TXPD_INVKEY")
	protected String txpdInvKey;

	@Expose
	@SerializedName("gstnTxpdKey")
	@Column(name = "TXPD_GSTN_INVKEY")
	protected String gstnTxpdKey;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

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
	@SerializedName("sectionType")
	@Column(name = "IS_AMENDMENT")
	protected boolean sectionType;
	
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
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	protected Long gstnBatchId;
	
	@Transient
	private String uiSectionType;
	
	@Transient
	private Long sNo;
}