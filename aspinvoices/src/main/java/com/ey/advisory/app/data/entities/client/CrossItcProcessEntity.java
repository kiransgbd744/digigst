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
@Table(name = "CROSS_ITC_PROCESSED")
@Setter
@Getter
@ToString
public class CrossItcProcessEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "CROSS_ITC_PROCESSED_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("asEnterId")
	@Column(name = "AS_ENTERED_ID")
	private Long asEnterId;

	@Expose
	@SerializedName("isdGstin")
	@Column(name = "ISD_GSTIN")
	protected String isdGstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxPeriod;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("igstUsedAsIgst")
	@Column(name = "IGST_USED_AS_IGST")
	protected BigDecimal igstUsedAsIgst;

	@Expose
	@SerializedName("sgstUsedAsIgst")
	@Column(name = "SGST_USED_AS_IGST")
	protected BigDecimal sgstUsedAsIgst;

	@Expose
	@SerializedName("cgstUsedAsIgst")
	@Column(name = "CGST_USED_AS_IGST")
	protected BigDecimal cgstUsedAsIgst;

	@Expose
	@SerializedName("sgstUsedAsSgst")
	@Column(name = "SGST_USED_AS_SGST")
	protected BigDecimal sgstUsedAsSgst;

	@Expose
	@SerializedName("igstUsedAsSgst")
	@Column(name = "IGST_USED_AS_SGST")
	protected BigDecimal igstUsedAsSgst;

	@Expose
	@SerializedName("cgstUsedAsCgst")
	@Column(name = "CGST_USED_AS_CGST")
	protected BigDecimal cgstUsedAsCgst;

	@Expose
	@SerializedName("igstUsedAsCgst")
	@Column(name = "IGST_USED_AS_CGST")
	protected BigDecimal igstUsedAsCgst;

	@Expose
	@SerializedName("cessUsedAsCess")
	@Column(name = "CESS_USED_AS_CESS")
	protected BigDecimal cessUsedAsCess;

	@Expose
	@SerializedName("crossItcDocKey")
	@Column(name = "DOC_KEY")
	protected String crossItcDocKey;

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
	@SerializedName("isSent")
	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSent;

	@Expose
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSaved;
	
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

	@Expose
	@SerializedName("gstnErrorCode")
	@Column(name = "GSTN_ERROR_CODE")
	protected String gstnErrorCode;

	@Expose
	@SerializedName("gstnErrorDesc")
	@Column(name = "GSTN_ERROR_DESC")
	protected String gstnErrorDesc;

}
