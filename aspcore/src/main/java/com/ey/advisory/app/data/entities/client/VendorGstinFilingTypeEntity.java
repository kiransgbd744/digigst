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

/**
 * 
 * @author Saif.S
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VENDOR_GSTIN_FILING_TYPE")
public class VendorGstinFilingTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "FY")
	private String fy;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "FILING_TYPE")
	private String filingType;

	@Column(name = "QUARTER")
	private String quarter;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
}
