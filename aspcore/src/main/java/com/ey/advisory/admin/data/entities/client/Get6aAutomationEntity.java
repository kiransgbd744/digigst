package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Arun.KA
 *
 */
@Entity
@Table(name = "GET6A_AUTOMATION")
@Data
public class Get6aAutomationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "CALENDAR_TIME")
	private LocalTime calendarTime;
	
	@Column(name = "CALENDAR_DATE_AS_STRING")
	private String calendarDateAsString;
	
	@Column(name = "UNIQUE_KEY")
	private String uniqueKey; 
	
	@Column(name = "GET_EVENT")
	private String getEvent; 
	
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
	
	@Column(name = "LAST_JOB_POST_DATE")
	private LocalDateTime lastPostedDate;

	@Column(name = "LAST_JOB_ID")
	private Long lastPostedJobId;
}
