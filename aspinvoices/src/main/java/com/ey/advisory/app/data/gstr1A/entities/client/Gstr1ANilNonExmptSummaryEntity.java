package com.ey.advisory.app.data.gstr1A.entities.client;

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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "GSTR1A_SUMMARY_NILEXTNON")
@Data
public class Gstr1ANilNonExmptSummaryEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_SUMMARY_NILEXTNON_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("asProcessedId")
	@Column(name = "AS_PROCESSED_ID")
	private Long asProcessedId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;
	
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
	protected BigDecimal qnt;

	@Expose
	@SerializedName("tableSection")
	@Column(name = "TABLE_SECTION")
	private String tableSection;

	@Expose
	@SerializedName("supplType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplType;

	@Expose
	@SerializedName("nKey")
	@Column(name = "N_INVKEY")
	private String nKey;

	@Expose
	@SerializedName("nGstnKey")
	@Column(name = "N_GSTN_INVKEY")
	private String nGstnKey;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	/*@Expose
	@SerializedName("docAmt")
	@Column(name = "DOC_AMT")
	private BigDecimal docAmt;*/

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
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

}
