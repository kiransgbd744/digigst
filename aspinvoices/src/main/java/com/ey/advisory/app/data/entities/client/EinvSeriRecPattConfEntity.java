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

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Entity
@Table(name = "TBL_RECON_2BPR_PATTERN")
@Data
public class EinvSeriRecPattConfEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PATTERN_ID")
	private Long id;

	@Column(name = "PATTERN")
	private String pattern;

	@Column(name = "PATTERN_COUNT")
	private Long patternCount;

	@Column(name = "USER_TYPE")
	private String userType;

	@Expose
	@Column(name = "FILTER_TYPE")
	protected String filterType;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
}
