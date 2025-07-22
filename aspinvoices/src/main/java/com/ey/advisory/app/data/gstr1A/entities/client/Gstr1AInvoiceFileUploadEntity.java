package com.ey.advisory.app.data.gstr1A.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Entity
@Table(name = "GSTR1A_PROCESSED_INV_SERIES")
@Data
public class Gstr1AInvoiceFileUploadEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_PROCESSED_INV_SERIES_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("asEnterId")
	@Column(name = "AS_ENTERED_ID")
	private Long asEnterId;
	
	@Expose
	@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginType;

	@Expose
	@SerializedName("gstnError")
	@Column(name = "GSTN_ERROR")
	private boolean gstnError;
	
	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("serialNo")
	@Column(name = "SERIAL_NUM")
	private Integer serialNo;

	@Expose
	@SerializedName("natureOfDocument")
	@Column(name = "NATURE_OF_DOC")
	private String natureOfDocument;

	@Expose
	@SerializedName("from")
	@Column(name = "DOC_SERIES_FROM")
	private String from;

	@Expose
	@SerializedName("to")
	@Column(name = "DOC_SERIES_TO")
	private String to;

	@Expose
	@SerializedName("totalNumber")
	@Column(name = "TOT_NUM")
	private Integer totalNumber;

	@Expose
	@SerializedName("cancelled")
	@Column(name = "CANCELED")
	private Integer cancelled;

	@Expose
	@SerializedName("netNumber")
	@Column(name = "NET_NUM")
	private Integer netNumber;

	@Expose
	@SerializedName("invoiceKey")
	@Column(name = "INV_SERIES_KEY")
	private String invoiceKey;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;

	@Expose
	@SerializedName("isSentToGstn")
	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Expose
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSaved;

	@Expose
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	private boolean isInfo;

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
	
	@Transient
	private String uiSectionType;
	
	@Transient
	private Long sNo;
}