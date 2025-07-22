package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.time.LocalDate;

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
@Table(name = "TBL_RECON_GSTIN_TAXPERIOD")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2ReconGstinTaxPeriodEntity {

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RECON_TAXPERIOD_ID")
	protected Long id;

	@Expose
	@Column(name = "RECON_REPORT_CONFIG_ID")
	protected Long configId;

	@Expose
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;
	
	@Expose
	@Column(name = "GSTIN")
	protected String gstin;
	
	@Expose
	@Column(name = "RETURN_PERIOD")
	protected Integer returnPeriod;
	
	@Expose
	@Column(name = "IS_ACTIVE")
	protected boolean isActive;

	@Expose
	@Column(name = "AUTO_RECON_DATE")
	protected LocalDate autoReconDate;

	@Expose
	@Column(name = "FROM_DATE")
	protected LocalDate fromDate;

	@Expose
	@Column(name = "TO_DATE")
	protected LocalDate toDate;
	
	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDate cretaedOn;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected LocalDate completedOn;
	
	@Expose
	@Column(name = "STATUS")
	protected String status;

	public Gstr2ReconGstinTaxPeriodEntity(String gstin, Long configId) {
		this.gstin = gstin;
		this.configId = configId;
	}

}
