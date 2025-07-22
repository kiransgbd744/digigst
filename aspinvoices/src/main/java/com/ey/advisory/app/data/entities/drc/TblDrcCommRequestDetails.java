package com.ey.advisory.app.data.entities.drc;

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
@Table(name = "DRC01_COMM_REQUEST")
@Getter
@Setter
@ToString
public class TblDrcCommRequestDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "EMAIL_TYPE")
	private String emailType;

	@Column(name = "REPORT_STATUS")
	private String reportStatus;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "CREATED_ON")
	private String createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "FILEPATH")
	private String filePath;

	@Column(name = "COMMUNICATION_TYPE")
	private String commType;
	
	@Column(name = "DOC_ID")
	private String docId;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "EMAIL_STATUS")
	private String emailStatus;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "DERIVED_RETURN_PERIOD")
	private int derivedReturnPeriod;
}
