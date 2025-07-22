package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Saif.S
 *
 */
@Setter
@Getter
@Entity
@Table(name = "CONSOLIDATE_EMAIL_MAPPING")
public class ConsolidateEmailMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PRIMARY_EMAIL_ID")
	private String primaryEmailId;

	@Column(name = "IS_FIRST_EMAIL_TRIGGERED")
	private boolean isFirstEmailTriggered;

	@Column(name = "IS_SECOND_EMAIL_TRIGGERED")
	private boolean isSecondEmailTriggered;

	@Column(name = "IS_SECOND_EMAIL_ELIGIBLE")
	private boolean isSecondEmailEligible;

	@Column(name = "ACTIVE_GSTINS")
	private String activeGstins;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "GET_CALL_DATE")
	private LocalDate getCallDate;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

}
