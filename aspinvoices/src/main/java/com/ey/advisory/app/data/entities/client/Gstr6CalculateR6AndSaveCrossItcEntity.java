/**
 * 
 *//*
package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

*//**
 * @author Hemasundar.J
 *
 *//*
@Data
@Entity
@Table(name = "GSTR6_CALCULATE_R6_AND_CROSS_ITC")
public class Gstr6CalculateR6AndSaveCrossItcEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;
	
	@Column(name = "BATCH_ID")
	private Long batchId;
	
	@Column(name = "CAL_R6_STATUS")
	private String r6Status;
	
	@Column(name = "CAL_R6_ERROR_CODE")
	private String r6ErrorCode;
	
	@Column(name = "CAL_R6_ERROR_DESC")
	private String r6ErrorDesc;
	
	@Column(name = "CAL_R6_JOB_TIME")
	private LocalDateTime r6JobTime;
	
	@Column(name = "CROSS_STATUS_CODE")
	private String crossStatusCode;
	
	@Column(name = "CROSS_ERROR_CODE")
	private String crossErrorCode;
	
	@Column(name = "CROSS_ERROR_DESC")
	private String crossErrorDesc;
	
	@Column(name = "CROSS_R6_JOB_TIME")
	private LocalDateTime crossJobTime;
	
	@Column(name = "SAVE_CROSS_PAYLOAD")
	private Clob payload;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
}
*/