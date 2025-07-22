package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

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
@Table(name = "TBL_RECON_REPORT_GSTIN_DETAILS")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2ReconGstinEntity {

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

	@Expose
	@Column(name = "AUTO_RECON_ID")
	protected Long autoReconId;

	@Expose
	@Column(name = "IS_ACTIVE")
	protected boolean isActive;

	@Expose
	@Column(name = "AUTO_RECON_DATE")
	protected LocalDate autoReconDate;

	@Expose
	@Column(name = "FROM_DATE")
	protected LocalDateTime fromDate;

	@Expose
	@Column(name = "TO_DATE")
	protected LocalDateTime toDate;
	
	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime cretaedOn;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected LocalDateTime completedOn;
	
	@Expose
	@Column(name = "STATUS")
	protected String status;
	
	@Expose
	@Column(name = "IS_DELTA")
	protected Boolean isDelta;
	
	@Expose
	@Column(name = "IS_ISD_RECON")
	protected Boolean isdRecon;

	public Gstr2ReconGstinEntity(String gstin, Long configId, Boolean isdRecon) {
		this.gstin = gstin;
		this.configId = configId;
		this.isdRecon = isdRecon;
	}

	public Gstr2ReconGstinEntity(String gstin, Long configId) {
		this.gstin = gstin;
		this.configId = configId;
	}
}
