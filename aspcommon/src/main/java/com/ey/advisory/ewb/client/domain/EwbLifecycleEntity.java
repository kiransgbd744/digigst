/**
 * 
 */
package com.ey.advisory.ewb.client.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Entity
@Table(name = "EWB_LIFECYCLE")
@Data
public class EwbLifecycleEntity {
	
	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "EWB_LIFECYCLE_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;
	
	@Column(name = "EWB_NUM")
	private String ewbNum;
	
	@Column(name = "CONSOLIDATED_EWB_NUM")
	private String cEwbNum;
	
	@Column(name = "CONSOLIDATED_EWB_DATE")
	private LocalDateTime cEwbDate;
	
	@Column(name = "EWB_FUNCTION")
	private String function;
	
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
	private String transDocNo;
	
	@Column(name = "TRANS_DOC_DATE")
	private LocalDate transDocDate;
	
	@Column(name = "REASON_CODE")
	private String reasonCode;
	
	@Column(name = "REASON_REM")
	private String reasonRemark;
	
	@Column(name = "TRANSPORTER_ID")
	private String transporterId;

	@Column(name = "REMAINING_DISTANCE")
	private Integer remDistance;
	
	@Column(name = "FROM_PINCODE")
	private String fromPincode;
	
	@Column(name = "CONSIGNMENT_STATUS")
	private String consignmentStatus;
	
	@Column(name = "TRANSIT_TYPE")
	private String  transitType;
	
	@Column(name = "ADDRESS_LINE1")
	private String addressLine1;
	
	@Column(name = "ADDRESS_LINE2")
	private String addressLine2;
	
	@Column(name = "ADDRESS_LINE3")
	private String addressLine3;
	
	@Column(name = "VALID_UPTO")
	private LocalDateTime  validUpto;
	
	@Column(name = "VEHICLE_UPDATE_DATE")
	private LocalDateTime vehicleUpdateDate;
	
	@Column(name = "CANCELLATION_DATE")
	private LocalDateTime cancelDate;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_DESC")
	private String errorDesc;
	
	@Column(name = "FUNCTION_STATUS")
	private boolean functionStatus;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "IS_ACTIVE")
	private boolean isActive;
	
	@Column(name = "ASP_DISTANCE")
	private Integer aspDistance;
	
	@Column(name = "REJECTED_DATE")
	private LocalDateTime rejectDate;
	
	@Column(name = "EWB_ORIGIN")
	private Integer ewbOrigin;
	

}
