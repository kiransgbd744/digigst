package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TBL_2BVS3B_COMPUTE")
@Data
public class Gstr2bvs3bStatusEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCHID")
	private Long batchId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "FROM_TAX_PERIOD")
	private Integer taxPeriodFrom;

	@Column(name = "TO_TAX_PERIOD")
	private Integer taxPeriodTo;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ERROR_DESC")
	private String errorDescription;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

}
