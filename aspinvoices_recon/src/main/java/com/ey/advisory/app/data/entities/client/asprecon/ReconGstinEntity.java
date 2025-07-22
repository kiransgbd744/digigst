package com.ey.advisory.app.data.entities.client.asprecon;

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
 * @author Arun.KA
 *
 */

@Entity
@Table(name = "RECON_REPORT_GSTIN_DETAILS")
@Setter
@Getter
@ToString
public class ReconGstinEntity {
private static final long serialVersionUID = 1L;

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RECON_REPORT_GSTIN_ID")
	protected Long gstinId;
	
	@Expose
	@Column(name = "RECON_REPORT_CONFIG_ID")
	protected Long configId;
	
	@Expose
	@Column(name = "GSTIN")
	protected String gstin;
	
	public ReconGstinEntity() { }
	
	public ReconGstinEntity(String gstin, Long configId) {
		this.gstin = gstin;
		this.configId = configId;
	}
	
	
	

}
