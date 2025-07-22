package com.ey.advisory.app.data.entities.gstr8;

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

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR8_UPLOAD_PSD")
public class Gstr8UploadProcessedEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR8_UPLOAD_PSD_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "RET_PERIOD")
	private String returnPeriod;
	
	@Column(name = "SECTION")
	private String section;

	@Column(name = "IDENTIFIER")
	private String identifier;

	@Column(name = "ORG_RET_PERIOD")
	private String originalReturnPeriod;

	@Column(name = "ORG_NET_SUPPLIES")
	private BigDecimal originalNetSupplies;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "SGSTIN_OR_ENROL_ID")
	private String sgstin;

	@Column(name = "ORG_SGSTIN_OR_ENROL_ID")
	private String originalSgstinOrEnrolId;
	
	@Column(name = "POS")
	private String pos;

	@Column(name = "ORG_POS")
	private String originalPos;

	@Column(name = "SUPPLIES_TO_REGISTERED")
	private BigDecimal suppliesToRegistered;

	@Column(name = "RETURNS_FROM_REGISTERED")
	private BigDecimal returnsFromRegistered;

	@Column(name = "SUPPLIES_TO_UNREGISTERED")
	private BigDecimal suppliesToUnregistered;

	@Column(name = "RETURNS_FROM_UNREGISTERED")
	private BigDecimal returnsFromUnregistered;

	@Column(name = "NET_SUPPLIES")
	private BigDecimal netSupplies;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmount;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmount;
	
	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmount;

	@Column(name = "USERDEFINEDFIELD_1")
	private String userDefinedField1;

	@Column(name = "USERDEFINEDFIELD_2")
	private String userDefinedField2;

	@Column(name = "USERDEFINEDFIELD_3")
	private String userDefinedField3;

	@Column(name = "USERDEFINEDFIELD_4")
	private String userDefinedField4;

	@Column(name = "USERDEFINEDFIELD_5")
	private String userDefinedField5;

	@Column(name = "USERDEFINEDFIELD_6")
	private String userDefinedField6;

	@Column(name = "USERDEFINEDFIELD_7")
	private String userDefinedField7;

	@Column(name = "USERDEFINEDFIELD_8")
	private String userDefinedField8;

	@Column(name = "USERDEFINEDFIELD_9")
	private String userDefinedField9;

	@Column(name = "USERDEFINEDFIELD_10")
	private String userDefinedField10;

	@Column(name = "USERDEFINEDFIELD_11")
	private String userDefinedField11;

	@Column(name = "USERDEFINEDFIELD_12")
	private String userDefinedField12;

	@Column(name = "USERDEFINEDFIELD_13")
	private String userDefinedField13;

	@Column(name = "USERDEFINEDFIELD_14")
	private String userDefinedField14;
	
	@Column(name = "USERDEFINEDFIELD_15")
	private String userDefinedField15;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "DOC_KEY")
	private String docKey;
	
	
	@Column(name = "BATCH_ID")
	private Long gstnBatchId;
	
	@Column(name = "GSTN_ERROR_CODE")
	protected String gstnErrorCode;
	
	@Column(name = "GSTN_ERROR_DESCRIPTION")
	protected String gstnErrorDesc;	
	
	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSavedGstn;

	@Column(name = "GSTN_ERROR")
	private boolean gstnError;

	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDate sentGstnDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDate savedGstnDate;

	@Column(name = "IS_SUBMITTED")
	private boolean isSubmitted;

	@Column(name = "SUBMITTED_DATE")
	private LocalDate subDate;

	@Column(name = "IS_FILED")
	private boolean isFiled;

	@Column(name = "FILED_DATE")
	private LocalDate filDate;
}
