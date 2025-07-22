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
@Table(name = "GSTR3B_ITC_STATUS")
@Data
public class Gstr3bItcStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer deriverdRetPeriod;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "REF_ID")
	private String refId;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

}
