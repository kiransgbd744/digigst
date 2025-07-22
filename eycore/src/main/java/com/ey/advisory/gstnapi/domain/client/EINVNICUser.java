package com.ey.advisory.gstnapi.domain.client;

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

/**
 * 
 * @author Sai.Pakanati
 *
 */
@Entity
@Table(name = "EINV_GSTIN_NIC_USER")
@Setter
@Getter
@ToString
public class EINVNICUser {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "NIC_USER_NAME")
	protected String nicUserName;

	@Column(name = "NIC_PASSWORD")
	protected String nicPassword;

	@Column(name = "CLIENT_ID")
	protected String clientId;

	@Column(name = "CLIENT_SECRET")
	protected String clientSecret;

	@Column(name = "UPDATED_BY")
	protected String updatedBy;

	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;

	@Column(name = "IDENTIFIER")
	protected String identifier;

}
