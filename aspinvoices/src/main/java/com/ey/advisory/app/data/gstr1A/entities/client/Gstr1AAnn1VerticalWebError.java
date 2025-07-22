package com.ey.advisory.app.data.gstr1A.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ANX_VERTICAL_ERROR_1A")
@Setter
@Getter
@ToString
public class Gstr1AAnn1VerticalWebError {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "COMMON_ID")
	private Long commanId;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "TABLE_TYPE")
	private String tableType;

	@Column(name = "ERROR_FIELD")
	private String errorField;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_SOURCE")
	private String errorSource;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDesc;

	@Column(name = "VAL_TYPE")
	private String valueType;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "INV_KEY")
	private String invKey;
	
	@Column(name = "ERROR_TYPE")
	private String errorType;
	
}