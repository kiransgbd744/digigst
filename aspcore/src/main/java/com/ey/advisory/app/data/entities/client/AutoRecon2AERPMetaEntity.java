package com.ey.advisory.app.data.entities.client;

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
 * @author Jithendra.B
 *
 */
@Entity
@Table(name = "TBL_2APR_REV_INTEGRATION_META")
@Data
public class AutoRecon2AERPMetaEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_2APR_REV_INTEGRATION_META_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "META_ID")
	private Long metaId;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long reconReportConfigId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TOTAL_RECORD")
	private Long totalRecord;

	@Column(name = "CHUNK_ID")
	private Integer chunkId;

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
