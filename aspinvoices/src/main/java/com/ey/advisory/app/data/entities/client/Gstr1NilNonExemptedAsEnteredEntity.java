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

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "GSTR1_AS_ENTERED_NILEXTNON")
@Data
public class Gstr1NilNonExemptedAsEnteredEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_AS_ENTERED_NILEXTNON_SEQ", allocationSize = 100)
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
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("hsn")
	@Column(name = "ITM_HSNSAC")
	protected String hsn;

	@Expose
	@SerializedName("description")
	@Column(name = "ITM_DESCRIPTION")
	protected String description;

	@Expose
	@SerializedName("uqc")
	@Column(name = "ITM_UQC")
	protected String uqc;

	@Expose
	@SerializedName("qnt")
	@Column(name = "ITM_QTY")
	protected String qnt;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("tableSection")
	@Column(name = "TABLE_SECTION")
	private String tableSection;

	@Expose
	@SerializedName("nKey")
	@Column(name = "N_INVKEY")
	private String nKey;

	@Expose
	@SerializedName("nGstnKey")
	@Column(name = "N_GSTN_INVKEY")
	private String nGstnKey;

	@Expose
	@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginType;

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
	@SerializedName("nilInterReg")
	@Column(name = "NIL_INTERSTATE_REG")
	private String nilInterReg;

	@Expose
	@SerializedName("nilIntraReg")
	@Column(name = "NIL_INTRASTATE_REG")
	private String nilIntraReg;

	@Expose
	@SerializedName("nilInterUnReg")
	@Column(name = "NIL_INTERSTATE_UNREG")
	private String nilInterUnReg;

	@Expose
	@SerializedName("nilIntraUnReg")
	@Column(name = "NIL_INTRASTATE_UNREG")
	private String nilIntraUnReg;

	@Expose
	@SerializedName("extInterReg")
	@Column(name = "EXT_INTERSTATE_REG")
	private String extInterReg;

	@Expose
	@SerializedName("extIntraReg")
	@Column(name = "EXT_INTRASTATE_REG")
	private String extIntraReg;

	@Expose
	@SerializedName("extInterUnReg")
	@Column(name = "EXT_INTERSTATE_UNREG")
	private String extInterUnReg;

	@Expose
	@SerializedName("extIntraUnReg")
	@Column(name = "EXT_INTRASTATE_UNREG")
	private String extIntraUnReg;

	@Expose
	@SerializedName("nonInterReg")
	@Column(name = "NON_INTERSTATE_REG")
	private String nonInterReg;

	@Expose
	@SerializedName("nonIntraReg")
	@Column(name = "NON_INTRASTATE_REG")
	private String nonIntraReg;

	@Expose
	@SerializedName("nonInterUnReg")
	@Column(name = "NON_INTERSTATE_UNREG")
	private String nonInterUnReg;

	@Expose
	@SerializedName("nonIntraUnReg")
	@Column(name = "NON_INTRASTATE_UNREG")
	private String nonIntraUnReg;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Transient
	private Long entityId;
	
	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;

}
