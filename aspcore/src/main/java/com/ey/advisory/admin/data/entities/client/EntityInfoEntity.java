package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

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
@Table(name = "ENTITY_INFO")
@Setter
@Getter
@ToString
public class EntityInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Expose
	@Column(name="ENTITY_NAME", length = 20)
	private String entityName; 
	@Expose
	@Column(name = "GROUP_ID",nullable = false)
	private Long groupId;
	
	@Expose
	@Column(name="GSTIN_ID")
	private Long gstinId;
	
	@Expose
	@Column(name="PAN", length = 10)
	private String pan;
	
	@Expose
	@Column(name="GROSS_TURNOVER", precision = 18, scale = 2)
	private BigDecimal grossTurnover;
	
	@Expose
	@Column(name="ENTITY_TYPE", length = 60)
	private String entityType;
	
	@Expose
	@Column(name="COMPANY_HQ", length = 5)
	private String companyHq;
	
	@Expose
	@Column(name = "IS_DELETE", nullable = false)
	private boolean isDelete;
	
	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	
	@Expose
	@Column(name="GROUP_CODE", length = 20)
	private String groupCode;
	
	

}
