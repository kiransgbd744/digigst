package com.ey.advisory.app.data.entities.client;

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

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GSTR3B_AUTO_CALCULATE_STATUS")
public class Gstr3bGenerateStatusEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_AUTO_CALCULATE_STATUS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CALC_TYPE")
	private String calculationType;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;	

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;	
}
