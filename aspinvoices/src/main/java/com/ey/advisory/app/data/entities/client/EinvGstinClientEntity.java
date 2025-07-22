/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Arun.KA
 *
 */
@Data
@Entity
@Table(name = "EINV_APPLICABLE_GSTINS")
public class EinvGstinClientEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "PAN")
	private String pan;

	@Column(name = "IS_SYNCED")
	private boolean isSynced;
	
	@Column(name = "SOURCE")
	private String source;
	
	@Column(name = "CANCELLED_DATE")
	private String cancelledDate;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

}
