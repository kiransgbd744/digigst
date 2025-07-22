package com.ey.advisory.app.data.entities.client;

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

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_CUSTOMER_FILING_FY_INITIATE_STATUS")
public class CustomerFilingStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "FINANCIAL_YEAR")
	private String financialYear;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	public CustomerFilingStatusEntity(String gstin, String financialYear, String status,
			String createdBy, String modifiedBy) {
		super();
		this.gstin = gstin;
		this.financialYear = financialYear;
		this.status = status;
		this.createdBy = createdBy;
		if (this.createdOn == null) {
			this.createdOn = LocalDateTime.now();
		}
		this.modifiedBy = modifiedBy;
		this.modifiedOn = LocalDateTime.now();
	}

}
