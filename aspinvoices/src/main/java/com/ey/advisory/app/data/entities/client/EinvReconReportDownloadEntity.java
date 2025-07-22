package com.ey.advisory.app.data.entities.client;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Entity
@Table(name = "TBL_EINV_RECON_RPT_DWNLD")
@Data
public class EinvReconReportDownloadEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "RECON_CONFIG_ID")
	private Long configId;

	@Expose
	@Column(name = "REPORT_TYPE_ID")
	private Integer reportTypeId;

	@Expose
	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Expose
	@Column(name = "FILE_PATH")
	private String filePath;

	@Expose
	@Column(name = "IS_DWNLD")
	private Boolean isDownloadable;
	
	@Expose
	@Column(name = "RECON_LINK_ST_ID")
	private Long startChunk;
	
	@Expose
	@Column(name = "RECON_LINK_END_ID")
	private Long endChunk;
	
	@Expose
	@Column(name = "CHUNK_NUMS")
	private Long chunkNums;
	
	@Column(name = "DOC_ID")
	protected String docId;
	
	
	
}
