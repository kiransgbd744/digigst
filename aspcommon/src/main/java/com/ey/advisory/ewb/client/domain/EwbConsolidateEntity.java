/**
 * 
 */
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
 * @author Arun.KA
 *
 */
@Entity
@Table(name = "EWB_CONSOLIDATED")
@Data
public class EwbConsolidateEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "EWB_NUM")
	private String ewbNum;
	
	@Column(name = "CONSOLIDATED_EWB_NUM")
	private String consolidatedEwbNum;
	
	@Column(name = "CONSOLIDATED_EWB_DATE")
	private LocalDateTime consolidatedEwbDate;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "FROM_PLACE")
	private String fromPlace;
	
	@Column(name = "FROM_STATE")
	private String fromState;
	
	@Column(name = "VEHICLE_NUM")
	private String vehicleNum;
	
	@Column(name = "TRANS_MODE")
	private String transMode;
	
	@Column(name = "TRANS_DOC_NUM")
	private String transDocNum;
	
	@Column(name = "TRANS_DOC_DATE")
	private LocalDate transDocDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	public EwbConsolidateEntity(String consolidatedEwbNum,
			LocalDateTime consolidatedEwbDate, boolean isDelete,
			String fromPlace, String fromState, String vehicleNum,
			String transMode, String transDocNum, LocalDate transDocDate,
			String createdBy, LocalDateTime createdOn) {
		super();
		this.consolidatedEwbNum = consolidatedEwbNum;
		this.consolidatedEwbDate = consolidatedEwbDate;
		this.isDelete = isDelete;
		this.fromPlace = fromPlace;
		this.fromState = fromState;
		this.vehicleNum = vehicleNum;
		this.transMode = transMode;
		this.transDocNum = transDocNum;
		this.transDocDate = transDocDate;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
	
	
	
}
