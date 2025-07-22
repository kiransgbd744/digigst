package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "GSTN_GET_STATUS")
public class GstnGetStatusEntity {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTN_GET_STATUS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "ERROR_DESC")
	private String errorDescription;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "CSV_GENERATION_FLAG")
	private Boolean csvGenerationFlag;
	
	@Column(name = "CSV_FILE_PATH")
	private String csvGenPath;
	
	@Column(name = "DERIVED_TAX_PERIOD")
	private Integer derivedTaxPeriod;

	@Column(name = "IS_DB_LOAD")
	private Boolean isdbLoad;

	@Column(name = "JOB_STATUS")
	private String jobStatus;
	
	@Column(name = "IS_DELETE")
	private Boolean isDeleted;
	
	@Column(name = "DOC_ID")
	private String docId;
	
	public GstnGetStatusEntity(String gstin, String taxPeriod,
			String section, String status, String returnType) {
		
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.section = section;
		this.status = status;
		this.returnType = returnType;
		
	}

}
