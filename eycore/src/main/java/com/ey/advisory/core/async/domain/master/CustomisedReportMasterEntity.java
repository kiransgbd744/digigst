package com.ey.advisory.core.async.domain.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "TBL_DY_REPORT_LIST")
public class CustomisedReportMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "REPORT_NAME")
	private String reportName;

	@Expose
	@Column(name = "PROCEDURE_NAME")
	private String procName;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

}
