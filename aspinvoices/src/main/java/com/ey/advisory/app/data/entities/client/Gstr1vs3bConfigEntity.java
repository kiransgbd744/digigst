package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TBL_RECON_GSTR1_3B_CONFIG")
@Data
public class Gstr1vs3bConfigEntity {
	@Id
	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long configId;

	@Column(name = "RECON_TYPE")
	private String reconType;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "FROM_RET_PERIOD")
	private Integer deriverdRetPeriodFrom;

	@Column(name = "TO_RET_PERIOD")
	private Integer deriverdRetPeriodTo;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;
	
	@Column(name = "FILE_PATH")
	private String filePath;
	
	

	
}
