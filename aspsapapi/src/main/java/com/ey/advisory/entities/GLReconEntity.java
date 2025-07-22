package com.ey.advisory.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
@Entity
@Table(name = "TBL_GL_RECON_STG")
public class GLReconEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;
	
	@Column(name = "FILE_ID")
	private Long fileId;
	
	@Column(name = "COMPANY_CODE")
	private String companyCode;
	
	@Column(name = "ENTITY_NAME")
	private String Entityname;
	
	@Column(name = "GSTIN")
	private String gSTIN;
	
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "DATA_TYPE")
	private String dataType;
	
	@Column(name = "ACTION_TYPE")
	private String actionType;
	
	@Column(name = "GL_CODE")
	private String gLCode;
	
	@Column(name = "GL_ACCOUNT_NAME")
	private String gLAccountName;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "SUB_TYPE1")
	private String subType1;
	
	@Column(name = "SUB_TYPE2")
	private String subType2;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name = "SUB_CATEGORY")
	private String subCategory;
	
	@Column(name = "SHORT_DESCRIPTION")
	private String shortDescription;
	
	@Column(name = "LONG_DESCRIPTION")
	private String longDescription;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "TOTAL_AMOUNT_GL")
	private String totalAmountGL;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "SOURCE")
	private String source;
	
	

}
