package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "RET_AS_ENTERED_INTEREST_LATEFEE")
@Setter
@Getter
@ToString
public class InterestExcelEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("sNo")
	@Column(name = "SR_NO")
	protected String sNo;

	@Expose
	@SerializedName("returnType")
	@Column(name = "RETURN_TYPE")
	protected String returnType;
	
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
	protected String derivedRetPeriod;

	@Expose
	@SerializedName("returnTable")
	@Column(name = "RETURN_TABLE")
	protected String returnTable;

	@Expose
	@SerializedName("interestIgstAmt")
	@Column(name = "INTEREST_IGST_AMT")
	protected String interestIgstAmt;

	@Expose
	@SerializedName("interestCgstAmt")
	@Column(name = "INTEREST_CGST_AMT")
	protected String interestCgstAmt;

	@Expose
	@SerializedName("interestSgstAmt")
	@Column(name = "INTEREST_SGST_AMT")
	protected String interestSgstAmt;

	@Expose
	@SerializedName("interestCessAmt")
	@Column(name = "INTEREST_CESS_AMT")
	protected String interestCessAmt;

	@Expose
	@SerializedName("lateCgstAmt")
	@Column(name = "LATEFEE_CGST_AMT")
	protected String lateCgstAmt;

	@Expose
	@SerializedName("lateSgstAmt")
	@Column(name = "LATEFEE_SGST_AMT")
	protected String lateSgstAmt;

	
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
	@SerializedName("interestInvKey")
	@Column(name = "INVKEY_RET_INTEREST_LATEFEE")
	protected String interestInvKey;
	
	@Expose
	@SerializedName("interestGstnKey")
	@Column(name = "INVKEY_RET_GSTN_INTEREST_LATEFEE")
	protected String interestGstnKey;

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
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;
	
	/*@Transient
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
	private boolean isCgstInMasterCust;*/
}