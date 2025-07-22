package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Entity
@Table(name = "GSTR2X_TDSTCS_GET_FILUPD")
@Data
public class Gstr2xTdsTcsFileUplEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PROCESSED_ID")
	private Long processedId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "RET_PERIOD")
	private String retPeriod;

	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginCode;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "RECORD_TYPE")
	private String recordType;

	@Column(name = "DEDUCTOR_UPL_MONTH")
	private String deductorUplMonth;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "REG_SUP")
	private BigDecimal regSup;

	@Column(name = "REG_SUPRET")
	private BigDecimal regSupret;

	@Column(name = "UNREG_SUP")
	private BigDecimal unRegSup;

	@Column(name = "UNREG_SUPRET")
	private BigDecimal unRegSupret;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "USER_ACTION")
	private String userAction;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	// BATCH_ID

	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSentToGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSavedToGstn;

	@Column(name = "GSTN_ERROR")
	protected String gstnError;

	// SENT_TO_GSTN_DATE
	// SAVED_TO_GSTN_DATE
	// GSTN_ERROR_CODE
	// GSTN_ERROR_DESCRIPTION
	// CREATED_BY
	// MODIFIED_BY
	// MODIFIED_ON
	// CREATED_ON
	// DOC_KEY

}
