/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.sql.Clob;
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
@Table(name = "GSTN_GET_BATCH_PAYLOADS")
@Data
public class GetBatchJsonPayloadsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RETURN_PERIOD")
	private String taxPeriod;
	
	@Column(name = "JSON_PAYLOAD")
	private Clob jsonPayload;
	
	@Column(name = "BATCH_ID")
	private Long batchId;
	
	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
}
