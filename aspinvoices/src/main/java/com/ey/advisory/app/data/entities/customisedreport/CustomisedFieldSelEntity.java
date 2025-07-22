package com.ey.advisory.app.data.entities.customisedreport;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "TBL_DY_COL_LIST")
public class CustomisedFieldSelEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("reportId")
	@Column(name = "REPORT_ID")
	private Long reportId;

	@Expose
	@SerializedName("columnList")
	@Lob
	@Column(name = "COLUMN_LIST")
	private String columnList;
	
	@Expose
	@SerializedName("javaMapping")
	@Lob
	@Column(name = "JAVA_MAPPING")
	private String javaMapp;

	@Expose
	@SerializedName("headerMapping")
	@Lob
	@Column(name = "HEADER_MAPPING")
	private String headerMapping;

	@Expose
	@SerializedName("dbMapping")
	@Lob
	@Column(name = "DB_MAPPING")
	private String dbMapping;

	@Expose
	@SerializedName("entityId")
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Expose
	@SerializedName("entityName")
	@Column(name = "ENTITY_NAME")
	private String entityName;

	@Expose
	@SerializedName("reportType")
	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;
}
