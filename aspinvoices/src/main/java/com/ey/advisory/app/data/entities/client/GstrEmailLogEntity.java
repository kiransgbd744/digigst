package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
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
@Table(name = "GSTR_EMAIL_LOG")
public class GstrEmailLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "REQ_PAYLOAD")
	private Clob reqPayload;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "NOTIFICATION_CODE")
	private String notificationCode;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

}
