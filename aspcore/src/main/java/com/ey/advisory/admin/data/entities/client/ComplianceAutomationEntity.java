package com.ey.advisory.admin.data.entities.client;

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
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "COMPLIANCE_AUTOMATION")
@Data
public class ComplianceAutomationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "JOB_ID")
	private Long jobId;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "LAST_JOB_POST_DATE")
	private LocalDateTime lastPostedDate;

	@Column(name = "CRON_EXPRESSION")
	private String cronExpression;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

}
