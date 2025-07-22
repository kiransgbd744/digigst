package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Siva Reddy
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "TBL_EINV_RECON_SUMMARY_RESPONSE_REPORT")
public class EinvReconSummRespReportEntity {

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	public EinvReconSummRespReportEntity(Long reconRespConfigId,
			Integer reportId, String userResp) {
		this.reconRespConfigId = reconRespConfigId;
		this.reportId = reportId;
		this.userResp = userResp;
	}

	@Expose
	@Column(name = "RECON_RESP_CONFIG_ID")
	private Long reconRespConfigId;

	@Expose
	@Column(name = "REPORT_ID")
	private Integer reportId;

	public EinvReconSummRespReportEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Expose
	@Column(name = "USER_RESPONSE")
	private String userResp;
}
