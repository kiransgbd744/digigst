package com.ey.advisory.core.async.domain.master;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Ravindra
 *
 */
@Data
@Entity
@Table(name = "EINV_APPLICABLE_MASTER")
public class EinvGstinMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "PAN")
	private String pan;

	@Column(name = "SOURCE")
	private String source;
	
	@Column(name = "TRADE_NAME")
	private String tradeName;
	
	@Column(name = "CANCELLED_DATE")
	private String cancelledDate;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;

}
