package com.ey.advisory.service.gstr1.sales.register;

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
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "TBL_SRVSDIGI_RPT_DOWNLOAD")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class SalesRegisterReconDownloadReportsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long configId;

	@Expose
	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Expose
	@Column(name = "IS_DOWNLOAD")
	private Boolean isDownloadable;

	@Expose
	@Column(name = "CHUNK_NUM")
	private Integer chunkNum;
	
	@Expose
	@Column(name = "CHUNK_SIZE")
	private Integer chunkSize;

	@Expose
	@Column(name = "FILE_PATH")
	private String path;
	
	public SalesRegisterReconDownloadReportsEntity( String reportType, Long configId, 
			Boolean flag) {
		super();
		this.configId = configId;
		this.reportType = reportType;
		this.isDownloadable = flag;
	}
	
	
	
}
