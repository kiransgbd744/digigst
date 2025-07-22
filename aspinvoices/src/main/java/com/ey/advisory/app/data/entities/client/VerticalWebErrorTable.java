package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

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
@Table(name = "VERTICAL_ERROR")
@Setter
@Getter
@ToString
public class VerticalWebErrorTable {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "VERTICAL_ERROR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
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