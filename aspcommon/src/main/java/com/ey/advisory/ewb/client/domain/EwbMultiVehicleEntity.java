package com.ey.advisory.ewb.client.domain;

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
@Table(name = "EWB_MULTIVEHICLE")
@Data
public class EwbMultiVehicleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;

	@Column(name = "GROUP_NO")
	private Long groupNo;

	@Column(name = "EWB_NO")
	private Long ewbNo;

	@Column(name = "TRANS_MODE")
	private String transMode;

	@Column(name = "FROM_PLACE")
	private String fromPlace;

	@Column(name = "TO_PLACE")
	private String toPlace;

	@Column(name = "TOTAL_QUANTITY")
	private Long totalQty;

	@Column(name = "UNIT")
	private String unit;

	@Column(name = "FROM_STATE")
	private String fromState;

	@Column(name = "TO_STATE")
	private String toState;

	@Column(name = "REASON")
	private String reason;

	@Column(name = "REMARKS")
	private String remarks;

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

}
