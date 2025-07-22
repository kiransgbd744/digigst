package com.ey.advisory.app.data.gstr1A.entities.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "GSTR1A_AS_ENTERED_ADV_ADJUSTMENT")
@Setter
@Getter
@ToString
public class Gstr1AAsEnteredTxpdFileUploadEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_AS_ENTERED_ADV_ADJUSTMENT_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

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
	private String orgRate;

	@Expose
	@SerializedName("orgGrossAdvanceAdjusted")
	@Column(name = "ORG_GROSS_ADV_ADJUSTED")
	private String orgGrossAdvanceAdjusted;;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	private String newPOS;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	private String newRate;

	@Expose
	@SerializedName("newGrossAdvanceAdjusted")
	@Column(name = "NEW_GROSS_ADV_ADJUSTED")
	private String newGrossAdvanceAdjusted;;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	private String integratedTaxAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	private String centralTaxAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	private String stateUTTaxAmount;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	private String cessAmount;
	
	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derivedRetPeriod;

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
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;

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
	
	@Transient
	private String uiSectionType;
	
	@Transient
	private Long sNo;
}