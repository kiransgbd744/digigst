package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_RECON_RPT_DWNLD")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Gstr2ReconAddlReportsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long configId;

	@Expose
	@Column(name = "RPTTYPID")
	private Integer reportTypeId;

	@Expose
	@Column(name = "RPTTYP")
	private String reportType;

	@Expose
	@Column(name = "FILE_PATH")
	private String filePath;

	@Expose
	@Column(name = "ISDWNLD")
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
	
	@Expose
	@Column(name = "IS_REPORT_PROC_EXECUTED")
	private Boolean isReportProcExecuted;
	
	@Expose
	@Column(name = "DOC_ID")
	private String docId;
	
	public Gstr2ReconAddlReportsEntity( String reportType, Long configId, 
			Boolean flag,Integer reportTypeId) {
		super();
		this.configId = configId;
		this.reportType = reportType;
		this.isDownloadable = flag;
		this.reportTypeId=reportTypeId;

	}
	
	
	
}
