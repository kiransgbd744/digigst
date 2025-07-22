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
import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "RET_AS_ENTERED_REFUND_FROM_E_CASHLEDGER")
@Data
public class RefundsExcelEntity {

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
	@SerializedName("desc")
	@Column(name = "DESCRIPTION")
	protected String desc;

	@Expose
	@SerializedName("tax")
	@Column(name = "TAX")
	protected String tax;

	@Expose
	@SerializedName("interest")
	@Column(name = "INTEREST")
	protected String interest;

	@Expose
	@SerializedName("penalty")
	@Column(name = "PENALTY")
	protected String penalty;

	@Expose
	@SerializedName("fee")
	@Column(name = "FEE")
	protected String fee;

	@Expose
	@SerializedName("other")
	@Column(name = "OTHER")
	protected String other;

	@Expose
	@SerializedName("total")
	@Column(name = "TOTAL")
	protected String total;

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
	@SerializedName("refundInvkey")
	@Column(name = "INVKEY_RET_REFUND_FROM_E_CASHLEDGER")
	protected String refundInvkey;

	@Expose
	@SerializedName("refundGstnkey")
	@Column(name = "INVKEY_RET_GSTN_REFUND_FROM_E_CASHLEDGER")
	protected String refundGstnkey;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;
	
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

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
