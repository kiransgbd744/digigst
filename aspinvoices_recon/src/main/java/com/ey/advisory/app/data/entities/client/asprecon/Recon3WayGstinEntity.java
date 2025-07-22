package com.ey.advisory.app.data.entities.client.asprecon;

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
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_3WAY_RECON_GSTIN")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recon3WayGstinEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RECON_GSTIN_ID")
	private Long gstinId;

	@Column(name = "RECON_CONFIG_ID")
	private Long reconConfigId;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

	public Recon3WayGstinEntity(String gstin, Long reconConfigId) {
		this.gstin = gstin;
		this.reconConfigId = reconConfigId;
	}
}
