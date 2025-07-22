package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "GSTR1_PROCESSED_HSN")
@Data
public class Gstr1HsnFileUploadEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_PROCESSED_HSN_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;
	
	@Expose
	@SerializedName("asEnterId")
	@Column(name = "AS_ENTERED_ID")
	private Long asEnterId;
	
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
	private BigDecimal quentity;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("igst")
	@Column(name = "IGST_AMT")
	private BigDecimal igst;

	@Expose
	@SerializedName("cgst")
	@Column(name = "CGST_AMT")
	private BigDecimal cgst;

	@Expose
	@SerializedName("sgst")
	@Column(name = "SGST_AMT")
	private BigDecimal sgst;

	@Expose
	@SerializedName("cess")
	@Column(name = "CESS_AMT")
	private BigDecimal cess;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	private BigDecimal totalValue;

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
}
