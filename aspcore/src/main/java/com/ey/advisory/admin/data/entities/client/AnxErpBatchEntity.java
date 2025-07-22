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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "ANX_ERP_BATCH_TABLE")
@Data
public class AnxErpBatchEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_ERP_BATCH_TABLE_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCH_SIZE")
	private Long batchSize;

	@Column(name = "GROUP_CODE")
	private String groupcode;

	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "ACKNOWLEDGEMENT_ID")
	private Long ackmtId;

	/*@Column(name = "STATUS")
	private String status;*/

	@Column(name = "IS_SUCCESS")
	private boolean isSuccess;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "DESTINATION_NAME")
	private String destinationName;
	
	@Column(name = "SCENARIO_ID")
	private Long scenarioId;
	
	@Column(name = "DATA_TYPE")
	private String dataType;
	
	@Column(name = "STATUS")
	private String httpStatus;
	
	@Column(name = "HTTP_CODE")
	private Integer httpCode;
	
	@Column(name = "APP_EXCEPTION")
	private String exception;
	
	@Column(name = "API_RESPONSE")
	private String apiResponse;
	
	@Column(name = "MIN_ID_OUT")
	private Long minIdOut;
	
	@Column(name = "MAX_ID_OUT")
	private Long maxIdOut;
	
	@Column(name = "MIN_ID_IN")
	private Long minIdIn;
	
	@Column(name = "MAX_ID_IN")
	private Long maxIdIn;
	
	@Column(name = "ERP_ID")
	private Long erpId;
	
	@Column(name = "PAYLOAD_ID")
	private String payloadId;
	
	@Column(name = "JOB_TYPE")
	private String jobType;
	
	@Column(name = "SOURCE_TYPE")
	private String sourceType;
	
	@Column(name = "SECTION")
	private String section;
	
	@Column(name = "JOB_ID")
	private Long jobId;
	
	@Column(name = "CHUNK_STATUS")
	private String chunkStatus;
}
