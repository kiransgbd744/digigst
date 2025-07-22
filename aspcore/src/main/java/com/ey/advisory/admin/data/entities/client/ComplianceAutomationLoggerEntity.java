package com.ey.advisory.admin.data.entities.client;

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
 * 
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "COMPLIANCE_AUTOMATION_LOGGER")
@Data
public class ComplianceAutomationLoggerEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "COMPLIANCE_AUTOMATION_LOGGER_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "GSTIN")
	protected String gstin;
	
	@Column(name = "TYPE")
	protected String type;

	@Column(name = "LOG")
	private String log;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

}
