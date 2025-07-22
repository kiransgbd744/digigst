package com.ey.advisory.app.data.entities.gstr8;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR8_UPLOAD_STAGING")
public class Gstr8UploadVarCharEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR8_UPLOAD_STAGING_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "RET_PERIOD")
	private String returnPeriod;

	@Column(name = "IDENTIFIER")
	private String identifier;

	@Column(name = "ORG_RET_PERIOD")
	private String originalReturnPeriod;

	@Column(name = "ORG_NET_SUPPLIES")
	private String originalNetSupplies;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "SGSTIN_OR_ENROL_ID")
	private String sgstinOrEnrolId;

	@Column(name = "ORG_SGSTIN_OR_ENROL_ID")
	private String originalSgstinOrEnrolId;

	@Column(name = "POS")
	private String pos;

	@Column(name = "ORG_POS")
	private String originalPos;
	
	@Column(name = "SUPPLIES_TO_REGISTERED")
	private String suppliesToRegistered;

	@Column(name = "RETURNS_FROM_REGISTERED")
	private String returnsFromRegistered;

	@Column(name = "SUPPLIES_TO_UNREGISTERED")
	private String suppliesToUnregistered;

	@Column(name = "RETURNS_FROM_UNREGISTERED")
	private String returnsFromUnregistered;

	@Column(name = "NET_SUPPLIES")
	private String netSupplies;

	@Column(name = "IGST_AMT")
	private String igstAmount;

	@Column(name = "CGST_AMT")
	private String cgstAmount;
	
	@Column(name = "SGST_AMT")
	private String sgstAmount;

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
}
