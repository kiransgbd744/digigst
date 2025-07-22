package com.ey.advisory.services.gstr3b.entity.setoff;

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
 * @author Hema G M 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GSTR3B_COMPUTE_CONFIG")
public class Gstr3BSetOffEntityComputeConfigEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_COMPUTE_CONFIG_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "COMPUTE_STATUS")
	private String computeStatus;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
}
