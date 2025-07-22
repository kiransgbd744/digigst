package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajesh N K
 *
 */

@Entity
@Table(name = "GSTR1_EINV_RECON_GSTIN_DETAILS")
@Getter
@Setter
@ToString
public class Gstr1EInvReconGstinDetailEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RECON_REPORT_GSTIN_ID")
	private Long reconReportGstinId;

	@Column(name = "RECON_CONFIG_ID")
	private Long reconConfigId;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "RETURN_PERIOD")
	private Integer returnPeriod;
	

	public Gstr1EInvReconGstinDetailEntity(String gstin, Long reconConfigId, 
			Integer returnPeriod) {
		this.gstin = gstin;
		this.reconConfigId = reconConfigId;
		this.returnPeriod = returnPeriod;
	}

	
}
