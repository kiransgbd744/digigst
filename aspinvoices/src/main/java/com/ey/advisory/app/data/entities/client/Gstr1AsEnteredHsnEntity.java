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

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
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
@Table(name = "GSTR1_AS_ENTERED_HSN")
@Setter
@Getter
@ToString
public class Gstr1AsEnteredHsnEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_AS_ENTERED_HSN_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

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
	@SerializedName("dataOrgType")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOrgType;

	@Expose
	@SerializedName("hsn")
	@Column(name = "ITM_HSNSAC")
	private String hsn;

	@Expose
	@SerializedName("description")
	@Column(name = "ITM_DESCRIPTION")
	private String description;

	@Expose
	@SerializedName("uqc")
	@Column(name = "ITM_UQC")
	private String uqc;

	@Expose
	@SerializedName("quentity")
	@Column(name = "ITM_QTY")
	private String quentity;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	private String taxableValue;

	@Expose
	@SerializedName("igst")
	@Column(name = "IGST_AMT")
	private String igst;

	@Expose
	@SerializedName("cgst")
	@Column(name = "CGST_AMT")
	private String cgst;

	@Expose
	@SerializedName("sgst")
	@Column(name = "SGST_AMT")
	private String sgst;

	@Expose
	@SerializedName("cess")
	@Column(name = "CESS_AMT")
	private String cess;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	private String totalValue;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("invHsnKey")
	@Column(name = "HSN_GSTN_INVKEY")
	protected String invHsnKey;

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
	@SerializedName("rate")
	@Column(name = "RATE")
	protected String rate;
	
	@Expose
	@SerializedName("serialNo")
	@Column(name = "SERIAL_NO")
	protected String serialNo;
	
	@Expose
	@SerializedName("errorCode")
	@Column(name = "ERROR_CODE")
	protected String errorCode;

	@Expose
	@SerializedName("errorDesc")
	@Column(name = "ERR_DESC")
	protected String errorDescription;
	
	@Transient
	private Long entityId;
	
	@Expose
	@SerializedName("recordType")
	@Column(name = "RECORD_TYPE")
	protected String recordType;

	
	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;
}
