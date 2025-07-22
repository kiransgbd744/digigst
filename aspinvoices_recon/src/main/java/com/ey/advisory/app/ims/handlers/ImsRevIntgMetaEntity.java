package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Ravindra  V S
 *
 */
@Entity
@Table(name = "TBL_IMS_REV_INTEGRATION_META")
@Data
public class ImsRevIntgMetaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "META_ID")
	private Long metaId;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TOTAL_RECORD")
	private Long totalRecord;

	@Column(name = "HEADER_CHUNK_ID")
	private Integer headerChunkId;
	
	@Column(name = "NESTED_CHUNK_ID")
	private Integer nestedChunkId;

	@Column(name = "TOTAL_SENT_CHUNK_RECORD")
	private Long totalChunkRecordSent;

	@Column(name = "TOTAL_RECEIVED_CHUNK_RECORD")
	private Long totalChunkRecordReceived;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;

}
