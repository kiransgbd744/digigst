package com.ey.advisory.admin.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Entity
@Table(name = "ONBOARDING_REQUEST_PAYLOAD")
@Data
public class OnboardingRequestPayloadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Integer entityId;
	
	@Column(name = "REQUEST_PAYLOAD")
	private Clob reqPaylaod;
	
	@Column(name = "RESPONSE_PAYLOAD")
	private Clob respPaylaod;
	
	@Column(name = "ERROR_MSG")
	private Clob errorMsg;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "CATEGORY")
	private String category;


}
