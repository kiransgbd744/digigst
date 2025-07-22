package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;

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
 * 
 * @author Anand3.M
 *
 */
@Data
@Entity
@Table(name = "GSTR7_DOC_ERROR")
public class Gstr7DocErrorEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR7_DOC_ERROR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "ERROR_FIELD")
	private String errorField;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_TYPE")
	private String errorType;

	@Column(name = "ERROR_SOURCE")
	private String errorSource;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDate createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDate modifiedOn;

	@Column(name = "VAL_TYPE")
	private String valType;

	@Column(name = "INV_KEY")
	private String invKey;

}
