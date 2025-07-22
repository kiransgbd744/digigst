package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TBL_RECON_GSTR2A2B_VS_3B")
@Data
public class Gstr2Avs3bReconEntity {
	@Id
	@Column(name = "REQUEST_ID")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR2A_VS_3B_CRED_DIST_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	private Long requestId;
	
	@Column(name = "NO_OF_GSTINS")
	private Long noOfGstin;

	@Column(name = "FROM_TAX_PERIOD")
	private String fromTaxPeriod;
	
	@Column(name = "TO_TAX_PERIOD")
	private String toTaxPeriod;
	
	@Column(name = "INITIATED_ON")
	private LocalDateTime initiatedOn;
	
	@Column(name = "INITIATED_BY")
	private String InitiatedBy;
	
	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "GSTINS")
	private Clob gstins;
		
	@Column(name = "DOC_ID")
	private String docId;
	
	@Column(name = "DERIVED_FROM_TAX_PERIOD")
	private int derivedFromTaxPeriod;
	
	@Column(name = "DERIVED_TO_TAX_PERIOD")
	private int derivedToTaxPeriod;
	
	@Column(name = "INITIATED_BY_EMAIL_ID")
	private String InitiatedByEmailId;
	
	@Column(name = "RECON_TYPE")
	private String reconType;
}
