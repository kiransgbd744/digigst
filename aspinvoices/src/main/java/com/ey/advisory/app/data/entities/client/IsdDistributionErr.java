package com.ey.advisory.app.data.entities.client;

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

@Entity
@Table(name = "TBL_ISD_DISTRIBUTION_ERR")
@Setter
@Getter
@ToString
public class IsdDistributionErr {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ISD_ERROR_DOC_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("isdgstn")
	@Column(name = "ISDGSTIN")
	private String isdgstn;

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;
	
	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	private String docType;

	@Expose
	@SerializedName("docNum")
	@Column(name = "DOC_NUM")
	private String docNum;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	private String docDate;

	@Expose
	@SerializedName("itemSerialNum")
	@Column(name = "ITEM_SERIAL_NUM")
	private String itemSerialNum;

	@Expose
	@SerializedName("gstinDistribution")
	@Column(name = "GSTIN_DISTRIBUTION")
	private String gstinDistribution;

	@Expose
	@SerializedName("distrbDocKey")
	@Column(name = "DISTRB_DOC_KEY")
	private String distrbDocKey;

	@Expose
	@SerializedName("prDocKey")
	@Column(name = "PR_DOC_KEY")
	private String prDocKey;

	@Expose
	@SerializedName("prId")
	@Column(name = "PR_ID")
	private String prId;
	
	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	@Expose
	@SerializedName("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Expose
	@SerializedName("updatedOn")
	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;
	
	@Expose
	@SerializedName("errorCodes")
	@Column(name = "ERROR_CODES")
	private String errorCodes;
	
	@Expose
	@SerializedName("errorDesc")
	@Column(name = "ERROR_DESC")
	private String errorDesc;
	
	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	private Boolean isDelete;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private String fileId;
	
	@Expose
	@SerializedName("dataOriginInTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginInTypeCode;
	
	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private Boolean isError;
	
	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private Boolean isProcessed;
	
	@Expose
	@SerializedName("actionType")
	@Column(name = "ACTION_TYPE")
	private String actionType;

}
