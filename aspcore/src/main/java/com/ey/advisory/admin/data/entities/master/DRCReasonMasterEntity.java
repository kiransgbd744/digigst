package com.ey.advisory.admin.data.entities.master;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 *         This class is works as DRC Reason master Table
 */
@Data
@Entity
@Table(name = "TBL_DRC_REASON_MASTER")
public class DRCReasonMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("reasonCode")
	@Column(name = "REASON_CODE")
	private String reasonCode;

	@Expose
	@SerializedName("reasonDesc")
	@Column(name = "REASON_DESCRIPTION")
	private String reasonDesc;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

}
