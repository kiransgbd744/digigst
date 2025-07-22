package com.ey.advisory.ewb.client.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Entity
@Table(name = "EWB_MULTIVEHICLE_DETAILS")
@Data
public class EwbMultiVehicleDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "MULTI_VEHICLE_ID")
	private Long multiVehicleId;

	@Column(name = "GROUP_NO")
	private Long groupNo;

	@Column(name = "FROM_PLACE")
	private String fromPlace;

	@Column(name = "FROM_STATE")
	private String fromState;

	@Column(name = "REASON")
	private String reason;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "VEHICLE_NUM")
	private String vehicleNum;

	@Column(name = "TRANS_DOC_NUM")
	private String transDocNo;

	@Column(name = "TRANS_DOC_DATE")
	private LocalDate transDocDate;

	@Column(name = "VEHICLE_QUANTITY")
	private Long vehicleQty;

	@Column(name = "OLD_VEHICLE_NUM")
	private String oldVehicleNum;

	@Column(name = "OLD_TRANS_DOC_NUM")
	private String oldTransDocNum;

	@Column(name = "VEHICLE_ADDED_DATE")
	private LocalDateTime vehicleAddedDate;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "IS_ERROR")
	private boolean isError;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdDate;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;
	
	@Column(name = "FUNCTION")
	private String function;

}
