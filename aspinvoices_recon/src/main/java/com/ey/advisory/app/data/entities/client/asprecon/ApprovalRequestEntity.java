package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "APPROVAL_WORKFLOW_REQUEST")
@Setter
@Getter
@ToString
@Component
public class ApprovalRequestEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "REQUEST_ID")
	protected Long requestId;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RETURN_TYPE")
	private String returnType;
	
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;
	
	@Column(name = "MAKER_NAME")
	private String makerName;

	@Column(name = "MAKER_EMAIL")
	private String makerEmail;

	@Column(name = "MAKER_COMMENTS")
	private String makComments;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "SNAPSHOT_PATH")
	private String snapPath;
	
	@Column(name = "SNAPSHOT_CREATED_ON")
	private LocalDateTime snapCreatedOn;
	
	@Column(name = "REQUEST_KEY")
	private String reqKey;
	  
}
