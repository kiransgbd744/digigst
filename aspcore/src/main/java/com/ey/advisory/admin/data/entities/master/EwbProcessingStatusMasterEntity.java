/**
 * 
 */
package com.ey.advisory.admin.data.entities.master;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "EWB_PROCESSING_STATUS_MASTER")
@Setter
@Getter
public class EwbProcessingStatusMasterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	
	@Column(name = "EWB_STATUS")
	private String ewbStatus;
	
	@Column(name = "EWB_STATUS_DESCRIPTION")
	private String ewbStatusDesc;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

}
