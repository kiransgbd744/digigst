package com.ey.advisory.core.async.domain.master;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * This entity class represents Gstn Api Counts GroupWise
 * 
 * @author Siva.Reddy
 *
 */
@Entity
@Data
@Table(name = "GSTN_PUBLICAPI_COUNTS")
public class GSTNAPICountConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTN_PUBLICAPI_COUNTS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GROUP_CODE")
	protected String groupCode;

	@Column(name = "SUMMARY_DATE")
	protected LocalDate summaryDate;

	@Column(name = "API_TYPE")
	protected String apiType;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "FY")
	protected String fy;

	@Column(name = "SOURCE")
	protected String source;

	@Column(name = "END_POINT")
	protected String endPoint;

	public GSTNAPICountConfig() {
		super();
	}
}
