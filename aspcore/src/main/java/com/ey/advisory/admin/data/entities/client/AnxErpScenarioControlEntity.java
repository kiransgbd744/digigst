/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "ERP_SCENARIO_CONTROL")
@Data
public class AnxErpScenarioControlEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "SCENARIO_ID")
	private Long scenarioId;

	/*@Column(name = "DESTINATION")
	private String destination;*/
	
	@Column(name = "MIN_ID_OUT")
	private Long minIdOut;
	
	@Column(name = "MAX_ID_OUT")
	private Long maxIdOut;
	
	@Column(name = "BATCH_ID_OUT")
	private Long batchIdOut;
	
	@Column(name = "MIN_ID_IN")
	private Long minIdIn;
	
	@Column(name = "MAX_ID_IN")
	private Long maxIdIn;
	
	@Column(name = "BATCH_ID_IN")
	private Long batchIdIn;
	
	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
}
