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
@Table(name = "DRC01B_DRC01C_AUTO_GET_EMAIL_DETAILS")
@Getter
@Setter
@ToString
public class TblDrc01AutoGetCallDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "EMAIL_TYPE")
	private String emailType;

	@Column(name = "REPORT_STATUS")
	private String reportstatus;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "GET_CALL_DATE")
	private LocalDateTime getCallDate;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "REF_ID")
	private String refId;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Column(name = "COMMUNICATION_TYPE")
	private String commType;
	
	@Column(name = "DRC01C_OPTED")
	private String drc01cOpted;
	
	@Column(name = "DRC01B_OPTED")
	private String drc01bOpted;
	
	@Column(name = "EMAIL_STATUS")
	private String emailStatus;
	
	@Column(name = "REMARK")
	private String remark;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "REMINDER_COUNT")
	private Integer reminderCount;
}
