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

@Entity
@Table(name = "ENTITY_USER_MAPPING")
@Setter
@Getter
@ToString
public class EntityUserMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Column(name = "GROUP_ID")
	private Long groupId;
	
	@Column(name = "GROUP_CODE")
	private String  groupCode;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "IS_DELETE")
	private Boolean isFlag;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime updateOn;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String updatedBy;

}
