package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * @author Balakrishna.S
 *
 */
@Entity
@Table(name = "REQUEST_FOR_APPROVAL")
@Data
@ToString
public class RequestForApprovalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

	
	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String returnPeriod;

	@Column(name = "WORK_FLOW_TYPE")
	private String workFlowType;
	
	@Column(name = "MAKER_ID")
	private String makerId;
	
	@Column(name = "CHECKER_ID")
	private String checkerId;
	
	@Column(name = "MAKER_COMMENTS")
	private String makerComments;	
	
	@Expose
	@Column(name = "REQUEST_TIME")
	private LocalDateTime requestTime;
	
	@Expose
	@Column(name = "APPROVAL_STATUS")
	private String approvalStatus;
	@Expose
	@Column(name = "CHECKER_COMMENTS")
	private String checkerComments;
	@Expose
	@Column(name = "RESPONSE_TIME")
	private LocalDateTime responseTime;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Expose
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
}
