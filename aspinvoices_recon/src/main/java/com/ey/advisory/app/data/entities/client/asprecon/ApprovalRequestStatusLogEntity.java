package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "REQUEST_STATUS_LOG")

public class ApprovalRequestStatusLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private  Long id;

	@Column(name = "REQUEST_ID")
	private Long reqId;

	@Column(name = "ACTION_BY_NAME")
	private String actionByName;

	@Column(name = "REQUEST_STATUS")
	private String reqStatus;

	@Column(name = "CHECKER_COMMENTS")
	private String checkComm;

	@Column(name = "ACTION_DATE_TIME")
	private LocalDateTime actionDateTime;

}
