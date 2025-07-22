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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva.Reddy
 *
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "AUTO_DRAFT_ATTRIBUTE_CONFIG")
public class AutoDraftAttributeConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "SOURCE_ID")
	protected String sourceId;

	@Column(name = "IS_ACTIVE")
	protected boolean isActive;

	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Column(name = "CREATED_ON")
	protected LocalDateTime created_on;

	@Column(name = "CREATED_BY")
	protected String created_by;
}
