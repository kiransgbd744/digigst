package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author kiran s
 *
 */

@Entity
@Table(name = "GSTR1_PR_SUBMITED_RECON_CONFIG")
@Data
public class Gstr1PrVsSubmittedReconConfigEntity {
	
	@Id
	@Column(name = "RECON_CONFIG_ID")
	private Long reconConfigId;


	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;
	
	@Column(name = "FROM_TAX_PERIOD")
	private String FromTaxPeriod;
	
	@Column(name = "TO_TAX_PERIOD")
	private String toTaxPeriod;
	
	@Column(name = "FROM_RETURN_PERIOD")
	private Integer fromDerivedTaxPeriod;
	
	@Column(name = "TO_RETURN_PERIOD")
	private Integer toDerivedTaxPeriod;
	

}
