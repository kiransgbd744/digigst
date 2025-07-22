package com.ey.advisory.app.data.entities.client.asprecon;

import java.sql.Clob;
import java.time.LocalDate;
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
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "TBL_VENDOR_RATING_ASYNC_API")
@Data
public class VendorComplianceRatingAsyncApiResponseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "REFERENCE_ID")
	private String referenceId;

	@Column(name = "GSTIN_COUNT")
	protected Integer gstinCount;

	@Column(name = "FY")
	protected String fy;

	@Column(name = "REQUEST_PAYLOAD")
	private Clob requestPayload;

	@Column(name = "RESPONSE_PAYLOAD")
	private Clob responsePayload;

	@Column(name = "REPORT_CATEG")
	private String reportCategory;

	@Column(name = "DATA_TYPE")
	private String dataType;

	@Column(name = "TABLE_TYPE")
	private String tableType;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "UPLOAD_MODE")
	private String uploadMode;
	
	@Column(name = "FILE_NAME")
	private String fileName;
}
