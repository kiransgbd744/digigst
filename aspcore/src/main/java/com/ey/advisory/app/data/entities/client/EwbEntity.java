package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "EWB_MASTER")
@Data
public class EwbEntity {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "EWB_MASTER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "EWB_ID")
	private Long ewbId;
	
	@Column(name = "EWB_NUM")
	private String ewbNum;
	
	@Column(name = "EWB_DATE")
	private LocalDateTime ewbDate;
	
	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;
	
	@Column(name = "EWB_LIFECYCLE_ID")
	private Long lifeCycleId;
	
	@Column(name = "VALID_UPTO")
	private LocalDateTime validUpto;
	
	@Column(name = "ALERT")
	private String alert;

	@Column(name = "CONSOLIDATED_EWB_NUM")
	private LocalDateTime consolidatedEwbNum;
	
	@Column(name = "CONSOLIDATED_EWB_DATE")
	private LocalDateTime consolidatedEwbDate;
	
	@Column(name = "CANCELLATION_DATE")
	private LocalDateTime cancelDate;
	
	@Column(name = "REJECTED_DATE")
	private LocalDateTime rejectDate;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "REASON_CODE")
	private String reasonCode;
	
	@Column(name = "REASON_REM")
	private String reasonRemark;
	
	@Column(name = "TRANSPORTER_ID")
	private String transporterId;
	
	@Column(name = "REMAINING_DISTANCE")
	private Integer remDistance;
	
	@Column(name = "FROM_PINCODE")
	private Integer fromPincode;
	
	@Column(name = "CONSIGNMENT_STATUS")
	private String consignmentStatus;
	
	@Column(name = "TRANSIT_TYPE")
	private String transitType;
	
	@Column(name = "ADDRESS_LINE1")
	private String addLine1;
	
	@Column(name = "ADDRESS_LINE2")
	private String addLine2;
	
	@Column(name = "ADDRESS_LINE3")
	private String addLine3;
	
	@Column(name = "EWB_STATUS")
	private Integer status;
	
	@Column(name = "FROM_PLACE")
	private String fromPlace;
	
	@Column(name = "FROM_STATE")
	private String fromState;
	
	@Column(name = "VEHICLE_NUM")
	private String vehicleNum;
	
	@Column(name = "VEHICLE_TYPE")
	private String vehicleType;
	
	@Column(name = "TRANS_MODE")
	private String transMode;
	
	@Column(name = "TRANS_DOC_NUM")
	private String transDocNum;
	
	@Column(name = "TRANS_DOC_DATE")
	private LocalDate transDocDate;
	
	@Column(name = "SOURCE")
	private String source;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "ASP_DISTANCE")
	private Integer aspDistance;
	
	@Column(name = "EWB_ORIGIN")
	private Integer ewbOrigin;
	
	@Column(name = "TRANS_TYPE")
	private String transType;
	
	@Column(name = "REJECT_REASON")
	private String rejectReason;
	
	@Column(name = "TYPE")
	private String type;
	
	

}
