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
 * @author kiran s
 *
 */

@Entity
@Table(name = "GSTR1_PR_VS_SUBM_RECON_GSTIN_DETAILS")
@Getter
@Setter
@ToString
public class Gstr1PRvsSubmReconGstinDetailEntity {

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

	@Column(name = "TO_RETURN_PERIOD")
	private Integer toReturnPeriod;

	@Column(name = "FROM_RETURN_PERIOD")
	private Integer fromReturnPeriod;

	public Gstr1PRvsSubmReconGstinDetailEntity(String gstin, Long reconConfigId, Integer fromReturnPeriod,
			Integer toReturnPeriod, Boolean isActive) {
		this.gstin = gstin;
		this.reconConfigId = reconConfigId;
		this.fromReturnPeriod = fromReturnPeriod;
		this.toReturnPeriod = toReturnPeriod;
		this.isActive = isActive;
	}

}
