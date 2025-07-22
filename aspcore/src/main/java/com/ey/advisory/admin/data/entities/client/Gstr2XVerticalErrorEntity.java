package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */

@Entity
@Data
@Table(name = "VERTICAL_ERROR")
public class Gstr2XVerticalErrorEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "COMMON_ID")
	protected Long commonId;

	@Column(name = "FILE_ID")
	protected Long fileId;

	@Column(name = "TABLE_TYPE")
	protected String tableType;

	@Column(name = "ERROR_FIELD")
	protected String errorField;

	@Column(name = "ERROR_CODE")
	protected String errorCode;

	@Column(name = "ERROR_SOURCE")
	protected String errorSource;

	@Column(name = "ERROR_DESCRIPTION")
	protected String errorDesc;

	@Column(name = "VAL_TYPE")
	protected String valType;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "INV_KEY")
	protected String invKey;

	@Column(name = "ERROR_TYPE")
	protected String errorType;
	
	@Column(name = "GSTIN")
	protected String gstin;
	
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;
	
	 
}
