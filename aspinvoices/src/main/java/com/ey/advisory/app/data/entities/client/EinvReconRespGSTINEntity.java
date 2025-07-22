package com.ey.advisory.app.data.entities.client;

import com.google.gson.annotations.Expose;

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
 * 
 * @author Siva Reddy
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "TBL_EINV_RECON_RESPONSE_GSTIN")
public class EinvReconRespGSTINEntity {

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@Column(name = "RECON_RESP_CONFIG_ID")
	private Long reconRespConfigId;

	@Expose
	@Column(name = "GSTIN")
	private String gstin;

	@Expose
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@Column(name = "STATUS")
	private String status;

	public EinvReconRespGSTINEntity(Long reconRespConfigId, String gstin,
			String returnPeriod, String status, Boolean isActive,
			String errorDesc) {
		this.reconRespConfigId = reconRespConfigId;
		this.gstin = gstin;
		this.returnPeriod = returnPeriod;
		this.status = status;
		this.isActive = isActive;
		this.errorDesc = errorDesc;
	}

	public EinvReconRespGSTINEntity() {
		super();
	}

	@Expose
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Expose
	@Column(name = "ERROR_DESCRIPTION")
	private String errorDesc;

}
