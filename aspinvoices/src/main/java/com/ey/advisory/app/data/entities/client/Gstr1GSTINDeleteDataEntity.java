package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "GSTR1_GSTN_DELETE_DATA")
@Setter
@Getter
@ToString
public class Gstr1GSTINDeleteDataEntity {
	
	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_GSTN_DELETE_DATA_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Integer fileId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("documentType")
	@Column(name = "DOCUMENT_TYPE")
	protected String documentType;

	@Expose
	@SerializedName("documentNumber")
	@Column(name = "DOCUMENT_NUMBER")
	protected String documentNumber;

	@Expose
	@SerializedName("documentDate")
	@Column(name = "DOCUMENT_DATE")
	protected LocalDate documentDate;

	@Expose
	@SerializedName("cgstin")
	@Column(name = "CUST_GSTIN")
	protected String cgstin;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("tableType")
	@Column(name = "TABLE_TYPE")
	protected String tableType;

	@Expose
	@SerializedName("errorCode")
	@Column(name = "ERROR_CODE")
	protected String errorCode;

	@Expose
	@SerializedName("errorDesc")
	@Column(name = "ERROR_DESC")
	protected String errorDesc;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	protected boolean isProcessed;

	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("fY")
	@Column(name = "FY")
	protected Integer fY;

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

	@Expose
	@SerializedName("createdDate")
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Expose
	@SerializedName("createdUser")
	@Column(name = "CREATED_USER")
	protected String createdUser;
	
	@Expose
	@SerializedName("updatedDate")
	@Column(name = "UPDATED_DATE")
	protected LocalDateTime updatedDate;
	

}
