package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajesh N K
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MONTHLY_ZIP_GEN_STATUS")
public class ZipGenStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "RETURN_TYPE")
	private String returnType;
	
	@Column(name = "DERIVED_TAX_PERIOD")
	private Integer derivedTaxPeriod;

	@Column(name = "ZIP_FILE_PATH")
	private String zipFilePath;

	@Column(name = "CREATED_ON")
	private LocalDateTime createOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "JOB_STATUS")
	private String jobStatus;

	public ZipGenStatusEntity(String gstin, String taxPeriod, String zipFilePath,
		Integer derivedTaxPeriod,String returnType, LocalDateTime createdDate, 
			LocalDateTime updatedDate, String jobStatus) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.zipFilePath = zipFilePath;
		this.derivedTaxPeriod = derivedTaxPeriod;
		this.returnType = returnType;
		this.createOn = createdDate;
		this.updatedOn = updatedDate;
		this.jobStatus = jobStatus;
	}
}
