package com.ey.advisory.app.data.entities.client;

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
@Table(name = "TBL_COMMON_CREDIT_REV_ERR")
@Setter
@Getter
@ToString
public class CommonCreditReversalErr {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "COM_CRD_ERROR_DOC_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("errorCodes")
	@Column(name = "ERROR_CODES")
	private String errorCodes;

	@Expose
	@SerializedName("errorDesc")
	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private Boolean isError;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private Boolean isProcessed;

	@Expose
	@SerializedName("commonCreditDocKey")
	@Column(name = "COM_CRD_KEY")
	private String commonCreditDocKey;

	@Expose
	@SerializedName("prId")
	@Column(name = "PR_ID")
	private Long prId;

	@Expose
	@SerializedName("prDocKey")
	@Column(name = "PR_DOC_KEY")
	private String prDocKey;

	@Expose
	@SerializedName("prTaxPeriod")
	@Column(name = "PR_TAX_PERIOD")
	private String prTaxPeriod;

	@Expose
	@SerializedName("dataOriginInTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginInTypeCode;

	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	private String custGstin;

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
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;

	@Expose
	@SerializedName("itemSerialNum")
	@Column(name = "ITEM_SERIAL_NUM")
	private String itemSerialNum;

	@Expose
	@SerializedName("commonSupplierIndicator")
	@Column(name = "COMMON_SUP_INDICATOR")
	private String commonSupplierIndicator;

	@Expose
	@SerializedName("revisedTaxPeriod")
	@Column(name = "REV_RET_PRD")
	private String revisedTaxPeriod;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private String fileId;

	@Expose
	@SerializedName("fileName")
	@Column(name = "FILE_NAME")
	private String fileName;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	private Boolean isDelete;

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

}
