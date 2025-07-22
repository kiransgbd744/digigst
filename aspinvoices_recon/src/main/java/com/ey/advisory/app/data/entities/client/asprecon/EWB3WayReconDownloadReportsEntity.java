package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "TBL_3WAY_RPT_DOWNLOAD")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class EWB3WayReconDownloadReportsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "RECON_CONFIG_ID")
	private Long configId;

	@Expose
	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Expose
	@Column(name = "IS_DOWNLOAD")
	private Boolean isDownloadable;

	@Expose
	@Column(name = "FILE_PATH")
	private String path;
	
	@Expose
	@Column(name = "DOC_ID")
	private String docId;
	
	
	public EWB3WayReconDownloadReportsEntity( String reportType, Long configId, 
			Boolean flag) {
		super();
		this.configId = configId;
		this.reportType = reportType;
		this.isDownloadable = flag;
		
	}
	
	
	
}
