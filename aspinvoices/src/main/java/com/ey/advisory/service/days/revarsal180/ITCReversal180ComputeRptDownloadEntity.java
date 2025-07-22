package com.ey.advisory.service.days.revarsal180;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_180_DAYS_COMPUTE_RPT_DWNLD")
@Data
public class ITCReversal180ComputeRptDownloadEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "COMPUTE_ID")
	private Long computeId;

	@Expose
	@Column(name = "RPT_TYP")
	private String reportType;

	@Expose
	@Column(name = "FILE_PATH")
	private String filePath;

	@Expose
	@Column(name = "IS_DWNLD")
	private Boolean isDownloadable;

	@Expose
	@Column(name = "COMPUTE_ST_ID")
	private Long startChunk;

	@Expose
	@Column(name = "COMPUTE_END_ID")
	private Long endChunk;

	@Expose
	@Column(name = "CHUNK_NUMS")
	private Long chunkNums;
	
	@Expose
	@Column(name = "DOC_ID")
	private String docId;

}
