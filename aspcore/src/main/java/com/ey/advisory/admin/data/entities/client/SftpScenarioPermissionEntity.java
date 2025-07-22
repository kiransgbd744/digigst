package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="SFTP_SCENARIO_PERMISSION")
@Data
public class SftpScenarioPermissionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ERP_ID")
	private Long erpId;

	@Column(name = "SCENARIO_ID")
	private Long scenarioId;

	@Column(name = "ENDPOINT_URI")
	private String endPointURI;
	
	@Column(name="SOURCE_TYPE")
	private String sourceType;
	
	@Column(name = "JOB_FREQUENCY")
	private String jobFrequency;
	
	@Column(name = "LAST_JOB_COMP_DATE")
	private LocalDateTime jobCompletionDate;

	@Column(name = "LAST_JOB_START_DATE")
	private LocalDateTime jobstartDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
}
