package com.ey.advisory.core.async.domain.master;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "TBL_DY_COL_NAME")
public class CustomisedMasterFieldEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("seqId")
	@Column(name = "SEQ_ID")
	private Long seqId;

	@Expose
	@SerializedName("reportId")
	@Column(name = "REPORT_ID")
	private Long reportId;

	@Expose
	@SerializedName("columnName")
	@Column(name = "COLUMN_NAME")
	private String columnName;

	@Expose
	@SerializedName("fieldName")
	@Column(name = "FIELD_NAME")
	private String fieldName;

	@Expose
	@SerializedName("tabName")
	@Column(name = "TAB_NAME")
	private String tabName;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
}
