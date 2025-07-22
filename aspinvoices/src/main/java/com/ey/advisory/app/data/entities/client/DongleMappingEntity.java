package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Entity
@Table(name = "DONGLE_PAN_GSTIN_MAPPING")
@Data
public class DongleMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "PAN")
	private String pan;

	@Column(name = "AUTHORISED_NAME")
	private String authorisedName;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "DESGN_AUTH_SIGN_STATUS")
	private String designation;

}
