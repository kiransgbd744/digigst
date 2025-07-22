package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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

@Entity
@Table(name = "GSTR1_AS_ENTERED_ADV_RECEVIED")
@Setter
@Getter
@ToString
public class Gstr1AsEnteredAREntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_AS_ENTERED_ADV_RECEVIED_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derivedRetPeriod;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("returnType")
	@Column(name = "RETURN_TYPE")
	protected String returnType;

	@Expose
	@SerializedName("transType")
	@Column(name = "TRAN_TYPE")
	protected String transType;

	@Expose
	@SerializedName("month")
	@Column(name = "MONTH")
	protected String month;

	@Expose
	@SerializedName("orgPos")
	@Column(name = "ORG_POS")
	protected String orgPos;

	@Expose
	@SerializedName("orgRate")
	@Column(name = "ORG_RATE")
	protected String orgRate;

	@Expose
	@SerializedName("orgGrossAdvRec")
	@Column(name = "ORG_GROSS_ADV_RECEIVED")
	protected String orgGrossAdvRec;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	protected String newPos;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	protected String newRate;

	@Expose
	@SerializedName("newGrossAdvRec")
	@Column(name = "NEW_GROSS_ADV_RECEIVED")
	protected String newGrossAdvRec;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected String igstAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected String cgstAmt;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected String sgstAmt;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected String cessAmt;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT_CODE")
	protected String plant;

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
	@SerializedName("invAtKey")
	@Column(name = "AT_INVKEY")
	protected String invAtKey;

	@Expose
	@SerializedName("gstnAtKey")
	@Column(name = "AT_GSTN_INVKEY")
	protected String gstnAtKey;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;

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
