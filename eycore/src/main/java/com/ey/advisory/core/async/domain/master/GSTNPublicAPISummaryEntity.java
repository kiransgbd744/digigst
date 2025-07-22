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
 * @author Jithendra.B
 *
 */
@Entity
@Data
@Table(name = "GSTN_PUBLICAPI_SUMMARY")
public class GSTNPublicAPISummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "DAY")
	private Integer day;

	@Column(name = "COUNT")
	private Long count;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

}
