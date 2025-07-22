/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "ERP_PUSH_BATCH_PAYLOADS")
@Data
public class ErpBatchJsonPayloadsEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ERP_PUSH_BATCH_PAYLOADS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "JSON_PAYLOAD")
	private Clob jsonPayload;
	
	@Column(name = "BATCH_ID")
	private Long batchId;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IS_DELETE")
	private Boolean isDelete;
	
}
