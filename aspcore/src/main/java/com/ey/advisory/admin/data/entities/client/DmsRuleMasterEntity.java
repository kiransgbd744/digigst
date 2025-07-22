package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @POST For DMS_RULE_MASTER
 * 
 * @author ashutosh.kar
 *
 */
@Entity
@Table(name = "DMS_RULE_MASTER")
@Setter
@Getter
@ToString
public class DmsRuleMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "RULE_ID")
	private Long ruleId;

	@Column(name = "RULE_NAME")
	private String ruleName;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;
}
