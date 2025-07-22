package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Entity
@Table(name = "GL_RECON_REPORT_CONFIG")
@Setter
@Getter
@ToString


public class GlReconReportConfigEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "RECON_REPORT_CONFIG_ID")
	protected Long configId;
	
	@Expose
	@Column(name="ENTITY_ID")
	protected Long entityId;

	@Expose
	@Column(name = "TYPE")
	protected String type;

	@Expose
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected LocalDateTime completedOn;

	@Expose
	@Column(name = "FROM_TAX_PERIOD")
	protected String fromTaxPeriod;
	
	@Expose
	@Column(name = "TO_TAX_PERIOD")
	protected String toTaxPeriod;

	@Expose
	@Column(name = "FILE_PATH")
	protected String filePath;

	@Expose
	@Column(name = "SR_FILE_PATH")
	protected String srFilePath;

	@Expose
	@Column(name = "STATUS")
	protected String status;
	
	@Expose
	@Column(name = "MASTER_FILE_IDS")
	protected String masterFileIds;
	
	@Expose
	@Column(name = "UPLOADED_DOCUMENT_ID")
	protected String docId;
	

}
