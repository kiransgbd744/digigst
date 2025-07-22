package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Rajesh N K
 *
 */

@Entity
@Table(name = "GSTR1_EINV_RECON_CONFIG")
@Data
public class Gstr1EInvReconConfigEntity {
	
	@Id
	@Column(name = "RECON_CONFIG_ID")
	private Long reconConfigId;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;
	
	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "RETURN_PERIOD")
	private Integer derivedTaxPeriod;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

}
