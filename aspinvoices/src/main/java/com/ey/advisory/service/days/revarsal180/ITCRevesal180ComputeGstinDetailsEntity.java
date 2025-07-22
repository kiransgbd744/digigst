package com.ey.advisory.service.days.revarsal180;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vishal.verma
 *
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBL_180_DAYS_COMPUTE_GSTINS")
public class ITCRevesal180ComputeGstinDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "COMPUTE_ID")
	private Long computeId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "IS_DELETE")
	private Boolean isDelete = false;

	public ITCRevesal180ComputeGstinDetailsEntity(String gstin,
			Long computeId) {
		this.gstin = gstin;
		this.computeId = computeId;
	}

}
