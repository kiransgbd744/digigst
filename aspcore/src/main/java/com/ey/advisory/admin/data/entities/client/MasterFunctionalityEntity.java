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
 * @author Umesha.M
 *
 */
@Entity
@Table(name = "MASTER_FUNCTIONALITY")
@Setter
@Getter
@ToString
public class MasterFunctionalityEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Column(name = "FUNCTIONALITY_CODE")
	private String functionalityCode;
	
	@Column(name = "FUNCTIONALITY_DESC")
	private String functionalityDesc;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
}
