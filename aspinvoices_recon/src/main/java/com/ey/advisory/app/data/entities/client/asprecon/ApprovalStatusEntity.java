package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "APPROVAL_STATUS")
@Data
public class ApprovalStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "GROUP_ID")
	private Long groupId;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "DOC_TYPE")
	private String docType;
	
	@Column(name = "APPROVAL_STATUS")
	private Integer approvalStatus;
	
	@Column(name = "INITIATED_ON")
	private LocalDateTime initiatedOn;	
	
	@Column(name = "APPROVED_ON")
	private LocalDateTime approvedOn;
	
	@Column(name = "APPROVED_BY")
	private String approvedBy;
	
	@Column(name = "INITIATED_BY")
	private String initiatedBy;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;

}
